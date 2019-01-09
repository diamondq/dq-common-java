package com.diamondq.common.security.jcasbin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.casbin.jcasbin.model.Model;

public class ModelUtils {

  public static Model loadFromResource(Class<?> pClass, String pResourceName) {
    Model result = new Model();

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (InputStream is = pClass.getResourceAsStream(pResourceName)) {
        if (is == null)
          throw new IllegalArgumentException();
        byte[] buffer = new byte[32000];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1)
          baos.write(buffer, 0, bytesRead);
      }
      baos.flush();
      String text = new String(baos.toByteArray(), "UTF-8");
      result.loadModelFromText(text);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }

    return result;
  }
}
