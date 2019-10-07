package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class NightlinesShirtDesign
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String numLines = "numLines";
//	protected String thickness = "thickness";
	protected String spacing = "spacing";
	protected String perlinIncSpeed = "perlinIncSpeed";
	protected String perlinStart = "perlinStart";
	protected String perlinStartLarge = "perlinStartLarge";
	protected String startSize = "startSize";
	protected String perlinAmp = "perlinAmp";
	protected String moonX = "moonX";
	protected String moonY = "moonY";
	protected String moonSize = "moonSize";
	
	protected float _x = 0;
	protected float _y = 0;
	
	protected boolean _shouldPrint = false;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.SHOW_SLIDERS, true );
	}

	public void setupFirstFrame() {
		p.ui.addSlider(numLines, 56, 1, 100, 1, false);
		p.ui.addSlider(spacing, 9, 0, 50, 0.5f, false);
		p.ui.addSlider(perlinStart, 0, 0, 100, 0.5f, false);
		p.ui.addSlider(perlinStartLarge, 0, 0, 10000, 10, false);
		p.ui.addSlider(perlinIncSpeed, 0.47f, 0, 1, 0.01f, false);
		p.ui.addSlider(perlinAmp, 480f, 0, 800, 1f, false);
		p.ui.addSlider(startSize, 500f, 2f, 1000f, 1f, false);
		p.ui.addSlider(moonX, 12.5f, -10, 80, 1f, false);
		p.ui.addSlider(moonY, 12.5f, -10, 80, 1f, false);
		p.ui.addSlider(moonSize, 10, 0, 50, 1f, false);
	}
	
	public void keyPressed() {
		if( p.key == 'p' ) _shouldPrint = true;
	}

	public void drawApp() {
		background(0);
		PG.setDrawCenter(p);

		if( _shouldPrint ) p.beginRecord( P.PDF,  FileUtil.getHaxademicOutputPath() + "mountains-"+ SystemUtil.getTimestamp() +".pdf" );

		_x = (p.width / 2) - (p.ui.value(numLines) * p.ui.value(spacing))/2;
		_y = p.height / 2;
		
//		 + P.sin(radians) * radius
//		 + P.cos(radians) * radius
		
//		p.stroke(255);
//		p.strokeCap(P.ROUND);
//		p.noFill();
//		p.strokeWeight(thickness);
		
		p.fill(255);
		p.strokeCap(P.ROUND);
		p.noStroke();	
		
		float thickness = p.ui.value(spacing) * 0.5f;
		
		// draw inverse peaks
		float curSize = p.ui.value(startSize);
		for (int i = 0; i < p.ui.value(numLines); i++) {
			float x = _x + p.ui.value(spacing) * i;
			
			p.pushMatrix();
			p.translate(x, _y);
//			p.rotate(rotation);
//			p.line(0, -startSize, 0, curSize);

			beginShape();
			vertex(-thickness/2f, -p.ui.value(startSize)/2f);
			vertex(thickness/2f, -p.ui.value(startSize)/2f);
			curSize = p.ui.value(perlinAmp) * (-0.5f + p.noise(p.ui.value(perlinStartLarge) + p.ui.value(perlinStart) + p.ui.value(perlinIncSpeed)/100f * (x + thickness/2f)));
			vertex(thickness/2f, curSize);
			curSize = p.ui.value(perlinAmp) * (-0.5f + p.noise(p.ui.value(perlinStartLarge) + p.ui.value(perlinStart) + p.ui.value(perlinIncSpeed)/100f * (x - thickness/2f)));
			vertex(-thickness/2f, curSize);
			endShape(CLOSE);

			p.popMatrix();
		}
		
		// draw moon
		p.fill(0);
		float moonRadius = p.ui.value(moonSize) * p.ui.value(spacing); // - spacing * 0.75f;
		p.ellipse(_x + P.round(p.ui.value(moonX)) * p.ui.value(spacing), _y - p.ui.value(startSize)/2f + P.round(p.ui.value(moonY)) * p.ui.value(spacing), moonRadius, moonRadius);
		

		// render pdf
		if( _shouldPrint == true ) {
			p.endRecord();
			_shouldPrint = false;
		}

	}

}

