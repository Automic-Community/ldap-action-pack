/* 
 * (c) 2012 Michael Schwartz e.U. 
 * All Rights Reserved.
 * 
 * This program is not a free software. The owner of the copyright
 * can license the software for you. You may not use this file except in
 * compliance with the License. In case of questions please
 * do not hesitate to contact us at idx@mschwartz.eu.
 * 
 * Filename: LDAPSearchCommand.java
 * Created: 10.05.2012
 * 
 * Author: $LastChangedBy$ 
 * Date: $LastChangedDate$ 
 * Revision: $LastChangedRevision$ 
 */
package com.automic.packages.feature.ldap.commands;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.automic.packages.feature.FeatureUtil;
import com.automic.packages.feature.FeatureUtil.MsgTypes;
import com.automic.packages.feature.globalcodes.ErrorCodes;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

public class LDAPSearchCommand {

	private static boolean isSpecific = false;
	private static boolean isNone = false;

	public static int execute(String[] args, LDAPConnection connection)
			throws Exception {
		int errorCode = ErrorCodes.OK;
		String dn = args[5];
		String scopeString = args[6];
		int sizeLimit = Integer.parseInt(args[7]);
		int timeLimit = Integer.parseInt(args[8]);
		String resultFormat = args[9];
		String filter = args[10];
		String attributes = args[11];
		String specificAttributes = args[12];
		String outputFile = args[13];
		int failIfBelow = Integer.parseInt(args[14]);

		SearchScope scope = getSearchScope(scopeString);

		String effectiveAttribute = getEffectiveAttributes(attributes,
				specificAttributes);
		SearchRequest searchRequest = new SearchRequest(dn, scope, filter,
				effectiveAttribute.split("\\,"));

		if (timeLimit > 0)
			searchRequest.setTimeLimitSeconds(timeLimit);
		if (sizeLimit > 0)
			searchRequest.setSizeLimit(sizeLimit);

		FeatureUtil.logMsg("Searching for DN: " + dn + " with scope: " + scope
				+ " and filter: " + filter);

		SearchResult searchResults = null;
		List<SearchResultEntry> searchResultEntries = null;
		try {
			searchResults = connection.search(searchRequest);
			searchResultEntries = searchResults.getSearchEntries();
		} catch (Exception e) {
			searchRequest.setSizeLimit(0);
			searchResults = connection.search(searchRequest);
			searchResultEntries = searchResults.getSearchEntries().subList(0, sizeLimit);
		}
		
		int tempSize = searchResultEntries.size();
		if (failIfBelow > 0
				&& tempSize < failIfBelow) {
			errorCode = ErrorCodes.SEVERE;
			FeatureUtil.logMsg("Searchresult count ("
					+ tempSize
					+ ") is below limit (" + failIfBelow + ")", MsgTypes.ERROR);
		} else {
			if (tempSize > 0) {
				FeatureUtil.logMsg("LDAP-Search-Result-Count: "
						+ tempSize);
				FileOutputStream fos = null;
				if (!outputFile.trim().equals("")) {
					fos = new FileOutputStream(outputFile.trim());
				}
				try {
					for (int i = 0; i < tempSize; i++) {
						SearchResultEntry entry = searchResults
								.getSearchEntries().get(i);
						String resultLine = "";

						if (resultFormat.trim().toLowerCase().equals("both")) {
							if (isSpecific) {
								resultLine = getSpecificResultLine(
										specificAttributes, entry, 0);
							} else if (isNone) {
								resultLine = getNoneResultLine(entry);
							} else {
								resultLine = getGeneralResultLine(entry, 0);
							}
						} else if (resultFormat.trim().toLowerCase()
								.equals("names")) {
							if (isSpecific) {
								resultLine = getSpecificResultLine(
										specificAttributes, entry, 1);
							} else if (isNone) {
								resultLine = getNoneResultLine(entry);
							} else {
								resultLine = getGeneralResultLine(entry, 1);
							}
						} else if (resultFormat.trim().toLowerCase()
								.equals("values")) {
							if (isSpecific) {
								resultLine = getSpecificResultLine(
										specificAttributes, entry, 2);
							} else if (isNone) {
								resultLine = getNoneResultLine(entry);
							} else {
								resultLine = getGeneralResultLine(entry, 2);
							}
						}

						if (fos != null) {
							// entry.toLDIFString() will produce: 'dn:
							// cn=testschablone,dc=example,dc=com' which is much
							// harder to analyze
							fos.write(resultLine.getBytes());
						}

						FeatureUtil.logMsg("LDAP-Search-Result: " + resultLine);
//						FeatureUtil.logMsg("LDAP-Search-Result:");
//						for (String line : resultLine.split("\n")) {
//							if (line.trim().length() == 0)
//								continue;
//							line = line.replace("\n", "");
//							line = line.replace("\r", "");
//							if (line.length() > 80)
//								line = line.substring(0, 60) + "..."
//										+ line.substring(line.length() - 16);
//							FeatureUtil.logMsg("  " + line);
//						}
					}
				} finally {
					if (fos != null) {
						fos.close();
					}
				}
			} else {
				FeatureUtil.logMsg("LDAP-Search-Result: none");
			}
		}
		return errorCode;
	}

