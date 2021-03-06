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
                    if (iAddr.getAddress().isLinkLocalAddress() || !iAddr.getAddress().getHostAddress().matches("[0-9|.]+")) {
                        continue;
                    }
                    ip = iAddr.getAddress().getHostAddress();
                    break;
                }
            }
        } catch (SocketException e) {
            System.out.println("Utils.getLocalIpAddress() - SocketException with message: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ip;
    }
    
    
    public static String getOS() {
        return System.getProperty("os.name");
    }
}
