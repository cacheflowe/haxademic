package com.haxademic.demo.draw.cv;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.DemoAssets;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

public class Demo_BlobDetection_perfTest 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	Movie video;
	BlobDetection theBlobDetection;
	PGraphics blobBufferGraphics;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640);
		Config.setProperty( AppSettings.HEIGHT, 640);
	}

	protected void firstFrame() {
		initMovie();
		initBlobDetection();
	}

	protected void drawApp() {
		background(0);
		runBlobDetection(video);
		drawEdges();

		p.image(pg, 0, 0);		
		p.image(blobBufferGraphics, 0, 0);
	}
	
	protected void initMovie() {
		video = DemoAssets.movieFractalCube();
		video.play();
		video.loop();
		video.jump(0);
	}
	
	protected void initBlobDetection() {
		// build scaled-down canvas
		float scaleDownForBlobDetect = 0.2f;
		int blurImgW = (int)(p.width * scaleDownForBlobDetect);
		int blurImgH = (int)(p.width * scaleDownForBlobDetect);
		blobBufferGraphics = P.p.createGraphics(blurImgW, blurImgH, P.P2D);
		
		// init blob obj
		theBlobDetection = new BlobDetection(blobBufferGraphics.width, blobBufferGraphics.height);
		theBlobDetection.setPosDiscrimination(true);	// true if looking for bright areas
		theBlobDetection.setThreshold(0.5f); 			// will detect bright areas whose luminosity > threshold
	}

	protected void runBlobDetection( PImage source ) {
		// copy & blur source image
		ImageUtil.copyImage(source, blobBufferGraphics);
		float shaderBlurAmount = 1f;
		BlurHFilter.instance(P.p).setBlurByPercent(shaderBlurAmount, blobBufferGraphics.width);
		BlurVFilter.instance(P.p).setBlurByPercent(shaderBlurAmount, blobBufferGraphics.width);
		for (int i = 0; i < 2; i++) {
			BlurHFilter.instance(P.p).applyTo(blobBufferGraphics);
			BlurVFilter.instance(P.p).applyTo(blobBufferGraphics);
		}
		
		// send pixels data to algorithm
		blobBufferGraphics.loadPixels();
		theBlobDetection.computeBlobs(blobBufferGraphics.pixels);
	}

	protected void drawEdges() {
		// set blob output context
		pg.beginDraw();
		pg.background(0);
		pg.stroke(255);
		pg.fill(127);

		// do edge detection
		Blob b;
		EdgeVertex eA,eB;
		for (int n=0 ; n < theBlobDetection.getBlobNb() ; n++) {
			b = theBlobDetection.getBlob(n);
			if ( b != null ) {
				pg.beginShape();
				for (int m = 0; m < b.getEdgeNb(); m++) {
					eA = b.getEdgeVertexA(m);
					eB = b.getEdgeVertexB(m);	// should store these too?
					if (eA !=null && eB !=null) {
						pg.vertex( eA.x * pg.width, eA.y * pg.height );
						pg.vertex( eB.x * pg.width, eB.y * pg.height );
//						_canvas.line(eA.x * _canvasW, eA.y * pg.height, eB.x * _canvasW, eB.y * pg.height);
					}
				}
				// connect last vertex to first
				eA = b.getEdgeVertexA(0);
				eB = b.getEdgeVertexB(0);
				pg.vertex( eA.x * pg.width, eA.y * pg.height );
				pg.vertex( eB.x * pg.width, eB.y * pg.height );

				// complete shape
				pg.endShape();
			}
		}
		pg.endDraw();
	}
	
	public void keyPressed() {
		super.keyPressed();
	}

}


