package edu.ncsu.ip.gogo.peer.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.ncsu.ip.gogo.dao.RFC;
import edu.ncsu.ip.gogo.peer.main.Initialize;

public final class RFCIndex {
    
    private final Queue<RFC> rfcs;
    public static final String relativeRfcDirPath = "./rfc";
    public static final String rfcFileNameEnd = ".txt";
    public static final String rfcFileNameStart = "rfc";
    private static final RFCIndex INSTANCE = new RFCIndex();
    public static final String rfcFileContent = "Request for Comments: ";
    
    private RFCIndex() {
        rfcs = new ConcurrentLinkedQueue<RFC>();
        buildRfcIndex();
    }
    
    public static RFCIndex getInstance() {
        return INSTANCE;
    }
    
    private void buildRfcIndex() {
        
        File rfcDir = new File(relativeRfcDirPath);
        File[] filesInRfcDir = rfcDir.listFiles();
        
        for (int i = 0; i < filesInRfcDir.length; i++) {
            
            File file = filesInRfcDir[i];
            String rfcNumber = null;
            if (file.isFile()) {
                if (file.getName().startsWith(rfcFileNameStart) && file.getName().endsWith(rfcFileNameEnd)) {
                    rfcNumber = file.getName().substring(3, file.getName().indexOf(".txt"));
                    RFC rfc = new RFC(rfcNumber, file.getName(), file.lastModified(), file.length(), Initialize.myIp, -1);
                    rfcs.add(rfc);
                } else {
                    rfcNumber = findRfcNumberFromContent(file);
                    if (rfcNumber != null) {
                        RFC rfc = new RFC(rfcNumber, file.getName(), file.lastModified(), file.length(), Initialize.myIp, -1);
                        rfcs.add(rfc);
                    }
                }
            }
            
        }
    }
    
    
    public void printRfcIndex() {
        
        int rfcCount = 1;
        for (RFC rfc : rfcs) {
            String printMessage = null;
            if (rfc.getPeerRfcServerPort() == -1) {
                printMessage = rfcCount + ". Rfc number: " + rfc.getRfcNumber() + " isLocal = Yes, Filename: " + rfc.getFilename();
            } else {
                printMessage = rfcCount + ". Rfc number: " + rfc.getRfcNumber() + " isLocal = No, Peer IP: " + rfc.getPeerIp() + " , Peer RFC Server Port: " 
                                + rfc.getPeerRfcServerPort() + " , Filename: " + rfc.getFilename();
            }
            
            System.out.println(printMessage);
            rfcCount++;
        }
    }
    
    
    public void mergeRfcIndex(Queue<RFC> peerRfcIndex, String peerIp, int peerRfcServerPort, int myRfcServerPort) {
        for (RFC rfc : peerRfcIndex) {
            if (!isRfcDuplicate(rfc, peerIp, peerRfcServerPort)) {
                RFC entry = null;
                if (rfc.getPeerIp().equals(peerIp) && rfc.getPeerRfcServerPort() == -1) {
                    entry = new RFC(rfc.getRfcNumber(), rfc.getFilename(), rfc.getLastModified(), rfc.getLength(),
                        peerIp, peerRfcServerPort);
                } else if (rfc.getPeerIp().equals(Initialize.myIp) && rfc.getPeerRfcServerPort() == myRfcServerPort){
                    entry = null;
                } else {
                    entry = rfc;
                }
                
                if (entry != null) {
                    rfcs.add(entry);
                }
            }
        }
    }
    
    
    public RFC findRfcInIndex(String rfcNumber) {

        List<RFC> rfcFoundList = new LinkedList<RFC>();
        
        for (RFC rfc : rfcs) {
            if (rfc.getRfcNumber().equals(rfcNumber)) {
                if (rfc.getPeerRfcServerPort() == -1) {
                    return rfc;
                }
                rfcFoundList.add(rfc);
            }
        }
        if (rfcFoundList.isEmpty()) {
            return null;
        } else {
            return rfcFoundList.get(0);
        }
    }
    
    private boolean isRfcDuplicate(RFC rfc, String peerIp, int peerRfcServerPort) {
        
        for (RFC r : rfcs) {
            if (rfc.getRfcNumber().equals(r.getRfcNumber()) 
                    && rfc.getPeerIp().equals(r.getPeerIp())
                    && rfc.getPeerRfcServerPort() == r.getPeerRfcServerPort()) {
                return true;
            } 
            
            if(rfc.getPeerRfcServerPort() == -1) {
                if(rfc.getRfcNumber().equals(r.getRfcNumber()) 
                    && rfc.getPeerIp().equals(r.getPeerIp())
                    && peerRfcServerPort == r.getPeerRfcServerPort()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    public Queue<RFC> getRfcs() {
        return rfcs;
    }

    
    public void addRfcToIndex(RFC rfc) {
        if (rfc != null) {
            rfcs.add(rfc);
        }
    }
    
    private String findRfcNumberFromContent(File file) {
        String rfcNumber = null;
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        while (scanner.hasNextLine()) {
           String lineFromFile = scanner.nextLine();
           if(lineFromFile.contains(rfcFileContent)) { 
               String[] tokens = lineFromFile.split("\\s");
               System.out.println("rfcNumber: " + tokens[3]);
               rfcNumber = tokens[3];
               break;
           }
        }
        return rfcNumber;
    }
    
    public static String getRfcFileNameFromNumber(String rfcNumber) {
        return relativeRfcDirPath + "/" + rfcFileNameStart + rfcNumber + rfcFileNameEnd;
    }       
}
