package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class Demo_Shapes_drawDiscAudio 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}
	
	public void drawApp() {
		AudioIn.instance();
		
		// context
		p.background(0);
		p.noStroke();
		PG.setBetterLights(p);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);
		
		// draw audio disc
		p.pushMatrix();
		Shapes.drawDiscAudio(p.g, 300, 280, AudioIn.waveform.length, 30, false);
		p.popMatrix();
	}
}
