/* 
 * (c) 2012 Michael Schwartz e.U. 
 * All Rights Reserved.
 * 
 * This program is not a free software. The owner of the copyright
 * can license the software for you. You may not use this file except in
 * compliance with the License. In case of questions please
 * do not hesitate to contact us at idx@mschwartz.eu.
 * 
 * Filename: LDAPTest.java
 * Created: 11.05.2012
 * 
 * Author: $LastChangedBy$ 
 * Date: $LastChangedDate$ 
 * Revision: $LastChangedRevision$ 
 */
package com.automic.packages.feature.ldap.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.automic.packages.feature.globalcodes.ErrorCodes;
import com.automic.packages.feature.ldap.LDAPFeature;
import com.automic.packages.feature.utils.FileUtil;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;

/**
 * The Class LDAPTest.
 */
@RunWith(value = Parameterized.class)
public class LDAPTest {

	/**
	 * The host.
	 */
	private String host;

	/**
	 * The port.
	 */
	private String port;

	/**
	 * The username.
	 */
	private String username;

	/**
	 * The password.
	 */
	private String password;

	/**
	 * The authentication type.
	 */
	private String authenticationType;

	/**
	 * The to create.
	 */
	private String toCreate;

	/**
	 * The cn.
	 */
	private String cn;

	/**
	 * The path.
	 */
	private String path;

	private String toModify;

	/**
	 * Before creates an entry to test with.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void before() throws Exception {
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "add", host, port, username,
					password, authenticationType,
					"dn: " + cn + "," + path + "|" + toCreate, "", "no" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
	}

	/**
	 * After removes the test-entry (if it is still existing).
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void after() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "delete", host, port, username,
				password, authenticationType, cn + "," + path, "no" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		// we do not care if the object is still existing or not.
		/* int ret = */test.run(args);
		// assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Returns a collection of access-variables to be used by each test.
	 * 
	 * @return the collection
	 */
	@Parameters
	public static Collection<String[]> data() {
		List<String[]> res = new LinkedList<String[]>();
		// -host
		// -port
		// -username
		// -password
		// authenticationType
		// toCreate
		// cn
		// path
		// toModify

		// active Directory (Microsoft)
		res.add(new String[] { "192.168.44.20", "389", "arademo\\orcladmin",
				"ventum2011", "simple", "objectClass: top|objectClass: user",
				"cn=mikeToModify", "O=example,DC=arademo,DC=local", "name" });

		// Oracle IDM 11g LDAP
		res.add(new String[] { "192.168.44.38", "3060", "cn=orcladmin",
				"ventum2011", "simple",
				"objectClass: top|objectclass: account", "uid=mikeToModify",
				"dc=example,dc=com", "uid" });

		// ibm
		// see http://192.168.44.38:12100/IDSWebApp/
		res.add(new String[] { "192.168.44.38", "399", "cn=orcladmin",
				"ventum2011", "simple",
				"objectClass: top|objectClass: account", "uid=mikeToModify",
				"dc=example,dc=com", "uid" });

		// openLDAP
		res.add(new String[] { "192.168.44.38", "389", "cn=orcladmin",
				"ventum2011", "simple",
				"objectClass: top|objectClass: inetOrgPerson|sn: mike",
				"cn=mikeToModify", "ou=people,dc=example,dc=com", "cn" });
		return res;
	}

