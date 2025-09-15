package com.diamondq.common.context.impl.stacklogging;

import com.diamondq.common.context.spi.ContextClass;
import com.diamondq.common.context.spi.ContextHandler;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

@Singleton
@Requires(property = "dq.logging.stacklogger.file")
@Component(service = ContextHandler.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class StackLogger implements ContextHandler {

  @SuppressWarnings("NotNullFieldNotInitialized") private FileWriter mWriter;

  @SuppressWarnings("null")
  public StackLogger() {
  }

  @PostConstruct
  @Activate
  public void onActivate(@Property(name = "dq.logging.stacklogger") Map<String, Object> pProperties) {
    String fileName = (String) pProperties.get(".file");
    try {
      mWriter = new FileWriter(fileName);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void writeStackTrace(ContextClass pContext) throws IOException {
    mWriter.write(pContext.startClass.getName() + "-" + pContext.getLatestStackMethod() + "\n");
    ContextClass parentContextClass = pContext.getParentContextClass();
    if (parentContextClass != null) writeStackTrace(parentContextClass);
  }

  @Override
  public void executeOnContextStart(ContextClass pContext) {
    Long startTime = pContext.getHandlerData("start-stack-logger", false, Long.class);
    if (startTime != null) System.out.println("here");
    startTime = System.currentTimeMillis();
    pContext.setHandlerData("start-stack-logger", startTime);
    ContextClass parentContextClass = pContext.getParentContextClass();
    if (parentContextClass != null) {
      Long parentStartTime = parentContextClass.getHandlerData("start-stack-logger", false, Long.class);
      if (parentStartTime != null) {
        /* Close the parent */
        Long collected = parentContextClass.getHandlerData("collected-stack-logger", false, Long.class);
        if (collected == null) collected = 0L;
        collected = collected + Math.max(0, startTime - parentStartTime);
        parentContextClass.setHandlerData("collected-stack-logger", collected);
        parentContextClass.setHandlerData("start-stack-logger", null);
      }
    }

  }

  @Override
  public void executeOnContextClose(ContextClass pContext, boolean pWithExitValue, @Nullable Object pExitValue,
    @Nullable Function<@Nullable Object, @Nullable Object> pFunc) {
    long endTime = System.currentTimeMillis();
    Long startTime = pContext.getHandlerData("start-stack-logger", false, Long.class);
    if (startTime == null) startTime = endTime;
    try {
      writeStackTrace(pContext);
      Long collected = pContext.getHandlerData("collected-stack-logger", false, Long.class);
      if (collected == null) collected = 0L;
      collected = collected + Math.max(endTime - startTime, 0);
      mWriter.write(collected + "\n");
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    /* Reactivate the parent */
    ContextClass parentContextClass = pContext.getParentContextClass();
    if (parentContextClass != null) {
      parentContextClass.setHandlerData("start-stack-logger", System.currentTimeMillis());
    }
  }

  @Override
  public void executeOnContextExplicitThrowable(ContextClass pContext, Throwable pThrowable) {

  }

  @Override
  public void executeOnContextReportTrace(ContextClass pContext, @Nullable String pMessage, boolean pWithMeta,
    @Nullable Object @Nullable ... pArgs) {

  }

  @Override
  public void executeOnContextReportDebug(ContextClass pContext, @Nullable String pMessage, boolean pWithMeta,
    @Nullable Object @Nullable ... pArgs) {

  }

  @Override
  public void executeOnContextReportInfo(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable ... pArgs) {

  }

  @Override
  public void executeOnContextReportWarn(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable ... pArgs) {

  }

  @Override
  public void executeOnContextReportError(ContextClass pContext, @Nullable String pMessage, Throwable pThrowable) {

  }

  @Override
  public void executeOnContextReportError(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable ... pArgs) {

  }

  @Override
  public void executeOnDetachContextToThread(ContextClass pContext) {
    Long startTime = pContext.getHandlerData("start-stack-logger", false, Long.class);
    if (startTime != null) {
      long endTime = System.currentTimeMillis();
      Long collected = pContext.getHandlerData("collected-stack-logger", false, Long.class);
      if (collected == null) collected = 0L;
      collected = collected + Math.max(endTime - startTime, 0);
      pContext.setHandlerData("collected-stack-logger", collected);
      pContext.setHandlerData("start-stack-logger", null);
    }
    /* Reactivate the parent */
    ContextClass parentContextClass = pContext.getParentContextClass();
    if (parentContextClass != null) {
      parentContextClass.setHandlerData("start-stack-logger", System.currentTimeMillis());
    }
  }

  @Override
  public void executeOnAttachContextToThread(ContextClass pContext) {
    Long startTime = pContext.getHandlerData("start-stack-logger", false, Long.class);
    if (startTime == null) {
      startTime = System.currentTimeMillis();
      pContext.setHandlerData("start-stack-logger", startTime);
      ContextClass parentContextClass = pContext.getParentContextClass();
      if (parentContextClass != null) {
        Long parentStartTime = parentContextClass.getHandlerData("start-stack-logger", false, Long.class);
        if (parentStartTime != null) {
          /* Close the parent */
          Long collected = parentContextClass.getHandlerData("collected-stack-logger", false, Long.class);
          if (collected == null) collected = 0L;
          collected = collected + Math.max(0, startTime - parentStartTime);
          parentContextClass.setHandlerData("collected-stack-logger", collected);
          parentContextClass.setHandlerData("start-stack-logger", null);
        }
      }
    }
  }

}
