package edu.ncsu.ip.gogo.peer.dao;

public class Message {

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
