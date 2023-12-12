package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

public class TextureEQChladni 
extends BaseTexture {

	// Notes:
	// - https://thelig.ht/chladni/
	// - https://paulbourke.net/geometry/chladni/
	// - https://www.shadertoy.com/view/cssfRr
	// - https://www.shadertoy.com/view/WdKXRV

	protected BaseTexture audioTexture;

	protected PShaderHotSwap shader;

	public TextureEQChladni(int width, int height) {
		super(width, height);
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/textures/chladni.glsl"));	

	}

	public void drawPre() {
		
	}
	
	public void draw() {
		pg.background(0);

		// update & run shader
		shader.update();
		shader.shader().set("time", FrameLoop.count(0.01f));
		pg.filter(shader.shader());
		shader.showShaderStatus(pg);
	}
}
