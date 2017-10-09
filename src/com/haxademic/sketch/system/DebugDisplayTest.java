package com.haxademic.sketch.system;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class DebugDisplayTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float easeFactor = 6f;
	protected EasingFloat _easingX = new EasingFloat(0, 6f);
	protected EasingFloat _easingY = new EasingFloat(0, 6f);

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, 90 );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.APP_NAME, "Debug Display Test" );
	}

	public void setup() {
		super.setup();
		p.showStats = true;
		p.debugView.addHelpLine("Test info here", "");
		p.debugView.addHelpLine("[key]", "[Value]");
	}

	public void drawApp() {
		background(0);
		
		p.debugView.addValue("p.mouseX", p.mouseX);
		p.debugView.addValue("p.mouseY", p.mouseY);
		
		_easingX.setEaseFactor(easeFactor);
		_easingY.setEaseFactor(easeFactor);
		
		_easingX.setTarget(p.mouseX);
		_easingY.setTarget(p.mouseY);

		_easingX.update();
		_easingY.update();
		
		DrawUtil.setDrawCenter(p);
		p.fill(255);
		p.ellipse(_easingX.value(), _easingY.value(), 40, 40);
	}

}
