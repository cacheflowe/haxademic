package com.haxademic.demo.draw.textures.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class Demo_PerlinNoisePShader 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics textureBuffer;
	protected PShaderHotSwap shader;

	protected void firstFrame() {
		// create noise buffer
		textureBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		DebugView.setTexture("textureBuffer", textureBuffer);
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/textures/noise-perlin.glsl"));
	}

	protected void drawApp() {
		background(0);
		
		// update 3d perlin texture with optional repeating period
		shader.update();
		shader.shader().set("offset", p.frameCount * 0.01f, p.frameCount * 0.002f, p.frameCount * 0.005f);
		shader.shader().set("zoom", 5f + 4f * P.sin(p.frameCount * 0.005f));
		shader.shader().set("rotation", P.sin(p.frameCount * 0.03f) * 0.2f);
		if(FrameLoop.frameMod(200) < 100) {
			float repeat = P.round(Mouse.xNorm * 20f);
			shader.shader().set("repeat", repeat, repeat, repeat);
			DebugView.setValue("repeat", repeat);
		} else {
			shader.shader().set("repeat", 0f, 0f, 0f);
			DebugView.setValue("repeat", 0f);
		}
		textureBuffer.filter(shader.shader());
		
		// draw to screen
		p.image(textureBuffer, 0, 0);  
	}
		
}