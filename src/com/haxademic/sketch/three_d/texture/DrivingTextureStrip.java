package com.haxademic.sketch.three_d.texture;

import java.util.Random;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.image.filters.shaders.BrightnessFilter;
import com.haxademic.core.system.FileUtil;

import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.sound.SoundFile;

public class DrivingTextureStrip 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PImage img;
	PVector position;
	float displayW = 200;
	float textureY;
	float textureSegmentRatio = 0.006f;
	float speed;
	float dirRadians = 0;
	float[] curVertices = {0f,0f,0f,0f};
	float[] lastVertices = null;
	float wrapPadding;
	boolean debugPoints = false;
	boolean debugTexture = false;
	boolean autoControl = true;
	int lastInteractionTime = 0;
	float lastTint = 255;
	
	SoundFile slimeSound;
	SoundFile arcadeSound;
	SoundFile[] incidentalSounds;
	SoundFile[] buttonSounds;
	int incidentalSoundIndex = 0;
	float nextPlayTime = 0;
	
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
		p.appConfig.setProperty( AppSettings.RENDERER, P.P2D );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 2 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 4000 );
	}


	public void setup() {
		super.setup();
		noStroke();
		
		img = loadImage(FileUtil.getFile("images/dino-pattern-3.png"));
		speed = img.height * 4f * textureSegmentRatio;
		textureY = img.height - speed; 
		wrapPadding = displayW * 1.2f;
		position = new PVector(p.width/2, 0);
		
		slimeSound = new SoundFile(this, FileUtil.getFile("audio/intestines/squish-loop.wav"));
		slimeSound.pan(-0.2f);
		slimeSound.loop();
		arcadeSound = new SoundFile(this, FileUtil.getFile("audio/intestines/arcade-ambience.wav"));
		arcadeSound.loop();
		arcadeSound.amp(0.3f);
		arcadeSound.pan(0.8f);
		
		incidentalSounds = new SoundFile[]{
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/8-bit-loop.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/56k-modem.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/aim-in.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/aim-out.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/bad-telephone-number.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/bleeps.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/blip.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/blips.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/blooops.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/fax.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/happy-startup.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/modem-3.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/naturalised-long.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/nokia.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/printer.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/speak-n-spell.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/startup.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/tetris.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/tweeps.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/uh-oh.wav"))
		};
		shuffleArray(incidentalSounds);
		nextPlayTime = p.random(2000, 7000);
		
		buttonSounds = new SoundFile[]{
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/aim-in.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/aim-out.wav")),
//				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/blip.wav")),
//				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/happy-startup.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/startup.wav")),
				new SoundFile(this, FileUtil.getFile("audio/intestines/random-sounds/uh-oh.wav"))
		};
	}
	
	protected void shuffleArray(SoundFile[] array) {
	    int index;
	    Random random = new Random();
	    for (int i = array.length - 1; i > 0; i--) {
	        index = random.nextInt(i + 1);
	        if (index != i) {
	            array[index] = array[i];
	            array[i] = array[index];
	            array[index] = array[i];
	        }
	    }
	}
	
    public void keyEvent(final KeyEvent keyEvent) {
    	P.println("keyEvent", keyEvent);
        switch (keyEvent.getAction()) {
        case KeyEvent.PRESS:
        	P.println(keyEvent.getKeyCode());
            break;
        case KeyEvent.RELEASE:
//            keyUp(keyEvent.getKeyCode());
            break;
        default:
            break;
        }
    }

	
	public void drawApp() {
		if(p.frameCount == 1) background(0);
		noStroke();
		
		// play incidental sounds
		if(p.millis() > nextPlayTime) {
			incidentalSounds[incidentalSoundIndex].stop();
			incidentalSoundIndex++;
			if(incidentalSoundIndex >= incidentalSounds.length) incidentalSoundIndex = 0;
			incidentalSounds[incidentalSoundIndex].amp(0.75f);
			incidentalSounds[incidentalSoundIndex].play();
			nextPlayTime = p.millis() + (incidentalSounds[incidentalSoundIndex].duration() * 1000) + p.random(2000, 7000);
		}
		
		// handle keyboard
		if(p.keyPressed == true) {
			if(p.key == P.CODED) {
				lastInteractionTime = p.millis();
				if (p.keyCode == P.LEFT || p.keyCode == P.UP) {
					dirRadians += 0.03f;
				}
				if (p.keyCode == P.RIGHT || p.keyCode == P.DOWN) {
					dirRadians -= 0.03f;
				} 
			}
			if(p.key == 'z') {
				buttonSounds[0].amp(0.5f);
				buttonSounds[0].play();
			}
			if(p.key == 'x') {
				buttonSounds[1].amp(0.5f);
				buttonSounds[1].play();
			}
			if(p.key == 'b') {
				buttonSounds[2].amp(0.5f);
				buttonSounds[2].play();
			}
			if(p.key == 'a') {
				buttonSounds[3].amp(0.5f);
				buttonSounds[3].play();
			}
		}
		autoControl = (p.millis() > lastInteractionTime + 5000);
		if(autoControl == true) {
			dirRadians += (0.35f + P.sin(p.frameCount/75f) * P.sin(p.frameCount/42f)) * 0.024f;
		}

		
		// move position 
		float ninetyDegrees = P.PI/2f;
		float ninetyDegreesLeft = ninetyDegrees + 0.2f * P.sin(p.frameCount/11f);
		float ninetyDegreesRight = ninetyDegrees + 0.2f * P.cos(p.frameCount/13f);
		float widthOsc = P.abs(P.sin(p.frameCount/18f));
		float curW = displayW/2f + 10f * widthOsc;
		float curTint = 127 + 50f * widthOsc;
		lastTint = curTint;
		
		// update position
		position.x = position.x + P.sin(dirRadians) * speed * 0.5f;
		position.y = position.y + P.cos(dirRadians) * speed * 0.5f;
		curVertices[0] = position.x + P.sin(dirRadians - ninetyDegreesLeft) * curW;
		curVertices[1] = position.y + P.cos(dirRadians - ninetyDegreesLeft) * curW;
		curVertices[2] = position.x + P.sin(dirRadians + ninetyDegreesRight) * curW;
		curVertices[3] = position.y + P.cos(dirRadians + ninetyDegreesRight) * curW;
		
		
		
		// move texture scrubber
		textureY -= speed;
		if(textureY <= 0) {
			textureY = img.height - speed;
		}
		
		// draw current section of texture - go clockwise from top left
		if(lastVertices != null && P.dist(curVertices[0], curVertices[1], lastVertices[0], lastVertices[1]) < 20) { // position.dist(lastPosition) < 20f
			noStroke();
			beginShape();
			texture(img);
			p.tint(curTint);
			vertex(curVertices[0], curVertices[1],  			0, textureY);
			vertex(curVertices[2], curVertices[3],  			img.width, textureY);
			p.tint(lastTint);
			vertex(lastVertices[2], lastVertices[3],			img.width, textureY + speed);
			vertex(lastVertices[0], lastVertices[1], 			0, textureY + speed);
			endShape();
		}
		p.tint(255);
		
		// debug draw current vertices
		if(debugPoints == true) {
			p.stroke(0, 70);
			p.strokeWeight(3);
			
			p.point(position.x, position.y);
			p.point(curVertices[0], curVertices[1]);
			p.point(curVertices[2], curVertices[3]);
			if(lastVertices != null) { 
				p.point(lastVertices[0], lastVertices[1]);
				p.point(lastVertices[2], lastVertices[3]);
			}
		}
		
		
		// constrain position on screen 
		if(position.x < -wrapPadding) position.x = p.width + wrapPadding;
		if(position.x > p.width + wrapPadding) position.x = -wrapPadding;
		if(position.y < -wrapPadding) position.y = p.height + wrapPadding;
		if(position.y > p.height + wrapPadding) position.y = -wrapPadding;


		// set last vertices
		if(lastVertices == null) lastVertices = new float[]{0f,0f,0f,0f};
		lastVertices[0] = curVertices[0];
		lastVertices[1] = curVertices[1];
		lastVertices[2] = curVertices[2];
		lastVertices[3] = curVertices[3];

		
		// draw texture image for debugging purposes
		if(debugTexture == true) {
			noStroke();
			float imgRatio = (float) p.height / (float) img.height;
			p.image(img, 0, 0, img.width * imgRatio, img.height * imgRatio);
			// show texture grab area
			p.fill(255, 200);
			p.rect(0, imgRatio * textureY, img.width * imgRatio, speed * imgRatio);
		}
		
		
		BrightnessFilter.instance(p).setBrightness(0.98f);
		if(p.frameCount % 40 == 0) BrightnessFilter.instance(p).applyTo(p);
//		ContrastFilter.instance(p).setContrast(1.01f);
//		ContrastFilter.instance(p).applyTo(p);
	}
	
}
