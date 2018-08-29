package com.diamondq.common.utils.misc.internationalization;

import java.util.Locale;
import java.util.ResourceBundle;

public interface MessagesEnum {

  public String getCode();

  public ResourceBundle getBundle(Locale pLocale);

}
