package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_GroupOfBoxesOrthoUVTexture 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	int FRAMES = 600;
	protected PShape shape;
	protected PGraphics texture;
	protected LinearFloat rotX = new LinearFloat(0, 0.004f);
	protected LinearFloat rotY = new LinearFloat(0, 0.004f);
	protected float rotXLast = 0;
	protected float rotYLast = 0;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 768);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void setupFirstFrame() {
		// make texture
		texture = PG.newPG(p.width * 2, p.height * 2);
//		PG.drawGrid(texture, 0xff000000, 0xffffffff, p.width / 21, p.height / 15);
		PG.drawGrid(texture, 0xff000000, 0xffffffff, p.width / 16, p.height / 12, 3);
		
		// build boxes
		shape = p.createShape(P.GROUP);
		for (int y = 0; y < 220; y++) {
			PShape subShape = Shapes.createBox(MathUtil.randRangeDecimal(40, 150));
			subShape.translate(
							MathUtil.randRangeDecimal(-p.width/1.5f, p.width/1.5f),
							MathUtil.randRangeDecimal(-p.height/1.5f, p.height/1.5f),	
							MathUtil.randRangeDecimal(-150, 150)
							);
			subShape.rotateX(MathUtil.randRangeDecimal(-P.PI, P.PI));
			subShape.rotateY(MathUtil.randRangeDecimal(-P.PI, P.PI));
			shape.addChild(subShape);
		}

		// normalize & prep mesh texture
		shape = shape.getTessellation();
		PShapeUtil.centerShape(shape);
		PShapeUtil.addTextureUVToShape(shape, texture);
		shape.setTexture(texture);
		p.debugView.setValue("shape.getVertexCount();", PShapeUtil.vertexCount(shape));
	}

	public void drawApp() {
		p.background(0);
		p.push();
		p.ortho();

				
		// set context & camera
		PG.setCenterScreen(p);
		p.g.translate(0, 0, -p.width * 1.3f);
		
//		PG.basicCameraFromMouse(p.g);
		// rotate!
		if(p.loop.loopCurFrame() == 1) {
			rotX.setCurrent(0);
			rotY.setCurrent(0);
			rotX.setTarget(1);
			rotY.setTarget(1);
		} else if(p.loop.loopCurFrame() == 300) {
			rotX.setCurrent(0);
			rotY.setCurrent(0);
			rotX.setTarget(0);
			rotY.setTarget(1);
		}
		rotX.update(true);
		rotY.update(true);
		float easedRotX = Penner.easeInOutCubic(rotX.value());
		float easedRotY = Penner.easeInOutCubic(rotY.value());
		p.rotateX(P.PI * easedRotX);
		p.rotateY(P.PI * easedRotY);
		
		// draw mesh
		p.fill(255);
		p.shape(shape);
		p.pop();
		
		// debug 
		if(Mouse.xNorm > 0.9f) p.image(texture, 0, 0);
	}
		
}