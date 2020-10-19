package com.automic.packages.common.xml;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.automic.packages.common.file.RandomAccessFileUtils;
import com.automic.packages.common.unicode.UnicodeInputStreamReader;

public class XmlParser {

	class DefaultEntityResolver implements EntityResolver {

		@Override
		public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException, IOException {
			// TODO Auto-generated method stub
			return new InputSource(new StringReader(""));
		}

	}

	class DefaultErrorHandler implements ErrorHandler {
		StringBuffer warn;
		StringBuffer error;
		StringBuffer fatalError;

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			// TODO Auto-generated method stub
			if (warn == null) {
				warn = new StringBuffer();
			}
			warn.append(exception.getMessage());
			warn.append('\n');
		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			// TODO Auto-generated method stub
			if (error == null) {
				error = new StringBuffer();
			}
			error.append(exception.getMessage());
			error.append('\n');
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			// TODO Auto-generated method stub
			if (fatalError == null) {
				fatalError = new StringBuffer();
			}
			fatalError.append(exception.getMessage());
			fatalError.append('\n');
		}

	}

	DefaultEntityResolver er;
	DefaultErrorHandler eh;

	/** The default property of DocumentBuilderFactory */
	boolean validating = false;
	boolean namespaceAware = false;

	boolean entityResolve = false;
	String xmlEncoding;
	boolean bom = false;

	Map<String, List<Byte>> m = new HashMap<String, List<Byte>>() {
		/**
         *
         */
		private static final long serialVersionUID = 2363479186897299257L;

		{
			put("UTF-16BE",
					Arrays.asList(new Byte[] { 0, 60, 0, 63, 0, 120, 0, 109, 0,
							108 }));
			put("UTF-16LE",
					Arrays.asList(new Byte[] { 60, 0, 63, 0, 120, 0, 109, 0,
							108, 0 }));
			put("UTF-32BE",
					Arrays.asList(new Byte[] { 0, 0, 0, 60, 0, 0, 0, 63, 0, 0,
							0, 120, 0, 0, 0, 109, 0, 0, 0, 108 }));
			put("UTF-32LE",
					Arrays.asList(new Byte[] { 60, 0, 0, 0, 63, 0, 0, 0, 120,
							0, 0, 0, 109, 0, 0, 0, 108, 0, 0, 0 }));
		}
	};

	public String getErrors() {
		String s = null;
		if (eh != null) {
			if (eh.error != null) {
				s = eh.error.toString();
			}

		}
		return s;
	}

	public String getWarnings() {
		String s = null;
		if (eh != null) {
			if (eh.warn != null) {
				s = eh.warn.toString();
			}

		}
		return s;
	}

	public void turnOnValidating() {
		this.validating = true;
	}

	public void turnOffValidating() {
		this.validating = false;
	}

	public void turnOnNamespaceAware() {
		this.namespaceAware = true;
	}

	public void turnOffNamespaceAware() {
		this.namespaceAware = false;
	}

	public void turnOnEntityResolve() {
		this.entityResolve = true;
	}

	public void turnOffEntityResolve() {
		this.entityResolve = false;
	}

	public String getXmlEncoding() {
		return xmlEncoding;
	}

	public boolean hasBOM() {
		return bom;
	}

	public Document parse(RandomAccessFile raf) throws IOException,
			SAXException, ParserConfigurationException {
		return parse(raf, "");
	}

	public Document parse(RandomAccessFile raf, String defaultEnc)
			throws IOException, SAXException, ParserConfigurationException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(validating);
		dbf.setNamespaceAware(namespaceAware);
		dbf.setIgnoringElementContentWhitespace(true);

		DocumentBuilder builder = dbf.newDocumentBuilder();
		eh = null;
		er = null;
		if (validating) {

			eh = new DefaultErrorHandler();
			builder.setErrorHandler(eh);

		}

		if (entityResolve == false) {
			er = new DefaultEntityResolver();
			builder.setEntityResolver(er);
		}

		Document doc = null;

		// the first time, detect bom
		String fileEnc = RandomAccessFileUtils.detectFileEncoding(raf);
		bom = !fileEnc.isEmpty();

		if (fileEnc.isEmpty()) {
			// try to search '<?xml' string in file
			List<Byte> source = RandomAccessFileUtils.fileAsByteArray(raf);
			for (Map.Entry<String, List<Byte>> entry : m.entrySet()) {

				int pos = Collections.indexOfSubList(source, entry.getValue());
				if (pos != -1) {
					fileEnc = entry.getKey();
					break;
				}
			}

		}

		String enc = !fileEnc.isEmpty() ? fileEnc : defaultEnc;
		raf.seek(0L);
		UnicodeInputStreamReader reader = new UnicodeInputStreamReader(
				raf.getFD(), enc);
		InputSource is = new InputSource(reader);
		doc = builder.parse(is);

		if (!fileEnc.isEmpty()) {
			xmlEncoding = fileEnc;

		} else if (!defaultEnc.isEmpty()) {
			xmlEncoding = defaultEnc;
		} else if (doc.getXmlEncoding() != null) {
			xmlEncoding = doc.getXmlEncoding();
		} else {
			xmlEncoding = "UTF-8";
		}

		return doc;
	}
}