package com.diamondq.common.utils.parsing.xml;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class Parser {

	private Parser() {
		throw new UnsupportedOperationException();
	}

	public static Document parse(InputSource pSource) {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(pSource);
			return document;
		}
		catch (ParserConfigurationException | SAXException | IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
