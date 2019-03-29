package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;

public class Demo_Shapes_drawDiscAudio 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ImageGradient imageGradient;
	
	protected void overridePropsFile() {
		int FRAMES = 140;
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES );
	}
	
	public void setupFirstFrame() {
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
	}

	public void drawApp() {
		// context
		p.background(0);
		p.noStroke();
		DrawUtil.setBetterLights(p);
		DrawUtil.setCenterScreen(p);
		DrawUtil.basicCameraFromMouse(p.g);
		
		// draw audio disc
		p.pushMatrix();
		Shapes.drawDiscAudio(p.g, 300, 280, p.audioData.waveform().length, 30, false);
		p.popMatrix();
	}
}
