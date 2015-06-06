package com.haxademic.sketch.shader;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class ShaderVertexTextureDeformTest
extends PAppletHax{


	PImage texture;
	PShape mesh;
	float angle;
	PShader texShader;
	float _frames = 40;


	protected void overridePropsFile() {
		_appConfig.setProperty( "fills_screen", "false" );
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "640" );
		
		_appConfig.setProperty( "rendering", "false" );
		
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "3" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+2) );

	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

//		texture = loadImage(FileUtil.getHaxademicDataPath()+"images/ello-multiple.png");
//		texture = loadImage(FileUtil.getHaxademicDataPath()+"images/justin-spike-portrait-02-smaller.png");
//		texture = loadImage(FileUtil.getHaxademicDataPath()+"images/snowblinded-beach.jpg");
		texture = loadImage(FileUtil.getHaxademicDataPath()+"images/snowblinded-mtn.jpg");
//		texture = loadImage(FileUtil.getHaxademicDataPath()+"images/ello-opaque.png");
//		texture = loadImage(FileUtil.getHaxademicDataPath()+"images/green-screen.png");
//		texture = loadImage(FileUtil.getHaxademicDataPath()+"images/cacheflowe-art/fractal-2013-09-26-20-11-32.png");
//		can = createCan(100, 200, 1000, texture);
		mesh = createSheet(10, texture);
		texShader = loadShader(
			FileUtil.getFile("shaders/vertex/brightness-displace-frag.glsl"), 
			FileUtil.getFile("shaders/vertex/brightness-displace-sheet-vert.glsl")
		);
		texShader.set("displacementMap", texture);
		texShader.set("displaceStrength", 100.0f);
	}

	public void drawApp() {
		background(255);
		
		// rendering
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);


		//		DrawUtil.setColorForPImage( p );
		//		DrawUtil.resetPImageAlpha( p );
		//		DrawUtil.setPImageAlpha(p, 1f);		

		texShader.set("displaceStrength", 200f + 200f * P.sin(percentComplete * P.TWO_PI));
		shader(texShader);  

		translate(width/2, height/2);
		rotateX(0.3f * P.sin(percentComplete * P.TWO_PI)); 
//		rotateX(P.TWO_PI + 0.9f); 
//		rotateX(mouseY/40f); 
//		rotateY(P.PI/2);  
//		rotateY(mouseX/40f);  
//		rotateZ(angle); 
//		scale(1.3f);
		shape(mesh);
		resetShader();
		angle += P.TWO_PI / _frames;

		
		if( p.frameCount == _frames * 2 ) {
			if(_appConfig.getBoolean("rendering", false) ==  true) {				
				_renderer.stop();
				P.println("render done!");
			}
		}

	}


	PShape createCan(float r, float h, int detail, PImage tex) {
		textureMode(NORMAL);
		PShape sh = createShape();
		sh.beginShape(QUAD_STRIP);
		sh.noStroke();
		sh.texture(tex);
		for (int i = 0; i <= detail; i++) {
			float angle = TWO_PI / detail;
			float x = sin(i * angle);
			float z = cos(i * angle);
			float u = (float)i / detail;
			sh.normal(x, 0, z);
			sh.vertex(x * r, -h/2, z * r, u, 0);
			sh.vertex(x * r, +h/2, z * r, u, 1);    
		}
		sh.endShape(); 
		return sh;
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

