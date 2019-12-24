package com.haxademic.sketch.hardware.kinect_openni;

import java.util.ArrayList;

import com.haxademic.app.haxmapper.HaxMapper;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.filters.pshader.BadTVLinesFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.WobbleFilter;
import com.haxademic.core.draw.image.MotionBlurPGraphics;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.hardware.dmx.DmxAjaxProManagerInterface;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;


public class KinectFaceRecorder {

	public static final float PIXEL_SIZE = 20;
	public static final int KINECT_TOP = 0;
	public static final int KINECT_BOTTOM = 480;
	public static final int KINECT_CLOSE = 0;
	public static final int KINECT_FAR = 600;
	
	protected final int MAX_SESSIONS = 3;
	
	protected int _playbackIndexSession = 0;
	protected int _playbackIndexFrame = 0;
	protected int _curSessionIndexFrame = 0;
	protected boolean _isRecording = false;
	protected ArrayList<FaceSession> _faceSessions;
	protected FaceSession _curFaceSession;
	protected MotionBlurPGraphics _motionBlurPlayback;
	protected MotionBlurPGraphics _motionBlurCurrentSession;
	protected PGraphics _realtimeTexture;
	public static PGraphics curSessionTexture;
	public static PGraphics playbackTexture;

	PShader _chromaKeyFilter;
//	public float thresholdSensitivity;
//	public float smoothing;
//	public float colorToReplaceR;
//	public float colorToReplaceG;
//	public float colorToReplaceB;
	
	protected DmxAjaxProManagerInterface _dmxInterface;
	protected EasingColor _lightColor1;
	protected EasingColor _lightColor2;
	
	protected HaxMapper haxMapper;

	public KinectFaceRecorder(HaxMapper haxMapper) {
		this.haxMapper = haxMapper;
		
		_playbackIndexSession = 0;
		_playbackIndexFrame = 0;

		// temporary canvas to preprocess frames before storing them. also used to display the realtime texture 
		playbackTexture = P.p.createGraphics( 320, 240, P.P3D );
		playbackTexture.smooth();
		
		curSessionTexture = P.p.createGraphics( 320, 240, P.P3D );
		curSessionTexture.smooth();

		_realtimeTexture = P.p.createGraphics( 320, 240, P.P3D );
		_realtimeTexture.smooth();
		
		_faceSessions = new ArrayList<FaceSession>();
		
		_motionBlurPlayback = new MotionBlurPGraphics(14);
		_motionBlurCurrentSession = new MotionBlurPGraphics(7);
		
		setupChromakey();
		setupDmxLights();
	}
		
	public boolean isRecording() {
		return _isRecording;
	}
		
	protected void setupDmxLights() {
		_dmxInterface = new DmxAjaxProManagerInterface(2);
		_lightColor1 = new EasingColor(0, 0, 0, 1, 10);
		_lightColor2 = new EasingColor(0, 0, 0, 1, 10);
	}
	
	protected void setupChromakey() {
		_chromaKeyFilter = P.p.loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/chroma-color.glsl" );
		_chromaKeyFilter.set("thresholdSensitivity", 0.75f);
		_chromaKeyFilter.set("smoothing", 0.25f);
		_chromaKeyFilter.set("colorToReplace", 0.2f, 0.4f, 0.1f);
	}

	public void update(boolean isRecordingNow, boolean isPlaybackActive) {
//		_chromaKeyFilter.set("thresholdSensitivity", thresholdSensitivity);
//		_chromaKeyFilter.set("smoothing", smoothing);
//		_chromaKeyFilter.set("colorToReplace", 0, 0, colorToReplaceB);
		
		
		runDmxLights();
		if(isPlaybackActive == true) playBackSavedFrames();
		if(isRecordingNow == true) playBackCurSessionFrames();
		if(isRecordingNow == true) copyCameraToRealtimeTexture();
		detectAnyFace();
		if(isRecordingNow == true) saveFramesWhileRecording();

		// debug draw realtime camera
//		p.image(_realtimeTexture, 640, 0);
		
		// p.translate(0, 9000);
		
		// debug memory info
		// if(p.frameCount % 30 == 0) DebugUtil.showMemoryUsage();
	}
	
	protected void copyCameraToRealtimeTexture() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;

