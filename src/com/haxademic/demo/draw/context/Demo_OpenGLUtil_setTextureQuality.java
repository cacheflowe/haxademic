package com.haxademic.demo.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.TextureShaderTimeStepper;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;

import processing.core.PConstants;

public class Demo_OpenGLUtil_setTextureQuality
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	BaseTexture texture;
	
	protected void config() {
		Config.setProperty( AppSettings.FPS, 90 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
	}


	protected void firstFrame() {

		texture = new TextureShaderTimeStepper(p.width, p.height, "cacheflowe-repeating-circles.glsl");
//		texture = new TextureMeshDeform(p.width, p.height);
		
		texture.texture().textureWrap(PConstants.REPEAT);
		p.textureWrap(PConstants.REPEAT);
//		OpenGLUtil.setWireframe(p.g, true);
		
		OpenGLUtil.setTextureQualityLow(p.g);
		OpenGLUtil.setTextureQualityLow(texture.texture());
	}

	protected void drawApp() {
		if(p.frameCount == 2) {	// wait until we have a GL version
			P.println(OpenGLUtil.getGlVersion(p.g).toString());
			P.println(OpenGLUtil.getGlVersion(texture.texture()).toString());
			OpenGLUtil.optimize2D(p.g);
			OpenGLUtil.setQuality(p.g, OpenGLUtil.GL_QUALITY_LOW);
			OpenGLUtil.optimize2D(texture.texture());
			OpenGLUtil.setQuality(texture.texture(), OpenGLUtil.GL_QUALITY_LOW);
		}
		background(0);
		if(p.frameCount % 60 == 0) texture.updateTiming();
		texture.update();
		PG.rotateRedraw(texture.texture(), 0.2f * P.sin((float) p.frameCount * 0.01f));
		p.image(texture.texture(), 0, 0);
	}

}
