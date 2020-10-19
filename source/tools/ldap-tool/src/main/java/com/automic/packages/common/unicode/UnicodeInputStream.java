package com.automic.packages.common.unicode;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class UnicodeInputStream extends InputStream {

	protected PushbackInputStream pis;

	private String detectedEncoding = "";

	@Override
	public int read() throws IOException {

		return pis.read();
	}

	public String getDetectedEncoding() {
		return detectedEncoding;
	}

	public UnicodeInputStream(FileDescriptor fdObj) throws IOException {
		this(new FileInputStream(fdObj));
	}

	public UnicodeInputStream(InputStream in) throws IOException {
		final int BOM_SIZE = 4;
		pis = new PushbackInputStream(in, BOM_SIZE);
		byte bom[] = new byte[BOM_SIZE];
		int unread;

		// Read first four bytes and check for BOM marks
		int n = this.pis.read(bom);

		if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB)
				&& (bom[2] == (byte) 0xBF)) {
			detectedEncoding = "UTF-8";
			unread = n - 3;
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			detectedEncoding = "UTF-16BE";
			unread = n - 2;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			detectedEncoding = "UTF-16LE";
			unread = n - 2;
		} else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00)
				&& (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			detectedEncoding = "UTF-32BE";
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)
				&& (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			detectedEncoding = "UTF-32LE";
			unread = n - 4;
		} else {
			unread = n;
		}
		if (unread > 0) {
			pis.unread(bom, (n - unread), unread);
		}
	}

	public synchronized void close() throws IOException {
		pis.close();
	}

	public int available() throws IOException {
		return pis.available();
	}

	public long skip(long n) throws IOException {
		return pis.skip(n);
	}

	public int read(byte b[], int off, int len) throws IOException {
		return pis.read(b, off, len);
	}
}
