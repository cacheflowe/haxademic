package com.haxademic.demo.draw.color;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.image.TickerScroller;

import processing.core.PGraphics;

public class Demo_Gradients_textureFromColorArray
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected TickerScroller ticker;
	int FRAMES = 120;
		
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280);
		Config.setProperty(AppSettings.HEIGHT, 720);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES);
	}

	protected void firstFrame() {
		int[] colors = new int[] {
			ColorUtil.colorFromHex("#ffff0000"),
			ColorUtil.colorFromHex("#ff00ff00"),
			ColorUtil.colorFromHex("#ff0000ff"),
		};
		
		// build gradient buffer
		PGraphics gradientTexture = Gradients.textureFromColorArray(512, 8, colors, true); 
		DebugView.setTexture("gradient", gradientTexture);
		
		// build ticker
		float tickerLoopSpeed = (float) gradientTexture.width / (float) FRAMES;
		ticker = new TickerScroller(gradientTexture, p.color(255), gradientTexture.width, gradientTexture.height, tickerLoopSpeed);
	}
	
	public void drawApp() {
		p.background(255);
		p.noStroke();
		
		// draw buffer to screen
		ticker.update();
		p.image(ticker.image(), 0, 0, p.width, p.height);
	}	

}

