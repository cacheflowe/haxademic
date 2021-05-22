package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.ui.UI;

public class Demo_PShaderHotSwap_Filter
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShaderHotSwap shader;
	
	protected void firstFrame() {
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/radial-flare.glsl"));
		UI.addSlider("radialLength", 0.95f, 0.5f, 1f, 0.01f, false);
		UI.addSlider("imageBrightness", 9f, 0f, 10f, 0.1f, false);
		UI.addSlider("flareBrightness", 9f, 0f, 10f, 0.1f, false);
		UI.addSlider("iters", 100f, 0f, 5000f, 10f, false);
	}
	
	protected void drawApp() {
		if(p.frameCount == 1) PG.setTextureRepeat(p.g, true);
		p.background(0);
		
		// draw into buffer
		pg.beginDraw();
//		pg.clear();
		pg.background(100);
		PG.setCenterScreen(pg);
		PG.setBetterLights(pg);
		pg.fill(180 + 55f * P.sin(p.frameCount * 0.02f), 180 + 55f * P.sin(p.frameCount * 0.03f), 180 + 55f * P.sin(p.frameCount * 0.04f), 255);
		pg.stroke(255);
		pg.strokeWeight(4);
		pg.noStroke();
		pg.rotateX(p.frameCount * 0.01f);
		pg.rotateY(p.frameCount * 0.01f);
		pg.box(200 + 10f * P.sin(p.frameCount * 0.01f), 200 + 50f * P.sin(p.frameCount * 0.01f), 200 + 50f * P.sin(p.frameCount * 0.02f));
		pg.endDraw();

		// run filter on buffer
		shader.update();
		shader.shader().set("radialLength", UI.value("radialLength"));
		shader.shader().set("imageBrightness", UI.value("imageBrightness"));
		shader.shader().set("flareBrightness", UI.value("flareBrightness"));
		shader.shader().set("iters", UI.value("iters"));
		pg.filter(shader.shader());
		
		// draw buffer to screen
		p.image(pg, 0, 0);
		
		// show shader compilation
		shader.showShaderStatus(p.g);
		
		
	}

}
