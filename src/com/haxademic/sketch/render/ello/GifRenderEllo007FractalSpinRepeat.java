package com.haxademic.sketch.render.ello;

import processing.core.PImage;
import processing.core.PShape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.AnimatedGifEncoder;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class GifRenderEllo007FractalSpinRepeat
extends PAppletHax{
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	PImage _bread;
	float _frames = 40;
	float _elloSize = 2;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "500" );
		_appConfig.setProperty( "height", "500" );


		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+1) );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-inverse.svg");
		_bread = p.loadImage(FileUtil.getHaxademicDataPath()+"images/bread.png");
	}
	
	public void drawApp() {
		p.background(255);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutCubic(percentComplete, 0, 1, 1);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);
		
		DrawUtil.setDrawCorner(p);
//		DrawUtil.setDrawCenter(p);
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



