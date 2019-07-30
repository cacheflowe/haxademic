package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_WebcamAudioFeedback
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;

	protected PShader feedbackShader;
	protected TextureShader textureShader;
	protected PGraphics feedbackMap;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty(AppSettings.INIT_ESS_AUDIO, true );
//		p.appConfig.setProperty(AppSettings.INIT_MINIM_AUDIO, true );
//		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame () {
		WebCam.instance().setDelegate(this);
	}

	@Override
	public void newFrame(PImage frame) {
		// lazy-init flipped camera buffer
		if(flippedCamera == null) flippedCamera = p.createGraphics(frame.width, frame.height, PRenderers.P2D);
		ImageUtil.copyImageFlipH(frame, flippedCamera);
		p.debugView.setTexture("flippedCamera", flippedCamera);

		// lazy-init displacement map
		if(feedbackShader == null) {
			feedbackShader = loadShader(FileUtil.getFile("haxademic/shaders/filters/feedback-map.glsl"));
			textureShader = new TextureShader(TextureShader.noise_simplex_2d_iq, 0.0005f);
			feedbackMap = P.p.createGraphics(flippedCamera.width, flippedCamera.height, PRenderers.P2D);
			p.debugView.setTexture("feedbackMap", feedbackMap);
		}

		// update feedback map on feedback shader
		textureShader.updateTime();
		textureShader.shader().set("zoom", 2f + P.sin(p.frameCount * 0.01f));
		textureShader.shader().set("rotation", p.frameCount * 0.01f);
		feedbackMap.filter(textureShader.shader());

		// apply feedback texture to main buffer
		float audioIn = P.p.audioFreq(100) * 0.01f;
		p.debugView.setValue("audioIn", audioIn);
//		p.debugView.setValue("audioIn", audioIn);
		feedbackShader.set("map", feedbackMap);
		feedbackShader.set("samplemult", P.map(p.mouseY, 0, p.height, 0.85f, 1.15f) );
		feedbackShader.set("amp", audioIn); // P.map(p.mouseX, 0, p.width, 0.004f, 0.02f) );
		int feedbackCycles = P.round(audioIn); // P.round(p.mousePercentX() * 10f)
		feedbackCycles = 10;
		for (int i = 0; i < feedbackCycles; i++) flippedCamera.filter(feedbackShader);
	}

	public void drawApp() {
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
