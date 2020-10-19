/* 
 * (c) 2012 Michael Schwartz e.U. 
 * All Rights Reserved.
 * 
 * This program is not a free software. The owner of the copyright
 * can license the software for you. You may not use this file except in
 * compliance with the License. In case of questions please
 * do not hesitate to contact us at idx@mschwartz.eu.
 * 
 * Filename: LDAPAddCommand.java
 * Created: 09.05.2012
 * 
 * Author: $LastChangedBy$ 
 * Date: $LastChangedDate$ 
 * Revision: $LastChangedRevision$ 
 */
package com.automic.packages.feature.ldap.commands;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.automic.packages.common.exception.FileLockedException;
import com.automic.packages.feature.FeatureUtil;
import com.automic.packages.feature.FeatureUtil.MsgTypes;
import com.automic.packages.feature.globalcodes.ErrorCodes;
import com.automic.packages.feature.ldap.LDAPFeature;
import com.automic.packages.feature.utils.FileUtil;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchScope;

/**
 * The Class LDAPAddCommand.
 */
public class LDAPAddCommand {

	/**
	 * Execute.
	 * 
	 * @param args
	 *            the args
	 * @param connection
	 *            the connection
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public static int execute(String[] args, LDAPConnection connection)
			throws Exception {
		int errorCode = ErrorCodes.OK;
		String[] ldifLines = args[5].split("\\|");
		String ldifFilename = args[6];
		boolean failIfExists = args[7].equalsIgnoreCase("yes") ? true : false;

		// if there is a filename specified, read the file into memory
		if (!StringUtils.isBlank(ldifFilename)) {
			File ldifFile = new File(ldifFilename);

			if (!FileUtil.verifyFileExists(ldifFile)) {
				errorCode = ErrorCodes.SEVERE;
				FeatureUtil.logMsg("File " + ldifFilename + " does not exist!",
						MsgTypes.ERROR);
				return errorCode;
			}
			try {
				ldifLines = LDAPFeature.readLDIFFile(ldifFile);
			} catch (FileLockedException e) {
				FeatureUtil.logMsg("Couldn't get lock for the file '" + ldifFile.getAbsolutePath() + "':" + e.getMessage(),
						MsgTypes.ERROR);
				return ErrorCodes.ERROR;
			}
		}

		List<AddEntry> entries = new LinkedList<AddEntry>();
		AddEntry entry = null;
		for (String ldifLine : ldifLines) {
			if (ldifLine.trim().toLowerCase().startsWith("dn:")) {
				// we found a new entry. Store the old one if existing and
				// create a new object.
				if (entry != null)
					entries.add(entry);
				entry = new AddEntry();
				entry.dn = ldifLine.substring(3).trim();
				continue;
			}
			if (ldifLine.trim().length() == 0)
				continue;
			entry.lines.add(ldifLine);
		}
		if (entry != null)
			entries.add(entry);

		if (entries.size() == 0) {
			// no entries found, exit with error
			errorCode = ErrorCodes.SEVERE;
			FeatureUtil.logMsg("LDIF command argument is empty!",
					MsgTypes.ERROR);
			return errorCode;
		}

		// now let us check each entry for existence and stop processing if
		// commanded to do so
		for (AddEntry addEntry : entries) {
			SearchRequest searchRequest = new SearchRequest(addEntry.dn,
					SearchScope.BASE, "(objectClass=*)", "");
			searchRequest.setTimeLimitSeconds(1);
			searchRequest.setSizeLimit(1);
			try {
				SearchResult searchResults = connection.search(searchRequest);
				// System.out.println("search for " + addEntry.dn + " resulted "
				// + searchResults.getEntryCount() + ", failIfExists is "
				// + failIfExists);
				if (searchResults.getEntryCount() == 1 && failIfExists) {
					return ErrorCodes.SEVERE;
				} else if (searchResults.getEntryCount() == 1 && !failIfExists) {
					addEntry.skip = true;
				}
			} catch (LDAPSearchException e) {
				continue;
			}
		}

		// finally we can add the entries
		try {
			int successful = 0;
			for (AddEntry addEntry : entries) {
				if (addEntry.skip)
					continue;
				FeatureUtil.logMsg("Adding DN: " + addEntry.dn);
				List<String> lines = new LinkedList<String>();
				lines.add("DN: " + addEntry.dn);
				lines.addAll(addEntry.lines);
				LDAPResult addResult = connection.add(lines
						.toArray(new String[0]));
				if (addResult.getResultCode() == ResultCode.ATTRIBUTE_OR_VALUE_EXISTS) {
					if (failIfExists)
						errorCode = ErrorCodes.SEVERE;
					else
						errorCode = ErrorCodes.OK;
				}

				if (addResult.getResultCode() == ResultCode.SUCCESS) {
					errorCode = ErrorCodes.OK;
					++successful;
				}
			}
			FeatureUtil.logMsg("LDAP-RESULT: " + successful);
		} catch (LDAPException e) {
			FeatureUtil.logMsg("LDAP-ERROR: " + e.getLocalizedMessage());
			// resultCode=68 (entry already exists)
			if (e.getResultCode().intValue() != 68)
				throw e;
			if (failIfExists)
				errorCode = ErrorCodes.SEVERE;
			else
				errorCode = ErrorCodes.OK;
		}

		return errorCode;
	}

	// ///////////////////////////////////////////////////////////////////////

	/**
	 * The Class AddEntry is a helper class to store entry specific attributes.
	 */
	private static class AddEntry {

		private boolean skip = false;

		/**
		 * The dn.
		 */
		private String dn;

		private List<String> lines = new LinkedList<String>();
	}

}
