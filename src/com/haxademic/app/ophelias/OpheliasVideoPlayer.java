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
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.draw.shapes.Gradients;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.hardware.kinect.KinectSilhouetteBasic;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
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
	protected PGraphics _lightLeak; 
	
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
	
	protected PShader _lightShader;
	
	protected ColorHaxEasing _colorGradientCenter;
	protected ColorHaxEasing _colorGradientOuter;


	protected boolean _isDebug = false;
	
	protected boolean _playlistSelected = false;
	
	protected float SCALE_DOWN = 0.5f;
	protected float BLOB_DETECT_SCALE = 0.6f;

	public static void main(String args[]) {
		_isFullScreen = false;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", OpheliasVideoPlayer.class.getName() });
	}

	protected void overridePropsFile() {
		p.appConfig.setProperty( "width", "1280" );
		p.appConfig.setProperty( "height", "720" );
//		p.appConfig.setProperty( "width", "960" );
//		p.appConfig.setProperty( "height", "540" );
		p.appConfig.setProperty( "fullscreen", "false" );
		p.appConfig.setProperty( "rendering", "false" );
		p.appConfig.setProperty( "hide_cursor", "true" );

		p.appConfig.setProperty( "kinect_active", "true" );
		p.appConfig.setProperty( "kinect_mirrored", "false" );
		p.appConfig.setProperty( "kinect_top_pixel", "0" );
		p.appConfig.setProperty( "kinect_bottom_pixel", "480" );
		p.appConfig.setProperty( "kinect_left_pixel", "0" );
		p.appConfig.setProperty( "kinect_right_pixel", "640" );

		p.appConfig.setProperty( "kinect_pixel_skip", "5" );
		p.appConfig.setProperty( "kinect_scan_frames", "400" );
		p.appConfig.setProperty( "kinect_depth_key_dist", "200" );
		p.appConfig.setProperty( "kinect_mirrored", "true" );
		
		// bar kinect setup:
		p.appConfig.setProperty( "kinect_top_pixel", "60" );
		p.appConfig.setProperty( "kinect_bottom_pixel", "400" );
		p.appConfig.setProperty( "kinect_left_pixel", "90" );
		p.appConfig.setProperty( "kinect_right_pixel", "570" );
		p.appConfig.setProperty( "kinect_mirrored", "true" );

		
		// stage kinect setup:
//		p.appConfig.setProperty( "kinect_top_pixel", "200" );
//		p.appConfig.setProperty( "kinect_bottom_pixel", "400" );
//		p.appConfig.setProperty( "kinect_left_pixel", "40" );
//		p.appConfig.setProperty( "kinect_right_pixel", "580" );
//		p.appConfig.setProperty( "kinect_mirrored", "false" );
		
		
		p.appConfig.setProperty( "kinect_blob_bg_int", "31" ); // opacity of the non-kinect-masked whitespace. 0-255
	}

	// TODO:
	// * Resaturate colors after all compositing 
	// * Push blob vertices out to make bigger blobs 
	// * Remove extra filling-in of depth data at the end of the room scan 
	
	// * Increase kinect data resolution
	//		* Quad tree?
	// * Make all blob resolution numbers configurable 
	// * Performance - make sure videos only draw what they need for the current mask mode
	// 		* Check memory use while running
	
	public void setup() {
		super.setup();
		
		_videoPath = FileUtil.getHaxademicDataPath()+"video/ophelias/";
		buildMenu();
	}
	
	public void drawApp() {
		
		if(_playlistSelected == false) {
			p.background(0);
			drawMenu();
		} else {
			DrawUtil.setPImageAlpha(p, 1f);

			setShaderValues();
			
			_movieLayer1.update();
			_movieLayer2.update();

			drawVideoBackgrounds();
			postProcessEffects();
			drawVideos();
			drawLightLeak();
			drawScanProgress();
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
		_files = FileUtil.getFilesInDirOfTypes(playlistDir, "mp4,m4v,mov");
		for (String file : _files) {
			P.println("File: "+file);
		}
		// FileUtil.shuffleFileList( _files );
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
		_lightLeak = p.createGraphics(P.round(p.width * SCALE_DOWN), P.round(p.height * SCALE_DOWN), P.OPENGL);

		_movieLayer1 = new MovieLayer(_files);
//		_movieLayer2 = new MovieLayer(_files.get(1));	
		_movieLayer2 = new KinectLayer();	
		
		_movieLayer1.randomFrame();
		_movieLayer2.randomFrame();

		
		
		// build shaders
		_desaturate = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/saturation.glsl" );
		_desaturate.set("saturation", 0.6f);
		_threshold = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/threshold.glsl" );
		_threshold.set("cutoff", 0.5f);
		_blurV = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-vertical.glsl" );
		_blurV.set( "v", 3f/p.height );
		_blurH = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-horizontal.glsl" );
		_blurH.set( "h", 3f/p.width );
		_invert = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/invert.glsl" );

		_lightShader = loadShader( FileUtil.getHaxademicDataPath()+"shaders/textures/light-leak.glsl" );
		
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
		
		
		// build colors for sampled gradient
		_colorGradientCenter = new ColorHaxEasing("#000000", 150f);
		_colorGradientOuter = new ColorHaxEasing("#000000", 150f);

	}
	
	protected void drawVideoBackgrounds() {
		_colorGradientCenter.setTargetColorIntWithBrightness( _movieLayer1.colorL(), 2f );
		_colorGradientCenter.update();
		_colorGradientOuter.setTargetColorIntWithBrightness( _movieLayer1.colorR(), 2f );
		_colorGradientOuter.update();
		
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		Gradients.radial(p, p.width * 2f, p.height * 2f, _colorGradientCenter.colorInt(), _colorGradientOuter.colorInt(), 12);
		p.popMatrix();
	}
	
	protected void drawVideos() {
		if(_movieLayer1.ready() && _movieLayer2.ready()) {
			// desaturate video
			_movieLayer1.image().filter(_desaturate);
			// blur kinect blobs
			_movieLayer2.image().filter(_blurH);
			_movieLayer2.image().filter(_blurV);
			// apply mask
			_movieLayer1.image().mask(_movieLayer2.image());
			
		}
		
		// draw to screen!
		if(_isDebug == false) {
			p.image(_movieLayer1.image(), 0, 0, p.width, p.height);
		} else {
			int thirdW = P.round(_movieComposite.width * 0.333f);
			int thirdH = P.round(_movieComposite.height * 0.333f);
			p.image(_movieLayer1.image(), 0, 0, thirdW, thirdH);
			p.image(_movieLayer1.mask(), thirdW, 0, thirdW, thirdH);
			p.image(_movieLayer1.maskInverse(), _movieComposite.width * 0.666f, 0, thirdW, thirdH);
			p.image(_movieLayer2.image(), 0, thirdH, thirdW, thirdH);
			p.image(_movieLayer2.mask(), thirdW, thirdH, thirdW, thirdH);
			p.image(_movieLayer2.maskInverse(), thirdW * 2, thirdH, thirdW, thirdH);

		}
//		postProcessEffects();
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
	
	protected void drawLightLeak() {
		_lightShader.set("time", p.millis() / 2000.0f);
		_lightLeak.filter(_lightShader);
		_lightLeak.filter(_desaturate);
		DrawUtil.setPImageAlpha(p, 0.2f);
		p.image(_lightLeak, 0, 0, p.width, p.height);
		DrawUtil.setPImageAlpha(p, 1f);
	}
	
	protected void drawScanProgress() {
		float scanProgress = ((KinectLayer) _movieLayer2)._silhouette.getRoomScanProgress();
		if(scanProgress < 0.99f) {
			p.fill(255,0,0, 120);
			p.rect(0,0,p.width * scanProgress, p.height);
		}	
	}
	
	protected void postProcessEffects() {
		p.filter(_vignette);
		
		p.filter( _postBrightness );		
		_badTV.set("time", millis() / 1000.0f);
//		p.filter(_badTV);
	}
	
	protected void handleInput( boolean isMidi ) {
		super.handleInput( isMidi );
		
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
		protected PGraphics _curFrameMask;
		protected PGraphics _curFrameMaskInverse;

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
		
		public PGraphics mask() {
			return _curFrameMask;
		}
		
		public PGraphics maskInverse() {
			return _curFrameMaskInverse;
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
			_movie.speed(0.75f);
			// reset crop fill offset properties
			_cropProps = null;
		}
		
		protected void initImageBuffers() {
			_curFrame = p.createGraphics(_movieComposite.width, _movieComposite.height, P.OPENGL);
			_curFrameMask = p.createGraphics(_movieComposite.width, _movieComposite.height, P.OPENGL);
			_curFrameMaskInverse = p.createGraphics(_movieComposite.width, _movieComposite.height, P.OPENGL);
			_curFrame.smooth(OpenGLUtil.SMOOTH_LOW);
			_curFrameMask.smooth(OpenGLUtil.SMOOTH_LOW);
			_curFrameMaskInverse.smooth(OpenGLUtil.SMOOTH_LOW);
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
				_curFrame.endDraw();
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
//			_curFrameMask.filter(_blurH);
//			_curFrameMask.filter(_blurV);
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
		public KinectSilhouetteBasic _silhouette;
		public KinectLayer() {
			super(null);
			_silhouette = new KinectSilhouetteBasic(BLOB_DETECT_SCALE, true, true);
		}
		
		public PImage imageToProcess() {
			return _silhouette._canvas;
		}
		
		public PGraphics mask() {
			return (PGraphics) _silhouette._kinectPixelated;
		}
		
		public PGraphics maskInverse() {
			return (PGraphics) _silhouette.debugKinectBuffer();
		}
		
		public void update() {
			_silhouette.update(true);
			super.update();
		}
	}
}