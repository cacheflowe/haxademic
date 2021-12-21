package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class Demo_ArtNetDataSender_BufferStripSegments
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetDataSender artNetDataSender;
	protected int numPixels = 600;
	protected PGraphics debugPG;
	protected BufferStrip[] buffers;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame() {
		// build artnet obj
		artNetDataSender = new ArtNetDataSender("192.168.1.100", 0, numPixels);
		
		// build debug buffer for visualizing artnet data array
		debugPG = PG.newPG(128, 128);
		DebugView.setTexture("debugPG", debugPG);
		
		// build buffers
		buffers = new BufferStrip[] {
			new BufferStrip(100, 0, 99),
			new BufferCustom1(4, 170, 173),
			new BufferCustom2(4, 180, 183),
		};
	}

	protected void drawApp() {
		background(0);
		setColors();
		artNetDataSender.send();
		artNetDataSender.drawDebug(debugPG, true);
	}
	
	protected void setColors() {
		// set all to black, then let buffers update their zones
		for(int i=0; i < numPixels; i++) artNetDataSender.setColorAtIndex(i, 0, 0, 0);
		for (int i = 0; i < buffers.length; i++) buffers[i].draw();
		for (int i = 0; i < buffers.length; i++) buffers[i].setData();
		for (int i = 0; i < buffers.length; i++) p.image(buffers[i].buffer(), 300, 30 + 30 * i, buffers[i].buffer().width, 10);
	}
	
	public class BufferStrip {
		
		protected PGraphics buffer;
		protected int width;
		protected int indexStart;
		protected int indexEnd;
		
		public BufferStrip(int width, int indexStart, int indexEnd) {
			this.buffer = PG.newDataPG(width, 4);
			DebugView.setTexture("buffer_"+indexStart, buffer);
			this.width = width;
			this.indexStart = indexStart;
			this.indexEnd = indexEnd;
		}
		
		public PGraphics buffer() {
			return buffer;
		}
		
		public void drawCustom() {
			for (int x = 0; x < this.width; x++) {
				float dashFreq = 0.5f;
				buffer.fill(0, 0, 20 + 100 * P.sin(FrameLoop.count(0.15f) + indexStart + x*dashFreq));
				buffer.rect(x, 0, 1, buffer.height);
			}
		}
		
		public void draw() {
			buffer.beginDraw();
			buffer.background(0);
			buffer.noStroke();
			drawCustom();
			buffer.endDraw();
			buffer.loadPixels();
		}
		
		public void setData() {
			for(int i=indexStart; i <= indexEnd; i++) {
				int bufferXIndex = P.round(P.map(i, indexStart, indexEnd, 0, this.width - 1)); 
//				P.out(bufferXIndex);;
				int pixelColor = ImageUtil.getPixelColor(buffer, bufferXIndex, 0);
				artNetDataSender.setColorAtIndex(i, ColorUtil.redFromColorInt(pixelColor), ColorUtil.greenFromColorInt(pixelColor), ColorUtil.blueFromColorInt(pixelColor));
			}
		}
	}
	
	public class BufferCustom1 extends BufferStrip {
		
		public BufferCustom1(int width, int indexStart, int indexEnd) { super(width, indexStart, indexEnd); }
		
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
	
	public class BufferCustom2 extends BufferStrip {
		
		public BufferCustom2(int width, int indexStart, int indexEnd) { super(width, indexStart, indexEnd); }
		
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