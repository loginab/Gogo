package edu.ncsu.ip.gogo.rs.main;

import edu.ncsu.ip.gogo.rs.server.RegistrationServer;

public class Initialize {
    
    private final static int RS_PORT = 65423;
    private final static int INITIAL_COOKIE_VALUE = 0;

    public static void main(String[] args) {

        RegistrationServer rs = new RegistrationServer(RS_PORT, INITIAL_COOKIE_VALUE);
        rs.init();
    }

}
