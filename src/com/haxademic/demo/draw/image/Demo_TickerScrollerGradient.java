package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.image.TickerScroller;

import processing.core.PGraphics;

public class Demo_TickerScrollerGradient
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected TickerScroller ticker;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void setup() {
		super.setup();
		int COLOR_1 = p.color(0);
		int COLOR_2 = p.color(127,0,127,0);
		int COLOR_3 = p.color(127,0,0);
		int COLOR_4 = p.color(0,127,127);
		
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

