package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class MoonMesh
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage texture;
	PGraphics textureFade;
	PShape mesh;
	float angle;
	float displaceAmp = 135f; 
	int FRAMES = 210;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);

	}

	public void setupFirstFrame() {
		texture = P.getImage("haxademic/images/space/luna.jpg");
//		texture = P.getImage("haxademic/images/space/sun.jpg");
//		textureFade = ImageUtil.imageToGraphics(texture);
//		VignetteFilter.instance(p).setDarkness(0.8f);
//		VignetteFilter.instance(p).setSpread(0.8f);
//		VignetteFilter.instance(p).applyTo(textureFade);
//		texture = textureFade.copy();
		
		mesh = Shapes.createSheet(170, texture);
		mesh.setTexture(texture);
	}

	public void drawApp() {
		p.background(0);
		PG.setCenterScreen(p);
		
		// set center screen & rotate
		p.scale(0.65f);
		p.rotateZ(-0.3f + 0.01f * P.sin(p.loop.progressRads() * 2f)); 
		p.rotateX(0.2f + 0.4f * P.sin(p.loop.progressRads())); 

		// set shader properties & draw mesh
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(texture);
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(displaceAmp + displaceAmp * P.sin(p.loop.progressRads()));
		MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(p).applyTo(p);
		p.shape(mesh);
		p.resetShader();
	}

}

