package com.haxademic.core.hardware.kinect;


import processing.core.PGraphics;
import processing.core.PImage;
import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;

import com.haxademic.core.app.P;
import com.haxademic.core.image.filters.FastBlurFilter;

public class KinectSilhouetteBasic {
	
	protected int PIXEL_SIZE = 4;
	protected final int KINECT_CLOSE = 500;
	protected final int KINECT_FAR = 3000;
	
	public PGraphics _kinectPixelated;
	
	protected KinectBufferedData _kinectBuffer;
	
	BlobDetection theBlobDetection;
	PImage blobBufferImg;

	protected float _canvasW = 640;
	protected float _canvasH = 480;
	public PGraphics _canvas;
	
	
	public KinectSilhouetteBasic() {
		initBlobDetection();
		_kinectBuffer = new KinectBufferedData(PIXEL_SIZE, KINECT_CLOSE, KINECT_FAR, 5);
		_kinectPixelated = P.p.createGraphics( KinectSize.WIDTH, KinectSize.HEIGHT, P.OPENGL );
		_canvas = P.p.createGraphics( KinectSize.WIDTH, KinectSize.HEIGHT, P.OPENGL );
	}
	
	protected void initBlobDetection() {
		// BlobDetection
		// img which will be sent to detection (a smaller copy of the image frame);
		blobBufferImg = new PImage( (int)(_canvasW * 0.2f), (int)(_canvasH * 0.2f) ); 
		theBlobDetection = new BlobDetection( blobBufferImg.width, blobBufferImg.height );
		theBlobDetection.setPosDiscrimination(true);	// true if looking for bright areas
		theBlobDetection.setThreshold(0.5f); // will detect bright areas whose luminosity > threshold
	}

	public void update() {
		drawKinect();
		runBlobDetection( _kinectPixelated );
		drawEdges();
	}
	
	protected void drawKinect() {
		_kinectBuffer.update(P.p.kinectWrapper);
		// loop through kinect data within player's control range
		_kinectPixelated.beginDraw();
		_kinectPixelated.clear();
		_kinectPixelated.noStroke();
		float pixelDepth;
		// leave edges blank to get solid blobs that don't go off-screen!
		for ( int x = PIXEL_SIZE * 3; x < KinectSize.WIDTH - PIXEL_SIZE * 4; x += PIXEL_SIZE ) {
			for ( int y = PIXEL_SIZE * 3; y < KinectSize.HEIGHT - PIXEL_SIZE * 4; y += PIXEL_SIZE ) {
				pixelDepth = _kinectBuffer.getBufferedDepthForKinectPixel( x, y );
//				pixelDepth = P.p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
					_kinectPixelated.fill(255f);
					_kinectPixelated.rect(x, y, PIXEL_SIZE, PIXEL_SIZE);
				}
			}
		}
		_kinectPixelated.endDraw();
	}
	
	protected void runBlobDetection( PImage source ) {
		blobBufferImg.copy(source, 0, 0, source.width, source.height, 0, 0, blobBufferImg.width, blobBufferImg.height);
		FastBlurFilter.blur(blobBufferImg, 3);
		theBlobDetection.computeBlobs(blobBufferImg.pixels);
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
	
	
}