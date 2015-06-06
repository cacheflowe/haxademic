package com.haxademic.sketch.shader;

import processing.opengl.PShader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class ShaderLightTest
extends PAppletHax {

	protected PShader _pixlightShader;
	protected PShader _toonShader;
	protected float angle;
	protected boolean isToon = false;

	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_pixlightShader = p.loadShader(FileUtil.getHaxademicDataPath() + "shaders/lights/lightfrag/lightfrag.glsl", FileUtil.getHaxademicDataPath() + "shaders/lights/lightfrag/lightvert.glsl");
		_toonShader = p.loadShader(FileUtil.getHaxademicDataPath() + "shaders/lights/toon/frag.glsl", FileUtil.getHaxademicDataPath() + "shaders/lights/toon/vert.glsl");
		_toonShader.set("fraction", 1.0f);
	}

	public void draw() {
		p.background(0);


		if( isToon == false ) {
			p.shader(_pixlightShader);
			p.pointLight(0, 255, 255, p.mouseX, p.mouseY, 500);
		} else {
			p.shader(_toonShader);
			float dirY = (mouseY / (float)height - 0.5f) * 2f;
			float dirX = (mouseX / (float)width - 0.5f) * 2f;
			p.directionalLight(204, 204, 204, -dirX, -dirY, -1);
		}

		p.noStroke();
		p.translate(p.width/2, p.height/2, 0);
		p.rotateY(angle);  
		p.sphereDetail(300);
		Shapes.drawStar(p, 8, 150, 0.5f, 100, 0);
		// p.sphere(200);  
		angle += 0.01;
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			isToon = !isToon;
		}
	}


}
