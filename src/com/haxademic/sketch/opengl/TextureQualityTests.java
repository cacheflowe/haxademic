package com.haxademic.sketch.opengl;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;

import processing.core.PConstants;
import processing.core.PGraphics;

public class TextureQualityTests
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	BaseTexture texture;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, 90 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
	}


	public void setup() {
		super.setup();
		texture = new TextureShaderTimeStepper(p.width, p.height, "cacheflowe-repeating-circles.glsl");
//		texture = new TextureMeshDeform(p.width, p.height);
		
		texture.texture().textureWrap(PConstants.REPEAT);
		p.textureWrap(PConstants.REPEAT);
//		OpenGLUtil.setWireframe(p.g, true);
		
		OpenGLUtil.setTextureQualityLow(p.g);
		OpenGLUtil.setTextureQualityLow(texture.texture());
	}

	public void drawApp() {
		if(p.frameCount == 2) {	// wait until we have a GL version
			P.println(OpenGLUtil.getGlVersion(p.g).toString());
			P.println(OpenGLUtil.getGlVersion(texture.texture()).toString());
			OpenGLUtil.optimize2D(p.g);
			OpenGLUtil.setQuality(p.g, OpenGLUtil.LOW);
			OpenGLUtil.optimize2D(texture.texture());
			OpenGLUtil.setQuality(texture.texture(), OpenGLUtil.LOW);
		}
		background(0);
		if(p.frameCount % 60 == 0) texture.updateTiming();
		texture.update();
		DrawUtil.rotateRedraw(texture.texture(), 0.2f * P.sin((float) p.frameCount * 0.01f));
		p.image(texture.texture(), 0, 0);
	}

}
