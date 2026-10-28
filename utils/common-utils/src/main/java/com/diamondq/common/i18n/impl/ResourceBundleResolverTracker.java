package com.diamondq.common.i18n.impl;

import com.diamondq.common.i18n.I18N;
import com.diamondq.common.i18n.ResourceBundleResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * Registers any ResourceBundleResolver OSGi services with I18N (ServiceLoader discovery doesn't work across OSGi
 * bundles), and unregisters them when they go away.
 */
@Component
public class ResourceBundleResolverTracker {

  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, unbind = "removeResolver")
  public void addResolver(ResourceBundleResolver pResolver) {
    I18N.addResourceBundleResolver(pResolver);
  }

  public void removeResolver(ResourceBundleResolver pResolver) {
    I18N.removeResourceBundleResolver(pResolver);
  }
}
