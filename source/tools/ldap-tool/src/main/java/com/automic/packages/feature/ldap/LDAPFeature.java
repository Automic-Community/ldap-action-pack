package com.automic.packages.feature.ldap;

import java.io.File;
import java.io.RandomAccessFile;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import com.automic.packages.common.file.RandomAccessFileUtils;
import com.automic.packages.common.unicode.UnicodeInputStreamReader;
import com.automic.packages.feature.AbstractFeature;
import com.automic.packages.feature.FeatureUtil;
import com.automic.packages.feature.globalcodes.ErrorCodes;
import com.automic.packages.feature.ldap.commands.LDAPAddCommand;
import com.automic.packages.feature.ldap.commands.LDAPCheckAttributeCommand;
import com.automic.packages.feature.ldap.commands.LDAPDeleteCommand;
import com.automic.packages.feature.ldap.commands.LDAPModifyAttributeCommand;
import com.automic.packages.feature.ldap.commands.LDAPModifyCommand;
import com.automic.packages.feature.ldap.commands.LDAPModifyDNCommand;
import com.automic.packages.feature.ldap.commands.LDAPSearchCommand;
import com.unboundid.ldap.sdk.ANONYMOUSBindRequest;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.CRAMMD5BindRequest;
import com.unboundid.ldap.sdk.DIGESTMD5BindRequest;
import com.unboundid.ldap.sdk.GSSAPIBindRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.PLAINBindRequest;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SimpleBindRequest;

public class LDAPFeature extends AbstractFeature {

	@Override
	public int run(String[] args) throws Exception {
		int errorCode = ErrorCodes.OK;
		String command = args[0];
		String host = args[1];
		int port = Integer.parseInt(args[2]);
		String bindDN = args[3];
		String password = args[4];
		String authenticationMethod = args[5];
		String failIfAuthenticationFails = args.length > 6 ? args[6] : "true";
		LDAPConnection connection = null;

		args = excludeAuthenticationMethod(args);

		try {
			FeatureUtil.logMsg("Connecting to " + host + ":" + port
					+ " with dn " + bindDN);
			connection = getLDAPConnection(host, port, bindDN, password,
					authenticationMethod);

			if (command.equals("authenticate")) {
				FeatureUtil.logMsg("LDAP-RESULT: 1");
			}

			if (command.equals("add"))
				errorCode = LDAPAddCommand.execute(args, connection);
			if (command.equals("search"))
				errorCode = LDAPSearchCommand.execute(args, connection);
			if (command.equals("modify"))
				errorCode = LDAPModifyCommand.execute(args, connection);
			if (command.equals("modifydn"))
				errorCode = LDAPModifyDNCommand.execute(args, connection);
			if (command.equals("modifyattribute"))
				errorCode = LDAPModifyAttributeCommand
						.execute(args, connection);
			if (command.equals("checkattribute"))
				errorCode = LDAPCheckAttributeCommand.execute(args, connection);
			if (command.equals("delete"))
				errorCode = LDAPDeleteCommand.execute(args, connection);
		} catch (Exception ex) {
			if (command.equals("authenticate")) {
				FeatureUtil.logMsg("LDAP-RESULT: 0");
				FeatureUtil.logMsg(ex.getLocalizedMessage());

				//if (Boolean.parseBoolean(failIfAuthenticationFails)) {
				if (failIfAuthenticationFails.equalsIgnoreCase("yes")) {
					errorCode = ErrorCodes.ERROR;
				}
			} else {
				throw ex;
			}
		} finally {
			if (connection != null && connection.isConnected())
				connection.close();
		}

		return errorCode;
	}

	private LDAPConnection getLDAPConnection(String host, int port,
			String bindDN, String password, String authenticationMethod)
			throws Exception {
		LDAPConnection connection = null;

		if (authenticationMethod.trim().toLowerCase().equals("anonymous")) {
			connection = new LDAPConnection(host, port);
			ANONYMOUSBindRequest bindRequest = new ANONYMOUSBindRequest();
			BindResult result = connection.bind(bindRequest);
			handleBindResult(result);
		} else if (authenticationMethod.trim().toLowerCase()
				.equals("kerberos v5")) {
			connection = new LDAPConnection(host, port);
			GSSAPIBindRequest bindRequest = new GSSAPIBindRequest(bindDN,
					password);
			BindResult result = connection.bind(bindRequest);
			handleBindResult(result);
		} else if (authenticationMethod.trim().toLowerCase()
				.equals("sasl cram-md5")) {
			connection = new LDAPConnection(host, port);
			CRAMMD5BindRequest bindRequest = new CRAMMD5BindRequest(bindDN,
					password);
			BindResult result = connection.bind(bindRequest);
			handleBindResult(result);
		} else if (authenticationMethod.trim().toLowerCase()
				.equals("sasl digest-md5")) {
			connection = new LDAPConnection(host, port);
			DIGESTMD5BindRequest bindRequest = new DIGESTMD5BindRequest(bindDN,
					password);
			BindResult result = connection.bind(bindRequest);
			handleBindResult(result);
		} else if (authenticationMethod.trim().toLowerCase()
				.equals("sasl plain")) {
			connection = new LDAPConnection(host, port);
			PLAINBindRequest bindRequest = new PLAINBindRequest(bindDN,
					password);
			BindResult result = connection.bind(bindRequest);
			handleBindResult(result);
		} else if (authenticationMethod.trim().toLowerCase().equals("simple")) {
			connection = new LDAPConnection(host, port);
			BindResult result = connection.bind(bindDN, password);
			handleBindResult(result);
		} else if (authenticationMethod.trim().toLowerCase()
				.equals("simple ssl")) {
			connection = new LDAPConnection(host, port);
			SimpleBindRequest bindRequest = new SimpleBindRequest(bindDN,
					password);
			SocketFactory socketFactory = SSLSocketFactory.getDefault();
			connection = new LDAPConnection(socketFactory, host, port);
			BindResult result = connection.bind(bindRequest);
			handleBindResult(result);
		}

		return connection;
	}

