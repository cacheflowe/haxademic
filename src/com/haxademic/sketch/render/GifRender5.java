
package com.haxademic.sketch.render;

import java.awt.image.BufferedImage;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.AnimatedGifEncoder;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PImage;

public class GifRender5
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "600" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "600" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.SUNFLOW, "true" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, "false" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, "high" );
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
//		DrawUtil.setDrawCenter(p);
		DrawUtil.setBasicLights(p);
//		p.translate( p.width/2, p.height/2, -1600 );
		
		p.background(0);
		joons.jr.background(JoonsWrapper.BACKGROUND_GI);
//		setUpRoom();
		

		p.translate( 0, -800, -4000 );
		p.rotateX(P.PI*0.5f);

		int steps = 30;
		float oscInc = P.TWO_PI / (float)steps;
		float lineSize = 50 + 1 * P.sin( ( p.frameCount ) * oscInc );
		
		for( int i=30; i > 0; i-- ) {
			
			float rot = P.sin( ( p.frameCount + i ) * oscInc );
			
			if( i % 2 == 0 ) {
				p.fill( 0 );
				joons.jr.fill(JoonsWrapper.MATERIAL_DIFFUSE, 0, 0, 0);
			} else {
				p.fill( 255 );
				joons.jr.fill(JoonsWrapper.MATERIAL_DIFFUSE, 200, 200, 200);
			}
//			if( p.frameCount % 2 == 0 ) p.noFill();
			p.noStroke();
			
			float size = i * lineSize * 3f;
			
			p.pushMatrix();
			p.translate(0, 0, -size * 0.9f);
			p.rotateZ( rot * .2f );
//			p.box( size, size, size );
//			Shapes.drawPyramid(p, size*0.6f, size, true);
			Shapes.drawStar(p, 8f, size*0.5f, 0.7f, size*0.4f, 0);
			p.popMatrix();
		}
		
		renderGifFrame();
	}
	
	protected void setUpRoom() {
		pushMatrix();
		translate(0, 0, -3000);
		float radiance = 20;
		int samples = 16;
		joons.jr.background("cornell_box", 
				4000, 3000, 3000,	// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				40, 40, 40, // left rgb
				40, 40, 40, // right rgb
				60, 60, 60, // back rgb
				60, 60, 60, // top rgb
				60, 60, 60  // bottom rgb
		); 
		popMatrix();		
	}
}



