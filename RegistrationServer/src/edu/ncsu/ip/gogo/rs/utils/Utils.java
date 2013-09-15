package edu.ncsu.ip.gogo.rs.utils;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class Utils {
	public static String getLocalIpAddress() {
    	String ip = null;
    	Enumeration<NetworkInterface> netInfs = null;
    	try {
    		netInfs = NetworkInterface.getNetworkInterfaces();

    	
	    	while(netInfs.hasMoreElements()) {
	    		NetworkInterface ni = netInfs.nextElement();
	    		if (ni.isLoopback()) { 
	    			continue;
	    		}
	    		List<InterfaceAddress> iAddrs = ni.getInterfaceAddresses();
	    		
	    		for (InterfaceAddress iAddr : iAddrs) {
	    			if (iAddr.getAddress().isLinkLocalAddress()) {
	    				continue;
	    			}
	    			ip = iAddr.getAddress().getHostAddress();
	    			break;
	    		}
	    	}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return ip;
    }
    
}
