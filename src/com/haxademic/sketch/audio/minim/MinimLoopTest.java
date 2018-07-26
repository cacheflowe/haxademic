package com.haxademic.sketch.audio.minim;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.file.FileUtil;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;

public class MinimLoopTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	Minim minim;
	protected AudioPlayer _sound;
	int loopBegin;
	int loopEnd;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "400" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "400" );
	}

	public void setup() {
		super.setup();	
		minim = new Minim(this);

		_sound = minim.loadFile( FileUtil.getHaxademicDataPath() + "audio/test-loop.wav", 512 );
		_sound.loop();
	}


	public void drawApp() {
		p.background(0);
		fill(255);  
		text("Loop Count: " + _sound.loopCount(), 5, 20);
		text("Looping: " + _sound.isLooping(), 5, 40);
		text("Playing: " + _sound.isPlaying(), 5, 60);
		int p = _sound.position();
		int l = _sound.length();
		text("Position: " + p, 5, 80);
		text("Length: " + l, 5, 100);
		float x = map(p, 0, l, 0, width);
		stroke(255);
		line(x, height/2 - 50, x, height/2 + 50);
		float lbx = map(loopBegin, 0, _sound.length(), 0, width);
		float lex = map(loopEnd, 0, _sound.length(), 0, width);
		stroke(0, 255, 0);
		line(lbx, 0, lbx, height);
		stroke(255, 0, 0);
		line(lex, 0, lex, height);

	}

	public void mousePressed()
	{
		int ms = (int)map(mouseX, 0, width, 0, _sound.length());
		if ( mouseButton == RIGHT )
		{
			_sound.setLoopPoints(loopBegin, ms);
			loopEnd = ms;
		}
		else
		{
			_sound.setLoopPoints(ms, loopEnd);
			loopBegin = ms;
		}
	}


}
