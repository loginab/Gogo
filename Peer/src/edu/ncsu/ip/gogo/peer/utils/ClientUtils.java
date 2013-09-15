package edu.ncsu.ip.gogo.peer.utils;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class ClientUtils {
	
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
			System.out.println("ClientUtils.getLocalIpAddress() - SocketException with message: " + e.getMessage());
			e.printStackTrace();
		}
    	
    	return ip;
    }
    
    public static String getOS() {
    	return System.getProperty("os.name");
    }

}
