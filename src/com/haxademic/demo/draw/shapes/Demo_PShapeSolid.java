package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class Demo_PShapeSolid 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage texture;
	protected float shapeSize;

	protected PShapeSolid shapeSolidObj;
	protected PShapeSolid shapeSolidSphere;
	protected PShapeSolid shapeSolidIcosa;
	protected PShapeSolid[] shapes;

	protected void config() {
		Config.setProperty( AppSettings.LOOP_FRAMES, 60 );
		Config.setProperty( AppSettings.WIDTH, 1200 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void firstFrame() {
		// get texture
		texture = DemoAssets.textureJupiter();
		
		// set sizes
		shapeSize = p.height * 0.15f;
		
		// build 2 types of spherical shapes
		shapeSolidObj = PShapeSolid.newSolidObj(shapeSize, DemoAssets.objSkullRealistic(), texture);
		shapeSolidIcosa = PShapeSolid.newSolidIcos(shapeSize, texture);
		shapeSolidSphere = PShapeSolid.newSolidSphere(shapeSize, null);
		
		shapes = new PShapeSolid[] {
			shapeSolidObj,
			shapeSolidIcosa, 
			shapeSolidSphere
		};
	}
	
	public void drawApp() {
		// setup center screen & lights
		PG.setCenterScreen(p.g);
		PG.setBetterLights(p.g);
		background(0);
		p.noStroke();
		p.fill(255);
		
		// blending test 
		if(P.round(p.frameCount/50) % 2 == 1) {
			OpenGLUtil.setBlending(p.g, true);
			OpenGLUtil.setBlendMode(p.g, OpenGLUtil.Blend.ALPHA_REVEAL);
		} else {
			OpenGLUtil.setBlending(p.g, false);
			OpenGLUtil.setBlendMode(p.g, OpenGLUtil.Blend.DEFAULT);
		}
		
		// wireframe!
		if(P.round(p.frameCount/50) % 2 == 0) {
			OpenGLUtil.setWireframe(p.g, true);
		} else {
			OpenGLUtil.setWireframe(p.g, false);
		}

		// swap deform modes
		int deformMode = P.round(p.frameCount / 400) % 4;
		if(deformMode == 0) {
			for (PShapeSolid shape : shapes) shape.updateWithTrig(true, loop.progress() * 2f, 0.05f, 17.4f);
		} else if(deformMode == 1) {
			for (PShapeSolid shape : shapes) shape.deformWithAudio(1f);
		} else if(deformMode == 2) {
			for (PShapeSolid shape : shapes) shape.deformWithAudioByNormals(shapeSize * 0.9f);
		} else if(deformMode == 3) {
			for (PShapeSolid shape : shapes) shape.updateWithNoise(p.frameCount * 0.1f, 0.02f, 0.9f, 1, 0.1f);
		}

		// draw!
		p.translate(-300, 0, 0);
		PShapeUtil.drawTriangles(p.g, shapeSolidIcosa.shape(), texture, 1);
		p.translate(300, 0, 0);
		PShapeUtil.drawTriangles(p.g, shapeSolidObj.shape(), texture, 1);
		p.translate(300, 0, 0);
		PShapeUtil.drawTriangles(p.g, shapeSolidSphere.shape(), texture, 1);
	}
		
}