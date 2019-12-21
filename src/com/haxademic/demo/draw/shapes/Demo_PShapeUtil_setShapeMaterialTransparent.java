package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PShape;

public class Demo_PShapeUtil_setShapeMaterialTransparent 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected boolean overrideColor = false;

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 768);
		Config.setProperty(AppSettings.HEIGHT, 768);
	}
	
	protected void firstFrame() {
		// build shape and assign texture
		shape = p.loadShape(FileUtil.getFile("haxademic/models/pink-car.obj"));
		
		// normalize shape
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToWidth(shape, p.width * 0.4f);
		PShapeUtil.meshRotateOnAxis(shape, P.PI, P.Z);
		
		// inspect materials to set target color transparent
		// transparent face must be drawn last, and so might need to be rearranged in the obj file. 
		// ...Move said faces to the end of the file:
		// usemtl Light Cast
		// f 4/4/1 5/3/2 2/2/2 1/1/1
		PShapeUtil.setShapeMaterialTransparent(shape, 0.079f, 0.13f, 0.4f, 0.5f);
	}
			
	public void drawApp() {
		// clear the screen
		background(10);
		p.noStroke();
		PG.setBasicLights(p.g);

		// rotate camera
		p.translate(p.width/2, p.height/2);
		p.rotateX(P.map(p.mouseY, 0, p.height, 0.5f, -0.5f));
		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI * 2));
		
		// draw shape
		p.shape(shape);
	}
	
}