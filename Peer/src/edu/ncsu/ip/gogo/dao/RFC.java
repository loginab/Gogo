package edu.ncsu.ip.gogo.dao;

import java.io.Serializable;

public class RFC implements Serializable {


    private static final long serialVersionUID = 1L;
    
    private final String rfcNumber;
    private final String filename;
    private final long lastModified;
    private final long length;
    private final String peerIp;
    private final int peerRfcServerPort;     // -1 if the RFC is available locally
    private final int TTL;

    
    public RFC(String rfcNumber, String filename, long lastModified,
            long length, String peerIp, int peerPort, int ttl) {
        super();
        this.rfcNumber = rfcNumber;
        this.filename = filename;
        this.lastModified = lastModified;
        this.length = length;
        this.peerIp = peerIp;
        this.peerRfcServerPort = peerPort;
        this.TTL = ttl;
    }


    public String getRfcNumber() {
        return rfcNumber;
    }


    public String getFilename() {
        return filename;
    }


    public long getLastModified() {
        return lastModified;
    }


    public long getLength() {
        return length;
    }


    public String getPeerIp() {
        return peerIp;
    }


    public int getPeerRfcServerPort() {
        return peerRfcServerPort;
    }


    public int getTTL() {
        return TTL;
    }
    
}
