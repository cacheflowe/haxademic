package com.haxademic.app.communichords;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.hardware.dmx.DmxInterface;
import com.haxademic.core.hardware.joystick.IJoystickCollection;
import com.haxademic.core.hardware.joystick.IJoystickControl;
import com.haxademic.core.hardware.leap.LeapRegionGrid;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

import ddf.minim.ugens.Gain;
import ddf.minim.ugens.Sampler;


@SuppressWarnings("serial")
public class Communichords
extends PAppletHax {
	
	protected DmxInterface _dmx;
	protected ArrayList<NotePlayer> _players;
	protected IJoystickCollection _joysticks;
	protected int NUM_PLAYERS = 4;
	protected boolean _dmxActive = false;
	
	// http://en.wikipedia.org/wiki/Piano_key_frequencies
	protected float[] _frequenciesFromMiddleC4 = {
			261.626f,
			277.183f,
			293.665f,
			311.127f,
			329.628f,
			349.228f,
			369.994f,
			391.995f,
			415.305f,
			440.000f,
			466.164f,
			493.883f
			};

	protected void overridePropsFile() {
		_appConfig.setProperty( "leap_active", "true" );
	}

	public void setup() {
		super.setup();
//		_joysticks = new AutoTesterJoysticksCollection(NUM_PLAYERS);
		_joysticks = new LeapRegionGrid(NUM_PLAYERS, 1, 1, 0f);
		// Make sure to run Pro-Manager and select the ENTTEC DMXUSB PRO as the device from the web interface. Leave this running in the browser.
		buildPhysicalLighting();
	}
	
	protected void buildPhysicalLighting() {
		_dmx = new DmxInterface(NUM_PLAYERS);
		_players = new ArrayList<NotePlayer>();
		_players.add(new NotePlayer(_joysticks.getRegion(0), "#f0695c", "#845da5", "audio/tone-loops/low-synth.wav"));
		_players.add(new NotePlayer(_joysticks.getRegion(1), "#845da5", "#00bbd1", "audio/tone-loops/mid-buzz-synth.wav"));
		_players.add(new NotePlayer(_joysticks.getRegion(2), "#00bbd1", "#c4d971", "audio/tone-loops/mid-synth.wav"));
		_players.add(new NotePlayer(_joysticks.getRegion(3), "#c4d971", "#f8a05f", "audio/tone-loops/high-synth.wav"));
	}
	


	public void drawApp() {
		background(0);
		
		// update controls
		if( _joysticks != null ) _joysticks.update();
		// send colors to hardware
		updateColors();
		if(_dmxActive == true) sendDmxLights();
	}
	
	protected void updateColors() {
		float playerW = p.width / NUM_PLAYERS;
		for (int i = 0; i < NUM_PLAYERS; i++) {
			_players.get(i).update();
			
			// draw debug
			p.fill(_players.get(i).color());
			p.noStroke();
			p.rect(i * playerW, 0, playerW, p.height);
		}
	}

	protected void sendDmxLights() {
		for (int i = 0; i < NUM_PLAYERS; i++) {
			_dmx.setColorAtIndex(i, _players.get(i).color());
		}
		_dmx.updateColors();
	}
	
	
	public class NotePlayer {
		
		protected boolean _isActive = false;
		protected int _noteIndex = -1;
		protected int _lowColor;
		protected int _highColor;
		protected float _notes;
		protected EasingFloat _brightness;
		protected ColorHaxEasing _color;
		protected IJoystickControl _joystick;
		protected Sampler _sampler;
		protected Gain _gainEfx;
		protected EasingFloat _gainDB;
		protected EasingFloat _sampleRate;
		protected float BASE_SAMPLE_RATE = 44100f;
		protected float SAMPLE_RATE_OCTAVE = 22050f;
		protected float OCTAVE_NUM_NOTES = 12f;
		
		public NotePlayer(IJoystickControl joystick, String lowColor, String highColor, String sampleFile) {
			_joystick = joystick;
			_brightness = new EasingFloat(0, 5f);
			_lowColor = ColorUtil.colorFromHex(lowColor);
			_highColor = ColorUtil.colorFromHex(highColor);
			_color = new ColorHaxEasing(lowColor, 3f);
			
			_gainEfx = new Gain(0);
			_gainEfx.patch(p.minim.getLineOut());
			_gainDB = new EasingFloat(-40f, 20f);
			
			_sampler = new Sampler(FileUtil.getHaxademicDataPath() + sampleFile, 1, p.minim); 
			_sampler.patch(_gainEfx);
			_sampler.looping = true;
			_sampler.trigger();
			_sampleRate = new EasingFloat(BASE_SAMPLE_RATE, 5f);
		}
		
		public void setPercent(float percent) {
			// calculate note
			int noteIndex = P.floor(OCTAVE_NUM_NOTES * percent);
			if(noteIndex > OCTAVE_NUM_NOTES - 1) noteIndex = Math.round(OCTAVE_NUM_NOTES - 1);
			// check for a changed note
			if(noteIndex != _noteIndex) newNote(noteIndex);
			
			// fade between start & end 
			_color.setTargetRGBA(
					MathUtil.interp(ColorHaxEasing.redFromColorInt(_lowColor), ColorHaxEasing.redFromColorInt(_highColor), percent),
					MathUtil.interp(ColorHaxEasing.greenFromColorInt(_lowColor), ColorHaxEasing.greenFromColorInt(_highColor), percent),
					MathUtil.interp(ColorHaxEasing.blueFromColorInt(_lowColor), ColorHaxEasing.blueFromColorInt(_highColor), percent),
					255);
		}
		
		protected void newNote(int noteIndex) {
			_noteIndex = noteIndex;
			_brightness.setCurrent(0);
			_brightness.setTarget(1);
		}
		
		public void update() {
			if(_joystick.isActive()) {
				float playerPercent = 0.5f + 0.5f * _joystick.controlZ();
				setPercent(playerPercent);
				if(_isActive == false) {					
					_isActive = true;
					_gainDB.setTarget(0);
				}
			} else {
				_brightness.setTarget(0);
				_noteIndex = -1;
				if(_isActive == true) {					
					_isActive = false;
				}
				float targetZero = _gainDB.value() - 4f;
				if(targetZero < -50) targetZero = -50;
				_gainDB.setTarget(targetZero);
			}

			_brightness.update();
			_color.update();
			
			_gainDB.update();
			_gainEfx.setValue(_gainDB.value());
			
			if(_noteIndex != -1) {
				float sampleRate = BASE_SAMPLE_RATE - (SAMPLE_RATE_OCTAVE / OCTAVE_NUM_NOTES) * _noteIndex;
				_sampleRate.setTarget(sampleRate);
			}
			_sampleRate.update();
			_sampler.setSampleRate(_sampleRate.value());
		}
		
		public int color() {
			float freqOsc = (_noteIndex != -1) ? 0.1f * P.sin((p.millis() / 1000f) * (_frequenciesFromMiddleC4[_noteIndex] / 10f)) : 0;
			return _color.colorInt(_brightness.value() * 0.85f + freqOsc);
		}
	}
	
}





