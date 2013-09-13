package edu.ncsu.ip.gogo.peer.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import edu.ncsu.ip.gogo.peer.dao.Message;
import edu.ncsu.ip.gogo.peer.dao.Register;
import edu.ncsu.ip.gogo.peer.utils.ClientUtils;

public class RFCClient implements Runnable {
	
	private final String rsHost;
	private final int rsPort;
	private final int rfcServerPort;
	private final String ip;
	private final String os;
	private final String version;
	
	private int cookie;
	

    public RFCClient(String rsHost, int rsPort, int rfcServerPort) {
		super();
		this.rsHost = rsHost;
		this.rsPort = rsPort;
		this.rfcServerPort = rfcServerPort;
		this.ip = ClientUtils.getLocalIpAddress();
		this.os = ClientUtils.getOS();
		this.version = "P2P-DI-GOGO/1.0";
	}

	@Override
    public void run() {
        int userOpt = -1;
        Scanner in = new Scanner(System.in);
        do {
            System.out.println("Enter option number for RFCClient operation from below: ");
            System.out.println("1. Register");
            System.out.println("2. Leave");
            System.out.println("3. PQuery");
            System.out.println("4. KeepAlive");
            System.out.println("5. RFCQuery");
            System.out.println("6. GetRFC");
            System.out.println("7. Exit");
            
            
            userOpt = in.nextInt();
            
            switch(userOpt) {
                case 1 : register();
                         break;
                case 2 : //leave();
                          break;
                case 3 : //pQuery();
                         break;
                case 4 : //keepAlive();
                         break;
                case 5 : //rfcQuery();
                         break;
                case 6 : //getRfc();
                         break;
            }
            
        } while (userOpt != 7);
        
        in.close();
    }
    
    private void register() {
    	
    	System.out.println("Inside register(): " + rsHost + " , " + rsPort + " , " + rfcServerPort + " , " + os);
    	Register reg = new Register(ip, os, version, rfcServerPort);
    	rsClient(reg);
    }
    
    // Should return some response from RS
    private void rsClient(Message m) {
    	Socket socket;
    	try {
    		socket = new Socket(rsHost, rsPort);
    		OutputStream os = socket.getOutputStream();   
       		ObjectOutputStream oos = new ObjectOutputStream(os);   
    		oos.writeObject(m);
    		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
}
