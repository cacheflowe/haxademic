package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.file.FileUtil;

import controlP5.ControlP5;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;


public class ChromaKeyShaderControlsKinect 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics _pg;

	PShader _chromaKeyFilter;
	protected ControlP5 _cp5;
	public float thresholdSensitivity;
	public float smoothing;
	public float colorToReplace_R;
	public float colorToReplace_G;
	public float colorToReplace_B;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "480" );
	}
	
	public void setup() {
		super.setup();

		_pg = p.createGraphics( p.width, p.height, P.P3D );
		setupChromakey();
	}
		
	protected void setupChromakey() {
		_cp5 = new ControlP5(this);
		int cp5W = 160;
		int cp5X = 20;
		int cp5Y = 20;
		int cp5YSpace = 40;
		_cp5.addSlider("thresholdSensitivity").setPosition(cp5X,cp5Y).setWidth(cp5W).setRange(0,1f).setValue(0.75f);
		_cp5.addSlider("smoothing").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.26f);
		_cp5.addSlider("colorToReplace_R").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.29f);
		_cp5.addSlider("colorToReplace_G").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.93f);
		_cp5.addSlider("colorToReplace_B").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.14f);

		_chromaKeyFilter = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/chroma-color.glsl" );
		_chromaKeyFilter.set("thresholdSensitivity", thresholdSensitivity);
		_chromaKeyFilter.set("smoothing", smoothing);
		_chromaKeyFilter.set("colorToReplace", colorToReplace_R, colorToReplace_G, colorToReplace_B);
	}

	public void drawApp() {
		// draw a background
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		Gradients.radial(p, p.width * 2, p.height * 2, p.color(127 + 127f * sin(p.frameCount/10f)), p.color(127 + 127f * sin(p.frameCount/11f)), 100);
		p.popMatrix();

		// reset chroma key uniforms
		_chromaKeyFilter.set("thresholdSensitivity", thresholdSensitivity);
		_chromaKeyFilter.set("smoothing", smoothing);
		_chromaKeyFilter.set("colorToReplace", colorToReplace_R, colorToReplace_G, colorToReplace_B);
		
		// draw frame to offscreen buffer
		PImage frame = p.kinectWrapper.getRgbImage();
		_pg.beginDraw();
		_pg.clear();
		_pg.image(frame, 0, 0);
		_pg.endDraw();
		
		// apply filter & draw to scren
		_pg.filter(_chromaKeyFilter);
		p.image(_pg, 0, 0);
	}
}
