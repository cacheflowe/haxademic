package com.haxademic.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PImage;
import processing.core.PShape;

public class GifRenderEllo007FractalSpinRepeat
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	PImage _bread;
	float _frames = 40;
	float _elloSize = 2;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "500" );
		Config.setProperty( AppSettings.HEIGHT, "500" );


		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		Config.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		Config.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		Config.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}
	
	protected void firstFrame() {

		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.haxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.haxademicDataPath()+"svg/ello-inverse.svg");
		_bread = p.loadImage(FileUtil.haxademicDataPath()+"images/bread.png");
	}
	
	protected void drawApp() {
		p.background(255);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutCubic(percentComplete, 0, 1, 1);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);
		
		PG.setDrawCorner(p);
//		PG.setDrawCenter(p);
		p.translate(p.width/2, p.height/2f);


		float rots = 6;
		float radsPerRot = P.TWO_PI / rots;
		
		p.rotate(radsPerRot * easedPercent);
		
		for(int i=0; i < rots; i++) {
			p.rotate(radsPerRot);
			
			_elloSize = (p.width*2 + easedPercent * p.width*4);
			int index = 0;
			while( _elloSize >= 1 ) {
				p.pushMatrix();
				p.translate(_elloSize, 0);
//				if(i%2 == 0) {
//					p.rotate(-P.TWO_PI/4f + easedPercent * P.TWO_PI);
//				} else {
					p.rotate(-P.TWO_PI/4f - easedPercent * P.TWO_PI);
//				}
//				p.ellipse(0, 0, _elloSize, _elloSize);
				p.shape(_logo, 0, 0, _elloSize, _elloSize);
//				p.image(_bread, 0, 0, _elloSize, _elloSize);
				p.popMatrix();
				
				
				_elloSize *= 0.3333f;
				index++;
			}
		}

//		filter(INVERT);

	}
}



