package com.haxademic.sketch.test;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class ShaderTextureTest
extends PAppletHax{


	PImage label;
	PShape can;
	float angle;
	PShader texShader;
	float _frames = 60;


	protected void overridePropsFile() {
		_appConfig.setProperty( "fills_screen", "false" );
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "640" );
		_appConfig.setProperty( "rendering", "false" );
		
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+1) );

	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		label = loadImage(FileUtil.getHaxademicDataPath()+"images/ello-multiple.png");
//		label = loadImage(FileUtil.getHaxademicDataPath()+"images/justin-spike-portrait-02-smaller.png");
		can = createCan(100, 200, 1000, label);
		texShader = loadShader(
				FileUtil.getHaxademicDataPath()+"shaders/test/shader-texture-frag.glsl", 
				FileUtil.getHaxademicDataPath()+"shaders/test/shader-texture-vert.glsl"
				);

	}

	public void drawApp() {
		background(255);

		//		DrawUtil.setColorForPImage( p );
		//		DrawUtil.resetPImageAlpha( p );
		//		DrawUtil.setPImageAlpha(p, 1f);		

		texShader.set("scale", (p.mouseX * 2f) / p.width);
		shader(texShader);  

		translate(width/2, height/2);
		rotateX(0.4f * P.sin(p.frameCount*0.1f)); 
		rotateY(P.PI/2);  
//		rotateY(angle);  
//		rotateZ(angle);  
		shape(can);
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

}

