package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.ui.UI;

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


	protected void config() {
		Config.setProperty( AppSettings.SHOW_UI, true );
	}
	
	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.KinectV1);

		_pg = p.createGraphics( p.width, p.height, P.P3D );
		setupChromakey();
	}
		
	protected void setupChromakey() {
		UI.addSlider(thresholdSensitivity, 0.75f, 0, 1, 0.01f, false);
		UI.addSlider(smoothing, 0.26f, 0, 1, 0.01f, false);
		UI.addSlider(colorToReplaceR, 0.29f, 0, 1, 0.01f, false);
		UI.addSlider(colorToReplaceG, 0.93f, 0, 1, 0.01f, false);
		UI.addSlider(colorToReplaceB, 0.14f, 0, 1, 0.01f, false);

		_chromaKeyFilter = loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/chroma-color.glsl" );
	}

	protected void drawApp() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;

		// draw a background
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		Gradients.radial(p, p.width * 2, p.height * 2, p.color(127 + 127f * sin(p.frameCount/10f)), p.color(127 + 127f * sin(p.frameCount/11f)), 100);
		p.popMatrix();

		// reset chroma key uniforms
		_chromaKeyFilter.set("thresholdSensitivity", UI.value(thresholdSensitivity));
		_chromaKeyFilter.set("smoothing", UI.value(smoothing));
		_chromaKeyFilter.set("colorToReplace", UI.value(colorToReplaceR), UI.value(colorToReplaceG), UI.value(colorToReplaceB));
		
		// draw frame to offscreen buffer
		PImage frame = depthCamera.getRgbImage();
		_pg.beginDraw();
		_pg.clear();
		_pg.image(frame, 0, 0);
		_pg.endDraw();
		
		// apply filter & draw to scren
		_pg.filter(_chromaKeyFilter);
		p.image(_pg, 0, 0);
	}
}
