package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class Demo_PShaderHotSwap_WriteFloatArray
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float[] data = new float[512];
	protected PGraphics dataBuffer;
	protected PShaderHotSwap shader;
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
		dataBuffer = PG.newPG(data.length, 1);
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/write-float-data.glsl"));
	}
		
	protected void drawApp() {
	  // clear screen
		p.background(0);
		
		// build data
		if(FrameLoop.frameModLooped(60)) {
			for (int i = 0; i < data.length; i++) {
				data[i] = p.random(0, 1f);
			}
		}

		// set data in shader & draw to pg
		shader.update();
		shader.shader().set("data", data);
		dataBuffer.filter(shader.shader());

		// draw buffer to screen
		p.image(dataBuffer, 0, 0, p.width, p.height);
		
		// show shader compilation
		shader.showShaderStatus(p.g);
		DebugView.setValue("isValid()", shader.isValid());
	}

}
