package com.haxademic.app.dancelab.prototype;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.Base64Image;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

public class SaveImageSequenceFromPGraphics
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	Movie movie;
	PGraphics buffer;
	PImage copyImg; // used to fix image format
	String outputFormat = "jpg";
	String curRenderDir;
	boolean sendToAMQP = false;
	protected int lastCaptureTime = -1;
	protected int startRecordTime = -1;
	protected float CAPTURE_SECONDS = 60f;
	protected float CAPTURE_MILLIS = CAPTURE_SECONDS * 1000f;
	protected float CAPTURE_FPS = 30f / 4f;
	protected int FRAMES_TO_CAPTURE = (int)(CAPTURE_SECONDS * CAPTURE_FPS);
	protected int CAPTURE_FPS_MILLIS = (int)(1000f / CAPTURE_FPS);
	protected int framesRendered = 0;
	protected int totalRenderingTime = 0;



	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 720 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1280 );
	}


	public void setup() {
		super.setup();
		
		buffer = p.createGraphics(p.width, p.height, P.P2D);
		copyImg = p.createImage(buffer.width, buffer.height, P.RGB);

		movie = new Movie(this, FileUtil.getFile("video/dancelab/015.mov")); 
		movie.play();
		movie.loop();
		movie.speed(1.0f);
		
		P.println("CAPTURE_SECONDS = ", CAPTURE_SECONDS);
		P.println("CAPTURE_MILLIS = ", CAPTURE_MILLIS);
		P.println("CAPTURE_FPS = ", CAPTURE_FPS);
		P.println("FRAMES_TO_CAPTURE = ", FRAMES_TO_CAPTURE);
		P.println("CAPTURE_FPS_MILLIS = ", CAPTURE_FPS_MILLIS);
		
		// .jpg = 1769ms rendering | 2mb
		// .png = 4003ms rendering | 16mb
		// .tga = 818ms rendering | 51.4mb
		
		// .jpg = 32825ms rendering  | 37mb
		// .png = 109896ms rendering | 275mb
		// .tga = 13109ms rendering  | 896mb
	}

	public void drawApp() {
		p.background(0);
		
		buffer.beginDraw();
		buffer.background(0);
		PG.setDrawCenter(buffer);
		buffer.translate(buffer.width/2, buffer.height/2);
		buffer.rotate(P.PI/2f);
		buffer.image(movie, 0, 0, height, width);
		buffer.endDraw();
		
		p.pushMatrix();
		p.image(buffer, 0, 0, p.width, p.height);
		p.popMatrix();

		p.fill(255);
		p.text("Framerate: " + (int)(p.frameRate), 10, 515);
		
		captureFrame();
	}
	
	protected void captureFrame() {
		if(startRecordTime != -1 && framesRendered < FRAMES_TO_CAPTURE) {
			if(p.millis() > startRecordTime + (framesRendered * CAPTURE_FPS_MILLIS)) {
				final int frameRenderStartTime = p.millis(); 
				copyImg.copy(buffer, 0, 0, buffer.width, buffer.height, 0, 0, buffer.width, buffer.height);
				new Thread(new Runnable() { public void run() {
					if(framesRendered == FRAMES_TO_CAPTURE) return;
					framesRendered++;
					if(sendToAMQP == false) {
						copyImg.save(curRenderDir + String.format("%05d", framesRendered) + "." + outputFormat);
					} else {
						P.println(Base64Image.encodePImageToBase64(copyImg, outputFormat));
					}
					totalRenderingTime += p.millis() - frameRenderStartTime; 
					P.println("Saving frame: "+framesRendered);
			    }}).start();
			} 
		} else if(framesRendered == FRAMES_TO_CAPTURE) {
			P.println("Total export time for"  + framesRendered + " frames of format " + " ." + outputFormat + " = " + totalRenderingTime);
			startRecordTime = -1;
			framesRendered = 0;
		}
	}


	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			// set up image sequence output
			curRenderDir = FileUtil.getHaxademicOutputPath() + "_dancelab/" + SystemUtil.getTimestamp() + "/";
			FileUtil.createDir(curRenderDir);
			startRecordTime = p.millis();
			totalRenderingTime = 0;
			
			// capture a frame in all available formats to check filesize
			//			buffer.save(FileUtil.getHaxademicOutputPath() + "_dancelab/" + String.format("%05d", p.frameCount) + ".tga");
			//			buffer.save(FileUtil.getHaxademicOutputPath() + "_dancelab/" + String.format("%05d", p.frameCount) + ".png");
			//			buffer.save(FileUtil.getHaxademicOutputPath() + "_dancelab/" + String.format("%05d", p.frameCount) + ".jpg");
			//			buffer.save(FileUtil.getHaxademicOutputPath() + "_dancelab/" + String.format("%05d", p.frameCount) + ".tif");
		}
	}

}
