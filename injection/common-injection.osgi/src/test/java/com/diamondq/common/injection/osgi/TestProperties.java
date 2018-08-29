package com.diamondq.common.injection.osgi;

import com.diamondq.common.injection.osgi.testmodel.TestClassWithObjConstructor;
import com.diamondq.common.injection.osgi.testmodel.TestConstructor;
import com.diamondq.common.injection.osgi.testmodel.TestDep;

import java.io.IOException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.sling.testing.mock.osgi.MockOsgi;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestProperties {

  private static final Logger sLogger = LoggerFactory.getLogger(TestProperties.class);

  @Rule
  public final OsgiContext    context = new OsgiContext();

  @Test
  public void propertyPassthrough() throws IOException, InvalidSyntaxException {
    sLogger.debug("********** propertyPassthrough");
    ConfigurationAdmin configAdmin = context.getService(ConfigurationAdmin.class);
    Assert.assertNotNull(configAdmin);
    Configuration config = configAdmin.getConfiguration("com.diamondq.common.injection.test.testconstructor");
    Dictionary<String, Object> props = new Hashtable<>();
    props.put(".dep_filter", "");
    props.put("extra_prop", "true");
    props.put(".hidden_prop", "true");
    config.update(props);

    /* Now register the dependency */

    TestDep dep = new TestDep("test");
    context.bundleContext().registerService(TestDep.class, dep, new Hashtable<>());

    /* Now register the service */

    TestConstructor service = new TestConstructor();
    MockOsgi.injectServices(service, context.bundleContext());
    MockOsgi.activate(service, context.bundleContext());

    /* Check that the real service has now registered */

    Collection<ServiceReference<TestClassWithObjConstructor>> refs =
      context.bundleContext().getServiceReferences(TestClassWithObjConstructor.class, null);
    Assert.assertEquals(1, refs.size());

    ServiceReference<TestClassWithObjConstructor> ref = refs.iterator().next();
    Object value = ref.getProperty("extra_prop");
    Assert.assertEquals("true", value);

    value = ref.getProperty(".hidden_prop");
    Assert.assertNull(value);

  }

}
