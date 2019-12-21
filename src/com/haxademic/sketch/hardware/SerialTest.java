package com.haxademic.sketch.hardware;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;

import processing.serial.Serial;

public class SerialTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected Runnable _asyncDmxRequest;
	protected Thread _requestThread;
	protected boolean _isRequesting = false;
	
	// hardware connection
	protected String serialInputString = null;
	protected Serial serialPort;
	protected int serialPortIndex = 0;
	protected int baudRate = 9600;// 115200;    	// 115200, 57600, 38400
	protected int lf = 10;    			// Linefeed in ASCII - used by Arduino interface
	protected int cr = 13;    			// Carriage return in ASCII


	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void firstFrame() {

	}

	protected void printSerialDevices() {
		String[] ports = Serial.list();
		for (int i = 0; i < ports.length; i++) {
			P.println("Serial["+ i +"]", ports[i]);
		}
	}


	protected void initSerialDevice() {
		serialPort = new Serial(P.p, Serial.list()[serialPortIndex], baudRate); 
		serialPort.clear();
		// Throw out the first reading, in case we started reading 
		// in the middle of a string from the sender.
		serialInputString = serialPort.readStringUntil(cr);
		serialInputString = null;
	}

	protected void writeToSerial() {
//		serialPort.write("0xa5 0x68 0x32 ID 7B FF LL LH PO TP CC " + ". . . . . ." + " SH SL 0xae");
		serialPort.write(0xa5);		// Start code : The start of a packet
		serialPort.write(0x68); 	// Packet type : Recognition of this type of packet
		serialPort.write(0x32);		// Card type : Fixed Type Code
		serialPort.write(0x00);		// Card ID : "Control card ID, the screen No, valid values are as follows:
									// 				1 ~ 254: the specified card ID
									// 				0XFF: that group address, unconditionally receiving data"
		serialPort.write(0x7B);		// Protocol code : Recognition of this type of potocol
		serialPort.write(0xff);		// Additional information/ confirmation mark : The meaning of bytes in the packet is sent, "Additional Information", is a packet plus instructions, and now only use the lowest:
									// 				bit 0: whether to return a confirmation, 1 to return and 0 not to return
									// 				bit1 ~ bi7: reserved, set to 0
		serialPort.write(0x0000);	// Packed data length (LL LH)
		serialPort.write(0x00);		// Packet number (PO) 
		serialPort.write(0x00);		// Last packet number (TP)
		
		// send custom command
		serialPort.write(0x02);		// Packet data (CC...) : Command sub-code and data	
		serialPort.write(0x00);		// Window No	0x00~0x07	1	The window sequence number, valid values 0 ~ 7.
		serialPort.write(1);		// Mode	1	1	Refer to Special effect for text and picture
		serialPort.write(1);		// Alignment	0-20 "0: Left-aligned
		serialPort.write(10);		// Speed	1-100	1	The smaller the value, the faster
		serialPort.write(0x004d);	// String		Variable-length	"Every 3 bytes to represent a character. Refer to Rich3 text of Formatted text data format.
		serialPort.write(0x004d);	// String		Variable-length	"Every 3 bytes to represent a character. Refer to Rich3 text of Formatted text data format.
		serialPort.write(0x004d);	// String		Variable-length	"Every 3 bytes to represent a character. Refer to Rich3 text of Formatted text data format.
		// end custom command
//		asciiToHex("h")
		
		serialPort.write(0x0000);	// Packet data checksum (SH SL) : Two bytes, checksum. Lower byte in the forme. The sum of each byte from " Packet type " to â€œ Packet dataâ€� content		
		
		serialPort.write(0xae);		// End Code
	}
	
	protected String asciiToHex(String asciiValue){
	    char[] chars = asciiValue.toCharArray();
	    StringBuffer hex = new StringBuffer();
	    for (int i = 0; i < chars.length; i++) {
	        hex.append(Integer.toHexString((int) chars[i]));
	    }
	    return hex.toString();
	}
	
//	protected int convertToHexInt(int n) {
//		return Integer.valueOf(String.valueOf(n), 16);
//	}
	
//	protected int convertToHexInt(int r, int g, int b) {
//		return Integer.valueOf(String.format("%02x%02x%02x", r, g, b), 16);
//	}
	
	protected void readSerial() {
		while (serialPort.available() > 0) {
//			P.println("available!");
//			P.println(serialPort.read());
			String serialInputString = serialPort.readStringUntil(cr);
			if(serialInputString != null) {
				P.println("in: ", serialInputString.trim());
				serialPort.clear();
			}
		}
	}
	
	public void drawApp() {
		background(0);
		if(p.frameCount == 1) {
			printSerialDevices();
			initSerialDevice();
//			writeToSerial();
		}
		
		// debug serial state
		p.fill(255);
		text(serialPort.available(), 20, 20);

		// test draw to make sure light update requests don't hurt framerate
		PG.setDrawCenter(p);
		p.fill(255);
		p.stroke(127);
		p.strokeWeight(5);
		p.rect(p.frameCount % p.width, p.height * 0.5f, 100, 100);
		
		// send messages thorugh serial port
//		if(p.frameCount % 200 == 0) {
//			writeToSerial();
			readSerial();
//		}
	}
	
}

