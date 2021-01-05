package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.Renderer;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class ShaderVertexTrigDeform
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage texture;
	PShape mesh;
	float angle;
	PShader texShader;
	float _frames = 240;


	protected void config() {
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		
		Config.setProperty( AppSettings.RENDERING_GIF, false );
		Config.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, 40 );
		Config.setProperty( AppSettings.RENDERING_GIF_QUALITY, 15 );
		Config.setProperty( AppSettings.RENDERING_GIF_START_FRAME, 3 );
		Config.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, Math.round(_frames+2) );

	}

	protected void firstFrame() {
		mesh = createCylinder(100, 200, 1000, DemoAssets.textureJupiter());

		texShader = loadShader(
			FileUtil.getPath("haxademic/shaders/vertex/trig-displace-frag.glsl"), 
			FileUtil.getPath("haxademic/shaders/vertex/trig-displace-cylinder-vert.glsl")
		);
	}

	protected void drawApp() {
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
			if(Config.getBoolean("rendering", false) ==  true) {				
				Renderer.instance().videoRenderer.stop();
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

