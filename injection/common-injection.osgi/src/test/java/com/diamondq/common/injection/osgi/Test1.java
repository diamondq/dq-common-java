package com.diamondq.common.injection.osgi;

import com.diamondq.common.context.impl.ContextFactoryImpl;
import com.diamondq.common.context.impl.logging.LoggingContextHandler;
import com.diamondq.common.errors.ExtendedIllegalArgumentException;
import com.diamondq.common.injection.osgi.i18n.Messages;
import com.diamondq.common.injection.osgi.testmodel.TestClassWithObjConstructor;
import com.diamondq.common.injection.osgi.testmodel.TestConstructor;
import com.diamondq.common.injection.osgi.testmodel.TestDep;
import org.apache.sling.testing.mock.osgi.MockOsgi;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

public class Test1 {

  private static final Logger sLogger = LoggerFactory.getLogger(Test1.class);

  @Rule public final OsgiContext context = new OsgiContext();

  /**
   * If we've required a filter, but no filter is passed, then we should fail.
   */
  @Test
  public void failNoFilters() {
    sLogger.debug("********** failNoFilters");
    TestConstructor service = new TestConstructor();
    context.registerInjectActivateService(new LoggingContextHandler());
    context.registerInjectActivateService(new ContextFactoryImpl());
    MockOsgi.injectServices(service, context.bundleContext());
    try {
      MockOsgi.activate(service, context.bundleContext());
    }
    catch (RuntimeException ex) {
      Throwable cause = ex.getCause();
      if (cause instanceof ExtendedIllegalArgumentException) {
        ExtendedIllegalArgumentException actualEx = (ExtendedIllegalArgumentException) cause;
        Assert.assertEquals(Messages.NO_PROP_MATCHING_FILTER, actualEx.getCode());
        return;
      }
      throw ex;
    }
    Assert.fail();
  }

  @Test
  public void basicInjection() throws IOException, InvalidSyntaxException {
    sLogger.debug("********** basicInjection");
    ConfigurationAdmin configAdmin = context.getService(ConfigurationAdmin.class);
    Assert.assertNotNull(configAdmin);
    Configuration config = configAdmin.getConfiguration("com.diamondq.common.injection.test.testconstructor");
    Dictionary<String, Object> props = new Hashtable<>();
    props.put(".dep_filter", "");
    config.update(props);
    TestConstructor service = new TestConstructor();
    context.registerInjectActivateService(new LoggingContextHandler());
    context.registerInjectActivateService(new ContextFactoryImpl());
    MockOsgi.injectServices(service, context.bundleContext());
    MockOsgi.activate(service, context.bundleContext());

    Collection<ServiceReference<TestClassWithObjConstructor>> refs = context.bundleContext()
      .getServiceReferences(TestClassWithObjConstructor.class, null);
    Assert.assertEquals(0, refs.size());

    /* Now register the dependency */

    TestDep dep = new TestDep("test");
    context.bundleContext().registerService(TestDep.class, dep, new Hashtable<>());

    /* Check that the real service has now registered */

    refs = context.bundleContext().getServiceReferences(TestClassWithObjConstructor.class, null);
    Assert.assertEquals(1, refs.size());

  }

  /**
   * Register the dependency and then remove it, making sure that the final object comes and then goes.
   *
   * @throws IOException
   * @throws InvalidSyntaxException
   */
  @Test
  public void basicInAndOut() throws IOException, InvalidSyntaxException {
    sLogger.debug("********** basicInAndOut");
    ConfigurationAdmin configAdmin = context.getService(ConfigurationAdmin.class);
    Assert.assertNotNull(configAdmin);
    Configuration config = configAdmin.getConfiguration("com.diamondq.common.injection.test.testconstructor");
    Dictionary<String, Object> props = new Hashtable<>();
    props.put(".dep_filter", "");
    config.update(props);
    TestConstructor service = new TestConstructor();
    context.registerInjectActivateService(new LoggingContextHandler());
    context.registerInjectActivateService(new ContextFactoryImpl());
    MockOsgi.injectServices(service, context.bundleContext());
    MockOsgi.activate(service, context.bundleContext());

    Collection<ServiceReference<TestClassWithObjConstructor>> refs = context.bundleContext()
      .getServiceReferences(TestClassWithObjConstructor.class, null);
    Assert.assertEquals(0, refs.size());

    /* Now register the dependency */

    TestDep dep = new TestDep("test");
    ServiceRegistration<TestDep> reg = context.bundleContext().registerService(TestDep.class, dep, new Hashtable<>());

    /* Check that the real service has now registered */

    refs = context.bundleContext().getServiceReferences(TestClassWithObjConstructor.class, null);
    Assert.assertEquals(1, refs.size());

    /* Now unregister */

    reg.unregister();

    refs = context.bundleContext().getServiceReferences(TestClassWithObjConstructor.class, null);
    Assert.assertEquals(0, refs.size());

  }
}
