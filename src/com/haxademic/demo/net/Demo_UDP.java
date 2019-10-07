package com.haxademic.demo.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.StringBufferLog;

import hypermedia.net.UDP;

public class Demo_UDP 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected UDP udp;
	protected int portLocal = 6001;	// <-- swap these and run a 2nd instance to test locally 
	protected int portRemote = 6000;
	
	protected StringBufferLog logOut = new StringBufferLog(10);
	protected StringBufferLog logIn = new StringBufferLog(10);

	public void setupFirstFrame() {
		udp = new UDP(this, portLocal);
		udp.log(true);
		udp.listen(true);
	}

	public void drawApp() {
		background(0);
		// should only be one
		logOut.printToScreen(p.g, 20, 20);
		logIn.printToScreen(p.g, 200, 20);
	}

	public void keyPressed() {
		super.keyPressed();

		String message  = str( key );	// the message to send
//		String ip       = "10.0.1.138";	// the remote IP address
		String ip       = "127.0.0.1";	// the (simulated) remote IP address

		udp.send(message, ip, portRemote);
		
		// log it
		logOut.update(message);
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
	public void receive(byte[] data, String senderIp, int senderPort) {	// <-- extended handler
		// convert byte data to string
		String message = new String(data);

		// print the result
		P.println("receive:", message, "from", senderIp, "on port", senderPort);
		
		// log it
		logIn.update(message);
	}
}