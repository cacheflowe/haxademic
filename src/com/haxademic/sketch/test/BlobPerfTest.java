package com.haxademic.sketch.test;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import processing.video.Movie;
import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.filters.FastBlurFilter;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class BlobPerfTest 
extends PAppletHax {
	
	Movie _movie;
	
	protected PShader _blurH;
	protected PShader _blurV;

	BlobDetection theBlobDetection;
	PImage blobBufferImg;
	PGraphics blobBufferGraphics;
	boolean _usingPimg = true;

	protected int _canvasW;
	protected int _canvasH;
	public PGraphics _canvas;


	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "640" );
	}


	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		_canvasW = p.width;
		_canvasH = p.height;
		_canvas = P.p.createGraphics(_canvasW, _canvasH, P.OPENGL);
		
		initMovie();
		initBlobDetection();
	}

	public void drawApp() {
		background(0);
		
//		drawVideo();
		runBlobDetection( _movie );
		drawEdges();

		p.image(_canvas, 0, 0);
//		p.image(_movie, 0, 0);
		
//		if(_usingPimg == true) {
//			p.image(blobBufferImg, 0, 0);
//		} else {
//			p.image(blobBufferGraphics, 0, 0);
//		}
	}
	
	protected void initMovie() {
		_movie = new Movie( p, FileUtil.getHaxademicDataPath() + "video/ello-fractal.mov" );
		_movie.play();
		_movie.loop();
		_movie.jump(0);
		_movie.speed(1.3f);
	}
	
	protected void initBlobDetection() {
		float scaleDownForBlobDetect = 0.7f;
		blobBufferImg = new PImage( (int)(_canvasW * scaleDownForBlobDetect), (int)(_canvasH * scaleDownForBlobDetect) ); 

		blobBufferGraphics = P.p.createGraphics( (int)(_canvasW * scaleDownForBlobDetect), (int)(_canvasH * scaleDownForBlobDetect), P.OPENGL);
		_blurV = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-vertical.glsl" );
		_blurV.set( "v", 3f/p.height );
		_blurH = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-horizontal.glsl" );
		_blurH.set( "h", 3f/p.width );

		theBlobDetection = new BlobDetection( blobBufferImg.width, blobBufferImg.height );
		theBlobDetection.setPosDiscrimination(true);	// true if looking for bright areas
		theBlobDetection.setThreshold(0.5f); // will detect bright areas whose luminosity > threshold
	}

	protected void runBlobDetection( PImage source ) {
		if(_usingPimg == true) {
			blobBufferImg.copy(source, 0, 0, source.width, source.height, 0, 0, blobBufferImg.width, blobBufferImg.height);
			FastBlurFilter.blur(blobBufferImg, 3);
			theBlobDetection.computeBlobs(blobBufferImg.pixels);
		} else {
			blobBufferGraphics.copy(source, 0, 0, source.width, source.height, 0, 0, blobBufferGraphics.width, blobBufferGraphics.height);
			blobBufferGraphics.filter(_blurH);
			blobBufferGraphics.filter(_blurV);
			theBlobDetection.computeBlobs(blobBufferGraphics.get().pixels);
		}
	}

	protected void drawEdges() {
		
		_canvas.beginDraw();
		_canvas.clear();
		_canvas.background(0);

		_canvas.stroke(255);
		_canvas.fill(255);

		// do edge detection
		Blob b;
		EdgeVertex eA,eB;
		for (int n=0 ; n < theBlobDetection.getBlobNb() ; n++) {
			b = theBlobDetection.getBlob(n);
			if ( b != null ) {
				_canvas.beginShape();
				for (int m = 0; m < b.getEdgeNb(); m++) {
					eA = b.getEdgeVertexA(m);
					eB = b.getEdgeVertexB(m);	// should store these too?
					if (eA !=null && eB !=null) {
						_canvas.vertex( eA.x * _canvasW, eA.y * _canvasH );
						_canvas.vertex( eB.x * _canvasW, eB.y * _canvasH );
//						_canvas.line(eA.x * _canvasW, eA.y * _canvasH, eB.x * _canvasW, eB.y * _canvasH);
					}
				}
				// connect last vertex to first
				eA = b.getEdgeVertexA(0);
				eB = b.getEdgeVertexB(0);
				_canvas.vertex( eA.x * _canvasW, eA.y * _canvasH );
				_canvas.vertex( eB.x * _canvasW, eB.y * _canvasH );

				// complete shape
				_canvas.endShape();
			}
		}
		_canvas.endDraw();
	}
	
	protected void handleInput( boolean isMidi ) {
		super.handleInput( isMidi );
		if( p.key == 'd' ){
			_usingPimg = !_usingPimg;
		}
	}

}


