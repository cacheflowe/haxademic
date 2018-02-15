package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_PShapeUtil_OBJNormalizationAndTexturing 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected PShapeSolid objSolid;
	protected PImage img;
	protected boolean useTexture = true;
	protected float modelHeight;
	protected float _frames = 360;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
	}

	public void setup() {
		super.setup();	
	}
	
	protected void setupFirstFrame() {
		// load texture
		img = DemoAssets.squareTexture();
		
		// build obj PShape and scale to window
		obj = DemoAssets.objHumanoid();
		
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToExtent(obj, p.height * 0.8f);
		
		// add UV coordinates to OBJ based on model extents
		float modelExtent = PShapeUtil.getMaxExtent(obj);
		modelHeight = PShapeUtil.getMaxAbsY(obj);
		if(useTexture) 
			PShapeUtil.addTextureUVToShape(obj, img, modelExtent, true);
//			PShapeUtil.addTextureUVSpherical(obj, img);
		
		// build solid, deformable PShape object
		objSolid = new PShapeSolid(obj);
	}

	public void drawApp() {
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		background(0);
		DrawUtil.setBetterLights(p);
		DrawUtil.setDrawCenter(p);
		
		// rotate
		p.translate(p.width/2f, p.height/2f, -width*1.5f);
		p.rotateY(0.4f * P.sin(percentComplete * P.TWO_PI)); // -P.HALF_PI +

		
		// draw mesh with texture or without
		// if a texture is set, drawing with p.shape() is super slow, so we can manually draw by looping over vertices
		// if no texture, p.shape() is fine
		p.noStroke();
		if(useTexture) {
			// texture mapped with decent performance:
			PShapeUtil.drawTriangles(p.g, objSolid.shape(), img, 1f); // img			
		} else {
			// pshape drawing + audioreactive
			objSolid.setVertexColorWithAudio(255);
			p.shape(objSolid.shape());
		}
		
		// draw ground
		p.translate(0, modelHeight/2f, 0);
		p.rotateX(P.HALF_PI);
		p.fill(80);
		p.rect(0, 0, modelHeight, modelHeight);
	}
		
}