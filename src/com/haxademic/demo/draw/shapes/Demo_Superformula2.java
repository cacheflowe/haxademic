package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.shapes.Superformula;

import processing.core.PGraphics;

public class Demo_Superformula2
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// ui
	public String a = "a";
	public String b = "b";
	public String m = "m";
	public String n1 = "n1";
	public String n2 = "n2";
	public String n3 = "n3";
	
	// superformula
	protected Superformula _superForm;
	protected float[] _camPos = { 0f, 0f, 2500f};
	protected PGraphics _superFormGfx;

	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "1280" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "720" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.SHOW_SLIDERS, true );
	}
	
	public void setupFirstFrame() {
		_superForm = new Superformula(100,100, 1, 1,   6, 20,  7, 18);
		_superFormGfx = p.createGraphics(p.width, p.height, P.P3D);
		_superFormGfx.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		p.ui.addSlider(a, 6, 0, 30, 0.1f, false);
		p.ui.addSlider(b, 8, 0, 30, 0.1f, false);
		p.ui.addSlider(m, 15, 0, 30, 0.1f, false);
		p.ui.addSlider(n1, 15, 0, 30, 0.1f, false);
		p.ui.addSlider(n2, 15, 0, 30, 0.1f, false);
		p.ui.addSlider(n3, 6, 0, 30, 0.1f, false);
	}
	
	public void drawApp() {
		p.background(0);
		drawSuperformula();
	}
	
	protected void drawSuperformula() {
		_superFormGfx.beginDraw();
		_superFormGfx.pushMatrix();
		_superFormGfx.clear();

		_superFormGfx.translate( p.width/2, p.height/2, 0 );

		//			p.rotateX(p.frameCount/20f);
		//			p.rotateY(P.PI/2f);
//		_superFormGfx.rotateZ(p.frameCount/50f);

		float audioRange = 0.1f;
		_superForm.a( p.ui.value(a) + (audioRange * 100f * p.audioFreq(0)));
		_superForm.b( p.ui.value(b) + (audioRange * 10f * p.audioFreq(1)));
		_superForm.m( p.ui.value(m) + (audioRange * 10f * p.audioFreq(2)));
		_superForm.n1( p.ui.value(n1) + (audioRange * 20f * p.audioFreq(3)));
		_superForm.n2( p.ui.value(n2) + (audioRange * 50f * p.audioFreq(4)));
		_superForm.n3( p.ui.value(n3) + (audioRange * 40f * p.audioFreq(5)));

		_superForm.update();
		_superForm.drawMesh(_superFormGfx, true, true, false, true, _camPos );

		_superFormGfx.popMatrix();
		_superFormGfx.endDraw();

		p.image( _superFormGfx, 0, 0);
		DrawUtil.setPImageAlpha(p, 1);
	}

}