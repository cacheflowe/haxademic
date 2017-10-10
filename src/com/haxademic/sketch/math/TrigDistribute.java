package com.haxademic.sketch.math;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;

import controlP5.ControlP5;

public class TrigDistribute
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public int numPoints = 0;
	public int connectionDivisions = 0;
	public float radius = 0;
	protected ControlP5 _cp5;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "60" );
	}

	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);

		_cp5 = new ControlP5(this);
		_cp5.addSlider("numPoints").setPosition(20,60).setWidth(200).setRange(1,90).setValue(3);
		_cp5.addSlider("radius").setPosition(20,100).setWidth(200).setRange(0,300).setValue(100);
		_cp5.addSlider("connectionDivisions").setPosition(20,140).setWidth(200).setRange(0,20).setValue(2);
	}

	public void drawApp() {
		background(0);
		DrawUtil.setDrawCenter(p);
		p.fill(255);
		p.stroke(255);
		p.strokeWeight(2);

		int centerX = p.width / 2;
		int centerY = p.height / 2;
		float segmentRadians = P.TWO_PI / numPoints;
		for( int i=0; i < numPoints; i++ ) {
			float amp = 1 + 1*_audioInput.getFFT().spectrum[i%numPoints];
			float x = centerX + P.sin(segmentRadians * i) * radius * amp;
			float y = centerY + P.cos(segmentRadians * i) * radius * amp;			
			p.ellipse(x, y, 10, 10);
			
			// connect lines
			for( int j=1; j <= connectionDivisions; j++ ) {
				float xDiv = centerX + P.sin(segmentRadians * ((i+numPoints/j)%numPoints)) * radius * amp;
				float yDiv = centerY + P.cos(segmentRadians * ((i+numPoints/j)%numPoints)) * radius * amp;			
				p.line(x, y, xDiv, yDiv);
			}
		}
	}
}
