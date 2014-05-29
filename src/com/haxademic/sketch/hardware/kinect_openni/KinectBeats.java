
package com.haxademic.sketch.hardware.kinect_openni;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.kinect.KinectWrapper;

import ddf.minim.AudioOutput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.signals.SineWave;


@SuppressWarnings("serial")
public class KinectBeats 
extends PAppletHax {

	public static final float PIXEL_SIZE = 7;
	public static final int KINECT_CLOSE = 1500;
	public static final int KINECT_FAR = 1700;
	
	protected ArrayList<BeatSquare> _beats;
	protected SynthHand _synth;
	
	public void setup() {
		super.setup();
		loadSounds();
		_synth = new SynthHand();
	}

	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "kinect_active", "true" );
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
		_appConfig.setProperty( "fps", "60" );
	}
	
	protected void loadSounds() {
		_beats = new ArrayList<BeatSquare>();
		_beats.add( new BeatSquare(40, 300, 50, 100, "audio/kit808/kick.wav") );
		_beats.add( new BeatSquare(120, 300, 50, 100, "audio/kit808/hi-hat.wav") );
		_beats.add( new BeatSquare(200, 300, 50, 100, "audio/kit808/snare.wav") );
		_beats.add( new BeatSquare(40, 150, 50, 100, "audio/kit808/clap.wav") );
		_beats.add( new BeatSquare(120, 150, 50, 100, "audio/kit808/hi-hat-open.wav") );
		_beats.add( new BeatSquare(200, 150, 50, 100, "audio/kit808/tom.wav") );
	}
	
	public void drawApp() {
		DrawUtil.resetGlobalProps( p );
		p.shininess(1000f); 
		p.lights();
		p.background(0);
		
		// draw filtered web cam
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);

		for( int i=0; i < _beats.size(); i++ ) {
			_beats.get(i).update();
		}
		_synth.update();
	}
	
	public class BeatSquare {
		
		public int _x;
		public int _y;
		public int _w;
		public int _h;
		protected AudioPlayer _sound;
		protected boolean _active = false;
		
		public BeatSquare( int x, int y, int w, int h, String file ) {
			_x = x;
			_y = y;
			_w = w; 
			_h = h;
			_sound = minim.loadFile( file, 512 );
		}
		
		public void update() {
			detectInteraction();
			draw();
		}
		
		protected void draw() {
			if( _active == true ) 
				fill( 127, 127 );
			else
				noFill();
			stroke( 255 );
			rect( _x, _y, _w, _h );			
		}

		protected void detectInteraction() {
//			detectWithMouse();
			detectWithKinect();
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
			drawKinectUser();
//			p.image(p.kinectWrapper.getRgbImage(), 0, 0);
			float pixelDepth;
			boolean madeActive = false;
			for ( int x = _x; x < _x + _w; x += PIXEL_SIZE ) {
				for ( int y = _y; y < _y + _h; y += PIXEL_SIZE ) {
					pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
					if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
						if( _active == false ) {
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
			for ( int x = 0; x < KinectWrapper.KWIDTH; x += PIXEL_SIZE ) {
				for ( int y = 0; y < KinectWrapper.KHEIGHT; y += PIXEL_SIZE ) {
					pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
					if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
						p.pushMatrix();
						p.fill(((pixelDepth - KINECT_CLOSE) / (KINECT_FAR - KINECT_CLOSE)) * 255f);
						p.rect(x, y, PIXEL_SIZE, PIXEL_SIZE);
						p.popMatrix();
					}
				}
			}
		}
	}
	
	public class SynthHand {
		protected AudioOutput _audioOut;
		protected SineWave _oscillator;

		@SuppressWarnings("deprecation")
		public SynthHand() {
			_audioOut = minim.getLineOut(Minim.STEREO, 512);
			_oscillator = new SineWave(200, 1, _audioOut.sampleRate());
			_oscillator.setAmp(0);
			_audioOut.addSignal(_oscillator);
		}
		
		public void update() {
			draw();
//			updateWithMouse();
			updateWithKinect();
		}
		
		protected void draw() {
			noFill();
			stroke( 255 );
			line( p.width / 2, 0, p.width / 2, p.height );			
			line( p.width / 2 + 100, 0, p.width / 2 + 100, p.height );			
		}
		
		protected void drawControlAtY( int y ) {
			fill(127, 127);
			noStroke();
			rect( p.width / 2, y, 100, 10 );
		}
		
		protected void updateWithMouse() {
			if( p.mouseX > p.width / 2 && p.mouseX < p.width / 2 + 100 ) {
				_oscillator.setFreq( 150 + ( p.height - p.mouseY ) );
				drawControlAtY( p.mouseY );
				_oscillator.setAmp(0.3f);
				return;
			}
			_oscillator.setAmp(0);
		}

		protected void updateWithKinect() {
			float pixelDepth;
			for ( int x = p.width / 2; x < p.width / 2 + 100; x += PIXEL_SIZE ) {
				for ( int y = 0; y < p.height; y += PIXEL_SIZE ) {
					pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
					if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
						_oscillator.setFreq( 30 + ( p.height - y ) );
						_oscillator.setAmp(0.3f);
						drawControlAtY(y);
						return;
					}
				}
			}
			_oscillator.setAmp(0);
		}

	}
}
