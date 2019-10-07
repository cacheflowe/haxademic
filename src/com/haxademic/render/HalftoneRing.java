package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.HalftoneLinesFilter;
import com.haxademic.core.math.easing.Penner;

public class HalftoneRing 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 160;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames + 1 );
	}

	public void setup() {
		super.setup();
		noStroke();
	}

	public void drawApp() {
		background(220);
		p.ortho();
//		translate(width/2, height/2, -400);
		translate(width/2, height/2, 0);
		PG.setDrawCenter(p);
		
		float percentComplete = (float)(p.frameCount%_frames)/(float)_frames;
		float easedPercent = Penner.easeInOutQuart(percentComplete % 1, 0, 1, 1);
		float radsComplete = (percentComplete) * P.TWO_PI;

		PG.setPImageAlpha(p, 0.4f + 0.4f * P.sin(radsComplete));
		fill(255);
		stroke(50);
		strokeWeight(30);
		
//		p.rotateX(-P.QUARTER_PI);
//		p.rotateY(P.QUARTER_PI + radsComplete/4f);
//		box(width/2);
		
		fill(220);
		stroke(50);
		strokeWeight(30);
		p.ellipse(0, 0, p.width/(1.7f + 0.3f * P.sin(radsComplete)), p.height/(1.7f + 0.3f * P.sin(radsComplete)));
		fill(255);
		stroke(50);
		strokeWeight(30);
		p.ellipse(0, 0, p.width * (0.3f + 0.15f * P.sin(P.TWO_PI + radsComplete)), p.height * (0.3f + 0.15f * P.sin(P.TWO_PI + radsComplete)));
		
		float sampleDist = P.map(p.mouseX, 0, p.width, 10, 1000);
		sampleDist = 600;
		float antiAlias = P.map(p.mouseY, 0, p.height, 0, 1f);
		antiAlias = 0.04f;
		float rows = P.map(p.mouseX, 0, p.width, 10, 200f);
		rows = 50f;
		
		HalftoneLinesFilter.instance(p).setSampleDistX(sampleDist);
		HalftoneLinesFilter.instance(p).setSampleDistY(sampleDist);
		HalftoneLinesFilter.instance(p).setAntiAlias(antiAlias);
		HalftoneLinesFilter.instance(p).setRows(rows);
		HalftoneLinesFilter.instance(p).setRotation(0);
//		HalftoneLinesFilter.instance(p).setMode(P.floor(p.frameCount / 20f) % 6);
		HalftoneLinesFilter.instance(p).setMode(3);
		HalftoneLinesFilter.instance(p).applyTo(p);
	}
}
