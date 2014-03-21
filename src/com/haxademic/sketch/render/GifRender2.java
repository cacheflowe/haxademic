package com.haxademic.sketch.render;

import java.awt.image.BufferedImage;

import processing.core.PImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.AnimatedGifEncoder;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

@SuppressWarnings("serial")
public class GifRender2
extends PAppletHax{
	
	AnimatedGifEncoder encoder;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "300" );
		_appConfig.setProperty( "height", "300" );
		_appConfig.setProperty( "rendering", "false" );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
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

		if( p.frameCount == 30 ) {
			encoder.finish();
			P.println("gif render done!");
		}
	}
	
	public void drawApp() {
		DrawUtil.setDrawCenter(p);
		p.translate( p.width/2, p.height/2, 0 );

		p.background(0);
		
		int steps = 30;
		float oscInc = P.TWO_PI / (float)steps;
		float lineSize = 25 + 1f * P.sin( ( p.frameCount ) * oscInc );
		
		for( int i=100; i > 0; i-- ) {
			
			float rot = P.sin( ( p.frameCount + i ) * oscInc );
			
			if( i % 2 == 0 ) {
				p.fill( 0 );
				p.stroke( 0 );
			} else {
				p.fill( 255 );
				p.stroke( 255 );
			}
			
			p.pushMatrix();
			p.rotate( rot * 0.4f );
			p.rect( 0, 0, i * lineSize, i * lineSize );
			p.popMatrix();
		}
		
		renderGifFrame();
	}
}



