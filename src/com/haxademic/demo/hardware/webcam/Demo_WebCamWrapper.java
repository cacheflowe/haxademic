package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.hardware.webcam.WebCamWrapper;

public class Demo_WebCamWrapper 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
	}
		
	public void setupFirstFrame () {
		WebCamWrapper.initWebCam(p, 1);
	}

	public void drawApp() {
		p.background( 0 );
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);
		WebCamWrapper.update();
		p.image(WebCamWrapper.getImage(), 0, 0);
	}

}
