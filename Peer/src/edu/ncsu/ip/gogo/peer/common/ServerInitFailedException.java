package edu.ncsu.ip.gogo.peer.common;

public class ServerInitFailedException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public ServerInitFailedException(Exception e) {
        super(e);
    }
    

}
