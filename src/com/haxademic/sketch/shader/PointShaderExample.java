package com.haxademic.sketch.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.file.FileUtil;

import processing.opengl.PShader;

public class PointShaderExample
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PShader pointShader;
	int mode = 0;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "640" );
	}

	public void setup() {
		super.setup();	
		pointShader = loadShader(FileUtil.getFile("haxademic/shaders/point/point-frag.glsl"), FileUtil.getFile("haxademic/shaders/point/point-vert.glsl"));
		stroke(255);
		strokeWeight(50);

		background(0);

	}

	public void drawApp() {
		shader(pointShader, POINTS);
		if (mousePressed) {
			point(mouseX, mouseY);
		}  

	}
}
