package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.KaleidoFilter;
import com.haxademic.core.draw.image.BufferActivityMonitor;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_KaliedoWebcam 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	protected BufferActivityMonitor activityMonitor;
	protected TiledTexture tiledTexture;
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280 );
		Config.setProperty(AppSettings.HEIGHT, 720 );
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}
		
	protected void firstFrame () {
		WebCam.instance().setDelegate(this);
	}
	
	@Override
	public void newFrame(PImage frame) {
		// lazy-init objects on first webcam frame
		if(flippedCamera == null) {
			flippedCamera = p.createGraphics(frame.width, frame.height, PRenderers.P2D);
			tiledTexture = new TiledTexture(flippedCamera);
			activityMonitor = new BufferActivityMonitor();
		}
		ImageUtil.copyImageFlipH(frame, flippedCamera);	
		
		// calculate activity monitor with new frame
		activityMonitor.update(flippedCamera);
		DebugView.setTexture("flippedCamera", flippedCamera);
		DebugView.setTexture("differenceBuffer", activityMonitor.differenceBuffer());
	}

	protected void drawApp() {
		// set up context
		p.background( 0 );
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		
		// draw webcam
		if(flippedCamera != null) {
			// show activity calculation and texture in debug panel
			DebugView.setValue("ACTIVITY", activityMonitor.activityAmp());

			// ImageUtil.cropFillCopyImage(flippedCamera, p.g, true);
			float sizee = 1f + 0.5f * P.sin(p.frameCount * 0.004f);
			tiledTexture.setSize(sizee, sizee);
			tiledTexture.setRotation(p.frameCount * 0.0001f);
			tiledTexture.setOffset(1f * P.sin(p.frameCount * 0.0001f), 1f * P.sin(p.frameCount * 0.00005f));
			tiledTexture.draw(p.g, p.width, p.height);
		}
		
		// kaleido
		PG.setTextureRepeat(p, true);
//		KaleidoFilter.instance(p).setSides(4f + activityMonitor.activityAmp() * 6f);
		KaleidoFilter.instance(p).setSides(6f);
		KaleidoFilter.instance(p).setAngle(p.frameCount * 0.0001f);
		KaleidoFilter.instance(p).applyTo(p);
	}
	
}
