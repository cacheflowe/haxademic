package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;

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
		p.appConfig.setProperty( AppSettings.SHOW_UI, true );
	}

	public void setupFirstFrame() {
		UI.addSlider(numLines, 56, 1, 100, 1, false);
		UI.addSlider(spacing, 9, 0, 50, 0.5f, false);
		UI.addSlider(perlinStart, 0, 0, 100, 0.5f, false);
		UI.addSlider(perlinStartLarge, 0, 0, 10000, 10, false);
		UI.addSlider(perlinIncSpeed, 0.47f, 0, 1, 0.01f, false);
		UI.addSlider(perlinAmp, 480f, 0, 800, 1f, false);
		UI.addSlider(startSize, 500f, 2f, 1000f, 1f, false);
		UI.addSlider(moonX, 12.5f, -10, 80, 1f, false);
		UI.addSlider(moonY, 12.5f, -10, 80, 1f, false);
		UI.addSlider(moonSize, 10, 0, 50, 1f, false);
	}
	
	public void keyPressed() {
		if( p.key == 'p' ) _shouldPrint = true;
	}

	public void drawApp() {
		background(0);
		PG.setDrawCenter(p);

		if( _shouldPrint ) p.beginRecord( P.PDF,  FileUtil.getHaxademicOutputPath() + "mountains-"+ SystemUtil.getTimestamp() +".pdf" );

		_x = (p.width / 2) - (UI.value(numLines) * UI.value(spacing))/2;
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
		
		float thickness = UI.value(spacing) * 0.5f;
		
		// draw inverse peaks
		float curSize = UI.value(startSize);
		for (int i = 0; i < UI.value(numLines); i++) {
			float x = _x + UI.value(spacing) * i;
			
			p.pushMatrix();
			p.translate(x, _y);
//			p.rotate(rotation);
//			p.line(0, -startSize, 0, curSize);

			beginShape();
			vertex(-thickness/2f, -UI.value(startSize)/2f);
			vertex(thickness/2f, -UI.value(startSize)/2f);
			curSize = UI.value(perlinAmp) * (-0.5f + p.noise(UI.value(perlinStartLarge) + UI.value(perlinStart) + UI.value(perlinIncSpeed)/100f * (x + thickness/2f)));
			vertex(thickness/2f, curSize);
			curSize = UI.value(perlinAmp) * (-0.5f + p.noise(UI.value(perlinStartLarge) + UI.value(perlinStart) + UI.value(perlinIncSpeed)/100f * (x - thickness/2f)));
			vertex(-thickness/2f, curSize);
			endShape(CLOSE);

			p.popMatrix();
		}
		
		// draw moon
		p.fill(0);
		float moonRadius = UI.value(moonSize) * UI.value(spacing); // - spacing * 0.75f;
		p.ellipse(_x + P.round(UI.value(moonX)) * UI.value(spacing), _y - UI.value(startSize)/2f + P.round(UI.value(moonY)) * UI.value(spacing), moonRadius, moonRadius);
		

		// render pdf
		if( _shouldPrint == true ) {
			p.endRecord();
			_shouldPrint = false;
		}

	}

}

