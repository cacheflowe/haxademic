package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.image.TickerScroller;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

public class Demo_TickerScroller
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected TickerScroller ticker;
	
	protected void config() {
//		Config.setProperty( AppSettings.WIDTH, 640 );
//		Config.setProperty( AppSettings.HEIGHT, 182 );
		Config.setProperty( AppSettings.FULLSCREEN, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 311 );
	}

	public void firstFrame() {
		ticker = new TickerScroller(DemoAssets.squareTexture(), p.color(255), 640, 200, 4.f);
	}
	
	public void drawApp() {
		p.background(0);
		p.noStroke();
		
		// sometime change source image
		if(p.frameCount % 200 == 0) {
			ticker.image(DemoAssets.textureNebula());
		} else if(p.frameCount % 200 == 100) {
			ticker.image(DemoAssets.justin());
		}
		
		// update ticker
		float speed = P.map(Mouse.xNorm, 0, 1, 30f, -30f);
		ticker.speed(speed);
		ticker.update();
		
		// post-process blur for speed effect
		BlurHFilter.instance(p).setBlurByPercent(speed / 3f, ticker.image().width);
		BlurHFilter.instance(p).applyTo(ticker.buffer());

		// draw to screen
		p.image(ticker.image(), 0, 0);
	}	
}

