package com.haxademic.demo.media.audio.playback;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;

import beads.AudioContext;
import beads.Buffer;
import beads.Gain;
import beads.WavePlayer;

public class Demo_Beads_ManualBufferWrite
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 512 );
		Config.setProperty( AppSettings.HEIGHT, 520 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}
	
	int count = 1;
	WavePlayer[] wp;
	
	protected void firstFrame() {
		AudioUtil.setPrimaryMixer();
		AudioContext ac = AudioUtil.getBeadsContext();
		AudioIn.instance(new AudioInputBeads(ac));
		
		// initialize the arrays
		wp = new WavePlayer[count];
		Gain[] g = new Gain[count];
		
		// use a loop to set up each WavePlayer
		for( int i = 0; i < count; i++ )
		{
			// create the WavePlayer and the Gain - use math to set frequence / volume
			// try changing Buffer.SINE to Buffer.SQUARE
			wp[i] = new WavePlayer(ac, 440.0f * (i+1), Buffer.SINE);
			g[i] = new Gain(ac, 1, 1.0f / (i+1));
			
			// connect the WavePlayer to the Gain, and the Gain to ac.out
			g[i].addInput(wp[i]);
			ac.out.addInput(g[i]);
		}
		
		ac.start();
	}
	
	protected void drawApp() {
		p.background(0);
//		player.setVolume(soundbed, Mouse.xNorm);
		
		for( int i = 0; i < count; i++ ) {
//			wp[i].setFrequency(120 + 40f * P.sin(p.frameCount * 0.001f));
			wp[i].setFrequency(40 + 40f * p.noise(p.frameCount * 0.001f)); //  + 5 * P.sin(p.frameCount * 0.1f)
			
			float[] buff = wp[i].getBuffer().buf;
			for (int j = 0; j < buff.length; j++) {
				float buffProg = (float)j / (float)buff.length;
				float noiseVal = -1f + 2f * p.noise(p.frameCount * 0.01f + buffProg * 3f);
				float sinVal = P.sin(p.frameCount * 0.02f + buffProg * P.TWO_PI * 1f);
				float sinVal2 = P.sin(p.frameCount * 0.04f + buffProg * P.TWO_PI * 3f);
//				float sinVal3 = Math.signum(P.sin(p.frameCount * 0.03f + buffProg * P.TWO_PI * 1f)) / 2f;
				float sinVal3 = P.sin(p.frameCount * 0.03f + buffProg * P.TWO_PI * 4f);
				buff[j] = P.lerp(noiseVal, sinVal, 0.7f);
				buff[j] = P.lerp(buff[j], sinVal2, 0.27f);
				buff[j] = P.lerp(buff[j], sinVal3, 0.39f);
				
				// fade out at ends
				if(j < 500) buff[j] = P.lerp(0, buff[j], j / 500f);
				if(j >= buff.length - 1500) buff[j] = P.lerp(buff[j], 0, P.map(j, buff.length - 1500, buff.length - 1, 0, 1));
			}
//			ArrayUtil.crossfadeEnds(buff, 0.2f);
			
			
			// debug draw
			p.rect(0, 300, p.width, 1);
			for (int j = 0; j < buff.length; j++) {
				p.stroke(255);
				p.point(j/10f, 300 + 100f * buff[j]);
			}
		}
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') {
			// set some index of the wave buffer to zero
			for( int i = 0; i < count; i++ )
			{
				wp[i].setFrequency(100);
				float[] buff = wp[i].getBuffer().buf;
				for (int j = 0; j < buff.length; j++) {
					float buffProg = (float)j / (float)buff.length;
					float noiseVal = -1f + 2f * p.noise(p.frameCount + buffProg * 10f);
					float sinVal = P.sin(buffProg * P.TWO_PI * 1f);
					float sinVal2 = P.sin(buffProg * P.TWO_PI * 3f);
					buff[j] = P.lerp(noiseVal, sinVal, 0.7f);
					buff[j] = P.lerp(buff[j], sinVal2, 0.4f);
				}
			}
		}
	}
}
