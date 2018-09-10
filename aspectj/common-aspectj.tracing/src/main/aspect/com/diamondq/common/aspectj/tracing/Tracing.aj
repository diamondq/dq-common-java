package com.diamondq.common.aspectj.tracing;

import com.diamondq.common.utils.misc.logging.LoggingUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect Tracing {

  private interface HasLogger {
  }

  declare        parents: (com.diamondq.storely.*) implements HasLogger;

  private Logger HasLogger.sLogger = LoggerFactory.getLogger(HasLogger.class);

  pointcut tracedConstructor(): execution(new(..));

  pointcut tracedMethod(): execution(* *(..));

  before(HasLogger p): target(p) && tracedConstructor() {
    LoggingUtils.entry(p.sLogger, this, thisJoinPoint.getArgs());
  }

  after(HasLogger p): target(p) && tracedConstructor() {
    LoggingUtils.exit(p.sLogger, this, thisJoinPoint.getArgs());
  }

  before(HasLogger p):target(p) &&  tracedMethod() {
    LoggingUtils.entry(p.sLogger, this, thisJoinPoint.getArgs());
  }

  after(HasLogger p): target(p) && tracedMethod() {
    LoggingUtils.exit(p.sLogger, this, thisJoinPoint.getArgs());
  }
}
