package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;

public class LightsSpecularTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 200 );
		p.appConfig.setProperty( AppSettings.RETINA, true );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}
	
	public void drawApp() {
		background(0);
		noStroke();
		directionalLight(102, 102, 102, 0, 0, -1);
		lightSpecular(204, 204, 204);
		directionalLight(102, 102, 102, 0, 1, -1);
		lightSpecular(102, 102, 102);
		translate(20, 50, 0);
		specular(51, 51, 255);
		sphere(30);
		translate(60, 0, 0);
		specular(102, 255, 102);
		sphere(30);
	}
	

}
