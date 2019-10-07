package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.easing.ElasticFloat;

public class Demo_ElasticFloat
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String fric = "fric";
	protected String accel = "accel";
	protected ElasticFloat _elasticX = new ElasticFloat(0, 0.5f, 0.5f);
	protected ElasticFloat _elasticY = new ElasticFloat(0, 0.5f, 0.5f);
	protected ElasticFloat _elasticBottom = new ElasticFloat(0, 0.5f, 0.5f);

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "60" );
		p.appConfig.setProperty( AppSettings.SHOW_SLIDERS, true );
	}

	public void setupFirstFrame() {
		p.ui.addSlider(fric, 0.5f, 0, 1, 0.001f, false);
		p.ui.addSlider(accel, 0.5f, 0, 1, 0.001f, false);
	}

	public void drawApp() {
		background(0);
		
		_elasticX.setFriction(p.ui.value(fric));
		_elasticY.setFriction(p.ui.value(fric));
		_elasticBottom.setFriction(p.ui.value(fric));
		_elasticX.setAccel(p.ui.value(accel));
		_elasticY.setAccel(p.ui.value(accel));
		_elasticBottom.setAccel(p.ui.value(accel));
		
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
