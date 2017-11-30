package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;

import processing.core.PShape;

public class Demo_PShapeUtil_verticalTwistShape 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected float modelHeight;
	protected float _frames = 360;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(1 + _frames) );
	}

	public void setup() {
		super.setup();	
	}
	
	protected void firstFrameSetup() {
		// build obj PShape and scale to window
		obj = DemoAssets.objSkullRealistic();
		float objHeight = p.height * 0.95f;
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToExtent(obj, objHeight);
	}

	public void drawApp() {
		if(p.frameCount == 1) firstFrameSetup();
		// progress
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radsComplete = percentComplete * P.TWO_PI;
		
		// set scene
		background(0);
		p.translate(p.width/2f, p.height/2f, -width*1.5f);
		
		// twist it
		p.rotateY(P.sin(-radsComplete));
		PShapeUtil.verticalTwistShape(obj,  0.002f, P.cos(radsComplete) * 20);

		// draw it - used drawTriangles() for good wireframe drawing
		p.stroke(0, 255, 0);
		p.strokeWeight(1);
		p.fill(0);
		PShapeUtil.drawTriangles(p.g, obj, null, 1);
	}
		
}