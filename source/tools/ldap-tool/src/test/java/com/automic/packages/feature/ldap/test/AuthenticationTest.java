package com.automic.packages.feature.ldap.test;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.automic.packages.feature.globalcodes.ErrorCodes;
import com.automic.packages.feature.ldap.LDAPFeature;
import com.unboundid.ldap.sdk.LDAPException;

@RunWith(value = Parameterized.class)
public class AuthenticationTest {

	private String host;

	private String port;

	private String username;

	private String password;

	private String authenticationType;

	@Parameters
	public static Collection<String[]> data() {
	    
	    
		List<String[]> res = new LinkedList<String[]>();
		// -host
		// -port
		// -username
		// -password
		// authenticationType

		// active Directory (Microsoft)
		res.add(new String[] { "192.168.44.20", "389", "arademo\\devuser",
				"devuser", "simple" });

		// Oracle Directory Server
		res.add(new String[] { "192.168.44.38", "3060", "cn=orcladmin",
				"ventum2011", "simple" });

		// IBM Tivoli Directory Server 6.3
		res.add(new String[] { "192.168.44.38", "399", "cn=orcladmin",
				"ventum2011", "simple" });

		// openLDAP
		res.add(new String[] { "192.168.44.38", "389", "cn=orcladmin",
				"ventum2011", "simple" });

		return res;
	}

	public AuthenticationTest(String host, String port, String username,
			String password, String authenticationType) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.authenticationType = authenticationType;
	}

	@Ignore
	@Test
	public void authTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "authenticate", host, port, username,
				password, authenticationType };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(0, ret);
	}

	@Ignore
	@Test(expected=LDAPException.class)
	public void wrongPWTest() throws Exception {
		LDAPFeature test = new LDAPFeature();
		String[] args = new String[] { "search", host, port, username,
				"wrongPW", authenticationType, "dc=example,dc=com", "scope",
				"5", "7", "both", "(description=*)", "", "", "", "1" };
		assertEquals(ErrorCodes.OK, test.checkParams(args));
		int ret = test.run(args);
		assertEquals(ErrorCodes.EXCEPTION, ret);
	}

}
