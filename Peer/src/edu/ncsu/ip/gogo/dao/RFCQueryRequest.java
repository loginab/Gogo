package edu.ncsu.ip.gogo.dao;

public class RFCQueryRequest extends MessageRequest {
    
    private static final long serialVersionUID = 1L;
    
    private final static String METHOD_NAME = "RFCQuery";
    
    public RFCQueryRequest(String ip, String os, String version, int cookie) {
        super(METHOD_NAME, ip, os, version, cookie);
    }
}
