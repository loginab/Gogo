package edu.ncsu.ip.gogo.rs.server;

import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
public class RegistrationServer {

	private int port;
	
	public RegistrationServer(int prt) {
		port = prt;
		
	}
	
	public void init()  {
		
		 ServerSocket serverSocket = null;
	        try {
	            serverSocket = new ServerSocket(port);
	        } catch (IOException e) {
	            System.err.println("Could not listen on port:"+ port);
	            System.exit(1);
	        }
	 
	        Socket clientSocket = null;
	        try {
	        	
	        	System.out.println("The ip address is "+Inet4Address.getLocalHost().getHostAddress());
	            clientSocket = serverSocket.accept();
	        } catch (IOException e) {
	            System.err.println("Accept failed.");
	            System.exit(1);
	        }
	}

}
