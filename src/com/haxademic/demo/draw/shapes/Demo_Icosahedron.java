package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_Icosahedron 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PShape shapeTessellated;
	protected PShape shapeIcos;
	protected PImage img;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, 160 );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void setup() {
		super.setup();	

		PImage img = DemoAssets.textureJupiter();
		
		p.sphereDetail(40);
		shape = p.createShape(P.SPHERE, p.width/10f);
		shapeTessellated = shape.getTessellation();
		
		float extent = PShapeUtil.getMaxExtent(shape);
		
		shape.setTexture(img);
		shapeTessellated.setTexture(img);
		
		shapeIcos = Icosahedron.createIcosahedron(p.g, 4, img);
		PShapeUtil.scaleShapeToExtent(shapeIcos, extent);
		
		PShapeUtil.addUVsToPShape_DEPRECATE(shape, extent);
		PShapeUtil.addUVsToPShape_DEPRECATE(shapeTessellated, extent);
	}

	public void drawApp() {
		background(0);
		
		// setup lights
		DrawUtil.setBetterLights(p);
		
		// icosahedron
		p.pushMatrix();
		p.translate(p.width/2f, p.height/4f);
		p.rotateY(loop.progressRads());
		p.shape(shapeIcos);
		p.popMatrix();

		// original
		p.pushMatrix();
		p.translate(p.width/4f, p.height/1.5f);
		p.rotateY(-loop.progressRads());
		p.shape(shape);
		p.popMatrix();

		// tessellated
		p.pushMatrix();
		p.translate(p.width - p.width/4f, p.height/1.5f);
		p.rotateY(loop.progressRads());
		p.rotateZ(P.PI);
		p.shape(shapeTessellated);
		p.popMatrix();
	}
		
}