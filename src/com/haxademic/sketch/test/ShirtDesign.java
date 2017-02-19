package com.haxademic.sketch.test;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import controlP5.ControlP5;

public class ShirtDesign
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float numLines = 0;
	public float thickness = 0;
	public float spacing = 0;
	public float perlinIncSpeed = 0;
	public float perlinStart = 0;
	public float perlinStartLarge = 0;
	public float startSize = 0;
	public float perlinAmp = 0;
//	public float rotation = 0;
	public float moonX = 0;
	public float moonY = 0;
	public float moonSize = 0;
	protected ControlP5 _cp5;
	
	protected float _x = 0;
	protected float _y = 0;
	
	protected boolean _shouldPrint = false;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "true" );
	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		_cp5 = new ControlP5(this);
		int spacing = 30;
		int cntrlY = 30;
		_cp5.addSlider("numLines").setPosition(20,cntrlY+=spacing).setWidth(400).setRange(1,100).setValue(56);
//		_cp5.addSlider("thickness").setPosition(20,cntrlY+=spacing).setWidth(400).setRange(0,100).setValue(5);
		_cp5.addSlider("spacing").setPosition(20,cntrlY+=spacing).setWidth(400).setRange(0,50).setValue(9);
		_cp5.addSlider("perlinStart").setPosition(20,cntrlY+=spacing).setWidth(400).setRange(0,100f).setValue(0f);
		_cp5.addSlider("perlinStartLarge").setPosition(20,cntrlY+=spacing).setWidth(400).setRange(0,10000f).setValue(0f);
		_cp5.addSlider("perlinIncSpeed").setPosition(20,cntrlY+=spacing).setWidth(400).setRange(0,1f).setValue(0.47f);
		_cp5.addSlider("perlinAmp").setPosition(20,cntrlY+=spacing).setWidth(400).setRange(0,800f).setValue(480f);
		_cp5.addSlider("startSize").setPosition(20,cntrlY+=spacing).setWidth(400).setRange(2f,1000f).setValue(500f);
//		_cp5.addSlider("rotation").setPosition(20,cntrlY+=spacing).setWidth(400).setRange(-P.PI,P.PI).setValue(0f);
		_cp5.addSlider("moonX").setPosition(20,cntrlY+=spacing).setWidth(400).setRange(-10,80).setValue(12.5f);
		_cp5.addSlider("moonY").setPosition(20,cntrlY+=spacing).setWidth(400).setRange(-10, 80).setValue(12.5f);
		_cp5.addSlider("moonSize").setPosition(20,cntrlY+=spacing).setWidth(400).setRange(0,50).setValue(10f);
	}
	
	public void keyPressed() {
		if( p.key == 'p' ) _shouldPrint = true;
	}

	public void drawApp() {
		background(0);
		DrawUtil.setDrawCenter(p);

		if( _shouldPrint ) p.beginRecord( P.PDF,  FileUtil.getHaxademicOutputPath() + "mountains-"+ SystemUtil.getTimestamp(p) +".pdf" );

		_x = (p.width / 2) - (numLines * spacing)/2;
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
		
		thickness = spacing * 0.5f;
		
		// draw inverse peaks
		float curSize = startSize;
		for (int i = 0; i < numLines; i++) {
			float x = _x + spacing * i;
			
			p.pushMatrix();
			p.translate(x, _y);
//			p.rotate(rotation);
//			p.line(0, -startSize, 0, curSize);

			beginShape();
			vertex(-thickness/2f, -startSize/2f);
			vertex(thickness/2f, -startSize/2f);
			curSize = perlinAmp * (-0.5f + p.noise(perlinStartLarge + perlinStart + perlinIncSpeed/100f * (x + thickness/2f)));
			vertex(thickness/2f, curSize);
			curSize = perlinAmp * (-0.5f + p.noise(perlinStartLarge + perlinStart + perlinIncSpeed/100f * (x - thickness/2f)));
			vertex(-thickness/2f, curSize);
			endShape(CLOSE);

			p.popMatrix();
		}
		
		// draw moon
		p.fill(0);
		float moonRadius = moonSize * spacing; // - spacing * 0.75f;
		p.ellipse(_x + P.round(moonX) * spacing, _y - startSize/2f + P.round(moonY) * spacing, moonRadius, moonRadius);
		

		// render pdf
		if( _shouldPrint == true ) {
			p.endRecord();
			_shouldPrint = false;
		}

	}

}

/*
		float curSize = startSize;
		for (int i = 0; i < numLines; i++) {
			p.pushMatrix();
			p.translate(_x, _y);
			p.rotate(rotation);
//			curSize = startSize + perlinAmp * p.noise(perlinIncSpeed * i);
			p.line(0, -startSize, 0, curSize);
			_x += spacing;
			curSize += perlinAmp * (-0.5f + p.noise(perlinStart + perlinIncSpeed * i));
			p.popMatrix();
		}

 */
