package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;

import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_LightShaderAndToon_Torus
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShader lightShaderBasic;
	protected PShader toonShader;
	protected PShape torus;
	protected PShape sphere;
	protected boolean isToon = false;

	protected void firstFrame() {
		lightShaderBasic = p.loadShader(FileUtil.getPath("haxademic/shaders/lights/lightfrag/lightfrag.glsl"), FileUtil.getPath("haxademic/shaders/lights/lightfrag/lightvert.glsl"));
		toonShader = p.loadShader(FileUtil.getPath("haxademic/shaders/lights/toon/frag.glsl"), FileUtil.getPath("haxademic/shaders/lights/toon/vert.glsl"));
		
		torus = Shapes.createTorus(200, 60, 3600, 360, null);
		torus = torus.getTessellation();
		torus.disableStyle();
		
		sphere = PShapeUtil.createBox(100, 100, 100, p.color(255));
		sphere = sphere.getTessellation();
		sphere.disableStyle();
	}

	protected void drawApp() {
		p.background(ColorsHax.COLOR_GROUPS[3][1]);
		p.translate(p.width/2, p.height/2, 0);

		float dirY = (mouseY / (float)height - 0.5f) * 2f;
		float dirX = (mouseX / (float)width - 0.5f) * 2f;
		isToon = (Mouse.xNorm > 0.5f);
		if( isToon == false ) {
			p.lights();
			p.shader(lightShaderBasic);
			p.pointLight(255, 255, 255, p.mouseX, p.mouseY, 500);
			p.directionalLight(204, 204, 204, -dirX, -dirY, -1);
		} else {
			toonShader.set("fraction", Mouse.yNorm);
			p.shader(toonShader);
			p.directionalLight(204, 204, 204, -dirX, -dirY, 1);
		}
		
		// draw shapes
		p.noStroke();
		p.fill(ColorsHax.COLOR_GROUPS[3][2]);
		
		// torus has cool toon lighting
		p.push();
		p.rotateY(p.frameCount * 0.01f);
		p.shape(torus);
		p.pop();
		
		// 2nd shape... not really working. which shape is wrong here?
		p.rotateX(p.frameCount * 0.01f);
		p.rotateY(p.frameCount * 0.01f);
		p.shape(sphere);
	}

}
