package com.haxademic.demo.debug;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.text.RandomStringUtil;

public class Demo_DebugView
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float easeFactor = 6f;
	protected EasingFloat _easingX = new EasingFloat(0, 6f);
	protected EasingFloat _easingY = new EasingFloat(0, 6f);

	protected void config() {
		Config.setProperty( AppSettings.FPS, 90 );
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 400 );
		Config.setProperty( AppSettings.APP_NAME, "DebugView" );
		Config.setProperty( AppSettings.RENDERER, PRenderers.JAVA2D );
	}

	protected void firstFrame() {
		DebugView.active(true);
		DebugView.setHelpLine("Test info here", "");
		DebugView.setHelpLine("[key]", "[Value]");
		
		// add a bunch of long strings to test value length
		for (int i = 0; i < 20; i++) {
			DebugView.setValue("test val "+i, RandomStringUtil.randomStringOfLength(MathUtil.randRange(5, 50), RandomStringUtil.ALPHANUMERIC));
		}
	}

	protected void drawApp() {
		background(0);

		DebugView.setValue("Max mouse", P.max(p.mouseX, p.mouseY));
		DebugView.setTexture("smallTexture", DemoAssets.smallTexture());
		DebugView.setTexture("app", p.g);
		
		_easingX.setEaseFactor(easeFactor);
		_easingY.setEaseFactor(easeFactor);
		
		_easingX.setTarget(p.mouseX);
		_easingY.setTarget(p.mouseY);

		_easingX.update();
		_easingY.update();
		
		PG.setDrawCenter(p);
		p.fill(255);
		p.ellipse(_easingX.value(), _easingY.value(), 40, 40);
	}

}
