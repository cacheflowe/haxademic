package com.haxademic.sketch.test;

import hypermedia.net.UDP;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

@SuppressWarnings("serial")
public class UdpTest 
extends PAppletHax {
	
	protected UDP udp;

	
	protected void overridePropsFile() {
		// _appConfig.setProperty( "width", "1200" );
	}
	
	public void setup() {
		super.setup();
		  udp = new UDP( this, 6000 );
		  udp.log( true ); 		// <-- printout the connection activity
		  udp.listen( true );

	}
	
	public void drawApp() {
	}

	public void keyPressed() {
		super.keyPressed();
		
	    String message  = str( key );	// the message to send
	    String ip       = "10.0.1.138";	// the remote IP address
	    int port        = 6100;		// the destination port
	    
	    message = message+";\n";
	    udp.send( message, ip, port );
	}
	
	/**
	 * To perform any action on datagram reception, you need to implement this 
	 * handler in your code. This method will be automatically called by the UDP 
	 * object each time he receive a nonnull message.
	 * By default, this method have just one argument (the received message as 
	 * byte[] array), but in addition, two arguments (representing in order the 
	 * sender IP address and his port) can be set like below.
	 */
	// void receive( byte[] data ) { 			// <-- default handler
	public void receive( byte[] data, String ip, int port ) {	// <-- extended handler
	  
	  
	  // get the "real" message =
	  // forget the ";\n" at the end <-- !!! only for a communication with Pd !!!
	  data = subset(data, 0, data.length-2);
	  String message = new String( data );
	  
	  // print the result
	  P.println( "receive: \""+message+"\" from "+ip+" on port "+port );
	}
}