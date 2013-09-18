package edu.ncsu.ip.gogo.peer.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

import edu.ncsu.ip.gogo.dao.KeepAliveRequest;
import edu.ncsu.ip.gogo.dao.LeaveRequest;
import edu.ncsu.ip.gogo.dao.MessageRequest;
import edu.ncsu.ip.gogo.dao.MessageResponse;
import edu.ncsu.ip.gogo.dao.PQueryRequest;
import edu.ncsu.ip.gogo.dao.PQueryResponse;
import edu.ncsu.ip.gogo.dao.PeerInfo;
import edu.ncsu.ip.gogo.dao.RegisterRequest;
import edu.ncsu.ip.gogo.dao.RegisterResponse;
import edu.ncsu.ip.gogo.peer.common.Constants;
import edu.ncsu.ip.gogo.peer.main.Initialize;

public class RFCClient implements Runnable {
    
    private final String rsHost;
    private final int rsPort;
    private final int rfcServerPort;
    
    private int cookie;
    

    public RFCClient(String rsHost, int rsPort, int rfcServerPort) {
        super();
        this.rsHost = rsHost;
        this.rsPort = rsPort;
        this.rfcServerPort = rfcServerPort;
    }

    @Override
    public void run() {
        int userOpt = -1;
        Scanner in = new Scanner(System.in);
        do {
            System.out.println("Enter option number for RFCClient operation from below: ");
            System.out.println("1. Register");
            System.out.println("2. Leave");
            System.out.println("3. PQuery");
            System.out.println("4. KeepAlive");
            System.out.println("5. RFCQuery");
            System.out.println("6. GetRFC");
            System.out.println("7. Exit");
            
            
            userOpt = in.nextInt();
            
            switch(userOpt) {
                case 1 : register();
                         break;
                         
                case 2 : leave();
                         break;
                         
                case 3 : pQuery();
                         break;
                         
                case 4 : keepAlive();
                         break;
                         
                case 5 : //rfcQuery();
                         break;
                         
                case 6 : //getRfc();
                         break;
            }
            
        } while (userOpt != 7);
        
        System.out.println("RFCClient.run() - Shutting down peer client ...");
        
        in.close();
    }
    
    private void register() {
        RegisterRequest reg = new RegisterRequest(Initialize.myIp, Initialize.myOs, Initialize.version, rfcServerPort);
        MessageResponse rsp = (MessageResponse) sendToRS(reg);
        
        if (rsp != null && rsp.getStatus().equals(Constants.RESPONSE_STATUS_OK)) {
            RegisterResponse response = (RegisterResponse) rsp;
            setCookie(response.getCookie());
            System.out.println("RFCClient.register() - Register request successful. RS assigned cookie: " + getCookie());
        } else {
            if (rsp == null) {
                System.out.println("RFCClient.register() - MessageResponse is null from RS");
            } else {
                System.out.println("RFCClient.register() - MessageResponse failed with reason: " + rsp.getReason());
            }
        }
    }
    
    private void leave() {
        LeaveRequest leave = new LeaveRequest(Initialize.myIp, Initialize.myOs, Initialize.version, cookie);
        MessageResponse rsp = (MessageResponse) sendToRS(leave);
        
        if (rsp != null && rsp.getStatus().equals(Constants.RESPONSE_STATUS_OK)) {
            System.out.println("RFCClient.leave() - Leave request successful.");
        } else {
            if (rsp == null) {
                System.out.println("RFCClient.leave() - MessageResponse is null from RS");
            } else {
                System.out.println("RFCClient.leave() - MessageResponse failed with reason: " + rsp.getReason());
            }
        }
    }
    
    private void keepAlive() {
        KeepAliveRequest keepAlive = new KeepAliveRequest(Initialize.myIp, Initialize.myOs, Initialize.version, cookie);
        MessageResponse rsp = (MessageResponse) sendToRS(keepAlive);
        
        if (rsp != null && rsp.getStatus().equals(Constants.RESPONSE_STATUS_OK)) {
            System.out.println("RFCClient.keepAlive() - Keep Alive request successful.");
        } else {
            if (rsp == null) {
                System.out.println("RFCClient.keepAlive() - MessageResponse is null from RS");
            } else {
                System.out.println("RFCClient.keepAlive() - MessageResponse failed with reason: " + rsp.getReason());
            }
        }
    }
    
    private void pQuery() {
        PQueryRequest pQuery = new PQueryRequest(Initialize.myIp, Initialize.myOs, Initialize.version, cookie);
        
        PQueryResponse rsp = (PQueryResponse) sendToRS(pQuery);
        
        if (rsp != null && rsp.getStatus().equals(Constants.RESPONSE_STATUS_OK)) {
            System.out.println("RFCClient.pQuery() - PQuery request successful.");
            List<PeerInfo> peers = rsp.getActivePeers();
            System.out.println("Printing active peer list ...");
            for (PeerInfo peer : peers) {
                System.out.println("Hostname/IP: " + peer.getHostname());
                System.out.println("Port: " + peer.getPort());
            }
            
        } else {
            if (rsp == null) {
                System.out.println("RFCClient.pQuery() - MessageResponse is null from RS");
            } else {
                System.out.println("RFCClient.pQuery() - MessageResponse failed with reason: " + rsp.getReason());
            }
        }
    }
    
    private MessageResponse sendToRS(MessageRequest req) {
        Socket socket = null;
        OutputStream os = null;
        ObjectOutputStream oos = null;
        InputStream is = null;
        ObjectInputStream ois = null;
        MessageResponse rsp = null;
        System.out.println("Opening TCP socket to " + rsHost + " and " + rsPort);
        
        try {
            socket = new Socket(rsHost, rsPort);
            os = socket.getOutputStream();
            oos = new ObjectOutputStream(os);   
            oos.writeObject(req);
            is = socket.getInputStream();
            ois = new ObjectInputStream(is);
            rsp = (MessageResponse) ois.readObject();
        } catch (UnknownHostException e) {
            System.out.println("RFCClient.sendToRS() - UnknownHostException with message: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("RFCClient.sendToRS() - IOException with message: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("RFCClient.sendToRS() - ClassNotFoundException with message: " + e.getMessage());
            e.printStackTrace();
        } finally { 
            try {
                oos.close();
                os.close();
                is.close();
                ois.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("RFCClient.sendToRS() - Exception in finally block: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return rsp;
    }

    public int getCookie() {
        return cookie;
    }

    private void setCookie(int cookie) {
        this.cookie = cookie;
    }    
}
