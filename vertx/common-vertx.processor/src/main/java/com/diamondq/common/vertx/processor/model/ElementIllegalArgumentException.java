package com.diamondq.common.vertx.processor.model;

import com.diamondq.common.utils.misc.errors.ExtendedIllegalArgumentException;
import com.diamondq.common.utils.misc.internationalization.MessagesEnum;

import javax.lang.model.element.Element;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ElementIllegalArgumentException extends ExtendedIllegalArgumentException {

  private static final long serialVersionUID = -2156808969110417281L;

  private final Element     mElement;

  public ElementIllegalArgumentException(Element pElement, MessagesEnum pCode, @Nullable Object @Nullable... pParams) {
    super(pCode, pParams);
    mElement = pElement;
  }

  public ElementIllegalArgumentException(Element pElement, Throwable pCause, MessagesEnum pCode,
    @Nullable Object @Nullable... pParams) {
    super(pCause, pCode, pParams);
    mElement = pElement;
  }

  public Element getElement() {
    return mElement;
  }
}
