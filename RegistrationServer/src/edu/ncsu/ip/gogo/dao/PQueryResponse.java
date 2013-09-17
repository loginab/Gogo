package edu.ncsu.ip.gogo.dao;

import java.util.List;

public class PQueryResponse extends MessageResponse {
    
    private static final long serialVersionUID = 1L;
    
    private final List<PeerInfo> activePeers;

    public PQueryResponse(String ip, String os, String version, String status, String reason, List<PeerInfo> peers) {
        super(ip, os, version, status, reason);
        this.activePeers = peers;
    }

    public List<PeerInfo> getActivePeers() {
        return activePeers;
    }
}
