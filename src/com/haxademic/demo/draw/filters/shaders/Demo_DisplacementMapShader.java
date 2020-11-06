package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_DisplacementMapShader
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage base;
	PImage mapSource;
	PGraphics map;
	int mode = 0;

	protected void firstFrame() {
		base = DemoAssets.textureJupiter();
		mapSource = DemoAssets.textureNebula();
		map = p.createGraphics(p.width, p.height, PRenderers.P3D);
	}

	protected void drawApp() {
		background(255);
		
		// rotate map for visibility
		map.beginDraw();
		PG.setDrawCenter(map);
		PG.setCenterScreen(map);
		map.rotate(p.frameCount * 0.01f);
		map.image(mapSource, 0, 0, mapSource.width * 2, mapSource.height * 2);
		map.endDraw();

		// draw image to screen, to be displaced next
		p.image(base, 0, 0);

		// set displace shader params & apply
		mode = P.round(Mouse.yNorm * 7);
		DisplacementMapFilter.instance(p).setMap(map);
		DisplacementMapFilter.instance(p).setMode(mode);
		DisplacementMapFilter.instance(p).setAmp(Mouse.xNorm/10f);
		DisplacementMapFilter.instance.applyTo(p.g);
		
		// debug
		DebugView.setValue("mode", mode);
		DebugView.setTexture("base", base);
		DebugView.setTexture("map", map);
		DebugView.setTexture("mapSource", mapSource);
	}
}

