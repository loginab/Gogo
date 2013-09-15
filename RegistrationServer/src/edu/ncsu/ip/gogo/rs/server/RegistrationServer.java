package edu.ncsu.ip.gogo.rs.server;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.*;

import edu.ncsu.ip.gogo.peer.dao.Message;
import edu.ncsu.ip.gogo.peer.dao.Register;
import edu.ncsu.ip.gogo.rs.utils.Utils;


public class RegistrationServer {

	private int port;
	private List<Peer> peerList;
	private String ipAddress;
	private int cookieUnique ;
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
	        	Message msg = (Message)ois.readObject();
	        	Register reg;
	        	if(msg.getMethod().equals(Register.class.getSimpleName())) {
	        		System.out.println("Get IP "+ msg.getMethod() + " " + Register.class.getSimpleName());
	        		reg = (Register) msg;
	        	
	        		System.out.println("Get IP "+ reg.getIp());
	        		System.out.println("Get IP "+ reg.getRfcServerPort());
	        	
	        		// returns the index of the peer in the linklist if exist, else returns -1
	        	
	        		val = checkIfExist(peerList,reg.getIp(),reg.getRfcServerPort());
	        		if (val == -1) {
	        			// create the object and add the object to the list
	        			setCookieUnique(cookieUnique++);
	        			
	        			Peer obj = new Peer(reg.getIp(),getCookieUnique(),true,7200,reg.getRfcServerPort(),1,"Date");
	        			peerList.add(obj);
	        			System.out.println("Peer data added");
	        			//return obj.getCookie();
	        		} else {
	        			Peer obj = peerList.get(val);
	        			
	        				        			
	        			/* If the object is already there in the peer list change the TTL value
	        			 * check  the number of active peer in last 30 days
	        			 * make the object active
		        		*/        		
	        			keepAlive(obj);
	        			obj.setFlag(true);
	        			obj.setNumber_active_peer(obj.getNumber_active_peer() + 1);
	        			System.out.println("Peer data modified");
	        		}
		        
	        		
	        	}
	        
	        //scanClientScoketRequest(clientSocket);
	        } catch (IOException e) {
	            System.err.println("Accept failed." + e.getMessage());
	            System.exit(1);
	        } catch (Exception e){
	        	System.err.println("Exception"+e);
	        }
	        
	        
	        
	}
	/*
	 * parse the client socket
	 * 
	 * 
	 */
	public void scanClientScoketRequest(Socket clientSocket) {
		
		//TODO set values of the Peer Object hostname, port etc
		
	}
	
	
	/*Check if the given peer exists in the list or not
	 * return -1 if element not in the list
	 * else return the index of the object in the list 
	*/
	public int checkIfExist(List<Peer> prLst,String ipAdd,int prt){
     	int flag = -1 ;
     	ListIterator<Peer> listIterator =  prLst.listIterator();
     	
     		while (listIterator.hasNext()){
     			Peer item = listIterator.next();
	     		if ((item.getHostname()).equals(ipAdd) && (item.getPort() == prt))
     				return prLst.indexOf(item);
     		}
		
		return -1;
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
	 * 
	*/
	public List<Peer> pquery(List<Peer> prLst) {
		
		ListIterator<Peer> listIterator =  prLst.listIterator();
     	ArrayList <Peer> list  = new ArrayList<Peer>();
 		while (listIterator.hasNext()){
 			Peer item = listIterator.next();
     		if ((item.getFlag() == true) ) {
     			list.add(item);
     		}
 				
 		}
	
		return list ;
	}
	
	public void keepAlive(Peer p){
		
		p.setTTL(7200);
	}

	
	

}
