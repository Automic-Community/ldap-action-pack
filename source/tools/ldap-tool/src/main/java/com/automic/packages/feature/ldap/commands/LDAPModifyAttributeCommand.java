package com.automic.packages.feature.ldap.commands;

import com.automic.packages.feature.FeatureUtil;
import com.automic.packages.feature.globalcodes.ErrorCodes;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.ResultCode;

public class LDAPModifyAttributeCommand {

	public static int execute(String[] args, LDAPConnection connection)
			throws Exception {
		int errorCode = ErrorCodes.OK;
		String dn = args[5];
		String attribute = args[6];
		String value = args[7];
		String method = args[8];

		ModificationType modificationType = getModificationType(method);

		ModifyRequest modifyRequest;
		if (value.trim().equals("")) {
			modifyRequest = new ModifyRequest(dn,
					new Modification(modificationType,attribute));
		} else {
			modifyRequest = new ModifyRequest(dn,
					new Modification(modificationType,attribute,value));
		}
		FeatureUtil.logMsg("Modifying DN: " + dn + ", attribute: " + attribute
				+ " to value: " + value + " with method: " + method);

		LDAPResult modifyResult;
		ResultCode resultCode;
		String errorMessageFromServer;
		 try
		 {
		   modifyResult = connection.modify(modifyRequest);
		   resultCode = modifyResult.getResultCode();
		 }catch (LDAPException le){
		   modifyResult = le.toLDAPResult();
		   resultCode = le.getResultCode();
		   errorMessageFromServer = le.getDiagnosticMessage();
		   FeatureUtil.logMsg("Error: " + errorMessageFromServer);
		 }

		 /*LDAPTestUtils.assertResultCodeEquals(modifyResult, ResultCode.SUCCESS);

		 LDAPTestUtils.assertHasControl(modifyResult,
		      PreReadResponseControl.PRE_READ_RESPONSE_OID);
		 PreReadResponseControl preReadResponse =
		      PreReadResponseControl.get(modifyResult);
		 Integer beforeValue =
		      preReadResponse.getEntry().getAttributeValueAsInteger(attribute);

		 LDAPTestUtils.assertHasControl(modifyResult,
		      PostReadResponseControl.POST_READ_RESPONSE_OID);
		 PostReadResponseControl postReadResponse =
		      PostReadResponseControl.get(modifyResult);
		 Integer afterValue =
		      postReadResponse.getEntry().getAttributeValueAsInteger(attribute);
		 FeatureUtil.logMsg("The value of " + attribute + " before" + modificationType + " : " + beforeValue);
		 FeatureUtil.logMsg("The value of " + attribute + " after" + modificationType + " : " + afterValue);*/

		if (resultCode.equals(ResultCode.SUCCESS)) {
			errorCode = ErrorCodes.OK;
			//System.out.println("The value of " + attribute + " before" + modificationType + " : " + beforeValue);
			//System.out.println("The value of " + attribute + " after" + modificationType + " : " + afterValue);
		} else
			errorCode = ErrorCodes.ERROR;

		return errorCode;
	}

	private static ModificationType getModificationType(String method) {
		if (method.equalsIgnoreCase("add"))
			return ModificationType.ADD;
		else if (method.equalsIgnoreCase("delete"))
			return ModificationType.DELETE;
		else if (method.equalsIgnoreCase("replace"))
			return ModificationType.REPLACE;
		else if (method.equalsIgnoreCase("increment"))
			return ModificationType.INCREMENT;
		return ModificationType.REPLACE;
	}
}
