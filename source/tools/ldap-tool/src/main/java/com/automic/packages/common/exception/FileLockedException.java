package com.automic.packages.common.exception;

import java.io.IOException;

public class FileLockedException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6611983567094871296L;

	public FileLockedException() {
		super();
	}

	public FileLockedException(String message) {
		super(message);
	}

}
