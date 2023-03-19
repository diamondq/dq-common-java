package com.diamondq.common.context.impl.stacklogging;

import com.diamondq.common.context.spi.ContextClass;
import com.diamondq.common.context.spi.ContextHandler;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

@Singleton
@Requires(property = "dq.logging.stacklogger.file")
public class StackLogger implements ContextHandler {

  @SuppressWarnings("NotNullFieldNotInitialized") private FileWriter mWriter;

  @SuppressWarnings("null")
  public StackLogger() {
  }

  @PostConstruct
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
    @Nullable ContextClass parentContextClass = pContext.getParentContextClass();
    if (parentContextClass != null) writeStackTrace(parentContextClass);
  }

  @Override
  public void executeOnContextStart(ContextClass pContext) {
    @Nullable Long startTime = pContext.getHandlerData("start-stack-logger", false, Long.class);
    if (startTime != null) System.out.println("here");
    startTime = System.currentTimeMillis();
    pContext.setHandlerData("start-stack-logger", startTime);
    @Nullable ContextClass parentContextClass = pContext.getParentContextClass();
    if (parentContextClass != null) {
      @Nullable Long parentStartTime = parentContextClass.getHandlerData("start-stack-logger", false, Long.class);
      if (parentStartTime != null) {
        /* Close the parent */
        @Nullable Long collected = parentContextClass.getHandlerData("collected-stack-logger", false, Long.class);
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
    @Nullable Long startTime = pContext.getHandlerData("start-stack-logger", false, Long.class);
    if (startTime == null) startTime = endTime;
    try {
      writeStackTrace(pContext);
      @Nullable Long collected = pContext.getHandlerData("collected-stack-logger", false, Long.class);
      if (collected == null) collected = 0L;
      collected = collected + Math.max(endTime - startTime, 0);
      mWriter.write(collected + "\n");
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    /* Reactivate the parent */
    @Nullable ContextClass parentContextClass = pContext.getParentContextClass();
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
    @Nullable Long startTime = pContext.getHandlerData("start-stack-logger", false, Long.class);
    if (startTime != null) {
      long endTime = System.currentTimeMillis();
      @Nullable Long collected = pContext.getHandlerData("collected-stack-logger", false, Long.class);
      if (collected == null) collected = 0L;
      collected = collected + Math.max(endTime - startTime, 0);
      pContext.setHandlerData("collected-stack-logger", collected);
      pContext.setHandlerData("start-stack-logger", null);
    }
    /* Reactivate the parent */
    @Nullable ContextClass parentContextClass = pContext.getParentContextClass();
    if (parentContextClass != null) {
      parentContextClass.setHandlerData("start-stack-logger", System.currentTimeMillis());
    }
  }

  @Override
  public void executeOnAttachContextToThread(ContextClass pContext) {
    @Nullable Long startTime = pContext.getHandlerData("start-stack-logger", false, Long.class);
    if (startTime == null) {
      startTime = System.currentTimeMillis();
      pContext.setHandlerData("start-stack-logger", startTime);
      @Nullable ContextClass parentContextClass = pContext.getParentContextClass();
      if (parentContextClass != null) {
        @Nullable Long parentStartTime = parentContextClass.getHandlerData("start-stack-logger", false, Long.class);
        if (parentStartTime != null) {
          /* Close the parent */
          @Nullable Long collected = parentContextClass.getHandlerData("collected-stack-logger", false, Long.class);
          if (collected == null) collected = 0L;
          collected = collected + Math.max(0, startTime - parentStartTime);
          parentContextClass.setHandlerData("collected-stack-logger", collected);
          parentContextClass.setHandlerData("start-stack-logger", null);
        }
      }
    }
  }

}
