package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.image.TickerScroller;
import com.haxademic.core.file.DemoAssets;

public class Demo_TickerScroller
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected TickerScroller ticker;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 182 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 311 );
	}

	public void setupFirstFrame() {
		ticker = new TickerScroller(DemoAssets.squareTexture(), p.color(255), p.width, p.height, 4.f);
	}
	
	public void drawApp() {
		p.background(255);
		p.noStroke();
		
		// update ticker
		float speed = P.map(p.mousePercentX(), 0, 1, 30f, -30f);
		ticker.speed(speed);
		ticker.update();

		// draw to screen
		p.image(ticker.image(), 0, 0);
		
		// post-process blur for speed effect
		BlurHFilter.instance(p).setBlurByPercent(speed / 3f, p.width);
		BlurHFilter.instance(p).applyTo(p);
	}	
}

