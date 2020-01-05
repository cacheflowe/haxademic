package com.haxademic.demo.math;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PVector;

public class Demo_MathUtil_getRadiansToTargetParticle
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PGraphics pg;
	ArrayList<Parti> partis = new ArrayList<Parti>();

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		for (int i = 0; i < 10000; i++) {
			partis.add(new Parti());
		}
	}

	protected void drawApp() {
		// fade background
		PG.setDrawCorner(p);
		p.fill(110,70,215,40);
		p.rect(0, 0, p.width, p.height);
		
		// draw particles
		PG.setDrawCenter(p);
		p.fill(255);
		p.noStroke();
		for (int i = 0; i < partis.size(); i++) {
			partis.get(i).update();
		}
	}

	public class Parti {
		
		public PVector pos = new PVector();
		public EasingFloat rads = new EasingFloat(0, MathUtil.randRangeDecimal(4f, 10f));
		public float speed = MathUtil.randRangeDecimal(2f, 5f);
		
		public Parti() {
			rads.setCurrent(MathUtil.randRangeDecimal(0, P.TWO_PI));
			rads.setTarget(rads.value());
			pos.set(MathUtil.randRangeDecimal(0, p.width), MathUtil.randRangeDecimal(0, p.width));
		}
		
		public void update() {
			// loop rads for easing
			float radsToTarget = MathUtil.getRadiansToTarget(pos.x, pos.y, p.mouseX, p.mouseY);
			// wrap target around
			float radsDiff = radsToTarget - rads.value();
			if(radsDiff > P.PI) radsToTarget -= P.TWO_PI;
			if(radsDiff < -P.PI) radsToTarget += P.TWO_PI;
		    rads.setTarget(radsToTarget);
		    rads.update();
		    // if lerp update wraps around, loop that
		    if(rads.value() > P.TWO_PI) rads.setCurrent(rads.value() - P.TWO_PI);
		    if(rads.value() < 0) rads.setCurrent(rads.value() + P.TWO_PI);
			float moveRads = rads.value();
			pos.add(speed * P.cos(moveRads), speed * P.sin(moveRads));
			
			p.pushMatrix();
			p.translate(pos.x, pos.y);
			p.rotate(-rads.value());
			p.rect(0, 0, speed, 2);
			p.popMatrix();
		}
		
	}
}

