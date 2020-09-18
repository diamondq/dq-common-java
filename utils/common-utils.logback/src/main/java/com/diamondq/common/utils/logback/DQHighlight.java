package com.diamondq.common.utils.logback;

import static ch.qos.logback.core.pattern.color.ANSIConstants.BOLD;
import static ch.qos.logback.core.pattern.color.ANSIConstants.DEFAULT_FG;
import static ch.qos.logback.core.pattern.color.ANSIConstants.ESC_END;
import static ch.qos.logback.core.pattern.color.ANSIConstants.ESC_START;
import static com.diamondq.common.utils.logback.ExtraANSIConstants.CLUT_BG;
import static com.diamondq.common.utils.logback.ExtraANSIConstants.CLUT_BLACK;
import static com.diamondq.common.utils.logback.ExtraANSIConstants.CLUT_BLUE;
import static com.diamondq.common.utils.logback.ExtraANSIConstants.CLUT_FG;
import static com.diamondq.common.utils.logback.ExtraANSIConstants.CLUT_RED;
import static com.diamondq.common.utils.logback.ExtraANSIConstants.DEFAULT_BG;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

public class DQHighlight extends CompositeConverter<ILoggingEvent>
{

	final private static String SET_DEFAULT_COLOR = ESC_START + "0;" + DEFAULT_FG + DEFAULT_BG + ESC_END;

	@Override
	protected String transform(ILoggingEvent event, String in)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(ESC_START);
		final Level level = event.getLevel();
		switch (level.toInt())
		{
			case Level.ERROR_INT:
				sb.append(BOLD + CLUT_BG + CLUT_RED);
				break;
			case Level.WARN_INT:
				sb.append(CLUT_BG + CLUT_RED);
				break;
			case Level.INFO_INT:
				sb.append(CLUT_BG + CLUT_BLUE);
				break;
			case Level.DEBUG_INT:
				sb.append(CLUT_BG + "248;" + CLUT_FG + CLUT_BLACK);
				break;
			case Level.TRACE_INT:
				sb.append(CLUT_BG + "252;" + CLUT_FG + CLUT_BLACK);
				break;
			default:
				sb.append(DEFAULT_FG);
				break;
		}
		sb.append(ESC_END);
		sb.append(in);
		sb.append(SET_DEFAULT_COLOR);
		return sb.toString();
	}

}
