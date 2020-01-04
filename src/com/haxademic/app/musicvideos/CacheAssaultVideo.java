package com.haxademic.app.musicvideos;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pgraphics.PixelTriFilter;
import com.haxademic.core.draw.filters.pgraphics.archive.BlobOuterMeshFilter;
import com.haxademic.core.draw.filters.pgraphics.archive.ImageHistogramFilter;
import com.haxademic.core.draw.filters.pgraphics.archive.ReflectionFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import processing.video.Movie;

public class CacheAssaultVideo
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	

	
	protected String _videoFile;
	protected Movie _movie;
	protected PImage _curFrame;
	protected PGraphics _curMov;
	protected int _movieW = 0;
	protected int _movieH = 0;

	PixelTriFilter _triPixelFilter;
	protected float _triangleSize = 40; 
	ImageHistogramFilter _histogramFilter;
	ReflectionFilter _reflectionFilter;
	BlobOuterMeshFilter _blobFilter;
//	Cluster8BitRow _8bitFilter;
	
	protected PShader vignette;
	protected PShader kaleido;
	protected PShader edge;
	protected PShader badtv;

	protected float _audioAlphaStart = 1f;
	protected float _audioAlphaMult = 5f;

	protected float _curTime = 50;
	protected float _lastTime = 0;
	protected float _curTimeInc = 0.1f;
	protected float _timerCount = 0;
	protected float _baseKickTime = 100;
	protected float _baseSnareTime = 100;
	
	// 163, 207.5f, 
	protected float[] _janetTimes = {180, 188, 197.5f, 211, 213, 217.3f, 224.5f, 226.8f, 235.9f, 239.8f, 243, 245.5f, 248, 252, 256, 261, 263, 269, 275.5f, 278.5f, 313, 314, 329, 332, 343.5f, 346, 375.5f, 382, 384, 396, 400.5f, 406.5f, 434.5f, 448, 483.8f, 497, 529, 535.5f};
	protected int _janetTimeIndex = -1;
	protected int _curSection = 0;
	protected int _curMeasure = 0;
	
	protected float[] _triangleSizes = {30, 24, 20, 18, 16, 12, 10, 8, 5, 4};	// 45, 40, 36, 
	protected int _triangleSizeStartIndex = -1;
	protected int _curTriangleSizeIndex = 0;


	protected void config() {
		Config.setProperty( AppSettings.FPS, "30" );
		Config.setProperty( "video_source_file", FileUtil.haxademicDataPath() + "video/Janet Jackson - Control - trimmed.mov" );
//		Config.setProperty( "video_source_file", FileUtil.getHaxademicDataPath() + "video/Madonna - Lucky Star.mov" );
		Config.setProperty( AppSettings.WIDTH, "1280" );
		Config.setProperty( AppSettings.HEIGHT, "720" );
//		Config.setProperty( "disable_esc", "true" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "true" );
//		Config.setProperty( AppSettings.RENDER_AUDIO, "true" );
		Config.setProperty( AppSettings.RENDER_AUDIO_FILE, FileUtil.haxademicDataPath() + "video/cache-assault-master.wav" );
		Config.setProperty( AppSettings.RENDER_MIDI_FILE, FileUtil.haxademicDataPath() + "video/cache-assault-midi.mid" );
		Config.setProperty( AppSettings.RENDER_MIDI_BPM, "150" );
		Config.setProperty( AppSettings.RENDER_MIDI_OFFSET, "0" );
	}

	public void firstFrame() {
	}

	public void initRender() {
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		_curMov = p.createGraphics(width, height, P.P3D);
		_curFrame = p.createImage(width, height, P.ARGB);
		_movie = new Movie(this, Config.getString( "video_source_file", "" ));

		
		_triPixelFilter = new PixelTriFilter( _curFrame.width, _curFrame.height, 10 );
		_histogramFilter = new ImageHistogramFilter( _curFrame.width, _curFrame.height, 4 );
		_reflectionFilter = new ReflectionFilter( _curFrame.width, _curFrame.height );
		_blobFilter = new BlobOuterMeshFilter( _curFrame.width, _curFrame.height );
//		_8bitFilter = new Cluster8BitRow( _curFrame.width, _curFrame.height, (int) 10, false );
		
		kaleido = loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/kaleido.glsl" ); 
		kaleido.set("sides", 6.0f);
		kaleido.set("angle", 0.0f);
		
		vignette = loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/vignette.glsl" );
		vignette.set("darkness", 0.85f);
		vignette.set("spread", 0.15f);

//		edge = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/edges.glsl" ); 
		
		badtv = loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/badtv.glsl" ); 
		badtv.set("nIntensity", 0.1f);
		badtv.set("sIntensity", 0.8f);

	}
	
	public void drawApp() {
		if( p.frameCount == 1 ) initRender();
		handleInputTriggers();
		p.background(0);
		
		// seek video
		if( _curTime != _lastTime ) {
			_movie.jump( _curTime );
			_movie.play();
			_movie.pause();
		}
		_lastTime = _curTime;

		// copy video to off-screen graphics
		_curMov.beginDraw();
		_curMov.image(_movie, 0, (p.height/2f) - (_movieH/2f), _movieW, _movieH);

		
		// adjust audio grid drawing vars every frame
		_audioAlphaStart -= 0.005;
		_audioAlphaMult += 0.1f;

		// add audio grid
		float cols = 16 * 3;
		float rows = 9 * 3;
		float cellW = p.width / cols;
		float cellH = p.height / rows;
		float startX = 0;
		float startY = 0;
		int spectrumIndex = 0;
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				float alphaVal = _audioAlphaStart - AudioIn.audioFreq(spectrumIndex) * _audioAlphaMult;
				if( alphaVal > 0 ) {
					_curMov.fill( 0, alphaVal * 255f );
					_curMov.rect( startX + i*cellW, startY + j*cellH, cellW, cellH );
				}
				spectrumIndex++;
			}
		}
		
		
		// add vertical EQ to sides
		_curMov.fill(0);
		float rowH = p.height / 256f;
		for (int i = 0; i < 256; i++) {
			float amp = AudioIn.audioFreq(i) * 150f;
			_curMov.rect( 0, i * rowH, amp, rowH );
		}
		for (int i = 256; i < 512; i++) {
			float amp = AudioIn.audioFreq(i) * 150f;
			_curMov.rect( p.width, (i-256) * rowH, -amp, rowH );
		}




		// draw waveforms
		_curMov.fill(0, 200);
		_curMov.noStroke();
		startX = 0;
		float spacing = p.width / 512f;
		_curMov.beginShape();
		for (int i = 0; i < AudioIn.waveform.length; i++ ) {
			float curY =  AudioIn.waveform[i] * 300;
			_curMov.vertex(startX + i * spacing, curY);
		}
		_curMov.vertex(p.width, -200);
		_curMov.vertex(0, -200);
		_curMov.endShape();
		_curMov.beginShape();
		for (int i = 0; i <  AudioIn.waveform.length; i++ ) {
			float curY = p.height +  AudioIn.waveform[i] * 300;
			_curMov.vertex(startX + i * spacing, curY);
		}
		_curMov.vertex(p.width, p.height + 200);
		_curMov.vertex(0, p.height + 200);
		_curMov.endShape();

		// end drawing on top of movie
		_curMov.endDraw();
		


		// copy pGraphics to PImage for post-processing - stretch a little to make sure entire image fills the screen
		_curFrame.copy( _curMov, 0, 0, _curMov.width, _curMov.height, -10, -10, _curFrame.width + 20, _curFrame.height + 20 );
	
		
		
		// draw filtered image
