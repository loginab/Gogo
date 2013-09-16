package edu.ncsu.ip.gogo.dao;

import java.io.Serializable;

public class Message implements Serializable {


	private static final long serialVersionUID = 1L;
	
	private final String method;
	private final String ip;
	private final String os;
	private final String version;
	
	
	public Message(String method, String ip, String os, String version) {
		this.method = method;
		this.ip = ip;
		this.os = os;
		this.version = version;
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
	
	
	
}
