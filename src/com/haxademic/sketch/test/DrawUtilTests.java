package com.haxademic.sketch.test;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;

import processing.core.PConstants;
import processing.core.PGraphics;

public class DrawUtilTests
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	BaseTexture texture;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, 90 );
		p.appConfig.setProperty( AppSettings.SHOW_STATS, true );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
	}


	public void setup() {
		super.setup();
		texture = new TextureShaderTimeStepper(p.width, p.height, "cacheflowe-repeating-circles.glsl");
//		texture = new TextureMeshDeform(p.width, p.height);
		texture.texture().textureWrap(PConstants.REPEAT);
		p.textureWrap(PConstants.REPEAT);
	}

	public void drawApp() {
		background(0);
//		OpenGLUtil.setTextureQualityHigh(texture.texture());
//		OpenGLUtil.setQuality(p.g, OpenGLUtil.LOW);
//		OpenGLUtil.setWireframe(p.g, true);
		if(p.frameCount % 60 == 0) texture.updateTiming();
		texture.update();
		DrawUtil.rotateRedraw(texture.texture(), 0.2f * P.sin((float) p.frameCount * 0.01f));
		p.image(texture.texture(), 0, 0);
	}

}