	private void handleBindResult(BindResult result) throws Exception {
		if (result.getResultCode() != ResultCode.SUCCESS)
			throw new Exception(
					"LDAP BindRequest failed - Please check the provided credentials");
	}

	private String[] excludeAuthenticationMethod(String[] args) {
		String[] myArgs = new String[args.length - 1];
		for (int i = 0; i < args.length; i++) {
			if (i > 4) {
				if (i != 5)
					myArgs[i - 1] = args[i];
			} else
				myArgs[i] = args[i];
		}

		return myArgs;
	}

	public static String[] readLDIFFile(File ldifFile) throws Exception {
		String[] ldifLines = new String[] {};
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(ldifFile, "rw");
			RandomAccessFileUtils.lock(raf, 10, 500);

			@SuppressWarnings("resource")
			UnicodeInputStreamReader reader = new UnicodeInputStreamReader(raf.getFD(), "");
			char[] content = new char[(int) ldifFile.length()];
			int read = 0;
			while (read != -1) {
				int offset = 0;
				read = reader.read(content, offset, content.length - offset);
				offset += read;
			}
			
			ldifLines = new String(content).split("\\r?\\n");
		} finally {
			if (raf != null) try { raf.close(); } catch (Exception e) {}
		}

		return ldifLines;
	}

	@Override
	public int getMinParams() {
		return 6;
	}

	@Override
	public int getMaxParams() {
		return 20;
	}

	@Override
	public void printUsage() {
		FeatureUtil.logMsg("LDAPFeature Command:");
		FeatureUtil
				.logMsg("LDAPFeature <command> <host> <port> <bind-dn> <password> <authenticationMethod> [<failIfAuthenticationFails>]");
		FeatureUtil
				.logMsg("Supported Commands: authenticate|add|search|modify|modifydn|modifyattribute|checkattribute|delete");
		FeatureUtil
				.logMsg("Supported AuthenticationMethods: anonymous|kerberos v5|sasl cram-md5|sasl digest-md5|sasl plain|simple|simple ssl");
		FeatureUtil.logMsg("  specific commands:");

		FeatureUtil
				.logMsg("LDAPFeature search <host> <port> <bind-dn> <password> <authenticationMethod> <baseDN> <scope> <sizeLimit> <timeLimit in seconds> <resultFormat both|names|values> <filter> <attributes> <specificAttributes> <outputFile> <failIfBelow>");
		FeatureUtil
				.logMsg("  filter could be for example '(description=*)'. See RFC 4515 for more information.");
		FeatureUtil
				.logMsg("  attributes can be one of '*' (ALL_USER_ATTRIBUTES), '+' (ALL_OPERATIONAL_ATTRIBUTES), '1.1' (NO_ATTRIBUTES) or '-'. The latter enables the <specificAttributes> field");

		FeatureUtil
				.logMsg("LDAPFeature add <host> <port> <bind-dn> <password> <authenticationMethod> <ldifLines> <ldifFilename> <failIfExists>");
		FeatureUtil
				.logMsg("  either ldifLines or ldifFilename must be specified. ldifLines can contain pipe-separated (|) values");
		FeatureUtil.logMsg("  failIfExists can be one of 'yes' or 'no'");

		FeatureUtil
				.logMsg("LDAPFeature delete <host> <port> <bind-dn> <password> <authenticationMethod> <dn> <failIfMissing>");
		FeatureUtil.logMsg("  failIfMissing can be one of 'yes' or 'no'");

		FeatureUtil
				.logMsg("LDAPFeature modify <host> <port> <bind-dn> <password> <authenticationMethod> <ldifLines> <ldifFilename>");
		FeatureUtil
				.logMsg("  either ldifLines or ldifFilename must be specified. ldifLines can contain pipe-separated (|) values");

		FeatureUtil
				.logMsg("LDAPFeature modifydn <host> <port> <bind-dn> <password> <authenticationMethod> <dn> <newRdn> <newSuperiorDn> <deleteOldRdn>");

		FeatureUtil
				.logMsg("LDAPFeature modifyattribute <host> <port> <bind-dn> <password> <authenticationMethod> <dn> <attribute> <value> <method>");
		FeatureUtil
				.logMsg("  method can be one of 'add', 'delete', 'replace', 'increment'");

		FeatureUtil
				.logMsg("LDAPFeature checkattribute <host> <port> <bind-dn> <password> <authenticationMethod> <dn> <attribute> <value> <failIfNotMatching>");
	}
}
