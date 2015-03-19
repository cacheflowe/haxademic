package com.haxademic.app.ophelias;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import processing.video.Movie;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.components.IMouseable;
import com.haxademic.core.components.TextButton;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class OpheliasVideoPlayer
extends PAppletHax{
	
	protected ArrayList<IMouseable> _mouseables;
	protected String _videoPath;

	protected ArrayList<String> _files;
	protected MovieLayer _movieLayer1;
	protected MovieLayer _movieLayer2;
	protected PGraphics _movieComposite;
	
	protected PShader _desaturate;
	protected PShader _vignette;
	protected PShader _postBrightness;
	protected PShader _clipBrightness;
	protected PShader _threshold;
	protected PShader _blurH;
	protected PShader _blurV;
	protected PShader _invert;
	protected PShader _badTV;
	protected PShader edge;
	protected PShader _halftone;
	protected PShader _mirror;
	protected PShader _contrast;

	protected boolean _isDebug = false;
	
	protected boolean _playlistSelected = false;
	
	protected float SCALE_DOWN = 0.5f;

	public static void main(String args[]) {
		_isFullScreen = true;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", OpheliasVideoPlayer.class.getName() });
	}

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "1280" );
		_appConfig.setProperty( "height", "720" );
		_appConfig.setProperty( "width", "960" );
		_appConfig.setProperty( "height", "540" );
		_appConfig.setProperty( "fullscreen", "true" );
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "kinect_active", "false" );
		_appConfig.setProperty( "kinect_mirrored", "true" );
	}

	// TODO:
	// * Performance - make sure videos only draw what they need for the current mask mode
	// 		* Check memory use while running
	// * Add kinect version with solid background
	// * Swap video when one ends
	// * Oscillate between effect values
	// * Change filters & masks occasionally
	// * Add particle system to masks to make them more organic
	
	public void setup() {
		super.setup();
		
		_videoPath = FileUtil.getHaxademicDataPath()+"video/ophelias/";
		buildMenu();
	}
	
	public void drawApp() {
		p.background(0);
		
		if(_playlistSelected == false) {
			drawMenu();
		} else {
			drawVideos();
		}
	}
	
	// Playlist menu ================ 
	public void buildMenu() {
		// get directories
		String[] videoFolders = FileUtil.getDirsInDir(_videoPath);

		// build buttons
		_mouseables = new ArrayList<IMouseable>();
		for (int i = 0; i < videoFolders.length; i++) {
			String[] pathComponents = videoFolders[i].split("/");
			_mouseables.add( new TextButton( p, pathComponents[pathComponents.length - 1], videoFolders[i], 20, 20 + 80 * i, p.width - 40, 60 ) );
		}
	}
	
	public void playlistSelected(String playlistDir) {
		_files = FileUtil.getFilesInDirOfTypes(playlistDir, "mp4,m4v");
		FileUtil.shuffleFileList( _files );
		buildVideoPlayer();
		_playlistSelected = true;
	}
	
	protected void drawMenu() {
		for( int i=0; i < _mouseables.size(); i++ ) {
			_mouseables.get(i).update( p );
		}
	}


	// Menu button component callbacks ================ 
	public void mouseReleased() {
		for( int i=0; i < _mouseables.size(); i++ ) {
			if( _mouseables.get(i).checkRelease( p.mouseX, p.mouseY ) ) {
				playlistSelected(_mouseables.get(i).id());
			}
		}
		if(_movieLayer1 != null) {
			_movieLayer1.randomFrame();
			_movieLayer2.randomFrame();
		}
	}

	public void mousePressed() {
		for( int i=0; i < _mouseables.size(); i++ ) {
			_mouseables.get(i).checkPress( p.mouseX, p.mouseY );
		}
	}

	public void mouseMoved() {
		for( int i=0; i < _mouseables.size(); i++ ) {
			_mouseables.get(i).checkOver( p.mouseX, p.mouseY );
		}
	}

	
	// Video player ================ 
	protected void buildVideoPlayer() {
		// build movie players & composite
		_movieComposite = p.createGraphics(P.round(p.width * SCALE_DOWN), P.round(p.height * SCALE_DOWN), P.OPENGL);

		_movieLayer1 = new MovieLayer(_files.get(0));
		_movieLayer2 = new MovieLayer(_files.get(1));	
//		_movieLayer2 = new KinectLayer();	
		
		_movieLayer1.randomFrame();
		_movieLayer2.randomFrame();

		
		
		// build shaders
		_desaturate = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/saturation.glsl" );
		_threshold = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blackandwhite.glsl" );
		_threshold.set("cutoff", 0.5f);
		_blurV = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-vertical.glsl" );
		_blurV.set( "v", 5f/p.height );
		_blurH = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-horizontal.glsl" );
		_blurH.set( "h", 5f/p.width );
		_invert = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/invert.glsl" );
		
		_badTV = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/badtv.glsl" );
		_badTV.set("time", millis() / 1000.0f);
		_badTV.set("grayscale", 0);
		_badTV.set("nIntensity", 0.75f);
		_badTV.set("sIntensity", 0.55f);
		_badTV.set("sCount", 4096.0f);


		edge = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/edges.glsl" ); 
		
		_halftone = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/dotscreen.glsl" ); 
		_halftone.set("tSize", 256f, 256f);
		_halftone.set("center", 0.5f, 0.5f);
		_halftone.set("angle", 1.57f);
		_halftone.set("scale", 1f);

		_mirror = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/mirror.glsl" ); 

		_contrast = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/contrast.glsl" ); 
		_contrast.set("contrast", 1.4f);
		
		_vignette = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.85f);
		_vignette.set("spread", 0.25f);

		_postBrightness = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/brightness.glsl" );
		_postBrightness.set("brightness", 1.0f );

		_clipBrightness = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/brightness.glsl" );
		_clipBrightness.set("brightness", 2.0f );
		
	}
	
	protected void drawVideos() {
		setShaderValues();
		
		_movieLayer1.update();
		_movieLayer2.update();
		
		if(_movieLayer1.ready() && _movieLayer2.ready()) {
			_movieLayer1.updateMask();
			_movieLayer1.updateMaskInverse();
			//			_movieLayer1.image().mask(_movieLayer1.mask());
			
			_movieLayer2.updateMask();
			_movieLayer2.updateMaskInverse();
			_movieLayer2.image().mask(_movieLayer1.mask());
			
			if(_isDebug == false) {
				_movieComposite.beginDraw();
				_movieComposite.clear();
				_movieComposite.image(_movieLayer1.image(), 0, 0);
				_movieComposite.image(_movieLayer2.image(), 0, 0);
				_movieComposite.endDraw();
			} else {
				_movieComposite.beginDraw();
				_movieComposite.clear();
				_movieComposite.image(_movieLayer1.image(), 0, 0, _movieComposite.width * 0.333f, _movieComposite.height * 0.333f);
				_movieComposite.image(_movieLayer1.mask(), _movieComposite.width * 0.333f, 0, _movieComposite.width * 0.333f, _movieComposite.height * 0.333f);
				_movieComposite.image(_movieLayer1.maskInverse(), _movieComposite.width * 0.666f, 0, _movieComposite.width * 0.333f, _movieComposite.height * 0.333f);
				_movieComposite.image(_movieLayer2.image(), 0, _movieComposite.height * 0.333f, _movieComposite.width * 0.333f, _movieComposite.height * 0.333f);
				_movieComposite.image(_movieLayer2.mask(), _movieComposite.width * 0.333f, _movieComposite.height * 0.333f, _movieComposite.width * 0.333f, _movieComposite.height * 0.333f);
				_movieComposite.image(_movieLayer2.maskInverse(), _movieComposite.width * 0.666f, _movieComposite.height * 0.333f, _movieComposite.width * 0.333f, _movieComposite.height * 0.333f);
				_movieComposite.endDraw();
			}
		}
		
		//		_movieComposite.filter(_blurVPost);
		
		p.image(_movieComposite, 0, 0, p.width, p.height);
		postProcessEffects();
	}
	
	protected void setShaderValues() {
		int brightnessKnob = 48;
		if(p.midi.midiCCPercent(0, brightnessKnob) != 0) {
			_postBrightness.set("brightness", p.midi.midiCCPercent(0, brightnessKnob) * 5f);
		}

		int halftoneKnob = 47;
		if(p.midi.midiCCPercent(0, halftoneKnob) != 0) {
			_halftone.set("scale", p.midi.midiCCPercent(0, halftoneKnob) * 5f);
		}
		
		int thresholdKnob = 46;
		if(p.midi.midiCCPercent(0, thresholdKnob) != 0) {
			_threshold.set("cutoff", p.midi.midiCCPercent(0, thresholdKnob));
		}
	}
	
	protected void postProcessEffects() {
		p.filter(_vignette);
		
//		_badTV.set("time", millis() / 1000.0f);
//		p.filter(_badTV);
		p.filter( _postBrightness );		
	}
	
	protected void handleInput( boolean isMidi ) {
		super.handleInput( isMidi );
		
		if( p.key == 'd' ){
			_isDebug = !_isDebug;
		}
	}
	
