package edu.ncsu.ip.gogo.dao;

public class PeerInfo {
	
	private	final String hostname;
	private final int port;
	
	public PeerInfo(String hostname, int port) {
		super();
		this.hostname = hostname;
		this.port = port;
	}
	
	public String getHostname() {
		return hostname;
	}
	public int getPort() {
		return port;
	}
	


}
