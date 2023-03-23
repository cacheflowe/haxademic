package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.CopyImage;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ImageUtil_copyImageVsCopyShader 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics dest1;
	protected PGraphics dest2;

	protected void config() {
//		Config.setProperty( AppSettings.WIDTH, 720 );
//		Config.setProperty( AppSettings.HEIGHT, 720 );
	}

	protected void firstFrame() {
		dest1 = PG.newPG(2048, 2048);
		dest2 = PG.newPG(2048, 2048);
	}

	protected void drawApp() {
		background(0);
		
		// grab source image
		PImage sourceTexture = DemoAssets.textureJupiter();
		
		// copy w/shader
		// loser!
		int startTime = p.millis();
		CopyImage.instance().setSourceTexture(sourceTexture);
		CopyImage.instance().setFlipY(true);
		for(int i = 0; i < 100; i++) {
			CopyImage.instance().applyTo(dest1);
		}
		int copyTime = p.millis() - startTime;
		
		// copy w/pixels
		// winner!
		int startTime2 = p.millis();
		for(int i = 0; i < 100; i++) {
			ImageUtil.copyImage(sourceTexture, dest2);
		}
		int copyTime2 = p.millis() - startTime2;
		
		// draw results to screen
		ImageUtil.cropFillCopyImage(dest1, p.g, 10, 10, p.width / 2 - 20, p.height - 20, true);
		ImageUtil.cropFillCopyImage(dest2, p.g, p.width / 2 + 10, 10, p.width / 2 - 20, p.height - 20, true);
		
		// draw copy times
		p.textSize(20);
		p.text(copyTime+"ms", 30, 40);
		p.text(copyTime2+"ms", p.width/2 + 30, 40);
	}
}
