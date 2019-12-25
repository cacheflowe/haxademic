package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class TwoDazzleLinesShaders
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics maskBuffer;
	protected PGraphics tiledBuffer;
	protected PGraphics stripesBuffer1;
	protected PGraphics stripesBuffer2;
	protected float frames = 220;
	
	protected PShader stripes;
	protected PShader maskShader;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1920 );
		Config.setProperty( AppSettings.HEIGHT, 460/2 );
		Config.setProperty( AppSettings.HEIGHT, 1080 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		
		Config.setProperty( AppSettings.FPS, 30 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int) frames );
	}

	public void firstFrame() {

		

		maskBuffer = p.createGraphics(p.width, p.height, P.P3D);
		maskBuffer.smooth(8);
		stripesBuffer1 = p.createGraphics(p.width, p.height, P.P3D);
		stripesBuffer1.smooth(8);
		stripesBuffer2 = p.createGraphics(p.width, p.height, P.P3D);
		stripesBuffer2.smooth(8);
		
		tiledBuffer = p.createGraphics(p.width, p.height, P.P3D);
		tiledBuffer.smooth(8);
		
		maskShader = loadShader(FileUtil.getPath("haxademic/shaders/filters/three-texture-opposite-mask.glsl"));
		
		stripes = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/cacheflowe-rotating-stripes.glsl"));
	}

	public void drawApp() {
//		p.background(0);
		float progress = (p.frameCount % frames) / frames; 
		float easedProgress = Penner.easeInOutExpo(progress, 0, 1, 1);
		float progressRads = progress * P.TWO_PI;
		float progressRadsEased = easedProgress * P.TWO_PI;
		
		// draw stripes
		stripes.set("time", progress);
		stripes.set("amp", 26.0f + 26f * P.sin(P.QUARTER_PI + progressRads));
		stripes.set("rot", P.QUARTER_PI - 0.32f * P.sin(progressRadsEased));
		stripesBuffer1.filter(stripes);
		
		// 2nd stripes
		stripes.set("amp", 126.0f + 126f * P.cos(P.QUARTER_PI + progressRads));
		stripes.set("time", progress * -1f);
		stripes.set("rot", -1f * P.QUARTER_PI - 0.32f * P.sin(progressRadsEased));
		stripesBuffer2.filter(stripes);
		
		// apply 3-texture mask shader
//		maskShader.set("mask", textImg );
		maskShader.set("mask", tiledBuffer );
		maskShader.set("tex1", stripesBuffer1 );
		maskShader.set("tex2", stripesBuffer2 );
//		p.filter(maskShader);

		// draw with exclusion blend mode
		p.image(stripesBuffer1, 0, 0);
		p.blendMode(PBlendModes.EXCLUSION);
		p.image(stripesBuffer2, 0, 0);
		p.blendMode(PBlendModes.BLEND);
		
		
		VignetteFilter.instance(p).setDarkness(0.5f);
		VignetteFilter.instance(p).applyTo(p);
	}
	
	protected float val255to1(float val) {
		return val / 255f;
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
		}
	}


}
