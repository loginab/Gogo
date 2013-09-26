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
import java.util.Queue;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

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

public class RFCClient implements Runnable {
    
    private final String rsHost;
    private final int rsPort;
    private final int rfcServerPort;
    private final int mode;     // -1 = command line mode, 1 = task1 mode, 2 = task2Best mode, 3 = task3Worst mode
    private int cookie;

    public RFCClient(String rsHost, int rsPort, int rfcServerPort, int mode) {
        super();
        this.rsHost = rsHost;
        this.rsPort = rsPort;
        this.rfcServerPort = rfcServerPort;
        this.mode = mode;
    }

    @Override
    public void run() {
        if (mode == -1) {
            commandLineMode();
        } else if (mode == 1) {
            task1();
        } else if (mode == 2) {
            task2Best();
        } else if (mode == 3) {
            task2Worst();
        } else if (mode == 4) {
            task2New();
        }
    }
    
    private void commandLineMode() {
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
                         
                case 5 : System.out.println("Enter Hostname/IP of Peer: ");
                         String peerIp = in.next();
                         System.out.println("Enter RFC Server port of Peer: ");
                         int peerServerPort = in.nextInt();
                         
                         rfcQuery(peerIp, peerServerPort);
                         
                         System.out.println("RFCClient.rfcQuery() - RFC Query request successful. Print RFC index?(Y/N)");
                         if (in.next().equalsIgnoreCase("Y")) {
                             RFCIndex.getInstance().printRfcIndex();
                         }
                         break;
                         
                case 6 : System.out.println("Enter Rfc Number: ");
                         String rfcNumber = in.next();
                         
                         getRfc(rfcNumber);
                         
                         System.out.println("Print RFC index?(Y/N)");
                         if (in.next().equalsIgnoreCase("Y")) {
                             RFCIndex.getInstance().printRfcIndex();
                         }
                         break;
                  
                case 7 : printRfcIndex();
                         break;
                        
            }
            
        } while (userOpt != 15);
        
        System.out.println("RFCClient.run() - Shutting down peer client ...");
        in.close();    
        System.exit(1);
    }
    
    private void task1() {
        
        long taskStartTime = System.nanoTime();
        
        register();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        List<PeerInfo> peers = pQuery();
        System.out.println("RFCQuery request to peer with IP: " + peers.get(0).getHostname() + " , port: " + peers.get(0).getPort());
        
        rfcQuery(peers.get(0).getHostname(), peers.get(0).getPort());
        
        System.out.println("RFC Index after rfcQuery");
        RFCIndex.getInstance().printRfcIndex();
        
        Queue<RFC> rfcs = RFCIndex.getInstance().getRfcs();
        for (RFC rfc : rfcs) {
            getRfc(rfc.getRfcNumber());
        }
        
        long duration = System.nanoTime() - taskStartTime - 5000000000L;
        System.out.println("RFCClient.taskMode() - Total time for the task = " + duration);
    }
    
    private void task2Best() {
        
        long taskStartTime = System.nanoTime();
        
        register();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        List<PeerInfo> peers = pQuery();
        PeerInfo nbr = null;
        int round = 1;
        
        while (RFCIndex.getInstance().countRfcs() < peers.size()*10) {
            int nbrIndex = -1;
            if (round % 2 == 0) {
                 if (cookie % 2 == 0) {
                     nbrIndex = cookie - 2;
                 } else {
                     nbrIndex = cookie;
                 }
            } else {
                // round 1 or 3
                nbrIndex = (cookie + 1)%peers.size();
            }
            nbr = peers.get(nbrIndex);
            System.out.println("Neighbour IP: " + nbr.getHostname() + ", port: " + nbr.getPort());
            rfcQuery(nbr.getHostname(), nbr.getPort());
            // System.out.println("RFC Index after rfcQuery");
            // RFCIndex.getInstance().printRfcIndex();
            
            Queue<RFC> rfcs = RFCIndex.getInstance().getRfcs();
            for (RFC rfc : rfcs) {
                // Ensure that each peer downloads from its neighbor only
                if (rfc.getPeerIp().equals(nbr.getHostname())
                        && rfc.getPeerRfcServerPort() == nbr.getPort()) {
                    getRfc(rfc.getRfcNumber());
                }
            }
            
            round++;
        }
        
        long duration = System.nanoTime() - taskStartTime - 5000000000L;
        System.out.println("RFCClient.taskMode() - Total time for the task = " + duration);
    }
      
    private void task2Worst() {
        
        long taskStartTime = System.nanoTime();
        
        register();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        List<PeerInfo> peers = pQuery();
        PeerInfo peer = null;
        int index = 0;
        while (RFCIndex.getInstance().countRfcs() < peers.size()*10) {
            
            peer = peers.get(index%peers.size());
            
            System.out.println("RFCQuery request to peer with index: " + index + " IP: " + peer.getHostname() + " , port: " + peer.getPort());
            rfcQuery(peer.getHostname(), peer.getPort());
            // System.out.println("RFC Index after rfcQuery");
            // RFCIndex.getInstance().printRfcIndex();
            
            Queue<RFC> rfcs = RFCIndex.getInstance().getRfcs();
            for (RFC rfc : rfcs) {
                // Ensure that each peer downloads from its neighbor only
                if (rfc.getPeerIp().equals(peer.getHostname())
                        && rfc.getPeerRfcServerPort() == peer.getPort()) {
                    getRfc(rfc.getRfcNumber());
                }
            }
            index++;
        }
        
        long duration = System.nanoTime() - taskStartTime - 5000000000L;
        System.out.println("RFCClient.taskMode() - Total time for the task = " + duration);
    }
    
    private void task2New() {
        
        long taskStartTime = System.nanoTime();
        
        register();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        List<PeerInfo> peers = pQuery();
        while (RFCIndex.getInstance().countRfcs() < peers.size()*10) {
            for (PeerInfo peer : peers) {
                rfcQuery(peer.getHostname(), peer.getPort());
            }
            Queue<RFC> rfcs = RFCIndex.getInstance().getRfcs();
            
            for (RFC rfc : rfcs) {  
                getRfc(rfc.getRfcNumber());
            }
        }
    
        long duration = System.nanoTime() - taskStartTime - 5000000000L;
        System.out.println("RFCClient.taskMode() - Total time for the task = " + duration);
        
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
    
    private List<PeerInfo> pQuery() {
        PQueryRequest pQuery = new PQueryRequest(Initialize.myIp, Initialize.myOs, Initialize.version, cookie);
        PQueryResponse rsp = (PQueryResponse) sendToRS(pQuery);
        List<PeerInfo> peers = null;
        
        if (rsp != null && rsp.getStatus().equals(Constants.RESPONSE_STATUS_OK)) {
            System.out.println("RFCClient.pQuery() - PQuery request successful.");
            peers = rsp.getActivePeers();
            
            int peerCount = 1;
            if (peers.size() == 1 && peers.get(0).getHostname().equals(Initialize.myIp) && rfcServerPort == peers.get(0).getPort()) {
                System.out.println("No other active peers in the system!");
            } else{
                System.out.println("Printing active peer list ...");
                for (PeerInfo peer : peers) {
                    
                    if (peer.getHostname().equals(Initialize.myIp) && rfcServerPort == peer.getPort()) {
                        // System.out.println("(SELF) Peer " + peerCount + ": Hostname/IP = " + peer.getHostname() + ", Port: " + peer.getPort());
                    } else {
                        System.out.println("Peer " + peerCount + ": Hostname/IP = " + peer.getHostname() + ", Port: " + peer.getPort());
                        peerCount++;
                    }
                    
                }
            }
        } else {
            if (rsp == null) {
                System.out.println("RFCClient.pQuery() - MessageResponse is null from RS");
            } else {
                System.out.println("RFCClient.pQuery() - MessageResponse failed with reason: " + rsp.getReason());
            }
        }
        return peers;
    }
    
    private void rfcQuery(String peerIp, int peerServerPort) {
        
        if(peerIp.equals(Initialize.myIp) && peerServerPort == rfcServerPort) {
            // System.out.println("RFCClient.rfcQuery() - RFCQuery on itself is not supported!");
            return;
        }
        RFCQueryRequest rfcQueryRequest = new RFCQueryRequest(Initialize.myIp, Initialize.myOs, Initialize.version, -1);
        RFCQueryResponse response = (RFCQueryResponse) send(rfcQueryRequest, peerIp, peerServerPort);
        
        if (response != null && response.getStatus().equals(Constants.RESPONSE_STATUS_OK)) {
            // System.out.println("RFCClient.rfcQuery() - RFCQuery request successful to " + peerIp + " & " + peerServerPort);
            RFCIndex.getInstance().mergeRfcIndex(response.getRfcIndex(), peerIp, peerServerPort, rfcServerPort);
        } else {
            if (response == null) {
                System.out.println("RFCClient.rfcQuery() - MessageResponse is null from peer");
            } else {
                System.out.println("RFCClient.rfcQuery() - MessageResponse failed with reason: " + response.getReason());
            }
        }
    }
    
    public void getRfc(String rfcNumber) {
        
        long taskStartTime = System.nanoTime();
        
        RFC rfc = RFCIndex.getInstance().findRfcInIndex(rfcNumber);
        GetRFCResponse response = null;
        
        if (rfc == null) {
            // System.out.println("RFCClient.getRfc() - Rfc number " + rfcNumber + " doesn't exist in the index!");            
        } else if (rfc.getPeerRfcServerPort() == -1) {
            // System.out.println("RFCClient.getRfc() - Rfc number " + rfcNumber + " is available locally in rfc directory!");
        } else {
            GetRFCRequest getRFCRequest = new GetRFCRequest(Initialize.myIp, Initialize.myOs, Initialize.version, -1, rfcNumber);
            // System.out.println("RFCClient.getRfc() - Downloading rfc " + rfcNumber + " from Peer with IP: " + rfc.getPeerIp() + " and rfc server port: " + rfc.getPeerRfcServerPort());
            response = (GetRFCResponse) send(getRFCRequest, rfc.getPeerIp(), rfc.getPeerRfcServerPort());
            
            if (response != null && response.getStatus().equals(Constants.RESPONSE_STATUS_OK)) {
                String filename = RFCIndex.getRfcFileNameFromNumber(rfc.getRfcNumber());
                OutputStream out = null;
                try {
                    out = new FileOutputStream(filename);
                    IOUtils.write(response.getRfcContent(), out);
                    
                    RFC localRfc = new RFC(rfcNumber, response.getFilename(), response.getLastModified(),
                            response.getLength(), Initialize.myIp, -1, 7200);
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
        
        long duration = System.nanoTime() - taskStartTime;
        System.out.println("RFCClient.getRfc() - Time to download rfc " + rfcNumber + " = " + duration);
    }
    
    private void printRfcIndex() {
        RFCIndex.getInstance().printRfcIndex();
    }

    private MessageResponse sendToRS(MessageRequest req) {
        return send(req, rsHost, rsPort);
    }
    
    private MessageResponse send(MessageRequest req, String host, int port) {
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        MessageResponse rsp = null;
        // System.out.println("Opening TCP socket to " + host + " and " + port);
        
        try {
            socket = new Socket(host, port);
            oos = new ObjectOutputStream(socket.getOutputStream());   
            oos.writeObject(req);
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            rsp = (MessageResponse) ois.readObject();
        } catch (UnknownHostException e) {
            System.out.println("RFCClient.send() - UnknownHostException with message: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            if (e.getMessage().equals("Connection refused")) {
                System.out.println("Unable to connect to host: " + host + ", port: " + port + ". Connection refused");
            } else if (e.getMessage().equals("Connection timed out")) { 
                System.out.println("Unable to connect to host: " + host + ", port: " + port + ". Connection timed out");
            } else{
                System.out.println("RFCClient.send() - IOException with message: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("RFCClient.send() - ClassNotFoundException with message: " + e.getMessage());
            e.printStackTrace();
        } finally { 
            try {
                oos.close();
                ois.close();
                socket.close();
            } catch (Exception e) {
                //System.out.println("RFCClient.send() - Exception in finally block: " + e.getMessage());
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
