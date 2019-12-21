package com.haxademic.app.kinectvideoplayer;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.KinectSilhouetteVectorField;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import processing.video.Movie;

public class OpheliasVideoPlayer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
//	protected ArrayList<IMouseable> _mouseables;
	protected String _videoPath;

	protected ArrayList<String> _files;
	protected MovieLayer _movieLayer;
	protected MovieLayer _kinectLayer;
	protected PGraphics _movieComposite; 
	protected PGraphics _lightLeak; 
	
	protected PShader _clipBrightness;
	protected PShader _lightShader;
	
	protected EasingColor _colorGradientCenter;
	protected EasingColor _colorGradientOuter;


	protected boolean _isDebug = false;
	
	protected boolean _playlistSelected = false;
	
	protected float SCALE_DOWN = 0.4f;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "1280" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "720" );
//		p.appConfig.setProperty( AppSettings.WIDTH, "960" );
//		p.appConfig.setProperty( AppSettings.HEIGHT, "540" );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, "false" );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "true" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );

		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		p.appConfig.setProperty( "kinect_mirrored", "false" );
		p.appConfig.setProperty( "kinect_top_pixel", "0" );
		p.appConfig.setProperty( "kinect_bottom_pixel", "480" );
		p.appConfig.setProperty( "kinect_left_pixel", "0" );
		p.appConfig.setProperty( "kinect_right_pixel", "640" );

		p.appConfig.setProperty( "kinect_pixel_skip", "7" );
		p.appConfig.setProperty( "kinect_scan_frames", "40" );
		p.appConfig.setProperty( "kinect_depth_key_dist", "200" );
		p.appConfig.setProperty( "kinect_mirrored", "true" );
		
		// bar kinect setup:
		p.appConfig.setProperty( "kinect_top_pixel", "60" );
		p.appConfig.setProperty( "kinect_bottom_pixel", "400" );
		p.appConfig.setProperty( "kinect_left_pixel", "90" );
		p.appConfig.setProperty( "kinect_right_pixel", "570" );
		p.appConfig.setProperty( "kinect_mirrored", "true" );
		p.appConfig.setProperty( "kinect_flipped", "false" );

		
		// stage kinect setup:
//		p.appConfig.setProperty( "kinect_top_pixel", "200" );
//		p.appConfig.setProperty( "kinect_bottom_pixel", "400" );
//		p.appConfig.setProperty( "kinect_left_pixel", "40" );
//		p.appConfig.setProperty( "kinect_right_pixel", "580" );
//		p.appConfig.setProperty( "kinect_mirrored", "false" );
		
		
		p.appConfig.setProperty( "kinect_blob_bg_int", "81" ); // opacity of the non-kinect-masked whitespace. 0-255
	}

	// TODO:
	// Adjust Pixel Skip variable since kinect is faster now. Warning: by setting it above in overridePropsFile(), things get out of sync with the particle plugin
	// More debug helpers - we need to know what's going on	
	// * Resaturate colors after all compositing 
	// * Remove extra filling-in of depth data at the end of the room scan 	
	// * Make all blob resolution numbers configurable 
	
	public void setupFirstFrame() {
		_videoPath = FileUtil.getHaxademicDataPath()+"video/ophelias/";
		buildMenu();
	}
	
	public void drawApp() {
		
		if(_playlistSelected == false) {
			p.background(0);
			drawMenu();
		} else {
			PG.setPImageAlpha(p, 1f);

//			setShaderValues();
			
			_movieLayer.update();
			_kinectLayer.update();

//			drawVideoBackgrounds();
			drawVideos();
			drawLightLeak();
//			postProcessEffects();
			drawScanProgress();
		}
	}
	
	// Playlist menu ================ 
	public void buildMenu() {
		// get directories
		String[] videoFolders = FileUtil.getDirsInDir(_videoPath);

		// build buttons
//		_mouseables = new ArrayList<IMouseable>();
//		for (int i = 0; i < videoFolders.length; i++) {
//			String[] pathComponents = videoFolders[i].split("/");
//			_mouseables.add( new TextButton( p, pathComponents[pathComponents.length - 1], videoFolders[i], 20, 20 + 80 * i, p.width - 40, 60 ) );
//		}
	}
	
	public void playlistSelected(String playlistDir) {
		_files = FileUtil.getFilesInDirOfTypes(playlistDir, "mp4,m4v,mov");
		for (String file : _files) {
			P.println("File: "+file);
		}
		// FileUtil.shuffleFileList( _files );
		buildVideoPlayer();
		_playlistSelected = true;
		p.noCursor();
	}
	
	protected void drawMenu() {
//		for( int i=0; i < _mouseables.size(); i++ ) {
//			_mouseables.get(i).update( p );
//		}
	}


	// Menu button component callbacks ================ 
