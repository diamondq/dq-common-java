package com.diamondq.common.security.acl.impl;

import com.diamondq.common.security.acl.ACLMessages;
import com.diamondq.common.security.acl.api.SecurityContext;
import com.diamondq.common.security.acl.api.SecurityContextManager;
import com.diamondq.common.security.acl.spi.SecurityContextSerializer;
import com.diamondq.common.utils.context.ContextFactory;
import com.diamondq.common.utils.misc.errors.ExtendedIllegalStateException;
import com.diamondq.common.utils.misc.errors.Verify;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SecurityContextManagerImpl implements SecurityContextManager {

  private ConcurrentMap<String, SecurityContextSerializer> mSerializers = new ConcurrentHashMap<>();

  public void addSecurityContextSerializer(SecurityContextSerializer pSerializer) {
    ContextFactory.staticReportTrace(SecurityContextManagerImpl.class, this, pSerializer);
    mSerializers.put(pSerializer.getSerializerId(), pSerializer);
  }

  public void removeSecurityContextSerializer(SecurityContextSerializer pSerializer) {
    ContextFactory.staticReportTrace(SecurityContextManagerImpl.class, this, pSerializer);
    mSerializers.remove(pSerializer.getSerializerId(), pSerializer);
  }

  /**
   * @see com.diamondq.common.security.acl.api.SecurityContextManager#serialize(com.diamondq.common.security.acl.api.SecurityContext)
   */
  @Override
  public byte[] serialize(SecurityContext pContext) {
    LinkedHashMap<String, byte[]> byteMap = new LinkedHashMap<>();
    for (SecurityContextSerializer ser : mSerializers.values()) {
      byte[] bytes = ser.serialize(pContext);
      if (bytes != null)
        byteMap.put(ser.getSerializerId(), bytes);
    }
    try {
      int byteCount = 1;
      for (Map.Entry<String, byte[]> pair : byteMap.entrySet()) {
        byteCount = byteCount + 2 + pair.getKey().getBytes("UTF-8").length + 2 + pair.getValue().length;
      }
      ByteBuffer buffer = ByteBuffer.allocate(byteCount);
      buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);
      buffer.put((byte) byteMap.size());
      for (Map.Entry<String, byte[]> pair : byteMap.entrySet()) {
        byte[] data = pair.getKey().getBytes("UTF-8");
        int dataLen = data.length;
        byte[] bytes = pair.getValue();
        int bytesLen = bytes.length;
        /* Data Len */
        buffer.putShort((short) dataLen);
        /* Data */
        buffer.put(data);
        /* Bytes Len */
        buffer.putShort((short) bytesLen);
        /* Bytes */
        buffer.put(bytes);
      }
      return buffer.array();
    }
    catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public SecurityContext deserialize(byte[] pBytes) {
    ByteBuffer buffer = ByteBuffer.wrap(pBytes);
    buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);
    int mapCount = buffer.get();
    SecurityContext context = null;
    for (int i = 0; i < mapCount; i++) {
      /* Key Len */
      short keyLen = buffer.getShort();
      /* Key */
      byte[] keyBytes = new byte[keyLen];
      buffer.get(keyBytes);
      String keyId;
      try {
        keyId = new String(keyBytes, "UTF-8");
      }
      catch (UnsupportedEncodingException ex) {
        throw new RuntimeException(ex);
      }
      /* Data Len */
      short dataLen = buffer.getShort();
      /* Data */
      byte[] dataBytes = new byte[dataLen];
      buffer.get(dataBytes);
      SecurityContextSerializer serializer = mSerializers.get(keyId);
      if (serializer == null)
        throw new ExtendedIllegalStateException(ACLMessages.NO_SUCH_SERIALIZER, keyId);
      context = serializer.deserialize(context, dataBytes);
    }
    return Verify.notNull(context);
  }

}
