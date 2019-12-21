package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_DisplacementMapShader
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage base;
	PImage mapSource;
	PGraphics map;
	PShader texShader;
	int mode = 0;

	public void setupFirstFrame() {
		base = DemoAssets.textureJupiter();
		mapSource = DemoAssets.textureNebula();
		map = p.createGraphics(p.width, p.height, PRenderers.P3D);
		texShader = loadShader(FileUtil.getFile("haxademic/shaders/filters/displacement-map.glsl"));
	}

	public void drawApp() {
		background(255);
		
		// rotate map for visibility
		map.beginDraw();
		PG.setDrawCenter(map);
		PG.setCenterScreen(map);
		map.rotate(p.frameCount * 0.01f);
		map.image(mapSource, 0, 0, mapSource.width * 2, mapSource.height * 2);
		map.endDraw();

		// set mode
		mode = P.round(Mouse.yNorm * 7);
		texShader.set("map", map );
		texShader.set("mode", mode );
		texShader.set("amp", Mouse.xNorm/10f);
		
		// draw image and apply displacement map
		p.image(base, 0, 0);
		p.filter(texShader); 
		
		// debug
		DebugView.setValue("mode", mode);
		DebugView.setTexture("base", base);
		DebugView.setTexture("map", map);
		DebugView.setTexture("mapSource", mapSource);
	}
}

