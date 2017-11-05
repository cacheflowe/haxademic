package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
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
	}

	public void setup() {
		super.setup();	
	}
	
	protected void firstFrameSetup() {
		// build obj PShape and scale to window
		obj = DemoAssets.objSkeleton();
		float objHeight = p.height * 0.95f;
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToExtent(obj, objHeight);
	}

	public void drawApp() {
		if(p.frameCount == 1) firstFrameSetup();
		
		background(0);
		p.translate(p.width/2f, p.height/2f, -width*1.5f);
		DrawUtil.setBetterLights(p);
		p.rotateY(P.map(p.mouseX, 0, p.width, -1f, 1f));
		
		// twist it
		PShapeUtil.verticalTwistShape(obj,  0.002f, P.cos(p.frameCount * 0.01f) * 20);

		// draw mesh 
		p.shape(obj);
	}
		
}