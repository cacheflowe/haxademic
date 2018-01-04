package com.haxademic.sketch.audio.minim;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import ddf.minim.analysis.WindowFunction;

public class MinimAudioInputTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	Minim minim;
	AudioInput in;
	FFT fft;
	String windowName;
	BeatDetect beat;
	float eRadius;
	float spectrumDampened[];
	float spectrumDb[];
	float dampening = 0.8f;

	protected void overridePropsFile() {
		// p.appConfig.setProperty( AppSettings.WIDTH, "1200" );
	}

	public void setup() {
		super.setup();
		minim = new Minim(this);
		in = minim.getLineIn();

		fft = new FFT(in.bufferSize(), in.sampleRate());
		spectrumDampened = new float[in.bufferSize()];
		spectrumDb = new float[in.bufferSize()];
		for(int i = 0; i < fft.specSize(); i++) {
			spectrumDampened[i] = 0;
			spectrumDb[i] = 0;
		}

		windowName = "Rectangular Window";

		// a beat detection object song SOUND_ENERGY mode with a sensitivity of 10 milliseconds
		beat = new BeatDetect();
		beat.setSensitivity(300);
		ellipseMode(RADIUS);
		eRadius = 20;
		
		
//		in.shiftGain(0, -60f, 5000);
	}

	public void drawApp() {
		background(0);
		stroke(255);

//		float newGain = P.map(p.mouseX, 0, p.width, -500, 10f);
//		if(in.hasControl(Controller.GAIN)) {
//			P.println(newGain);
//			in.setGain( newGain );
//		}
		
		// draw the waveforms so we can see what we are monitoring --------------------
		for(int i = 0; i < in.bufferSize() - 1; i++) {
			line( i, 50 + in.left.get(i)*50, i+1, 50 + in.left.get(i+1)*50 );
			line( i, 150 + in.right.get(i)*50, i+1, 150 + in.right.get(i+1)*50 );
		}

		// compute spectrum
		fft.forward(in.mix);
		
		// draw the spectrum -------------------------------------------------------
		for(int i = 0; i < fft.specSize(); i++) {
			spectrumDampened[i] = ( spectrumDampened[i] < fft.getBand(i) ) ? fft.getBand(i) : spectrumDampened[i] * dampening;
			spectrumDb[i] = 10 - -1f * log( 1 * spectrumDampened[i] / fft.timeSize() );
			// line(i, height/2, i, height/2 - fft.getBand(i) * 10);
			line(i, height/2, i, height/2 - spectrumDampened[i] * 10f);
			line(i, height*0.8f, i, height*0.8f - spectrumDb[i] * 10f);
		}

		// marius normalized vals
//		float volNow = P.max(spectrumDampened);

		// apply damping to find the new volume
		float damper = map(mouseY, 0f,height, 0.001f,1);
		float volFFT = 1;//volFFT*(1-damper)+volNow*damper;
		volFFT=max(0.1f,volFFT);

		// calculate the new normalized values
		for(int i=0; i<spectrumDampened.length; i++) {
			float newVal=min(1, spectrumDampened[i]/volFFT);

			//		    if(doShaper) newVal=shaper(newVal);

			spectrumDampened[i]=spectrumDampened[i]*(1-damper)+newVal*damper;
			line(i, height/3, i, height/3 - spectrumDampened[i] * 10f);
		}



		for(int i = 0; i < fft.specSize(); i++) {
			line(i, height, i, height -  P.p.audioIn.getEqBand( i ) * 10f );
		}

//		// perform a forward FFT on the samples in jingle's left buffer ---------
//		// note that if jingle were a MONO file, 
//		// this would be the same as using jingle.right or jingle.left
//		for(int i = 0; i < fft.specSize(); i++)
//		{
////			// convert the magnitude to a DB value. 
////			// this means values will range roughly from 0 for the loudest
////			// bands to some negative value.
//			float bandDB = 20 * log( 2 * spectrumDampened[i] / fft.timeSize() );
////			
////			// so then we want to map our DB value to the height of the window
////			// given some reasonable range
//			float bandHeight = map( bandDB, 0, -150, 0, height );
//			line(i, height, i, bandHeight );
//		}
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
		if ( key == ',' ) {
			in.setGain( -20.f );
			P.println(in.getGain());
		} else if ( key == '.' ) {
			in.setGain( -90.f );
			P.println(in.getGain());
		}
		
		
		
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

