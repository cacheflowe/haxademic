package com.haxademic.app.musicvideos;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.filters.pgraphics.GPUParticlesLauncher;
import com.haxademic.core.draw.filters.pgraphics.SmokeFeedback;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;
import processing.video.Movie;

public class BrimVFX 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float w = 1280;
	protected float h = 720;
	protected BaseVideoFilter vfxPre;
	protected BaseVideoFilter vfx;
	
//	protected int webcamW = 640;
//	protected int webcamH = 480;
	protected PGraphics videoBuffer;
	protected Movie video;
	
	protected boolean frameDirty = false;
	

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, (int) w);
		p.appConfig.setProperty(AppSettings.HEIGHT, (int) h);
//		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 5);
		p.appConfig.setProperty(AppSettings.FULLSCREEN, false);
		p.appConfig.setProperty(AppSettings.ALWAYS_ON_TOP, false);
	}

	protected void setupFirstFrame() {
//		vfx = new GPUParticlesSheetDisplacer(p.width, p.height, 0.5f);
//		vfx = new PixelTriFilter(p.width, p.height, 20);
//		vfx = new ColorDiff8BitRows(p.width, p.height, 20);
//		vfx = new TileRepeat(p.width, p.height);
//		vfx = new BlobLinesFeedback(p.width, p.height);
//		vfx = new HalftoneCamo(p.width, p.height);
//		vfx = new RadialHistory(p.width, p.height);
		vfxPre = new GPUParticlesLauncher(p.width, p.height);
		vfx = new SmokeFeedback(p.width, p.height);
		
		// build video buffer for frame processing
		videoBuffer = p.createGraphics((int) w, (int) h, PRenderers.P2D);
		
		// load video 
		String videoPath = "/Users/cacheflowe/Downloads/BrimLiski_Duels_RC01_FirstMinute.mov";
		video = new Movie(p, videoPath);
		video.play();
		video.loop();
	}

	public void drawApp() {
		background(0);
		
		if(frameDirty) {
			ImageUtil.cropFillCopyImage(video, videoBuffer, true);
			ContrastFilter.instance(p).setContrast(2.0f);
			ContrastFilter.instance(p).applyTo(videoBuffer);
			
			if(vfxPre != null) {
				// chain it up
				vfxPre.newFrame(videoBuffer);
			} else {
				// send new webcam mirror frame to vfx
				vfx.newFrame(videoBuffer);
			}
			frameDirty = false;
		}
		
		if(vfxPre != null) {
			vfxPre.update();
			vfx.newFrame(vfxPre.image());
			vfx.update();
		} else {
			vfx.update();
		}
		
		// bloom
		pg.beginDraw();
		pg.image(vfx.image(), 0, 0);
		pg.endDraw();
//		BloomFilter.instance(p).setStrength(1f);
//		BloomFilter.instance(p).applyTo(pg);
		
		// draw to screen
		p.image(pg, 0, 0);
	}

	public void movieEvent(Movie m) {
		super.movieEvent(m);
		
//		P.out(p.frameCount);
		frameDirty = true;
	}

}

