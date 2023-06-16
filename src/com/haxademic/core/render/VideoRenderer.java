package com.haxademic.core.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputESS;
import com.haxademic.core.system.DateUtil;
import com.haxademic.core.system.SystemUtil;

import krister.Ess.AudioChannel;
import processing.core.PGraphics;

public class VideoRenderer {
	
	protected PGraphics pg;
	protected UMovieMakerCustom movieMaker;
	protected AudioInputESS audioInput;
	protected AudioChannel audioPlayer;
	protected int curFrame = 0;
	protected int audioPos = 0;
	protected float audioCurSeconds;
	protected String timestampStart;
	protected String outputDir;
	protected Boolean isRendering = false;
	protected Boolean renderSimulation = false;
	protected Boolean audioSimulation = false;
	protected float audioCurSeconds = 0;
	protected float fps;
	public static final int OUTPUT_TYPE_IMAGE = 0;
	public static final int OUTPUT_TYPE_MOVIE = 1;
	public static int outputType = OUTPUT_TYPE_MOVIE;
	public static void setOutputVideo() { outputType = OUTPUT_TYPE_MOVIE;	}
	public static void setOutputImages() { outputType = OUTPUT_TYPE_IMAGE;	}
	public int timeStarted;
	public static String IMAGE_EXTENSION = "tga";

	public VideoRenderer(int framesPerSecond, String outputDir ) {
		this.pg = P.p.g;
		fps = framesPerSecond;
		this.outputDir = outputDir;
		audioSimulation = Config.getBoolean( AppSettings.RENDER_AUDIO_SIMULATION, false );
		renderSimulation = Config.getBoolean( AppSettings.RENDER_SIMULATION, false );
	}
	
	public void setPG(PGraphics pg) {
		this.pg = pg;
	}
	
	public void startVideoRenderer() {
		// bail if simulating
		if(audioSimulation) return;
		
		// create timestamp for file output
		timestampStart = SystemUtil.getTimestamp();

		// initialize movie renderer
		if(outputType == OUTPUT_TYPE_MOVIE && !renderSimulation) {
			if( FileUtil.fileOrPathExists(outputDir) == false ) FileUtil.createDir(outputDir);
			movieMaker = new UMovieMakerCustom(pg, outputDir+"render-"+timestampStart+".mov", pg.width, pg.height, (int)fps);
			P.println("VideoRenderer started :: "+timestampStart);
		}
		
		// set rendering flag
		isRendering = true;
		timeStarted = P.p.millis();
	}
	