	private static String getNoneResultLine(SearchResultEntry entry) {
		String resultLine;
		resultLine = entry.getDN() + "\n" + entry.getDN() + "\r\n";

		return resultLine;
	}

	private static String getGeneralResultLine(SearchResultEntry entry,
			int formatCode) {
		String resultLine;
		resultLine = entry.getDN() + "\n";
		for (Attribute attribute : entry.getAttributes()) {
			if (formatCode == 0) {
				resultLine += attribute.getName() + ": " + attribute.getValue()
						+ ",";
			} else if (formatCode == 1) {
				resultLine += attribute.getName() + ",";
			} else if (formatCode == 2) {
				resultLine += attribute.getValue() + ",";
			}
		}

		resultLine = resultLine.substring(0, resultLine.length() - 1);
		resultLine += "\r\n";
		return resultLine;
	}

	/**
	 * Gets the specific result line and replaces the placeholders coming from
	 * <code>specificAttributes</code> with the results from ldap.
	 * 
	 * @param specificAttributes
	 *            the specific attributes
	 * @param entry
	 *            the entry
	 * @param formatCode
	 *            the format code
	 * @return the specific result line
	 */
	private static String getSpecificResultLine(String specificAttributes,
			SearchResultEntry entry, int formatCode) {

		String resultLine = entry.getDN() + "\r\n";
		int beginIndex = 0;
		int tokenIndex = 0;

		while (true) {
			int startPos = specificAttributes.indexOf("[", beginIndex);
			int endPos = specificAttributes.indexOf("]", startPos);
			if (startPos < 0 || endPos < 0)
				break;
			// append the filler (if any)
			resultLine += specificAttributes.substring(beginIndex, startPos);

			if (entry.getAttributes().size() > tokenIndex) {
				// attribute found, replace the placeholder with the attribute
				// in the desired format
				Attribute attribute = (Attribute) entry.getAttributes()
						.toArray()[tokenIndex];
				if (formatCode == 0) {
					resultLine += attribute.getName() + ": "
							+ attribute.getValue();
				} else if (formatCode == 1) {
					resultLine += attribute.getName();
				} else if (formatCode == 2) {
					resultLine += attribute.getValue();
				}
				tokenIndex++;
			} else {
				// attribute not found, replace with empty string according to
				// christopher, 20120510
				// resultLine += specificAttributes.substring(beginIndex,
				// endPos);
			}
			beginIndex = endPos + 1;
		}

		resultLine += "\r\n";
		return resultLine;
	}

	private static String getEffectiveAttributes(String attributes,
			String specificAttributes) {
		if (attributes.toLowerCase().equals("*"))
			return SearchRequest.ALL_USER_ATTRIBUTES;
		else if (attributes.toLowerCase().equals("+"))
			return SearchRequest.ALL_OPERATIONAL_ATTRIBUTES;
		else if (attributes.toLowerCase().equals("1.1"))
			return SearchRequest.NO_ATTRIBUTES;
		else if (attributes.toLowerCase().equals("-"))
			return getSpecificAttributes(specificAttributes);

		return "";
	}

	/**
	 * extracts the desired attributes from the string
	 * <code>specificAttributes</code>.
	 * 
	 * @param specificAttributes
	 *            the specific attributes. Is used to define a set of attributes
	 *            which will be included in the search result if 'Attributes' is
	 *            set to 'Specific'. A single attribute (eg: "[surname]") or a
	 *            list of attributes can be provided enclosed by square brackets
	 *            (eg: "[surname], [title] - [phone]")
	 * @return the specific attributes
	 */
	private static String getSpecificAttributes(String specificAttributes) {
		String attributes = "";
		isSpecific = true;

		int beginIndex = 0;

		while (true) {
			int startPos = specificAttributes.indexOf("[", beginIndex);
			int endPos = specificAttributes.indexOf("]", startPos);
			if (startPos < 0 || endPos < 0)
				break;
			if (attributes.length() > 0)
				attributes += ",";
			attributes += specificAttributes.substring(startPos + 1, endPos);
			beginIndex = endPos + 1;
		}

		return attributes;
	}

	private static SearchScope getSearchScope(String scopeString) {
		if (scopeString.equals("Base"))
			return SearchScope.BASE;
		if (scopeString.equals("Subtree"))
			return SearchScope.SUBORDINATE_SUBTREE;
		if (scopeString.equals("One"))
			return SearchScope.ONE;

		return SearchScope.SUB;
	}
}
