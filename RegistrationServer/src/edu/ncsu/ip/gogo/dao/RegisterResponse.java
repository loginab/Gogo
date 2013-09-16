package edu.ncsu.ip.gogo.dao;

public class RegisterResponse extends MessageResponse {
	

	private static final long serialVersionUID = 1L;
	
	private final int cookie;

	public RegisterResponse(String ip, String os, String version,
			String status, String reason, int cookie) {
		super(ip, os, version, status, reason);
		this.cookie = cookie;
	}

	public int getCookie() {
		return cookie;
	}
	

}
