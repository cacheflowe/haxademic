package com.haxademic.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.DrawCommand.Command;
import com.haxademic.core.draw.image.HaxMotionBlur;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PImage;
import processing.core.PShape;

public class GifRenderEllo016SpinLine
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PShape _logo;
	PShape _logoInverse;
	PImage _bread;
	float _frames = 40;
	float _elloSize = 2;
	HaxMotionBlur _motionBlur;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "640" );


		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-inverse.svg");
		_bread = p.loadImage(FileUtil.getHaxademicDataPath()+"images/bread.png");
		
		_motionBlur = new HaxMotionBlur(3);
	}
	
	public void drawApp() {
		p.background(255);
//		_motionBlur.render(this, new DrawCommand());
		drawFrame();
	}
	
	
    public class DrawCommand implements Command {
    	public void execute(Object data, float t) {
    		drawFrame();
    	}    
    }
    
	public void drawFrame() {
		p.pushMatrix();
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutCubic(percentComplete, 0, 1, 1);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);
		
		PG.setDrawCorner(p);
//		PG.setDrawCenter(p);
		p.translate(p.width/2, p.height/2f);


		float rots = 18;
		float radius = width/3f;
		float radsPerRot = P.TWO_PI / rots;
		float _elloSize = MathUtil.getDistance(P.sin(0) * radius, P.cos(0) * radius, P.sin(radsPerRot) * radius, P.cos(radsPerRot) * radius); // find distance between 2 logos
		
		p.rotate(radsPerRot * percentComplete);
		
		for(float i=0; i < rots; i++) {
			float curRads = i * radsPerRot;
			float osc = P.sin(curRads + (P.TWO_PI - radsPerRot) * percentComplete) / 5f;
			float rotRads = percentComplete * -P.TWO_PI;
			p.pushMatrix();
			p.translate(P.sin(curRads) * radius, P.cos(curRads) * radius);
			p.rotate(-curRads + rotRads);
//			p.rotate(-curRads);
			p.scale(0.8f + osc);
			p.shape(_logo, 0, 0, _elloSize, _elloSize);
//			p.fill(127, 127);
//			p.rect(0, 0, _elloSize, _elloSize);
			p.popMatrix();
		}
		p.popMatrix();
//		filter(INVERT);

	}
}



