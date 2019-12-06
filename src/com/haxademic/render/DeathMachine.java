package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class DeathMachine 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape skullMesh;
	protected PShape gunMesh;
	protected PImage img;
//	protected PImage flag;
	protected PGraphics gunTexture;
	protected TiledTexture gunTilingTexture;
	protected TextToPShape textToPShape;
	protected PShape textAmerica;
	protected PShape textThe;
	protected PShape textIndefensible;
	protected float _frames = 900;
	


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames );
	}

	public void setupFirstFrame() {
	
	}
	
	protected void firstFrameSetup() {
		P.println("America the Indefensible");
		// load texture
//		flag = p.loadImage(FileUtil.getFile("images/usa.png"));
		img = p.loadImage(FileUtil.getFile("images/las-vegas-victims-nbcnews.png"));
		gunTexture = p.createGraphics(img.width * 3, img.width * 3, P.P3D);
		gunTexture.smooth(8);
		gunTilingTexture = new TiledTexture(img);
		
		
		// build obj PShape and scale to window
		skullMesh = p.loadShape( FileUtil.getFile("models/skull-realistic.obj"));	
		gunMesh = p.loadShape( FileUtil.getFile("models/m4a1.obj"));	
		normalizeMesh(skullMesh, p.height * 0.03f, null); // 
		normalizeMesh(gunMesh, p.height, gunTexture);
		
		// build texts
		float textDepth = 14f;
		textToPShape = new TextToPShape();
		String fontFile = FileUtil.getFile("fonts/AvantGarde-Book.ttf");
		textAmerica = textToPShape.stringToShape3d("AMERICA", textDepth, fontFile);
		PShapeUtil.scaleShapeToMaxAbsY(textAmerica, p.height * 0.068f);
		textThe = textToPShape.stringToShape3d("THE", textDepth, fontFile);
		PShapeUtil.scaleShapeToMaxAbsY(textThe, p.height * 0.05f);
		textIndefensible = textToPShape.stringToShape3d("INDEFENSIBLE", textDepth, fontFile);
		PShapeUtil.scaleShapeToMaxAbsY(textIndefensible, p.height * 0.055f);
		
		// smooth more
//		OpenGLUtil.setTextureQualityHigh(p.g);
	}
	
	protected void normalizeMesh(PShape s, float extent, PImage texture) {
		PShapeUtil.scaleShapeToExtent(s, extent);
		
		// add UV coordinates to OBJ
		float modelExtent = PShapeUtil.getMaxExtent(s);
		if(texture != null) PShapeUtil.addTextureUVToShape(s, texture, modelExtent, false);
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
		p.pushMatrix();
		if(p.frameCount == 1) firstFrameSetup();
		
		background(0);
		p.ortho();
		setBetterLights(p.g);
		
		// mouse control
//		float xmouse = P.map(P.p.mouseX, 0, p.width, -800f, 800f);
//		float ymouse = P.map(P.p.mouseY, 0, p.height, -800f, 800f);
//		P.p.debugView.addValue("xmouse", xmouse);
//		P.p.debugView.addValue("ymouse", ymouse);
		
		// loop progress
		float loopFrames = p.frameCount % _frames;
		float percentComplete = loopFrames / _frames;
//		percentComplete = 0.75f;
//		percentComplete = Penner.easeInOutCirc(percentComplete % 1f, 0, 1, 1);
//		float cameraPercent = Penner.easeInOutCirc((0.75f + percentComplete) % 1f, 0, 1, 1);
		float radsComplete = percentComplete * P.TWO_PI;
//		float cameraPercentRadsComplete = cameraPercent * P.TWO_PI;
		
		// rotate scene
		float starXOffset = (p.width/7f + p.width/7f * P.sin(P.PI - radsComplete));
		float starYOffset = (p.height/12f + p.width/12f * P.sin(P.PI - radsComplete));
		p.translate(p.width * 0.49f - starXOffset, p.height * 0.49f - starYOffset, -p.width * 0.6f);
		p.rotateY(0.35f - P.HALF_PI + 0.25f * P.sin(-0.4f + radsComplete));
		p.scale(0.7f + 2.2f + 2.2f * P.sin(radsComplete));
		
		// update gun texture
		gunTexture.beginDraw();
		gunTexture.background(180);
		gunTexture.translate(gunTexture.width/2, gunTexture.height/2);
		gunTilingTexture.update();
//		gunTilingTexture.setRotation(p.frameCount * 0.001f);
		gunTilingTexture.setOffset(percentComplete, percentComplete);
		float tileScale = 1.0f + 0.5f * P.sin(radsComplete);
		gunTilingTexture.setSize(tileScale, -tileScale);
		gunTilingTexture.drawCentered(gunTexture, gunTexture.width, gunTexture.height);
		gunTexture.endDraw();

		// draw gun
		p.rotateZ(P.PI - 0.7f  + (0.2f + 0.2f * P.sin(radsComplete)));
//		p.rotateZ(ymouse);
		p.rotateX(0.4f  - (0.3f + 0.3f * P.sin(radsComplete)));
//		p.rotateX(xmouse);
		
		p.pushMatrix();		
		p.noStroke();
		PShapeUtil.drawTriangles(p.g, gunMesh, gunTexture, 1f); // img
		p.popMatrix();
		
		
		// draw skull
		p.pushMatrix();
		float yOffset = -24;
		float zOffset = -49;
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
		p.popMatrix();
		
		
//		PShapeUtil.drawTrianglesWithTexture(p.g, skullMesh, gunTexture, 0.2f); // img
		
		// draw text 
		// AMERICA
		p.pushMatrix();
		p.rotateZ(P.PI);
		p.rotateY(P.HALF_PI);
		p.translate(-209, -302, 0);
//		p.translate(xmouse , ymouse, 0);
		p.fill(255);
		textAmerica.disableStyle();
		p.shape(textAmerica);
//		PShapeUtil.drawTriangles(p.g, textAmerica, flag, 1);
		p.popMatrix();
		
		// THE
		p.pushMatrix();
		p.rotateZ(P.PI);
		p.rotateY(P.HALF_PI);
		p.translate(-259, 51, 0);
//		p.translate(yOffsetAM ,zOffsetAM, 0);
		textThe.disableStyle();
		p.fill(255);
		p.shape(textThe);
		p.popMatrix();
		
		// INDEFENSIBLE
		p.pushMatrix();
		p.rotateZ(P.PI);
		p.rotateY(P.HALF_PI);
		p.translate(337, 302, 0);
//		p.translate(yOffsetAM ,zOffsetAM, 0);
		textIndefensible.disableStyle();
		p.fill(255);
		p.shape(textIndefensible);
		p.popMatrix();
		
		p.popMatrix();
	}
		
}