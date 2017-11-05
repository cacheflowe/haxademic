package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_PShapeUtil_meshShapeToPointsShape 
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
		obj = DemoAssets.objSkullRealistic();
		float objHeight = p.height * 0.8f;
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToExtent(obj, objHeight);
		
		// replace with a points version
		obj = PShapeUtil.meshShapeToPointsShape(obj);
	}

	public void drawApp() {
		if(p.frameCount == 1) firstFrameSetup();
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		background(0);
		DrawUtil.setBetterLights(p);
		
		// rotate
		p.translate(p.width/2f, p.height/2f, -width*1.5f);
		p.rotateZ(P.PI);
		p.rotateY(0.4f * P.sin(percentComplete * P.TWO_PI)); // -P.HALF_PI +
		
		// draw mesh with texture or without
		obj.disableStyle();
		p.stroke(0, 255, 0);
		p.shape(obj);
	}
		
}