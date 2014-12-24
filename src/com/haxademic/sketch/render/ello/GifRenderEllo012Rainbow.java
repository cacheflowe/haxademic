package com.haxademic.sketch.render.ello;

import processing.core.PShape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.AnimatedGifEncoder;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class GifRenderEllo012Rainbow
extends PAppletHax{
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	int _frames = 100;
	
	int[] _colors;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "500" );
		_appConfig.setProperty( "height", "500" );

		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "45" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+(_frames+1) );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		_logo.disableStyle();
		_logoInverse = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-inverse.svg");
		
		_colors = new int[]{
			ColorUtil.colorFromHex("#FF0000"),
			ColorUtil.colorFromHex("#FF4A00"),
			ColorUtil.colorFromHex("#FFFF08"),
			ColorUtil.colorFromHex("#006F08"),
			ColorUtil.colorFromHex("#0000FB"),
			ColorUtil.colorFromHex("#350074"),
			ColorUtil.colorFromHex("#B217FE")
		};
	}
	
	public void drawApp() {
		p.background(255);
//		p.rect(0, 0, p.width, p.height);
		p.noStroke();
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		float curColorPercent = percentComplete * (float) _colors.length;
		int curColorIndex = P.floor( curColorPercent );
		
		float remainder = curColorPercent - curColorIndex;
		float easedPercent = Penner.easeInOutQuart(remainder, 0, 1, 1);

		float elloSize = (float)(p.width/1.5f);
		
		DrawUtil.setDrawCorner(p);
		
		p.translate(p.width/2, p.height/2);
//		p.rotate(frameRadians * p.frameCount);
//		p.rotate(easedPercent * PConstants.TWO_PI);
		int curColor = p.lerpColor(_colors[curColorIndex], _colors[(curColorIndex+1) % _colors.length], easedPercent) ;
		p.fill(curColor);
		p.shape(_logo, 0, 0, elloSize, elloSize);
	}
}



