package com.haxademic.sketch.audio.minim;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.file.FileUtil;

import ddf.minim.AudioInput;
import ddf.minim.AudioOutput;
import ddf.minim.AudioRecorder;
import ddf.minim.Minim;
import ddf.minim.ugens.FilePlayer;

public class AudioRecordAndPlayback 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	Minim minim;

	//for recording
	AudioInput in;
	AudioRecorder recorder;
	boolean recorded;

	//for playing back
	AudioOutput out;
	FilePlayer player;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "800" );
		Config.setProperty( AppSettings.HEIGHT, "600" );
	}


	public void firstFrame() {
	
		
		minim = new Minim(this);

		// get a stereo line-in: sample buffer length of 2048
		// default sample rate is 44100, default bit depth is 16
		in = minim.getLineIn(Minim.STEREO, 2048);

		// create an AudioRecorder that will record from in to the filename specified.
		// the file will be located in the sketch's main folder.
		recorder = minim.createRecorder(in, FileUtil.haxademicOutputPath() + "audio/myrecording.wav");

		// get an output we can playback the recording on
		out = minim.getLineOut( Minim.STEREO );

		textFont(createFont("Arial", 12));
	}

	public void drawApp() {
		background(0); 
		stroke(255);
		// draw the waveforms
		// the values returned by left.get() and right.get() will be between -1 and 1,
		// so we need to scale them up to see the waveform
		for(int i = 0; i < in.left.size()-1; i++)
		{
			line(i, 50 + in.left.get(i)*50, i+1, 50 + in.left.get(i+1)*50);
			line(i, 150 + in.right.get(i)*50, i+1, 150 + in.right.get(i+1)*50);
		}

		if ( recorder.isRecording() )
		{
			text("Now recording, press the r key to stop recording.", 5, 15);
		}
		else if ( !recorded )
		{
			text("Press the r key to start recording.", 5, 15);
		}
		else
		{
			text("Press the s key to save the recording to disk and play it back in the sketch.", 5, 15);
		}
	}

	public void keyReleased()
	{
		if ( !recorded && key == 'r' ) 
		{
			// to indicate that you want to start or stop capturing audio data, 
			// you must callstartRecording() and stopRecording() on the AudioRecorder object. 
			// You can start and stop as many times as you like, the audio data will 
			// be appended to the end of to the end of the file. 
			if ( recorder.isRecording() ) 
			{
				recorder.endRecord();
				recorded = true;
			}
			else 
			{
				recorder.beginRecord();
			}
		}
		if ( recorded && key == 's' )
		{
			// we've filled the file out buffer, 
			// now write it to a file of the type we specified in setup
			// in the case of buffered recording, 
			// this will appear to freeze the sketch for sometime, if the buffer is large
			// in the case of streamed recording, 
			// it will not freeze as the data is already in the file and all that is being done
			// is closing the file.
			// save returns the recorded audio in an AudioRecordingStream, 
			// which we can then play with a FilePlayer
			if ( player != null )
			{
				player.unpatch( out );
				player.close();
			}
			player = new FilePlayer( recorder.save() );
			player.patch( out );
			player.play();
		}
	}
}

