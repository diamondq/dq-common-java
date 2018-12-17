package com.diamondq.common.security.acl.api;

public interface SecurityContextManager {

  /**
   * Serialize the security context into a series of bytes. This can be 'deserialized' back to the SecurityContext
   * (assuming a compatible environment) using the SecurityContextManager.
   * 
   * @param pContext the context
   * @return the serialized bytes
   */
  public byte[] serialize(SecurityContext pContext);

  /**
   * Deserialize the bytes into a SecurityContext
   * 
   * @param pBytes the bytes
   * @return the SecurityContext
   */
  public SecurityContext deserialize(byte[] pBytes);
}
