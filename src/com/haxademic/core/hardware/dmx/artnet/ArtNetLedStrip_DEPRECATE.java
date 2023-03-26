package com.haxademic.core.hardware.dmx.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.image.ImageUtil;

import ch.bildspur.artnet.ArtNetClient;
import processing.core.PGraphics;
import processing.core.PImage;

public class ArtNetLedStrip_DEPRECATE {
	
	public static ArtNetClient artnet;
	protected PGraphics buffer;
	protected String universeIp;
	protected int universeNum;
	protected int numLights;
	protected byte[] colorsData = new byte[512];

	public ArtNetLedStrip_DEPRECATE(String universeIp, int universeNum, int numLights) {
		this.universeIp = universeIp;
		this.universeNum = universeNum;
		this.numLights = numLights;
		if(artnet == null) {
			artnet = new ArtNetClient(null);
			artnet.start();
		}
		colorsData = new byte[numLights * 3];
		buffer = PG.newPG(numLights, 4, false, false);
		DebugView.setTexture("ArtNetLedStrip.buffer", buffer);
	}

	public void update(PImage img) {
		update(img, 1, 1);
	}

	public void update(PImage img, float brightness, float smoothing) {
		// copy frame - lerp it!
		BlendTowardsTexture.instance().setSourceTexture(img);
		BlendTowardsTexture.instance().setBlendLerp(smoothing);
		BlendTowardsTexture.instance().applyTo(buffer);
		ImageUtil.cropFillCopyImage(img, buffer, true);
		buffer.loadPixels();

		// sampling across buffer, write color bytes
		int y = buffer.height / 2;
		for (int i = 0; i < numLights; i++) {
			///////////////////////////
			// get pixel color from buffer
			// GRB format
			///////////////////////////
			int pixelColor = ImageUtil.getPixelColor(buffer, i, y);
			colorsData[i * 3 + 0] = P.parseByte(P.max(10, P.p.red(pixelColor) * brightness));
			colorsData[i * 3 + 1] = P.parseByte(P.max(10, P.p.green(pixelColor) * brightness));
			colorsData[i * 3 + 2] = P.parseByte(P.max(10, P.p.blue(pixelColor) * brightness));
		}
		// send dmx to localhost
		artnet.unicastDmx(universeIp, 0, 0, colorsData);
	}
}