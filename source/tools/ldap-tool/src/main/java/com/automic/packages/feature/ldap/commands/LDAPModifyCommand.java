package com.automic.packages.feature.ldap.commands;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.automic.packages.common.exception.FileLockedException;
import com.automic.packages.feature.FeatureUtil;
import com.automic.packages.feature.FeatureUtil.MsgTypes;
import com.automic.packages.feature.globalcodes.ErrorCodes;
import com.automic.packages.feature.ldap.LDAPFeature;
import com.automic.packages.feature.utils.FileUtil;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

public class LDAPModifyCommand {

	public static int execute(String[] args, LDAPConnection connection)
			throws Exception {
		int errorCode = ErrorCodes.OK;
		String[] ldifLines = args[5].split("\\|");
		String ldifFilePath = args[6];

		LDAPResult modifyResult = null;

		if (!StringUtils.isBlank(ldifFilePath)) {
			File ldifFile = new File(ldifFilePath);

			if (FileUtil.verifyFileExists(ldifFile)) {
				String[] ldifLinesFromFile = null;
				try {
					ldifLinesFromFile = LDAPFeature.readLDIFFile(ldifFile);
				} catch (FileLockedException e) {
					FeatureUtil.logMsg("Couldn't get lock for the file '" + ldifFile.getAbsolutePath() + "':" + e.getMessage(),
							MsgTypes.ERROR);
					return ErrorCodes.ERROR;
				}
				FeatureUtil.logMsg("Modifying " + ldifLinesFromFile[0]);
				modifyResult = connection.modify(ldifLinesFromFile);

				if (modifyResult.getResultCode() == ResultCode.SUCCESS) {
					errorCode = ErrorCodes.OK;
					FeatureUtil.logMsg("LDAP-RESULT: " + ldifLines.length);
				} else {
					errorCode = ErrorCodes.ERROR;
					FeatureUtil.logMsg("LDAP-RESULT: 0");
				}
			} else {
				errorCode = ErrorCodes.SEVERE;
				FeatureUtil.logMsg("File " + ldifFilePath + " does not exist!",
						MsgTypes.ERROR);
				return errorCode;
			}
		} else {
			if (!ldifLines[0].trim().equals("")) {
				FeatureUtil.logMsg("Modifying " + ldifLines[0]);
				modifyResult = connection.modify(ldifLines);

				if (modifyResult.getResultCode() == ResultCode.SUCCESS) {
					errorCode = ErrorCodes.OK;
					FeatureUtil.logMsg("LDAP-RESULT: " + ldifLines.length);
				} else {
					errorCode = ErrorCodes.ERROR;
					FeatureUtil.logMsg("LDAP-RESULT: 0");
				}
			} else {
				errorCode = ErrorCodes.SEVERE;
				FeatureUtil.logMsg("LDIF command argument is empty!",
						MsgTypes.ERROR);
				return errorCode;
			}
		}

		return errorCode;
	}
}
