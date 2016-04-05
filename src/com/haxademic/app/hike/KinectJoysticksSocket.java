package com.haxademic.app.hike;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.kinect.KinectRegionGrid;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.image.filters.shaders.BlurHFilter;
import com.haxademic.core.image.filters.shaders.BlurVFilter;
import com.haxademic.core.net.WebSocketRelay;
import com.haxademic.core.system.FileUtil;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.data.JSONObject;
import processing.opengl.PShader;
import processing.video.Movie;


public class KinectJoysticksSocket 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectRegionGrid _kinectGrid;
	protected boolean _userActive = false;

	protected KinectBlob _kinectBlob;
	
	protected WebSocketRelay _server;
	protected int _userFoundTime = -1;
	
	
	
	protected Movie _movie;
	protected Movie _movieColor;
	protected float[] _cropProps = null;

	PGraphics _maskImage;
	PShader _maskShader;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		p.appConfig.setProperty( AppSettings.WIDTH, "1920" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "1080" );
	}
	
	public void setup() {
		super.setup();
		
		// debug display
		_kinectGrid = new KinectRegionGrid(3, 3, 1000, 2000, 40, 0, 480, 20, 10, true);
		// no debug display - control only
		// _kinectGrid = new KinectRegionGrid(2, 2, 1000, 2000, 40, 0, 480, 20, 10);
		
		// kinect blob for silhouette
		_kinectBlob = new KinectBlob(4, 0, 640, 0, 480, 500, 2000);
		
		// fire up websocket server
		_server = new WebSocketRelay();
		_server.start();
		
		// build movie layer		
		_movie = new Movie( p, FileUtil.getFile("video/nike/nike-hike-gray-loop.mov") );
		_movieColor = new Movie( p, FileUtil.getFile("video/nike/nike-hike-color-loop.mov") );

		_movie.jump(0);
		_movie.loop();
		_movie.play();

		_movieColor.jump(0);
		_movieColor.loop();
		_movieColor.play();

//		_maskImage = createGraphics(p.width, p.height, P2D);
//		_maskImage.noSmooth();

		_maskShader = loadShader(FileUtil.getFile("shaders/filters//mask.glsl"));
//		_maskShader.set("mask", _maskImage);

		
	}

	public void drawApp() {
		// reset drawing 
		p.background(0);
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);
		p.noStroke();
		
//		// debug draw webcam
//		DrawUtil.setPImageAlpha(p, 0.5f);
//		p.image( p.kinectWrapper.getRgbImage(), 0, 0);
		
		// update Kinect data
		_kinectGrid.update();
		
		// update kinect blob
		_kinectBlob.update();
		p.image(_kinectBlob._canvas, 0, 0);
		
		
		
		// draw movie
		// update mask
