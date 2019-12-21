package com.haxademic.demo.draw.textures.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.filters.pshader.BrightnessToAlphaFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class Demo_SDF_ui 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics buffer;
	protected PShader shader;

	protected String sdfThreshold = "sdfThreshold";
	protected String twistAmp = "twistAmp";
	protected String rotation = "rotation";
	protected String cubeSize = "cubeSize";
	protected String cubePos = "cubePos";

	protected void setupFirstFrame() {
		// create shader/buffer
		buffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		shader = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/sdf-04-manual.glsl"));
		
		// ui
		UI.addSlider(sdfThreshold, 0.00001f, 0.000001f, 0.0001f, 0.000001f, false);
		UI.addSlider(twistAmp, 0f, -3f, 3f, 0.001f, false);
		UI.addSliderVector(rotation, 0f, 0, 2, 0.01f, false);
		UI.addSliderVector(cubeSize, 0.6f, 0, 2, 0.01f, false);
		UI.addSliderVector(cubePos, 0f, -5f, 5f, 0.01f, false);
		UI.setValue(cubePos+"_Z", -3f);
	}

	public void drawApp() {
		background(0);
		
		
		// draw sphere
		p.noFill();
		p.stroke(255);
		p.push();
		p.translate(
			p.width/2 + UI.valueX(cubePos) * ((float)-p.width/8f), 
			p.height/2 + UI.valueY(cubePos) * ((float)p.height/5.6f), 
			UI.valueZ(cubePos) * ((float)p.width/8f)
		);
		p.sphere(200);
		p.pop();

		
		// update shader texture
		shader.set("time", p.frameCount * 0.01f);
		shader.set(sdfThreshold, UI.value(sdfThreshold));
		shader.set(twistAmp, UI.value(twistAmp));
		shader.set(rotation, UI.valueX(rotation), UI.valueY(rotation), UI.valueZ(rotation));
		shader.set(cubeSize, UI.valueX(cubeSize), UI.valueY(cubeSize), UI.valueZ(cubeSize));
		shader.set(cubePos, UI.valueX(cubePos), UI.valueY(cubePos), UI.valueZ(cubePos));
		buffer.filter(shader);
		
		buffer.beginDraw();
		buffer.fill(255);
		buffer.rect(100, 100, 100, 100);
		buffer.endDraw();
		
		// knock out black bg
//		BrightnessToAlphaFilter.instance(p).set
		BrightnessToAlphaFilter.instance(p).applyTo(buffer);
		
		// draw to screen
		p.debugView.setTexture("buffer", buffer);
//		p.image(buffer, 0, 0);
	}
		
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') shader = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/sdf-04-manual.glsl"));
	}
}