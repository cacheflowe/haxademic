package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;

public class Demo_PShaderHotSwap_Frag
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShaderHotSwap shader;
	
	public void firstFrame() {
//		shader = new PShaderHotSwap(FileUtil.getFile("haxademic/shaders/textures/cacheflowe-triangle-wobble-stairs.glsl"));	
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/textures/truchet-tooth.glsl"));	
//		shader = new PShaderHotSwap(FileUtil.getFile("haxademic/shaders/textures/cacheflowe-stripe-waves.glsl"));	
	}
	
	public void drawApp() {
		if(p.frameCount == 1) PG.setTextureRepeat(p.g, true);
		p.background(0);
		shader.update();
		shader.shader().set("time", p.frameCount * 0.01f);
		shader.shader().set("noiseSeed", p.frameCount * 0.0000001f);
		shader.shader().set("tileSize", 0.15f + 0.1f * P.sin(p.frameCount * 0.01f));
		shader.shader().set("rotation", Mouse.xNorm * P.TWO_PI);
		p.filter(shader.shader());
		shader.showShaderStatus(p.g);
	}

}