	/**
	 * Instantiates a new lDAP test.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param authenticationType
	 *            the authentication type
	 * @param toCreate
	 *            the to create
	 * @param cn
	 *            the cn
	 * @param path
	 *            the path
	 */
	public LDAPTest(String host, String port, String username, String password,
			String authenticationType, String toCreate, String cn, String path,
			String toModify) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.authenticationType = authenticationType;
		this.toCreate = toCreate;
		this.cn = cn;
		this.path = path;
		this.toModify = toModify;
	}

	// add/remove is not necessary since we are doing the same in before and
	// after
	// @Test
	// public void addRemoveTest() {
	// // see http://192.168.44.38:12100/IDSWebApp/
	// {
	// LDAPFeature test = new LDAPFeature();
	// String[] args = new String[] {
	// "add",
	// host,
	// port,
	// username,
	// password,
	// authenticationType,
	// "dn: cn=mike,dc=example,dc=com|objectClass: top|objectClass: ibm-realm|cn: mike",
	// "", "yes" };
	// assertEquals(ErrorCodes.OK, test.checkParams(args));
	// int ret = test.run(args);
	// assertEquals(0, ret);
	// }
	// {
	// LDAPFeature test = new LDAPFeature();
	// String[] args = new String[] { "delete", host, port, username,
	// password, authenticationType, "cn=mike,dc=example,dc=com",
	// "yes" };
	// assertEquals(ErrorCodes.OK, test.checkParams(args));
	// int ret = test.run(args);
	// assertEquals(0, ret);
	// }
	// }

	/**
	 * This tests the "fail flag" in the add-method set to "yes". We expect to
	 * get an error.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void addToExisting1Test() throws Exception {
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "add", host, port, username,
					password, authenticationType,
					"dn: " + cn + "," + path + "|" + toCreate, "", "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.SEVERE, ret);
		}
	}

	/**
	 * This tests the "fail flag" in the add-method set to "no". We expect a
	 * normal operation.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore    
	@Test
	public void addToExisting2Test() throws Exception {
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "add", host, port, username,
					password, authenticationType,
					"dn: " + cn + "," + path + "|" + toCreate, "", "No" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
	}

	/**
	 * This test adds a new entry by using an ldif-file.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void addWithFileTest() throws Exception {
		// for safety, try to remove the entry which we want to add
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Adding," + path, "no" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
		{
			StringBuffer content = new StringBuffer();
			content.append("dn: " + cn + "Adding," + path + "\r\n");
			for (String line : toCreate.split("\\|"))
				content.append(line + "\r\n");
			FileUtil.writeFile("testdn.txt", content.toString());

			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "add", host, port, username,
					password, authenticationType, "", "testdn.txt", "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);

			File file = new File("testdn.txt");
			file.delete();
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Adding," + path, "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
	}

	/**
	 * Tests with both command line entry and file-based entry. File based entry
	 * should be used in such cases.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void addWithFileAndCommandLineTest() throws Exception {
		{
			StringBuffer content = new StringBuffer();
			content.append("dn: " + cn + "Adding1," + path + "\r\n");
			for (String line : toCreate.split("\\|"))
				content.append(line + "\r\n");
			FileUtil.writeFile("testdn.txt", content.toString());

			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "add", host, port, username,
					password, authenticationType,
					"dn: " + cn + "Adding2," + path + "|" + toCreate,
					"testdn.txt", "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);

			File file = new File("testdn.txt");
			file.delete();
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Adding1," + path, "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Adding2," + path, "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.SEVERE, ret);
		}
	}

	/**
	 * tries to add multiple entries at once while the Fail-flag is set to yes
	 * and at least one entry is already existing. We expect to have NO added
	 * entries and an error.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void addMultipleFailYesTest() throws Exception {
		{
			StringBuffer content = new StringBuffer();
			content.append("dn: " + cn + "Adding1," + path + "\r\n");
			for (String line : toCreate.split("\\|"))
				content.append(line + "\r\n");
			content.append("\r\n");
			content.append("dn: " + cn + "," + path + "\r\n");
			for (String line : toCreate.split("\\|"))
				content.append(line + "\r\n");
			content.append("\r\n");
			content.append("dn: " + cn + "Adding3," + path + "\r\n");
			for (String line : toCreate.split("\\|"))
				content.append(line + "\r\n");
			FileUtil.writeFile("testdn.txt", content.toString());

			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "add", host, port, username,
					password, authenticationType, "", "testdn.txt", "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.SEVERE, ret);

			File file = new File("testdn.txt");
			file.delete();
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Adding1," + path, "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.SEVERE, ret);
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Adding3," + path, "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.SEVERE, ret);
		}
	}

	/**
	 * tries to add multiple entries at once while the Fail-flag is set to no
	 * and at least one entry is already existing. We expect to have all added
	 * entries.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void addMultipleFailNoTest() throws Exception {
		{
			StringBuffer content = new StringBuffer();
			content.append("dn: " + cn + "Adding1," + path + "\r\n");
			for (String line : toCreate.split("\\|"))
				content.append(line + "\r\n");
			content.append("\r\n");
			content.append("dn: " + cn + "," + path + "\r\n");
			for (String line : toCreate.split("\\|"))
				content.append(line + "\r\n");
			content.append("\r\n");
			content.append("dn: " + cn + "Adding3," + path + "\r\n");
			for (String line : toCreate.split("\\|"))
				content.append(line + "\r\n");
			FileUtil.writeFile("testdn.txt", content.toString());

			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "add", host, port, username,
					password, authenticationType, "", "testdn.txt", "no" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);

			File file = new File("testdn.txt");
			file.delete();
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Adding1," + path, "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Adding3," + path, "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
	}

	/**
	 * This test does neither handle an ldif file nor a ldif command line
	 * argument and therefore this test expects a failure.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void addOmitParamsTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "add", host, port, username, password,
				authenticationType, "", "", "yes" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.SEVERE, ret);
	}

	/**
	 * Delete a non-existent entry and set to fail-flag to yes. We expect an
	 * error.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void deleteFailYesTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "delete", host, port, username,
				password, authenticationType,
				"dn: " + cn + "NonExistent," + path + "|" + toCreate, "yes" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.SEVERE, ret);
	}

	/**
	 * Delete a non-existent entry and set to fail-flag to no. We expect normal
	 * operation.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void deleteFailNoTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "delete", host, port, username,
				password, authenticationType,
				"dn: " + cn + "NonExistent," + path + "|" + toCreate, "no" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Modifies an Entry via Parameter. @see <code>modifyViaFileTest()</code>
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void modifyViaParameterTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] {
				"modify",
				host,
				port,
				username,
				password,
				authenticationType,
				"dn: "
						+ cn
						+ ","
						+ path
						+ "|changetype: modify|add: description|description: This is the new description.",
				"" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Performs a modification request with a non-existing dn. An Exception is
	 * expected.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test(expected = LDAPException.class)
	public void modifyNonExistentDNTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] {
				"modify",
				host,
				port,
				username,
				password,
				authenticationType,
				"dn: "
						+ cn
						+ "NonExistent,"
						+ path
						+ "|changetype: modify|add: description|description: This is the new description.",
				"" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Performs a modification request with an ldif-file.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void modifyViaFileTest() throws Exception {
		StringBuffer content = new StringBuffer();
		content.append("dn: " + cn + "," + path + "\r\n");
		for (String line : "changetype: modify|add: description|description: This is the new description."
				.split("\\|"))
			content.append(line + "\r\n");
		FileUtil.writeFile("testdn.txt", content.toString());

		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "modify", host, port, username,
				password, authenticationType, "", "testdn.txt" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);

		File file = new File("testdn.txt");
		file.delete();
	}

	/**
	 * Do not give file or edit parameter. We expect an error in such cases
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void modifyOmitTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "modify", host, port, username,
				password, authenticationType, "", "" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.SEVERE, ret);
	}

	/**
	 * Performs a modify-dn test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void modifyDNTest() throws Exception {
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "modifydn", host, port, username,
					password, authenticationType, cn + "," + path,
					cn + "Modified", path, "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Modified," + path,
					"yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
	}

	/**
	 * Performs a modify-dn test with a non-existing dn. An exception is
	 * expected
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test(expected = LDAPException.class)
	public void modifyDNNonExistingTest() throws Exception {

		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "modifydn", host, port, username,
				password, authenticationType, cn + "NonExisting," + path,
				cn + "Modified", path, "yes" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.SEVERE, ret);
	}

	/**
	 * performs a Modify dn test with an invalid target.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test(expected = LDAPException.class)
	public void modifyDNInvalidTargetTest() throws Exception {

		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "modifydn", host, port, username,
				password, authenticationType, cn + "," + path, cn + "Modified",
				path + "Invalid", "yes" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.SEVERE, ret);
	}

	/**
	 * performs a Modify dn test with the parameter <delete old> set to "no".
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void modifyDNDeleteOldNoTest() throws Exception {
		// for security try to remove an already-existing destiny-object
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Modified," + path, "no" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "modifydn", host, port, username,
					password, authenticationType, cn + "," + path,
					cn + "Modified", "", "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Modified," + path,
					"yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "," + path, "no" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
	}

	/**
	 * performs a Modify dn test with the parameter <delete old> set to "no".
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void modifyDNDeleteOldYesTest() throws Exception {
		// for security try to remove an already-existing destiny-object
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Modified," + path, "no" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "modifydn", host, port, username,
					password, authenticationType, cn + "," + path,
					cn + "Modified", path + "", "yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "Modified," + path,
					"yes" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "delete", host, port, username,
					password, authenticationType, cn + "," + path, "no" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
	}

	/**
	 * Modify attribute test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void modifyAttributeTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "modifyattribute", host, port, username,
				password, authenticationType, cn + "," + path, "uid",
				"tester@home.at", "add" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Modify attribute with a non existent dn.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test(expected = LDAPException.class)
	public void modifyAttributeNonExistentDNTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "modifyattribute", host, port, username,
				password, authenticationType, cn + "," + path + "NonExistent",
				"uid", "tester@home.at", "add" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.SEVERE, ret);
	}

	/**
	 * Modify attribute by deleting the attribute.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void modifyAttributeDeleteTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "modifyattribute", host, port, username,
				password, authenticationType, cn + "," + path, toModify, "",
				"delete" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Modify attribute with replacing the attribute.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void modifyAttributeReplaceTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "modifyattribute", host, port, username,
				password, authenticationType, cn + "," + path, "uid", "MyUser",
				"replace" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Modify attribute with replacing the attribute while the source attribute
	 * is missing.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test(expected = LDAPException.class)
	public void modifyAttributeReplaceMissingAttributeTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "modifyattribute", host, port, username,
				password, authenticationType, cn + "," + path,
				"missingAttribute", "MyUser", "replace" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.SEVERE, ret);
	}

	/**
	 * Modify attribute with parameter "increment".
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void modifyAttributeIncrementTest() throws Exception {
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "modifyattribute", host, port,
					username, password, authenticationType, cn + "," + path,
					toModify, "1", "add" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
		{
			LDAPFeature test = new LDAPFeature();
			String[] args = new String[] { "modifyattribute", host, port,
					username, password, authenticationType, cn + "," + path,
					"uid", "1", "increment" };
			assertEquals(ErrorCodes.OK, test.checkParams(args));
			int ret = test.run(args);
			assertEquals(ErrorCodes.OK, ret);
		}
	}

	/**
	 * Check attribute test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void checkAttributeTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "checkattribute", host, port, username,
				password, authenticationType, cn + "," + path, toModify,
				"mikeToModify", "yes" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Check attribute with an invalid cn.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void checkAttributeUnexpectedYesTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "checkattribute", host, port, username,
				password, authenticationType, cn + "," + path, toModify,
				"MikeToModifyIsItNot", "yes" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.ERROR, ret);
	}

	/**
	 * Check attribute unexpected no test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void checkAttributeUnexpectedNoTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "checkattribute", host, port, username,
				password, authenticationType, cn + "," + path, toModify,
				"MikeToModifyIsItNot", "no" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Searches for existent entries.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void search1Test() throws Exception {
		// see http://192.168.44.38:12100/IDSWebApp/
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "99", "7", "both",
				"(" + toModify + "=*)", "", "", "", "1" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));

		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Search2 test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void search2Test() throws Exception {
		// see http://192.168.44.38:12100/IDSWebApp/
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "5", "7", "both",
				"(" + toModify + "=MikeToModify)", "", "", "", "1" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Searches for non-existent entries.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void searchNonExistingEntriesTest() throws Exception {
		// see http://192.168.44.38:12100/IDSWebApp/
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "5", "7", "both",
				"(" + toModify + "=Notfound)", "", "", "", "0" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Search non existing scope test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test(expected = LDAPSearchException.class)
	public void searchNonExistingScopeTest() throws Exception {
		// see http://192.168.44.38:12100/IDSWebApp/
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path + "non", "scope", "5", "7",
				"both", "(description=Description)", "", "", "", "0" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Search below size limit test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void searchBelowSizeLimitTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "1", "7", "both",
				"(" + toModify + "=MikeToModify)", "", "", "", "1" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Tests the "Fail if Result Count Below" parameter for triggering an error.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void searchBelowSizeLimit2Test() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "5", "7", "both",
				"(" + toModify + "=MikeToModify)", "", "", "", "2" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.SEVERE, ret);
	}

	/**
	 * Search result format names test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void searchResultFormatNamesTest() throws Exception {
		// see http://192.168.44.38:12100/IDSWebApp/
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "10", "7",
				"names", "(" + toModify + "=*)", "*", "", "", "0" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Search result format values test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void searchResultFormatValuesTest() throws Exception {
		// see http://192.168.44.38:12100/IDSWebApp/
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "10", "7",
				"values", "(" + toModify + "=*)", "*", "", "", "0" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Search result format both test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void searchResultFormatBothTest() throws Exception {
		// see http://192.168.44.38:12100/IDSWebApp/
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "10", "7", "both",
				"(" + toModify + "=*)", "*", "", "", "0" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Search attributes none test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void searchAttributesNoneTest() throws Exception {
		// see http://192.168.44.38:12100/IDSWebApp/
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "10", "7", "both",
				"(" + toModify + "=*)", "1.1", "", "", "0" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Search attributes all operational test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void searchAttributesAllOperationalTest() throws Exception {
		// see http://192.168.44.38:12100/IDSWebApp/
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "10", "7", "both",
				"(" + toModify + "=*)", "+", "", "", "0" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Search attributes specific1 test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void searchAttributesSpecific1Test() throws Exception {
		// see http://192.168.44.38:12100/IDSWebApp/
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "10", "7",
				"values", "(" + toModify + "=*)", "-", "[description]-[uid]",
				"", "0" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * Search attributes specific2 test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void searchAttributesSpecific2Test() throws Exception {
		// see http://192.168.44.38:12100/IDSWebApp/
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "10", "7",
				"values", "(" + toModify + "=*)", "-", "[description[[]-[uid]",
				"", "0" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);
	}

	/**
	 * search for entries and creates an Outputfile.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Ignore
	@Test
	public void searchOutputfileTest() throws Exception {
		// see http://192.168.44.38:12100/IDSWebApp/
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				password, authenticationType, path, "scope", "5", "7", "both",
				"(" + toModify + "=*)", "", "", "test.txt", "1" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.OK, ret);

		File file = new File("test.txt");
		assertTrue(file.exists());
		assertTrue(file.length() > 100);
		file.delete();
	}

}
