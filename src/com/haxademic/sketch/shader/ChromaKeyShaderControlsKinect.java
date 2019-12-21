package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;


public class ChromaKeyShaderControlsKinect 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics _pg;

	PShader _chromaKeyFilter;
	
	public String thresholdSensitivity = "thresholdSensitivity";
	public String smoothing = "smoothing";
	public String colorToReplaceR = "colorToReplaceR";
	public String colorToReplaceG = "colorToReplaceG";
	public String colorToReplaceB = "colorToReplaceB";


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "480" );
		p.appConfig.setProperty( AppSettings.SHOW_UI, true );
	}
	
	public void setupFirstFrame() {
		_pg = p.createGraphics( p.width, p.height, P.P3D );
		setupChromakey();
	}
		
	protected void setupChromakey() {
		p.ui.addSlider(thresholdSensitivity, 0.75f, 0, 1, 0.01f, false);
		p.ui.addSlider(smoothing, 0.26f, 0, 1, 0.01f, false);
		p.ui.addSlider(colorToReplaceR, 0.29f, 0, 1, 0.01f, false);
		p.ui.addSlider(colorToReplaceG, 0.93f, 0, 1, 0.01f, false);
		p.ui.addSlider(colorToReplaceB, 0.14f, 0, 1, 0.01f, false);

		_chromaKeyFilter = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/chroma-color.glsl" );
	}

	public void drawApp() {
		// draw a background
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		Gradients.radial(p, p.width * 2, p.height * 2, p.color(127 + 127f * sin(p.frameCount/10f)), p.color(127 + 127f * sin(p.frameCount/11f)), 100);
		p.popMatrix();

		// reset chroma key uniforms
		_chromaKeyFilter.set("thresholdSensitivity", p.ui.value(thresholdSensitivity));
		_chromaKeyFilter.set("smoothing", p.ui.value(smoothing));
		_chromaKeyFilter.set("colorToReplace", p.ui.value(colorToReplaceR), p.ui.value(colorToReplaceG), p.ui.value(colorToReplaceB));
		
		// draw frame to offscreen buffer
		PImage frame = p.depthCamera.getRgbImage();
		_pg.beginDraw();
		_pg.clear();
		_pg.image(frame, 0, 0);
		_pg.endDraw();
		
		// apply filter & draw to scren
		_pg.filter(_chromaKeyFilter);
		p.image(_pg, 0, 0);
	}
}
