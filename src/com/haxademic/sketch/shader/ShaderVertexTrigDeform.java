package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class ShaderVertexTrigDeform
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	PImage texture;
	PShape mesh;
	float angle;
	PShader texShader;
	float _frames = 60;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "false" );
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "640" );
		
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "3" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+2) );

	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		// texture = loadImage(FileUtil.getFile("images/ello-multiple.png"));
		texture = loadImage(FileUtil.getFile("images/cacheflowe-art/fractal-2013-09-26-20-11-32.png"));
		
		mesh = createCylinder(100, 200, 1000, texture);

		texShader = loadShader(
			FileUtil.getFile("haxademic/shaders/vertex/trig-displace-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/vertex/trig-displace-cylinder-vert.glsl")
		);
	}

	public void drawApp() {
		background(255);
		
		// rendering progress
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		angle = P.TWO_PI * percentComplete;

		// set vert shader on processing context
		shader(texShader);  

		// spin & draw
		translate(width/2, height/2);
		rotateY(angle); 
		shape(mesh);
		
		// unset vert shader
		resetShader();
		
		// stop rendering
		if( p.frameCount == _frames * 2 ) {
			if(p.appConfig.getBoolean("rendering", false) ==  true) {				
				videoRenderer.stop();
				P.println("render done!");
			}
		}
	}


	PShape createCylinder(float r, float h, int detail, PImage tex) {
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

}

