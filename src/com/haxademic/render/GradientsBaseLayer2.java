package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.image.TickerScroller;

import processing.core.PGraphics;

public class GradientsBaseLayer2
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected TickerScroller ticker;
	int FRAMES = 1000;
		
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280);
		Config.setProperty(AppSettings.HEIGHT, 720);
//		Config.setProperty(AppSettings.WIDTH, 6912);
//		Config.setProperty(AppSettings.HEIGHT, 1344);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES);
	}

	protected void firstFrame() {
		int[] colors = new int[] {
				// superbowl: #0D131B
				ColorUtil.colorFromHex("#ff0D131B"),
				// team 1
//				ColorUtil.colorFromHex("#ff0B1630"),
//				ColorUtil.colorFromHex("#ffBB0022"),
//				ColorUtil.colorFromHex("#ff0B1630"),
//				ColorUtil.colorFromHex("#ffBB0022"),
				// team 2
//				ColorUtil.colorFromHex("#ff471D6C"),
//				ColorUtil.colorFromHex("#ffF5AB2C"),
//				ColorUtil.colorFromHex("#ff471D6C"),
//				ColorUtil.colorFromHex("#ffF5AB2C"),
				// team 3
				ColorUtil.colorFromHex("#ff9AAAA8"),
				ColorUtil.colorFromHex("#ff0E1210"),
				ColorUtil.colorFromHex("#ff9AAAA8"),
				ColorUtil.colorFromHex("#ff0E1210"),
//				// superbowl: #0D131B
				ColorUtil.colorFromHex("#ff0D131B"),
		};
		
		int gradientW = P.round(p.width * 0.25f);
		
		PGraphics img = p.createGraphics(gradientW * colors.length, p.height, P.P3D);
		img.smooth(8);
		
		img.beginDraw();
		img.noStroke();
		img.translate(gradientW/2, p.height/2);
		
		for (int i = 0; i < colors.length; i++) {
			Gradients.linear(img, gradientW, p.height, colors[i], colors[(i+1) % colors.length]);
			img.translate(gradientW, 0);
		}
		
		img.endDraw();
		
		// apply blur
		BlurHFilter.instance(p).setBlurByPercent(0.15f, img.width);
		for (int i = 0; i < 10; i++) BlurHFilter.instance(p).applyTo(img);
		
		// add to debug display
		DebugView.setTexture("gradient", img);
		
		// build ticker
		float tickerLoopSpeed = (float) img.width / (float) FRAMES;
		ticker = new TickerScroller(img, p.color(255), gradientW, p.height, tickerLoopSpeed);
	}
	
	protected void drawApp() {
		p.background(255);
		p.noStroke();
		
		// draw buffer to screen
		ticker.update();
		p.image(ticker.image(), 0, 0, p.width, p.height);
//		for (int i = 0; i < 4; i++) BlurHFilter.instance(p).applyTo(p);
	}	

}

