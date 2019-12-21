package com.haxademic.render;

import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PImage;

public class GifRender
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "200" );
		Config.setProperty( AppSettings.HEIGHT, "200" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}
	
	public void firstFrame() {

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

		if( p.frameCount == 40 ) {
			encoder.finish();
			P.println("gif render done!");
		}
	}

	public void drawApp() {
		p.background(0);
		p.noStroke();
		
		int barW = 20;
		int x = p.frameCount;
		
		for( int y=0; y < p.height; y+=barW*2 ) {
			for( int i=x - barW*2; i < p.width; i+=barW*2 ) {
				p.fill( 0 );
				p.rect(i, y, barW, barW );
				p.fill( 255 );
				p.rect(i+barW, y, barW, barW );
			}
			for( int i=p.width + barW - x; i > -barW*2; i-=barW*2 ) {
				p.fill( 0 );
				p.rect(i, y+barW, barW, barW );
				p.fill( 255 );
				p.rect(i+barW, y+barW, barW, barW );
			}
		}

		renderGifFrame();
	}
}



