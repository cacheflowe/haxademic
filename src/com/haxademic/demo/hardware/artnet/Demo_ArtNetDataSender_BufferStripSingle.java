package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleFactory;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.hardware.dmx.artnet.LightStripBuffer;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ArtNetDataSender_BufferStripSingle
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetDataSender artNetDataSender;
	protected int numPixels = 600;
	protected PGraphics debugPG;
	protected LightStripBuffer lightStripBuffer;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame() {
		AudioIn.instance(AudioInputLibrary.ESS);
		
		// build artnet obj
		artNetDataSender = new ArtNetDataSender("192.168.1.100", 0, numPixels);
		
		// build debug buffer for visualizing artnet data array
		debugPG = PG.newPG(128, 128);
		DebugView.setTexture("debugPG", debugPG);
		
		// build main buffer
		lightStripBuffer = new BufferCustom(artNetDataSender, numPixels, 0, numPixels-1, 40);
	}

	protected void drawApp() {
		background(0);
		
		// should normally be called in a pre() draw method!
		// draw info buffer
		lightStripBuffer.draw();
		lightStripBuffer.setData();
		// send to lighting hardware
		artNetDataSender.send();
		artNetDataSender.drawDebug(debugPG, true);
	}
	
	////////////////////////////////
	// Custom light 
	////////////////////////////////
	
	public class BufferCustom extends LightStripBuffer {

		protected ParticleSystem particles;

		public BufferCustom(ArtNetDataSender artNetDataSender, int width, int indexStart, int indexEnd, int bufferH) { 
			super(artNetDataSender, width, indexStart, indexEnd, bufferH); 
			
			// create particle system with basic particle texture
			PImage[] particleImages = new PImage[] { DemoAssets.particle() };
			particles = new ParticleSystem(new ParticleFactory(particleImages));
		}

		public void drawCustom() {
			PG.setDrawCenter(buffer);
			
			// draw a bunch of particles on the beat
			if(AudioIn.isBeat()) {
				for(int i=0; i < 10; i++) {
					Particle particle = particles.launchParticle(0, 0, 0);
					particle
						.setGravityRange(0, 0, 0, 0, 0, 0)
						.setLifespanRange(10, 30)
						.setSizeRange(10, 50)
						.setSpeedRange(-0.5f, 0.5f, 0, 0, 0, 0)
						.setColor(p.color(P.p.random(255), P.p.random(255), P.p.random(255)))
						.launch(P.p.random(0, numPixels), 0, 0);	// .launch() to set params properly
				}
			}
			particles.updateAndDrawParticles(buffer);
		}
		
	}

}