//		_maskImage.beginDraw();
//		_maskImage.background(0);
//		if (mouseX != 0 && mouseY != 0) {  
//			_maskImage.noStroke();
//			_maskImage.fill(255, 0, 0);
//			_maskImage.ellipse(mouseX, mouseY, 450, 450);
//		}
//		_maskImage.endDraw();
		_maskShader.set("mask", _kinectBlob._canvas);


		// draw movie
		if(_movie.width > 1 && _movieColor.width > 1) {
			_cropProps = ImageUtil.getOffsetAndSizeToCrop(p.width, p.height, _movie.width, _movie.height, true);
			DrawUtil.setPImageAlpha(p, 1);
			p.image(_movie, _cropProps[0], _cropProps[1], _cropProps[2], _cropProps[3]);
			p.shader(_maskShader);
			p.image(_movieColor, _cropProps[0], _cropProps[1], _cropProps[2], _cropProps[3]);
			p.resetShader();
		}

		

		
		
		
		// draw kinect debug
		DrawUtil.setPImageAlpha(p, 1f);
		p.pushMatrix();
		p.scale(0.4f);
		_kinectGrid.drawDebug(p.g);
		p.popMatrix();
		
		// detect user
		if(_kinectGrid.getRegion(4).isActive() == true) {
			p.fill(0,255,0);
			p.rect(p.width - 120, 20, 100, 100, 50);
			if(_userActive == false) {
				_userActive = true;
				sendSocketMessage("user", "active");
				_userFoundTime = p.millis();
				
				_movie.jump(0);
				_movie.play();
			}
		} else {
			p.fill(255,0,0);
			p.rect(p.width - 120, 20, 100, 100, 50);
			if(_userActive == true) {
				_userActive = false;
				sendSocketMessage("user", "inactive");
				_userFoundTime = -1;
			}
		}
		
		// detect user active for 3 secs
		if(_userFoundTime != -1) {
			if(p.millis() > _userFoundTime + 3000) {
				sendSocketMessage("capture", "take some pictures!");
				_userFoundTime = -1;
			}
		}
		
	}
	
	public void sendSocketMessage(String type, String message) {
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setString("type", type);
	    jsonOut.setString("text", message);
	    _server.sendMessage(jsonOut.toString().replaceAll("[\r\n]+", " ").replaceAll("\\s+", " "));
	}
	
	public void keyPressed() {
		super.keyPressed();
		if (p.key == ' ') {
			_kinectGrid.toggleDebugOverhead();
		}
	}

	
	
	
	
	
	
	
	public class KinectBlob {
		protected int PIXEL_SIZE = 20;
		protected int KINECT_NEAR = 500;
		protected int KINECT_FAR = 2000;
		protected int KINECT_TOP_PIXEL = 0;
		protected int KINECT_BOTTOM_PIXEL = 480;
		protected int KINECT_LEFT_PIXEL = 0;
		protected int KINECT_RIGHT_PIXEL = 640;
		protected int KINECT_BUFFER_FRAMES = 3;
		protected int DEPTH_KEY_DIST = 400;

		public PGraphics _kinectPixelated;
		
		protected int _framesToScan = 300;
		
		BlobDetection theBlobDetection;
//		public PGraphics blobBufferGraphics;

		protected float _canvasW = 640;
		protected float _canvasH = 480;
		public PGraphics _canvas;

		public KinectBlob(int pixelSize, int leftPixel, int rightPixel, int topPixel, int bottomPixel, int nearDistance, int farDistance) {
			PIXEL_SIZE = pixelSize;
			KINECT_LEFT_PIXEL = leftPixel;
			KINECT_RIGHT_PIXEL = rightPixel;
			KINECT_TOP_PIXEL = topPixel;
			KINECT_BOTTOM_PIXEL = bottomPixel;
			KINECT_NEAR = nearDistance;
			KINECT_FAR = farDistance;
	
			// canvas to draw kinect pixels to. also used for blob detection
			_kinectPixelated = P.p.createGraphics( Math.round((KINECT_RIGHT_PIXEL - KINECT_LEFT_PIXEL)/PIXEL_SIZE), Math.round((KINECT_BOTTOM_PIXEL - KINECT_TOP_PIXEL)/PIXEL_SIZE), P.P3D );
			_kinectPixelated.noSmooth();
			
			initBlobDetection();
			
			_canvas = P.p.createGraphics( KINECT_RIGHT_PIXEL - KINECT_LEFT_PIXEL, KINECT_BOTTOM_PIXEL - KINECT_TOP_PIXEL, P.P3D );
			_canvas.noSmooth();
//			_canvas.smooth(OpenGLUtil.SMOOTH_LOW);
//			_canvas.smooth();
			_canvasW = _canvas.width;
			_canvasH = _canvas.height;

		}
		
		protected void initBlobDetection() {
			theBlobDetection = new BlobDetection( _kinectPixelated.width, _kinectPixelated.height );
			theBlobDetection.setPosDiscrimination(true);	// true if looking for bright areas
			theBlobDetection.setThreshold(0.3f); // will detect bright areas whose luminosity > threshold
		}

		protected void runBlobDetection( PGraphics source ) {
			BlurHFilter.instance(p).setBlurByPercent(0.2f, source.width);
			BlurHFilter.instance(p).applyTo(source);
			BlurVFilter.instance(p).setBlurByPercent(0.2f, source.height);
			BlurVFilter.instance(p).applyTo(source);
			theBlobDetection.computeBlobs(source.get().pixels);
		}


		public void update() {
			drawKinectForBlob();
			runBlobDetection( _kinectPixelated );
			drawBlobs();
		}
		
		protected void drawKinectForBlob() {
			// loop through kinect data within player's control range
			int pixelsDrawn = 0;
			_kinectPixelated.beginDraw();
			_kinectPixelated.clear();
			_kinectPixelated.background(0);
			_kinectPixelated.noStroke();
//			_kinectPixelated.noSmooth();
			_kinectPixelated.fill(255f);
			
			float pixelDepth = 0;
			int kinectBlobPadding = PIXEL_SIZE * 3;
			// leave edges blank to get solid blobs that don't go off-screen!
			for ( int x = KINECT_LEFT_PIXEL + kinectBlobPadding; x < KINECT_RIGHT_PIXEL - kinectBlobPadding; x += PIXEL_SIZE ) {
				for ( int y = KINECT_TOP_PIXEL + kinectBlobPadding; y < KINECT_BOTTOM_PIXEL - kinectBlobPadding; y += PIXEL_SIZE ) {
					pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
					if(pixelDepth > KINECT_NEAR && pixelDepth < KINECT_FAR) {
						_kinectPixelated.rect(Math.round((x - KINECT_LEFT_PIXEL)/PIXEL_SIZE), Math.round((y - KINECT_TOP_PIXEL)/PIXEL_SIZE), 1, 1);
						pixelsDrawn++;
					}
				}
			}
			_kinectPixelated.endDraw();
		}
		
		protected void drawBlobs() {
			_canvas.beginDraw();
			_canvas.clear();		
			_canvas.background(0);			
			_canvas.noStroke();
			_canvas.fill(255);
			
			// do edge detection
			Blob b;
			for (int n=0 ; n < theBlobDetection.getBlobNb() ; n++) {
				b = theBlobDetection.getBlob(n);
				if ( b != null ) processBlob(b);
			}
			
			_canvas.endDraw();
		}
		
		protected void processBlob(Blob b) {
			int numEdges = 0;
			_canvas.beginShape();
			for (int m = 0; m < b.getEdgeNb(); m++) {
				if(m % 3 == 0) { // only draw every other segment
					drawEdgeSegmentAtIndex(b, m);
					numEdges++;
				}
			}
			// connect last vertex to first
			drawEdgeSegmentAtIndex(b, 0);
			
			// complete shape
			_canvas.endShape();
//			if(DEBUG_OUTPUT) P.println("drawEdges",numEdges);
		}
		
		protected void drawEdgeSegmentAtIndex(Blob b, int i) {
			EdgeVertex eA,eB;
			eA = b.getEdgeVertexA(i);
			eB = b.getEdgeVertexB(i);
			if (eA !=null && eB !=null) {
				drawEdgeVertex( _canvas, eA.x * _canvasW, eA.y * _canvasH, b.x * _canvasW, b.y * _canvasH);
				drawEdgeVertex( _canvas, eB.x * _canvasW, eB.y * _canvasH, b.x * _canvasW, b.y * _canvasH);
			}
		}
		
		
		protected void drawEdgeVertex(PGraphics canvas, float vertexX, float vertexY, float blobX, float blobY) {
			canvas.vertex( vertexX, vertexY );
		}
	}
}

