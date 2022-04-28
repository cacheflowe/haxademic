package com.haxademic.core.hardware.dmx.editor;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.dmx.DMXFixture.DMXMode;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PVector;

public class LightPoint
extends BaseLight
implements ILight {
	
	protected PVector point;
	
	public LightPoint(DMXUniverse universe, DMXMode dmxMode, PVector point1) {
		super(universe, dmxMode);
		this.point = point1;
	}
	
	public void setActive(PVector activePoint) {
		if(point == activePoint) active = true;
		else active = false;
	}
	
	public PVector point() {
		return point;
	}
	
	public void sampleColorTexture(PGraphics pgUI, PGraphics textureMap) {
		super.sampleColorTexture(pgUI, textureMap, point);
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
		pg.fill(dmxFixture.color().colorIntRGB());
		pg.push();
		pg.translate(point.x, point.y);
		pg.ellipse(0, 0, 30, 30);
		pg.pop();
		
		// show text labels overlay
		if(UI.valueToggle(DMXEditor.SHOW_DMX_CHANNELS)) {
			drawNumberValue(pg, dmxChannel);
		}
		if(UI.valueToggle(DMXEditor.SHOW_LIGHT_INDEX)) {
			drawNumberValue(pg, index);
		}
	}
	
	protected void drawNumberValue(PGraphics pg, int val) {
		super.drawNumberValue(pg, val, point);
	}
	
	public String toSaveString() {
		return 
			this.getClass().getSimpleName() + "," +
			dmxChannel + "," +
			dmxMode().name() + "," +
			P.round(point.x) + "," + 
			P.round(point.y);
	}
	
}
