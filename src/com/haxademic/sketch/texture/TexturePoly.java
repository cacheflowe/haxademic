package com.haxademic.sketch.texture;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Capture;
import processing.video.Movie;

public class TexturePoly
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PImage img;
	
	PGraphics _barsTexture;
	PGraphics _eqTexture;
	PGraphics _webcamTexture;
	Capture _webCam;
	PGraphics _movieTexture;
	Movie _movie;
	ArrayList<MappedTriangle> _triangles;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void firstFrame() {
		AudioIn.instance();
		img = loadImage(FileUtil.getHaxademicDataPath() + "images/justin-tiny-color1.png");
		// img = loadImage(FileUtil.getHaxademicDataPath() + "images/sphere-map-test-2.jpg");
		noStroke();
		
		_triangles = new ArrayList<MappedTriangle>();
		for(int i=0; i < 100; i++ ) {
			float startX = p.random(0,p.width);
			float startY = p.random(0,p.height);
			_triangles.add( new MappedTriangle( startX, startY, startX + p.random(-300,300), startY + p.random(-300,300), startX + p.random(-300,300), startY + p.random(-300,300) ) );
		}
		_triangles.add( new MappedTriangle( 100, 200, 400, 700, 650, 300 ) );
		
//		initWebCam();
	}
	
	void initWebCam() {
		String[] cameras = Capture.list();
		if (cameras.length == 0) {
			exit();
		} else {
			_webCam = new Capture(this, cameras[0]);
			_webCam.start();
		}      
	}
		
	public void drawApp() {
		updateTextures();
		
		background(0);
		
		// basic quad
//		beginShape(QUADS);
//		texture(_barsTexture);
//		vertex(50+ 90f*sin(f/10f), 70+ 50f*sin(f/9f),  150f*sin(f/6f), 	0, 0);
//		vertex(250+ 90f*sin(f/7f), 100+ 50f*sin(f/7f), 150f*sin(f/7f), 	_barsTexture.width, 0);
//		vertex(300+ 90f*sin(f/3f), 350+ 50f*sin(f/4f), 150f*sin(f/3f), 	_barsTexture.width, _barsTexture.height);
//		vertex(90+ 90f*sin(f/9f),  300+ 60f*sin(f/6f), 150f*sin(f/8f), 	0, _barsTexture.height);
//		endShape();
		
		// update triangles
		for(int i=0; i < _triangles.size(); i++ ) {
			MappedTriangle triangle = _triangles.get(i);
			if(p.random(0,100) > 99) {
				triangle.rotateTexture();
				
				float rand = p.random(0,100);
				if(rand > 66) {
					triangle.setTexture(_barsTexture);
				} else if(rand > 33) {
					triangle.setTexture(_eqTexture);					
				} else {
					triangle.setTexture(_movieTexture);
				}
				
				triangle.setTextureStyle( MathUtil.randBoolean() );
			}
			triangle.draw(p.g);
		}		
	}
	
	public void updateTextures() {
		// webcam ---------------------------------------------------
		if( _webCam != null && _webCam.available() ) { 
			if( _webcamTexture == null && _webCam.width > 1 ) {
				_webcamTexture = p.createGraphics( _webCam.width, _webCam.height, P.P3D );
			}
			_webCam.read(); 

			if( _webcamTexture != null ) {
				_webcamTexture.beginDraw();
				_webcamTexture.image( _webCam.get(), 0, 0, _webCam.width, _webCam.height );
				_webcamTexture.endDraw();
			}
		}
		
		// video ----------------------------------------------------
		if( _movie == null ) {
			_movie = DemoAssets.movieFractalCube();
			_movie.play();
//			_movie.loop();
//			_movie.volume(0);
//			_movie.speed(1f);
		}
		if(_movie.width > 10 && _movie.available() == true) {
			if(_movieTexture == null) {
				_movieTexture = p.createGraphics( _movie.width, _movie.height, P.P3D );
				_movie.play();
			}
			_movieTexture.beginDraw();
			_movieTexture.image(_movie, 0, 0);
			_movieTexture.endDraw();
		}
		
		// repeating columns ----------------------------------------
		if( _barsTexture == null ) {
			_barsTexture = p.createGraphics(100, 100, P.P3D);
			_barsTexture.smooth(OpenGLUtil.SMOOTH_HIGH);
		}
		int barW = 20;
		int x = p.frameCount % (barW * 2);
		
		_barsTexture.beginDraw();
		_barsTexture.clear();
		
		for( int i=x - barW*2; i < _barsTexture.width; i+=barW*2 ) {
			_barsTexture.fill( 0 );
			_barsTexture.rect(i, 0, barW, _barsTexture.height );
			_barsTexture.fill( 255 );
			_barsTexture.rect(i+barW, 0, barW, _barsTexture.height );
		}
		
		_barsTexture.endDraw();

		// eq columns ------------------------------------------------
		if( _eqTexture == null ) {
			_eqTexture = p.createGraphics(200, 100, P.P3D);
		}
		int numBands = 32;
		float eqW = P.ceil( _eqTexture.width / numBands );
		float spectrumInterval = (int) ( 256 / numBands );	// 256 keeps it in the bottom half of the spectrum since the high ends is so overrun
		
		_eqTexture.beginDraw();
		_eqTexture.clear();
		
		for( int i=0; i < numBands; i++ ) {
			_eqTexture.fill( 255 * AudioIn.audioFreq(P.floor(i*spectrumInterval)) * 2, 255 );
			_eqTexture.rect(i * eqW, 0, eqW, _eqTexture.height );
		}
		
		_eqTexture.endDraw();
	}
	
	public class MappedTriangle {
		
		public float x1;
		public float y1;
		public float x2;
		public float y2;
		public float x3;
		public float y3;
		
		protected PImage _texture;
		
		protected int mappingOrientation;
		protected boolean _mappingStyleIsFullImage = false;
		
		public MappedTriangle( float x1, float y1, float x2, float y2, float x3, float y3 ) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.x3 = x3;
			this.y3 = y3;
			
			mappingOrientation = 0;
		}
		
		public void setTexture( PImage texture ) {
			_texture = texture;
		}
		
		public void setTextureStyle( boolean isFullImage ) {
			_mappingStyleIsFullImage = isFullImage;
		}
		
		public void rotateTexture() {
			float xTemp = x1;
			float yTemp = y1;
			x1 = x2;
			y1 = y2;
			x2 = x3;
			y2 = y3;
			x3 = xTemp;
			y3 = yTemp;

			mappingOrientation = MathUtil.randRange(0, 3); 
		}
		
		public void draw( PGraphics pg ) {
			if( _texture != null ) {
				pg.beginShape(PConstants.TRIANGLE);
				pg.texture(_texture);
				if( _mappingStyleIsFullImage == true ) {
					if( mappingOrientation == 0 ) {
						pg.vertex(x1, y1, 0, 		0, 0);
						pg.vertex(x2, y2, 0, 		_texture.width, _texture.height/2);
						pg.vertex(x3, y3, 0, 		0, _texture.height);
					} else if( mappingOrientation == 1 ) {
						pg.vertex(x1, y1, 0, 		0, 0);
						pg.vertex(x2, y2, 0, 		_texture.width, 0);
						pg.vertex(x3, y3, 0, 		_texture.width/2, _texture.height);
					} else if( mappingOrientation == 2 ) {
						pg.vertex(x1, y1, 0, 		0, _texture.height/2);
						pg.vertex(x2, y2, 0, 		_texture.width, 0);
						pg.vertex(x3, y3, 0, 		_texture.width, _texture.height);
					} else if( mappingOrientation == 3 ) {
						pg.vertex(x1, y1, 0, 		0, _texture.height);
						pg.vertex(x2, y2, 0, 		_texture.width/2, 0);
						pg.vertex(x3, y3, 0, 		_texture.width, _texture.height);
					}
				} else {
					float texScreenRatioW = (float) _texture.width / (float) pg.width;
					float texScreenRatioH = (float) _texture.height / (float) pg.height;
					pg.vertex(x1, y1, 0, 		x1 * texScreenRatioW, y1 * texScreenRatioH);
					pg.vertex(x2, y2, 0, 		x2 * texScreenRatioW, y2 * texScreenRatioH);
					pg.vertex(x3, y3, 0, 		x3 * texScreenRatioW, y3 * texScreenRatioH);
				}
				pg.endShape();
			}
		}
	}
}
