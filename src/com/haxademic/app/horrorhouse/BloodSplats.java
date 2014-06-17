package com.haxademic.app.horrorhouse;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import processing.video.Movie;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.kinect.KinectAmbientActivityMonitor;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class BloodSplats
extends PAppletHax{
	
	protected int _numDrips = 50;
	protected Drip[] _drips = new Drip[_numDrips];
	protected int _newDripIndex = 0;
	protected ArrayList<Movie> _vidClips;
	protected Movie _curClip;
	protected boolean _clipPlaying = false;
	
	protected PGraphics _pg;
	
	protected PShader _invert;
	protected PShader _badtv;
	protected PShader _vignette;
	protected PShader _blurH;
	protected PShader _blurV;
	
	protected KinectAmbientActivityMonitor _kinectMonitor;
	
	protected PImage _horrorhouse;
	protected boolean _logoShowing = true;
	
	protected boolean _kinectActive = false;
	protected int _nextLightning = 0;
	protected int _nextLogoSpaz = 0;
	protected int _nextLightningLength = 0;
	protected int _nextLogoSpazLength = 0;
	protected int _nextSplatFrame = 0;
	// configure everything for the install here ======================
	protected boolean _mirrored = false;
	protected float _baseLogoScale = 1.0f;
	protected float _kinectAcitivityLaunchThreshold = 40000f;
	protected int _nextLightningLow = 100;
	protected int _nextLightningHigh = 800;
	// end config =====================================================
	
	
	/**
	 * Auto-initialization of the main class.
	 * @param args
	 */
	public static void main(String args[]) {
		_isFullScreen = true;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "com.haxademic.app.horrorhouse.BloodSplats" });
	}
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "1000" );
		_appConfig.setProperty( "height", "800" );
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "fps", "30" );
		_appConfig.setProperty( "kinect_active", "true" );
		_appConfig.setProperty( "fullscreen", "true" );
	}
	
	public void setup() {
		super.setup();
		
		_kinectActive = _appConfig.getBoolean( "kinect_active", false );
		
		if( _kinectActive == true ) _kinectMonitor = new KinectAmbientActivityMonitor( 20, 500, 15000 );
		
		_vidClips = new ArrayList<Movie>();
		_vidClips.add( new Movie(this, "/Users/cacheflowe/Documents/workspace/haxademic-2/data/video/horrorhouse/MotelLondon_02.mov") );
		_vidClips.add( new Movie(this, "/Users/cacheflowe/Documents/workspace/haxademic-2/data/video/horrorhouse/MotelLondon_01.mov") );
		_vidClips.add( new Movie(this, "/Users/cacheflowe/Documents/workspace/haxademic-2/data/video/horrorhouse/MotelLondon_03.mov") );
		_vidClips.add( new Movie(this, "/Users/cacheflowe/Documents/workspace/haxademic-2/data/video/horrorhouse/MotelLondon_04.mov") );
		_vidClips.add( new Movie(this, "/Users/cacheflowe/Documents/workspace/haxademic-2/data/video/horrorhouse/MotelLondon_05.mov") );
		_vidClips.add( new Movie(this, "/Users/cacheflowe/Documents/workspace/haxademic-2/data/video/horrorhouse/MotelLondon_06.mov") );
		_vidClips.add( new Movie(this, "/Users/cacheflowe/Documents/workspace/haxademic-2/data/video/horrorhouse/MotelLondon_07.mov") );
		_vidClips.add( new Movie(this, "/Users/cacheflowe/Documents/workspace/haxademic-2/data/video/horrorhouse/MotelLondon_08.mov") );
		for(int i=0; i < _vidClips.size(); i++) {
//			_vidClips.get(i).speed(5.4f);
		}
		_curClip = _vidClips.get(0);
		
		_horrorhouse = p.loadImage( FileUtil.getHaxademicDataPath()+"images/horrorhouse.png" );
//		_drop = p.loadImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABaCAIAAACHRsd0AAAAAXNSR0IArs4c6QAAAAZiS0dEAP8A/wD/oL2nkwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9wIARENKcscdTYAAAFbSURBVHja7ZoxTsMwFIZtN02DkooBxg6VGBArF+AArD1Fr8HWjYF7cI5K5QJdKnUqUjaEBEkxa5f3VNxgOer3b9GTnHx6v/3HiW1ZluaP+siupFLV1iaunOm5AAAAAAAAOEk2IMjoAAAHyg4vFq6cuyKd9xwsBEDsOdCVYm4Y6EAUKS1lEgOQ4stc56sQc4Bl9BgzhBkMC5HEx0ixFhYCAIDzXIX0PFLkR9Z++dSX0e/70GGXzAEAAAAAAAA6CzIlbv2Fbe4GUjV/2kil/arav14H3FHaEmAhAAAAAIAzDzIlO5SNlS2a/HZLBwAAAAAAAEh5R5ZN38XaxJsHK1aVD6fDQTwAd/kp1kbG3NiQG1Y/WAgAAAAAoCdB9h/S//AFnFZRAWZKoKqjPotP6dY+f2uxEAAAAAAAAF0lo3L0WDtTM23My06sPk6kyritfa874LEQAAAAAEBS+gXVX0Ar1x59kgAAAABJRU5ErkJggg==");
		for(int i=0; i < _numDrips; i++) {
			_drips[i] = new Drip();
		}
		p.background(0);
		
		
		_invert = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/invert.glsl" ); 

		_badtv = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/badtv.glsl" ); 
		_badtv.set("time", p.frameCount * 0.1f);
		_badtv.set("grayscale", 0);
		_badtv.set("nIntensity", 0.75f);
		_badtv.set("sIntensity", 0.55f);
		_badtv.set("sCount", 4096.0f);

		_vignette = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.85f);
		_vignette.set("spread", 0.15f);
		
		_blurH = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-horizontal.glsl" ); 
		_blurH.set( "h", 1f/p.width );
		_blurV = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-vertical.glsl" ); 
		_blurV.set( "v", 1f/p.height );

		_pg = p.createGraphics(width, height, P.OPENGL);
		_pg.background(0);
		p.background(0);
		
		getNextLightning();
		getNextLogoSpaz();
	}
	
	public void drawApp() {
		p.background(0);
		setScreenProps();
		handleKinectInput();
		drawBloodAndVideo();

		drawLogo();
		applyFilters();
	}
	
	public void keyPressed() {
		if(p.key == ' ') {
			startClip();
		} else if (p.key == 'm') {
			_curClip.stop();
			_clipPlaying = false;
		}
	}
	
	public void setScreenProps() {
		DrawUtil.setDrawCorner(p);
		if( _mirrored == true ) {
			p.translate( p.width, 0 );
			p.rotateY( P.PI );
		}
	}
	
	public void handleKinectInput() {
		if( _kinectActive == true ) {
			float kinectActivity = _kinectMonitor.update( p.kinectWrapper, false );
			if( _clipPlaying == false && kinectActivity > _kinectAcitivityLaunchThreshold && p.frameCount > 60 ) {
				startClip();
			}
		}
	}
	
	public void drawBloodAndVideo() {
		_pg.beginDraw();
		_pg.noStroke();
		
		// fade out screen black
		_pg.fill(0, 1);
		_pg.rect(0,0,_pg.width,_pg.height);
		
		// set blood color
		_pg.fill(255,0,0);
		
		// launch new drips and splats
		if(p.frameCount % 20 == 0) {
			newDrip();
		}
		if(p.frameCount >= _nextSplatFrame) {
			newBlood();
			_nextSplatFrame += MathUtil.randRange(10, 100);
		}
		
		//animate drips
		for(int i=0; i < _numDrips; i++) _drips[i].update();
		
		// handle video playback
		if( _clipPlaying == true ) {
			// ImageUtil.cropFillCopyImage( _curClip, _pg, true );
			_pg.image(_curClip, 0, 0, width, height);
		}
		if( _curClip.time() >= _curClip.duration() ) {
			_curClip.stop();
			_clipPlaying = false;
			for( int i=0; i < 15; i++ ) newBlood();
			for( int i=0; i < _drips.length; i++ ) _drips[i].reset( p.random(0,p.width), p.height * 2 );
		}
		
		// wrap it up and draw PGraphics to scren
		_pg.endDraw();
		p.image( _pg, 0, 0 );
	}
	
	public void drawLogo() {
		if( _clipPlaying == false ) {
			DrawUtil.setDrawCenter(p);
			p.translate( p.width / 2, p.height / 2 );
			p.scale(P.sin(p.frameCount/100f) * (_baseLogoScale * 0.1f) + _baseLogoScale);
			p.rotate( P.sin(p.frameCount/330f) * 0.03f );
			
			if( isLightning() == true || isLogoSpaz() == true ) {
				p.translate( MathUtil.randRange(-20, 20), MathUtil.randRange(-20, 20) );
				p.rotate( MathUtil.randRangeDecimal(-.3f, -.3f) );
				p.scale( MathUtil.randRangeDecimal( 0.5f, 1.5f ) );
			}
			
			p.image( _horrorhouse, 0, 0 );
		}
	}
	
	public void applyFilters() {
		p.filter(_vignette);
		applyBlur();
		// p.filter(_badtv);
		if( isLightning() == true ) p.filter( _invert );
	}
	
	public void applyBlur() {
		int frameCountOffset = p.frameCount % 1000;
		if( frameCountOffset < 150 ) {
			float blurOsc = (float)(Math.sin(frameCountOffset/30f));
			if( blurOsc > 0 ) {
				p.filter(_blurH);
				_blurH.set( "h", blurOsc/p.width );
				p.filter(_blurV);
				_blurV.set( "v", blurOsc/p.height );
			}
		}
	}
	
	public void startClip() {
		_pg.background(0);
		if(_curClip != null) _curClip.stop();
		_clipPlaying = true;
		for(int i=0; i < _vidClips.size(); i++) {
			if( _vidClips.get(i) == _curClip ) {
				_curClip = _vidClips.get( (i+1) % _vidClips.size() );
				break;
			}
		}
		_curClip.jump(0);
		_curClip.play();
	}
	
	public void getNextLightning() {
		_nextLightning += MathUtil.randRange( _nextLightningLow, _nextLightningHigh );
		if( _nextLightning % 2 != 0 ) _nextLightning++;
		_nextLightningLength = MathUtil.randRange( 4, 10 );
		if( _nextLightningLength % 2 != 0 ) _nextLightningLength++;
	}
	
	public boolean isLightning() {
		if( p.frameCount >= _nextLightning && p.frameCount <= _nextLightning + _nextLightningLength && p.frameCount % 2 == 0 ) { 
			if( p.frameCount == _nextLightning + _nextLightningLength ) getNextLightning();
			return true;
		} else if( p.frameCount % 1000 == 0 || p.frameCount % 1000 == 2 || p.frameCount % 1000 == 94 || p.frameCount % 1000 == 96 ) {
			// time with blur filter start/end
			return true;
		} else {
			return false;
		}
	}
	
	public void getNextLogoSpaz() {
		_nextLogoSpaz += MathUtil.randRange( _nextLightningLow, _nextLightningHigh );
		if( _nextLogoSpaz % 2 != 0 ) _nextLogoSpaz++;
		_nextLogoSpazLength = MathUtil.randRange( 4, 10 );
		if( _nextLogoSpazLength % 2 != 0 ) _nextLogoSpazLength++;
	}
	
	public boolean isLogoSpaz() {
		if( p.frameCount >= _nextLogoSpaz && p.frameCount <= _nextLogoSpaz + _nextLogoSpazLength && p.frameCount % 2 == 0 ) { 
			if( p.frameCount == _nextLogoSpaz + _nextLogoSpazLength ) getNextLogoSpaz();
			return true;
		} else {
			return false;
		}
	}
	
	
	
	public void newDrip() {
		// place drip
		_drips[_newDripIndex % _numDrips].reset( p.random(0,p.width), -10 );
		_newDripIndex++;
	}
	
	public void newBlood() {
		int numPoints = 30;
		float[] radii = new float[numPoints];
		float circleSegment = P.TWO_PI / (float)numPoints;
		float segment = 0;
		
		_pg.fill(255,0,0, p.random(200, 255));
		_pg.pushMatrix();
		_pg.translate(p.random(0,width), p.random(0, p.height));
		
		_pg.beginShape();
		for(int i=0; i < numPoints; i++) {
			segment = circleSegment * i;
			float radius = p.random(30,100);
			radii[i] = radius;
			_pg.curveVertex(P.sin(segment) * radius, P.cos(segment) * radius);
		}
		_pg.endShape();
		
		for(int i=0; i < numPoints; i++) {
			segment = circleSegment * i;
			if(radii[(i+1)%numPoints] < radii[i] && radii[(i-1+numPoints)%numPoints] < radii[i]) {
				float radiusSplat = radii[i] * p.random(1.1f,1.3f);
				float splatSize = p.random(5f,20f);
				_pg.pushMatrix();
				_pg.translate( P.sin(segment) * radiusSplat, P.cos(segment) * radiusSplat );
				_pg.rotate( -segment );
				_pg.ellipse( 0, 0, splatSize*p.random(0.3f,1.f), splatSize*p.random(2f,3.5f) );
				_pg.popMatrix();
				
				// place_newDripIndex drip
//				_drips[_newDripIndex % _numDrips].reset( P.sin(segment) * radii[i], P.cos(segment) * radii[i] );
//				_newDripIndex++;
			}
		}
		_pg.popMatrix();
	}
	
	public class Drip {
		
		float _x = 0;
		float _y = 0;
		float _size = 10;
		float _speed = 1;
		int _color = 0;
		
		public Drip() {
			
		}
		
		public void reset( float x, float y ) {
			_x = x;
			_y = y;
			_size = p.random(10, 50);
			_speed = p.random(0.3f, 2f);
			_color = p.color( p.random(180,255), 0, 0 );
		}
		
		public void update() {
			_y += _speed;
			
			_pg.fill( _color );

			_pg.ellipse( _x, _y, _size, _size );
			
			_pg.beginShape();
			_pg.fill( _color );
			_pg.vertex( _x - _size * 0.5f, _y );
			_pg.fill( 0 );
			_pg.vertex( _x, _y - _size * 10.0f );
			_pg.fill( _color );
			_pg.vertex( _x + _size * 0.5f, _y );
			_pg.endShape();
		}
	}
	
	
	public void movieEvent(Movie m) {
		m.read();
	}

}