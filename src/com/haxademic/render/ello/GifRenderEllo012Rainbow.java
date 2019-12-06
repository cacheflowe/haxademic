package com.haxademic.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PShape;

public class GifRenderEllo012Rainbow
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	int _frames = 100;
	
	int[] _colors;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "500" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "500" );

		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "45" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+(_frames+1) );
	}
	
	public void setupFirstFrame() {

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
		
		PG.setDrawCorner(p);
		
		p.translate(p.width/2, p.height/2);
//		p.rotate(frameRadians * p.frameCount);
//		p.rotate(easedPercent * PConstants.TWO_PI);
		int curColor = p.lerpColor(_colors[curColorIndex], _colors[(curColorIndex+1) % _colors.length], easedPercent) ;
		p.fill(curColor);
		p.shape(_logo, 0, 0, elloSize, elloSize);
	}
}