//		if( _curMov != null ) p.image( _triPixelFilter.updateWithPImage( _histogramFilter.updateWithPImage( _curFrame ) ), 0, 0, width, height);
//		if( _curMov != null ) p.image( _8bitFilter.updateWithPImage( _histogramFilter.updateWithPImage( _curFrame ) ), 0, 0, width, height);
//		if( _curMov != null ) p.image( _triPixelFilter.updateWithPImage( _blobFilter.updateWithPImage( _curFrame ) ), 0, 0, width, height);
		if( _curMov != null ) p.image( _histogramFilter.updateWithPImage( _curFrame ) , 0, 0, width, height);
		
		p.filter(badtv);
		kaleido.set("sides",4f);
//		p.filter(kaleido);
//		p.filter(vignette);
//		p.filter(vignette);
//		p.filter(vignette);

	}
	
	protected void randomPixels() {
		int pixels = MathUtil.randRange( 10, 30 );
		if( pixels % 2 == 1 ) pixels++;
		_triPixelFilter.setPixelSize( pixels );
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
		_audioAlphaStart = 1f;
		_audioAlphaMult = 5f;
		
		if( _curSection == 1 ) _triangleSizeStartIndex = 4;
		_triangleSizeStartIndex++;	// restart scaling down triangles each section
		_curTriangleSizeIndex = _triangleSizeStartIndex;
		
		newMeasure();
		kick();
	}
	
	protected void newMeasure() {
		_curMeasure++;
		P.println("================= NEW MEASURE: "+_curMeasure+" =================");
		if( _curMeasure == 3 ) _triangleSizeStartIndex = 0;
		
		_janetTimeIndex++;
		if( _janetTimeIndex < _janetTimes.length ) _baseKickTime = _janetTimes[_janetTimeIndex];
		if( _janetTimeIndex+1 < _janetTimes.length ) _baseSnareTime = _janetTimes[_janetTimeIndex+1];
		
		P.println("kickTime = "+_baseKickTime+"   snareTime = "+_baseSnareTime);

		_curTriangleSizeIndex++;
		if( _curTriangleSizeIndex < _triangleSizes.length ) _triangleSize = _triangleSizes[_curTriangleSizeIndex];
		_triPixelFilter.setPixelSize( Math.round( _triangleSize ) );
	}
	
	protected void timer() {
		_curTime += _curTimeInc;
		_timerCount++;
	}
	
	protected void kick() {
		if( Math.abs( _baseKickTime - _curTime ) > 3 ) {
			_curTime = _baseKickTime;
		} else {
			_curTime += _curTimeInc;
		}
	}

	protected void snare() {
		_curTime = _baseSnareTime;
	}

	protected void perc() {
		_curTime = _baseSnareTime; 
	}

	public void movieEvent(Movie m) {
		_movieW = p.width;
		_movieH = Math.round(((float)p.width/_movie.width) * _movie.height);


		m.read();
	}

}




/**
 * Janet video times:
 * Guys:
 * 108.2
 * 138.1
 * 146.5
 * 275.5
 * 278.5
 * 313
 * 375.5
 * 
 * 
 * Janet:
 * 163 (face)
 * 180 (face)
 * 188 (swing)
 * 207.5 (jump)
 * 211
 * 213
 * 217.5
 * 224.5
 * 226.8
 * 235.8 
 * 239.8
 * 243
 * 245.5
 * 248-258
 * 261
 * 263
 * 269
 * 329
 * 332
 * 343.5
 * 346
 * 382
 * 384
 * 396
 * 400.5
 * 406.5
 * 434.5
 * 448
 * 483.8
 * 497
 * 529
 * 
 * Stage:
 * 195
 * 314
 * 535.5
 * 
 */