package com.automic.packages.common.exception;

public class DataNotUniqueException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1221740580806364329L;
	
	public DataNotUniqueException(String message) {
		super(message);
	}

	public DataNotUniqueException() {
		super();
	}

}
