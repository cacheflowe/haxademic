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

public class LightPoint
implements ILight {
	
	protected PVector point;
	protected int dmxChannel;
	protected DMXFixture dmxFixture;
	
	protected boolean active = false;
	
	public LightPoint(DMXUniverse universe, DMXMode dmxMode, PVector point1) {
		dmxFixture = (new DMXFixture(universe, 1, dmxMode)).setEaseFactor(0.75f);
		this.point = point1;
	}
	
	public void setDmxChannel(int channel) {
		dmxChannel = channel;
		dmxFixture.dmxChannel(channel);
	}
	
	public int dmxChannel() {
		return dmxChannel;
	}
	
	public void setActive(PVector activePoint) {
		if(point == activePoint) active = true;
		else active = false;
	}
	
	public boolean isActive() {
		return active;
	}

	public PVector point() {
		return point;
	}
	
	public void sampleColorTexture(PGraphics pgUI, PGraphics textureMap) {
		// sample LED texture from midpoint of 2 light end points
		int sampleX = (int) P.map(point.x, 0, pgUI.width, 0, textureMap.width);
		int sampleY = (int) P.map(point.y, 0, pgUI.height, 0, textureMap.height);
		int color = ImageUtil.getPixelColor(textureMap, sampleX, sampleY);
		
		// lerp towards map color
		dmxFixture.color().setTargetInt(color);
		dmxFixture.color().update();
	}
	
	public void update(PGraphics pg, int index) {
		// draw
		if(active) {
			// draw enclosing circle to highlight tube
			float highlightSize = 50;
			pg.noFill();
			pg.strokeWeight(1);
			pg.stroke(0, 255, 0);
			pg.ellipse(point.x, point.y, highlightSize, highlightSize);
			
			// flash color
			int rainbow = P.p.color(
					127 + 127 * P.sin(P.p.frameCount * 0.1f),
					127 + 127 * P.sin(P.p.frameCount * 0.15f),
					127 + 127 * P.sin(P.p.frameCount * 0.225f));
			dmxFixture.color().setCurrentInt((P.p.frameCount % 16 < 8) ? rainbow : P.p.color(0));
			// dmxFixture.color().setCurrentInt(0xffff0000);
		}
		
		// draw light
		pg.noStroke();
		pg.fill(dmxFixture.color().colorInt());
		pg.push();
		pg.translate(point.x, point.y);
		pg.ellipse(point.x, point.y, 10, 10);
		pg.pop();
		
		// small circular ends
		pg.ellipse(point.x, point.y, 3, 3);
		
		// show text labels overlay
		if(P.store.getBoolean(DMXEditor.SHOW_DMX_CHANNELS)) {
			drawNumberValue(pg, dmxChannel);
		}
		if(P.store.getBoolean(DMXEditor.SHOW_LIGHT_INDEX)) {
			drawNumberValue(pg, index);
		}
	}
	
	protected void drawNumberValue(PGraphics pg, int val) {
		// channel info text
		pg.push();
		pg.translate(0, 0);
		pg.fill(255);
		pg.noStroke();
		float ellipseW = (val > 99) ? 30 : 20;	// make wider for 3 digits
		pg.ellipse(point.x, point.y, ellipseW, 20);

		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 12);
		FontCacher.setFontOnContext(pg, font, P.p.color(0), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		pg.text(val+"", point.x, point.y - 2, 30, 20);
		pg.pop();

	}
	
	public String toSaveString() {
		return P.round(point.x) + "," + 
			   P.round(point.y) + "," +
			   dmxChannel;
	}
	
}
