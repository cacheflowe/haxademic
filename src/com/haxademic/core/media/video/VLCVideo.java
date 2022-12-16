package com.haxademic.core.media.video;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;

import VLCJVideo.VLCJVideo;
import processing.core.PApplet;
import uk.co.caprica.vlcj.player.base.State;

public class VLCVideo
extends VLCJVideo {
    
    // A wrapper for VLCJVideo with some helpers
    
    protected int videoStartTime = -999;
    protected String videoPath;
    
    public VLCVideo(PApplet p) {
        super(p);
    }
    
    public void open(String videoPath) {
        this.videoPath = videoPath;
        if(!FileUtil.fileExists(videoPath)) P.error("Couldn't find video file: " + videoPath);
        super.open(videoPath);
    }

    public void play() {
        videoStartTime = P.p.millis();
        super.play();
    }
    
    public boolean isFinished() {
        return super.state() == State.ENDED || super.state() == State.STOPPED || isDefinitelyFinished();
    }
    
    public boolean isDefinitelyFinished() {
        float timeSinceStart = P.p.millis() - videoStartTime;
        return timeSinceStart > super.duration() + 1000;
    }
    
}
