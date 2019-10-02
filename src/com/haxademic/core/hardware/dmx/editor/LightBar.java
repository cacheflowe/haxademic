package com.haxademic.core.hardware.dmx.editor;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXFixture.DMXMode;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

public class LightBar
implements ILight {
	
	protected PVector point1;
	protected PVector point2;
	protected PVector midPoint;
	protected int dmxChannel;
	protected DMXFixture dmxFixture;
	
	protected boolean active = false;
	
	public LightBar(DMXUniverse universe, DMXMode dmxMode, PVector point1, PVector point2) {
		dmxFixture = (new DMXFixture(universe, 1, dmxMode)).setEaseFactor(0.75f);
		this.point1 = point1;
		this.point2 = point2;
		midPoint = new PVector();
	}
	
	public void setDmxChannel(int channel) {
		dmxChannel = channel;
		dmxFixture.dmxChannel(channel);
	}
	
	public int dmxChannel() {
		return dmxChannel;
	}
	
	public void setActive(PVector activePoint) {
		if(point1 == activePoint || activePoint == point2) active = true;
		else active = false;
	}
	
	public boolean isActive() {
		return active;
	}

	public PVector point1() {
		return point1;
	}
	
	public PVector point2() {
		return point2;
	}
	
	public void sampleColorTexture(PGraphics pgUI, PGraphics textureMap) {
		// sample LED texture from midpoint of 2 light end points
		int sampleX = (int) P.map(midPoint.x, 0, pgUI.width, 0, textureMap.width);
		int sampleY = (int) P.map(midPoint.y, 0, pgUI.height, 0, textureMap.height);
		int color = ImageUtil.getPixelColor(textureMap, sampleX, sampleY);
		
		// lerp towards map color
		dmxFixture.color().setTargetInt(color);
		dmxFixture.color().update();
	}
	
	public void update(PGraphics pg, int index) {
		// update midpoint
		midPoint.set(point1);
		midPoint.lerp(point2, 0.5f);
		
		// draw
		if(active) {
			// draw enclosing circle to highlight tube
			float highlightSize = point1.dist(point2) + 50;
			pg.noFill();
			pg.strokeWeight(1);
			pg.stroke(0, 255, 0);
			pg.ellipse(midPoint.x, midPoint.y, highlightSize, highlightSize);
			
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
		pg.translate(midPoint.x, midPoint.y);
		pg.rotate(MathUtil.getRadiansToTarget(point2.x, point2.y, point1.x, point1.y));
		pg.rect(0, 0, point1.dist(point2), 5);
		pg.pop();
		
		// small circular ends
		pg.ellipse(point1.x, point1.y, 3, 3);
		pg.ellipse(point2.x, point2.y, 3, 3);
		
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
		pg.ellipse(midPoint.x, midPoint.y, ellipseW, 20);

		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 12);
		FontCacher.setFontOnContext(pg, font, P.p.color(0), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		pg.text(val+"", midPoint.x, midPoint.y - 2, 30, 20);
		pg.pop();

	}
	
	public String toSaveString() {
		return P.round(point1.x) + "," + 
			   P.round(point1.y) + "," +
			   P.round(point2.x) + "," +
			   P.round(point2.y) + "," +
			   dmxChannel;
	}
	
}
