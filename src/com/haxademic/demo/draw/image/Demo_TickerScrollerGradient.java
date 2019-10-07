package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.image.TickerScroller;

import processing.core.PGraphics;

public class Demo_TickerScrollerGradient
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected TickerScroller ticker;
	protected PGraphics gradientImg;

	protected int[] colors = new int[] {
			ColorUtil.colorFromHex("#ff000000"),
			ColorUtil.colorFromHex("#ff471D6C"),
			ColorUtil.colorFromHex("#ffF5AB2C"),
		};
	protected int gradientW;
	
	int FRAMES = 400;
		
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES);
	}

	public void setupFirstFrame() {
		
		gradientW = P.round(p.width * 0.25f);
		
		gradientImg = p.createGraphics(gradientW * colors.length, p.height, PRenderers.P3D);
		gradientImg.smooth(8);
		
		// draw gradient
		gradientImg.beginDraw();
		gradientImg.noStroke();
		gradientImg.translate(gradientW/2, p.height/2);
		
		for (int i = 0; i < colors.length; i++) {
			Gradients.linear(gradientImg, gradientW, p.height, colors[i], colors[(i+1) % colors.length]);
			gradientImg.translate(gradientW, 0);
		}
		
		// apply blur
		BlurHFilter.instance(p).setBlurByPercent(0.15f, gradientImg.width);
		for (int i = 0; i < 10; i++) BlurHFilter.instance(p).applyTo(gradientImg);
		
		gradientImg.endDraw();
		
		
		// add to debug display
		p.debugView.setTexture("gradientImg", gradientImg);
		
		// build ticker
		float tickerLoopSpeed = (float) gradientImg.width / (float) FRAMES;
		ticker = new TickerScroller(gradientImg, p.color(255), gradientW, p.height, tickerLoopSpeed);
	}
	
	public void drawApp() {

		p.background(255);
		p.noStroke();
		
		// draw buffer to screen
		ticker.update();
		p.image(ticker.image(), 0, 0, p.width, p.height);
	}	
}

