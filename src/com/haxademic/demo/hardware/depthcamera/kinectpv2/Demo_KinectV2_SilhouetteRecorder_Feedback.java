package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.LeaveWhiteFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.filters.pshader.compound.ReactionDiffusionStepFilter;
import com.haxademic.core.draw.image.ImageFramesHistory;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.render.FrameLoop;

import KinectPV2.KinectPV2;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_KinectV2_SilhouetteRecorder_Feedback
extends PAppletHax {

    public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

    protected KinectPV2 kinect;

    protected PGraphics bufferRgb;
    protected PGraphics bufferMask;

    protected ImageFramesHistory recorder;

    protected void config() {
        Config.setProperty( AppSettings.WIDTH, 1280 );
        Config.setProperty( AppSettings.HEIGHT, 960 );
        Config.setProperty( AppSettings.SHOW_DEBUG, true );
        Config.setProperty( AppSettings.FILLS_SCREEN, false );
    }

    protected void firstFrame() {
        kinect = new KinectPV2(p);
        kinect.enableDepthImg(true);
        kinect.enableColorImg(true);
        kinect.enableDepthMaskImg(true);
        kinect.enableBodyTrackImg(true);
        kinect.enableInfraredImg(true);
        kinect.init();

        // init buffers
        bufferRgb = PG.newPG(p.width, p.height);
        bufferMask = PG.newPG(p.width, p.height);

        // init image recorder
        recorder = new ImageFramesHistory(p.width/2, p.height/2, 50);
    }

    protected void drawApp() {
        if(p.frameCount == 1) p.background(0);

        // crop images into buffers to match sizes
        PImage rgb = kinect.getColorImage();
        PImage silhouette = kinect.getBodyTrackImage();
        DebugView.setTexture("kinect.getColorImage()", rgb);
        DebugView.setTexture("kinect.getBodyTrackImage()", silhouette);
        ImageUtil.cropFillCopyImage(rgb, bufferRgb, true);
        ImageUtil.cropFillCopyImage(silhouette, bufferMask, true);
        InvertFilter.instance().setOnContext(bufferMask);
        bufferRgb.mask(bufferMask);
        
        // for b/w mode, knock out black
        ThresholdFilter.instance().setOnContext(bufferMask);
        LeaveWhiteFilter.instance().setOnContext(bufferMask);
        if(p.frameCount % 4 < 2) InvertFilter.instance().setOnContext(bufferMask);

        
        // add to history
        recorder.addFrame(bufferMask, true);
//        recorder.addFrame(bufferRgb, true);
        
        // draw image history
        pg.beginDraw();

        // post/pre effects
//        BrightnessStepFilter.instance().setBrightnessStep(-1/255f);
//        BrightnessStepFilter.instance().applyTo(pg);
        
        RotateFilter.instance().setRotation(FrameLoop.osc(0.025f, -0.04f, 0.04f));
        RotateFilter.instance().setZoom(0.96f);
        RotateFilter.instance().setOnContext(pg);
        
        p.blendMode(PBlendModes.DIFFERENCE);
        // draw image layers from the back, scaling down
        PG.setDrawCenter(pg);
        PG.setCenterScreen(pg);
        int numImages = recorder.images().length;
        for(int i=0; i < numImages; i++) {
            float progress = (float) i / numImages;
            int iRev = numImages - 1 - i;
            
            // frame offset for b/w mode
            int offsetI = (p.frameCount % 2 == 0) ? 0 : 1;
//            if(iRev < numImages - 2) iRev += offsetI;

            PImage img = recorder.getSortedFrame(iRev);
            float scale = P.map(progress, 0, 1, 5, 2);

            // set tint & draw
//            PG.setPImageAlpha(pg, progress);
//            pg.tint(P.sin(i/5f) * 127 + 127, P.sin(i/3f) * 127 + 127, P.sin(i/12f) * 127 + 127, 255 * progress);
            pg.image(img, 0, 0, img.width * scale, img.height * scale);
        }
        
        ReactionDiffusionStepFilter.applyTo(pg, 3, 2, 1, 1, 1.75f, true, 1.f, 0.5f);
        
        // draw last image on top
        PG.resetPImageAlpha(pg);
//        pg.image(recorder.getSortedFrame(0), 0, 0);
        pg.image(bufferRgb, 0, 0);
        
        pg.endDraw();
        
        // draw to screen 
        ImageUtil.copyImage(pg, p.g);
//         recorder.drawDebug(p.g);
    }

}
