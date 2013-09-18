package edu.ncsu.ip.gogo.peer.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.ncsu.ip.gogo.dao.MessageRequest;
import edu.ncsu.ip.gogo.peer.common.ServerInitFailedException;
import edu.ncsu.ip.gogo.peer.handler.PeerRequestHandler;


public class RFCServer {
    
    private int rfcServerPort;

    public RFCServer(int port) {
        this.rfcServerPort = port;
    }


    public void init() throws ServerInitFailedException {
        // Read local RFCs and initialize RFC index
        
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(rfcServerPort);
        } catch (IOException e) {
            throw new ServerInitFailedException(e);
        }
        
        System.out.println("RFCServer.init() - Started RfCServer on port: " + rfcServerPort + ". Waiting for peer requests ...");
        
        
        while (true) {
            Socket clientSocket = null;
            InputStream is = null;
            ObjectInputStream ois = null;
            try {
                clientSocket = serverSocket.accept();
                is = clientSocket.getInputStream();   
                ois = new ObjectInputStream(is);
                MessageRequest msg = (MessageRequest)ois.readObject();
                
                PeerRequestHandler handler = new PeerRequestHandler(clientSocket, msg);
                handler.run();
            } catch (IOException e) {
                System.out.println("RFCServer.init() - Accept failed with IOException: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e){
                System.out.println("RFCServer.init() - Accept failed with Exception: " + e.getMessage());
                e.printStackTrace();
            } finally { 
                try {
                    ois.close();
                    is.close();
                    clientSocket.close();
                } catch (Exception e) {
                    System.out.println("RFCServer.init() - Exception in finally block: " + e.getMessage());
                    e.printStackTrace();
                }
            }
                
        }
    }

}
