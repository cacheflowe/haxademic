package com.haxademic.demo.media.audio.playback;

import com.haxademic.core.app.PAppletHax;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.ugens.FilePlayer;
import ddf.minim.ugens.Gain;
import ddf.minim.ugens.Sampler;
import ddf.minim.ugens.TickRate;

public class Demo_Minim_TickRate_PitchBend
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Minim minim;
	protected TickRate rateControl;
	protected FilePlayer filePlayer;
	protected AudioOutput out;
	protected Sampler _sampler;

	protected String fileName = "D:\\workspace\\haxademic-cacheflowe\\data\\audio\\communichords\\bass\\operator-hiphop-sub-bass.aif.wav";
	

	protected void firstFrame() {
		// init mini,
		minim = new Minim(this);
		
		// init FilePlayer
		filePlayer = new FilePlayer( minim.loadFileStream(fileName) );
//		filePlayer.loop();
		
		// OR Sampler!
		_sampler = new Sampler(fileName, 1, minim);
		_sampler.trigger();
		_sampler.looping = true;
		Gain gainEfx = new Gain(0f);

		// this creates a TickRate UGen with the default playback speed of 1.
		// ie, it will sound as if the file is patched directly to the output
		rateControl = new TickRate(1.f);
		rateControl.setInterpolation( true );
		
		// patch the file player through the TickRate to the output.
		out = minim.getLineOut();
//		filePlayer.patch(rateControl).patch(out);
		_sampler.patch(rateControl).patch(gainEfx).patch(out);
	}

	protected void drawApp() {
		// clear screen
		p.background(0);
		p.stroke( 255 );
		
		// change the rate control value based on mouse position
		float rate = map(mouseX, 0, width, 0.0f, 3.f);
		rateControl.value.setLastValue(rate);

		// erase the window to black
		// draw using a white stroke
		// draw the waveforms
		for( int i = 0; i < out.bufferSize() - 1; i++ )
		{
			// find the x position of each buffer value
			float x1  =  map( i, 0, out.bufferSize(), 0, width );
			float x2  =  map( i+1, 0, out.bufferSize(), 0, width );
			// draw a line from one buffer position to the next for both channels
			line( x1, 50  - out.left.get(i)*50,  x2, 50  - out.left.get(i+1)*50);
			line( x1, 150 - out.right.get(i)*50, x2, 150 - out.right.get(i+1)*50);
		}  
	}

	/*
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'i') {
			// with interpolation on, it will sound as a record would when slowed down or sped up
			rateControl.setInterpolation( true );
		}
	}

	public void keyReleased() {
		super.keyReleased();
		if ( key == 'i') {
			// with interpolation off, the sound will become "crunchy" when playback is slowed down
			rateControl.setInterpolation( false );
		}
	}
	*/
}
