package com.diamondq.common.utils.logback;

import static ch.qos.logback.core.pattern.color.ANSIConstants.DEFAULT_FG;
import static ch.qos.logback.core.pattern.color.ANSIConstants.ESC_END;
import static ch.qos.logback.core.pattern.color.ANSIConstants.ESC_START;
import static com.diamondq.common.utils.logback.ExtraANSIConstants.DEFAULT_BG;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

public class DQColorMessage extends CompositeConverter<ILoggingEvent>
{
	private static Map<String, String> sMATCHES;

	static
	{
		final HashMap<String, String> map = new HashMap<>();
		map.put("%yellow%", ESC_START + ExtraANSIConstants.CLUT_BG + ExtraANSIConstants.CLUT_YELLOW + ESC_END);
		map.put("%green%", ESC_START + ExtraANSIConstants.CLUT_BG + ExtraANSIConstants.CLUT_GREEN + ESC_END);
		sMATCHES = Collections.unmodifiableMap(map);
	}
	final private static String SET_DEFAULT_COLOR = ESC_START + "0;" + DEFAULT_FG + DEFAULT_BG + ESC_END;

	@Override
	protected String transform(ILoggingEvent event, String in)
	{
		/* Look for given substrings */

		boolean modified = false;
		for (final Map.Entry<String, String> pair : sMATCHES.entrySet())
		{
			final int offset = in.indexOf(pair.getKey());
			if (offset == -1)
			{
				continue;
			}
			modified = true;
			in = in.substring(0, offset) + pair.getValue() + in.substring(offset + pair.getKey().length());
		}
		if (modified == true)
		{
			final StringBuilder sb = new StringBuilder();
			sb.append(in);
			sb.append(SET_DEFAULT_COLOR);
			return sb.toString();
		}
		else
		{
			return in;
		}
	}

}
