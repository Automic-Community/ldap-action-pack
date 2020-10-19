package com.automic.packages.feature.ldap.commands;

import com.automic.packages.feature.FeatureUtil;
import com.automic.packages.feature.globalcodes.ErrorCodes;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

public class LDAPDeleteCommand {

	public static int execute(String[] args, LDAPConnection connection)
			throws Exception {
		int errorCode = ErrorCodes.ERROR;
		String dn = args[5];
		boolean failIfMissing = args[6].equalsIgnoreCase("yes") ? true : false;

		try {
			FeatureUtil.logMsg("Deleting DN: " + dn);
			LDAPResult result = connection.delete(dn);
			if (result.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
				if (failIfMissing)
					errorCode = ErrorCodes.SEVERE;
				else
					errorCode = ErrorCodes.OK;
			}

			if (result.getResultCode() == ResultCode.SUCCESS)
				errorCode = ErrorCodes.OK;
		} catch (LDAPException e) {
			if (failIfMissing)
				errorCode = ErrorCodes.SEVERE;
			else
				errorCode = ErrorCodes.OK;
		}

		FeatureUtil.logMsg("LDAP-RESULT: " + errorCode);
		return errorCode;
	}
}
