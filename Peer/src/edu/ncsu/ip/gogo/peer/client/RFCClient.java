package edu.ncsu.ip.gogo.peer.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import edu.ncsu.ip.gogo.dao.KeepAliveRequest;
import edu.ncsu.ip.gogo.dao.LeaveRequest;
import edu.ncsu.ip.gogo.dao.MessageRequest;
import edu.ncsu.ip.gogo.dao.MessageResponse;
import edu.ncsu.ip.gogo.dao.RegisterRequest;
import edu.ncsu.ip.gogo.dao.RegisterResponse;
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
                         
                case 2 : leave();
                         break;
                         
                case 3 : pQuery();
                         break;
                         
                case 4 : keepAlive();
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
    	RegisterRequest reg = new RegisterRequest(ip, os, version, rfcServerPort);
    	MessageResponse rsp = (MessageResponse) sendToRS(reg);
    	
    	if (rsp != null && rsp.getStatus().equals("OK")) {
    		RegisterResponse response = (RegisterResponse) rsp;
    		setCookie(response.getCookie());
    		System.out.println("RFCClient.register() - Register request successful. RS assigned cookie: " + getCookie());
    	} else {
    		if (rsp == null) {
    			System.out.println("RFCClient.register() - MessageResponse is null from RS");
    		} else {
    			System.out.println("RFCClient.register() - MessageResponse failed with reason: " + rsp.getReason());
    		}
    	}
    }
    
    private void leave() {
    	LeaveRequest leave = new LeaveRequest(ip, os, version, cookie);
    	MessageResponse rsp = (MessageResponse) sendToRS(leave);
    	
    	if (rsp != null && rsp.getStatus().equals("OK")) {
    		System.out.println("RFCClient.leave() - Leave request successful.");
    	} else {
    		if (rsp == null) {
    			System.out.println("RFCClient.leave() - MessageResponse is null from RS");
    		} else {
    			System.out.println("RFCClient.leave() - MessageResponse failed with reason: " + rsp.getReason());
    		}
    	}
    }
    
    private void keepAlive() {
    	KeepAliveRequest keepAlive = new KeepAliveRequest(ip, os, version, cookie);
    	MessageResponse rsp = (MessageResponse) sendToRS(keepAlive);
    	
    	if (rsp != null && rsp.getStatus().equals("OK")) {
    		System.out.println("RFCClient.keepAlive() - Keep Alive request successful.");
    	} else {
    		if (rsp == null) {
    			System.out.println("RFCClient.keepAlive() - MessageResponse is null from RS");
    		} else {
    			System.out.println("RFCClient.keepAlive() - MessageResponse failed with reason: " + rsp.getReason());
    		}
    	}
    }
    
    private void pQuery() {
    	KeepAliveRequest keepAlive = new KeepAliveRequest(ip, os, version, cookie);
    	sendToRS(keepAlive);
    }
    
    private MessageResponse sendToRS(MessageRequest req) {
    	Socket socket;
    	MessageResponse rsp = null;
    	System.out.println("Opening TCP socket to " + rsHost + " and " + rsPort);
    	try {
    		socket = new Socket(rsHost, rsPort);
    		OutputStream os = socket.getOutputStream();
       		ObjectOutputStream oos = new ObjectOutputStream(os);   
       		InputStream is = socket.getInputStream();
    		ObjectInputStream ois = new ObjectInputStream(is);
    		
    		oos.writeObject(req);
    		
    		rsp = (MessageResponse) ois.readObject();
    		
    		oos.close();
    		os.close();
    		is.close();
    		ois.close();
    		socket.close();
		} catch (UnknownHostException e) {
			System.out.println("RFCClient.sendToRS() - UnknownHostException with message: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("RFCClient.sendToRS() - IOException with message: " + e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("RFCClient.sendToRS() - ClassNotFoundException with message: " + e.getMessage());
			e.printStackTrace();
		}
    	
    	return rsp;
    }

	public int getCookie() {
		return cookie;
	}

	public void setCookie(int cookie) {
		this.cookie = cookie;
	}
    
}