//	public void mousePressed() {
//		super.mousePressed();
//		_movieLayer1.randomFrame();
//		_movieLayer2.randomFrame();
//	}
	
	public class MovieLayer {

		protected Movie _movie;
		protected float[] _cropProps = null;
		protected PGraphics _curFrame;
		protected PGraphics _curFrameMask;
		protected PGraphics _curFrameMaskInverse;

		public MovieLayer(String videoPath) {
			initMovie(videoPath);
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
		
		public PGraphics mask() {
			return _curFrameMask;
		}
		
		public PGraphics maskInverse() {
			return _curFrameMaskInverse;
		}
		
		protected void initMovie(String videoPath) {
			if(videoPath != null) {
				_movie = new Movie( p, videoPath );
				_movie.play();
				_movie.loop();
				_movie.jump(0);
				_movie.speed(0.8f);
			}
			_curFrame = p.createGraphics(_movieComposite.width, _movieComposite.height, P.OPENGL);
			_curFrameMask = p.createGraphics(_movieComposite.width, _movieComposite.height, P.OPENGL);
			_curFrameMaskInverse = p.createGraphics(_movieComposite.width, _movieComposite.height, P.OPENGL);
		}

		public void randomFrame() {
			if(_movie == null) return;
			_movie.jump(_movie.duration() * p.random(0.8f));
		}
		
		public void update() {
			if(_cropProps == null && imageToProcess().width != 0 && imageToProcess().height != 0) {
				_cropProps = ImageUtil.getOffsetAndSizeToCrop(_movieComposite.width, _movieComposite.height, imageToProcess().width, imageToProcess().width, true);
			} else {
				_curFrame.beginDraw();
				_curFrame.clear();
				_curFrame.image(imageToProcess(), _cropProps[0], _cropProps[1], _cropProps[2], _cropProps[3]);
				_curFrame.endDraw();
//				_curFrame.filter(_mirror);
			}
		}
		
		public void updateMask() {
			if(_cropProps == null) return;
			_curFrameMask.beginDraw();
			_curFrameMask.clear();
			_curFrameMask.image(imageToProcess(), _cropProps[0], _cropProps[1], _cropProps[2], _cropProps[3]);
			_curFrameMask.endDraw();
			_curFrameMask.filter(_clipBrightness);
			_curFrameMask.filter(_contrast);
			_curFrameMask.filter(_threshold);
			_curFrameMask.filter(_blurH);
			_curFrameMask.filter(_blurV);
//			_curFrameMask.filter(_halftone);
		}
		
		public void updateMaskInverse() {
			if(_cropProps == null) return;
			_curFrameMaskInverse.beginDraw();
			_curFrameMaskInverse.clear();
			_curFrameMaskInverse.image(imageToProcess(), _cropProps[0], _cropProps[1], _cropProps[2], _cropProps[3]);
			_curFrameMaskInverse.endDraw();
			_curFrameMaskInverse.filter(_clipBrightness);
			_curFrameMaskInverse.filter(_contrast);
			_curFrameMaskInverse.filter(_threshold);
			_curFrameMaskInverse.filter(_blurH);
			_curFrameMaskInverse.filter(_blurV);
			_curFrameMaskInverse.filter(_invert);
		}
	}
	
	
	public class KinectLayer extends MovieLayer {
		protected KinectSilhouettePG _silhouette;
		public KinectLayer() {
			super(null);
			_silhouette = new KinectSilhouettePG();
		}
		
		public PImage imageToProcess() {
			return _silhouette._canvas;
		}
		
		public void update() {
			_silhouette.update();
			super.update();
		}
	}
}