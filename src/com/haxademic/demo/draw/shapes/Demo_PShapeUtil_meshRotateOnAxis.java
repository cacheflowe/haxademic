package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;

public class Demo_PShapeUtil_meshRotateOnAxis 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected float modelHeight;
	
	protected void firstFrame() {
		// build obj PShape and scale to window
		obj = DemoAssets.objSkullRealistic();
		float objHeight = p.height * 0.95f;
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToExtent(obj, objHeight);
		
		// replace with a points version
		obj = PShapeUtil.meshShapeToPointsShape(obj);
	}

	protected void drawApp() {
		background(0);
		p.translate(p.width/2f, p.height/2f, -width*1.5f);
		PG.setBetterLights(p);

		// rotate mesh vertices
		PShapeUtil.meshRotateOnAxis(obj, 0.01f, P.X);
		
		// draw mesh 
		p.shape(obj);
	}
		
}