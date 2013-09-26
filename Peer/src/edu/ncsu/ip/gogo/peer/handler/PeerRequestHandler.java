package edu.ncsu.ip.gogo.peer.handler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

import edu.ncsu.ip.gogo.dao.GetRFCRequest;
import edu.ncsu.ip.gogo.dao.GetRFCResponse;
import edu.ncsu.ip.gogo.dao.MessageRequest;
import edu.ncsu.ip.gogo.dao.MessageResponse;
import edu.ncsu.ip.gogo.dao.RFC;
import edu.ncsu.ip.gogo.dao.RFCQueryRequest;
import edu.ncsu.ip.gogo.dao.RFCQueryResponse;
import edu.ncsu.ip.gogo.peer.common.Constants;
import edu.ncsu.ip.gogo.peer.common.RFCIndex;
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
        
        if (request instanceof RFCQueryRequest) {
            // System.out.println("PeerRequestHandler - RFCQuery request from peer with IP: " + request.getIp());
            RFCQueryResponse response = new RFCQueryResponse(Initialize.myIp, Initialize.myOs, Initialize.version, Constants.RESPONSE_STATUS_OK,
                    null, RFCIndex.getInstance().getRfcs());
            sendResponseToPeer(clientSocket, response);
        } else if (request instanceof GetRFCRequest) { 
            // System.out.println("PeerRequestHandler - GetRFC request from peer with IP: " + request.getIp());
            GetRFCRequest req = (GetRFCRequest) request;
            RFC rfc = RFCIndex.getInstance().findRfcInIndex(req.getRfcNumber()); 
            
            if (rfc == null || rfc.getPeerRfcServerPort() != -1) {
                MessageResponse rfcDoesntExist = new MessageResponse(Initialize.myIp, Initialize.myOs, Initialize.version, Constants.RESPONSE_STATUS_ERROR,
                        "RFC file doesn't exist on this peer");
                sendResponseToPeer(clientSocket, rfcDoesntExist);
            } else {
                String filename = RFCIndex.getRfcFileNameFromNumber(rfc.getRfcNumber());
                byte[] content = null;
                try {
                    content = IOUtils.toByteArray(new FileInputStream(filename));
                } catch (IOException e) {
                    System.out.println("PeerRequestHandler.run() - IOException with message: " + e.getMessage());
                    e.printStackTrace();
                    // TODO: return message response with error and reason
                }
                
                GetRFCResponse response = new GetRFCResponse(Initialize.myIp, Initialize.myOs, Initialize.version, Constants.RESPONSE_STATUS_OK,
                        null, content, rfc.getFilename(), rfc.getLastModified(), rfc.getLength());
                sendResponseToPeer(clientSocket, response);
            }
        } else {
            MessageResponse invalidMethodRsp = new MessageResponse(Initialize.myIp, Initialize.myOs, Initialize.version, Constants.RESPONSE_STATUS_ERROR,
                    "Request type isn't supported by the rfc server!");
            sendResponseToPeer(clientSocket, invalidMethodRsp);
        } 
        
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    

    private void sendResponseToPeer(Socket clientSocket, MessageResponse rsp) {
        
        OutputStream os = null;
        ObjectOutputStream oos = null;
        try {
            os = clientSocket.getOutputStream();
            oos = new ObjectOutputStream(os);   
            oos.writeObject(rsp);
        } catch (IOException e) {
            if (e.getMessage().equals("Connection refused")) {
                System.out.println("PeerRequestHandler.sendResponseToPeer() - Unable to connect to peer. Connection refused");
            } else if (e.getMessage().equals("Connection timed out")) { 
                System.out.println("PeerRequestHandler.sendResponseToPeer() - Unable to connect to host. Connection timed out");
            } else {
                System.out.println("PeerRequestHandler.sendResponseToPeer() - IOException with message: " + e.getMessage());
                e.printStackTrace();
            }
            
        } finally { 
            try {
                oos.close();
                os.close();
            } catch (Exception e) {
                System.out.println("PeerRequestHandler.sendResponseToPeer() - Exception in finally block: " + e.getMessage());
            }       
        }
    }
    
}
