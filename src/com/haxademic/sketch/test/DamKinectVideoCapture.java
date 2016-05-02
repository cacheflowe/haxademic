package com.haxademic.sketch.test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openkinect.processing.Kinect2;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.image.filters.shaders.BrightnessFilter;
import com.haxademic.core.image.filters.shaders.SaturationFilter;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PImage;

public class DamKinectVideoCapture
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected Kinect2 kinect2;
	
	protected int startRecordTime = -1;
	protected int lastCaptureTime = -1;
	protected int captureFPS = (int)(8f * 1000f/30f); // capture every 8 frames at 30fps 
	
	protected int ONE_MINUTE = 60 * 1000;
	protected String curRenderDir;
	protected int renderFrameCount;
	
	protected Process _ffmpegScript;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERER, P.P2D ); // P.JAVA2D P.FX2D P.P2D P.P3D
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.SHOW_STATS, true );
	}

	public void setup() {
		super.setup();
		
		// init kinect
		kinect2 = new Kinect2(this);
		kinect2.initVideo();
		kinect2.initDevice();
		
		// create output directory if needed
		FileUtil.createDir(FileUtil.getHaxademicOutputPath() + "_dancelab");
	}
			
	public void drawApp() {	
		// draw kinect camera
		p.image(kinect2.getVideoImage(), 0, 0, width, height);

		// special effects
		BrightnessFilter.instance(p).setBrightness(1.5f);
		BrightnessFilter.instance(p).applyTo(p);
		SaturationFilter.instance(p).setSaturation(0.3f);
		SaturationFilter.instance(p).applyTo(p);
		
		// capturing
		captureFrame();
	}
	
	////////////////////////////////
	// Capture frames efficiently
	// .tga is apparently the fastest to export, says amnon owed
	////////////////////////////////
	protected void captureFrame() {
		if(startRecordTime != -1 && p.millis() < startRecordTime + ONE_MINUTE) {
			if(p.millis() > lastCaptureTime + captureFPS) {
				final PImage capturedFrame = p.get();
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
	}

}
