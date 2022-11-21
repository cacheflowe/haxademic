package com.haxademic.demo.hardware.artnet.sacn;

import java.io.IOException;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

public class Demo_SACNTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

    protected LiveOutput lo;
	protected OutputStreamer os;

    protected void firstFrame() {
		setLights();
	}
	
	protected void setLights() {
        try {
            // TODO code application logic here
            lo = new LiveOutput(2, "sACN4J testing");
            lo.setPriority((byte) 0x65);
            lo.setDMXVal(3, (byte) 0xFF);
            os = new OutputStreamer(lo);
            os.sendLiveOutput();

        } catch (IOException ex) {
//            Logger.getLogger(SACN4J.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidUniverseException ex) {
//            Logger.getLogger(SACN4J.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

	protected void drawApp() {
		for(int i=0; i < 100; i++) {
			// set data bytes
			int indx = i * 3;
            lo.setDMXVal(indx + 0, P.parseByte(127 + 127f * sin(0+(i/10f) + frameCount * 0.002f)));
            lo.setDMXVal(indx + 1, P.parseByte(127 + 127f * sin(0+(i/10f) + frameCount * 0.004f)));
            lo.setDMXVal(indx + 2, P.parseByte(127 + 127f * sin(0+(i/10f) + frameCount * 0.006f)));
		}
        try {
            os.sendLiveOutput();
        } catch (IOException e) { e.printStackTrace(); }
	}
}
