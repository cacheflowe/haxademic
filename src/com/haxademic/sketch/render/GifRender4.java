
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
public class GifRender4
extends PAppletHax{
	
	AnimatedGifEncoder encoder;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "600" );
		_appConfig.setProperty( "height", "600" );
		_appConfig.setProperty( "rendering", "false" );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
//		startGifRender();
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

		if( p.frameCount == 60 ) {
			encoder.finish();
			P.println("gif render done!");
		}
	}
	
	public void drawApp() {
		DrawUtil.setDrawCenter(p);
		DrawUtil.setBasicLights(p);
		p.translate( p.width/2, p.height/2 - 200, 0 );
		
		p.background(0);
		p.rotateX(0.3f);
		
		int steps = 60;
		float oscInc = P.TWO_PI / (float)steps;
		float lineSize = 35 + 10f * P.sin( ( p.frameCount ) * oscInc );
		
		for( int i=250; i > 0; i-- ) {
			
			float rot = P.sin( ( p.frameCount + i ) * oscInc );
			
			if( i % 2 == 0 ) {
				p.fill( 0 );
				p.stroke( 0 );
			} else {
				p.fill( 255 );
				p.stroke( 255 );
			}
			p.noStroke();
//			if( p.frameCount % 2 == 0 ) p.noFill();

			
			float size = i * lineSize * 2f;
			
			p.pushMatrix();
			p.translate(0, 0, -size * 1.1f);
			p.rotate( rot * 1.25f );
			p.box( size, size, size );
			p.popMatrix();
		}
		
//		renderGifFrame();
	}
}



