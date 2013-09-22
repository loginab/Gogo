package edu.ncsu.ip.gogo.dao;


public class GetRFCResponse extends MessageResponse {

    private static final long serialVersionUID = 1L;
    
    private final byte[] rfcContent;
    private final String filename;
    private final long lastModified;
    private final long length;
    
    public GetRFCResponse(String ip, String os, String version, String status,
            String reason, byte[] rfcContent, String filename,
            long lastModified, long length) {
        super(ip, os, version, status, reason);
        this.rfcContent = rfcContent;
        this.filename = filename;
        this.lastModified = lastModified;
        this.length = length;
    }

    public byte[] getRfcContent() {
        return rfcContent;
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
    
    
}
