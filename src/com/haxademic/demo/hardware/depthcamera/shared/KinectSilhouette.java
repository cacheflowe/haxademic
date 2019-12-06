package com.haxademic.demo.hardware.depthcamera.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pgraphics.archive.FastBlurFilter;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.math.MathUtil;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class KinectSilhouette
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
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
	
	
	ArrayList<PolygonPoint> points = new ArrayList<PolygonPoint>();
	List<DelaunayTriangle> triangles;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1024 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 768 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
//		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.REALSENSE_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.DEPTH_CAM_RGB_ACTIVE, false );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, "false" );
	}
	
	public void setupFirstFrame() {

		
		initBlobDetection();

		_kinectPixelated = createGraphics( DepthCameraSize.WIDTH, DepthCameraSize.WIDTH, P.P3D );
		
		_particles = new Vector<FloatParticle>();
		_inactiveParticles = new Vector<FloatParticle>();

		
		_curEdgeList = new ArrayList<PVector>();
		_lastEdgeList = new ArrayList<PVector>();
	}
	
	protected void initBlobDetection() {
		_silhouette = p.createGraphics( p.width, p.height, P.P3D );
		
		// BlobDetection
		// img which will be sent to detection (a smaller copy of the image frame);
		blobBufferImg = new PImage( (int)(p.width * 0.2f), (int)(p.height * 0.2f) ); 
		theBlobDetection = new BlobDetection( blobBufferImg.width, blobBufferImg.height );
		theBlobDetection.setPosDiscrimination(true);	// true if looking for bright areas
		theBlobDetection.setThreshold(0.5f); // will detect bright areas whose luminosity > threshold
	}

	public void drawApp() {
		p.background(0);
		drawKinect();
		runBlobDetection( _kinectPixelated );
		getEdges();
		drawEdges();
		updateParticles();
	}
	
	protected void drawKinect() {
		// loop through kinect data within player's control range
		_kinectPixelated.beginDraw();
		_kinectPixelated.clear();
		_kinectPixelated.noStroke();
		float pixelDepth;
		for ( int x = 0; x < DepthCameraSize.WIDTH; x += PIXEL_SIZE ) {
			for ( int y = 0; y < DepthCameraSize.HEIGHT; y += PIXEL_SIZE ) {
				pixelDepth = p.depthCamera.getDepthAt( x, y );
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
		FastBlurFilter.blur(blobBufferImg, 3);
		theBlobDetection.computeBlobs(blobBufferImg.pixels);
	}

	protected void drawEdges() {
		_silhouette.beginDraw();
		_silhouette.clear();
		_silhouette.smooth(OpenGLUtil.SMOOTH_LOW); 
		_silhouette.stroke(255);
		_silhouette.noFill();
		
		
		if (_curEdgeList != null && _lastEdgeList != null && _curEdgeList.size() > 0 && _lastEdgeList.size() > 0) {
			
			// find closest point from each current point to last points
			PVector curEdgeVector;
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
					
					newX = curEdgeVector.x * p.width; 
					newY = curEdgeVector.y * p.height; 
					oldX = _lastEdgeList.get(closestLastIndex).x * p.width; 
					oldY = _lastEdgeList.get(closestLastIndex).y * p.height; 
					
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
							_silhouette.line(curEdgeVector.x * p.width, curEdgeVector.y * p.height, lastFewEdgeVector.x * p.width, lastFewEdgeVector.y * p.height);
						}
					}
					
				}
				

			}
			
		}
				
		_silhouette.endDraw();
		p.image(_silhouette,0,0);
	}
	
	protected void getEdges() {
		
		_lastEdgeList.clear();
		_lastEdgeList = _curEdgeList;
		_curEdgeList = new ArrayList<PVector>();
		
		p.stroke(255);
		p.fill(255);

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
			_speed.set( speedX * 15f * p.random(2f) + p.random(-0.2f,0.2f), speedY * 5f * p.random(3f) );	// add a little extra x variance
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
			if (_position.y > p.height) {
				_position.sub( _speed );
				_speed.set( _speed.x, _speed.y * -0.4f );
			}
			
			_opacity -= _opacityFadeSpeed;
			if( _opacity <= 0 ) {
				active = false;
			} else {
				p.fill( _color, 200f * _opacity );
				p.noStroke();
				// float size = _size; // 1 + p._audioInput.getFFT().spectrum[_audioIndex] * 10; // was 3
				p.rect( _position.x, _position.y, _size, _size );
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
				PolygonPoint point = new PolygonPoint(eA.x * p.width, eA.y * p.height);
				points.add(point);
			}
		}
		
		if (points.size() < 10) return;

		// calculate triangles
		doTriangulation();

		// draw it
		p.stroke(0,255,0);
		p.strokeWeight(2);
		p.fill(255, 120);
		
		TriangulationPoint point1, point2, point3;
		for (DelaunayTriangle triangle : triangles) {
			point1 = triangle.points[0];
			point2 = triangle.points[1];
			point3 = triangle.points[2];
			if( point1.getYf() > 0 && point1.getYf() < p.height &&
				point2.getYf() > 0 && point2.getYf() < p.height &&
				point3.getYf() > 0 && point3.getYf() < p.height && 
				point1.getXf() > 0 && point1.getXf() < p.width &&
				point2.getXf() > 0 && point2.getXf() < p.width &&
				point3.getXf() > 0 && point3.getXf() < p.width) {
				
				p.line( point1.getXf(), point1.getYf(), point2.getXf(), point2.getYf() );
				p.line( point3.getXf(), point3.getYf(), point2.getXf(), point2.getYf() );
				p.line( point1.getXf(), point1.getYf(), point3.getXf(), point3.getYf() );
				
				// sometimes fill triangles
				if (MathUtil.randBoolean() == true ) {
					p.beginShape();
					p.vertex( point1.getXf(), point1.getYf() );
					p.vertex( point2.getXf(), point2.getYf() );
					p.vertex( point3.getXf(), point3.getYf() );
					p.endShape();
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