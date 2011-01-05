package jsc_controller;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class HTTPAuth extends Authenticator{
	private String username;
	private String password;
	
	public HTTPAuth (String username, String password) {
		this.username = username; this.password = password;
	}
	
	protected PasswordAuthentication getPasswordAuthentication () {
		return new PasswordAuthentication(username, password.toCharArray());
	}
}
