package edu.ncsu.ip.gogo.peer.common;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.ncsu.ip.gogo.dao.RFC;
import edu.ncsu.ip.gogo.peer.main.Initialize;

public final class RFCIndex {
    
    private final Queue<RFC> rfcs;
    public static final String relativeRfcDirPath = "./rfc";
    public static final String rfcFileNameEnd = ".txt";
    public static final String rfcFileNameStart = "rfc";
    private static final RFCIndex INSTANCE = new RFCIndex();
    
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
            
            if (file.isFile() && file.getName().startsWith(rfcFileNameStart) && file.getName().endsWith(rfcFileNameEnd)) {
                String rfcNumber = file.getName().substring(3, file.getName().indexOf(".txt"));
                RFC rfc = new RFC(rfcNumber, file.getName(), file.lastModified(), file.length(), Initialize.myIp, -1);
                rfcs.add(rfc);
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
                printMessage = rfcCount + ". Rfc number: " + rfc.getRfcNumber() + " isLocal = No, Peer IP: " + rfc.getPeerIp() + " , Peer RFC Server Port: " + rfc.getPeerRfcServerPort() + "Filename: " + rfc.getFilename();
            }
            
            System.out.println(printMessage);
            rfcCount++;
        }
    }
    
    
    public void mergeRfcIndex(Queue<RFC> peerRfcIndex) {
        rfcs.addAll(peerRfcIndex);
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
    
    
    public Queue<RFC> getRfcs() {
        return rfcs;
    }

    
    public void addRfcToIndex(RFC rfc) {
        if (rfc != null) {
            rfcs.add(rfc);
        }
    }
    
    
    public static String getRfcFileNameFromNumber(String rfcNumber) {
        return relativeRfcDirPath + "/" + rfcFileNameStart + rfcNumber + rfcFileNameEnd;
    }
       
}
