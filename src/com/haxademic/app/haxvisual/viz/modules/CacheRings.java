package com.haxademic.app.haxvisual.viz.modules;

import processing.core.PConstants;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.core.camera.CameraOscillate;
import com.haxademic.core.camera.common.ICamera;
import com.haxademic.core.draw.color.ColorHax;
import com.haxademic.core.draw.shapes.CacheFloweLogo;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.midi.MidiWrapper;

public class CacheRings
	extends ModuleBase
	implements IVizModule
	{
		// class props
		protected ICamera _camera;
		protected int NUM_RINGS = 10;
		
		// draw mode props
		protected int _mode = 0;
		protected final int MODE_ALPHA = 0;
		protected final int MODE_SOLID = 1;
		protected final int MODE_WIREFRAME = 2;
		protected final int NUM_MODES = 3;
		
		// spin modes
		protected int _spinMode = 0;
		protected final int SPINMODE_NONE = 0;
		protected final int SPINMODE_Y = 1;
		protected final int SPINMODE_XY = 2;
		protected final int NUM_SPIN_MODES = 3;
		
		
		// mode vars
		protected float _fillAlpha = 1;
		protected ColorHax _yellow = new ColorHax( 255/255f, 249/255f, 0, 1 );
		protected ColorHax _blue = new ColorHax( 0, 249/255f, 255/255f, 1 );
		protected ColorHax _red = new ColorHax( 249/255f, 150/255f, 150/255f, 1 );
		protected ColorHax _white = new ColorHax( 240/255f, 240/255f, 240/255f, 1 );
		protected ColorHax[] _ringColors;
		protected ColorHax _curColor;
		protected float wallOffset = -30/255f;
		
		/*
		 * TODO:
		 * shake circle to wave?
		 * add waveform back in somehow?
		*/
		
		public CacheRings( )
		{
			super();
			// store and init audio engine
			initAudio();

			// init viz
			init();
		}

		public void init() {
			p.colorMode( PConstants.RGB, 1, 1, 1, 1 );			
			
			// init colors
			_ringColors = new ColorHax[NUM_RINGS];
			for(int i = 0; i < NUM_RINGS; i++)
			{
				_ringColors[i] = new ColorHax(0,0,0,1);   
			}
			_curColor = new ColorHax(1, 1, 1, 1);
			
			// init with random mode
			pickMode();
		}

		public void initAudio()
		{
			_audioData.setNumAverages( NUM_RINGS * 2 );
			_audioData.setDampening( .13f );
		}

		public void focus() {
			p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
			p.rectMode(PConstants.CORNER);
			p.shininess(1000); 
			p.lights();
			p.camera();
			initAudio();
			pickMode();
			newSpinMode();
			update();
		}

		public void update() {
			// clear screen
			p.background(0);	// 44/255f
			// start at center
			p.translate( p.width / 2, p.height / 2, 0 );
			
			// always rotate beginning draw position - rotates entire scene
			p.rotateX(p.radians(40*p.sin( p.frameCount * 0.02f )));
			p.rotateY(p.radians(20*p.sin( p.frameCount * 0.02f )));
			
			
			// Object properties	
			float scale = 2; // + p.sin( p.frameCount * 0.01f );
			int discPrecision = 40;
			//float thickness = 100 + 50 * p.cos( p.frameCount * 0.03f );
						
			// DRAW CACHEFLOWE LOGO... "c" radii : 25, 55   outer ring radii: 89, 118
			float logoThickness = ( _audioData.getFFT().averages[0] + _audioData.getFFT().averages[1] + _audioData.getFFT().averages[2] ) * 4000;
			float logoAlpha = ( _mode == MODE_WIREFRAME ) ? 0 : _fillAlpha;
			if( _mode == MODE_WIREFRAME ) p.stroke( _curColor.colorInt() );
			CacheFloweLogo.drawCacheFloweLogo( p, 5, logoThickness, _curColor.colorIntWithAlpha(logoAlpha, 0), _curColor.colorIntWithAlpha(logoAlpha, wallOffset) );		
			
			int outerDiscRadius = 29;
			int outerDiscStartRadius = 89;
			float discSpacing = 6000 + 5000 * p.sin(p.frameCount * 0.01f);
			
			for( int i = 0; i < NUM_RINGS; i++ )
			{
				int ringSpacingIndex = i+1;
				
				float ringEQVal = _audioData.getFFT().averages[i + 5];
				float ringAlpha = ( _mode == MODE_ALPHA ) ? ringEQVal : _fillAlpha;
				
				if( _mode == MODE_WIREFRAME ) p.stroke( _ringColors[i].colorInt() );
				
				// draw disc, with thickness based on eq 
				float eqThickness = ( ringEQVal * 6 ) * ( 2000 + 10000 * i );	// (i/NUM_RINGS)
				float innerRadius = outerDiscStartRadius + discSpacing * ringSpacingIndex;
				p.pushMatrix();
				if( _spinMode == SPINMODE_Y ) {
					p.rotateY( i * (2*p.PI)/NUM_RINGS );
				}
				if( _spinMode == SPINMODE_XY ) {
					p.rotateY( i * (2*p.PI)/NUM_RINGS );
					p.rotateZ( i * (2*p.PI)/NUM_RINGS );
				}

				Shapes.drawDisc3D( p, innerRadius * scale, ( innerRadius + outerDiscRadius ) * scale, eqThickness, discPrecision, _ringColors[i].colorIntWithAlpha(ringAlpha, 0), _ringColors[i].colorIntWithAlpha(ringAlpha, wallOffset) );
				p.popMatrix();
				
				// draw orbiting star per ring
				p.pushMatrix();
				p.fill( _ringColors[i].colorIntWithAlpha(ringAlpha, 0) );
				float starX = innerRadius * scale * p.sin( p.frameCount * ringSpacingIndex * 0.01f );
				float starY = innerRadius * scale * p.cos( p.frameCount * ringSpacingIndex * 0.01f );
				p.translate( starX, starY, 0 );
				p.rotateZ( i * (2*p.PI)/NUM_RINGS );
				p.rotateY( i * (2*p.PI)/NUM_RINGS );
				Shapes.drawStar( p, 5f, 50f * ringEQVal, 10f, 50 + 50 * ringEQVal, 0f);
				p.popMatrix();
			}
			
			// update camera
			_camera.update();
						
			// lets us use the keyboard to funk it up
			if( p.keyPressed ) handleKeyboardInput();
		}

		public void handleKeyboardInput()
		{
			if ( p.key == 'm' || p.key == 'M' || p.midi.midiPadIsOn( MidiWrapper.PAD_04 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_04 ) == 1 ) {
				pickMode();
			}
			if ( p.key == 'c' || p.key == 'C' || p.midi.midiPadIsOn( MidiWrapper.PAD_01 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_01 ) == 1 ) {
				newColor();
			}
			if ( p.key == 'v' || p.key == 'V' || p.midi.midiPadIsOn( MidiWrapper.PAD_02 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_02 ) == 1 ) {
				newCamera();
			}
			if ( p.key == 'f' || p.key == 'F' || p.midi.midiPadIsOn( MidiWrapper.PAD_05 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_05 ) == 1 ) {
				newSpinMode();
			}
			if ( p.key == 'l' || p.key == 'L' || p.midi.midiPadIsOn( MidiWrapper.PAD_08 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_08 ) == 1 ) {
				newDrawMode();
			}
			if ( p.key == 'b' || p.key == 'B' || p.midi.midiPadIsOn( MidiWrapper.PAD_03 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_03 ) == 1 ) {
				shiftRingColorArray();
			}
		}
		
		void pickMode()
		{
			newColor();
			newDrawMode();
			newCamera();
			newSpinMode();
		}

		void newColor()
		{
			// randomize base color
			int newColor = p.round( p.random(0,3) );
			switch( newColor )
			{
				case 0 :
					_curColor = _yellow;
					break;
				case 1 :
					_curColor = _red;
					break;
				case 2 :
					_curColor = _blue;
					break;
				case 3 :
					_curColor = _white;
					break;
			}

			// set rings' colors
			for(int i = 0; i < NUM_RINGS; i++)
			{
				_ringColors[i].r = _curColor.r; 
				_ringColors[i].g = _curColor.g; 
				_ringColors[i].b = _curColor.b;   
			}
		}
		
		void newDrawMode()
		{
			// randomize draw mode
			_mode = p.round( p.random( 0, NUM_MODES - 1 ) );
			switch( _mode )
			{
				case MODE_ALPHA :
					_fillAlpha = 0.5f;
					p.noStroke();
					break;
				case MODE_SOLID :
					_fillAlpha = 1;
					p.noStroke();
					break;
				case MODE_WIREFRAME :
					_fillAlpha = 0;
					p.noFill();
					break;
			}
		}
		
		void newSpinMode() {
			_spinMode = p.round( p.random( 0, NUM_SPIN_MODES - 1 ) );
		}
		
		public void newCamera() 
		{
			// set up camera
			_camera = new CameraOscillate( p, 0, 0, (int)p.random(-600f, 600f), (int)p.random(-2000f, 2000f) );
		}
		
		protected void shiftRingColorArray() 
		{
			// set rings' colors
			for(int i = NUM_RINGS - 1; i > 0; i--)
			{
				_ringColors[i].r = _ringColors[i-1].r; 
				_ringColors[i].g = _ringColors[i-1].g; 
				_ringColors[i].b = _ringColors[i-1].b; 
			}
			
			// set new inner ring
			int newColor = p.round( p.random(0,3) );
			ColorHax newColorObj = null;
			switch( newColor )
			{
				case 0 :
					newColorObj = _yellow;
					break;
				case 1 :
					newColorObj = _red;
					break;
				case 2 :
					newColorObj = _blue;
					break;
				case 3 :
					newColorObj = _white;
					break;
			}

			_ringColors[0].r = newColorObj.r;
			_ringColors[0].g = newColorObj.g;
			_ringColors[0].b = newColorObj.b;
		}
		
		public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
		{

		}

		protected void drawWaveform ()
		{
			// draw waveform
			p.pushMatrix();
			p.translate( 0, 0, 256 );
			p.rotateY(p.radians(90));
			p.stroke(255/255f, 249/255f, 0);
			int interp = (int)p.max( 0, (( ( p.millis() - p._audioInput._myInput.bufferStartTime)/(float)p._audioInput._myInput.duration) * p._audioInput._myInput.size));
			//p.println(p._audioInput._myInput.buffer2.length);
			for (int i=0;i<p._audioInput._bufferSize;i++) {
				int segmentLength = i;
				
				float left=0;
				float right=0;
			
				if (i+interp+1<p._audioInput._myInput.buffer2.length) {
					left -= p._audioInput._myInput.buffer2[segmentLength+interp]*150.0;
					right -= p._audioInput._myInput.buffer2[segmentLength*2+interp]*150.0;
				}
				
				p.line(i,left,i+1,right);
			}
			p.popMatrix();
		}
		
	}