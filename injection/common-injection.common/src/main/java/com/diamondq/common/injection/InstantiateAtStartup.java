package com.diamondq.common.injection;

/**
 * This marker interface is added to @Singleton's that should be started on startup instead of on-demand. This is used
 * as a replacement for the injection platform specific model.
 */
public interface InstantiateAtStartup {

}
