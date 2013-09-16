package edu.ncsu.ip.gogo.dao;

public class Register extends Message {
	
	private static final long serialVersionUID = 1L;
	private final int rfcServerPort;

	public Register(String ip, String os, String version, int rfcServerPort) {
		super(Register.class.getSimpleName(), ip, os, version);
		this.rfcServerPort = rfcServerPort;
	}
	
	public int getRfcServerPort() {
		return rfcServerPort;
	}
	
}
