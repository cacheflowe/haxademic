package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;

import processing.core.PShape;

public class Demo_PShapeUtil_shapeFromImage 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PShape shape2;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1200 );
		Config.setProperty( AppSettings.HEIGHT, 900 );
	}
	
	protected void firstFrame() {
//		shape = PShapeUtil.shapeFromImage(DemoAssets.textureCursor());
//		shape = PShapeUtil.shapeFromImage(P.getImage("images/_sketch/pixel-objects/power-glove.png"));
//		shape = PShapeUtil.shapeFromImage(P.getImage("images/_sketch/pixel-objects/hand-peace.png"));
//		shape = PShapeUtil.shapeFromImage(P.getImage("images/_sketch/pixel-objects/mario-mushroom.png"));
//		shape = PShapeUtil.shapeFromImage(P.getImage("images/_sketch/pixel-objects/mario-cloud-smile.png"));
		shape = PShapeUtil.shapeFromImage(P.getImage("images/_sketch/pixel-objects/skull-mini.png"));
		shape2 = PShapeUtil.shapeFromImage(P.getImage("images/_sketch/pixel-objects/sunglasses-deal-with-it.png"));
		
		PShapeUtil.scaleVertices(shape, 40, 40, 80);
//		PShapeUtil.scaleShapeToWidth(shape, p.height * 0.2f);
//		PShapeUtil.scaleShapeToWidth(shape, p.height * 0.01f * 40);

		PShapeUtil.scaleVertices(shape2, 40, 40, 80);
//		PShapeUtil.scaleShapeToWidth(shape2, p.height * 0.01f * 40);
	}
	
	protected void drawApp() {
		p.background(200, 255, 200);
		PG.setCenterScreen(p);
		PG.setBetterLights(p);
		PG.basicCameraFromMouse(p.g);
		p.ortho();

		// draw
		p.shape(shape);
		
		p.translate(0, 0, 80);
		p.shape(shape2);
		
	}
}