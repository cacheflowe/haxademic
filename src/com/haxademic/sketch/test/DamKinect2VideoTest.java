package com.haxademic.sketch.test;

import org.openkinect.processing.Kinect2;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.image.filters.shaders.EdgesFilter;
import com.haxademic.core.system.FileUtil;

import processing.video.Movie;

public class DamKinect2VideoTest
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	Movie movie;
	Kinect2 kinect2;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1920 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1080 );
	}


	public void setup() {
		super.setup();

		kinect2 = new Kinect2(this);
		kinect2.initVideo();
		kinect2.initDevice();

		movie = new Movie(this, FileUtil.getFile("video/dancelab-demo.mov")); 
		movie.play();
		movie.loop();
		movie.speed(0.5f);
	}

	public void drawApp() {
		background(0);

		tint( 255, 255 );
		image(movie, 0, 0, width, height);
		EdgesFilter.instance(p).applyTo(p);

		tint( 255, 90 );
		image(kinect2.getVideoImage(), 0, 0, width, height);
		
		fill(255);
		text("Framerate: " + (int)(frameRate), 10, 515);
	}

}
