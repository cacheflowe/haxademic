package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.math.MathUtil;

public class GradientGenerator
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	float dcOffsetR = 0f;
	float dcOffsetG = 0f;
	float dcOffsetB = 0f;
	float ampR = 0f;
	float ampG = 0f;
	float ampB = 0f;
	float freqR = 0f;
	float freqG = 0f;
	float freqB = 0f;
	float phaseR = 0f;
	float phaseG = 0f;
	float phaseB = 0f;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 255 );
		p.appConfig.setProperty( AppSettings.FPS, 30 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1500 );
	}

	public void setup() {
		super.setup();	
		p.noStroke();
		
		randomParams();
	}
	
	protected float newDC() {
		return MathUtil.randRangeDecimal(-127f, 255f);
	}
	
	protected float newAmp() {
		return MathUtil.randRangeDecimal(0, 255f);
	}
	
	protected float newFreq() {
		return MathUtil.randRangeDecimal(0.5f, 3f);
	}
	
	protected float newPhase() {
		return MathUtil.randRangeDecimal(0f, P.TWO_PI);
	}
	
	protected void randomParams() {
		dcOffsetR = newDC();
		dcOffsetG = newDC();
		dcOffsetB = newDC();
		ampR = newAmp();
		ampG = newAmp();
		ampB = newAmp();
		freqR = newFreq();
		freqG = newFreq();
		freqB = newFreq();
		phaseR = newPhase();
		phaseG = newPhase();
		phaseB = newPhase();
	}
	
	protected void oscillateParams() {
		float frame = (float)p.frameCount;
		dcOffsetR = P.sin(frame / 140f) * 45f + 25f;
		dcOffsetG = P.sin(frame / 70f) * 45f + 25f;
		dcOffsetB = P.sin(frame / 50f) * 45f + 25f;
		ampR = P.sin(frame / 190f) * 127f + 127f;
		ampG = P.sin(frame / 370f) * 127f + 127f;
		ampB = P.sin(frame / 250f) * 127f + 127f;
		freqR = P.sin(frame / 360f) * 0.5f + 0.7f;
		freqG = P.sin(frame / 280f) * 0.5f + 0.7f;
		freqB = P.sin(frame / 110f) * 0.5f + 0.7f;
		phaseR = P.sin(frame / 280f) * P.PI + P.TWO_PI;
		phaseG = P.sin(frame / 390f) * P.PI + P.TWO_PI;
		phaseB = P.sin(frame / 140f) * P.PI + P.TWO_PI;
	}
	
	public void keyPressed() {
		if( p.key == ' ' ) randomParams();
	}

	public void drawApp() {
		oscillateParams();
		for(int x = 0; x < p.width; x++) {
			float r = ColorUtil.gradientComponent(dcOffsetR, ampR, freqR, phaseR, p.frameCount/100f + P.map(x, 0, p.width, 0, P.TWO_PI));
			float g = ColorUtil.gradientComponent(dcOffsetG, ampG, freqG, phaseG, p.frameCount/100f + P.map(x, 0, p.width, 0, P.TWO_PI));
			float b = ColorUtil.gradientComponent(dcOffsetB, ampB, freqB, phaseB, p.frameCount/100f + P.map(x, 0, p.width, 0, P.TWO_PI));
			// draw gradient
			p.fill(r, g, b);
			p.rect(x, 0, 5, p.height);
			// draw debug overlay wave
			p.fill(255, 0, 0);
			p.rect(x, p.height - r, 1, 1);
			p.fill(0, 255, 0);
			p.rect(x, p.height - g, 1, 1);
			p.fill(0, 0, 255);
			p.rect(x, p.height - b, 1, 1);
		}
	}
	
}
