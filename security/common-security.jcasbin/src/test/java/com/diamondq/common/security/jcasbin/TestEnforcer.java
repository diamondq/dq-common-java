package com.diamondq.common.security.jcasbin;

import static org.junit.Assert.fail;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.impl.ContextFactoryImpl;
import com.diamondq.common.context.impl.logging.LoggingContextHandler;

import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class TestEnforcer {

  @Rule
  public OsgiContext        mContext = new OsgiContext();

  @SuppressWarnings("null")
  public ContextFactoryImpl mContextFactory;

  @BeforeClass
  public static void julSetup() {
    String fileName = TestEnforcer.class.getResource("/logging.properties").getFile();
    System.setProperty("java.util.logging.config.file", fileName);
  }

  @Before
  public void setup() {

    /* Basic setup */

    mContext.registerInjectActivateService(new LoggingContextHandler());
    mContextFactory = new ContextFactoryImpl();
    mContext.registerInjectActivateService(mContextFactory);

  }

  @Test
  public void test() {
    try (Context ctx = mContextFactory.newContext(TestEnforcer.class, this)) {
      ctx.trace("Loading model...");
      Model model = ModelUtils.loadFromResource(TestEnforcer.class, "model.conf");
      ctx.trace("Loading adapter...");
      ResourceAdapter adapter = new ResourceAdapter(TestEnforcer.class, "policy.csv");
      ctx.trace("Loading Enforcer...");
      Enforcer enforcer = new Enforcer(model, adapter);

      ctx.trace("Checking permission...");

      String sub = "alice"; // the user that wants to access a resource.
      String obj = "data1"; // the resource that is going to be accessed.
      String act = "read"; // the operation that the user performs on the resource.

      if (enforcer.enforce(sub, obj, act) == true) {

      }
      else {
        fail("Failed");
      }
    }
  }

}
