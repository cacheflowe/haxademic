package com.haxademic.app.musicvideos;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat3d;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import processing.video.Movie;

public class CacheAssaultVideo2
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	

	
	protected String _videoFile;
	protected Movie _movie;
	protected PImage _curFrame;
	protected PGraphics pg;
	protected int _movieW = 0;
	protected int _movieH = 0;

	protected PShader vignette;
	protected PShader kaleido;
	protected PShader edge;
	protected PShader badtv;
	protected PShader toon;

	protected float _lastTime = 0;
	protected float _timerCount = 0;

	
	protected Kicks _kicks;
	protected Snares _snares;
	protected Percs _percs;
	protected Timers _timers;
	
	protected EasingFloat3d _sceneRotation;
	protected EasingFloat3d _cameraOffset;

	protected int _curSection = 0;
	protected int _curMeasure = 0;
	protected float _songLengthFrames = 5940;

	protected void config() {
		Config.setProperty( AppSettings.FPS, "30" );
		Config.setProperty( AppSettings.WIDTH, "1920" );
		Config.setProperty( AppSettings.HEIGHT, "1080" );
		Config.setProperty( AppSettings.WIDTH, "1280" );
		Config.setProperty( AppSettings.HEIGHT, "720" );
//		Config.setProperty( "disable_esc", "true" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "true" );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int) _songLengthFrames );
//		Config.setProperty( AppSettings.RENDER_AUDIO, "true" );
		Config.setProperty( AppSettings.RENDER_AUDIO_FILE, FileUtil.haxademicDataPath() + "video/cache-assault-master.wav" );
		Config.setProperty( AppSettings.RENDER_MIDI_FILE, FileUtil.haxademicDataPath() + "video/cache-assault-midi.mid" );
		Config.setProperty( AppSettings.RENDER_MIDI_BPM, "150" );
		Config.setProperty( AppSettings.RENDER_MIDI_OFFSET, "0" );
		
		
		Config.setProperty( AppSettings.SUNFLOW, "false" );
		Config.setProperty( AppSettings.SUNFLOW_ACTIVE, "false" );
		Config.setProperty( AppSettings.SUNFLOW_QUALITY, "low" );
		Config.setProperty( AppSettings.SUNFLOW_SAVE_IMAGES, "false" );

	}

	public void firstFrame() {

		pg = p.createGraphics(p.width, p.height, P.P3D);
		pg.smooth( AppSettings.SMOOTH_HIGH );
	}

	public void initRender() {

		p.smooth( 8 );
		
		_sceneRotation = new EasingFloat3d(0, 0, 0, 6);
		_cameraOffset = new EasingFloat3d(0, 0, -700, 6);
		
		_kicks = new Kicks();
		_snares = new Snares();
		_percs = new Percs();
		_timers = new Timers();
	}
	
	protected void loadShaders() {
		kaleido = loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/kaleido.glsl" ); 
		kaleido.set("sides", 6.0f);
		kaleido.set("angle", 0.0f);
		
		vignette = loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/vignette.glsl" );
		vignette.set("darkness", 0.35f);
		vignette.set("spread", 0.15f);

//		edge = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/edges.glsl" ); 
		
		badtv = loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/badtv.glsl" ); 
		badtv.set("nIntensity", 0.1f);
		badtv.set("sIntensity", 0.8f);

		toon = loadShader(FileUtil.haxademicDataPath() + "haxademic/shaders/lights/toon/frag.glsl", FileUtil.haxademicDataPath() + "haxademic/shaders/lights/toon/vert.glsl");
		toon.set("fraction", 1.0f);
	}
	
	protected float cycle(float mid, float amp, float inc) {
		return mid + amp * P.sin(p.frameCount*inc);
	}
	
	public void drawApp() {
		if( p.frameCount == 1 ) initRender();
		handleInputTriggers();
		p.background(0);
		pg.beginDraw();
		pg.clear();
		pg.noStroke();

//		  directionalLight(204, 204, 204, -dirX, -dirY, -1);

		PG.setBasicLights(pg);
		pg.pointLight(cycle(50,25,0.01f), cycle(100,35,0.015f), cycle(50,25,0.02f), 0, p.height/2f, 1500);
		pg.pointLight(cycle(150,125,0.01f), cycle(200,135,0.015f), cycle(150,45,0.02f), pg.width, p.height/2f, 1500);
		pg.specular(80, 80, 80);
		pg.lightFalloff(1.0f, 0.001f, 2.0f);
		
		pg.ambientLight(102, 102, 102);
		pg.lightSpecular(204, 204, 204);
		pg.directionalLight(102, 102, 102, 0, 0, -1);
		
		pg.specular(200, 255, 100);
		pg.emissive(0, 51, 51);
		pg.ambient(100, 10, 11);
		pg.shininess(20.0f); 

		
		
		PG.setCenterScreen(pg);
		_cameraOffset.update();
		pg.translate(_cameraOffset.x(), _cameraOffset.y(), _cameraOffset.z());
		
		_sceneRotation.update();
		pg.rotateZ(_sceneRotation.z());
		pg.rotateY(_sceneRotation.y());
		
		float boxW = 100;
		float boxSmallW = 10;
		
		// move to center
		
		// draw kicks
		_kicks.update();
		_snares.update();
		_percs.update();
		_timers.update();
		
		// draw EQ tubes
		drawEQ(512, 3, 1400, 120, 2);
		
		pg.noLights();
		PG.setBasicLights(pg);
		pg.pointLight(cycle(100,50,0.02f), cycle(100,50,0.037f), cycle(100,50,0.021f), 0, p.height/2f, 500);
		pg.pointLight(cycle(50,25,0.03f), cycle(100,45,0.035f), cycle(50,45,0.04f), pg.width, p.height/2f, 500);
		pg.specular(20, 80, 20);

//		drawEQ(512, 39, 30, 20, 6);
		drawEQSmoothed(50, 3, 30, 100, 25, 10, 2);
		
		
//		_curMov.beginShape();
//		for (int i = 0; i < _waveformData._waveform.length; i++ ) {
//			float curY = _waveformData._waveform[i] * 300;
//			_curMov.vertex(startX + i * spacing, curY);
//		}
//		_curMov.endShape();
		
		// draw canvas to screen
		pg.endDraw();

		loadShaders();	// not sure why, but during rendering, yu have 
		pg.filter(badtv);
		pg.filter(vignette);
//		pg.filter(kaleido);
		
		p.image(pg, 0, 0, p.width, p.height);
	}
	
	protected void drawEQ(int numBands, int discReso, float radius, float spacing, float amp) {
		float startX = -spacing * numBands/2f;
		
		pg.noStroke();
		pg.fill(100, 100, 100);


//		pg.noFill();
//		pg.stroke(255);
//		pg.strokeWeight(1);
//		pg.rotateX(p.mouseY/10f);
//		pg.rotateY(p.mouseX/10f);
		
		// draw EQ
		float radSegment = P.TWO_PI / discReso;
		for (int i = 1; i < numBands; i++) {
			float lastEqVal = radius + radius * amp * AudioIn.audioFreq(i-1);
			float eqVal = radius + radius * amp * AudioIn.audioFreq(i);
			float curX = startX + i * spacing;
			float lastX = startX + (i-1) * spacing;
			
			// float slopeDown = P.map(P.abs(i-numBands/2),0,20,1,0);
			lastEqVal *= P.map(P.abs((i-1)-numBands/2),0,20,1,0);
			eqVal *= P.map(P.abs(i-numBands/2),0,20,1,0);
			
			pg.beginShape(P.TRIANGLE_STRIP);
			for (int j = 0; j <= discReso; j++) {
				float curRads = j * radSegment;
				pg.vertex(lastX, P.sin(curRads) * lastEqVal, P.cos(curRads) * lastEqVal);
				pg.vertex(curX, P.sin(curRads) * eqVal, P.cos(curRads) * eqVal);
			}
			pg.endShape();
		}
	}
	
	protected void drawEQSmoothed(int numBands, int discReso, float radius, float spacing, float smoothsteps, float amp, float smoothEasing) {
		float startX = -spacing * numBands/2f;
		
		pg.noStroke();
		pg.fill(255, 255, 255);
		
		int from = p.color(0);
		int to = p.color(255);

//		pg.stroke(200, 200, 200);
//		pg.fill(0, 0, 0);
//		pg.noFill();
//		pg.rotateY(p.mouseX/100f);
//		pg.rotateX(p.mouseY/100f);

		// draw EQ
		float spacingSubDiv = spacing / (smoothsteps);
		float radSegment = P.TWO_PI / discReso;
		for (int i = 1; i < numBands; i++) {
			
			float lastEqVal = radius + radius * amp * AudioIn.audioFreq(i-1);
			float eqVal = radius + radius * amp * AudioIn.audioFreq(i);
			float curX = startX + i * spacing;
			float lastX = startX + (i-1) * spacing;
			
			float ampDiff = eqVal - lastEqVal;
			
			pg.fill( p.lerpColor(from, to, (float)i/numBands) );
			
//			P.println("lastEqVal",lastEqVal);
//			P.println("eqVal",eqVal);
//			P.println("ampDiff",lastEqVal+ampDiff);
			
			for (float subDivision = 1; subDivision <= smoothsteps; subDivision++) {
				
				// interpolate the amplitude
				float lastEqSubDiv = lastEqVal + ampDiff * MathUtil.easePowPercent((subDivision-1f)/smoothsteps, smoothEasing);
				float curEqSubDiv = lastEqVal + ampDiff * MathUtil.easePowPercent((subDivision)/smoothsteps, smoothEasing);
				
				// break up subdivision spacing
				float subDivLastX = lastX + spacingSubDiv * (subDivision-1);
				float subDivCurX = lastX + spacingSubDiv * subDivision;
				
//				P.println("% ",(subDivision-1f)/smoothsteps);
//				P.println("% ",(subDivision)/smoothsteps);
//				if(subDivision == smoothsteps) P.println("curEqSubDiv",curEqSubDiv);
//				P.println("subDivLastX ",subDivLastX);
//				P.println("subDivCurX ",subDivCurX);
				
//				pg.noFill();
//				if(subDivision == smoothsteps) pg.fill(255, 255, 255);

				
				pg.beginShape(P.TRIANGLE_STRIP);
				for (int j = 0; j <= discReso; j++) {
					float curRads = j * radSegment;
					pg.vertex(subDivLastX, P.sin(curRads) * lastEqSubDiv, P.cos(curRads) * lastEqSubDiv);
					pg.vertex(subDivCurX, P.sin(curRads) * curEqSubDiv, P.cos(curRads) * curEqSubDiv);
				}
				pg.endShape();
				
			}
		}
		
	}
	
	public void handleInputTriggers() {
//		 P.println(_midi._notesOn);
		if( MidiState.instance().isMidiNoteOn( 58 ) ) newSection();
		else if( MidiState.instance().isMidiNoteOn( 57 ) ) newMeasure();
		else if( MidiState.instance().isMidiNoteOn( 65 ) ) timer();
		else if( MidiState.instance().isMidiNoteOn( 60 ) ) kick();
		else if( MidiState.instance().isMidiNoteOn( 61 ) ) snare();
		else if( MidiState.instance().isMidiNoteOn( 62 ) ) perc();
	}

	protected void newSection() {
		_curSection++;
		P.println("================= NEW SECTION: "+_curSection+" =================");
		
		newMeasure();
		kick();
	}
	
	protected void newMeasure() {
		_curMeasure++;
		P.println("================= NEW MEASURE: "+_curMeasure+" =================");
//		P.println("kickTime = "+_baseKickTime+"   snareTime = "+_baseSnareTime);
		
		if(_curMeasure == 2) {
			_sceneRotation.setTargetZ(P.PI/2f);
			_sceneRotation.setTargetY(P.PI/2.3f);
			_cameraOffset.setTargetZ(-100);
			_cameraOffset.setTargetY(0);
		} else if(_curMeasure == 3) {
			_sceneRotation.setTargetZ(P.PI);
			_sceneRotation.setTargetY(P.PI/-4f);
			_cameraOffset.setTargetZ(-300);
			_cameraOffset.setTargetY(0);
		} else if(_curMeasure == 4) {
			_sceneRotation.setTargetZ(0);
			_sceneRotation.setTargetY(0);
			_cameraOffset.setTargetZ(-600);
			_cameraOffset.setTargetY(0);
		} else if(_curMeasure == 5) {
			_sceneRotation.setTargetZ(0);
			_sceneRotation.setTargetY(P.PI/2f);
			_cameraOffset.setTargetZ(-800);
			_cameraOffset.setTargetY(-200);
		} else {
			_sceneRotation.setTargetZ(P.PI/2f * MathUtil.randRange(0, 8));
			_sceneRotation.setTargetY(P.PI/2f * MathUtil.randRange(0, 8));
			_cameraOffset.setTargetZ(MathUtil.randRange(-100, -800));
			_cameraOffset.setTargetY(MathUtil.randRange(-300, 300));
		}
	}
	
	protected void timer() {
		_timers.newTimer();
	}
	
	protected void kick() {
		_kicks.newKick();
	}

	protected void snare() {
		_snares.newSnare();
	}

	protected void perc() {
		_percs.newPerc();
	}
	

	/*************************************
	 * KICKS
	 *************************************/
	public class Kicks {
		protected ArrayList<Kick> _kicks;
		
		public Kicks() {
			_kicks = new ArrayList<Kick>();
		}
		
		public void newKick() {
			Kick oldKick = findRecycleKick();
			if(oldKick != null) {
				oldKick.reset();
			} else {
				_kicks.add(new Kick());
			}
		}
		
		protected Kick findRecycleKick() {
			for (Kick kick : _kicks) {
				if(kick.active() == false) {
					return kick;
				}
			}
			return null;
		}
		
		public void update() {
			for (Kick kick : _kicks) {
				kick.update();
			}
		}
	}
	
	public class Kick {
		
		protected boolean _active = true;
		protected float _x;
		protected float _speed = 10;
		protected float _radius;
		protected float _radiusInner;
		protected float _discHeight;
		protected int _reso = 3;
		protected int _color = p.color(200);
		protected int _wallColor = p.color(100);
		
		public Kick() {
			reset();
		}
		
		public boolean active() {
			return _active;
		}
		
		public void reset() {
			_active = true;
			_discHeight = 50;
			_x = _discHeight;
			_radius = 150;
			_radiusInner = 130;
		}
		
		public void update() {
			if(!_active) return;
			
			_x += _speed;
			_discHeight -= 0.5f;
			if(_discHeight <= 0) _active = false;
			_radius += 1f;
			_radiusInner += 1.25f;
			
			pg.pushMatrix();
			pg.translate(-_x, 0, 0);
			pg.rotateY(P.HALF_PI);
			Shapes.drawDisc3D(pg, _radius, _radiusInner, _discHeight, _reso, _color, _wallColor);
			pg.popMatrix();
			
			pg.pushMatrix();
			pg.translate(_x, 0, 0);
			pg.rotateY(-P.HALF_PI);
			Shapes.drawDisc3D(pg, _radius, _radiusInner, _discHeight, _reso, _color, _wallColor);
			pg.popMatrix();
		}
	}

	/*************************************
	 * SNARES
	 *************************************/
	public class Snares {
		protected ArrayList<Snare> _snares;
		
		public Snares() {
			_snares = new ArrayList<Snare>();
		}
		
		public void newSnare() {
			Snare oldSnare = findRecycleSnare();
			if(oldSnare != null) {
				oldSnare.reset();
			} else {
				_snares.add(new Snare());
			}
		}
		
		protected Snare findRecycleSnare() {
			for (Snare snare : _snares) {
				if(snare.active() == false) {
					return snare;
				}
			}
			return null;
		}
		
		public void update() {
			for (Snare snare : _snares) {
				snare.update();
			}
		}
	}
	
	public class Snare {
		
		protected boolean _active = true;
		protected float _x = 0;
		protected float _speed = 20;
		protected float _radius;
		protected float _radiusInner;
		protected float _discHeight;
		protected int _reso = 3;
		protected int _color = p.color(100, 200, 100);
		protected int _wallColor = p.color(0, 100, 0);
		
		public Snare() {
			reset();
		}
		
		public boolean active() {
			return _active;
		}
		
		public void reset() {
			_active = true;
			_discHeight = 30;
			_x = _discHeight;
			_radius = 100;
			_radiusInner = 90;
		}
		
		public void update() {
			if(!_active) return;
			
			_x += _speed;
			_discHeight -= 0.5f;
			if(_discHeight <= 0) _active = false;
			_radius += 0;
			_radiusInner -= 0.25f;
			
			pg.pushMatrix();
			pg.translate(-_x, 0, 0);
			pg.rotateY(P.HALF_PI);
			Shapes.drawDisc3D(pg, _radius, _radiusInner, _discHeight, _reso, _color, _wallColor);
			pg.popMatrix();
			
			pg.pushMatrix();
			pg.translate(_x, 0, 0);
			pg.rotateY(-P.HALF_PI);
			Shapes.drawDisc3D(pg, _radius, _radiusInner, _discHeight, _reso, _color, _wallColor);
			pg.popMatrix();
		}
	}
	
	/*************************************
	 * PERCS
	 *************************************/
	public class Percs {
		protected ArrayList<Perc> _percs;
		
		public Percs() {
			_percs = new ArrayList<Perc>();
		}
		
		public void newPerc() {
			Perc oldPerc = findRecyclePerc();
			if(oldPerc != null) {
				oldPerc.reset();
			} else {
				_percs.add(new Perc());
			}
		}
		
		protected Perc findRecyclePerc() {
			for (Perc perc : _percs) {
				if(perc.active() == false) {
					return perc;
				}
			}
			return null;
		}
		
		public void update() {
			for (Perc perc : _percs) {
				perc.update();
			}
		}
	}
	
	public class Perc {
		
		protected boolean _active = true;
		protected float _x = 0;
		protected float _speed = 30;
		protected float _radius;
		protected float _radiusInner;
		protected float _discHeight;
		protected int _reso = 3;
		protected int _color = p.color(0, 0, 0);
		protected int _wallColor = p.color(0, 100, 0);
		
		public Perc() {
			reset();
		}
		
		public boolean active() {
			return _active;
		}
		
		public void reset() {
			_active = true;
			_discHeight = 100;
			_x = _discHeight;
			_radius = 200;
			_radiusInner = 190;
		}
		
		public void update() {
			if(!_active) return;
			
			_x += _speed;
			_discHeight -= 2f;
			if(_discHeight <= 0) _active = false;
			_radius += 1;
			_radiusInner -= 1f;
			
			pg.pushMatrix();
			pg.translate(-_x, 0, 0);
			pg.rotateY(-P.HALF_PI);
			Shapes.drawDisc3D(pg, _radius, _radiusInner, _discHeight, _reso, _color, _wallColor);
			pg.popMatrix();
			
			pg.pushMatrix();
			pg.translate(_x, 0, 0);
			pg.rotateY(P.HALF_PI);
			Shapes.drawDisc3D(pg, _radius, _radiusInner, _discHeight, _reso, _color, _wallColor);
			pg.popMatrix();
		}
	}
	
	/*************************************
	 * Timers
	 *************************************/
	public class Timers {
		protected ArrayList<Timer> _timers;
		protected float circleSegments = 16;
		protected float segmentRads = P.TWO_PI / circleSegments;
		protected float curRads = 0;
		protected int _timerNum = 0;
		
		public Timers() {
			_timers = new ArrayList<Timer>();
		}
		
		public void newTimer() {
			curRads += segmentRads;
			Timer timer = findRecycleTimer();
			if(timer != null) {
				timer.reset(curRads, _timerNum % 32);
			} else {
				timer = new Timer(curRads, _timerNum % 32);
				_timers.add(timer);
			}
			// every 4th is big
			if(_timerNum % 4 == 0) {
				timer.bigOne();
			}
			_timerNum++;
		}
		
		protected Timer findRecycleTimer() {
			for (Timer timer : _timers) {
				if(timer.active() == false) {
					return timer;
				}
			}
			return null;
		}
		
		public void update() {
			for (Timer timer : _timers) {
				timer.update();
			}
		}
	}
	
	public class Timer {
//		protected PVector _position;
		protected boolean _active = true;
		protected float _x = 0;
		protected float _speed = 10;
		protected float _radius;
		protected float _radians;
		protected float _size;
		protected int _reso = 30;
		protected int _color = p.color(0, 0, 100);
		protected int _index = 0;
		
		public Timer(float rads, int index) {
//			_position = new PVector();
			reset(rads, index);
		}
		
		public boolean active() {
			return _active;
		}
		
		public void reset(float rads, int index) {
			_radians = rads;
			_index = index;
			_active = true;
			_size = 20;
			_x = _size;
			_radius = 350;
		}
		
		public void bigOne() {
			_size = 50;
		}
		
		public void update() {
			if(!_active) return;
			
			_x += _speed;
			_size -= 0.1f;
			if(_size <= 0) _active = false;
			_radius += 3;
			if(_radius <= 0) _active = false;
			
			pg.pushMatrix();
			pg.translate(-_x, P.sin(_radians - P.PI) * _radius, P.cos(_radians - P.PI) * _radius);
			pg.rotateX(-_radians - P.PI);
//			pg.sphere(_size);
			Shapes.drawPyramid(pg, _size * (1 + AudioIn.audioFreq(_index)), _size, true);
			pg.popMatrix();
			
			pg.pushMatrix();
			pg.translate(_x, P.sin(_radians) * _radius, P.cos(_radians) * _radius);
//			pg.sphere(_size);
			pg.rotateX(-_radians);
			Shapes.drawPyramid(pg, _size * (1 + AudioIn.audioFreq(_index)), _size, true);
			pg.popMatrix();
		}
	}
}
