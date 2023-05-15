package com.diamondq.common.utils.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.util.OptionHelper;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.context.spi.ContextClass;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DQContext extends ClassicConverter {

  protected boolean mPrintContext = false;

  protected boolean mPrintRefCount = false;

  protected int mContextLength = -1;

  public DQContext() {
  }

  @Override
  public void start() {
    processOptionList(getOptionList());
  }

  protected void processOptionList(@Nullable List<String> pOptionList) {
    if (pOptionList != null) {
      if (pOptionList.size() >= 1) {
        String[] optionInfo = OptionHelper.extractDefaultReplacement(pOptionList.get(0));
        String key = optionInfo[0];
        if (key != null) {
          mContextLength = Integer.parseInt(key);
          mPrintContext = true;
        }
      }
      if (pOptionList.size() >= 2) {
        String[] optionInfo = OptionHelper.extractDefaultReplacement(pOptionList.get(1));
        String key = optionInfo[0];
        if (key != null) {
          mPrintRefCount = Boolean.parseBoolean(key);
        }
      }
    }
    super.start();
  }

  /**
   * @see ch.qos.logback.core.pattern.Converter#convert(java.lang.Object)
   */
  @Override
  public String convert(ILoggingEvent pEvent) {
    ContextClass context = (ContextClass) ContextFactory.nullableCurrentContext();
    if (context == null) return "";
    List<String> stack = context.getContextStackNames(mPrintRefCount);
    StringBuilder sb = new StringBuilder();
    int size = stack.size();
    if (mPrintContext == true) {
      StringBuilder mid = new StringBuilder();
      for (int i = 0; i < size; i++) {
        if (i == 0) sb.append(stack.get(i));
        else if (i == (size - 1)) {
          String last = stack.get(i);
          if (mContextLength == -1) sb.append(mid);
          else {
            String midStr = mid.toString();
            if (last.length() + sb.length() + midStr.length() + 1 > mContextLength) {
              int midLen = mContextLength - last.length() - 1 - sb.length() - 4;
              midStr = ">..." + midStr.substring(midStr.length() - midLen);
            }
            sb.append(midStr);
          }
          sb.append(">");
          sb.append(last);
        } else {
          mid.append(">");
          mid.append(stack.get(i));
        }
      }
    } else {
      Boolean duringNormal = context.getHandlerData(ContextClass.sDURING_CONTEXT_CONTROL, false, Boolean.class);
      if ((duringNormal != null) && (duringNormal == true)) size--;
      if (size > 0) for (int i = 0; i < size; i++)
        sb.append("  ");
    }

    return sb.toString();
  }
}
