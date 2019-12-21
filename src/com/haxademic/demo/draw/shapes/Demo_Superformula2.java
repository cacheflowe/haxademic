package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Superformula;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_Superformula2
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
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

	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "1280" );
		Config.setProperty( AppSettings.HEIGHT, "720" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}
	
	public void firstFrame() {
		AudioIn.instance();
		_superForm = new Superformula(100,100, 1, 1,   6, 20,  7, 18);
		_superFormGfx = p.createGraphics(p.width, p.height, P.P3D);
		_superFormGfx.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		UI.addSlider(a, 6, 0, 30, 0.1f, false);
		UI.addSlider(b, 8, 0, 30, 0.1f, false);
		UI.addSlider(m, 15, 0, 30, 0.1f, false);
		UI.addSlider(n1, 15, 0, 30, 0.1f, false);
		UI.addSlider(n2, 15, 0, 30, 0.1f, false);
		UI.addSlider(n3, 6, 0, 30, 0.1f, false);
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
		_superForm.a( UI.value(a) + (audioRange * 100f * AudioIn.audioFreq(0)));
		_superForm.b( UI.value(b) + (audioRange * 10f * AudioIn.audioFreq(1)));
		_superForm.m( UI.value(m) + (audioRange * 10f * AudioIn.audioFreq(2)));
		_superForm.n1( UI.value(n1) + (audioRange * 20f * AudioIn.audioFreq(3)));
		_superForm.n2( UI.value(n2) + (audioRange * 50f * AudioIn.audioFreq(4)));
		_superForm.n3( UI.value(n3) + (audioRange * 40f * AudioIn.audioFreq(5)));

		_superForm.update();
		_superForm.drawMesh(_superFormGfx, true, true, false, true, _camPos );

		_superFormGfx.popMatrix();
		_superFormGfx.endDraw();

		p.image( _superFormGfx, 0, 0);
		PG.setPImageAlpha(p, 1);
	}

}