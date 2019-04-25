package com.haxademic.app.kinectvideoplayer;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.hardware.kinect.KinectSize;
import com.haxademic.core.math.MathUtil;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class KinectSilhouettePG {
	
	protected int PIXEL_SIZE = 5;
	protected final int KINECT_CLOSE = 500;
	protected final int KINECT_FAR = 3000;
	
	protected PGraphics _kinectPixelated;
	protected Vector<FloatParticle> _particles;
	protected Vector<FloatParticle> _inactiveParticles;
	
	protected PGraphics _silhouette;
	
	BlobDetection theBlobDetection;
	PImage blobBufferImg;

	protected ArrayList<PVector> _curEdgeList;
	protected ArrayList<PVector> _lastEdgeList;
	
	protected float _canvasW = 640;
	protected float _canvasH = 480;
	public PGraphics _canvas;
	
	
	ArrayList<PolygonPoint> points = new ArrayList<PolygonPoint>();
	List<DelaunayTriangle> triangles;


	public KinectSilhouettePG() {
	
		initBlobDetection();

		_kinectPixelated = P.p.createGraphics( KinectSize.WIDTH, KinectSize.HEIGHT, P.P3D );
		_canvas = P.p.createGraphics( KinectSize.WIDTH, KinectSize.HEIGHT, P.P3D );
		
		_particles = new Vector<FloatParticle>();
		_inactiveParticles = new Vector<FloatParticle>();

		
		_curEdgeList = new ArrayList<PVector>();
		_lastEdgeList = new ArrayList<PVector>();
	}
	
	protected void initBlobDetection() {
		_silhouette = P.p.createGraphics( (int)_canvasW, (int)_canvasH, P.P3D );
		
		// BlobDetection
		// img which will be sent to detection (a smaller copy of the image frame);
		blobBufferImg = new PImage( (int)(_canvasW * 0.2f), (int)(_canvasH * 0.2f) ); 
		theBlobDetection = new BlobDetection( blobBufferImg.width, blobBufferImg.height );
		theBlobDetection.setPosDiscrimination(true);	// true if looking for bright areas
		theBlobDetection.setThreshold(0.5f); // will detect bright areas whose luminosity > threshold
	}

	public void update() {
		_canvas.beginDraw();
		_canvas.clear();
		_canvas.background(0);
		drawKinect();
		runBlobDetection( _kinectPixelated );
		getEdges();
		drawEdges();
		updateParticles();
		_canvas.endDraw();
	}
	
	protected void drawKinect() {
		// loop through kinect data within player's control range
		_kinectPixelated.beginDraw();
		_kinectPixelated.clear();
		_kinectPixelated.noStroke();
		float pixelDepth;
		for ( int x = 0; x < KinectSize.WIDTH; x += PIXEL_SIZE ) {
			for ( int y = 0; y < KinectSize.HEIGHT; y += PIXEL_SIZE ) {
				pixelDepth = P.p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
//					_kinectPixelated.fill(((pixelDepth - KINECT_CLOSE) / (KINECT_FAR - KINECT_CLOSE)) * 255f);
					_kinectPixelated.fill(255f);
					_kinectPixelated.rect(x, y, PIXEL_SIZE, PIXEL_SIZE);
				}
			}
		}
		_kinectPixelated.endDraw();
	}
	
	protected void runBlobDetection( PImage source ) {
		blobBufferImg.copy(source, 0, 0, source.width, source.height, 0, 0, blobBufferImg.width, blobBufferImg.height);
//		FastBlurFilter.blur(blobBufferImg, 3);
		theBlobDetection.computeBlobs(blobBufferImg.pixels);
	}

	protected void drawEdges() {
		_silhouette.beginDraw();
		_silhouette.clear();
		_silhouette.smooth(OpenGLUtil.SMOOTH_LOW); 
		_silhouette.stroke(255);
		_silhouette.fill(255);
		
		
		if (_curEdgeList != null && _lastEdgeList != null && _curEdgeList.size() > 0 && _lastEdgeList.size() > 0) {
			
			PVector curEdgeVector;
			
			// draw blob
			_canvas.beginShape();
			int numBlobs = _curEdgeList.size();
			for(int i=0; i < numBlobs; i++) {
				curEdgeVector = _curEdgeList.get(i);
				float newX = curEdgeVector.x * _canvasW; 
				float newY = curEdgeVector.y * _canvasH; 
				_canvas.vertex( newX, newY );
			}
			_canvas.endShape();
			
			// find closest point from each current point to last points
			for(int i=0; i < _curEdgeList.size(); i++) {
				
				curEdgeVector = _curEdgeList.get(i);
				
				float dist = 0f;
				float minDist = 100f;
				int closestLastIndex = -1;
				
				float oldX, newX, oldY, newY;
				
				// int plusFrame = (p.frameCount % 2 == 0) ? 0 : 1;	// swap between even and odd vertices each frame

				// find closest last vertex
				for(int j=0; j < _lastEdgeList.size(); j++) {
					dist = MathUtil.getDistance(curEdgeVector.x, curEdgeVector.y, _lastEdgeList.get(j).x, _lastEdgeList.get(j).y);
					if(dist < minDist) {
						minDist = dist;
						closestLastIndex = j;
					}
				}
				
				// upon finding closest last, draw a line for now, if close enough
				if (minDist < 0.2f && i % 4 == 0) {
					
					newX = curEdgeVector.x * _canvasW; 
					newY = curEdgeVector.y * _canvasH; 
					oldX = _lastEdgeList.get(closestLastIndex).x * _canvasW; 
					oldY = _lastEdgeList.get(closestLastIndex).y * _canvasH; 
					
					_silhouette.line(newX, newY, oldX, oldY);
//					_silhouette.ellipse( newX, newY, 6, 6 );

//					float angle = -MathUtil.getAngleToTarget(curEdgeVector.x, curEdgeVector.y, _lastEdgeList.get(closestLastIndex).x, _lastEdgeList.get(closestLastIndex).y);
					
//					float outerX = curEdgeVector.x + P.sin( MathUtil.degreesToRadians(angle) ) * minDist;
//					float outerY = curEdgeVector.y + P.cos( MathUtil.degreesToRadians(angle) ) * minDist;

					newParticle( 
							newX, 
							newY, 
							(newX - oldX) * 0.05f, 
							(newY - oldY) * 0.05f, 
							255 
					);


					
					// draw 'mesh' between every few points
					if( i > 20 ) {
						PVector lastFewEdgeVector = _curEdgeList.get(i-20);
						if ( MathUtil.getDistance(curEdgeVector.x, curEdgeVector.y, lastFewEdgeVector.x, lastFewEdgeVector.y) < 0.2f ) {
							_silhouette.stroke(255);
							_silhouette.line(curEdgeVector.x * _canvasW, curEdgeVector.y * _canvasH, lastFewEdgeVector.x * _canvasW, lastFewEdgeVector.y * _canvasH);
						}
					}
					
				}
				

			}
			
		}
				
		_silhouette.endDraw();
	}
	
	protected void getEdges() {
		
		_lastEdgeList.clear();
		_lastEdgeList = _curEdgeList;
		_curEdgeList = new ArrayList<PVector>();
		
		_canvas.stroke(255);
		_canvas.fill(255);

		// do edge detection
		Blob b;
		EdgeVertex eA,eB;
		for (int n=0 ; n < theBlobDetection.getBlobNb() ; n++) {
			b = theBlobDetection.getBlob(n);
			if ( b!= null ) {
				for (int m=0;m<b.getEdgeNb();m++) {
					eA = b.getEdgeVertexA(m);
					eB = b.getEdgeVertexB(m);	// should store these too?
					
					if (eA !=null && eB !=null) {
						_curEdgeList.add( new PVector(eA.x, eA.y) );
					}
				}
				
//				drawTrianglesForBlob(b);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected void updateParticles() {
		// update particles
		FloatParticle particle;
		int particlesLen = _particles.size() - 1;
		for( int i = particlesLen; i > 0; i-- ) {
			particle = _particles.get(i);
			if( particle.active == true ) {
				particle.update();
			} else {
				_particles.remove(i);
				_inactiveParticles.add(particle);
			}
		}

	}
	
	
	protected void newParticle( float x, float y, float speedX, float speedY, int color ) {
		FloatParticle particle;
		if( _inactiveParticles.size() > 0 ) {
			particle = _inactiveParticles.remove( _inactiveParticles.size() - 1 );
		} else {
			particle = new FloatParticle();
		}
		particle.startAt( x, y, speedX, speedY, color );
		_particles.add( particle );
	}

	
	public class FloatParticle {
		
		PVector _position = new PVector();
		PVector _speed = new PVector();
		PVector _gravity = new PVector(0,0.31f);
		int _color = 0;
		float _size = 8;
		float _opacity = 1;
		float _opacityFadeSpeed = 0.03f;
		public boolean active = false;
		protected int _audioIndex;
		
		public FloatParticle() {
			
		}
		
		public void startAt( float x, float y, float speedX, float speedY, int color ) {
			_position.set( x, y );
			_speed.set( speedX * 15f * P.p.random(2f) + P.p.random(-0.2f,0.2f), speedY * 5f * P.p.random(3f) );	// add a little extra x variance
//			_speed.mult( 1 + p._audioInput.getFFT().spectrum[_audioIndex] ); // speed multiplied by audio
			_color = color;
			_opacity = 0.8f;
//			_opacityFadeSpeed = p.random(400f, 800f) / 10000f; // 0.005 - 0.05
			active = true;
			_size = P.max(6,(P.abs(_speed.x) + P.abs(_speed.y))*0.3f);
			_audioIndex = MathUtil.randRange(0, 511);
		}
		
		public void update() {
			_position.add( _speed );
			_speed.add( _gravity );
			_speed.set( _speed.x * 0.98f, _speed.y );
			_size *= 0.98f;
			// bounce
			if (_position.y > _canvasH) {
				_position.sub( _speed );
				_speed.set( _speed.x, _speed.y * -0.4f );
			}
			
			_opacity -= _opacityFadeSpeed;
			if( _opacity <= 0 ) {
				active = false;
			} else {
				_canvas.fill( _color, 200f * _opacity );
				_canvas.noStroke();
				// float size = _size; // 1 + p._audioInput.getFFT().spectrum[_audioIndex] * 10; // was 3
				_canvas.rect( _position.x, _position.y, _size, _size );
			}
		}
	}
	
	
	
	
	
	public void drawTrianglesForBlob(Blob b) {
		// create points array, skipping over blob edge vertices
		points.clear();
		EdgeVertex eA;
		for (int m=0;m<b.getEdgeNb();m+=10) {
			eA = b.getEdgeVertexA(m);
			if (eA != null) {
				PolygonPoint point = new PolygonPoint(eA.x * _canvasW, eA.y * _canvasH);
				points.add(point);
			}
		}
		
		if (points.size() < 10) return;

		// calculate triangles
		doTriangulation();

		// draw it
		_canvas.stroke(0,255,0);
		_canvas.strokeWeight(2);
		_canvas.fill(255, 120);
		
		TriangulationPoint point1, point2, point3;
		for (DelaunayTriangle triangle : triangles) {
			point1 = triangle.points[0];
			point2 = triangle.points[1];
			point3 = triangle.points[2];
			if( point1.getYf() > 0 && point1.getYf() < _canvasH &&
				point2.getYf() > 0 && point2.getYf() < _canvasH &&
				point3.getYf() > 0 && point3.getYf() < _canvasH && 
				point1.getXf() > 0 && point1.getXf() < _canvasW &&
				point2.getXf() > 0 && point2.getXf() < _canvasW &&
				point3.getXf() > 0 && point3.getXf() < _canvasW) {
				
				_canvas.line( point1.getXf(), point1.getYf(), point2.getXf(), point2.getYf() );
				_canvas.line( point3.getXf(), point3.getYf(), point2.getXf(), point2.getYf() );
				_canvas.line( point1.getXf(), point1.getYf(), point3.getXf(), point3.getYf() );
				
				// sometimes fill triangles
				if (MathUtil.randBoolean(P.p) == true ) {
					_canvas.beginShape();
					_canvas.vertex( point1.getXf(), point1.getYf() );
					_canvas.vertex( point2.getXf(), point2.getYf() );
					_canvas.vertex( point3.getXf(), point3.getYf() );
					_canvas.endShape();
				}
			}
		}

		
	}
	
	void doTriangulation() {
		Polygon polygon = new Polygon(points);
//		polygon.
		try {
			Poly2Tri.triangulate(polygon);
			triangles = polygon.getTriangles();
	//		println("DT: " + triangles);
		} catch(NullPointerException e) {
			
		}
	}


}