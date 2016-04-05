package com.haxademic.sketch.hardware.kinect_openni;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.image.filters.shaders.ChromaColorFilter;
import com.haxademic.core.system.FileUtil;

import controlP5.ControlP5;
import processing.core.PGraphics;
import processing.video.Movie;


public class KinectChromaTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected Movie _movie;
	protected float[] _cropProps = null;

	PGraphics _chromaBuffer;
	
	protected ControlP5 _cp5;
	public float thresholdSensitivity;
	public float smoothing;
	public float colorToReplaceR;
	public float colorToReplaceG;
	public float colorToReplaceB;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "480" );
	}
	
	public void setup() {
		super.setup();
				
		// build chroma cam buffer
		_chromaBuffer = p.createGraphics(p.width, p.height, P.P3D);
		_chromaBuffer.smooth( 8 );

		_cp5 = new ControlP5(this);
		int cp5W = 160;
		int cp5X = 20;
		int cp5Y = 20;
		int cp5YSpace = 40;
		_cp5.addSlider("thresholdSensitivity").setPosition(cp5X,cp5Y).setWidth(cp5W).setRange(0,1f).setValue(0.75f);
		_cp5.addSlider("smoothing").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.26f);
		_cp5.addSlider("colorToReplaceR").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.29f);
		_cp5.addSlider("colorToReplaceG").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.93f);
		_cp5.addSlider("colorToReplaceB").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.14f);

		
		// build movie layer		
		_movie = new Movie( p, FileUtil.getFile("video/nike/nike-hike-gray-loop.mov") );

		_movie.jump(0);
		_movie.loop();
		_movie.play();

	}

	public void drawApp() {
		// reset drawing 
		p.background(0);
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);
		p.noStroke();
		
		// draw webcam to buffer & apply chroma filter
		_chromaBuffer.beginDraw();
		_chromaBuffer.clear();
		_chromaBuffer.image(p.kinectWrapper.getRgbImage(), 0, 0);
		_chromaBuffer.endDraw();
		
		ChromaColorFilter.instance(p).setColorToReplace(colorToReplaceR, colorToReplaceG, colorToReplaceB);
		ChromaColorFilter.instance(p).setSmoothing(smoothing);
		ChromaColorFilter.instance(p).setThresholdSensitivity(thresholdSensitivity);
		ChromaColorFilter.instance(p).applyTo(_chromaBuffer);
		
		


		// draw movie
		if(_movie.width > 1) {
			_cropProps = ImageUtil.getOffsetAndSizeToCrop(p.width, p.height, _movie.width, _movie.height, true);
			DrawUtil.setPImageAlpha(p, 1);
			p.image(_movie, _cropProps[0], _cropProps[1], _cropProps[2], _cropProps[3]);
		}

		// draw chroma'd cam
		p.image(_chromaBuffer, 0, 0);
	}

	
}

