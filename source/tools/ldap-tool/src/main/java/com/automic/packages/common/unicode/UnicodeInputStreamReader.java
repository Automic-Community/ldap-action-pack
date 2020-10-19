package com.automic.packages.common.unicode;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class UnicodeInputStreamReader extends Reader {

	private InputStreamReader isr;

	public UnicodeInputStreamReader(FileDescriptor fdObj, String defaultEncoding)
			throws IOException {
		this(new FileInputStream(fdObj), defaultEncoding);
	}

	public UnicodeInputStreamReader(InputStream in, String defaultEncoding)
			throws IOException {

		UnicodeInputStream uis = new UnicodeInputStream(in);
		String actualEncoding = !uis.getDetectedEncoding().isEmpty() ? uis
				.getDetectedEncoding() : defaultEncoding;

		if (actualEncoding.isEmpty()) {
			isr = new InputStreamReader(uis);
		} else {
			CharsetDecoder dec = Charset.forName(actualEncoding)
					.newDecoder();
			isr = new InputStreamReader(uis, dec);
		}

	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {

		return isr.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException {
		//isr.close();
	}

}