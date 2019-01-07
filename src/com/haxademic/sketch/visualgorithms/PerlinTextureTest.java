package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.image.PerlinTexture;

import controlP5.ControlP5;

public class PerlinTextureTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PerlinTexture _perlinTexture;
	
	public float increment = 0;
	public float detail = 0;
	public float xProgress = 0;
	public float yProgress = 0;
	protected ControlP5 _cp5;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "400" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "200" );
	}

	public void setup() {
		super.setup();	

		_cp5 = new ControlP5(this);
		_cp5.addSlider("increment").setPosition(20,20).setWidth(100).setRange(0.001f,0.1f).setValue(0.01f);
		_cp5.addSlider("detail").setPosition(20,40).setWidth(100).setRange(0.0001f,0.3f).setValue(0.1f);
		_cp5.addSlider("xProgress").setPosition(20,60).setWidth(100).setRange(0,1).setValue(0);
		_cp5.addSlider("yProgress").setPosition(20,80).setWidth(100).setRange(0,1).setValue(0);

		_perlinTexture = new PerlinTexture(this, 200, 200);
	}

	public void drawApp() {
		p.background(0);
		_perlinTexture.update(increment, detail, xProgress, yProgress);
		p.image(_perlinTexture.texture(), p.width - _perlinTexture.texture().width, 0);
	}
}
