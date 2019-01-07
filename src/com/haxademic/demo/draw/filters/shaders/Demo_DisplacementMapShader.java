package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_DisplacementMapShader
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage base;
	PImage mapSource;
	PGraphics map;
	PShader texShader;
	int mode = 0;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void setup() {
		super.setup();	
		base = DemoAssets.textureJupiter();
		mapSource = DemoAssets.textureNebula();
		map = p.createGraphics(p.width, p.height, PRenderers.P3D);
		texShader = loadShader(FileUtil.getFile("haxademic/shaders/filters/displacement-map.glsl"));
	}

	public void drawApp() {
		background(255);
		
		// rotate map for visibility
		map.beginDraw();
		DrawUtil.setDrawCenter(map);
		DrawUtil.setCenterScreen(map);
		map.rotate(p.frameCount * 0.01f);
		map.image(mapSource, 0, 0, mapSource.width * 2, mapSource.height * 2);
		map.endDraw();

		// set mode
		if(p.frameCount % 200 == 0) {
			mode++;
			if(mode > 3) mode = 0;
		}
		texShader.set("map", map );
		texShader.set("mode", mode );
		
		// draw image and apply displacement map
		p.image(base, 0, 0);
		p.filter(texShader); 
		
		// debug
		p.debugView.setValue("Mode ", mode);
		p.debugView.setTexture(base);
		p.debugView.setTexture(map);
		p.debugView.setTexture(mapSource);
	}
}

