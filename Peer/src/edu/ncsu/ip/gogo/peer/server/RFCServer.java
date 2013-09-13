package edu.ncsu.ip.gogo.peer.server;

import edu.ncsu.ip.gogo.peer.common.ServerInitFailedException;

public class RFCServer {
    
    private int port;
    
    
    public RFCServer(int port) {
        this.port = port;
    }


    public void init() throws ServerInitFailedException {
        // Read local RFCs and initialize RFC index
        System.out.println("RFCServer.init() - Starting RFCServer on port: " + port);
        
    }

}
