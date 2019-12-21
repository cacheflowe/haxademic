package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;

public class ShaderVertexTextureDeform
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	PImage texture;
	PShape mesh;
	float angle;
	float _frames = 210;
	float displaceAmp = 105f; 


	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames );
	}

	public void firstFrame() {
		texture = DemoAssets.squareTexture();
		mesh = Shapes.createSheet(270, texture);
	}

	public void drawApp() {
		background(0);
		
		// rendering
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		angle = P.TWO_PI * percentComplete;

		// set center screen & rotate
		translate(width/2, height/2);
		scale(0.65f);
		rotateZ(-0.3f + 0.01f * P.sin(percentComplete * 2f * P.TWO_PI)); 
		rotateX(0.2f + 0.4f * P.sin(percentComplete * P.TWO_PI)); 

		// set shader properties & set on processing context
		// deform mesh
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(texture);
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(displaceAmp + displaceAmp * P.sin(percentComplete * P.TWO_PI));
		MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(p).applyTo(p);

		// draw mesh
		shape(mesh);
		resetShader();
	}

}

