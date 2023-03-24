package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.hardware.dmx.DMXDebug;
import com.haxademic.core.hardware.dmx.editor.DMXEditor;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_DmxEditor_Shader
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXEditor editor;
	protected PGraphics textureMap;
	protected PGraphics dmxUI;
	protected PImage floorplan;
	protected TextureShader textureShader;
	protected DMXDebug dmxDebug;
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 960);
		Config.setProperty(AppSettings.HEIGHT, 540 * 2);
	}

	protected void firstFrame() {
		// Create buffer for DMXEditor color sampling
		// Can be smaller, but should be same aspect ratio as pgUI
		// - Buffer is treated as the same size as GUI, but scaled up for speed of sampling smaller texture
		textureMap = PG.newPG(960/2, 540/2);
		// create buffer for DMXEditor UI
		dmxUI = PG.newPG(960, 540);
		// load floorplan - doesn't need to be the same aspect ratio, but will be cropped-to-fill
		floorplan = DemoAssets.textureNebula();
		// load a shader to sample colors from
		textureShader = new TextureShader(TextureShader.cacheflowe_scrolling_dashed_lines);
		// build editor with all buffers & images
		editor = new DMXEditor("COM8", 115200, "text/dmx/dmx-lights-editor.txt", dmxUI, textureMap, floorplan);
		// add debug buffer
		dmxDebug = new DMXDebug();
		DebugView.setTexture("dmxDebug", dmxDebug.buffer());
	}

	protected void drawApp() {
		// clear background
		p.background(0);
		// update & draw shader
		textureShader.setTime((float) p.frameCount * 0.01f);
		pg.filter(textureShader.shader());
		ContrastFilter.instance().setContrast(2f);
		ContrastFilter.instance().applyTo(pg);
		// copy lights animation to DMXEditor buffer 
		ImageUtil.cropFillCopyImage(pg, textureMap, true); 
		// update & send lights to DMX hardware
		editor.update();
		// Draw editor to screen
		p.image(dmxUI, 0, 0);
		// update debug buffer as needed
//		if(DebugView.active()) dmxDebug.updateRGB(editor.dmxUniverse().data());
//		dmxDebug.updateRGB(editor.dmxUniverse().data());
		dmxDebug.updateFixtures(editor.dmxUniverse().fixtures());
		p.image(dmxDebug.buffer(), 0, 540);
	}
}
