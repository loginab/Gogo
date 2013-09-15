package edu.ncsu.ip.gogo.rs.server;
import java.util.*;

public class Peer {

	private	String hostname ;
	private int cookie;
	private boolean flag;
	private int TTL;
	private int port;
	private int number_active_peer;
	private String date;
	
	
	public Peer(String hostname, int cookie, boolean flag, int tTL, int port,
			int number_active_peer, String date) {
		super();
		this.hostname = hostname;
		this.cookie = cookie;
		this.flag = flag;
		TTL = tTL;
		this.port = port;
		this.number_active_peer = number_active_peer;
		this.date = date;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getCookie() {
		return cookie;
	}
	public void setCookie(int cookie) {
		this.cookie = cookie;
	}
	public boolean getFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public int getTTL() {
		return TTL;
	}
	public void setTTL(int tTL) {
		TTL = tTL;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getNumber_active_peer() {
		return number_active_peer;
	}
	public void setNumber_active_peer(int number_active_peer) {
		this.number_active_peer = number_active_peer;
	}
	/*
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
*/
}