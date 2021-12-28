package com.haxademic.core.hardware.dmx.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

public class MappedLightStrip {
	
	protected int numLights;
	protected int artNetIndexStart;
	protected PVector point1;
	protected PVector point2;
	protected PVector midPoint;
	protected int dmxChannel;
	
	protected boolean active = false;
	
	public MappedLightStrip(int numLights, int artNetIndexStart, PVector point1, PVector point2) {
		this.numLights = numLights;
		this.artNetIndexStart = artNetIndexStart;
		this.point1 = point1;
		this.point2 = point2;
		midPoint = new PVector();
	}
	
	// public
	
	public int numLights() {
		return numLights;
	}
	
	public int artNetIndexStart() {
		return artNetIndexStart;
	}
	
	public PVector point1() {
		return point1;
	}
	
	public PVector point2() {
		return point2;
	}
	
	// UI
	
	public void setActive(PVector activePoint) {
		if(point1 == activePoint || activePoint == point2) active = true;
		else active = false;
	}
	
	public boolean isActive() {
		return active;
	}

	// set colors on artnet object
	
	public void sampleColorTexture(PGraphics pgUI, PGraphics textureMap) {
		// sample LED texture from midpoint of 2 light end points
		int sampleX = (int) P.map(midPoint.x, 0, pgUI.width, 0, textureMap.width);
		int sampleY = (int) P.map(midPoint.y, 0, pgUI.height, 0, textureMap.height);
		int color = ImageUtil.getPixelColor(textureMap, sampleX, sampleY);
		
		// lerp towards map color
//		dmxFixture.color().setTargetInt(color);
//		dmxFixture.color().update();
	}
	
	public void update(PGraphics pg, PGraphics map, ArtNetDataSender artNet) {
		// update midpoint
		midPoint.set(point1);
		midPoint.lerp(point2, 0.5f);
				
		// draw background line
		pg.push();
		pg.stroke(0, 180);
		pg.strokeWeight(7);
		pg.line(point1.x, point1.y, point2.x, point2.y);
		pg.pop();

		// draw
		if(active) {
			// TODO: add raindbow test mode on hover
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
//			dmxFixture.color().setCurrentInt((P.p.frameCount % 16 < 8) ? rainbow : P.p.color(0));
			// dmxFixture.color().setCurrentInt(0xffff0000);
		}
		
		// draw light points
		pg.noStroke();
		pg.push();
		int startIndex = artNetIndexStart;
		for (int i = 0; i < numLights; i++) {
			// interpolate coordinates between points
			float stripProgress = (float)i/numLights;
			float pixelX = P.lerp(point1().x, point2().x, stripProgress); 
			float pixelY = P.lerp(point1().y, point2().y, stripProgress); 
			
			// get color
			int pixelColor = ImageUtil.getPixelColor(map, (int) pixelX, (int) pixelY);
			
			// set rgb colors
			float r = P.p.red(pixelColor);
			float g = P.p.green(pixelColor);
			float b = P.p.blue(pixelColor);
			
			// draw to screen
			pg.fill(r, g, b);
			pg.rect(pixelX, pixelY, 3, 3);

			// set data
			int pixelIndex = i;
			artNet.setColorAtIndex(startIndex + pixelIndex, r, g, b);
		}
		pg.pop();
		
		// small circular ends
		pg.ellipse(point1.x, point1.y, 3, 3);
		pg.ellipse(point2.x, point2.y, 3, 3);
		
		// show text labels overlay
		/*
		if(P.store.getBoolean(DMXEditor.SHOW_DMX_CHANNELS)) {
			drawNumberValue(pg, dmxChannel);
		}
		if(P.store.getBoolean(DMXEditor.SHOW_LIGHT_INDEX)) {
			drawNumberValue(pg, index);
		}
		*/
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
