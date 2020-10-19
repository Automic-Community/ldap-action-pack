package com.automic.packages.common.exception;

public class DataNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1493507548682974078L;

	public DataNotFoundException(String message) {
		super(message);
	}

	public DataNotFoundException() {
		super();
	}
}