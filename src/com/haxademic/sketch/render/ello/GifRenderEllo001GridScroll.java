package com.haxademic.sketch.render.ello;

import java.awt.image.BufferedImage;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.AnimatedGifEncoder;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

@SuppressWarnings("serial")
public class GifRenderEllo001GridScroll
extends PAppletHax{
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "300" );
		_appConfig.setProperty( "height", "300" );
		_appConfig.setProperty( "rendering", "false" );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-inverse.svg");
		startGifRender();
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
		
		DrawUtil.setDrawCorner(p);
		
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



