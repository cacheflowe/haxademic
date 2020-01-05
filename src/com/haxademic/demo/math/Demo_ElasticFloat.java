package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.easing.ElasticFloat;
import com.haxademic.core.ui.UI;

public class Demo_ElasticFloat
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String fric = "fric";
	protected String accel = "accel";
	protected ElasticFloat _elasticX = new ElasticFloat(0, 0.5f, 0.5f);
	protected ElasticFloat _elasticY = new ElasticFloat(0, 0.5f, 0.5f);
	protected ElasticFloat _elasticBottom = new ElasticFloat(0, 0.5f, 0.5f);

	protected void config() {
		Config.setProperty( AppSettings.FPS, "60" );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	protected void firstFrame() {
		UI.addSlider(fric, 0.5f, 0, 1, 0.001f, false);
		UI.addSlider(accel, 0.5f, 0, 1, 0.001f, false);
	}

	protected void drawApp() {
		background(0);
		
		_elasticX.setFriction(UI.value(fric));
		_elasticY.setFriction(UI.value(fric));
		_elasticBottom.setFriction(UI.value(fric));
		_elasticX.setAccel(UI.value(accel));
		_elasticY.setAccel(UI.value(accel));
		_elasticBottom.setAccel(UI.value(accel));
		
		_elasticX.setTarget(p.mouseX);
		_elasticY.setTarget(p.mouseY);
		int bottomVal = P.round(p.frameCount * 0.01f) % 2;
		_elasticBottom.setTarget((bottomVal % 2) * p.width);
		
		_elasticX.update();
		_elasticY.update();
		_elasticBottom.update();
		
		PG.setDrawCenter(p);
		p.fill(255);
		p.ellipse(_elasticX.value(), _elasticY.value(), 40, 40);
		p.ellipse(_elasticBottom.value(), p.height - 20, 40, 40);

	}

}
