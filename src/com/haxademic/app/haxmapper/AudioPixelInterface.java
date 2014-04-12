package com.haxademic.app.haxmapper;

import hypermedia.net.UDP;

import com.haxademic.core.draw.color.ColorHaxEasing;

import processing.core.PApplet;

public class AudioPixelInterface {
	
	public UDP _udp;

	public AudioPixelInterface() {
		_udp = new UDP( this, 6000, "224.0.0.1" );
		_udp.log( false ); 		// <-- printout the connection activity
		_udp.listen( true );
	}
	
	public void sendColorData( int[] colors ) {
		int curGroup = 0;
		int nodeCount = 72;
		byte[] rgbData = new byte[nodeCount * 3];
		int t = 0;
		for( int e=0; e < 6; e = e + 1 ) {
			int theColor = colors[curGroup % colors.length];
			t = e * 3;
			rgbData[t] = (byte) ColorHaxEasing.redFromColorInt(theColor);
			rgbData[t+1] = (byte) ColorHaxEasing.greenFromColorInt(theColor);
			rgbData[t+2] = (byte) ColorHaxEasing.blueFromColorInt(theColor);
			curGroup++;
		}
		broadcastKiNet("10.0.0.29", 30, rgbData, true);
		
		for( int e=7; e < nodeCount-7; e = e + 1 ) {
			int theColor = colors[curGroup % colors.length];
			t = e * 3;
			rgbData[t] = (byte) ColorHaxEasing.redFromColorInt(theColor);
			rgbData[t+1] = (byte) ColorHaxEasing.greenFromColorInt(theColor);
			rgbData[t+2] = (byte) ColorHaxEasing.blueFromColorInt(theColor);
			curGroup++;
		}
		broadcastKiNet("10.0.0.30", 30, rgbData, true);

	}

	
    public Boolean broadcastKiNet(String addy, int port, byte[] rbgvalues, boolean is150Server) {

        // when sending to strands cut the FPS in half
//        if(p.stateManager().getPortType(port) == HardwareManager.PORT_TYPE_KINET_2 && p.tick() % 2 == 1){
//            //return false; 
//            // !hack! TODO turn this back on - for some reason it was causing us to not be able to talk to the 2x8 tiles
//        }
        
        if (addy != "") {

            // Construct a UDP Packet with RGB
            int a = 24;
            int len = rbgvalues.length;
            if (is150Server) {
                a = 21;
            }
            // !CK Protocol
            byte[] data = new byte[a + len + 296];
            data[0] = (byte) (PApplet.unhex("04"));
            data[1] = (byte) (PApplet.unhex("01"));
            data[2] = (byte) (PApplet.unhex("dc"));
            data[3] = (byte) (PApplet.unhex("4a"));
            data[4] = (byte) (PApplet.unhex("01"));
            data[5] = (byte) (PApplet.unhex("00"));
            data[6] = (byte) (PApplet.unhex("08"));
            if (is150Server) {
                data[6] = (byte) (PApplet.unhex("01"));
            }
            data[7] = (byte) (PApplet.unhex("01"));
            data[8] = (byte) (PApplet.unhex("00"));
            data[9] = (byte) (PApplet.unhex("00"));
            data[10] = (byte) (PApplet.unhex("00"));
            data[11] = (byte) (PApplet.unhex("00"));
            data[12] = (byte) (PApplet.unhex("00"));
            data[13] = (byte) (PApplet.unhex("00"));
            data[14] = (byte) (PApplet.unhex("00"));
            data[15] = (byte) (PApplet.unhex("00"));
            String p1 = "";
            switch (port) {
            case 1:
                p1 = "01";
                break;
            case 2:
                p1 = "02";
                break;
            case 3:
                p1 = "03";
                break;
            case 4:
                p1 = "04";
                break;
            case 5:
                p1 = "05";
                break;
            case 6:
                p1 = "06";
                break;
            case 7:
                p1 = "07";
                break;
            case 8:
                p1 = "08";
                break;
            case 9:
                p1 = "09";
                break;
            case 10:
                p1 = "0A";
                break;
            case 11:
                p1 = "0B";
                break;
            case 12:
                p1 = "0C";
                break;
            case 13:
                p1 = "0D";
                break;
            case 14:
                p1 = "0E";
                break;
            case 15:
                p1 = "0F";
                break;
            case 16:
                p1 = "10";
                break;
            default:
                p1 = "ff";
                break;
            }
            data[16] = (byte) (PApplet.unhex(p1));
            if (is150Server) {
                data[17] = (byte) (PApplet.unhex("ff"));
                data[18] = (byte) (PApplet.unhex("ff"));
                data[19] = (byte) (PApplet.unhex("ff"));
                data[20] = (byte) (PApplet.unhex("00"));
            } else {
                data[17] = (byte) (PApplet.unhex("00"));
                data[18] = (byte) (PApplet.unhex("00"));
                data[19] = (byte) (PApplet.unhex("00"));
                data[20] = (byte) (PApplet.unhex("00"));
                data[21] = (byte) (PApplet.unhex("02"));
                data[22] = (byte) (PApplet.unhex("00"));
                data[23] = (byte) (PApplet.unhex("00"));
            }
            // RGB
            for (int i = 0; i < (len / 3); i = i + 1) {
                data[a] = rbgvalues[i * 3];
                data[a + 1] = rbgvalues[i * 3 + 1];
                data[a + 2] = rbgvalues[i * 3 + 2];
                a += 3;
            }
            return _udp.send(data, addy, 6038);
        }
        return false;
    }

}
