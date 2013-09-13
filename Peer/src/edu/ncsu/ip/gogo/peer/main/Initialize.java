package edu.ncsu.ip.gogo.peer.main;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.ncsu.ip.gogo.peer.client.RFCClient;
import edu.ncsu.ip.gogo.peer.common.ServerInitFailedException;
import edu.ncsu.ip.gogo.peer.server.RFCServer;
import edu.ncsu.ip.gogo.peer.utils.ThreadUtils;

public class Initialize {

    private final static int serverPortStartRange = 65400;
    private final static int serverPortEndRange = 65500;
    private final static String RS_IP_OPT = "rsIp";
    private final static String RS_PORT_OPT = "rsPort";
    
    @SuppressWarnings("static-access")
	public static void main(String[] args) {
        
    	/********************************* Parse command line options **********************************/
    	Option rsIp = OptionBuilder.withArgName(RS_IP_OPT)
    							   .hasArg()
					               .withDescription("ip of the registration server")
					               .isRequired()
					               .create(RS_IP_OPT);
    	
    	Option rsPort = OptionBuilder.withArgName(RS_PORT_OPT)
						             .hasArg()
						             .withDescription("port of the registration server")
						             .isRequired()
						             .create(RS_PORT_OPT);
    	
    	Options options = new Options();
    	options.addOption(rsIp);
    	options.addOption(rsPort);
    	
    	CommandLineParser parser = new BasicParser();
    	CommandLine line = null;
  
    	try {
    		line = parser.parse(options, args);
    	} catch (ParseException e) {
    		System.out.println("Initialize.main() - ParseException with message: " + e.getMessage());
    	}
    	
    	String rsHostIpVal = null;
    	int rsPortVal = -1;
    	
    	if (line.hasOption(RS_IP_OPT)) {
    		rsHostIpVal = line.getOptionValue(RS_IP_OPT);
    	}
    	
    	if (line.hasOption(RS_PORT_OPT)) {
    		rsPortVal = Integer.parseInt(line.getOptionValue(RS_PORT_OPT));
    	}
    	
    	/********************************* Initialize RFC Server **********************************/
    	
        int serverPort = findServerPort(serverPortStartRange, serverPortEndRange);
        RFCServer server = new RFCServer(serverPort);
        
        try{    
            server.init();
        } catch (ServerInitFailedException e) {
            System.out.println("Initialize.main() - ServerInitFailedException with message: " + e.getMessage());
        }
        
        /********************************* Initialize RFC Client **********************************/
        
        RFCClient client = new RFCClient(rsHostIpVal, rsPortVal, serverPort);
        ExecutorService clientExec = Executors.newSingleThreadExecutor();
        clientExec.execute(client);
        ThreadUtils.shutdownAndAwaitTermination(clientExec);
        
    }
    
    static int findServerPort(int start, int end) {
        Random rand = new Random();
        int port = rand.nextInt((end - start) + 1) + start;
        return port;
    }
}
