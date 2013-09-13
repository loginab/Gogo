package edu.ncsu.ip.gogo.rs.main;

import edu.ncsu.ip.gogo.rs.server.RegistrationServer;

public class Initialize {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RegistrationServer rs = new RegistrationServer(65423);
		rs.init();
	}

}
