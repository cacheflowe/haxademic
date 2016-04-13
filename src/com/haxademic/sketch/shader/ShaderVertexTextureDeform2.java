package com.haxademic.sketch.shader;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class ShaderVertexTextureDeform2
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	PGraphics texture;
	PShape mesh;
	float angle;
	PShader textureShader;
	PShader displacementShader;
	float _frames = 210;
	float displaceAmp = 10f; 


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
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

		texture = createGraphics(p.width, p.height, P.P3D);
		textureShader = P.p.loadShader( FileUtil.getFile("shaders/textures/basic-diagonal-stripes.glsl")); 
		textureShader.set("time", 0 );

		mesh = createSheet(200, texture);
		displacementShader = loadShader(
			FileUtil.getFile("shaders/vertex/brightness-displace-frag-texture.glsl"), 
			FileUtil.getFile("shaders/vertex/brightness-displace-sheet-vert.glsl")
		);
		displacementShader.set("displacementMap", texture);
		displacementShader.set("displaceStrength", displaceAmp);
	}

	public void drawApp() {
		background(0);
		
		// rendering
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		angle = P.TWO_PI * percentComplete;

		// set center screen & rotate
		translate(width/2, height/2);
		scale(3.65f);
		rotateX(P.PI * p.mouseY * 0.01f); 
//		rotateZ(percentComplete * P.TWO_PI); 
		rotateZ(P.PI/4f); 

		// update generative texture
		textureShader.set("time", 10f * P.sin( P.TWO_PI * percentComplete) );
		texture.filter(textureShader);

		// set shader properties & set on processing context
		displacementShader.set("displaceStrength", displaceAmp + displaceAmp * P.sin(percentComplete * P.TWO_PI));
		shader(displacementShader);  
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

