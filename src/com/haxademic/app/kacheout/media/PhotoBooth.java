package com.haxademic.app.kacheout.media;

import processing.core.PImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.image.ScreenUtil;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class PhotoBooth {
	public static void snapGamePhoto( PAppletHax p, int stageWidth, int stageHeight ) {
		String projectPath = FileUtil.getProjectAbsolutePath();
		
		// save screenshot and open it back up
		String screenshotFile = ScreenUtil.saveScreenshot( p, projectPath + "/bin/output/kacheout/kacheout-" );
		PImage screenshot = p.loadImage( screenshotFile );

		if( p.kinectWrapper != null && p.kinectWrapper.isActive() ) {
			// save normal kinect image
			p.kinectWrapper.getRgbImage().save( projectPath + "/bin/output/kacheout/kacheout-" + SystemUtil.getTimestampFine( p ) + "-rgb.png" );

			// create composite image
			float screenToOutputWidthRatio = 640f / (float)stageWidth;
			int screenShotHeight = Math.round( stageHeight * screenToOutputWidthRatio );
			PImage img = p.createImage(640, 480 + screenShotHeight, P.RGB);

			// paste 2 images together and save
//			img.copy( ImageUtil.getReversePImage( p.kinectWrapper.getRgbImage() ), 0, 0, 640, 480, 0, 0, 640, 480 );
			img.copy( p.kinectWrapper.getRgbImage(), 0, 0, 640, 480, 0, 0, 640, 480 );
			img.copy( screenshot, 0, 0, stageWidth, stageHeight, 0, 481, 640, screenShotHeight );
			img.save( projectPath + "/bin/output/kacheout/kacheout-" + SystemUtil.getTimestampFine( p ) + "-comp.png" );
		}

		// clean up screenshot
		//			boolean success = ( new File( screenshotFile ) ).delete();
		//			if (!success) p.println("counldn't delete screenshot");
	}

}
