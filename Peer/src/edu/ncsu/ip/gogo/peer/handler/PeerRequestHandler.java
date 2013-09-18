package edu.ncsu.ip.gogo.peer.handler;

import java.net.Socket;

import edu.ncsu.ip.gogo.dao.MessageRequest;
import edu.ncsu.ip.gogo.dao.MessageResponse;
import edu.ncsu.ip.gogo.dao.RFCQueryRequest;
import edu.ncsu.ip.gogo.peer.common.Constants;
import edu.ncsu.ip.gogo.peer.main.Initialize;

public class PeerRequestHandler implements Runnable {

    private final Socket clientSocket;
    private final MessageRequest request;
    
    public PeerRequestHandler(Socket clientSocket, MessageRequest request) {
        this.clientSocket = clientSocket;
        this.request = request;
    }
    
    @Override
    public void run() {
        if(request instanceof RFCQueryRequest) {
            
        } else {
            MessageResponse invalidMethodRsp = new MessageResponse(Initialize.myIp, Initialize.myOs, Initialize.version, Constants.RESPONSE_STATUS_ERROR,
                    "Request type isn't supported by the rfc server!");
            //sendResponseToPeer(clientSocket, invalidMethodRsp);
        } 

    }

}
