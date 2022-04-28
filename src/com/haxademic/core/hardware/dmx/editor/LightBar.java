package com.haxademic.core.hardware.dmx.editor;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.dmx.DMXFixture.DMXMode;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PVector;

public class LightBar
extends BaseLight
implements ILight {
	
	protected PVector point1;
	protected PVector point2;
	protected PVector midPoint = new PVector();
	
	public LightBar(DMXUniverse universe, DMXMode dmxMode, PVector point1, PVector point2) {
		super(universe, dmxMode);
		this.point1 = point1;
		this.point2 = point2;
	}
	
	public void setActive(PVector activePoint) {
		if(point1 == activePoint || activePoint == point2) active = true;
		else active = false;
	}

	public PVector point1() {
		return point1;
	}
	
	public PVector point2() {
		return point2;
	}
	
	public void sampleColorTexture(PGraphics pgUI, PGraphics textureMap) {
		super.sampleColorTexture(pgUI, textureMap, midPoint);
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
			if(UI.valueToggle(DMXEditor.RAINBOW_ON_HOVER)) {
				int rainbow = P.p.color(
						127 + 127 * P.sin(P.p.frameCount * 0.1f),
						127 + 127 * P.sin(P.p.frameCount * 0.15f),
						127 + 127 * P.sin(P.p.frameCount * 0.225f));
				dmxFixture.color().setCurrentInt((P.p.frameCount % 16 < 8) ? rainbow : P.p.color(0));
			}
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
		if(UI.valueToggle(DMXEditor.SHOW_DMX_CHANNELS)) {
			drawNumberValue(pg, dmxChannel);
		}
		if(UI.valueToggle(DMXEditor.SHOW_LIGHT_INDEX)) {
			drawNumberValue(pg, index);
		}
	}
	
	protected void drawNumberValue(PGraphics pg, int val) {
		super.drawNumberValue(pg, val, midPoint);
	}
	
	public String toSaveString() {
		return
			this.getClass().getName() + "," +
			dmxChannel + "," +
			dmxMode().name() + "," +
			P.round(point1.x) + "," + 
			P.round(point1.y) + "," +
			P.round(point2.x) + "," +
			P.round(point2.y);
	}
	
}
