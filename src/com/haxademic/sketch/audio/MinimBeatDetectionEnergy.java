package com.haxademic.sketch.audio;

import com.haxademic.core.app.PAppletHax;

import ddf.minim.AudioInput;
import ddf.minim.AudioListener;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;

@SuppressWarnings("serial")
public class MinimBeatDetectionEnergy 
extends PAppletHax {

	Minim minim;
	AudioInput in;

	BeatDetect beat;
	BeatListener bl;

	float kickSize, snareSize, hatSize;


	protected void overridePropsFile() {
		// _appConfig.setProperty( "width", "1200" );
	}

	public void setup() {
		super.setup();
		minim = new Minim(this);
		in = minim.getLineIn();


		  // a beat detection object that is FREQ_ENERGY mode that 
		  // expects buffers the length of song's buffer size
		  // and samples captured at songs's sample rate
		  beat = new BeatDetect(in.bufferSize(), in.sampleRate());
		  // set the sensitivity to 300 milliseconds
		  // After a beat has been detected, the algorithm will wait for 300 milliseconds 
		  // before allowing another beat to be reported. You can use this to dampen the 
		  // algorithm if it is giving too many false-positives. The default value is 10, 
		  // which is essentially no damping. If you try to set the sensitivity to a negative value, 
		  // an error will be reported and it will be set to 10 instead. 
		  beat.setSensitivity(50);  
		  kickSize = snareSize = hatSize = 16;
		  // make a new beat listener, so that we won't miss any buffers for the analysis
		  bl = new BeatListener(beat, in);  
		  textFont(createFont("Helvetica", 16));
		  textAlign(CENTER);
	}
	
	class BeatListener implements AudioListener
	{
	  private BeatDetect beat;
	  private AudioInput source;
	  
	  BeatListener(BeatDetect beat, AudioInput source)
	  {
	    this.source = source;
	    this.source.addListener(this);
	    this.beat = beat;
	  }
	  
	  public void samples(float[] samps)
	  {
	    beat.detect(source.mix);
	  }
	  
	  public void samples(float[] sampsL, float[] sampsR)
	  {
	    beat.detect(source.mix);
	  }
	}


	public void drawApp() {
		background(0);
		stroke(255);

		
		
		  background(0);
		  fill(255);
		  if ( beat.isKick() ) kickSize = 32;
		  if ( beat.isSnare() ) snareSize = 32;
		  if ( beat.isHat() ) hatSize = 32;
		  textSize(kickSize);
		  text("KICK", width/4, height/2);
		  textSize(snareSize);
		  text("SNARE", width/2, height/2);
		  textSize(hatSize);
		  text("HAT", 3*width/4, height/2);
		  kickSize = constrain(kickSize * 0.95f, 16, 32);
		  snareSize = constrain(snareSize * 0.95f, 16, 32);
		  hatSize = constrain(hatSize * 0.95f, 16, 32);

	}

}

