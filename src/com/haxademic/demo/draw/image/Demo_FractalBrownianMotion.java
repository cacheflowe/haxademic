package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.image.FractalBrownianMotion;

public class Demo_FractalBrownianMotion
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected FractalBrownianMotion fbm;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, 90 );
		p.appConfig.setProperty( AppSettings.RENDERER, P.P3D );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	public void setup() {
		super.setup();
		fbm = new FractalBrownianMotion(p);
	}

	public void drawApp() {
		noStroke();
		for(int i=0; i < 100; i++){
			for(int j=0; j < 100; j++){
				fill(200 * fbm.f(i, j));
				rect(i*8,j*8,8,8);
			}
		}
	}

	

}

