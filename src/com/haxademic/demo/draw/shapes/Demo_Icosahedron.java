package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_Icosahedron 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PShape shapeTessellated;
	protected PShape shapeIcos;
	protected PImage img;

	protected void config() {
		Config.setProperty( AppSettings.LOOP_FRAMES, 160 );
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void firstFrame() {
	

		PImage img = DemoAssets.textureJupiter();
		
		p.sphereDetail(40);
		shape = p.createShape(P.SPHERE, p.width/10f);
		shapeTessellated = shape.getTessellation();
		
		float extent = PShapeUtil.getMaxExtent(shape);
		
		shape.setTexture(img);
		shapeTessellated.setTexture(img);
		
		shapeIcos = Icosahedron.createIcosahedron(p.g, 4, img);
		PShapeUtil.scaleShapeToExtent(shapeIcos, extent);
		
		PShapeUtil.addTextureUVSpherical(shape, img);
		PShapeUtil.addTextureUVToShape(shapeTessellated, img);
	}

	public void drawApp() {
		background(0);
		
		// setup lights
		PG.setBetterLights(p);
		
		// icosahedron
		p.pushMatrix();
		p.translate(p.width/2f, p.height/4f);
		p.rotateY(FrameLoop.progressRads());
		p.shape(shapeIcos);
		p.popMatrix();

		// original
		p.pushMatrix();
		p.translate(p.width/4f, p.height/1.5f);
		p.rotateY(-FrameLoop.progressRads());
		p.shape(shape);
		p.popMatrix();

		// tessellated
		p.pushMatrix();
		p.translate(p.width - p.width/4f, p.height/1.5f);
		p.rotateY(FrameLoop.progressRads());
		p.rotateZ(P.PI);
		p.shape(shapeTessellated);
		p.popMatrix();
	}
		
}