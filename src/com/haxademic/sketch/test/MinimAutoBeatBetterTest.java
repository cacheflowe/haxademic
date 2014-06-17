package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.FileUtil;

import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Toggle;
import ddf.minim.AudioOutput;
import ddf.minim.ugens.Instrument;
import ddf.minim.ugens.Sampler;

@SuppressWarnings("serial")
public class MinimAutoBeatBetterTest 
extends PAppletHax {
	
	// from: // https://github.com/ddf/Minim/blob/master/examples/Advanced/DrumMachine/DrumMachine.pde
	
	Sampler     kick;
	Sampler     snare;
	Sampler     hat;

	ControlP5 gui;
	boolean[] hatRow = new boolean[16];
	boolean[] snrRow = new boolean[16];
	boolean[] kikRow = new boolean[16];

	public int bpm;

	int beat; // which beat we're on
	
	AudioOutput out;


	protected void overridePropsFile() {
	}
	
	// here's an Instrument implementation that we use 
	// to trigger Samplers every sixteenth note. 
	// Notice how we get away with using only one instance
	// of this class to have endless beat making by 
	// having the class schedule itself to be played
	// at the end of its noteOff method. 
	class Tick implements Instrument
	{
	  public void noteOn( float dur )
	  {
	    if ( hatRow[beat] ) hat.trigger();
	    if ( snrRow[beat] ) snare.trigger();
	    if ( kikRow[beat] ) kick.trigger();
	  }
	  
	  public void noteOff()
	  {
	    // next beat
	    beat = (beat+1)%16;
	    // set the new tempo
	    out.setTempo( bpm );
	    // play this again right now, with a sixteenth note duration
	    out.playNote( 0, 0.25f, this );
	  }
	}


	public void setup() {
		super.setup();
		
//		_kick = p.minim.loadFile( FileUtil.getHaxademicDataPath() + "audio/kit808/kick.wav", 1024 );
//		_snare = p.minim.loadFile( FileUtil.getHaxademicDataPath() + "audio/kit808/snare.wav", 1024 );
//		_stab = p.minim.loadFile( FileUtil.getHaxademicDataPath() + "audio/drums/janet-stab.wav", 1024 );
//		_bass = p.minim.loadFile( FileUtil.getHaxademicDataPath() + "audio/kit808/bass.wav", 1024 );

//		_beats.add( new BeatSquare(0 * drumPadW, 1 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(1,0), ) );
//		_beats.add( new BeatSquare(1 * drumPadW, 1 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(1,1), "data/audio/kit808/snare.wav") );
//		_beats.add( new BeatSquare(2 * drumPadW, 1 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(1,2), "data/audio/kit808/tom.wav") );
//		
//		_beats.add( new BeatSquare(0 * drumPadW, 2 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(2,0), "data/audio/drums/snare-x10.wav") );
//		_beats.add( new BeatSquare(1 * drumPadW, 2 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(2,1), "data/audio/drums/chirp-11.wav") );
//		_beats.add( new BeatSquare(2 * drumPadW, 2 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(2,2), "data/audio/drums/chirp-18.wav") );
//		_beats.add( new BeatSquare(3 * drumPadW, 2 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(2,3), "data/audio/drums/janet-stab.wav") );

		  out   = minim.getLineOut();
		  
		  // load all of our samples, using 4 voices for each.
		  // this will help ensure we have enough voices to handle even
		  // very fast tempos.
		  kick  = new Sampler( FileUtil.getHaxademicDataPath() + "audio/kit808/kick.wav", 4, minim );
		  snare = new Sampler( FileUtil.getHaxademicDataPath() + "audio/kit808/snare.wav", 4, minim );
		  hat   = new Sampler( FileUtil.getHaxademicDataPath() + "audio/kit808/hi-hat.wav", 4, minim );
		  
		  // patch samplers to the output
		  kick.patch( out );
		  snare.patch( out );
		  hat.patch( out );
		  
		  gui = new ControlP5(this);
		  gui.setColorForeground(color(128, 200));
		  gui.setColorActive(color(255, 0, 0, 200));
		  Toggle h;
		  Toggle s;
		  Toggle k;
		  for (int i = 0; i < 16; i++)
		  {
		    h = gui.addToggle("hat" + i, false, 10+i*24, 50, 14, 30);
		    h.setId(i);
		    h.setLabel("hat");
		    s = gui.addToggle("snr" + i, false, 10+i*24, 100, 14, 30);
		    s.setId(i);
		    s.setLabel("snr");
		    k = gui.addToggle("kik" + i, false, 10+i*24, 150, 14, 30);
		    k.setId(i);
		    k.setLabel("kik");
		  }
		  gui.addNumberbox("bpm", 120, 10, 5, 20, 15);
		  bpm = 120;
		  beat = 0;
		  
		  // start the sequencer
		  out.setTempo( bpm );
		  out.playNote( 0, 0.25f, new Tick() );
		  
		  textFont(createFont("Arial", 16));

	}

	public void drawApp() {
		  background(0);
		  fill(255);
		  //text(frameRate, width - 60, 20);
		  
		  stroke(128);
		  if ( beat % 4 == 0 )
		  {
		    fill(200, 0, 0);
		  }
		  else
		  {
		    fill(0, 200, 0);
		  }
		    
		  // beat marker    
		  rect(10+beat*24, 35, 14, 9);
		  
		  gui.draw();
	}

	public void controlEvent(ControlEvent e)
	{
	  println(e.getController().getLabel() + ": " + e.getController().getValue());
	  if ( e.getController().getLabel() == "hat" )
	  {
	    hatRow[ e.getController().getId() ] = e.getController().getValue() == 0.0 ? false : true;
	  }
	  else if ( e.getController().getLabel() == "snr" )
	  {
	    snrRow[ e.getController().getId() ] = e.getController().getValue() == 0.0 ? false : true;
	  }
	  else if ( e.getController().getLabel() == "kik" )
	  {
	    kikRow[ e.getController().getId() ] = e.getController().getValue() == 0.0 ? false : true;
	  }
	}
}