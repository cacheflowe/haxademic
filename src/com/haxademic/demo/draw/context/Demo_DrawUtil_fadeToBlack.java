package com.haxademic.demo.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Gradients;

public class Demo_DrawUtil_fadeToBlack
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	float frames = 500f;
	
	protected void overridePropsFile() {
//		p.appConfig.setProperty( AppSettings.FPS, 60 );
		p.appConfig.setProperty( AppSettings.RENDERER, P.P3D );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	public void setup() {
		super.setup();	
	}

	public void drawApp() {
		if(p.frameCount == 1) background(0);
		DrawUtil.fadeToBlack(p.g, 1);
		DrawUtil.setDrawCenter(p);
		DrawUtil.setDrawFlat2d(p, true);
		
		float progress = (p.frameCount % frames) / frames;
		float progressRads = progress * P.TWO_PI;

		float radius = 200f;// + 50f * P.sin(progressRads * 4f);
		float x = p.width / 2 + P.cos(progressRads) * radius;
		float y = p.height / 2 + P.sin(progressRads) * radius;
		
//		p.stroke(180);
//		p.strokeWeight(0.8f);
		p.noStroke();
		p.fill(255);
		p.translate(x, y);
		p.rotate(-progressRads);
		float size = 50 + P.sin(progressRads * 2f) * 10f;
		Gradients.linear(p, size, size, 
				p.color(70, 170f + 85f * P.sin(progressRads), 200), 
				p.color(100, 200f + 55f * P.sin(P.PI + progressRads), 255)
		);
		p.fill(0);
		p.rect(0, 0, size - 8f, size - 8f);
		
	}

}
