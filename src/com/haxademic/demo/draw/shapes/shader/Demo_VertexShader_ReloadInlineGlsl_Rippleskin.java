package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderCompiler;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class Demo_VertexShader_ReloadInlineGlsl_Rippleskin 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape icosa;
	protected PImage texture;
	protected PShader shader;

	protected void config() {
		int FRAMES = 340;
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 1024);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false );
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 3);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 4);
	}
	
	protected void firstFrame() {
		buildShape();
		rebuildShader();
	}
	
	protected void buildShape() {
		int detail = 10;
		icosa = Icosahedron.createIcosahedron(p.g, detail, null);// DemoAssets.textureJupiter());
		PShapeUtil.scaleShapeToHeight(icosa, p.height * 0.5f);
	}

	protected void rebuildShader() {
		PShaderCompiler newShader = new PShaderCompiler(p, vertSource(), fragSource());
		if(newShader.isValid()) {
			shader = newShader;
		}
	}
	
	protected String[] vertSource() {
		return new String[] {
			"#version 150",
			"uniform mat4 transform;",
			"uniform mat4 modelview;",
			"uniform mat4 normalMatrix;",
			"uniform mat4 texMatrix;",
			"in vec4 position;",
			"in vec4 color;",
			"in vec2 texCoord;",
			"out vec4 vertColor;",
			"out vec4 vertTexCoord;",
			"out vec3 v_texCoord3D;",
			"uniform mat4 modelviewInv;",
			"uniform float time = 0;",
			"uniform float displaceAmp = 0.25f;",
			"void main() {",
			"	vec4 tmp = position * modelviewInv;",								// apply inverse matrix to use models original position as uv coords
			"	v_texCoord3D = tmp.xyz;",
			"	vec3 p = tmp.xyz;",
			"	float radsToCenter = atan(p.x, p.z);",								// circular waviness progress
			"	p.y += sin(time + radsToCenter * 14.) * (60. + 60. * sin(time));",	// uv warping like we do in 2d
			"	// send position-based stripes to fragment shader",
			"	float grey = 0.5 + 0.5 * sin(time * 6. + p.y / 10.);",
			"	vec4 col = vec4(grey, grey, grey, 1.0);",
			"	vertColor = col;",
			"	// deform based on color",
			"	float amp = (1. + displaceAmp * col.r);",
			"	gl_Position = transform * (position * vec4(amp, amp, amp, 1.));",
			"	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);",
			"}",
		};
	}
	
	protected String[] fragSource() {
		return new String[] {
			"#version 150",
			"#ifdef GL_ES",
			"precision highp float;",
			"precision mediump int;",
			"#endif",
			"#define PROCESSING_COLOR_SHADER",
			"in vec4 position;",
			"uniform sampler2D texture;",
			"uniform vec2 texOffset;",
			"in vec4 vertColor;",
			"in vec4 vertTexCoord;",
			"in vec3 v_texCoord3D;",
			"uniform float time = 0;",
			"out vec4 outColor;",
			"#define PI     3.14159265358",
			"#define TWO_PI 6.28318530718",
			"void main(void) {",
			"  outColor = vertColor * vec4(0.75 + 0.25 * sin(1. + v_texCoord3D.z * 0.01), 0.75 + 0.25 * sin(v_texCoord3D.y * 0.008), 0.75 + 0.25 * sin(1. + v_texCoord3D.z * 0.015), 1.);",
			"}",
		};
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') rebuildShader();
	}
	
	protected void drawApp() {
		// set context
		pg.beginDraw();
		pg.background(0);
		pg.noLights();
		PG.setCenterScreen(pg);
//		PG.basicCameraFromMouse(pg);
		pg.rotateY(FrameLoop.progressRads());
		
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		shader.set("time", FrameLoop.progressRads());
		shader.set("displaceAmp", 0.4f);
		shader.set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);
		pg.shader(shader);

		// apply shader, draw shape
		pg.shape(icosa);
		pg.resetShader();
		pg.endDraw();
		
		// draw pg to screen
		p.image(pg, 0, 0);
	}
		
}