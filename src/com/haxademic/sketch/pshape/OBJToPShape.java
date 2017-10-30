package com.haxademic.sketch.pshape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;

public class OBJToPShape 
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
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
	}

	public void setup() {
		super.setup();	
	}
	
	protected void firstFrameSetup() {
		// load texture
//		img = p.loadImage(FileUtil.getFile("images/jupiter-360.jpg"));
//		img = p.loadImage(FileUtil.getFile("images/nasa-01.jpg"));
		img = p.loadImage(FileUtil.getFile("images/pluto-chandra.jpg"));
//		img = p.loadImage(FileUtil.getFile("images/usa.png"));
		
		// build obj PShape and scale to window
//		obj = p.loadShape( FileUtil.getFile("models/poly-hole-square.obj"));	
		obj = p.loadShape( FileUtil.getFile("models/skull-realistic.obj"));	
//		obj = p.loadShape( FileUtil.getFile("models/man-lowpoly.obj"));	
//		obj = p.loadShape( FileUtil.getFile("models/lego-man.obj"));	
//		obj = p.loadShape( FileUtil.getFile("models/bomb.obj"));	
//		obj = obj.getTessellation();
//		obj = p.loadShape( FileUtil.getFile("models/poly-hole-tri.obj"));	
		PShapeUtil.centerSvg(obj);
		PShapeUtil.scaleObjToExtentVerticesAdjust(obj, p.height * 0.8f);
		
		// add UV coordinates to OBJ based on model extents
		float modelExtent = PShapeUtil.getObjMaxExtent(obj);
		modelHeight = PShapeUtil.getObjHeight(obj);
		P.println("modelExtent", modelExtent);
		P.println("getObjHeight", modelHeight);
		if(useTexture) 
//			PShapeUtil.addTextureUVToObj(obj, img, modelExtent, true);
			PShapeUtil.addTextureUVSpherical(obj, img);
		
		// build solid, deformable PShape object
		objSolid = new PShapeSolid(obj);
	}

	public void drawApp() {
		if(p.frameCount == 1) firstFrameSetup();
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		p.pushMatrix();
		background(0);
		DrawUtil.setBetterLights(p);
		DrawUtil.setDrawCenter(p);
		
		// rotate
		p.translate(p.width/2f, p.height/2f, -width*1.5f);
		
		p.rotateY(P.HALF_PI);
		p.rotateZ(P.PI);
		p.rotateY(0.4f * P.sin(percentComplete * P.TWO_PI)); // -P.HALF_PI +
//		p.rotateY(percentComplete * P.TWO_PI); // -P.HALF_PI + 
		p.rotateY(P.map(p.mouseX, 0, p.width, -1f, 1f));
//		p.rotateX(-P.PI/2f);

		
		// deform
//		objSolid.updateWithTrig(true, percentComplete, 0.35f, 5.4f);
//		objSolid.deformWithAudio();
//		objSolid.deformWithAudioByNormals();

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
		p.translate(0, -modelHeight/2, 0);
		p.rotateX(P.HALF_PI);
		p.fill(80);
		p.rect(0, 0, modelHeight * 2, modelHeight * 2);
		
		p.popMatrix();
	}
		
}