package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.media.video.MovieBuffer;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebcamColorizeFromVideo
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	protected MovieBuffer movie;

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280 );
		Config.setProperty(AppSettings.HEIGHT, 720 );
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame () {
		// load movie
		movie = new MovieBuffer("C:\\Users\\cacheflowe\\Downloads\\210720_GRADIENT_B_raster.mp4");
		movie.movie.speed(3);
		movie.movie.loop();
		
		// capture webcam frames
		WebCam.instance().setDelegate(this);
	}

	@Override
	public void newFrame(PImage frame) {
		// lazy-init flipped camera buffer
		if(flippedCamera == null) flippedCamera = p.createGraphics(800, 600, PRenderers.P2D);
		ImageUtil.copyImageFlipH(frame, flippedCamera);
		DebugView.setTexture("webcam", flippedCamera);
	}

	protected void drawApp() {
		// set up context
		p.background( 0 );
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);

		// show camera & colorize
		if(flippedCamera != null && movie.buffer != null) {
			DebugView.setTexture("movie.buffer", movie.buffer);

			ImageUtil.cropFillCopyImage(flippedCamera, p.g, true);
			ColorizeFromTexture.instance().setTexture(movie.buffer);
			ColorizeFromTexture.instance().setLumaMult(Mouse.xNorm > 0.5f);
			ColorizeFromTexture.instance().setCrossfade(Mouse.yNorm);
			ColorizeFromTexture.instance().applyTo(p);
		}
	}

}
