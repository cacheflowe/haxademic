package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.ui.UI;

public class Demo_EasingFloat
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String easeFactor = "easeFactor";
	protected EasingFloat easingX = new EasingFloat(0, 6f);
	protected EasingFloat easingY = new EasingFloat(0, 6f);
	protected EasingFloat easingRotation = new EasingFloat(0, 6f);
	protected EasingFloat easingBottom = new EasingFloat(0, 16f);

	protected void config() {
		Config.setProperty( AppSettings.APP_NAME, "Demo_EasingFloat" );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	public void firstFrame() {
		UI.addSlider(easeFactor, 6, 0, 30, 0.1f, false);
	}

	public void drawApp() {
		background(0);
		
		easingX.setEaseFactor(UI.value(easeFactor));
		easingY.setEaseFactor(UI.value(easeFactor));
		easingBottom.setEaseFactor(UI.value(easeFactor));
		
		easingX.setTarget(p.mouseX);
		easingY.setTarget(p.mouseY);
		easingRotation.setTarget(Mouse.xNorm * P.TWO_PI);
		int bottomVal = P.round(p.frameCount * 0.01f) % 2;
		easingBottom.setTarget((bottomVal % 2) * p.width);

		easingX.update();
		easingY.update();
		easingRotation.update(true);
		easingBottom.update(true);
		
		PG.setDrawCenter(p);
		p.fill(255);
		p.pushMatrix();
		p.translate(easingX.value(), easingY.value());
		p.rotate(easingRotation.value());
		p.rect(0, 0, 40, 40);
		p.popMatrix();
		p.ellipse(easingBottom.value(), p.height - 20, 40, 40);
	}

}
