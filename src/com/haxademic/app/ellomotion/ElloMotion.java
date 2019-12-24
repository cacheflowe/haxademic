package com.haxademic.app.ellomotion;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.KinectRegionGrid;
import com.haxademic.core.hardware.depthcamera.KinectSilhouetteVectorField;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;

import processing.core.PGraphics;
import processing.core.PImage;

public class ElloMotion
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ArrayList<String> _files;
	protected PGraphics _movieComposite; 
	
	protected boolean _isDebug = false;
	
	protected KinectRegionGrid _kinectGrid;
	protected int _lastPlayerTime = 0;
	
	protected KinectLayer _kinectLayer;
//	protected PImage _gallery;
	protected ArrayList<PImage> _galleryImages;
	protected ArrayList<String> _galleryArtists;
	protected int _galleryIndex;
//	protected CustomFontText2D _galleryLabel;
	protected int _labelWidth;
		
	protected float SCALE_DOWN = 0.65f;
	protected float BLOB_DETECT_SCALE = 0.6f;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "1280" );
		Config.setProperty( AppSettings.HEIGHT, "720" );
//		Config.setProperty( AppSettings.WIDTH, "960" );
//		Config.setProperty( AppSettings.HEIGHT, "540" );
		Config.setProperty( AppSettings.FILLS_SCREEN, "true" );
		Config.setProperty( AppSettings.FULLSCREEN, "true" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( "force_foreground", "false" );

		Config.setProperty( "kinect_close", "500" );
		Config.setProperty( "kinect_far", "1300" );
		Config.setProperty( "kinect_pixel_skip", "5" );
		Config.setProperty( "kinect_mirrored", "true" );		
	}
	
	public void firstFrame() {
		DepthCamera.instance(DepthCameraType.KinectV1);
		buildCanvas();
		loadGalleryImages();
		_kinectLayer = new KinectLayer();
		_kinectGrid = new KinectRegionGrid(1, 1, Config.getInt("kinect_close", 0), Config.getInt("kinect_far", 0), 0, 0, 480, 20, 10);
		buildText();
		newImageForNewPlayers();
	}
	
	public void loadGalleryImages() {
		_galleryImages = new ArrayList<PImage>();
		_galleryArtists = new ArrayList<String>();
		
		String imgBase = "images/ello-art/";
		
		ArrayList<String> files = FileUtil.getFilesInDirOfType( FileUtil.getHaxademicDataPath() + imgBase, "jpg" );
		// files.addAll( FileUtil.getFilesInDirOfType( FileUtil.getHaxademicDataPath() + imgBase, "png" ) );
		FileUtil.shuffleFileList( files );
		
		for( int i=0; i < files.size(); i++ ) {
			_galleryImages.add( P.p.loadImage( FileUtil.getFile( imgBase + files.get(i) ) ) );
			_galleryArtists.add(files.get(i).replaceAll(".jpg", ""));
		}
		_galleryIndex = 0;
	}
	
	public void drawApp() {
		background(0);
		newImageForNewPlayers();
		_kinectLayer.updateWithImage(_galleryImages.get(_galleryIndex));
		p.image(_kinectLayer.canvas(), 0, 0, p.width, p.height);
		drawArtistText();
//		p.image(_kinectLayer.mask(), 0, 0, p.width, p.height);
//		postProcessEffects();
	}

	protected void drawArtistText() {
		PG.resetPImageAlpha(p);
//		PImage img = _galleryLabel.getTextPImage();
//		p.fill(0);
//		p.rect(p.width - 20 - _labelWidth, p.height - 20 - img.height, _labelWidth, img.height);
//		p.image(img, p.width - 39 - img.width, p.height - 19 - img.height);
	}
	
	protected void newImageForNewPlayers() {
		_kinectGrid.update();
		if(_kinectGrid.getRegion(0).isActive() == true) {
			_lastPlayerTime = p.frameCount;
		} else {
			if(p.frameCount - _lastPlayerTime == 150) {
				// next image
				_galleryIndex++;
				if(_galleryIndex > _galleryImages.size()-1) _galleryIndex = 0;
//				_galleryLabel.updateText("ello.co/"+_galleryArtists.get(_galleryIndex));
//				_labelWidth = _galleryLabel.getTextPImage().width + 40 - _galleryLabel.getLeftmostPixel();
			}
		}
	}
	
	// Video player ================ 
	protected void buildText() {
//		_galleryLabel = new CustomFontText2D( this, FileUtil.getFile("fonts/AtlasTypewriter-Regular-Web.ttf"), 18, p.color(255), CustomFontText2D.ALIGN_RIGHT, 500, 46 );
	}
	
	protected void buildCanvas() {
		// build movie players & composite
		_movieComposite = p.createGraphics(P.round(p.width * SCALE_DOWN), P.round(p.height * SCALE_DOWN), P.P3D);
	}
	
	
	public void keyPressed() {
		super.keyPressed();
		
		if( p.key == 'd' ){
			_isDebug = !_isDebug;
		}
		if( p.key == 'n' ){
			_galleryIndex++;
			if(_galleryIndex > _galleryImages.size()-1) _galleryIndex = 0;
		}
	}
	
	
	
	
	
	public class KinectLayer {

		public KinectSilhouetteVectorField _silhouette;
		protected float[] _cropProps = null;
		protected PGraphics _particleMask;
		protected PGraphics _galleryImg;

		public KinectLayer() {
			_silhouette = new KinectSilhouetteVectorField(false, true);
			initImageBuffer();
		}
		
		protected void initImageBuffer() {
			_particleMask = p.createGraphics(_movieComposite.width, _movieComposite.height, P.P3D);
			_particleMask.smooth(OpenGLUtil.SMOOTH_LOW);
			_galleryImg = p.createGraphics(_movieComposite.width, _movieComposite.height, P.P3D);
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
		
		public void updateWithImage(PImage img) {
			// copy current gallery image to canvas
			_galleryImg.beginDraw();
			float[] galleryCrop = ImageUtil.getOffsetAndSizeToCrop(p.width * SCALE_DOWN, p.height * SCALE_DOWN, img.width, img.height, true);
			_galleryImg.image(img, galleryCrop[0], galleryCrop[1], galleryCrop[2], galleryCrop[3]);
			_galleryImg.endDraw();

			// kinect silhouette 
			if(_cropProps == null) {
				if(_silhouette._canvas.width != 0 && _silhouette._canvas.height != 0) {
					_cropProps = ImageUtil.getOffsetAndSizeToCrop(p.width * SCALE_DOWN, p.height * SCALE_DOWN, _silhouette._canvas.width, _silhouette._canvas.height, true).clone();
				}
			}
			_silhouette.update(false);
			
			// draw kinect mask to canvas
			_particleMask.beginDraw();
			if(_silhouette._canvas != null) {
				_particleMask.image(_silhouette._canvas, _cropProps[0], _cropProps[1], _cropProps[2], _cropProps[3]);
			}
			_particleMask.endDraw();
			
			// apply mask to gallery image
			_galleryImg.mask(_particleMask);
		}
	}
}