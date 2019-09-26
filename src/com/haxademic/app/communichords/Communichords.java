package com.haxademic.app.communichords;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.KinectRegionGrid;
import com.haxademic.core.hardware.dmx.DmxAjaxProManagerInterface;
import com.haxademic.core.hardware.joystick.AutoTesterJoysticksCollection;
import com.haxademic.core.hardware.joystick.IJoystickCollection;
import com.haxademic.core.hardware.joystick.IJoystickControl;
import com.haxademic.core.hardware.leap.LeapRegionGrid;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import ddf.minim.Minim;
import ddf.minim.ugens.Gain;
import ddf.minim.ugens.Sampler;


public class Communichords
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }	
	/**
	 * TODO SOFTWARE:
	 * - Make sure sample rates are correct to play notes from C
	 * 		- use the casio to be sure
	 * TODO AUDIO:
	 * - Build better loops in Ableton
	 * - Add more sounds
	 * GATHER HARDWARE:
	 * - Tarp for waterproofness
	 */
	
	protected DmxAjaxProManagerInterface _dmx;
	protected ArrayList<TonePlayer> _bassTones;
	protected int _bassToneIndex = 0;
	protected ArrayList<TonePlayer> _midTones;
	protected int _midToneIndex = 0;
	protected ArrayList<TonePlayer> _midTones2;
	protected int _midTone2Index = 0;
	protected ArrayList<TonePlayer> _highTones;
	protected int _highToneIndex = 0;
	protected ArrayList<Player> _players;
	protected IJoystickCollection _joysticks;
	protected int NUM_PLAYERS = 4;
	protected boolean _dmxActive;
	protected boolean _isScreensaverMode = true;
	protected int _screenSaverStartFrame = 0;
	protected Minim minim;
	
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
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		p.appConfig.setProperty( "kinect_min_mm", "600" );
		p.appConfig.setProperty( "kinect_max_mm", "2500" );
		p.appConfig.setProperty( "kinect_top_pixel", "0" );
		p.appConfig.setProperty( "kinect_bottom_pixel", "480" );
		p.appConfig.setProperty( "kinect_pixel_skip", "14" );
		p.appConfig.setProperty( "kinect_player_gap", "150" );
		p.appConfig.setProperty( "kinect_player_min_pixels", "10" );
		p.appConfig.setProperty( "kinect_mirrored", "true" );
		
		p.appConfig.setProperty( "leap_active", "false" );
		
		p.appConfig.setProperty( "dmx_active", "true" );
	}

	public void setup() {
		super.setup();
		minim = new Minim(this);
		// hardware config
		if(p.appConfig.getBoolean("leap_active", false) == true) {
			_joysticks = new LeapRegionGrid(NUM_PLAYERS, 1, 1, 0f);
		} else if(p.appConfig.getBoolean("kinect_active", false) == true) {
			_joysticks = new KinectRegionGrid(
				NUM_PLAYERS, 
				1, 
				p.appConfig.getInt("kinect_min_mm", -1), 
				p.appConfig.getInt("kinect_max_mm", -1), 
				p.appConfig.getInt("kinect_player_gap", -1), 
				p.appConfig.getInt("kinect_top_pixel", -1), 
				p.appConfig.getInt("kinect_bottom_pixel", -1), 
				p.appConfig.getInt("kinect_pixel_skip", -1), 
				p.appConfig.getInt("kinect_player_min_pixels", -1)
			);
		} else {
			_joysticks = new AutoTesterJoysticksCollection(NUM_PLAYERS);			
		}
		_dmxActive = p.appConfig.getBoolean("dmx_active", false);
		// Make sure to run Pro-Manager and select the ENTTEC DMXUSB PRO as the device from the web interface. Leave this running in the browser.
		buildTonePlayers();
		buildPhysicalLighting();
	}
	
	protected void buildTonePlayers() {
		_bassTones = new ArrayList<TonePlayer>();
		folderSoundsToTonePlayerPool(_bassTones, "audio/communichords/bass");
		_midTones = new ArrayList<TonePlayer>();
		folderSoundsToTonePlayerPool(_midTones, "audio/communichords/mid");
		_midTones2 = new ArrayList<TonePlayer>();
		folderSoundsToTonePlayerPool(_midTones2, "audio/communichords/mid");
		_highTones = new ArrayList<TonePlayer>();
		folderSoundsToTonePlayerPool(_highTones, "audio/communichords/high");
	}
	
	protected void folderSoundsToTonePlayerPool(ArrayList<TonePlayer> array, String folder) {
		ArrayList<String> files = FileUtil.getFilesInDirOfTypes(FileUtil.getHaxademicDataPath() + folder, "wav,aif");
		for (String file : files) {
			P.println("File: "+file);
			array.add(new TonePlayer(file));
		}

	}
	
	protected void buildPhysicalLighting() {
		_dmx = new DmxAjaxProManagerInterface(NUM_PLAYERS);
		_players = new ArrayList<Player>();
		_players.add(new Player(0, "#f0695c", "#845da5"));
		_players.add(new Player(1, "#845da5", "#00bbd1"));
		_players.add(new Player(2, "#00bbd1", "#c4d971"));
		_players.add(new Player(3, "#c4d971", "#f8a05f"));
		
		cycleTonesForPlayers();
	}
	
	public int setNextToneForPlayer(Player player, ArrayList<TonePlayer> tonePlayers, int curIndex) {
		tonePlayers.get(curIndex).stop();
		curIndex++;
		if(curIndex >= tonePlayers.size()) curIndex = 0;
		player.setTonePlayer(tonePlayers.get(curIndex));
		tonePlayers.get(curIndex).start();
		return curIndex;
	}

	protected void cycleTonesForPlayers() {
		_bassToneIndex = setNextToneForPlayer(_players.get(0), _bassTones, _bassToneIndex);
		_midTone2Index = setNextToneForPlayer(_players.get(1), _midTones2, _midTone2Index);
		_midToneIndex = setNextToneForPlayer(_players.get(2), _midTones, _midToneIndex);
		_highToneIndex = setNextToneForPlayer(_players.get(3), _highTones, _highToneIndex);
	}

	public void drawApp() {
		background(0);
		
		// update controls
		if( _joysticks != null ) _joysticks.update();
		// send colors to hardware
		checkActivity();
		updateNotes();
		if(_dmxActive == true) sendDmxLights();
	}
	
	protected void checkActivity() {
		int numActive = 0;
		for (int i = 0; i < NUM_PLAYERS; i++) {
			if(_players.get(i).isActive() == true) {
				numActive++;
			}
		}
		if(numActive == 0) {
			if(_isScreensaverMode == false) {
				_isScreensaverMode = true;
				_screenSaverStartFrame = p.frameCount;
				cycleTonesForPlayers();
			}
		} else {
			if(_isScreensaverMode == true) {
				_isScreensaverMode = false;
			}
		}
	}
	
	protected void updateNotes() {
		float playerW = p.width / NUM_PLAYERS;
		for (int i = 0; i < NUM_PLAYERS; i++) {
			if(_isScreensaverMode) {
				_players.get(i).updateScreensaver();
			} else {
				_players.get(i).update();
			}
			
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
	
	
	public class Player {
		
		protected boolean _isActive = false;
		protected int _lastActiveTime = -1;
		protected int _playerIndex;
		protected int _noteIndex = -1;
		protected int _lowColor;
		protected int _highColor;
		protected float _notes;
		protected LinearFloat _brightness;
		protected EasingColor _color;
		protected IJoystickControl _joystick;
		
		protected TonePlayer _tonePlayer;
		protected float OCTAVE_NUM_NOTES = 12f;

		
		public Player(int index, String lowColor, String highColor) {
			_playerIndex = index;
			_joystick = _joysticks.getRegion(index);
			_brightness = new LinearFloat(0, 0.04f);
			_lowColor = ColorUtil.colorFromHex(lowColor);
			_highColor = ColorUtil.colorFromHex(highColor);
			_color = new EasingColor(lowColor, 3f);
		}
		
		public void setTonePlayer(TonePlayer player) {
			_tonePlayer = player;
		}
		

		public void setColorFromPercent(float percent) {
			// fade between start & end 
			_color.setTargetRGBA(
					MathUtil.interp(ColorUtil.redFromColorInt(_lowColor), ColorUtil.redFromColorInt(_highColor), percent),
					MathUtil.interp(ColorUtil.greenFromColorInt(_lowColor), ColorUtil.greenFromColorInt(_highColor), percent),
					MathUtil.interp(ColorUtil.blueFromColorInt(_lowColor), ColorUtil.blueFromColorInt(_highColor), percent),
					255);
		}
		
		protected void newNote(int noteIndex) {
			_noteIndex = noteIndex;
			// _brightness.setCurrent(0);
			_brightness.setTarget(1);
		}
		
		
		public void setNoteFromPercent(float percent) {
			// calculate note
			int noteIndex = P.floor(OCTAVE_NUM_NOTES * percent);
			if(noteIndex > OCTAVE_NUM_NOTES - 1) noteIndex = Math.round(OCTAVE_NUM_NOTES - 1);
			// check for a changed note
			if(noteIndex != _noteIndex) newNote(noteIndex);
		}

		
		public boolean isActive() {
			return _isActive;
		}
		
		public void updateScreensaver() {
			update();
		}
		
		public void update() {
			if(_joystick.isActive()) {
				updateActivePlayer();
				_tonePlayer.setVolume(0);
			} else {
				updateInactivePlayer();
				_tonePlayer.setVolume(-50);
			}

			_brightness.update();
			_color.update();
			_tonePlayer.update(_noteIndex);
		}
		
		public void updateActivePlayer() {
			float playerPercent = 0.5f + 0.5f * _joystick.controlZ();
			setNoteFromPercent(playerPercent);
			setColorFromPercent(playerPercent);
			if(_isActive == false) {					
				_isActive = true;
			}
			_lastActiveTime = p.millis();
		}
		
		public void updateInactivePlayer() {
			_noteIndex = -1;
			if(_isActive == true) {					
				_brightness.setTarget(0);
				if(p.millis() > _lastActiveTime + 4000) {
					_isActive = false;
				}
			} else {
				if(_isScreensaverMode == true) {
					float totalCycleFrames = 200f;
					float playerCycleFrames = totalCycleFrames / _players.size();
					float playerPercent = MathUtil.getPercentWithinRange(playerCycleFrames * _playerIndex, playerCycleFrames * (_playerIndex + 1), (p.frameCount - _screenSaverStartFrame) % totalCycleFrames);
					float easedPercent = Penner.easeInOutSine(playerPercent);
					if(playerPercent >= 0 && playerPercent <= 1) {
						_brightness.setTarget(1);
						setColorFromPercent(easedPercent);
					} else {
						_brightness.setTarget(0);						
					}
				} else {
					_brightness.setTarget(0);
				}
			}
		}
		
		public int color() {
			float freqOsc = (_noteIndex != -1) ? 0.1f * P.sin((p.millis() / 1000f) * (_frequenciesFromMiddleC4[_noteIndex] / 10f)) : 0;
			return _color.colorInt(_brightness.value() * 1.05f + freqOsc);
		}
	}
	
	
	public class TonePlayer {
		// TODO: wat?!
		protected float[] _sampleRateSteps = {
				P.pow(2f, 0f / 12f) - 1f,  				// 0  C
				P.pow(2f, 2f / 12f) - 1f,				// 1  C#
				(P.pow(2f, 3f / 12f) - 1f) * 1.2f,		// 2  D
				(P.pow(2f, 4f / 12f) - 1f) * 1.25f,		// 3  D#
				P.pow(2f, 6f / 12f) - 1f,				// 4  E
				(P.pow(2f, 6f / 12f) - 1f) * 1.2f,		// 5  F
				P.pow(2f, 8f / 12f) - 1f,				// 6  F#
				P.pow(2f, 9f / 12f) - 1f,				// 7  G
				(P.pow(2f, 9f / 12f) - 1f) * 1.1f,		// 8  G#
				(P.pow(2f, 10f / 12f) - 1f) * 1.0375f,	// 9  A
				(P.pow(2f, 10f / 12f) - 1f) * 1.125f,	// 10 A#
				(P.pow(2f, 11f / 12f) - 1f) * 1.0675f	// 11 B
		};
		protected String _sampleFile;
		protected Sampler _sampler;
		protected Gain _gainEfx;
		protected LinearFloat _gainDB;
		protected EasingFloat _sampleRate;
		protected float BASE_SAMPLE_RATE = 44100f;
		protected float SAMPLE_RATE_OCTAVE = 22050f;
		
		protected float OCTAVE_NUM_NOTES = 12f;

		public TonePlayer(String sampleFile) {
			_sampleFile = sampleFile;
			
			_gainEfx = new Gain(0);
			_gainEfx.patch(minim.getLineOut());
			_gainDB = new LinearFloat(-50f, 2.f);
			
			_sampler = new Sampler(sampleFile, 1, minim); 
			_sampler.patch(_gainEfx);
			_sampler.looping = true;
			
			_sampleRate = new EasingFloat(BASE_SAMPLE_RATE, 3f);
		}
		
		public void start() {
			P.println("_sampleFile",_sampleFile);
			_sampler.trigger();
		}
		
		public void stop() {
			_sampler.stop();
		}
		
		public void update(int noteIndex) {
//			noteIndex = P.round(OCTAVE_NUM_NOTES - noteIndex);
			_gainDB.update();
			_gainEfx.setValue(_gainDB.value());
			
			// probably need  to do this instead: http://wiki.audacityteam.org/wiki/Change_Speed
			if(noteIndex != -1) {
//				float sampleRate = BASE_SAMPLE_RATE - (SAMPLE_RATE_OCTAVE / OCTAVE_NUM_NOTES) * noteIndex;
//				float mouseMod = P.map(p.mouseX, 0, p.width, 0f, 2f); - this was for testing/tuning
				float sampleRateStep = _sampleRateSteps[noteIndex];// * mouseMod;
				float sampleRate = BASE_SAMPLE_RATE - SAMPLE_RATE_OCTAVE * (sampleRateStep);
//				P.println("( P.pow(2f, (float)noteIndex / OCTAVE_NUM_NOTES) )",( P.pow(2f, (float)noteIndex / OCTAVE_NUM_NOTES) ) - 1f);
//				P.println("noteIndex",noteIndex,"OCTAVE_NUM_NOTES",OCTAVE_NUM_NOTES,"result",(float)noteIndex / OCTAVE_NUM_NOTES);
//				P.println("mouseMod",mouseMod,"sampleRateStep",sampleRateStep);
//				P.println("note",noteIndex,"sampleRate",sampleRate);
				_sampleRate.setTarget(sampleRate);
			}
			_sampleRate.update();
			_sampler.setSampleRate(_sampleRate.value());
		}
		
		public void setVolume(float volumeDb) {
			_gainDB.setTarget(volumeDb);
		}

	}
}





