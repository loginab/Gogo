package edu.ncsu.ip.gogo.dao;

public class RegisterRequest extends MessageRequest {
    
    private static final long serialVersionUID = 1L;
    
    private final int rfcServerPort;
    private final static String METHOD_NAME = "Register";

    public RegisterRequest(String ip, String os, String version, int rfcServerPort) {
        super(METHOD_NAME, ip, os, version, -1);
        this.rfcServerPort = rfcServerPort;
    }
    
    public int getRfcServerPort() {
        return rfcServerPort;
    }
    
}
