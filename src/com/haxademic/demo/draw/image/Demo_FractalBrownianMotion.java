package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.textures.FractalBrownianMotion;

public class Demo_FractalBrownianMotion
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected FractalBrownianMotion fbm;
	
	protected void config() {
		Config.setProperty( AppSettings.FPS, 90 );
		Config.setProperty( AppSettings.RENDERER, P.P3D );
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	public void firstFrame() {

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

