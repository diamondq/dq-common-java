package com.diamondq.common.tracing.opentracing.xmpp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.propagation.TextMapInjectAdapter;
import io.opentracing.util.GlobalTracer;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.stanza.IQEvent;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.IQ.Type;

public class OpenTracingExtender {

	private static final Logger							sLogger				=
		LoggerFactory.getLogger(OpenTracingExtender.class);

	private static final String							sKEYWORD			= "__OPENTRACING__";

	private static final ConcurrentMap<String, String>	sPendingIDRemaps	= new ConcurrentHashMap<>();

	public static class OpenTracingOutboundIQListener implements Consumer<IQEvent> {

		/**
		 * @see java.util.function.Consumer#accept(java.lang.Object)
		 */
		@Override
		public void accept(IQEvent pEvent) {
			Span activeSpan = GlobalTracer.get().activeSpan();
			if (activeSpan == null)
				return;
			IQ iq = pEvent.getIQ();
			Type type = iq.getType();

			/* Only add the OpenTracing details if this is a request */

			if ((type == Type.GET) || (type == Type.SET)) {
				try {
					Map<@NonNull String, @NonNull String> map = new HashMap<>();
					GlobalTracer.get().inject(activeSpan.context(), Format.Builtin.TEXT_MAP,
						new TextMapInjectAdapter(map));
					StringBuilder sb = new StringBuilder();
					sb.append(iq.getId());
					sb.append(sKEYWORD);
					boolean isFirst = true;
					for (Map.Entry<@NonNull String, @NonNull String> pair : map.entrySet()) {
						if (isFirst == true)
							isFirst = false;
						else
							sb.append('&');
						sb.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
						sb.append('=');
						sb.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
					}
					String newId = sb.toString();

					/* Remember this remap to fix the response when it comes back */

					sPendingIDRemaps.put(newId, iq.getId());
					iq.setId(newId);
				}
				catch (IllegalArgumentException | UnsupportedEncodingException ex) {
					throw new RuntimeException(ex);
				}
			}
		}

	}

	public static class OpenTracingInboundIQListener implements Consumer<IQEvent> {

		/**
		 * @see java.util.function.Consumer#accept(java.lang.Object)
		 */
		@Override
		public void accept(IQEvent pEvent) {
			IQ iq = pEvent.getIQ();
			Type type = iq.getType();

			/* If it's a response, then fix up the mapping */

			if ((type == Type.RESULT) || (type == Type.ERROR)) {
				String incomingId = iq.getId();
				String remapId = sPendingIDRemaps.remove(incomingId);
				if (remapId != null)
					iq.setId(remapId);
			}

		}

	}

	public static void setup(XmppClient pClient) {
		OpenTracingOutboundIQListener outboundListener = new OpenTracingOutboundIQListener();
		pClient.addOutboundIQListener(outboundListener);
		OpenTracingInboundIQListener inboundListener = new OpenTracingInboundIQListener();
		pClient.addInboundIQListener(inboundListener);
	}

	public static @Nullable SpanBuilder processID(String id) {
		try {
			int offset = id.indexOf(sKEYWORD);
			if (offset != -1) {
				String data = id.substring(offset + sKEYWORD.length());
				Map<String, String> map = new HashMap<>();
				String[] parts = data.split("\\&");
				for (String part : parts) {
					int breakOffset = part.indexOf('=');
					String key = URLDecoder.decode(part.substring(0, breakOffset), "UTF-8");
					String value = URLDecoder.decode(part.substring(breakOffset + 1), "UTF-8");
					map.put(key, value);
				}

				SpanContext spanContext =
					GlobalTracer.get().extract(Format.Builtin.TEXT_MAP, new TextMapExtractAdapter(map));
				return GlobalTracer.get().buildSpan("").asChildOf(spanContext);
			}
		}
		catch (UnsupportedEncodingException ex) {
			sLogger.error("Error, but continuing", ex);
		}
		return null;
	}

	public static class WrappedIQHandler implements IQHandler {

		private final IQHandler	mDelegate;

		@SuppressWarnings("unused")
		private final String	mOperation;

		public WrappedIQHandler(IQHandler pDelegate, String pOperation) {
			mDelegate = pDelegate;
			mOperation = pOperation;
		}

		/**
		 * @see rocks.xmpp.core.stanza.IQHandler#handleRequest(rocks.xmpp.core.stanza.model.IQ)
		 */
		@Override
		public IQ handleRequest(IQ pIQ) {

			/* Let's see if there is a tracing block */

			String id = pIQ.getId();
			SpanBuilder spanBuilder = processID(id);
			if (spanBuilder == null)
				return mDelegate.handleRequest(pIQ);
			try (Scope scope = spanBuilder.startActive(true)) {
				return mDelegate.handleRequest(pIQ);
			}

		}

	}

	public static IQHandler wrapIQHandler(IQHandler pIQHandler, String pOperation) {
		return new WrappedIQHandler(pIQHandler, pOperation);
	}

}
