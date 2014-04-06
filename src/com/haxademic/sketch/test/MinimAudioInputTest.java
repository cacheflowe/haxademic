package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import ddf.minim.analysis.WindowFunction;

@SuppressWarnings("serial")
public class MinimAudioInputTest 
extends PAppletHax {

	Minim minim;
	AudioInput in;
	FFT fft;
	String windowName;
	BeatDetect beat;
	float eRadius;


	protected void overridePropsFile() {
		// _appConfig.setProperty( "width", "1200" );
	}

	public void setup() {
		super.setup();
		minim = new Minim(this);
		in = minim.getLineIn();

		fft = new FFT(in.bufferSize(), in.sampleRate());
		windowName = "Rectangular Window";

		// a beat detection object song SOUND_ENERGY mode with a sensitivity of 10 milliseconds
		beat = new BeatDetect();
		beat.setSensitivity(300);
		ellipseMode(RADIUS);
		eRadius = 20;
	}

	public void drawApp() {
		background(0);
		stroke(255);

		// draw the waveforms so we can see what we are monitoring --------------------
		for(int i = 0; i < in.bufferSize() - 1; i++) {
			line( i, 50 + in.left.get(i)*50, i+1, 50 + in.left.get(i+1)*50 );
			line( i, 150 + in.right.get(i)*50, i+1, 150 + in.right.get(i+1)*50 );
		}

		// draw the spectrum -------------------------------------------------------
		for(int i = 0; i < fft.specSize(); i++)
		{
			line(i, height/2, i, height/2 - fft.getBand(i) * 10);
		}



		// perform a forward FFT on the samples in jingle's left buffer ---------
		// note that if jingle were a MONO file, 
		// this would be the same as using jingle.right or jingle.left
		fft.forward(in.mix);
		for(int i = 0; i < fft.specSize(); i++)
		{
			// convert the magnitude to a DB value. 
			// this means values will range roughly from 0 for the loudest
			// bands to some negative value.
			float bandDB = 20 * log( 2 * fft.getBand(i) / fft.timeSize() );
			// so then we want to map our DB value to the height of the window
			// given some reasonable range
			float bandHeight = map( bandDB, 0, -150, 0, height );
			line(i, height, i, bandHeight );
		}
		fill(255);
		// keep us informed about the window being used
		text("The window being used is: " + windowName, 5, 20);


		// beat detection ----------------------------------------------------
		beat.detect(in.mix);
		float a = map(eRadius, 20, 80, 60, 255);
		fill(60, 255, 0, a);
		if ( beat.isOnset() ) eRadius = 80;
		ellipse(width/2, height/2, eRadius, eRadius);
		eRadius *= 0.95;
		if ( eRadius < 20 ) eRadius = 20;


		String monitoringState = in.isMonitoring() ? "enabled" : "disabled";
		text( "Input monitoring is currently " + monitoringState + ".", 5, 15 );
	}

	public void keyPressed()
	{
		if ( key == 'm' || key == 'M' )
		{
			if ( in.isMonitoring() )
			{
				in.disableMonitoring();
			}
			else
			{
				in.enableMonitoring();
			}
		}
	}

	public void keyReleased()
	{
		WindowFunction newWindow = FFT.NONE;

		if ( key == '1' ) 
		{
			newWindow = FFT.BARTLETT;
		}
		else if ( key == '2' )
		{
			newWindow = FFT.BARTLETTHANN;
		}
		else if ( key == '3' )
		{
			newWindow = FFT.BLACKMAN;
		}
		else if ( key == '4' )
		{
			newWindow = FFT.COSINE;
		}
		else if ( key == '5' )
		{
			newWindow = FFT.GAUSS;
		}
		else if ( key == '6' )
		{
			newWindow = FFT.HAMMING;
		}
		else if ( key == '7' )
		{
			newWindow = FFT.HANN;
		}
		else if ( key == '8' )
		{
			newWindow = FFT.LANCZOS;
		}
		else if ( key == '9' )
		{
			newWindow = FFT.TRIANGULAR;
		}

		fft.window( newWindow );
		windowName = newWindow.toString();
	}
}

