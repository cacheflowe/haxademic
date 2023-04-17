package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.compound.ColorAdjustmentFilter;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PImage;

public class Demo_ColorAdjustmentFilter 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
		Config.setProperty(AppSettings.SHOW_UI, true );
		Config.setProperty(AppSettings.RENDER_DEMO_SCREENSHOT_FRAME, 2000);
	}
		
	protected void firstFrame () {
		// load a webcam with the fancy gstreamer config, allowing for high-res camera feeds
		WebCam.instance().setDelegate(this).set1080p();
		
		// init filter with UI controls
		ColorAdjustmentFilter.buildUI("two", true);
	}

	protected void drawApp() {
		PImage webcamImg = WebCam.instance().image();
		boolean webcamIsGood = (webcamImg.width > 400);
		
		// draw webcam
		p.background((webcamIsGood) ? 50 : p.color(255,0,0));
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		p.image(webcamImg, 0, 0);
		
		// apply ColorAdjustmentFilter
		ColorAdjustmentFilter.applyFromUI(p.g, "two");
	}

	@Override
	public void newFrame(PImage frame) {
		DebugView.setValue("Last WebCam frame", p.frameCount);
	}

}
