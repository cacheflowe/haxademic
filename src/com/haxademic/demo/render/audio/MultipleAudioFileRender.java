package com.haxademic.demo.render.audio;

import com.haxademic.core.media.audio.deprecated.AudioInputWrapper;
import com.haxademic.core.render.VideoRenderer;

import krister.Ess.AudioInput;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

/**
 * 
 * @author justin
 *
 */
public class MultipleAudioFileRender
	extends PApplet
{
	// global vars
	protected int _fps = 60;
	protected int _numWavs = 6;
	protected EQColumn[] _eqColumn;
	protected AudioInputWrapper[] _audioInputs;
	protected AudioInputWrapper _audioInput1;
	protected AudioInputWrapper _audioInput2;
	protected AudioInputWrapper _audioInput3;
	protected AudioInputWrapper _audioInput4;
	protected AudioInputWrapper _audioInput5;
	protected AudioInputWrapper _audioInput6;
	protected VideoRenderer[] _renderers;
	protected VideoRenderer _renderFinal;
	protected VideoRenderer _render1;
	protected VideoRenderer _render2;
	protected VideoRenderer _render3;
	protected VideoRenderer _render4;
	protected VideoRenderer _render5;
	protected VideoRenderer _render6;

	public void setup ()
	{
		// set up stage and drawing properties
		size( 600, 600, PConstants.P2D );
		frameRate( _fps );
		colorMode( PConstants.RGB, 1, 1, 1, 1 );
		imageMode( PConstants.CORNER );
		background( 0 );
		smooth();
		noStroke();
		
		// set up inputs
		float dampening = .35f;
		_audioInput1 = new AudioInputWrapper( this, false );
		_audioInput1.setNumAverages( 1 );
		_audioInput1.setDampening( dampening );
		_audioInput2 = new AudioInputWrapper( this, false );
		_audioInput2.setNumAverages( 1 );
		_audioInput2.setDampening( dampening );
		_audioInput3 = new AudioInputWrapper( this, false );
		_audioInput3.setNumAverages( 1 );
		_audioInput3.setDampening( dampening );
		_audioInput4 = new AudioInputWrapper( this, false );
		_audioInput4.setNumAverages( 1 );
		_audioInput4.setDampening( dampening );
		_audioInput5 = new AudioInputWrapper( this, false );
		_audioInput5.setNumAverages( 1 );
		_audioInput5.setDampening( dampening );
		_audioInput6 = new AudioInputWrapper( this, false );
		_audioInput6.setNumAverages( 1 );
		_audioInput6.setDampening( dampening );
		
		// store inputs for cycling 
		_audioInputs = new AudioInputWrapper[ _numWavs ];
		_audioInputs[0] = _audioInput1;
		_audioInputs[1] = _audioInput2;
		_audioInputs[2] = _audioInput3;
		_audioInputs[3] = _audioInput4;
		_audioInputs[4] = _audioInput5;
		_audioInputs[5] = _audioInput6;
		
		// set up renders
		VideoRenderer.setOutputImages();
		_render1 = new VideoRenderer( _fps, "bin/output/" );
		_render2 = new VideoRenderer( _fps, "bin/output/" );
		_render3 = new VideoRenderer( _fps, "bin/output/" );
		_render4 = new VideoRenderer( _fps, "bin/output/" );
		_render5 = new VideoRenderer( _fps, "bin/output/" );
		_render6 = new VideoRenderer( _fps, "bin/output/" );
		VideoRenderer.setOutputVideo();
		_renderFinal = new VideoRenderer( _fps, "bin/output/" );
//		_renderFinal.startRendererForAudio( "wav/ringtones/ringtone-01.wav", new AudioInputWrapper( this, false ) );
		
		// store renderers for cycling 
		_renderers = new VideoRenderer[ _numWavs ];
		_renderers[0] = _render1;
		_renderers[1] = _render2;
		_renderers[2] = _render3;
		_renderers[3] = _render4;
		_renderers[4] = _render5;
		_renderers[5] = _render6;
		
		// create graphical columns and fire up renderer for each
		_eqColumn = new EQColumn[ _numWavs ];
		for( int i = 0; i < _numWavs; i++ )
		{
			// create graphics for each part
			_eqColumn[i] = new EQColumn( i, "img/ringtones/0"+(i+1)+".png" );
			
			// create corresponding audio inputs
//			_renderers[i].startRendererForAudio( "wav/ringtones/ringtone-0"+(i+1)+".wav", _audioInputs[i] );
		}
		
	}

	public void draw() 
	{
		// redraw white background
		background(1);
		
		// update audio inputs
		for (int i = 0; i < _numWavs; i++) 
		{
			_renderers[i].analyzeAudio();
			_renderers[i].renderFrame();
		}
		
		// update EQ columns
		for (int i = 0; i < _numWavs; i++) 
		{
			_eqColumn[i].update( _audioInputs[i].getFFT().averages[0] );
		}
		
		// render it
		_renderFinal.renderFrame();
	}
	
	// EQColumn object
	class EQColumn 
	{
		protected PImage _image;
		protected int _index;
		protected float _eqSegment;

		public EQColumn( int index, String img ) 
		{
			_index = index;
			_image = loadImage( img );
			_eqSegment = 1f / 6f;
		} 

		public void update( float vol ) 
		{
			vol *= 4;
			
			// calc how many to draw
			int numToDraw = PApplet.floor( vol / _eqSegment ); 
			// float remainder = vol - numToDraw * _eqSegment;
			
			// draw whole steps
			tint( 1, 1 );
			for( int i = 0; i < numToDraw; i ++ )
			{
				pushMatrix();
				image( _image, _index * 100, 500 - i*100, 100, 100 );
				popMatrix();
			}
			
			// draw last
			tint( 1, vol );
			pushMatrix();
			image( _image, _index * 100, 500 - numToDraw*100, 100, 100 );
			popMatrix();
			
		}
	}
	
	// PApp-level listener for audio input data ------------------------ 
	public void audioInputData( AudioInput theInput ) 
	{
		if( theInput == _audioInput1.getAudioInput() ) _audioInput1.getFFT().getSpectrum(theInput);
		if( theInput == _audioInput2.getAudioInput() ) _audioInput2.getFFT().getSpectrum(theInput);
		if( theInput == _audioInput3.getAudioInput() ) _audioInput3.getFFT().getSpectrum(theInput);
		if( theInput == _audioInput4.getAudioInput() ) _audioInput4.getFFT().getSpectrum(theInput);
		if( theInput == _audioInput5.getAudioInput() ) _audioInput5.getFFT().getSpectrum(theInput);
		if( theInput == _audioInput6.getAudioInput() ) _audioInput6.getFFT().getSpectrum(theInput);
	}
}
