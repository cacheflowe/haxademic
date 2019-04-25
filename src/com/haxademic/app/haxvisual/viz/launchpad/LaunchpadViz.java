package com.haxademic.app.haxvisual.viz.launchpad;
//package com.haxademic.viz.launchpad;
//
//import processing.core.PApplet;
//import themidibus.MidiBus;
//
//import com.haxademic.app.P;
//import com.haxademic.core.audio.AudioInputWrapper;
//import com.rngtng.launchpad.LColor;
//import com.rngtng.launchpad.Launchpad;
//
//public class LaunchpadViz {
//	PApplet p;
//	
//	Launchpad _launchpad;
//	MidiBus midibus;
//	protected AudioInputWrapper _audioInput;
//	
//	protected int _width = 8;
//	protected int _height = 8;
//	protected int _multiplier = 100;
//	protected int _audioMultiplier = 50;
//	
//	public int GREEN_LOW_COLOR;
//	public int GREEN_MEDIUM_COLOR;
//	public int GREEN_HIGH_COLOR;
//	public int YELLOW_LOW_COLOR;
//	public int YELLOW_HIGH_COLOR;
//	
//	protected LaunchpadCell[][] _cells;
//	protected int[] _verticalColors;
//	protected int[] _verticalLocalColors;
//	
//	public LaunchpadViz( PApplet p ) {
//		this.p = p;
////		P.size( _width * _multiplier, _height * _multiplier, P.P3D );
////		P.frameRate( 40 );
////		P.background( 0 );
////
////		P.rectMode(PConstants.CORNER);
////		P.noStroke();
//		
//		_launchpad = new Launchpad( p );
//		
//		_verticalColors = new int[ 8 ];
//		_verticalColors[0] = LColor.YELLOW_HIGH;
//		_verticalColors[1] = LColor.YELLOW_HIGH;
//		_verticalColors[2] = LColor.YELLOW_LOW;
//		_verticalColors[3] = LColor.YELLOW_LOW;
//		_verticalColors[4] = LColor.GREEN_HIGH;
//		_verticalColors[5] = LColor.GREEN_MEDIUM;
//		_verticalColors[6] = LColor.GREEN_MEDIUM;
//		_verticalColors[7] = LColor.GREEN_LOW;
//		
//		GREEN_LOW_COLOR = p.color( 0, 150, 0 );
//		GREEN_MEDIUM_COLOR = p.color( 0, 200, 0 );
//		GREEN_HIGH_COLOR = p.color( 0, 255, 0 );
//		YELLOW_LOW_COLOR = p.color( 255, 255, 0 );
//		YELLOW_HIGH_COLOR = p.color( 255, 0, 0 );
//
//		_verticalLocalColors = new int[ 8 ];
//		_verticalLocalColors[0] = YELLOW_HIGH_COLOR;
//		_verticalLocalColors[1] = YELLOW_HIGH_COLOR;
//		_verticalLocalColors[2] = YELLOW_LOW_COLOR;
//		_verticalLocalColors[3] = YELLOW_LOW_COLOR;
//		_verticalLocalColors[4] = GREEN_HIGH_COLOR;
//		_verticalLocalColors[5] = GREEN_MEDIUM_COLOR;
//		_verticalLocalColors[6] = GREEN_MEDIUM_COLOR;
//		_verticalLocalColors[7] = GREEN_LOW_COLOR;
//		
//		_cells = new LaunchpadCell[ _width ][ _height ];
//		for( int i = 0; i < _width; i++ ) {
//			for( int j = 0; j < _height; j++ ) {
//				_cells[i][j] = new LaunchpadCell( i, j );
//			}
//		}
//
//		// set up audio input
//		_audioInput = new AudioInputWrapper( p, false );
//		_audioInput.setNumAverages( 12 );	// add 4 on the front that are for padding, since bass response it so bad
//	}
//	
//	public void update() {
//		
////		P.background(0,0,0,255);
////		P.stroke(0, 255);
////		P.strokeWeight(5);
////		P.fill(255, 255);
//		
//		for( int i = 0; i < _width; i++ ) {
//			// get volume for column - round and counstrain to 8
//			float volVal = _audioInput.getFFT().spectrum[ 3 + i ] * _audioMultiplier;
//			volVal = P.round( P.constrain( volVal, 0f, 8f ) );
//			
//			for( int j = 0; j < _height; j++ ) {				
//				if( j < 8 - volVal ) {
//					_cells[i][j].off();
//				} else {
//					_cells[i][j].on( _verticalColors[ j ] );
//					// paint the local screen
////					P.fill( _verticalLocalColors[ j ] );
////					P.rect( i * _multiplier, j * _multiplier, _multiplier, _multiplier );
//				}
//			}
//		}
//	}
//	
//	public AudioInputWrapper getAudio() {
//		return _audioInput;
//	}
//	
//	public void dispose() {
//		_launchpad.reset();
//		_launchpad.dispose();
//	}
//	
//	public class LaunchpadCell {
//		
//		protected int _x;
//		protected int _y;
//		protected int _curColor;
//		
//		public LaunchpadCell( int x, int y ) {
//			_x = x;
//			_y = y;
//		}
//		
//		public void on( int color ) {
//			if( _curColor != color ) {
//				_curColor = color;
//				_launchpad.changeGrid( _x, _y, new LColor( _curColor, _curColor ) );
//			}
//		}
//		
//		public void off() {
//			if( _curColor != LColor.OFF ) {
//				_curColor = LColor.OFF;
//				_launchpad.changeGrid( _x, _y, new LColor( _curColor, _curColor ) );
//			}
//		}
//	}
//}
