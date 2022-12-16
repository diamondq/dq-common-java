package com.diamondq.common.converters.impl;

import com.diamondq.common.converters.AbstractConverter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

@Singleton
public class StringToDocumentConverter extends AbstractConverter<String, Document> {

  public StringToDocumentConverter() {
    super(String.class, Document.class, null);
  }

  @Override
  public Document convert(String pInput) {
    try {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      final DocumentBuilder builder = factory.newDocumentBuilder();
      final Document document = builder.parse(new InputSource(new StringReader(pInput)));
      return document;
    }
    catch (IOException | SAXException | ParserConfigurationException ex) {
      throw new RuntimeException(ex);
    }
  }
}
