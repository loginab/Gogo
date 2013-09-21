package edu.ncsu.ip.gogo.peer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
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
                    if (iAddr.getAddress().isLinkLocalAddress() || !iAddr.getAddress().getHostAddress().matches("[0-9|.]+")) {
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

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }
    
    public static void printIaddr(InetAddress iAddr) {
        System.out.println("getHostAddress: " + iAddr.getHostAddress());
        System.out.println("isLinkLocalAddress: " + iAddr.isLinkLocalAddress());
        System.out.println("isMCGlobal: " + iAddr.isMCGlobal());
        System.out.println("isMCNodeLocal: " + iAddr.isMCNodeLocal());
        System.out.println("isMCLinkLocal: " + iAddr.isMCLinkLocal());
        System.out.println("isMac: " + !iAddr.getHostAddress().matches("[0-9|.]+"));
        
        
    }
    
}
