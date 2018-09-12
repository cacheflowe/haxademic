package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.filters.pgraphics.FastBlurFilter;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.file.DemoAssets;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

public class BlobPerfTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	Movie _movie;
	
	BlobDetection theBlobDetection;
	PImage blobBufferImg;
	PGraphics blobBufferGraphics;
	boolean _usingPimg = true;

	protected int _canvasW;
	protected int _canvasH;
	public PGraphics _canvas;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "640" );
	}


	public void setup() {
		super.setup();	

		_canvasW = p.width;
		_canvasH = p.height;
		_canvas = P.p.createGraphics(_canvasW, _canvasH, P.P2D);
		
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
		
		if(_usingPimg == true) {
			p.image(blobBufferImg, 0, 0);
		} else {
			p.image(blobBufferGraphics, 0, 0);
		}
	}
	
	protected void initMovie() {
		_movie = DemoAssets.movieFractalCube();
		_movie.play();
		_movie.loop();
		_movie.jump(0);
	}
	
	protected void initBlobDetection() {
		float scaleDownForBlobDetect = 0.2f;
		int blurImgW = (int)(_canvasW * scaleDownForBlobDetect);
		int blurImgH = (int)(_canvasH * scaleDownForBlobDetect);
		
		blobBufferImg = new PImage(blurImgW, blurImgH); 
		blobBufferGraphics = P.p.createGraphics(blurImgW, blurImgH, P.P2D);
		
		float shaderBlurAmount = 1f;
		BlurHFilter.instance(P.p).setBlurByPercent(shaderBlurAmount, blobBufferGraphics.width);
		BlurVFilter.instance(P.p).setBlurByPercent(shaderBlurAmount, blobBufferGraphics.width);
		BlurProcessingFilter.instance(P.p).setSigma(10f);
		BlurProcessingFilter.instance(P.p).setBlurSize(10);

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
//			blobBufferGraphics.beginDraw();
			blobBufferGraphics.copy(source, 0, 0, source.width, source.height, 0, 0, blobBufferGraphics.width, blobBufferGraphics.height);
//			blobBufferGraphics.endDraw();
//			blobBufferImg.copy(source, 0, 0, source.width, source.height, 0, 0, blobBufferImg.width, blobBufferImg.height);

//			BlurProcessingFilter.instance(P.p).applyTo(blobBufferGraphics);
			BlurHFilter.instance(P.p).applyTo(blobBufferGraphics);
			BlurVFilter.instance(P.p).applyTo(blobBufferGraphics);
//			blobBufferGraphics.filter(_blurH);
//			blobBufferGraphics.filter(_blurV);
//			theBlobDetection.computeBlobs(blobBufferImg.pixels);
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
	
	public void keyPressed() {
		super.keyPressed();
		if( p.key == 'd' ){
			_usingPimg = !_usingPimg;
		}
	}

}


