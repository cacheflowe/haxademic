package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.HaxMotionBlur;
import com.haxademic.core.draw.image.DrawCommand.Command;
import com.haxademic.core.math.easing.Penner;

public class RoundedRectTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int _frames = 60;
	HaxMotionBlur _motionBlur;

	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "800" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "800" );


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
		_motionBlur = new HaxMotionBlur(3);
	}

	public void drawApp() {
		p.background(255);
//		_motionBlur.render(p.g, new DrawCommand());
		drawFrame();
		
		if( p.frameCount == _frames * 2 + 2 ) {
			if(p.appConfig.getBoolean("rendering", false) ==  true) {				
				movieRenderer.stop();
				P.println("render done!");
			}
		}

	}
	
    public class DrawCommand implements Command {
    	public void execute(Object data, float t) {
    		drawFrame();
    	}    
    }
    
	public void drawFrame() {
		p.pushMatrix();

		DrawUtil.setDrawCenter(p);
		
//		p.fill(255);
		p.noFill();
		p.stroke(0);
		p.strokeWeight(1.5f);
		p.translate(p.width/2, p.height/2);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete, 0, 1, 1);
		// float radsComplete = percentComplete * P.TWO_PI;


		float spacing = 3.9925f; //P.map(p.mouseX, 0, p.width, 1, 10f);
		// P.println(spacing);
		float baseSize = p.width * 0.5f;
		float j = 0;
		
		for (float i = baseSize; i > baseSize * 0.7f; i -= spacing) {
			float curPercent = easedPercent + j * (1f/_frames);
			
			float w = baseSize + (baseSize/2f) * P.sin(curPercent * P.TWO_PI);
			float h = baseSize + (baseSize/2f) * P.cos(curPercent * P.TWO_PI);
			
			float rounded = (h > w) ? h/2f : w/2f;
			
			p.pushMatrix();
//			p.rotate(P.sin(curPercent * P.TWO_PI) * 0.2f);
			p.rect(0, 0, w, h, rounded, rounded, rounded, rounded);
//			p.rect(0, 0, w, w);
			p.popMatrix();
			
			j++;
		}
		p.popMatrix();
	}
}
