package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.FeedbackMapFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebcamAudioFeedback
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;

	protected TextureShader textureShader;
	protected PGraphics feedbackMap;

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280 );
		Config.setProperty(AppSettings.HEIGHT, 720 );
//		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame () {
		WebCam.instance().setDelegate(this);
		AudioIn.instance();
	}

	@Override
	public void newFrame(PImage frame) {
		// lazy-init flipped camera buffer
		if(flippedCamera == null) flippedCamera = PG.newPG(frame.width, frame.height);
		ImageUtil.copyImageFlipH(frame, flippedCamera);
		DebugView.setTexture("flippedCamera", flippedCamera);

		// lazy-init displacement map
		if(textureShader == null) {
			textureShader = new TextureShader(TextureShader.noise_simplex_2d_iq, 0.0005f);
			feedbackMap = P.p.createGraphics(flippedCamera.width, flippedCamera.height, PRenderers.P2D);
			DebugView.setTexture("feedbackMap", feedbackMap);
		}

		// update feedback map on feedback shader
		textureShader.updateTime();
		textureShader.shader().set("zoom", 2f + P.sin(p.frameCount * 0.01f));
		textureShader.shader().set("rotation", p.frameCount * 0.01f);
		feedbackMap.filter(textureShader.shader());

		// apply feedback texture to main buffer
		float audioIn = AudioIn.audioFreq(100) * 0.01f;
		int feedbackCycles = P.round(audioIn); // P.round(Mouse.xNorm * 10f)
		feedbackCycles = 10;
		DebugView.setValue("audioIn", audioIn);
		FeedbackMapFilter.instance().setMap(feedbackMap);
		FeedbackMapFilter.instance().setAmp(audioIn);
//		FeedbackMapFilter.instance().setBrightnessStep(1f/255f);
//		FeedbackMapFilter.instance().setAlphaStep(-3f/255f);
		for (int i = 0; i < feedbackCycles; i++) FeedbackMapFilter.instance().applyTo(flippedCamera);
	}

	protected void drawApp() {
		// set up context
		p.background( 0 );
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);

		// show diff buffer
		if(flippedCamera != null) {
			ImageUtil.cropFillCopyImage(flippedCamera, p.g, true);
		}
	}

}