//	public void mouseReleased() {
//		if(_playlistSelected == true) return;
//		for( int i=0; i < _mouseables.size(); i++ ) {
//			if( _mouseables.get(i).checkRelease( p.mouseX, p.mouseY ) ) {
//				playlistSelected(_mouseables.get(i).id());
//			}
//		}
//		if(_movieLayer != null) {
//			_movieLayer.randomFrame();
//			_kinectLayer.randomFrame();
//		}
//	}
//
//	public void mousePressed() {
//		if(_playlistSelected == true) return;
//		for( int i=0; i < _mouseables.size(); i++ ) {
//			_mouseables.get(i).checkPress( p.mouseX, p.mouseY );
//		}
//	}
//
//	public void mouseMoved() {
//		if(_playlistSelected == true) return;
//		for( int i=0; i < _mouseables.size(); i++ ) {
//			_mouseables.get(i).checkOver( p.mouseX, p.mouseY );
//		}
//	}

	
	// Video player ================ 
	protected void buildVideoPlayer() {
		// build movie players & composite
		_movieComposite = p.createGraphics(P.round(p.width * SCALE_DOWN), P.round(p.height * SCALE_DOWN), P.P3D);

		_lightLeak = p.createGraphics(P.round(p.width * SCALE_DOWN), P.round(p.height * SCALE_DOWN), P.P3D);
		_lightShader = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/textures/light-leak.glsl" );

		_movieLayer = new MovieLayer(_files);
		_movieLayer.randomFrame();

		_kinectLayer = new KinectLayer();	

		// build colors for sampled gradient
		_colorGradientCenter = new EasingColor("#000000", 150f);
		_colorGradientOuter = new EasingColor("#000000", 150f);
	}
	
	protected void drawVideoBackgrounds() {
		_colorGradientCenter.setTargetColorIntWithBrightness( _movieLayer.colorL(), 2f );
		_colorGradientCenter.update();
		_colorGradientOuter.setTargetColorIntWithBrightness( _movieLayer.colorR(), 2f );
		_colorGradientOuter.update();
		
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		Gradients.radial(p, p.width * 2f, p.height * 2f, _colorGradientCenter.colorInt(), _colorGradientOuter.colorInt(), 12);
		p.popMatrix();
	}
	
	protected void drawVideos() {
		if(_movieLayer.ready() && _kinectLayer.ready()) {
			// desaturate video
			SaturationFilter.instance(p).setSaturation(0.7f);
			SaturationFilter.instance(p).applyTo(_movieLayer.image());

			// apply mask
			_movieLayer.image().mask(_kinectLayer.image());
			
		}
		
		// draw to screen!
		if(_isDebug == false) {
			p.image(_movieLayer.image(), 0, 0, p.width, p.height);
		} else {
			int thirdW = P.round(_movieComposite.width * 0.333f);
			int thirdH = P.round(_movieComposite.height * 0.333f);
			p.image(_movieLayer.image(), 0, 0, thirdW, thirdH);
			p.image(_kinectLayer.image(), 0, thirdH, thirdW, thirdH);

		}
//		postProcessEffects();
	}
	
	protected void setShaderValues() {
		int halftoneKnob = 47;
		if(MidiState.instance().midiCCPercent(0, halftoneKnob) != 0) {
//			_halftone.set("scale", p.midi.midiCCPercent(0, halftoneKnob) * 5f);
		}
	}
	
	protected void drawLightLeak() {
		_lightShader.set("time", (float)p.millis() / 8000.0f);
		_lightLeak.filter(_lightShader);
		SaturationFilter.instance(p).setSaturation(0.2f);
		SaturationFilter.instance(p).applyTo(_lightLeak);
		PG.setPImageAlpha(p, 0.2f);
		p.image(_lightLeak, 0, 0, p.width, p.height);
		PG.setPImageAlpha(p, 1f);
	}
	
	protected void drawScanProgress() {
		float scanProgress = ((KinectLayer) _kinectLayer)._silhouette.getRoomScanProgress();
		if(scanProgress < 0.99f) {
			p.fill(255,0,0, 120);
			p.rect(0,0,p.width * scanProgress, p.height);
		}	
	}
	
	protected void postProcessEffects() {
//		BrightnessFilter.instance(p).setBrightness(1);
//		BrightnessFilter.instance(p).applyTo(p);
	}
	
	public void keyPressed() {
		super.keyPressed();
		
		if( p.key == 'd' ){
			_isDebug = !_isDebug;
		}
	}
	
	public class MovieLayer {

		protected Movie _movie;
		protected ArrayList<String> _videoPaths;
		protected int _movieIndex = -1;
		protected float[] _cropProps = null;
		protected PGraphics _curFrame;
		protected int _whiteOverlayAlpha = 0;

		public MovieLayer(ArrayList<String> videoPaths) {
			_videoPaths = videoPaths;
			if(_videoPaths != null) {
				_movieIndex = MathUtil.randRange(0, _videoPaths.size() - 1);
				playNextMovie();
			}
			initImageBuffers();
		}
		
		public boolean ready() {
			return (_cropProps != null);
		}
		
		public PImage imageToProcess() {
			return _movie;
		}
		
		public PGraphics image() {
			return _curFrame;
		}
		
		public int colorL() {
			return _curFrame.get(
					Math.round( _curFrame.width * 0.3f + (P.sin(p.frameCount * 0.05f) * _curFrame.width * 0.1f) ), 
					Math.round(_curFrame.height * 0.5f)
			);
		}
		
		public int colorR() {
			return _curFrame.get(
					Math.round( _curFrame.width * 0.7f + (P.sin(p.frameCount * 0.05f) * _curFrame.width * 0.1f) ), 
					Math.round(_curFrame.height * 0.5f)
			);
		}
		
		protected void playNextMovie() {
			_movieIndex++;
			if(_movieIndex > _videoPaths.size() - 1) _movieIndex = 0;
			if(_movie != null) {
				_movie.dispose();
				_movie = null;
			}
			_movie = new Movie( p, _videoPaths.get(_movieIndex) );
			_movie.play();
			_movie.loop();
			_movie.jump(0);
			_movie.volume(0);
//			_movie.speed(0.75f);
			// reset crop fill offset properties
			_cropProps = null;
		}
		
		protected void initImageBuffers() {
			_curFrame = p.createGraphics(_movieComposite.width, _movieComposite.height, P.P3D);
			_curFrame.smooth(OpenGLUtil.SMOOTH_LOW);
		}

		public void randomFrame() {
			if(_movie == null) return;
			_movie.jump(_movie.duration() * p.random(0.8f));
		}
		
		public void update() {
			if(_movie != null) {
				if(_movie.time() > _movie.duration() - 5) playNextMovie();
			}
			if(_cropProps == null) {
				if(imageToProcess().width != 0 && imageToProcess().height != 0) {
					_cropProps = ImageUtil.getOffsetAndSizeToCrop(_movieComposite.width, _movieComposite.height, imageToProcess().width, imageToProcess().width, true);
				}
			} else {
				_curFrame.beginDraw();
				_curFrame.clear();
				_curFrame.image(imageToProcess(), _cropProps[0], _cropProps[1], _cropProps[2], _cropProps[3]);
				if(_whiteOverlayAlpha > 0) {
					BlurHFilter.instance(p).applyTo(_curFrame);
					BlurVFilter.instance(p).applyTo(_curFrame);
					_curFrame.fill(255, _whiteOverlayAlpha);
					_curFrame.rect(0, 0, _curFrame.width, _curFrame.height);
				}
				_curFrame.endDraw();
				_curFrame.fill(255);
			}
		}
	}
	
	
	public class KinectLayer extends MovieLayer {
		public KinectSilhouetteVectorField _silhouette;
		public KinectLayer() {
			super(null);
			_whiteOverlayAlpha = 4;
			BlurHFilter.instance(p).setBlurByPercent(0.3f, _curFrame.width);
			BlurVFilter.instance(p).setBlurByPercent(0.3f, _curFrame.height);
			_silhouette = new KinectSilhouetteVectorField(false, true);
		}
		
		public PImage imageToProcess() {
			return _silhouette._canvas;
		}
		
		public void update() {
			_silhouette.update(false);
			super.update();

		}
		
//		public PGraphics mask() {
//			return (PGraphics) _silhouette._kinectPixelated;
//		}
//		
//		public PGraphics maskInverse() {
//			return (PGraphics) _silhouette.debugKinectBuffer();
//		}
	}
}