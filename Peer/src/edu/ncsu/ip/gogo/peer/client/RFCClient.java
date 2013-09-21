package edu.ncsu.ip.gogo.peer.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

import edu.ncsu.ip.gogo.dao.GetRFCRequest;
import edu.ncsu.ip.gogo.dao.GetRFCResponse;
import edu.ncsu.ip.gogo.dao.KeepAliveRequest;
import edu.ncsu.ip.gogo.dao.LeaveRequest;
import edu.ncsu.ip.gogo.dao.MessageRequest;
import edu.ncsu.ip.gogo.dao.MessageResponse;
import edu.ncsu.ip.gogo.dao.PQueryRequest;
import edu.ncsu.ip.gogo.dao.PQueryResponse;
import edu.ncsu.ip.gogo.dao.PeerInfo;
import edu.ncsu.ip.gogo.dao.RFC;
import edu.ncsu.ip.gogo.dao.RFCQueryRequest;
import edu.ncsu.ip.gogo.dao.RFCQueryResponse;
import edu.ncsu.ip.gogo.dao.RegisterRequest;
import edu.ncsu.ip.gogo.dao.RegisterResponse;
import edu.ncsu.ip.gogo.peer.common.Constants;
import edu.ncsu.ip.gogo.peer.common.RFCIndex;
import edu.ncsu.ip.gogo.peer.main.Initialize;
import edu.ncsu.ip.gogo.peer.utils.ClientUtils;

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
        Scanner in;
        
        do {
            in = new Scanner(System.in);
            System.out.println("Enter option number for RFCClient operation from below: ");
            System.out.println("1. Register");
            System.out.println("2. Leave");
            System.out.println("3. PQuery");
            System.out.println("4. KeepAlive");
            System.out.println("5. RFCQuery");
            System.out.println("6. GetRFC");
            System.out.println("7. Print RFC Index");
            System.out.println("15. Exit");
            
            
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
                         
                case 5 : rfcQuery(in);
                         break;
                         
                case 6 : getRfc(in);
                         break;
                  
                case 7 : printRfcIndex();
                         break;
                        
            }
            
        } while (userOpt != 15);
        
        System.out.println("RFCClient.run() - Shutting down peer client ...");
        in.close();
    }
    
    private void register() {
        RegisterRequest reg = new RegisterRequest(Initialize.myIp, Initialize.myOs, Initialize.version, rfcServerPort);
        RegisterResponse rsp = (RegisterResponse) sendToRS(reg);
        
        if (rsp != null && rsp.getStatus().equals(Constants.RESPONSE_STATUS_OK)) {
            setCookie(rsp.getCookie());
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
        MessageResponse rsp = sendToRS(leave);
        
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
        MessageResponse rsp = sendToRS(keepAlive);
        
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
            int peerCount = 1;
            for (PeerInfo peer : peers) {
                System.out.println("Peer " + peerCount + ": Hostname/IP = " + peer.getHostname() + ", Port: " + peer.getPort());
                peerCount++;
            }
            
        } else {
            if (rsp == null) {
                System.out.println("RFCClient.pQuery() - MessageResponse is null from RS");
            } else {
                System.out.println("RFCClient.pQuery() - MessageResponse failed with reason: " + rsp.getReason());
            }
        }
    }
    
    private void rfcQuery(Scanner in) {
        System.out.println("Enter Hostname/IP of Peer: ");
        String peerIp = in.next();
        System.out.println("Enter RFC Server port of Peer: ");
        int rfcServerPort = in.nextInt();
        
        RFCQueryRequest rfcQueryRequest = new RFCQueryRequest(Initialize.myIp, Initialize.myOs, Initialize.version, -1);
        RFCQueryResponse response = (RFCQueryResponse) send(rfcQueryRequest, peerIp, rfcServerPort);
        
        if (response != null && response.getStatus().equals(Constants.RESPONSE_STATUS_OK)) {
            RFCIndex.getInstance().mergeRfcIndex(response.getRfcIndex(), peerIp, rfcServerPort);
            System.out.println("RFCClient.rfcQuery() - RFC Query request successful. Print RFC index?(Y/N)");
            if (in.next().equals("Y")) {
                RFCIndex.getInstance().printRfcIndex();
            }
        } else {
            if (response == null) {
                System.out.println("RFCClient.rfcQuery() - MessageResponse is null from peer");
            } else {
                System.out.println("RFCClient.rfcQuery() - MessageResponse failed with reason: " + response.getReason());
            }
        }
    }
    
    private void getRfc(Scanner in) {
        
        System.out.println("Enter Rfc Number: ");
        String rfcNumber = in.next();
        
        RFC rfc = RFCIndex.getInstance().findRfcInIndex(rfcNumber);
        GetRFCResponse response = null;
        
        if (rfc == null) {
            System.out.println("RFCClient.getRfc() - Rfc number " + rfcNumber + " doesn't exist in the index!");
            System.out.println("Print RFC index?(Y/N)");
            if (in.next().equals("Y")) {
                RFCIndex.getInstance().printRfcIndex();
            }
            
        } else if (rfc.getPeerRfcServerPort() == -1) {
            System.out.println("RFCClient.getRfc() - Rfc number " + rfcNumber + " is available locally in rfc directory!");
            System.out.println("Print RFC index?(Y/N)");
            if (in.next().equals("Y")) {
                RFCIndex.getInstance().printRfcIndex();
            }
        } else {
            GetRFCRequest getRFCRequest = new GetRFCRequest(Initialize.myIp, Initialize.myOs, Initialize.version, -1, rfcNumber);
            System.out.println("RFCClient.getRfc() - Downloading rfc " + rfcNumber + " from Peer with IP: " + rfc.getPeerIp() + " and rfc server port: " + rfc.getPeerRfcServerPort());
            response = (GetRFCResponse) send(getRFCRequest, rfc.getPeerIp(), rfc.getPeerRfcServerPort());
            
            if (response != null && response.getStatus().equals(Constants.RESPONSE_STATUS_OK)) {
                // TODO: Update rfc index here with newly downloaded rfc! Add bytestream to rfc class itself and send an object of rfc in GetRFCResponse
                String filename = RFCIndex.getRfcFileNameFromNumber(rfc.getRfcNumber());
                OutputStream out = null;
                try {
                    out = new FileOutputStream(filename);
                    ClientUtils.copy(response.getRfcContent(), out);
                    RFC localRfc = new RFC(rfcNumber, response.getFilename(), response.getLastModified(), response.getLength(), Initialize.myIp, -1);
                    RFCIndex.getInstance().addRfcToIndex(localRfc);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                
            } else {
                if (response == null) {
                    System.out.println("RFCClient.getRfc() - MessageResponse is null from peer");
                } else {
                    System.out.println("RFCClient.getRfc() - MessageResponse failed with reason: " + response.getReason());
                }
            }
        }
    }
    
    private void printRfcIndex() {
        RFCIndex.getInstance().printRfcIndex();
    }

    private MessageResponse sendToRS(MessageRequest req) {
        return send(req, rsHost, rsPort);
    }
    
    private MessageResponse send(MessageRequest req, String host, int port) {
        Socket socket = null;
        //OutputStream os = null;
        ObjectOutputStream oos = null;
        //InputStream is = null;
        ObjectInputStream ois = null;
        MessageResponse rsp = null;
        System.out.println("Opening TCP socket to " + host + " and " + port);
        
        try {
            socket = new Socket(host, port);
            //os = socket.getOutputStream();
            oos = new ObjectOutputStream(socket.getOutputStream());   
            oos.writeObject(req);
            oos.flush();
            //is = socket.getInputStream();
            ois = new ObjectInputStream(socket.getInputStream());
            rsp = (MessageResponse) ois.readObject();
        } catch (UnknownHostException e) {
            System.out.println("RFCClient.send() - UnknownHostException with message: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("RFCClient.send() - IOException with message: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("RFCClient.send() - ClassNotFoundException with message: " + e.getMessage());
            e.printStackTrace();
        } finally { 
            try {
                oos.close();
                //os.close();
                //is.close();
                ois.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("RFCClient.send() - Exception in finally block: " + e.getMessage());
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
