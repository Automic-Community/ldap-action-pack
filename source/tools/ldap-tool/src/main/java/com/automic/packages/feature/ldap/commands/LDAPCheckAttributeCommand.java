package com.automic.packages.feature.ldap.commands;

import com.automic.packages.feature.FeatureUtil;
import com.automic.packages.feature.globalcodes.ErrorCodes;
import com.unboundid.ldap.sdk.CompareResult;
import com.unboundid.ldap.sdk.LDAPConnection;

public class LDAPCheckAttributeCommand {

	public static int execute(String[] args, LDAPConnection connection)
			throws Exception {
		int errorCode = ErrorCodes.OK;
		String dn = args[5];
		String attribute = args[6];
		String value = args[7];
		boolean failIfNoMatch = args[8].equalsIgnoreCase("yes") ? true : false;

		CompareResult compareResult = connection.compare(dn, attribute, value);

		if (compareResult.compareMatched()) {
			errorCode = ErrorCodes.OK;
			FeatureUtil.logMsg("LDAP-RESULT: 1");
		} else {
			if (failIfNoMatch)
				errorCode = ErrorCodes.ERROR;
			else
				errorCode = ErrorCodes.OK;
			FeatureUtil.logMsg("LDAP-RESULT: 0");
		}

		return errorCode;
	}
}
