package com.haxademic.sketch.audio;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;

public class EQBandDistribute 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "800" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "600" );
	}


	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );
	}

	public void drawApp() {
		background(0);
		p.noStroke();
		float numElements = p.width;

		float eqStep = 512f / numElements;
		float barW = numElements / 512f;
		int eqIndex = 0;
		for(int i=0; i < numElements; i++) {
			eqIndex = P.floor(i * eqStep);
			float eq = _audioInput.getFFT().spectrum[eqIndex];
			p.fill(255f * eq);
			p.rect(i * barW, 0, barW, p.height);
		}

	}
}
