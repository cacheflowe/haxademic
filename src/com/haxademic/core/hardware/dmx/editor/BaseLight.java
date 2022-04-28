package com.haxademic.core.hardware.dmx.editor;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXFixture.DMXMode;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

public class BaseLight {

	protected int dmxChannel;
	protected DMXFixture dmxFixture;
	protected boolean active = false;

	public BaseLight(DMXUniverse universe, DMXMode dmxMode) {
		dmxFixture = (new DMXFixture(universe, 1, dmxMode)).setEaseFactor(0.75f);
	}
	
	public void setDmxChannel(int channel) {
		dmxChannel = channel;
		dmxFixture.dmxChannel(channel);
	}
	
	public int dmxChannel() {
		return dmxChannel;
	}
	
	public DMXMode dmxMode() {
		return dmxFixture.mode();
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void sampleColorTexture(PGraphics pgUI, PGraphics textureMap, PVector samplePoint) {
		// sample LED texture from midpoint of 2 light end points
		int sampleX = (int) P.map(samplePoint.x, 0, pgUI.width, 0, textureMap.width);
		int sampleY = (int) P.map(samplePoint.y, 0, pgUI.height, 0, textureMap.height);
		int color = ImageUtil.getPixelColor(textureMap, sampleX, sampleY);
		
		// lerp towards map color
		dmxFixture.color().setTargetInt(color);
		dmxFixture.color().setTargetA(P.p.brightness(color));
		dmxFixture.color().update();
	}

	protected void drawNumberValue(PGraphics pg, int val, PVector location) {
		// channel info text
		pg.push();
		pg.translate(0, 0);
		pg.fill(255);
		pg.noStroke();
		float ellipseW = (val > 99) ? 30 : 20;	// make wider for 3 digits
		pg.ellipse(location.x, location.y, ellipseW, 20);

		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 12);
		FontCacher.setFontOnContext(pg, font, P.p.color(0), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		pg.text(val+"", location.x, location.y - 2, 30, 20);
		pg.pop();
	}

}
