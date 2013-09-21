package edu.ncsu.ip.gogo.dao;

import java.util.Queue;

public class RFCQueryResponse extends MessageResponse {

    private static final long serialVersionUID = 1L;
    
    private final Queue<RFC> rfcIndex;
    
    public RFCQueryResponse(String ip, String os, String version,String status, String reason, Queue<RFC> rfcIndex) {
        super(ip, os, version, status, reason);
        this.rfcIndex = rfcIndex;
    }

    public Queue<RFC> getRfcIndex() {
        return rfcIndex;
    }

}
