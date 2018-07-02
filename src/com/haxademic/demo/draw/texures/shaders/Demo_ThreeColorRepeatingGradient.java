package com.haxademic.demo.draw.texures.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.EasingColor;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class Demo_ThreeColorRepeatingGradient
extends PAppletHax { 
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics gradientBuffer;
	protected PShader gradientShader;
	protected float numColors = 3;
	protected EasingColor[] colorStops = new EasingColor[3];
	protected int FRAMES = 200;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	protected void setupFirstFrame() {
		// create noise buffer
		gradientBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		gradientShader = p.loadShader("haxademic/shaders/textures/cacheflowe-three-color-repeating-gradient.glsl");
		buildColors();
	}

	protected void buildColors() {
		for (int i = 0; i < colorStops.length; i++) {
			colorStops[i] = new EasingColor("#ff000000", 20);
		}
		setColors("#ffbd03d0", "#ffe8d74d", "#ffec3a6f");
	}
	
	public void setColors(String hex1, String hex2, String hex3) {
		colorStops[0].setTargetHex(hex1);
		colorStops[1].setTargetHex(hex2);
		colorStops[2].setTargetHex(hex3);
	}
	
	public void drawApp() {
		background(0);

		// change colors
		if(loop.progress() < 0.5f)  setColors("#ff000000", "#ffffffff", "#ff44ff44");
		else                        setColors("#ff000000", "#ffffffff", "#ffdddd44");
		
		// update colors in shader
		for (int i = 0; i < colorStops.length; i++) {
			colorStops[i].update();
			gradientShader.set("color"+(i+1), colorStops[i].r.value()/255f, colorStops[i].g.value()/255f, colorStops[i].b.value()/255f);
		}
		
		// update other shader properties
		gradientShader.set("zoom", 85f);
		gradientShader.set("scrollY", loop.progress() * 5f);
		gradientShader.set("oscFreq", P.PI * 3f);
		gradientShader.set("oscAmp", 0.08f + 0.03f * P.sin(loop.progressRads()));
		gradientShader.set("fade", 0.6f);

		// update gradient
		gradientBuffer.filter(gradientShader);
		
		// draw to screen
		p.image(gradientBuffer, 0, 0);  
	}
	
}