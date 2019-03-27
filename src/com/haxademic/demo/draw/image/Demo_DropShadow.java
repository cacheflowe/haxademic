package com.haxademic.demo.draw.image;

import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.DemoAssets;
import com.jhlabs.image.ShadowFilter;

import processing.core.PImage;

public class Demo_DropShadow 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PImage img;
	PImage imgShadow;
	
	public void setupFirstFrame() {
		img = DemoAssets.smallTexture();
		// img = P.getImage("images/silhouect/sponsor-disabled.png");
		
		// create padded image
		img = ImageUtil.imageToImageWithPadding(img, 2f);
		
		// new Thread(new Runnable() { public void run() {
			// generate drop shadow
			BufferedImage buff = ImageUtil.pImageToBuffered( img );
			BufferedImage dest = ImageUtil.newBufferedImage( img.width, img.height );

			ShadowFilter filt = new ShadowFilter();
			filt.setOpacity(0.85f);
			filt.setDistance(0f);
			filt.setRadius(img.width * 0.2f);
			filt.setShadowOnly(true);
			filt.setShadowColor(0x000000);
			filt.setAddMargins(false);
			filt.filter(buff, dest);

			imgShadow = ImageUtil.bufferedToPImage( dest );
		// }}).start();	
	}
	
	public void drawApp() {
		background(127);
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);
		
		if(imgShadow != null) p.image(imgShadow, 0, 0);
		else background(255, 0, 0);	// still loading
		
		float shadowOsc = 5f + 5f * P.sin(p.frameCount * 0.04f);
		if(p.mousePercentY() < 0.8f) p.image(img, -shadowOsc, -shadowOsc);
	}
}
