package com.haxademic.render.ello;

import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PImage;
import processing.core.PShape;

public class GifRenderEllo001GridScroll
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "300" );
		Config.setProperty( AppSettings.HEIGHT, "300" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}
	
	public void firstFrame() {

		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-inverse.svg");
		startGifRender();
	}
	
	public void startGifRender() {
		encoder = new AnimatedGifEncoder();
		encoder.start( FileUtil.getHaxademicOutputPath() + SystemUtil.getTimestamp() + "-export.gif" );
		encoder.setFrameRate( 40 );
		encoder.setRepeat( 0 );
	}
		
	public void renderGifFrame() {
		PImage screenshot = get();
		BufferedImage newFrame = (BufferedImage) screenshot.getNative();
		encoder.addFrame(newFrame);

		if( p.frameCount == 100 ) {
			encoder.finish();
			P.println("gif render done!");
		}
	}

	public void drawApp() {
//		if(p.frameCount % 2 == 0) {
//			p.background(0);
//		} else {
			p.background(255);
//		}
		p.noStroke();
		
		int barW = 50;
		int barWHalf = Math.round(barW/2f);
		int x = p.frameCount;
		
		PG.setDrawCorner(p);
		
		for( int y=0; y < p.height; y+=barW*2 ) {
			for( int i=x - barW*2; i < p.width; i+=barW*2 ) {
				p.fill( 0 );
				p.ellipse(barWHalf + i, barWHalf + y, barW, barW );
				p.fill( 255 );
				p.ellipse(barWHalf + i+barW, barWHalf + y, barW, barW );
				
				// p.rotate(((p.frameCount%100)/100f) * PConstants.TWO_PI);
				p.shape(_logoInverse, barWHalf + i, barWHalf + y, barW, barW);
				p.shape(_logo, barWHalf + i+barW, barWHalf + y, barW, barW);
			}
			for( int i=p.width + barW - x; i > -barW*2; i-=barW*2 ) {
				p.fill( 0 );
				p.ellipse(barWHalf + i, barWHalf + y+barW, barW, barW );
				p.fill( 255 );
				p.ellipse(barWHalf + i+barW, barWHalf + y+barW, barW, barW );

				p.shape(_logoInverse, barWHalf + i, barWHalf + y+barW, barW, barW);
				p.shape(_logo, barWHalf + i+barW, barWHalf + y+barW, barW, barW);
}
		}

		renderGifFrame();
	}
}



