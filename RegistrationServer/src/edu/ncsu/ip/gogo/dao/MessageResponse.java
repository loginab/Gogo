package edu.ncsu.ip.gogo.dao;

import java.io.Serializable;

public class MessageResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final String ip;
	private final String os;
	private final String version;
	private final String status;		// OK, ERROR
	private final String reason;
	
	public MessageResponse(String ip, String os, String version, String status,
			String reason) {
		super();
		this.ip = ip;
		this.os = os;
		this.version = version;
		this.status = status;
		this.reason = reason;
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

	public String getStatus() {
		return status;
	}

	public String getReason() {
		return reason;
	}		 

}
