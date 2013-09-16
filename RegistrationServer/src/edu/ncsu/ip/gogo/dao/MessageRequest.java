package edu.ncsu.ip.gogo.dao;

import java.io.Serializable;

public class MessageRequest implements Serializable {


	private static final long serialVersionUID = 1L;
	
	private final String method;
	private final String ip;
	private final String os;
	private final String version;
	private final int cookie;
	
	
	public MessageRequest(String method, String ip, String os, String version, int cookie) {
		this.method = method;
		this.ip = ip;
		this.os = os;
		this.version = version;
		this.cookie = cookie;
	}


	public String getMethod() {
		return method;
	}


	public String getIp() {
		return ip;
	}


	public String getOs() {
		return os;
	}


	public String getVersion() {
		return version;
	}


	public int getCookie() {
		return cookie;
	}
}
