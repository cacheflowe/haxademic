package com.haxademic.app.airdrums;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import toxi.color.TColor;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.kinect.IKinectWrapper;
import com.haxademic.core.system.FileUtil;

import ddf.minim.AudioPlayer;

public class AirDrums
extends PAppletHax  
{
	public static final float PIXEL_SIZE = 7;
	public static int KINECT_CLOSE = 1500;
	public static int KINECT_FAR = 1700;
	
	protected final int PAD_COLS = 4;
	protected final int PAD_ROWS = 3;
	protected ArrayList<BeatSquare> _beats;
	protected ColorGroup _colors;
	protected float _drawRatio = 1;

	/**
	 * Auto-initialization of the main class.
	 */
	private static final long serialVersionUID = 1L;
	public static void main(String args[]) {
		_isFullScreen = true;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "com.haxademic.app.airdrums.AirDrums" });
	}

	public void setup() {
		_customPropsFile = FileUtil.getHaxademicDataPath() + "properties" + File.pathSeparator + "airdrums.properties";
		super.setup();
		initDrums();
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "kinect_active", "true" );
	}

	public void initDrums() {
		KINECT_CLOSE = _appConfig.getInt( "kinect_min_mm", 1500 );
		KINECT_FAR = _appConfig.getInt( "kinect_max_mm", 1700 );

		_colors = new ColorGroup( ColorGroup.NEON );		
	
		_drawRatio = (float)p.height / (float)IKinectWrapper.KHEIGHT;
		P.println(_drawRatio);

//		float threeFourthsRatioW = p.height * (1f+1f/3f);
//		float drumPadW = threeFourthsRatioW / PAD_COLS;
//		float drumPadH = p.height / PAD_ROWS;
		
		float drumPadW = IKinectWrapper.KWIDTH / PAD_COLS;
		float drumPadH = IKinectWrapper.KHEIGHT / PAD_ROWS;
		
		_beats = new ArrayList<BeatSquare>();
		_beats.add( new BeatSquare(0 * drumPadW, 0 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(0,0), "data/audio/drums/bass.wav") );
		_beats.add( new BeatSquare(1 * drumPadW, 0 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(0,1), "data/audio/drums/booty.wav") );
		_beats.add( new BeatSquare(2 * drumPadW, 0 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(0,2), "data/audio/drums/booty-house.wav") );
		_beats.add( new BeatSquare(3 * drumPadW, 0 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(0,3), "data/audio/drums/labia.wav") );
		
		_beats.add( new BeatSquare(0 * drumPadW, 1 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(1,0), "data/audio/kit808/kick.wav") );
		_beats.add( new BeatSquare(1 * drumPadW, 1 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(1,1), "data/audio/kit808/snare.wav") );
		_beats.add( new BeatSquare(2 * drumPadW, 1 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(1,2), "data/audio/kit808/tom.wav") );
		_beats.add( new BeatSquare(3 * drumPadW, 1 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(1,3), "data/audio/kit808/bass.wav") );
		
		_beats.add( new BeatSquare(0 * drumPadW, 2 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(2,0), "data/audio/drums/snare-x10.wav") );
		_beats.add( new BeatSquare(1 * drumPadW, 2 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(2,1), "data/audio/drums/chirp-11.wav") );
		_beats.add( new BeatSquare(2 * drumPadW, 2 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(2,2), "data/audio/drums/chirp-18.wav") );
		_beats.add( new BeatSquare(3 * drumPadW, 2 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(2,3), "data/audio/drums/janet-stab.wav") );
		
	}
		
	public void drawApp() {
//		DrawUtil.resetGlobalProps( p );
		p.shininess(1000f); 
		p.lights();
		p.background(0);
		
		// draw filtered web cam
//		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);
		
		p.translate(p.width, 0);
		p.rotateY(P.PI);

		for( int i=0; i < _beats.size(); i++ ) {
			_beats.get(i).update();
		}
	}
	
	public class BeatSquare {
		
		public float _x;
		public float _y;
		public float _w;
		public float _h;
		public TColor _color;
		protected AudioPlayer _sound;
		protected boolean _active = false;
		
		public BeatSquare( float x, float y, float w, float h, TColor color, String file ) {
			_x = x;
			_y = y;
			_w = w;
			_h = h;
			_color = color;
			_sound = minim.loadFile( file, 512 );
		}
		
		public void update() {
			detectInteraction();
			draw();
		}
		
		protected void draw() {
			if( _active == true ) 
				fill( _color.toARGB() );
			else
				noFill();
//			stroke( 255 );
			noStroke();
			rect( _x * _drawRatio, _y * _drawRatio, _w * _drawRatio, _h * _drawRatio );			
		}

		protected void detectInteraction() {
			if( _appConfig.getBoolean("kinect_active", false) == false ) {				
				detectWithMouse();
			} else {				
				detectWithKinect();
			}
		}
		
		protected void detectWithMouse() {
			if( p.mouseX > _x && p.mouseX < _x + _w && p.mouseY > _y && p.mouseY < _y + _h ) {
				if( _active == false ) {
					_active = true;
					_sound.play(0);
				}
			} else {
				_active = false;
			}
		}

		protected void detectWithKinect() {
//			drawKinectUser();
//			p.image(p.kinectWrapper.getRgbImage(), 0, 0);
			float pixelDepth;
			int activePixels = 0;
			boolean madeActive = false;
			for ( int x = (int)_x; x < _x + _w; x += PIXEL_SIZE ) {
				for ( int y = (int)_y; y < _y + _h; y += PIXEL_SIZE ) {
					pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
					if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
						activePixels++;
						if( _active == false && activePixels >= 4 ) {
							_active = true;
							_sound.play(0);
						}
						madeActive = true;
					}
				}
			}
			if( madeActive == false ) {
				_active = false;
			}
		}
		
		protected void drawKinectUser() {
			p.kinectWrapper.setMirror(true);
			// loop through kinect data within player's control range
			p.stroke(255, 127);
			float pixelDepth;
			for ( int x = 0; x < IKinectWrapper.KWIDTH; x += PIXEL_SIZE ) {
				for ( int y = 0; y < IKinectWrapper.KHEIGHT; y += PIXEL_SIZE ) {
					pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
					if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
						p.pushMatrix();
						// p.fill(((pixelDepth - KINECT_CLOSE) / (KINECT_FAR - KINECT_CLOSE)) * 255f);
						p.fill(255f, 180);
						p.rect(x * _drawRatio, y * _drawRatio, PIXEL_SIZE * _drawRatio, PIXEL_SIZE * _drawRatio);
						p.popMatrix();
					}
				}
			}
		}
	}
}
