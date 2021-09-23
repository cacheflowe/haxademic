package com.haxademic.demo.debug;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.text.RandomStringUtil;

public class Demo_DebugView_Occasional
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
//		Config.setProperty( AppSettings.RENDERER, PRenderers.JAVA2D );	// why doesn't DebugView work in JAVA2D renderer?
	}

	protected void firstFrame() {
		DebugView.active(true);
		DebugView.switchToManual();
		DebugView.setHelpLine("Test info here", "");
		DebugView.setHelpLine("[key]", "[Value]");
		
		// add a bunch of long strings to test value length
		for (int i = 0; i < 20; i++) {
			DebugView.setValue("test val "+i, RandomStringUtil.randomStringOfLength(MathUtil.randRange(5, 50), RandomStringUtil.ALPHANUMERIC));
		}
	}

	protected void drawApp() {
		// only redraw once every 120 frames, because NUC performance is so bad showing DebugView
		// can't use SMOOTH_NONE on main renderer!
		boolean shouldDrawDebug = FrameLoop.frameModLooped(120);
		DebugView.active(shouldDrawDebug);
		
		// main app canvas context setup
		if(shouldDrawDebug) p.background(0);	// only clear on frames when we're going to draw DebugView 
		
		
		DebugView.setValue("Max mouse", P.max(p.mouseX, p.mouseY));
		DebugView.setTexture("smallTexture", DemoAssets.smallTexture());
		DebugView.setTexture("squareTexture", DemoAssets.squareTexture());
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
		
		if(shouldDrawDebug) DebugView.instance().post();
	}

}
