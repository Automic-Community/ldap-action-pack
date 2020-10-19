package com.automic.packages.feature.ldap.commands;

import com.automic.packages.feature.FeatureUtil;
import com.automic.packages.feature.globalcodes.ErrorCodes;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

public class LDAPModifyDNCommand {

	public static int execute(String[] args, LDAPConnection connection)
			throws Exception {
		int errorCode = ErrorCodes.OK;
		String dn = args[5];
		String newRdn = args[6];
		String newSuperiorDn = args[7];
		boolean deleteOldRdn = args[8].equalsIgnoreCase("yes") ? true : false;

		LDAPResult modifyResult = null;

		FeatureUtil.logMsg("Modifying DN: " + dn + " to new dn: " + newRdn);
		if (newSuperiorDn.trim().equals(""))
			modifyResult = connection.modifyDN(dn, newRdn, deleteOldRdn);
		else
			modifyResult = connection.modifyDN(dn, newRdn, deleteOldRdn,
					newSuperiorDn);

		if (modifyResult.getResultCode() == ResultCode.SUCCESS) {
			errorCode = ErrorCodes.OK;
			FeatureUtil.logMsg("LDAP-RESULT: " + newRdn);
		} else {
			errorCode = ErrorCodes.ERROR;
			FeatureUtil.logMsg("LDAP-RESULT: 0");
		}

		return errorCode;
	}
}
