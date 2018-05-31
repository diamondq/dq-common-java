package com.diamondq.common.utils.log4j2;

import java.io.Serializable;
import java.nio.charset.Charset;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;

@Plugin(name = "OpenTracing", category = "Core", elementType = "appender", printObject = true)
public class TracingAppender extends AbstractAppender {

	private static final Charset sUTF8 = Charset.forName("UTF-8");

	public TracingAppender(String pName, @Nullable Filter pFilter, Layout<? extends Serializable> pLayout,
		boolean pIgnoreExceptions) {
		super(pName, pFilter, pLayout, pIgnoreExceptions);
	}

	@PluginFactory
	public static @Nullable TracingAppender createAppender(@PluginAttribute("name") @Nullable String name,
		@PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
		@PluginElement("Layout") @Nullable Layout<@NonNull ? extends @NonNull Serializable> layout,
		@PluginElement("Filters") @Nullable Filter filter) {

		if (name == null) {
			LOGGER.error("No name provided for StubAppender");
			return null;
		}

		if (layout == null) {
			@SuppressWarnings("null")
			Layout<@NonNull ? extends @NonNull Serializable> defaultLayout = PatternLayout.createDefaultLayout();
			layout = defaultLayout;
		}
		return new TracingAppender(name, filter, layout, ignoreExceptions);
	}

	@Override
	public void append(LogEvent pEvent) {
		Span activeSpan = GlobalTracer.get().activeSpan();
		if (activeSpan == null)
			return;
		Layout<? extends Serializable> layout = getLayout();
		String data;
		if (layout == null)
			data = pEvent.toString();
		else {
			byte[] byteArray = layout.toByteArray(pEvent);
			data = new String(byteArray, sUTF8);
		}
		activeSpan.log(pEvent.getTimeMillis() * 1000, data);
	}
}