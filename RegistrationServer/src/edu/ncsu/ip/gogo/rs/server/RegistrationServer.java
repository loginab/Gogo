package edu.ncsu.ip.gogo.rs.server;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.*;

import edu.ncsu.ip.gogo.dao.KeepAliveRequest;
import edu.ncsu.ip.gogo.dao.LeaveRequest;
import edu.ncsu.ip.gogo.dao.MessageRequest;
import edu.ncsu.ip.gogo.dao.PQueryRequest;
import edu.ncsu.ip.gogo.dao.PeerInfo;
import edu.ncsu.ip.gogo.dao.RegisterRequest;
import edu.ncsu.ip.gogo.dao.RegisterResponse;
import edu.ncsu.ip.gogo.rs.utils.Utils;


public class RegistrationServer {

	private int port;
	private List<Peer> peerList;
	private String ipAddress;
	private int cookieUnique ;
	private final static String myIp;
	private final static String myOs;
	
	static {
		myIp = Utils.getLocalIpAddress();
		myOs = Utils.getOS();
	}
	
	public RegistrationServer(int prt, int cookie) {
		port = prt;
		cookieUnique = cookie;
	}
	
	public int getCookieUnique() {
		return cookieUnique;
	}

	public void setCookieUnique(int cookieUnique) {
		this.cookieUnique = cookieUnique;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void init()  {
		 peerList = new LinkedList<Peer>();
		 int val = -1;
		 
		 Thread t = new Thread() {
			
			 public void run() {
			        while(true) {
			            try {
			                Thread.sleep(10*1000);
			                
			                for (Peer peer : peerList) { 	
			         	 		if (peer.getFlag() && peer.getCookie() > 60)
			         	 			peer.setCookie(peer.getCookie()-60);
			         	 		else {
			         	 			peer.setCookie(0);
			         	 			peer.setFlag(false);
			         	 		}
			                
			         		}
			            } catch (InterruptedException ie) {
			            }
			        }
			    }
			 
			 
		 };
		 
		 t.start();
		 while (true) {
			 ServerSocket serverSocket = null;
			 try {
				 serverSocket = new ServerSocket(port);
			 } catch (IOException e) {
				 System.err.println("Could not listen on port:"+ port);
				 System.exit(1);
			 }
	 
			 	Socket clientSocket = null;
			 	try {
	        	
			 		clientSocket = serverSocket.accept();
			 		InputStream is = clientSocket.getInputStream();   
			 		ObjectInputStream ois = new ObjectInputStream(is);   
			 		MessageRequest msg = (MessageRequest)ois.readObject();
	        	   	if(msg instanceof RegisterRequest) {
	        		
	        	   		RegisterRequest reg;
	        	   		reg = (RegisterRequest) msg;
	        	   		System.out.println(reg.getRfcServerPort());
	        	   		// returns the index of the peer in the linklist if exist, else returns -1
	        	   		val = checkIfExist(peerList,reg.getIp(),reg.getRfcServerPort());
	        		
	        	   		if (val == -1) {
	        	   			
	        	   			// create the object and add the object to the list
	        	   			cookieUnique++;
	        			
	        	   			Peer obj = new Peer(reg.getIp(),getCookieUnique(),true,7200,reg.getRfcServerPort(),1,"Date");
	        	   			peerList.add(obj);

	        	   			System.out.println("Peer data added");
	        			
	        	   			RegisterResponse regResponse = new RegisterResponse(myIp, myOs, reg.getVersion(),
	        	   					"OK", null, obj.getCookie());
	        			
	        	   			OutputStream os = clientSocket.getOutputStream();
	        	   			ObjectOutputStream oos = new ObjectOutputStream(os);   
	            		
	        	   			oos.writeObject(regResponse);
	        			
	        	   		} else {
	
  	        	   			/* If the object is already there in the peer list change the TTL value
	        	   			 * check  the number of active peer in last 30 days
	        	   			 * make the object active
	        	   			 */
	        	   			Peer obj = peerList.get(val);
	        	   			keepAlive(obj);
	        	   			obj.setFlag(true);
	        	   			obj.setNumber_active_peer(obj.getNumber_active_peer() + 1);
	        	   			System.out.println("Peer data modified");
	        			
	        	   			RegisterResponse regResponse = new RegisterResponse(myIp, myOs, reg.getVersion(),
	        	   					"OK", null, obj.getCookie());
	        			
	        	   			OutputStream os = clientSocket.getOutputStream();
	        	   			ObjectOutputStream oos = new ObjectOutputStream(os);   
	            		
	        	   			oos.writeObject(regResponse);
	        	   		}
		        
	        		
	        	   		} else if (msg instanceof LeaveRequest ){
	        	   			
	        	   			LeaveRequest leaveObj ;
	        	   			leaveObj = (LeaveRequest)msg;
	        	   			Peer rm = checkCookie(peerList,leaveObj.getCookie());
	        	   			
	        	   			if (rm != null)
	        	   				leave(rm);
	        		
	        	   		} else if (msg instanceof PQueryRequest) {
	        	   			
	        	   			PQueryRequest pqueryObj ;
	        	   			pqueryObj = (PQueryRequest) msg;
	        	   			Peer rm = checkCookie(peerList,pqueryObj.getCookie());
	        	   			
	        	   			if (rm != null) {
	        	   				
	        	   				List<PeerInfo> peerInfo = pquery(peerList);
	        	   				OutputStream os = clientSocket.getOutputStream();
	        	   				ObjectOutputStream oos = new ObjectOutputStream(os);   
	        	   				oos.writeObject(peerInfo);
	        	   				
	        	   			} else {
	        	   				
	        	   				//TODO if the cookie does not exist 
	        	   				
	        	   			}
	        		
	        	   		} else if (msg instanceof KeepAliveRequest) {
	        	   			
	        	   			KeepAliveRequest keepAliveObj ;
	        	   			keepAliveObj = (KeepAliveRequest)msg;
	        			
	        	   			Peer rm = checkCookie(peerList,keepAliveObj.getCookie());
	        	   			if (rm !=null)
	        	   				keepAlive(rm);
	        	   			else {
	        	   				//TODO if object for the cookie is not there
	        	   			}
	        		
	        	   		} else {
	        	   				//TODO 
	        	   		}
	        
	       	 	} catch (IOException e) {
			 		System.err.println("Accept failed." + e.getMessage());
			 		e.printStackTrace();
			 		System.exit(1);
			 	} catch (Exception e){
			 		System.err.println("Exception"+e);
			 	}
	        
	        
	        
		 }
	}
	
	
	/*Check if the given peer has registered in the list or not 
	 * return -1 if element not in the list
	 * else return the index of the object in the list 
	*/
	public int checkIfExist(List<Peer> prLst,String ipAdd,int prt){
     	
     	ListIterator<Peer> listIterator =  prLst.listIterator();
     	
     		while (listIterator.hasNext()){
     			Peer item = listIterator.next();
	     		if ((item.getHostname()).equals(ipAdd) && (item.getPort() == prt))
     				return prLst.indexOf(item);
     		}
		
		return -1;
     	}
	/*
	 *  check for the cookie value in the peer list
	 *  returns the peer object if present else null
	 * 
	 */
	public Peer checkCookie (List<Peer> prLst, int ckie) {
		for (Peer peer : prLst) { 	
 	 		if ((peer.getCookie() == ckie) ) {
 	 			return peer ;
     		}
 		}
		return null;
	}
	
	/*
	 * Add the peer to the linklist
	*/
	public void register(Peer p){
		
			peerList.add(p);
	}
	/*
	 * Remove the peer from the linkList
	*/
	
	public void leave(Peer p){
			peerList.remove(p);
	}
	/*
	 * send the list of active peers
	 * done by iterating all the registered list of peers
	*/
	public List<PeerInfo> pquery(List<Peer> prLst) {
		
     	ArrayList <PeerInfo> list  = new ArrayList<PeerInfo>();
     	for (Peer peer : prLst) { 	
 	 		if ((peer.getFlag() == true) ) {
 	 			PeerInfo peerInfo = new PeerInfo(peer.getHostname(), peer.getPort());
     			list.add(peerInfo);
     		}
 		}
		return list ;
	}
	
	/*
	 * Increase the value of TTL to 7200
	 * 
	*/
	public void keepAlive(Peer p){
		
		p.setTTL(7200);
	}

	
	

}
