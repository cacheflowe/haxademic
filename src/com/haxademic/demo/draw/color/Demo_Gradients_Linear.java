package com.haxademic.demo.draw.color;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.color.Gradients;

public class Demo_Gradients_Linear
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected EasingColor _colorStart;
	protected EasingColor _colorStop;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "600" );
		Config.setProperty( AppSettings.HEIGHT, "600" );
	}

	protected void firstFrame() {
	
		_colorStart = new EasingColor("#000000", 20f);
		_colorStop = new EasingColor("#000000", 20f);
	}

	protected void drawApp() {
		p.background(0);

		_colorStart.setTargetInt( p.color(255f * P.sin(p.frameCount/20f), 255f * P.sin(p.frameCount/25f), 255f * P.sin(p.frameCount/30f)) );
		_colorStart.update();
		_colorStop.setTargetInt( p.color(255f * P.sin(p.frameCount/40f), 255f * P.sin(p.frameCount/45f), 255f * P.sin(p.frameCount/50f)) );
		_colorStop.update();
		
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		Gradients.linear(p, p.width, p.height, _colorStart.colorInt(), _colorStop.colorInt());
		p.popMatrix();
	}
}
