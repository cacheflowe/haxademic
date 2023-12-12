package com.haxademic.demo.draw.textures;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.filters.pshader.GlitchShakeFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SpoutTexture;

public class Demo_SpoutTexture 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SpoutTexture spoutTexture;

	protected void firstFrame() {
		// SpoutTexture dimensions must match sender dimensions!
		// spoutTexture = new SpoutTexture(1920, 1080);
//		spoutTexture = new SpoutTexture(960, 540);
//		spoutTexture = new SpoutTexture(1280, 720, "CEF_0");
		// spoutTexture = new SpoutTexture(1280, 720, "TDSyphonSpoutOut");
		spoutTexture = new SpoutTexture(1280, 720, "SpoutWinCapture");
//		spoutTexture.setFlipY();
		DebugView.setTexture("spoutTexture.texture()", spoutTexture.texture());
	}

	protected void drawApp() {
		background(127);
		
		// update & draw to screen
		spoutTexture.update();
		ImageUtil.cropFillCopyImage(spoutTexture.texture(), p.g, true);
//		p.image(spoutTexture.texture(), 0, 0);
		
		// some post-processing, just for fun
		GlitchShakeFilter.instance().setAmp(0.03f);
		GlitchShakeFilter.instance().setTime(p.frameCount * 0.1f);
		GlitchShakeFilter.instance().setGlitchSpeed(0.002f);
		GlitchShakeFilter.instance().applyTo(p);
		VignetteFilter.instance().applyTo(p);
	}

}