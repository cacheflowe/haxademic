package com.haxademic.sketch.hardware.kinect_openni;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.hardware.dmx.DmxInterface;
import com.haxademic.core.hardware.kinect.KinectSize;
import com.haxademic.core.image.MotionBlurPGraphics;
import com.haxademic.core.image.filters.PixelFilter;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

import controlP5.ControlP5;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;


@SuppressWarnings("serial")
public class KinectFaceRecorder 
extends PAppletHax {

	public static final float PIXEL_SIZE = 10;
	public static final int KINECT_TOP = 0;
	public static final int KINECT_BOTTOM = 480;
	public static final int KINECT_CLOSE = 100;
	public static final int KINECT_FAR = 600;
	
	protected PixelFilter _pixelFilter;
	
	protected int _savedFramePlaybackIndex = 0;
	protected boolean _isRecording = false;
	protected ArrayList<PImage> _savedFrames;
	protected MotionBlurPGraphics _motionBlur;
	protected PGraphics _pg;

	PShader _chromaKeyFilter;
	protected ControlP5 _cp5;
	public float thresholdSensitivity;
	public float smoothing;
	public float colorToReplaceR;
	public float colorToReplaceG;
	public float colorToReplaceB;
	
	protected DmxInterface _dmxInterface;
	protected ColorHaxEasing _lightColor1;
	protected ColorHaxEasing _lightColor2;


	public void setup() {
		super.setup();
		_pixelFilter = new PixelFilter(KinectSize.WIDTH, KinectSize.WIDTH, (int)PIXEL_SIZE);
		
		_savedFrames = new ArrayList<PImage>();
		_savedFramePlaybackIndex = 0;

		_pg = p.createGraphics( p.width, p.height, P.OPENGL );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);

		_motionBlur = new MotionBlurPGraphics(20);
		
		setupChromakey();
		setupDmxLights();
	}
		
		
	protected void setupDmxLights() {
		_dmxInterface = new DmxInterface(2);
		_lightColor1 = new ColorHaxEasing(0, 0, 0, 1, 10);
		_lightColor2 = new ColorHaxEasing(0, 0, 0, 1, 10);
	}
	
	protected void setupChromakey() {
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

		_chromaKeyFilter = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/chroma-gpu.glsl" );
		_chromaKeyFilter.set("thresholdSensitivity", thresholdSensitivity);
		_chromaKeyFilter.set("smoothing", smoothing);
		_chromaKeyFilter.set("colorToReplace", colorToReplaceR, colorToReplaceG, colorToReplaceB);
	}

	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "kinect_active", "true" );
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
	}
	
	public void drawApp() {
		runDmxLights();
		
		_chromaKeyFilter.set("thresholdSensitivity", thresholdSensitivity);
		_chromaKeyFilter.set("smoothing", smoothing);
		_chromaKeyFilter.set("colorToReplace", colorToReplaceR, colorToReplaceG, colorToReplaceB);

//		DrawUtil.resetGlobalProps( p );
//		p.shininess(1000f); 
//		p.lights();
		p.background(0);
		
		// draw filtered web cam
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);
		
//		p.image( p.kinectWrapper.getRgbImage(), 0, 0, 320, 240 );
		PImage frame = p.kinectWrapper.getRgbImage();

		// loop through kinect data within player's control range
		p.stroke(255, 127);
		float pixelDepth;
		int numPixels = 0;
		for ( int x = 0; x < KinectSize.WIDTH; x += PIXEL_SIZE ) {
			for ( int y = KINECT_TOP; y < KINECT_BOTTOM; y += PIXEL_SIZE ) {
				pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
					p.pushMatrix();
//					p.fill(((pixelDepth - KINECT_CLOSE) / (KINECT_FAR - KINECT_CLOSE)) * 255f);
					p.fill(frame.get(x, y));
					p.translate(x, y); // pixelDepth/10f
					// p.rect(0, 0, PIXEL_SIZE, PIXEL_SIZE);
					p.popMatrix();
					
					numPixels++;
				}
			}
		}
		
		// record
		if(numPixels > 100) {
			if(_isRecording == false) {
				_isRecording = true;
				_lightColor1.setTargetColorInt(newDmxColor());
				_lightColor2.setTargetColorInt(newDmxColor());
			}
		} else {
			_isRecording = false;
			_lightColor1.setTargetColorInt(p.color(0));
			_lightColor2.setTargetColorInt(p.color(0));
		}
		if(_isRecording == true && p.frameCount % 20 == 0) {
			if(_savedFrames.size() < 500) {
				_savedFrames.add(frame.get());
				P.println("_savedFrames: ",_savedFrames.size());
			}
		}
		
		// playback
		if(_savedFrames.size() > 0) {
			if(p.frameCount % 3 == 0) _savedFramePlaybackIndex++;
			if(_savedFramePlaybackIndex > _savedFrames.size() - 1) {
				_savedFramePlaybackIndex = 0;
			}
//			p.image(_savedFrames.get(_savedFramePlaybackIndex), 0, 0);
			
			_pg.beginDraw();
			_pg.clear();
			_pg.image(_savedFrames.get(_savedFramePlaybackIndex), 0, 0);
			_pg.endDraw();
			// _pg.filter(_chromaKeyFilter);

			
			_motionBlur.updateToCanvas(_pg, p.g, 0.5f);
		}
		
		
		// hide ControlP5
		p.translate(0, 9000);
		
		// debug memory info
		// if(p.frameCount % 30 == 0) DebugUtil.showMemoryUsage();
	}
	
	protected int newDmxColor() {
		return p.color(
				MathUtil.randRangeDecimal(180, 255), 
				MathUtil.randRangeDecimal(180, 255), 
				MathUtil.randRangeDecimal(180, 255)
		);
	}
	
	protected void runDmxLights() {
		_lightColor1.update();
		_dmxInterface.setColorAtIndex(0, _lightColor1.colorInt());
		_lightColor2.update();
		_dmxInterface.setColorAtIndex(1, _lightColor2.colorInt());
		_dmxInterface.updateColors();
	}
	
	public class FaceSession {
		
		protected ArrayList<PImage> _capturedFrames;

		public FaceSession() {
			_capturedFrames = new ArrayList<PImage>();
		}
		
	}
}
