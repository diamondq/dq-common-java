package com.diamondq.common.i18n;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * A ResourceBundle that holds some messages of its own and falls back to a parent bundle for everything else. It is
 * meant for {@link ResourceBundleResolver}s that override only some messages.
 */
public class LayeredResourceBundle extends ResourceBundle {

  private final Map<String, Object> mOverrides;

  /**
   * Creates a bundle from a map of messages
   *
   * @param pOverrides the messages that this bundle provides
   * @param pParent the bundle to use for any message that is not in pOverrides
   */
  public LayeredResourceBundle(Map<String, ?> pOverrides, ResourceBundle pParent) {
    mOverrides = Map.copyOf(pOverrides);
    setParent(pParent);
  }

  /**
   * Creates a bundle from the messages of another bundle
   *
   * @param pOverrides the bundle whose messages this bundle provides (including any it gets from its own parents)
   * @param pParent the bundle to use for any message that is not in pOverrides
   */
  public LayeredResourceBundle(ResourceBundle pOverrides, ResourceBundle pParent) {
    Map<String, Object> overrides = new HashMap<>();
    for (String key : pOverrides.keySet())
      overrides.put(key, pOverrides.getObject(key));
    mOverrides = Map.copyOf(overrides);
    setParent(pParent);
  }

  @Override
  protected Object handleGetObject(String pKey) {
    return mOverrides.get(pKey);
  }

  @Override
  protected Set<String> handleKeySet() {
    return mOverrides.keySet();
  }

  @Override
  public Enumeration<String> getKeys() {
    Set<String> keys = new LinkedHashSet<>(mOverrides.keySet());
    if (parent != null) keys.addAll(parent.keySet());
    return Collections.enumeration(keys);
  }
}
