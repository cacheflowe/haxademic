package com.haxademic.sketch.three_d;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;

public class ConcentricCubes
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	//	protected ControlP5 _cp5;

	protected int _x = 0;
	protected int _y = 0;

	protected int _frames = 40;

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
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		//		_cp5 = new ControlP5(this);
		//		_cp5.addSlider("_x").setPosition(20,60).setWidth(200).setRange(0,p.displayWidth - p.width);
		//		_cp5.addSlider("_y").setPosition(20,100).setWidth(200).setRange(0,p.displayHeight - p.height);
	}

	public void drawApp() {
		background(20);
		DrawUtil.setDrawCenter(p);
		p.noFill();
		p.translate(p.width/2, p.height/2, -1200);

		float percentComplete = ((float)(p.frameCount%_frames)/_frames);

		p.sphereDetail(4);

		//				p.blendMode(P.SCREEN);
//		p.blendMode(P.MULTIPLY);
//		p.blendMode(P.ADD);

		float cubeSize = p.width/1f;
		float cubScaleDown = 300f + 270 * P.sin(P.TWO_PI * percentComplete);
		int i=0;
		while(cubeSize > 10) {
			p.pushMatrix();
			p.stroke(127 + 127f * P.sin(i/20f), 127 + 127f * P.sin(i/30f), 127 + 127f * P.sin(i/10f));
			p.strokeWeight(2.2f);
			//			p.rotateY(0.5f * P.sin(P.TWO_PI * percentComplete + i/numCubes));
			p.rotateY(1.1f * P.sin(P.TWO_PI * percentComplete + i * 0.03f));
			p.rotateZ(2.1f * P.sin(P.TWO_PI * percentComplete + i * 0.03f));
			p.rotateX(1.1f * P.sin(P.TWO_PI * percentComplete + i * 0.03f));
//			p.box(cubeSize);
			p.sphere(cubeSize);
			p.popMatrix();
			cubeSize -= p.width / cubScaleDown;
			i++;
		}

		//		p.filter(P.BLUR);

		if( p.frameCount == _frames * 2 + 2 ) {
			if(p.appConfig.getBoolean("rendering", false) ==  true) {				
				_renderer.stop();
				P.println("render done!");
			}
		}

	}

}



