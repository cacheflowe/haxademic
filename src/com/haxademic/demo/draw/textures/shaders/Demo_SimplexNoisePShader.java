package com.haxademic.demo.draw.textures.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;

public class Demo_SimplexNoisePShader 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics textureBuffer;
	protected PShaderHotSwap shader;

	protected void firstFrame() {
		// create noise buffer
		textureBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/textures/noise-simplex-3d.glsl"));
	}

	protected void drawApp() {
		background(0);
		
		// update perlin texture
		shader.update();
		shader.shader().set("offset", 0f, p.frameCount * 0.001f, p.frameCount * 0.0002f);
		shader.shader().set("zoom", 1f + 0.2f * P.sin(p.frameCount * 0.01f));
		shader.shader().set("rotation", P.sin(p.frameCount * 0.01f) * 0.4f);
		shader.shader().set("fractalMode", (p.frameCount % 200 < 100) ? 0 : 1);
		textureBuffer.filter(shader.shader());
		DebugView.setTexture("textureBuffer", textureBuffer);
		
		// draw to screen
		p.image(textureBuffer, 0, 0);  
	}
		
}