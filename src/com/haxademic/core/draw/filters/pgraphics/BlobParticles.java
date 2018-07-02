
package com.haxademic.core.draw.filters.pgraphics;

import java.util.Vector;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PShader;

public class BlobParticles {
	
	protected PAppletHax p;
	protected int _width;
	protected int _height;
	protected PGraphics _pg;
	protected PImage _image;
	protected PImage _source;
	BlobDetection theBlobDetection;
	PImage blobBufferImg;
	PShader _desaturate;

	protected Vector<BlobParticle> _particles;
	protected Vector<BlobParticle> _inactiveParticles;
	
	public BlobParticles( int width, int height ) {
		p = (PAppletHax) P.p;
		_width = width;
		_height = height;
		initBlobDetection();
		_particles = new Vector<BlobParticle>();
		_inactiveParticles = new Vector<BlobParticle>();
		
		_desaturate = p.loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/saturation.glsl" );
		_desaturate.set("saturation", 0.75f);

	}
	
	public PImage pg() {
		return _pg;
	}
	
	protected void initBlobDetection() {
		_pg = p.createGraphics( _width, _height, P.P3D );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH); 
		_image = p.createImage( _width, _height, P.ARGB );
		
		// BlobDetection
		// img which will be sent to detection (a smaller copy of the image frame);
		blobBufferImg = new PImage( (int)(_width * 0.15f), (int)(_height * 0.15f) ); 
		theBlobDetection = new BlobDetection( blobBufferImg.width, blobBufferImg.height );
		theBlobDetection.setPosDiscrimination(false);	// true if looking for bright areas
		theBlobDetection.setThreshold(0.32f); // will detect bright areas whose luminosity > 0.2f;
	}
	
	public PImage updateWithPImage( PImage source ) {
		_source = source;
		runBlobDetection( source );
		drawEdges( source );
		
		// update particles
		BlobParticle particle;
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
		P.println("Active particles: "+_particles.size()+"  available: "+_inactiveParticles.size());
	
		return _image;
	}
	
	public void runParticlesFullImage() {
		for( int x=0; x < _source.width; x += 1 ) {
			for( int y=0; y < _source.height; y += 1 ) {
				int color = ImageUtil.getPixelColor( _source, x, y );
				if( color != ImageUtil.BLACK_INT && color != ImageUtil.CLEAR_INT && color != ImageUtil.EMPTY_INT ) {
					newParticle( x, y, p.random(-0.1f,0.1f), p.random(0,-0.5f), color );
				}
			}				
		}
	}
	
	// IMAGE PROCESSING METHODS ===================================================================================
	protected void runBlobDetection( PImage source ) {
		blobBufferImg.copy(source, 0, 0, source.width, source.height, 0, 0, blobBufferImg.width, blobBufferImg.height);
		FastBlurFilter.blur(blobBufferImg, 2);
		theBlobDetection.computeBlobs(blobBufferImg.pixels);
	}
	
	// test 2 - mesh from outer
	protected void drawEdges(PImage source)
	{
		_pg.beginDraw();
		ImageUtil.clearPGraphics( _pg );
		_pg.noStroke();
		_pg.fill(0,0);
		_pg.image( source, 0, 0 );
		
		// do edge detection
		Blob b;
		EdgeVertex eA,eB;
		for (int n=0 ; n<theBlobDetection.getBlobNb() ; n++) {
			b=theBlobDetection.getBlob(n);
			if (b!=null) {
				for (int m=0;m<b.getEdgeNb();m++) {
					eA = b.getEdgeVertexA(m);
					eB = b.getEdgeVertexB(m);
										
					if (eA !=null && eB !=null) {
						
						float angle = -MathUtil.getAngleToTarget(eA.x, eA.y, b.x, b.y);
						float angleB = -MathUtil.getAngleToTarget(eB.x, eB.y, b.x, b.y);
						float distance = MathUtil.getDistance(b.x, b.y, eA.x, eA.y) * 1f;
						float distanceB = MathUtil.getDistance(b.x, b.y, eB.x, eB.y) * 1f;

						float outerX = eA.x + P.sin( MathUtil.degreesToRadians(angle) )*distance;
						float outerY = eA.y + P.cos( MathUtil.degreesToRadians(angle) )*distance;
						float outerXB = eB.x + P.sin( MathUtil.degreesToRadians(angleB) )*distanceB;
						float outerYB = eB.y + P.cos( MathUtil.degreesToRadians(angleB) )*distanceB;

						// draw inner lines for debugging
//						_pg.beginDraw();
//						_pg.stroke(0,127);
//						_pg.line(eA.x*_width, eA.y*_height, eB.x*_width, eB.y*_height);
//						_pg.noStroke();
//						_pg.endDraw();

						
						int color = ImageUtil.getPixelColor( source, P.round(eA.x*source.width-1), P.round(eA.y*source.height-1) );
						_pg.fill( color, 127 );
						float bright = p.brightness(color);

						newParticle( 
								eA.x*_width + p.random(-3f,2f), 
								eA.y*_height + p.random(-1f,1f), 
								outerX - eA.x, 
								outerY - eA.y, 
								color 
						);
						
					}
				}
			}
		}
		_pg.endDraw();
//		_pg.filter(_desaturate);
		
		_image.copy( _pg, 0, 0, _width, _height, 0, 0, _width, _height );
	}
	
	protected void newParticle( float x, float y, float speedX, float speedY, int color ) {
		BlobParticle particle;
		if( _inactiveParticles.size() > 0 ) {
			particle = _inactiveParticles.remove( _inactiveParticles.size() - 1 );
		} else {
			particle = new BlobParticle();
		}
		particle.startAt( x, y, speedX, speedY, color );
		_particles.add( particle );
	}
	
	
	
	
	
	
	public class BlobParticle {
		
		PVector _position = new PVector();
		PVector _speed = new PVector();
		PVector _gravity = new PVector(0,-0.07f);
		int _color = 0;
		float _opacity = 1;
		float _opacityFadeSpeed = -0.01f;
		public boolean active = false;
		protected int _audioIndex;
		
		public BlobParticle() {
			
		}
		
		public void startAt( float x, float y, float speedX, float speedY, int color ) {
			_position.set( x, y );
			_speed.set( speedX * 15 * p.random(2f) + p.random(-0.2f,0.2f), speedY * 5 * p.random(3f) );	// add a little extra x variance
			_speed.mult( 1 + p.audioFreq(_audioIndex) ); // speed multiplied by audio
			_color = color;
			_opacity = 0.8f;
			_opacityFadeSpeed = p.random(50f, 500f) / 10000f; // 0.005 - 0.05
			active = true;
			_audioIndex = MathUtil.randRange(0, 511);
		}
		
		public void update() {
			_position.add( _speed );
			_speed.add( _gravity );
			_speed.set( _speed.x * 0.98f, P.constrain( _speed.y, -5f, 5f )  );
			
			_opacity -= _opacityFadeSpeed;
			if( _opacity <= 0 ) {
				active = false;
			} else {
				p.fill( _color, 127f * _opacity );
				p.noStroke();
				float size = 1 + p.audioFreq(_audioIndex) * 10; // was 3
				p.rect( _position.x - size/2f, _position.y - size/2f, size, size );
			}
		}
	}

}

