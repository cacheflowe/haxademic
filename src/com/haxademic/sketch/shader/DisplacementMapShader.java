package com.haxademic.sketch.shader;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.filters.shaders.BrightnessFilter;
import com.haxademic.core.draw.filters.shaders.ColorDistortionFilter;
import com.haxademic.core.draw.filters.shaders.ContrastFilter;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.draw.filters.shaders.DeformTunnelFanFilter;
import com.haxademic.core.draw.filters.shaders.EdgesFilter;
import com.haxademic.core.draw.filters.shaders.KaleidoFilter;
import com.haxademic.core.draw.filters.shaders.RadialRipplesFilter;
import com.haxademic.core.draw.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.opengl.PShader;

public class DisplacementMapShader
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage base;
	PImage map;
	PShader texShader;
	int mode = 0;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "640" );
	}

	public void setup() {
		super.setup();	
		base = p.loadImage(FileUtil.getFile("images/snowblinded-beach.jpg"));
		map = p.loadImage(FileUtil.getFile("images/snowblinded-mtn-2.jpg"));
		texShader = loadShader(FileUtil.getFile("shaders/filters/displacement-map.glsl"));
	}

	public void drawApp() {
		background(255);
//		OpenGLUtil.setTextureRepeat(g);
		
		if(p.frameCount % 200 == 0) {
			mode++;
			if(mode > 2) mode = 0;
		}
		
		p.image(base, 0, 0);
		texShader.set("map", map );
		texShader.set("mode", mode );
		p.filter(texShader); 
		
		p.fill(255);
		p.text("Mode "+mode, 20, 20);
	}
}

