package com.haxademic.render.ello;

import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.system.SystemUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class GifRenderEllo008VertexPointsTrain
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PShape _logo, _logoOrig;
	float _frames = 16;
	float _elloSize = 2;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "640" );
		Config.setProperty( AppSettings.HEIGHT, "640" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF, "false" );
	}
	
	public void firstFrame() {

		p.background(0);
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-centered-complex-lofi.svg");
//		_logo.scale(5);
//		_logoOrig = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		if(Config.getBoolean("rendering_gif", false) == true) startGifRender();
	}
	
	public void startGifRender() {
		encoder = new AnimatedGifEncoder();
		encoder.start( FileUtil.getHaxademicOutputPath() + SystemUtil.getTimestamp() + "-export.gif" );
		encoder.setFrameRate( 45 );
		encoder.setQuality( 15 );
		encoder.setRepeat( 0 );
	}
		
	public void renderGifFrame() {
		PImage screenshot = get();
		BufferedImage newFrame = (BufferedImage) screenshot.getNative();
		encoder.addFrame(newFrame);
	}

	public void drawApp() {
		p.fill(0, 40);
		p.rect(0, 0, p.width, p.height);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutCubic(percentComplete, 0, 1, 1);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);
		
		PG.setDrawCorner(p);
		p.translate(p.width/2f - 10, p.height/2f - 5);


		float rots = 6;
		float radsPerRot = P.TWO_PI / rots;		
			
		_elloSize = p.width/2f;
		float amp = 3;
		
		for (int j = 0; j < _logo.getChildCount(); j++) {
			for (int i = 0; i < _logo.getChild(j).getVertexCount() - 1; i++) {
				PVector v = _logo.getChild(j).getVertex(i);
				PVector vNext = _logo.getChild(j).getVertex(i+1);

			    p.fill(255);
			    p.noStroke();
			    	
			    if(i != 48) {
				    p.ellipse(
				    		P.lerp(v.x * amp, vNext.x * amp, percentComplete), 
				    		P.lerp(v.y * amp, vNext.y * amp, percentComplete), 
				    		3.4f, 
				    		3.4f
				    );
			    }
			    
////				float amp = 1 + 0.9f * AudioIn.getEqAvgBand( P.floor(_spectrumInterval * spectrumIndex) );
//				float amp = P.sin(i * 0.01f) * 100f;
////				PVector v = _logo.getChild(j).getVertex(i);
//				PVector vOrig = _logoOrig.getChild(j).getVertex(i);
////				v.set(vOrig.x * amp, vOrig.y * amp, vOrig.z * amp);
////				P.println(amp,i,v.x,v.y,v.z);
//				_logo.getChild(j).setVertex(i,vOrig.x * amp, vOrig.y * amp, vOrig.z * amp);
			}
		}
	

//		filter(BLUR);

		if(Config.getBoolean("rendering_gif", false) == true) {
			if(p.frameCount > _frames * 2) renderGifFrame();
		}
		if( p.frameCount == _frames * 20 ) {
			if(Config.getBoolean("rendering_gif", false) ==  true) encoder.finish();
			if(isRendering == true) {				
				videoRenderer.stop();
				P.println("render done!");
			}
		}

	}
}



