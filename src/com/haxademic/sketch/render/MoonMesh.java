package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class MoonMesh
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	PImage texture;
	PShape mesh;
	float angle;
	PShader texShader;
	float _frames = 210;
	float displaceAmp = 105f; 


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, false );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, 40 );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, 15 );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, (int)_frames );

	}

	public void setup() {
		super.setup();	

		texture = loadImage(FileUtil.getFile("images/luna.jpg"));
		mesh = createSheet(170, texture);
		texShader = loadShader(
			FileUtil.getFile("shaders/vertex/brightness-displace-frag-texture.glsl"), 
			FileUtil.getFile("shaders/vertex/brightness-displace-sheet-vert.glsl")
		);
		texShader.set("displacementMap", texture);
		texShader.set("displaceStrength", displaceAmp);
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
		texShader.set("displaceStrength", displaceAmp + displaceAmp * P.sin(percentComplete * P.TWO_PI));
		shader(texShader);  
		shape(mesh);
		
		// unset shader deformation
		resetShader();
	}


	PShape createSheet(int detail, PImage tex) {
		p.textureMode(NORMAL);
		PShape sh = p.createShape();
		sh.beginShape(QUADS);
		sh.noStroke();
		sh.texture(tex);
		float cellW = tex.width / detail;
		float cellH = tex.height / detail;
		int numVertices = 0;
		for (int col = 0; col < tex.width; col += cellW) {
			for (int row = 0; row < tex.height; row += cellH) {
				float xU = col;
				float yV = row;
				float x = -tex.width/2f + xU;
				float y = -tex.height/2f + yV;
				float z = 0;
				sh.normal(x, y, z);
				sh.vertex(x, y, z, P.map(xU, 0, tex.width, 0, 1), P.map(yV, 0, tex.height, 0, 1));
				sh.vertex(x, y + cellH, z, P.map(xU, 0, tex.width, 0, 1), P.map(yV + cellH, 0, tex.height, 0, 1));    
				sh.vertex(x + cellW, y + cellH, z, P.map(xU + cellW, 0, tex.width, 0, 1), P.map(yV + cellH, 0, tex.height, 0, 1));    
				sh.vertex(x + cellW, y, z, P.map(xU + cellW, 0, tex.width, 0, 1), P.map(yV, 0, tex.height, 0, 1));
				numVertices++;
			}
		}
		P.println(numVertices, "vertices");
		sh.endShape(); 
		return sh;
	}

}

