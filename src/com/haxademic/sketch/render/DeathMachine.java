package com.haxademic.sketch.render;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class DeathMachine 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape skullMesh;
	protected PShape gunMesh;
	protected PImage img;
	protected PGraphics gunTexture;
	protected TiledTexture gunTilingTexture;
	protected float _frames = 900;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 400 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
	}

	public void setup() {
		super.setup();	
	}
	
	protected void firstFrameSetup() {
		// load texture
		img = p.loadImage(FileUtil.getFile("images/las-vegas-victims-nbcnews.jpg"));
		gunTexture = p.createGraphics(img.width, img.width, P.P3D);
		gunTilingTexture = new TiledTexture(img);
		
		
		// build obj PShape and scale to window
		skullMesh = p.loadShape( FileUtil.getFile("models/skull-realistic.obj"));	
		gunMesh = p.loadShape( FileUtil.getFile("models/m4a1.obj"));	
		normalizeMesh(skullMesh, p.height * 0.03f, null);
		normalizeMesh(gunMesh, p.height, gunTexture);
	}
	
	protected void normalizeMesh(PShape s, float extent, PImage texture) {
		PShapeUtil.centerSvg(s);
		PShapeUtil.scaleObjToExtentVerticesAdjust(s, extent);
		
		// add UV coordinates to OBJ
		float modelExtent = PShapeUtil.getObjMaxExtent(s);
		P.println("modelExtent", modelExtent);
		if(texture != null) PShapeUtil.addTextureUVToObj(s, texture, modelExtent, false);
	}

	public void setBetterLights( PGraphics p ) {
		// setup lighting props
		p.ambient(127);
		p.lightSpecular(230, 230, 230); 
		p.directionalLight(200, 200, 200, -0.0f, -0.0f, 1); 
		p.directionalLight(200, 200, 200, 0.0f, 0.0f, -1); 
		p.specular(p.color(200)); 
		p.shininess(5.0f); 
	}

	public void drawApp() {
		if(p.frameCount == 1) firstFrameSetup();
		
		background(0);
		p.ortho();
		setBetterLights(p.g);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radsComplete = percentComplete * P.TWO_PI;
		
		// rotate
		float starXOffset = (p.width/7f + p.width/7f * P.sin(P.PI - radsComplete));
		float starYOffset = (p.height/12f + p.width/12f * P.sin(P.PI - radsComplete));
		p.translate(p.width/2f - starXOffset, p.height * 0.54f - starYOffset, -p.width * 0.6f);
		p.rotateY(0.35f - P.HALF_PI + 0.25f * P.sin(-0.4f + radsComplete));
		p.scale(0.6f + 2.2f + 2.2f * P.sin(radsComplete));
		
		// draw gun
		p.rotateZ(P.PI - 0.7f  + (0.2f + 0.2f * P.sin(radsComplete)));
		p.rotateX(0.4f  - (0.3f + 0.3f * P.sin(radsComplete)));
		
		p.pushMatrix();
		// update gun texture
		gunTexture.beginDraw();
		gunTexture.background(180);
		gunTexture.translate(gunTexture.width/2, gunTexture.height/2);
		gunTilingTexture.update();
//		gunTilingTexture.setRotation(p.frameCount * 0.001f);
		gunTilingTexture.setOffset(percentComplete, percentComplete);
//		float tileScale = 3f + 2.5f * P.sin(radsComplete);
//		gunTilingTexture.setSize(tileScale, tileScale);
		//gunTilingTexture.drawCentered(gunTexture, gunTexture.width, gunTexture.height);
		gunTexture.endDraw();
		
		// texture mapped with decent performance:
		p.noStroke();
		PShapeUtil.drawTrianglesWithTexture(p.g, gunMesh, gunTexture, 1f); // img
		
		p.translate(width, 0, 0);
		PShapeUtil.drawTrianglesWithTexture(p.g, gunMesh, gunTexture, 1f); // img
		p.popMatrix();
		
		
		// skull
		p.pushMatrix();
		float yOffset = -7; // P.map(p.mouseX, 0, p.width, -400, 400);
		float zOffset = -34; // P.map(p.mouseY, 0, p.height, -400, 400);
//		P.println(yOffset, zOffset);
		p.translate(-3000f - 3500f/2f * percentComplete, yOffset, zOffset);
//		float curZ = 0;
		for (int i = 0; i < 9000f; i+=70f) {
			p.pushMatrix();
			p.translate(i, 0, 0);
			p.rotateY(-P.HALF_PI);
			p.shape(skullMesh);
			p.popMatrix();
		}
//		p.rotateZ(P.PI);
//		p.rotateY(P.HALF_PI);
		
//		PShapeUtil.drawTrianglesWithTexture(p.g, skullMesh, gunTexture, 0.2f); // img
		p.popMatrix();
		
	}
		
}