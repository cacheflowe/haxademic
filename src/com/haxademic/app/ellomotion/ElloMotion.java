package com.haxademic.app.ellomotion;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class ElloMotion
extends PAppletHax{
	
	protected ArrayList<String> _files;
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
	
	protected PShader _lightShader;

	protected boolean _isDebug = false;
	
	protected KinectLayer _kinectLayer;
	protected PImage _gallery;
		
	protected float SCALE_DOWN = 0.5f;
	protected float BLOB_DETECT_SCALE = 0.6f;

	public static void main(String args[]) {
		_isFullScreen = false;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", ElloMotion.class.getName() });
	}

	protected void overridePropsFile() {
		p.appConfig.setProperty( "width", "1280" );
		p.appConfig.setProperty( "height", "720" );
//		p.appConfig.setProperty( "width", "960" );
//		p.appConfig.setProperty( "height", "540" );
//		p.appConfig.setProperty( "fills_screen", "true" );
		p.appConfig.setProperty( "fullscreen", "false" );
		p.appConfig.setProperty( "rendering", "false" );
		p.appConfig.setProperty( "hide_cursor", "true" );
		p.appConfig.setProperty( "force_foreground", "false" );

		p.appConfig.setProperty( "kinect_active", "true" );
		p.appConfig.setProperty( "kinect_far", "500" );
		p.appConfig.setProperty( "kinect_far", "1500" );
		p.appConfig.setProperty( "kinect_pixel_skip", "5" );
		p.appConfig.setProperty( "kinect_mirrored", "true" );		
	}
	
	public void setup() {
		super.setup();
		_gallery = p.loadImage(FileUtil.getFile("images/ello-art/ello-echophon.jpg"));
		buildCanvas();
		_kinectLayer = new KinectLayer();
	}
	
	public void drawApp() {
		background(0);
		_kinectLayer.setGallery(_gallery);
		_kinectLayer.update();
		p.image(_kinectLayer.canvas(), 0, 0, p.width, p.height);
//		p.image(_kinectLayer.mask(), 0, 0, p.width, p.height);
//		postProcessEffects();
	}

	
	// Video player ================ 
	protected void buildCanvas() {
		// build movie players & composite
		_movieComposite = p.createGraphics(P.round(p.width * SCALE_DOWN), P.round(p.height * SCALE_DOWN), P.OPENGL);

		
		// build shaders
		_desaturate = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/saturation.glsl" );
		_desaturate.set("saturation", 0.6f);
		_threshold = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blackandwhite.glsl" );
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
	
	
	
	
	
	public class KinectLayer {

		public KinectSilhouetteVectorField _silhouette;
		protected float[] _cropProps = null;
		protected PGraphics _particleMask;
		protected PGraphics _galleryImg;

		public KinectLayer() {
			_silhouette = new KinectSilhouetteVectorField(BLOB_DETECT_SCALE, false, true);
			initImageBuffer();
		}
		
		protected void initImageBuffer() {
			_particleMask = p.createGraphics(_movieComposite.width, _movieComposite.height, P.OPENGL);
			_galleryImg = p.createGraphics(_movieComposite.width, _movieComposite.height, P.OPENGL);
			_particleMask.smooth(OpenGLUtil.SMOOTH_LOW);
			_galleryImg.smooth(OpenGLUtil.SMOOTH_LOW);
		}
		
		public PGraphics canvas() {
			return _galleryImg;
		}
		
		public PGraphics mask() {
			return _particleMask;
//			return (PGraphics) _silhouette._kinectPixelated;
		}
		
		public PGraphics maskInverse() {
			return (PGraphics) _silhouette.debugKinectBuffer();
		}
		
		public void setGallery(PImage img) {
			_galleryImg.beginDraw();
			_galleryImg.clear();
			float[] cropProps = ImageUtil.getOffsetAndSizeToCrop(p.width * SCALE_DOWN, p.height * SCALE_DOWN, img.width, img.height, true);
			_galleryImg.image(img, cropProps[0], cropProps[1], cropProps[2], cropProps[3]);
			_galleryImg.endDraw();
		}
		
		public void update() {
			if(_cropProps == null) {
				if(_silhouette._canvas.width != 0 && _silhouette._canvas.height != 0) {
					_cropProps = ImageUtil.getOffsetAndSizeToCrop(p.width * SCALE_DOWN, p.height * SCALE_DOWN, _silhouette._canvas.width, _silhouette._canvas.height, true);
				}
			}
			_silhouette.update(false);
			
			_particleMask.beginDraw();
			_particleMask.clear();
			if(_silhouette._canvas != null) _particleMask.image(_silhouette._canvas, _cropProps[0], _cropProps[1], _cropProps[2], _cropProps[3]);
			_particleMask.endDraw();
			
			_galleryImg.mask(_particleMask);
		}
	}
}