	public void startRendererForAudio( String audioFile ) {
		// grab the system ESS audio input object
		audioInput = (AudioInputESS) AudioIn.audioInput;
		
		// store audio analysis object
		P.println("Rendering with audio: "+audioFile);
		
		// fire it up
		startVideoRenderer();
		
		// load & play audio
		audioPlayer = new AudioChannel( P.p.dataPath( audioFile ) );
		audioPlayer.play();
		if(audioSimulation == false) {
			audioPlayer.pause();
			Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 999999999); // let the audio track determine end of movie
		} else {
			audioPlayer.loop();
		}
	}

	/**
	 * Called by the PApplet to render the current frame.
	 * If using audio, make sure to call analyzeAudio() at the beginning of draw(), and renderFrame() at the end draw();
	 */
	public void renderFrame() {
		// don't do anything if renderer hasn't been inited, or has been stopped
		if( isRendering == true ) {			
			// if movie, add frame to MovieMaker file
			if ( outputType == OUTPUT_TYPE_MOVIE ) {
				if(!renderSimulation) {
					pg.loadPixels();
					movieMaker.addFrame();
				}
				
			// otherwise, save an image
			} else {
				if(!renderSimulation) {
					String outputDir = FileUtil.haxademicOutputPath() + timestampStart + "/";
					if( FileUtil.fileOrPathExists(outputDir) == false ) FileUtil.createDir(outputDir);
					String filename = "out_" + P.nf( curFrame, 8 ) + "." + IMAGE_EXTENSION;
					pg.save(outputDir + filename);
				}
			}
			
			// keep track of rendered frame count & debug output
			curFrame++;
			debugRenderProgress();
		}
	}
	
	// called by Renderer before each frame is drawn
	public void analyzeAudio() {
		if(audioSimulation) {
			updateAudioSimulation();
		} else {
			updateAudioRenderFrame();
		}
	}

	protected void updateAudioSimulation() {
		// restart realtime audio on loop, if there's a loop
		if(FrameLoop.loopFrames() != 0) {
			if(FrameLoop.loopCurFrame() == 0) {
				audioPlayer.cue(0);
				audioPlayer.loop();
			}
		}
		// pass through audio player directly to audio input data
		audioInput.updateForRender(audioPlayer, -1);
		// keep track of audio time
		audioCurSeconds = audioPlayer.cue / audioPlayer.sampleRate;
	}
	
	protected void updateAudioRenderFrame() {
		// get position in wav file
		audioPos = (int)( curFrame * audioPlayer.sampleRate / fps );
		audioCurSeconds = audioPos / audioPlayer.sampleRate;
		// make sure we're still in bounds - kept getting data run-out errors
		if (audioPos < audioPlayer.size) {	//  - (audioPlayer.sampleRate * 0.01f) 
			audioCurSeconds = (float) curFrame / fps;
			audioPlayer.cue(audioPos);
			audioPlayer.volume(0.2f);	
			// if still running & in-bounds, grab next data within the ESS wrapper
			audioInput.updateForRender(audioPlayer, audioPos);
		} else {
			if(outputType == OUTPUT_TYPE_MOVIE) movieMaker.finish();
			stop();  
			P.p.exit();
			return;
		}
	}

	public float audioPosition() {
		return audioCurSeconds;
	}

	protected void debugRenderProgress() {
		// get elapsed time
		int elapsedMillis = P.p.millis() - timeStarted;
		float millisLeft = -1;
		
		// get projected completion time
		String totalFrames = "?";
		int totalFramesInt = -1; 
		int startFrame = Config.getInt(AppSettings.RENDERING_MOVIE_START_FRAME, -1);
		int stopFrame = Config.getInt(AppSettings.RENDERING_MOVIE_STOP_FRAME, -1);
		if(startFrame != -1 && stopFrame != -1) {
			totalFramesInt = stopFrame - startFrame;
			totalFrames = totalFramesInt + "";
			float totalMillisProjected = ((float) totalFramesInt / (float) curFrame) * elapsedMillis;
			millisLeft = totalMillisProjected - elapsedMillis;
		} else if(audioPlayer != null) {
			float audioProg = (float)audioPos/(float)audioPlayer.size;
			totalFramesInt = P.round(curFrame * (1f / audioProg));
			totalFrames = totalFramesInt + "";
			float totalMillisProjected = ((float) totalFramesInt / (float) curFrame) * elapsedMillis;
			millisLeft = totalMillisProjected - elapsedMillis;
		}
		
		// output
		if(!audioSimulation && !renderSimulation) {
			P.println( "= RENDERING ==================" );
			P.println( "= Exporting frame number:   " + curFrame + " / " + totalFrames );
			P.println( "= Elapsed time:             "  + DateUtil.timeFromMilliseconds(elapsedMillis, true));
			if(millisLeft != -1) {
				P.println( "= Expected time left:       "  + DateUtil.timeFromMilliseconds(P.round(millisLeft), true));
				P.println( "= Progress:                 "  + ((float) curFrame / (float) totalFramesInt));
			}
			if(audioPlayer != null) {
				if ((curFrame % 15) == 0) {
					P.println( "= Audio position: " + audioPos + " fps: " + fps + " seconds: " + audioCurSeconds + " _chn.sampleRate = " + audioPlayer.sampleRate + "  position in file: " + audioPos + " / " + audioPlayer.size );
					P.println( "= Audio @ seconds: " + audioCurSeconds + "  Progress: " + Math.round(100f*((float)audioPos/(float)audioPlayer.size)) + "%" );
					P.println( "= Frame Number: " + curFrame );
				}
			}
			P.println( "============================" );
		}
	}

	public void stop() {
		if(isRendering && outputType == OUTPUT_TYPE_MOVIE && !audioSimulation && !renderSimulation) {
			movieMaker.finish();
		}
		isRendering = false;
	}
	
}