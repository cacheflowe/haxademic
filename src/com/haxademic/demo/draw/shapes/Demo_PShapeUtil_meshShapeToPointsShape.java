package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;

public class Demo_PShapeUtil_meshShapeToPointsShape 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected float modelHeight;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, 120);
	}
	
	protected void setupFirstFrame() {
		// build obj PShape and scale to window
		obj = DemoAssets.objSkullRealistic();
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToExtent(obj, p.height * 0.8f);
		
		// replace with a points version
		obj = PShapeUtil.meshShapeToPointsShape(obj);
	}

	public void drawApp() {		
		background(0);
		PG.setBetterLights(p);
		
		// rotate
		p.translate(p.width/2f, p.height/2f, -width*1.5f);
		p.rotateY(0.4f * P.sin(loop.progressRads())); /// -P.HALF_PI +

		// draw mesh with texture or without
		obj.disableStyle();
		p.stroke(0, 255, 0);
		p.shape(obj);
	}
		
}