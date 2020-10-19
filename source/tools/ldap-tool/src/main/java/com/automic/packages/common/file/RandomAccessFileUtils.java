package com.automic.packages.common.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.automic.packages.common.exception.FileLockedException;
import com.automic.packages.common.unicode.UnicodeInputStream;
import com.automic.packages.feature.FeatureUtil;
import com.automic.packages.feature.FeatureUtil.MsgTypes;

public class RandomAccessFileUtils {

	public static FileLock lock(RandomAccessFile raf, int numberOfRetries,
			long waitTime) throws FileLockedException {
		FileLock lock = null;
		FileChannel fc = raf.getChannel();
		for ( int i = 0; i < numberOfRetries; i++) {
			Exception ex = null;
			try {
				lock = fc.tryLock();
				if (lock != null && lock.isValid()) {
					FeatureUtil.logMsg("Acquired the lock after " + (i + 1)
							+ " attempts.", MsgTypes.INFO);
					break;
				} 
			} catch (Exception e) {	
				lock = null;
				ex = e; 
			}
			FeatureUtil.logMsg(
					"Couldn't acquire an exclusive lock on this file after '"
							+ (i + 1) + "' attempts.", MsgTypes.WARNING);
			if (i == numberOfRetries - 1) {
				String msg = ( ex != null ) ? ex.getMessage() : "";
				throw new FileLockedException( msg);
			}
			try {
				TimeUnit.MILLISECONDS.sleep(waitTime);
			} catch (InterruptedException ie) {}
			
		}

		return lock;

	}

	public static List<Byte> fileAsByteArray(RandomAccessFile raf) throws IOException {
		raf.seek(0L);
		byte[] b = new byte[(int)raf.length()];
		raf.readFully(b);
		Byte[] content = new Byte[b.length];
		for(int i = 0; i < b.length;i++) {
			content[i] = b[i];			
		}
		return Arrays.asList(content);
	}
	@SuppressWarnings("resource")
	public static String detectFileEncoding(RandomAccessFile raf)
			throws IOException {
		raf.seek(0L);
		return new UnicodeInputStream(raf.getFD()).getDetectedEncoding();
	}

	@SuppressWarnings("resource")
	public static boolean hasBOM(RandomAccessFile raf) throws IOException {
		raf.seek(0L);
		return !(new UnicodeInputStream(raf.getFD())).getDetectedEncoding()
				.isEmpty();
	}
}
