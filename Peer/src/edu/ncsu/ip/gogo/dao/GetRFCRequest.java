package edu.ncsu.ip.gogo.dao;

public class GetRFCRequest extends MessageRequest {
    
    private static final long serialVersionUID = 1L;
    
    private final String rfcNumber;
    private final static String METHOD_NAME = "GetRFC";
    
    public GetRFCRequest(String ip, String os, String version, int cookie, String rfcNumber) {
        super(METHOD_NAME, ip, os, version, -1);
        this.rfcNumber = rfcNumber;
    }

    public String getRfcNumber() {
        return rfcNumber;
    }

}
