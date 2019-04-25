package com.haxademic.app.dancelab.prototype;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openkinect.processing.Kinect2;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.filters.pshader.BlurBasicFilter;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.EdgesFilter;
import com.haxademic.core.draw.filters.pshader.SharpenFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

public class BasicGhostAndKinectCapture
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected Kinect2 kinect2;
	protected boolean kinectActive = true;
	protected PGraphics mask;
	protected PGraphics movieBuffer;
	
	protected Movie movie;
	protected boolean movieActive = false;

	protected int startRecordTime = -1;
	protected int lastCaptureTime = -1;
	protected int captureFPS = (int)(4f * 1000f/30f); // capture every 8 frames at 30fps 
	
	protected int ONE_MINUTE = 60 * 1000;
	protected String curRenderDir;
	protected int renderFrameCount;
	
	protected Process _ffmpegScript;
	
	protected boolean sharpening = false;
	protected boolean dilating = false;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERER, P.P2D ); // 
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
	}

	public void setup() {
		super.setup();
		
		if(kinectActive == true) {
			// init camera
			kinect2 = new Kinect2(this);
			kinect2.initVideo();
			kinect2.initDevice();
			// create output directory if needed
			FileUtil.createDir(FileUtil.getHaxademicOutputPath() + "_dancelab");
		}
		
		if(movieActive == true) {
			movie = new Movie(this, FileUtil.getFile("video/dancelab/15.mov")); 
			movie.play();
			movie.loop();
			movie.speed(1f);
			
			movieBuffer = p.createGraphics(p.width, p.height, P.P2D);
			mask = p.createGraphics(p.width, p.height, P.P2D);
		}		
	}
			
	public void drawApp() {	
		// draw kinect camera
		if(kinectActive == true) {
			p.image(kinect2.getVideoImage(), 0, 0, width, height);
		}
		p.background(0);

		if(movieActive == true) {
			if(movie.width > 0) {
				// draw ghost movie overlay
				movieBuffer.beginDraw();
				movieBuffer.clear();
				movieBuffer.background(255);
				movieBuffer.endDraw();
		
				mask.beginDraw();
				mask.clear();
				mask.image(movie, 0, 0, width, height);
				mask.endDraw();
				EdgesFilter.instance(p).applyTo(mask);
				BlurProcessingFilter.instance(p).applyTo(mask);
				if(dilating == true) { 
	//				DilateFilter.instance(p).applyTo(mask);
	//				ErosionFilter.instance(p).applyTo(mask);
					BlurBasicFilter.instance(p).applyTo(mask);
				}
				if(sharpening == true) { 
					SharpenFilter.instance(p).applyTo(mask);
				}
				
				movieBuffer.mask( mask );
				p.image(movieBuffer, 0, 0);
	//			p.image(movieBuffer, -1, -1);
	//			p.image(movieBuffer, 1, 1);
	//			p.image(movieBuffer, -1, 0);
	//			p.image(movieBuffer, 1, 0);
	//			p.image(movieBuffer, 1, -1);
	//			p.image(movieBuffer, -1, 1);
			}
		}
		
		// special effects
//		BrightnessFilter.instance(p).setBrightness(1.5f);
//		BrightnessFilter.instance(p).applyTo(p);
//		SaturationFilter.instance(p).setSaturation(0.3f);
//		SaturationFilter.instance(p).applyTo(p);
		
		// capturing
		if(kinectActive == true) {
			captureFrame();
		}
	}
	
	////////////////////////////////
	// Capture frames efficiently
	// .tga is apparently the fastest to export, says amnon owed
	////////////////////////////////
	protected void captureFrame() {
		if(startRecordTime != -1 && p.millis() < startRecordTime + ONE_MINUTE) {
			if(p.millis() > lastCaptureTime + captureFPS) {
				final PImage capturedFrame = kinect2.getVideoImage();
				new Thread(new Runnable() { public void run() {
					capturedFrame.save(curRenderDir + String.format("%05d", renderFrameCount) + ".tga");
					renderFrameCount++;
			    }}).start();
				lastCaptureTime = p.millis();
			} 
		} else if(lastCaptureTime != -1 && p.millis() > startRecordTime + ONE_MINUTE) {
			startRecordTime = -1;
			lastCaptureTime = -1;
			generateMovieFile();
		}
	}

	
	public void generateMovieFile() {
		ProcessBuilder pb = new ProcessBuilder(new String[]{"/bin/bash", "tgaSequence2mp4.sh", curRenderDir});
		Map<String, String> env = pb.environment();
		env.put("PATH", env.get("PATH")+":/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin/");    // adds ffmpeg path http://stackoverflow.com/questions/13696644/providing-a-hint-to-processbuilder-to-help-commanline-utility-use-other-utilites
		pb.inheritIO();	// sends shell script output to console
		pb.directory(new File( FileUtil.getHaxademicUtilScriptsPath() ));
		try {
			_ffmpegScript = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		// start capturing
		if(p.key == ' ') {
			curRenderDir = FileUtil.getHaxademicOutputPath() + "_dancelab/" + SystemUtil.getTimestamp(p) + "/";
			FileUtil.createDir(curRenderDir);
			renderFrameCount = 1;
			startRecordTime = p.millis();
		}
		if(p.key == 'd') {
			dilating = !dilating;
		}
		if(p.key == 's') {
			sharpening = !sharpening;
		}
	}

}
