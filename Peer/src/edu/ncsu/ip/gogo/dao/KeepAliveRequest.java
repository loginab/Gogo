package edu.ncsu.ip.gogo.dao;

public class KeepAliveRequest extends MessageRequest {
    
    private static final long serialVersionUID = 1L;
    
    private final static String METHOD_NAME = "KeepAlive";
    
    public KeepAliveRequest(String ip, String os, String version, int cookie) {
        super(METHOD_NAME, ip, os, version, cookie);
    }

}
