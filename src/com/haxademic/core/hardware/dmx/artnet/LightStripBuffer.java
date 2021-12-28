package com.haxademic.core.hardware.dmx.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class LightStripBuffer {
	
	protected ArtNetDataSender artNetDataSender;
	protected int width;
	protected int indexStart;
	protected int indexEnd;
	protected PGraphics buffer;

	public LightStripBuffer(ArtNetDataSender artNetDataSender, int width, int indexStart, int indexEnd) {
		this(artNetDataSender, width, indexStart, indexEnd, 4);
	}
	
	public LightStripBuffer(ArtNetDataSender artNetDataSender, int width, int indexStart, int indexEnd, int bufferH) {
		this.artNetDataSender = artNetDataSender;
		this.width = width;
		this.indexStart = indexStart;
		this.indexEnd = indexEnd;

		this.buffer = PG.newDataPG(width, bufferH);
		DebugView.setTexture("buffer_"+indexStart, buffer);
	}

	public PGraphics buffer() {
		return buffer;
	}

	public void drawCustom() {
		for (int x = 0; x < this.width; x++) {
			float dashFreq = 0.5f;
			buffer.fill(
				20 + 100 * P.sin(FrameLoop.count(0.175f) + indexStart + x*dashFreq),
				20 + 100 * P.sin(FrameLoop.count(0.150f) + indexStart + x*dashFreq),
				20 + 100 * P.sin(FrameLoop.count(0.195f) + indexStart + x*dashFreq)
			);
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
			//					P.out(bufferXIndex);;
			int pixelColor = ImageUtil.getPixelColor(buffer, bufferXIndex, 0);
			artNetDataSender.setColorAtIndex(i, ColorUtil.redFromColorInt(pixelColor), ColorUtil.greenFromColorInt(pixelColor), ColorUtil.blueFromColorInt(pixelColor));
		}
	}

}
