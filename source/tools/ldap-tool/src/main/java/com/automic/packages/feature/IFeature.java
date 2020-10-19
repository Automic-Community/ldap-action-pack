package com.automic.packages.feature;

public interface IFeature {

	void initialize();
	
	int run(String[] args) throws Exception;
	
	void finalize(int errorCode);
	
	void printUsage();

	void printDescription(boolean isOneliner);
}
