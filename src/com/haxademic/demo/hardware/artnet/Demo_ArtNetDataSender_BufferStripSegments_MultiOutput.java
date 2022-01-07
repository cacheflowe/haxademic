package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.hardware.dmx.artnet.LightStripBuffer;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class Demo_ArtNetDataSender_BufferStripSegments_MultiOutput
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetDataSender artNetDataSender1;
	protected ArtNetDataSender artNetDataSender2;
	protected int numPixels = 600;
	protected PGraphics debugPG;
	protected PGraphics debugPG2;
	protected LightStripBuffer[] buffers;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame() {
		// build artnet obj
		artNetDataSender1 = new ArtNetDataSender("192.168.1.100", 0, numPixels);
		artNetDataSender2 = new ArtNetDataSender("192.168.1.100", 4, numPixels);
		
		// build debug buffer for visualizing artnet data array
		debugPG = PG.newPG(128, 128);
		debugPG2 = PG.newPG(128, 128);
		DebugView.setTexture("debugPG", debugPG);
		DebugView.setTexture("debugPG2", debugPG2);
		
		// build buffers
		buffers = new LightStripBuffer[] {
			new LightStripBuffer(artNetDataSender1, 100, 0, 99),
			new BufferCustom1(artNetDataSender1, 4, 170, 173),
//			new BufferCustom2(artNetDataSender2, 4, 180, 183),
			new BufferCustom2(artNetDataSender2, 100, 0, 99),
		};
	}

	protected void drawApp() {
		background(0);
		setColors();
		artNetDataSender1.send();
		artNetDataSender1.drawDebug(debugPG, true);
		artNetDataSender2.send();
		artNetDataSender2.drawDebug(debugPG2, true);
	}
	
	protected void setColors() {
		// set all to black, then let buffers update their zones
		for(int i=0; i < numPixels; i++) artNetDataSender1.setColorAtIndex(i, 0, 0, 0);
		for(int i=0; i < numPixels; i++) artNetDataSender2.setColorAtIndex(i, 0, 0, 0);
		for (int i = 0; i < buffers.length; i++) buffers[i].draw();
		for (int i = 0; i < buffers.length; i++) buffers[i].setData();
		for (int i = 0; i < buffers.length; i++) p.image(buffers[i].buffer(), 300, 30 + 30 * i, buffers[i].buffer().width, 10);
	}
	
	public class BufferCustom1 extends LightStripBuffer {
		
		public BufferCustom1(ArtNetDataSender artNetDataSender, int width, int indexStart, int indexEnd) { super(artNetDataSender, width, indexStart, indexEnd); }
		
		public void drawCustom() {
			for (int x = 0; x < this.width; x++) {
				int oddEven = (p.frameCount % 60 < 30) ? 0 : 1;
				if(x % 2 == oddEven) {
//					buffer.fill(0, 20 + 100 * P.sin(FrameLoop.count(-0.05f) + -indexStart*0.33f + x), 0);
					buffer.fill(0, 80, 0);
					buffer.rect(x, 0, 1, buffer.height);
				}
			}
		}
	}
	
	public class BufferCustom2 extends LightStripBuffer {
		
		public BufferCustom2(ArtNetDataSender artNetDataSender, int width, int indexStart, int indexEnd) { super(artNetDataSender, width, indexStart, indexEnd); }
		
		public void drawCustom() {
			for (int x = 0; x < this.width; x++) {
				float dashFreq = 0.95f;
				float col = 20 + 50 * P.sin(FrameLoop.count(0.25f) + indexStart + x*dashFreq);
				buffer.fill(col, 0, col);
				buffer.rect(x, 0, 1, buffer.height);
			}
		}
	}

}