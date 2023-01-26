package com.haxademic.demo.media.video;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.media.video.VideoWindowJavaFX;
import com.haxademic.core.media.video.VideoWindowJavaFX.IVideoDelegate;

public class Demo_VideoWindowJavaFX
extends PAppletHax
implements IVideoDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected VideoWindowJavaFX videoWindow;
	
    // Need to add this to the VM arguments!
    // --module-path "D:\workspace\haxademic\lib\processing-4\libraries\javafx\library\windows-amd64\modules" --add-modules=javafx.controls,javafx.media

	protected void config() {
	}

	protected void firstFrame() {
	    // launch javafx app
	    VideoWindowJavaFX.launchPlayer("", this);
	}
	
	protected void drawApp() {
		p.background(0);
		p.fill(255);
		
		if(videoWindow != null && videoWindow.getImage() != null) {
		    if(p.key == ' ') p.image(videoWindow.getImage(), 0, 0);
		}
	}


	/////////////////////////////////
	// IVideoDelegate methods
	/////////////////////////////////
	
    public void videoWindowCreated(VideoWindowJavaFX videoWindow) {
        this.videoWindow = videoWindow;
    }
    public void videoWindowLoaded(VideoWindowJavaFX videoWindow) {}
    public void videoWindowBridged(VideoWindowJavaFX videoWindow) {}
    public void videoWindowClosed(VideoWindowJavaFX videoWindow) {}

}

