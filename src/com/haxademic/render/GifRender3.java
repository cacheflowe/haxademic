package com.haxademic.render;

import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class GifRender3
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PGraphics _graphics;
	PGraphics _mask;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "200" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "200" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}
	
	public void setup() {
		super.setup();
		startGifRender();
		
		_graphics = p.createGraphics( p.width, p.height, P.P2D );
		_mask = p.createGraphics( p.width, p.height, P.P2D );
	}
	
	public void startGifRender() {
		encoder = new AnimatedGifEncoder();
		encoder.start( FileUtil.getHaxademicOutputPath() + SystemUtil.getTimestamp(p) + "-export.gif" );
		encoder.setFrameRate( 40 );
		encoder.setRepeat( 0 );
	}
		
	public void renderGifFrame() {
		PImage screenshot = get();
		BufferedImage newFrame = (BufferedImage) screenshot.getNative();
		encoder.addFrame(newFrame);

		if( p.frameCount == 41 ) {
			encoder.finish();
			P.println("gif render done!");
		}
	}

	public void drawApp() {
		_graphics.background(0);
		_graphics.noStroke();
		
		int barW = 20;
		int x = p.frameCount % 40;
		
		
		_graphics.beginDraw();
		for( int y=0; y < _graphics.height; y+=barW*2 ) {
			for( int i=x - barW*2; i < _graphics.width; i+=barW*2 ) {
				_graphics.fill( 0 );
				_graphics.rect(i, y, barW, barW );
				_graphics.fill( 255 );
				_graphics.rect(i+barW, y, barW, barW );
			}
			for( int i=_graphics.width + barW - x; i > -barW*2; i-=barW*2 ) {
				_graphics.fill( 0 );
				_graphics.rect(i, y+barW, barW, barW );
				_graphics.fill( 255 );
				_graphics.rect(i+barW, y+barW, barW, barW );
			}
		}
		_graphics.endDraw();

		// draw increasingly smaller concentric masks and flip the inage for each
		_mask.beginDraw();
		_mask.clear();
		_mask.noStroke();
		_mask.fill(255);
		_mask.rect(0, 0, p.width, p.height );
		_mask.endDraw();
		_graphics.mask(_mask);
		p.image(_graphics, 0, 0);
		
		// mask 1
		_mask.beginDraw();
		_mask.clear();
		_mask.fill(255);
		_mask.rect(barW, barW, p.width - barW * 2, p.height - barW * 2 );
		_mask.endDraw();
		
		p.pushMatrix();
		p.translate(p.width, 0);
		p.rotateY(P.PI);
		_graphics.mask(_mask);
		p.image(_graphics, 0, 0);
		p.popMatrix();

		// mask 2
		_mask.beginDraw();
		_mask.clear();
		_mask.fill(255);
		_mask.rect(barW * 2, barW * 2, p.width - barW * 4, p.height - barW * 4 );
		_mask.endDraw();
		
		p.pushMatrix();
		_graphics.mask(_mask);
		p.image(_graphics, 0, 0);
		p.popMatrix();
		
		// mask 3
		_mask.beginDraw();
		_mask.clear();
		_mask.fill(255);
		_mask.rect(barW * 3, barW * 3, p.width - barW * 6, p.height - barW * 6 );
		_mask.endDraw();
		
		p.pushMatrix();
		p.translate(p.width, 0);
		p.rotateY(P.PI);
		_graphics.mask(_mask);
		p.image(_graphics, 0, 0);
		p.popMatrix();
		
		// mask 4
		_mask.beginDraw();
		_mask.clear();
		_mask.fill(255);
		_mask.rect(barW * 4, barW * 4, p.width - barW * 8, p.height - barW * 8 );
		_mask.endDraw();
		
		p.pushMatrix();
		_graphics.mask(_mask);
		p.image(_graphics, 0, 0);
		p.popMatrix();
		
		if( p.frameCount > 1 ) renderGifFrame();
	}
}



