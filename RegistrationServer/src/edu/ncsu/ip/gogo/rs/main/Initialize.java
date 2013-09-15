package edu.ncsu.ip.gogo.rs.main;

import edu.ncsu.ip.gogo.rs.server.RegistrationServer;
import edu.ncsu.ip.gogo.rs.utils.Utils;

public class Initialize {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 65423;
		int cookieInitial = 0;
		RegistrationServer rs = new RegistrationServer(port,cookieInitial);
		System.out.println("The server is up");
		System.out.println("The IP of the server is "+Utils.getLocalIpAddress());
		System.out.println("The server is lisiting on port " + port);
		rs.init();

	}

}
