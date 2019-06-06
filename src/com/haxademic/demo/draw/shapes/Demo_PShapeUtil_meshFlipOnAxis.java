package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;

public class Demo_PShapeUtil_meshFlipOnAxis 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected float modelHeight;
	
	protected void setupFirstFrame() {
		// build obj PShape and scale to window
		obj = DemoAssets.objHumanoid();
		float objHeight = p.height * 0.7f;
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToHeight(obj, objHeight);
	}

	public void drawApp() {
		background(0);
		p.translate(p.width/2f, p.height/2f, 0);
		PG.setBetterLights(p);

		// rotate mesh vertices
		if(p.frameCount % 100 == 0) {
			PShapeUtil.meshFlipOnAxis(obj, P.Y);
		}
		
		// draw mesh 
		p.shape(obj);
	}
		
}