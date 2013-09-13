package edu.ncsu.ip.gogo.peer.dao;

public class Register extends Message {
	
	private final int rfcServerPort;

	public Register(String ip, String os, String version, int rfcServerPort) {
		super(Register.class.getSimpleName(), ip, os, version);
		this.rfcServerPort = rfcServerPort;
	}
	
	public int getRfcServerPort() {
		return rfcServerPort;
	}
	
}
