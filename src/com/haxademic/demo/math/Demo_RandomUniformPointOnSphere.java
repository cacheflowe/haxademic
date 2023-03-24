package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.math.SphericalCoord;

import processing.core.PShape;
import processing.core.PVector;

public class Demo_RandomUniformPointOnSphere
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PShape shapeGroup;
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		shapeGroup = p.createShape(P.GROUP);
//		while(shapeGroup.getChildCount() < 500) {
		for(int i=0; i < 500; i++) {
			PVector randPoint = SphericalCoord.randomUniformCoord(p.height * 0.3f, true);
			float shapeSize = 20;
			shapeGroup.addChild(PShapeUtil.createSphere(shapeSize, randPoint.x, randPoint.y, randPoint.z, ColorsHax.colorFromGroupAt(0, i), 0, 0));
		}
		DebugView.setValue("shapeGroup.getChildCount()", shapeGroup.getChildCount());
	}
	
	protected void drawApp() {
		// fade 
		if(p.frameCount == 1) background(0);
		BrightnessStepFilter.instance().setBrightnessStep(-30/255f);
		BrightnessStepFilter.instance().applyTo(p);
		PG.setDrawCorner(p);
		p.noStroke();
		PG.setCenterScreen(p);
		PG.setBetterLights(p.g);
		PG.basicCameraFromMouse(p.g);

		// draw to screen
		p.shape(shapeGroup);
	}
}
