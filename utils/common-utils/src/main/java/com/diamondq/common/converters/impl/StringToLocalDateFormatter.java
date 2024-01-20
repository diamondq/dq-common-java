package com.diamondq.common.converters.impl;

import com.diamondq.common.converters.AbstractConverter;
import jakarta.inject.Singleton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Singleton
public class StringToLocalDateFormatter extends AbstractConverter<String, LocalDate> {

  private static final DateTimeFormatter[] sFormatters = new DateTimeFormatter[] { DateTimeFormatter.ISO_LOCAL_DATE, DateTimeFormatter.BASIC_ISO_DATE };

  public StringToLocalDateFormatter() {
    super(String.class, LocalDate.class, null);
  }

  @Override
  public LocalDate convert(String pInput) {
    for (DateTimeFormatter formatter : sFormatters) {
      try {
        return LocalDate.parse(pInput, formatter);
      }
      catch (DateTimeParseException ex) {
        /* Skip to the next one */
        continue;
      }
    }
    throw new RuntimeException("Unable to convert " + pInput + " to a ZonedDateTime.");
  }
}
