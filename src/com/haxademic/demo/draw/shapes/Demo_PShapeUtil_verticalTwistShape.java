package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;

public class Demo_PShapeUtil_verticalTwistShape 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected float modelHeight;
	protected int FRAMES = 360;

	protected void config() {
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES );
	}

	protected void firstFrame() {
		// build obj PShape and scale to window
		obj = DemoAssets.objSkullRealistic();
		float objHeight = p.height * 0.95f;
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToExtent(obj, objHeight);
	}

	protected void drawApp() {
		// set scene
		background(0);
		p.translate(p.width/2f, p.height/2f, -width*1.5f);
		
		// twist it
//		p.rotateY(P.sin(-AnimationLoop.progressRads()));
		PShapeUtil.verticalTwistShape(obj,  0.002f, P.cos(FrameLoop.progressRads()) * 20);

		// draw it - used drawTriangles() for good wireframe drawing
		p.stroke(0, 255, 0);
		p.strokeWeight(1);
		p.fill(0);
		PShapeUtil.drawTriangles(p.g, obj, null, 1);
	}
		
}