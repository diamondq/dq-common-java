package com.diamondq.common.security.acl.spi;

import com.diamondq.common.security.acl.api.SecurityContext;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface SecurityContextSerializer {

  /**
   * Returns a unique id that represents this serializer
   * 
   * @return the id
   */
  public String getSerializerId();

  /**
   * Serialize the security context into a series of bytes. If this returns a null, then it's assumed that this
   * serializer is not participating in the serialization process for this SecurityContext.
   * 
   * @param pContext the context
   * @return the serialized bytes
   */
  public byte @Nullable [] serialize(SecurityContext pContext);

  /**
   * Deserialize the bytes into a SecurityContext
   * 
   * @param pPartialContext the partial context that is being deserialized
   * @param pBytes the bytes
   * @return the SecurityContext the further deserialized context
   */
  public SecurityContext deserialize(@Nullable SecurityContext pPartialContext, byte[] pBytes);
}