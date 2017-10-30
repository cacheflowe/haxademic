package com.haxademic.sketch.buffer;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.image.TickerScroller;
import com.haxademic.core.draw.shapes.Gradients;

import processing.core.PGraphics;

public class TickerScrollerGradientTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// gradient colors
	protected int COLOR_1 = ColorUtil.colorFromHex("#7B73DB");
	protected int COLOR_2 = ColorUtil.colorFromHex("#9B6CBB");
	protected int COLOR_3 = ColorUtil.colorFromHex("#FC655F");
	protected int COLOR_4 = ColorUtil.colorFromHex("#FD8C6B");

	protected TickerScroller ticker;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.FPS, 30 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 311 );
	}

	public void setup() {
		super.setup();
		PGraphics img = p.createGraphics(p.width * 4, p.height, P.P2D);
		img.beginDraw();
		img.noStroke();
		img.translate(p.width/2, p.height/2);
		Gradients.linear(img, p.width, p.height, COLOR_1, COLOR_3);
		img.translate(p.width, 0);
		Gradients.linear(img, p.width, p.height, COLOR_3, COLOR_2);
		img.translate(p.width, 0);
		Gradients.linear(img, p.width, p.height, COLOR_2, COLOR_4);
		img.translate(p.width, 0);
		Gradients.linear(img, p.width, p.height, COLOR_4, COLOR_1);
		img.endDraw();
		BlurHFilter.instance(p).setBlurByPercent(0.5f, img.width);
		BlurHFilter.instance(p).setBlurByPercent(0.5f, img.width);
		BlurHFilter.instance(p).setBlurByPercent(0.5f, img.width);
		BlurHFilter.instance(p).setBlurByPercent(0.5f, img.width);
		ticker = new TickerScroller(img, p.color(255), p.width, p.height, 4.f);
	}
	
	public void drawApp() {
		p.background(255);
		p.noStroke();
		
		// draw buffer to screen
		ticker.update();
		p.image(ticker.image(), 0, 0);
	}	

}

