package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;

public class MoonMesh
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage texture;
	PShape mesh;
	float angle;
	float _frames = 210;
	float displaceAmp = 105f; 

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames );

	}

	public void setupFirstFrame() {
		texture = loadImage(FileUtil.getFile("haxademic/images/space/luna.jpg"));
		mesh = Shapes.createSheet(170, texture);
		
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(texture);
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(displaceAmp);
		MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
		// set texture using PShape method
		mesh.setTexture(texture);
	}

	public void drawApp() {
		p.background(0);
		
		// rendering
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		angle = P.TWO_PI * percentComplete;

		// set center screen & rotate
		DrawUtil.setCenterScreen(p);
		p.scale(0.65f);
		p.rotateZ(-0.3f + 0.01f * P.sin(percentComplete * 2f * P.TWO_PI)); 
		p.rotateX(0.2f + 0.4f * P.sin(percentComplete * P.TWO_PI)); 

		// set shader properties & draw mesh
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(displaceAmp + displaceAmp * P.sin(percentComplete * P.TWO_PI));
		MeshDeformAndTextureFilter.instance(p).applyVertexShader(p);
		p.shape(mesh);
		p.resetShader();
	}

}