		_realtimeTexture.beginDraw();
		// _realtimeTexture.copy(p.kinectWrapper.getRgbImage(), 0, 0, 640, 480, 0, 0, _realtimeTexture.width, _realtimeTexture.height);
		_realtimeTexture.copy(depthCamera.getRgbImage(), 160, 120, 320, 240, 0, 0, _realtimeTexture.width, _realtimeTexture.height);
		_realtimeTexture.endDraw();
		_realtimeTexture.filter(_chromaKeyFilter);
	}

	protected void detectAnyFace() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		
		// loop through kinect data within player's control range
		float pixelDepth;
		int numPixels = 0;
		for ( int x = 0; x < DepthCameraSize.WIDTH; x += PIXEL_SIZE ) {
			for ( int y = KINECT_TOP; y < KINECT_BOTTOM; y += PIXEL_SIZE ) {
				pixelDepth = depthCamera.getDepthAt( x, y );
				if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
					numPixels++;
				}
			}
		}
		
		// detect a person for record mode
		if(numPixels > 30) {
			startRecording();
		} else {
			stopRecording();
		}
	}
	
	protected void saveFramesWhileRecording() {
		// save frames while recording
		if(_isRecording == true && P.p.frameCount % 20 == 0) {
			_curFaceSession.addFrame(_realtimeTexture.get());
		}
	}
	
	protected void startRecording() {
		if(_isRecording == false) {
			_isRecording = true;
			_curFaceSession = new FaceSession();
			_motionBlurCurrentSession.clearFrames();
			if(_faceSessions.size() > MAX_SESSIONS && _playbackIndexSession != 0) {
				_faceSessions.get(0).disposeFrames();
				_faceSessions.remove(0);
			}
			_lightColor1.setTargetInt(newDmxColor());
			_lightColor2.setTargetInt(newDmxColor());
			
			if(haxMapper != null) haxMapper.startFaceRecording();
		}
	}
	
	protected void stopRecording() {
		if(_isRecording == true) {
			_isRecording = false;
			_curFaceSession.endRecordSession();
			_faceSessions.add(_curFaceSession);
			_curFaceSession = null;
			_lightColor1.setTargetInt(P.p.color(0));
			_lightColor2.setTargetInt(P.p.color(0));
			
			if(haxMapper != null) haxMapper.stopFaceRecording();
		}
	}
	
	protected void playBackCurSessionFrames() {
		if(_curFaceSession != null) {			
			// step through sessions & their frames every x frames
			if(P.p.frameCount % 3 == 0) {
				_curSessionIndexFrame++;
				if(_curSessionIndexFrame >= _curFaceSession.frameCount()) {
					_curSessionIndexFrame = 0;
				}
			}
						
			// draw current playback frame
			if(_curFaceSession.frameCount() > 0) {
				// draw playback to screen
				curSessionTexture.beginDraw();
				curSessionTexture.clear();
				_motionBlurCurrentSession.updateToCanvas(_curFaceSession.getFrame(_curSessionIndexFrame), curSessionTexture, 0.5f);
				curSessionTexture.endDraw();
				
				BrightnessFilter.instance(P.p).setBrightness(1.75f + 0.25f * P.sin(P.p.frameCount/20f));
				BrightnessFilter.instance(P.p).applyTo(curSessionTexture);
				WobbleFilter.instance(P.p).setTime(P.p.frameCount/10f);
				WobbleFilter.instance(P.p).setSpeed(0.8f);
				WobbleFilter.instance(P.p).setStrength(0.004f + 0.004f * P.sin(P.p.frameCount/25f));
				WobbleFilter.instance(P.p).setSize(100f);
				WobbleFilter.instance(P.p).applyTo(curSessionTexture);
				BadTVLinesFilter.instance(P.p).applyTo(curSessionTexture);

			} 
		}
		// p.image(_curSessionTexture, 320, 0);
	}
	
	protected void playBackSavedFrames() {
		if(_faceSessions.size() > 0) {
			// _pg.filter(_chromaKeyFilter);
			if(_playbackIndexSession >= _faceSessions.size()) _playbackIndexSession = 0;
			FaceSession curSession = _faceSessions.get(_playbackIndexSession);

			// step through sessions & their frames every x frames
			if(P.p.frameCount % 8 == 0) {
				_playbackIndexFrame++;
				if(_playbackIndexFrame >= curSession.frameCount()) {
					_playbackIndexFrame = 0;
					_playbackIndexSession++;
					if(_playbackIndexSession >= _faceSessions.size()) _playbackIndexSession = 0;
					
					// clean up old sessions
					if(_faceSessions.size() > MAX_SESSIONS && _playbackIndexSession == 0) {
						_faceSessions.get(0).disposeFrames();
						_faceSessions.remove(0);
					}
					
					// make sure we have a fresh reference to the current session
					curSession = _faceSessions.get(_playbackIndexSession);
					_playbackIndexFrame = 0;
				}
			}
			
			// P.println(_playbackIndexSession, _playbackIndexFrame);
			
			// draw current playback frame
			if(curSession.frameCount() > 0) {
				// draw playback to screen
				playbackTexture.beginDraw();
				playbackTexture.clear();
				_motionBlurPlayback.updateToCanvas(curSession.getFrame(_playbackIndexFrame), playbackTexture, 0.5f);
				playbackTexture.endDraw();
				// p.image(_playbackTexture, 0, 0);
			}
		}
	}
	
	protected int newDmxColor() {
		return P.p.color(
				MathUtil.randRangeDecimal(180, 255), 
				MathUtil.randRangeDecimal(220, 255), 
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
		
		protected int MAX_SAVED_FRAMES = 3;
		protected int MAX_CUR_FRAMES = 6;
		
		protected ArrayList<PImage> _capturedFrames;

		public FaceSession() {
			_capturedFrames = new ArrayList<PImage>();
		}
		
		public int frameCount() {
			return _capturedFrames.size();
		}
		
		public PImage getFrame(int index) {
			if(index >= frameCount()) index = 0; 
			return _capturedFrames.get(index);
		}
		
		public void addFrame(PImage newFrame) {
			if(_capturedFrames.size() > MAX_CUR_FRAMES) _capturedFrames.remove(0);
			_capturedFrames.add(newFrame);
		}
		
		public void endRecordSession() {
			// keep only a few random frames
			while(_capturedFrames.size() > MAX_SAVED_FRAMES) _capturedFrames.remove(MathUtil.randRange(0, _capturedFrames.size() - 1));
		}
		
		public void disposeFrames() {
			_capturedFrames.clear();
		}
	}
}
