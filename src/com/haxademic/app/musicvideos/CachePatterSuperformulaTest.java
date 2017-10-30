package com.haxademic.app.musicvideos;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.shapes.Superformula;

import controlP5.ControlP5;
import processing.core.PGraphics;

public class CachePatterSuperformulaTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ControlP5 _cp5;
	public float a;
	public float b;
	public float m;
	public float n1;
	public float n2;
	public float n3;
	
	
	// superformula
	protected Superformula _superForm;
	protected float[] _camPos = { 0f, 0f, 2500f};
	protected PGraphics _superFormGfx;


	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "1280" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "720" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}
	
	public void setup() {
		super.setup();
		
		_superForm = new Superformula(100,100, 1, 1,   6, 20,  7, 18);
		_superFormGfx = p.createGraphics(p.width, p.height, P.P3D);
		_superFormGfx.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		
		_cp5 = new ControlP5(this);
		int cp5W = 160;
		int cp5X = 20;
		int cp5Y = 20;
		int cp5YSpace = 40;
		_cp5.addSlider("a").setPosition(cp5X,cp5Y).setWidth(cp5W).setRange(0,30f).setValue(6);
		_cp5.addSlider("b").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,30f).setValue(8);
		_cp5.addSlider("m").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,30f).setValue(15);
		_cp5.addSlider("n1").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,30f).setValue(15);
		_cp5.addSlider("n2").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,30f).setValue(15);
		_cp5.addSlider("n3").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,30f).setValue(6);

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
		_superForm.a( a + (audioRange * 100f * p._audioInput.getFFT().averages[0]));
		_superForm.b( b + (audioRange * 10f * p._audioInput.getFFT().averages[1]));
		_superForm.m( m + (audioRange * 10f * p._audioInput.getFFT().averages[2]));
		_superForm.n1( n1 + (audioRange * 20f * p._audioInput.getFFT().averages[3]));
		_superForm.n2( n2 + (audioRange * 50f * p._audioInput.getFFT().averages[4]));
		_superForm.n3( n3 + (audioRange * 40f * p._audioInput.getFFT().averages[5]));

		_superForm.update();
		_superForm.drawMesh(_superFormGfx, true, true, false, true, _camPos );

		_superFormGfx.popMatrix();
		_superFormGfx.endDraw();

		p.image( _superFormGfx, 0, 0);
		DrawUtil.setPImageAlpha(p, 1);
	}

}