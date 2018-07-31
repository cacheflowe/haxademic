package com.haxademic.app.silhouect;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.filters.pshader.LeaveBlackFilter;
import com.haxademic.core.draw.filters.pshader.LeaveWhiteFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.SharpenFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.draw.textures.pgraphics.TextureEQConcentricCircles;
import com.haxademic.core.draw.textures.pgraphics.TextureLinesEQ;
import com.haxademic.core.draw.textures.pgraphics.TextureOuterCube;
import com.haxademic.core.draw.textures.pgraphics.TextureOuterSphere;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import KinectPV2.KinectPV2;
import processing.core.PGraphics;
import processing.core.PImage;

public class Silhouect
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	/**
	 * TODO:
	 * - Write instructions PDF for installers - add bits about setting up sponsor & slideshow images
	 */

	// main buffers
	protected PGraphics mainBuffer;
	protected PGraphics rdBuffer;
	protected PGraphicsKeystone keystone;
	protected boolean testPattern = false;

	// user detection
	protected PGraphics userBuffer;
	protected ArrayList<PGraphics> lastUserBuffer = new ArrayList<PGraphics>();
	protected int userHistoryStorageIndex = 0;
	protected int userHistoryPlaybackIndex = 0;
	protected int USER_HISTORY_SIZE = 6;
	protected KinectPV2 kinect;
	protected boolean noUser = true;

	// instructions / slideshow
	protected ArrayList<PGraphics> slideshow;
	protected int slideshowIndex = -1;
	protected boolean flashFrame = false;
	protected int flashFrameInterval = 300;

	// R/D
	protected int RD_ITERATIONS = 1;
	protected boolean mouseConfig = false;
	float blurLow = 0.75f;
	float blurHigh = 1.15f;
	float blurMid = (blurHigh + blurLow) / 2f;
	float blurHalf = (blurHigh - blurLow) / 2f;

	float sharpLow = 1.2f;
	float sharpHigh = 7f;
	float sharpMid = (sharpHigh + sharpLow) / 2f;
	float sharpHalf = (sharpHigh - sharpLow) / 2f;

	// sponsor image
	protected PImage sponsorImg;
	
	// audio texture
	protected BaseTexture audioTextures[];
	protected int audioTextureIndex = 0;
	protected boolean deformMode = true;
	
	protected void overridePropsFile() {
		if(P.platform != P.MACOSX) {
			p.appConfig.setProperty( AppSettings.WIDTH, 1920 );
			p.appConfig.setProperty( AppSettings.HEIGHT, 1080 );
		} else {
			p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
			p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		}
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, false );
		p.appConfig.setProperty( AppSettings.INIT_MINIM_AUDIO, false );
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, true );
		p.appConfig.setProperty( AppSettings.AUDIO_DEBUG, true );
	}

	public void setupFirstFrame() {
		// main buffer
		mainBuffer = p.createGraphics(1920, 1080, PRenderers.P2D);
		rdBuffer = p.createGraphics(1920, 1080, PRenderers.P2D);
		keystone = new PGraphicsKeystone(p, mainBuffer, 10, FileUtil.getFile("text/keystoning/silhouect.txt"));
		
		// init kinect
		if(P.platform != P.MACOSX) {
			kinect = new KinectPV2(p);
			kinect.enableDepthImg(true);
			kinect.enableDepthMaskImg(true);
			kinect.enableBodyTrackImg(true);
			kinect.enableInfraredImg(true);
			// kinect.enableColorImg(true);
			kinect.init();
		}
		
		// init instructions/slideshow
		String imagesPath = FileUtil.getFile("images/silhouect/slideshow");
		ArrayList<String> files = FileUtil.getFilesInDirOfTypes(imagesPath, "png,jpg");
		slideshow = new ArrayList<PGraphics>();
		for (int i = 0; i < files.size(); i++) {
			P.println("Loaded image:", i, files.get(i));
			String filePath = files.get(i);
			PGraphics image = ImageUtil.imageToGraphics(p.loadImage(filePath));
			LeaveWhiteFilter.instance(p).applyTo(image);
			slideshow.add(image);
		}
		
		// load sponsor image if it exists
		String sponsorImgPath = FileUtil.getFile("images/silhouect/sponsor.png");
		if(FileUtil.fileExists(sponsorImgPath)) {
			sponsorImg = p.loadImage(sponsorImgPath);
		}
		
		// load audio texture
		audioTextures = new BaseTexture[] {
			new TextureOuterCube(mainBuffer.width, mainBuffer.height),
			new TextureOuterSphere(mainBuffer.width, mainBuffer.height),
			new TextureEQConcentricCircles(mainBuffer.width, mainBuffer.height),
			new TextureLinesEQ(mainBuffer.width, mainBuffer.height),
		};
		
		// init help menu
		p.debugView.setHelpLine("Key Commands:", "");
		p.debugView.setHelpLine("[R]", "Reset keystone");
		p.debugView.setHelpLine("[D]", "Keystone test pattern");
	}
	
	///////////////////////////////////////
	// USER CAPTURE
	///////////////////////////////////////

	protected void lazyCreateBuffer() {
		if(userBuffer == null && kinect != null && kinect.getBodyTrackImage().width > 10) {
			userBuffer = p.createGraphics(kinect.getBodyTrackImage().width, kinect.getBodyTrackImage().height, PRenderers.P3D);
		}
	}
	
	protected void updateUserBuffer() {
		// check number of users - hide instructions if user appears
		if(numUsers() > 0) noUser = false; 
		p.debugView.setValue("NUM USERS", numUsers());
		
		// set kinect silhouette on buffer
		userBuffer.beginDraw();
		userBuffer.clear();
		userBuffer.image(kinect.getBodyTrackImage(), 0, 0);
		userBuffer.endDraw();
		
		// turn buffer white pixels to transparent 
		LeaveBlackFilter.instance(p).applyTo(userBuffer);
		p.debugView.setTexture(userBuffer);
	}
	
	protected int numUsers() {
		return (kinect != null) ? kinect.getNumOfUsers() : 0;
	}
	
	protected void drawUser() {
		if(userBuffer != null && noUser == false) {
			ImageUtil.cropFillCopyImage(userBuffer, rdBuffer, true);   // fill transparent silhouette buffer to screen
		}
	}
	
	protected void storeUserFrame() {
		if(noUser == false && flashFrame == true) {
			// if user, add a frame to the array
			if(lastUserBuffer.size() < USER_HISTORY_SIZE) {
				PGraphics newUserBuffer = p.createGraphics(userBuffer.width, userBuffer.height, PRenderers.P2D); 
				if(lastUserBuffer.size() == 0) slideshow.add(newUserBuffer);	// add a single PGraphics to end of slideshow array
				lastUserBuffer.add(newUserBuffer);
				userHistoryStorageIndex = lastUserBuffer.size() - 1;
			} else {
				userHistoryStorageIndex++;
				if(userHistoryStorageIndex >= lastUserBuffer.size()) userHistoryStorageIndex = 0;
			}
			
			// draw current frame to oldest buffer
			PGraphics oldBuffer = lastUserBuffer.get(userHistoryStorageIndex);
			oldBuffer.beginDraw();
			oldBuffer.clear();
			oldBuffer.copy(userBuffer, 0, 0, userBuffer.width, userBuffer.height, 0, 0, oldBuffer.width, oldBuffer.height);
			oldBuffer.endDraw();
		}
	}
	
	///////////////////////////////////////
	// REACTION DIFFUSION
	///////////////////////////////////////
	
	protected void flashScreen() {
		if(p.frameCount < 10 || p.frameCount % flashFrameInterval == 0) {
			flashFrame = true;
			
			// only draw background at the start. also flash/reset screen every few seconds with almost-black (this still resolves to b&w from the RD functions);
			rdBuffer.background(20);
			
			// check for user
			noUser = (numUsers() == 0);
			
			// advance slideshow
			slideshowIndex++;
			if(slideshowIndex >= slideshow.size()) slideshowIndex = 0;
			
			// if end of slideshow, move to the next user image (if we've created a history array)
			if(lastUserBuffer.size() > 0) {
				if(slideshowIndex == slideshow.size() - 1) {
					userHistoryPlaybackIndex++;
					if(userHistoryPlaybackIndex >= lastUserBuffer.size()) userHistoryPlaybackIndex = 0;
					slideshow.set(slideshow.size() - 1, lastUserBuffer.get(userHistoryPlaybackIndex));
				}
			}
		} else {
			flashFrame = false;
		}
		
		// if no users, set a static image
		if(noUser == true) {
			DrawUtil.setPImageAlpha(rdBuffer, 0.8f);
			ImageUtil.cropFillCopyImage(slideshow.get(slideshowIndex), rdBuffer, true);
			DrawUtil.resetPImageAlpha(rdBuffer);
		}
	}
	
	protected void applyRD() {
		// effect config - 0.7, 1.2  ->  1.2, 10.0
		float osc = P.sin(p.frameCount * 0.005f);
				
		float blurAmp = blurMid + blurHalf * osc;
		float sharpAmp = sharpMid + sharpHalf * osc;
		
		if(mouseConfig) {
			blurAmp = p.mousePercentX() * 3f;
			sharpAmp = p.mousePercentY() * 15f;
		}
		
		p.debugView.setValue("blurAmp", blurAmp);
		p.debugView.setValue("sharpAmp", sharpAmp);

		// run shaders
		for (int i = 0; i < RD_ITERATIONS; i++) {			
			BlurHFilter.instance(p).setBlurByPercent(blurAmp, rdBuffer.width);
			BlurHFilter.instance(p).applyTo(rdBuffer);
			BlurVFilter.instance(p).setBlurByPercent(blurAmp, rdBuffer.height);
			BlurVFilter.instance(p).applyTo(rdBuffer);
			SharpenFilter.instance(p).setSharpness(sharpAmp);
			SharpenFilter.instance(p).applyTo(rdBuffer);
		}
		
		// ensure black & white
		SaturationFilter.instance(p).setSaturation(0); 
		SaturationFilter.instance(p).applyTo(rdBuffer); 
	}
	
	protected void drawSilhouetteGraphics() {
		rdBuffer.beginDraw();
		flashScreen();
		drawUser();
		applyRD();	// do reaction-diffusion feedback
		rdBuffer.endDraw();
	}
	
	///////////////////////////////////////
	// AUDIO TEXTURE
	///////////////////////////////////////
	
	protected void updateAudioTexture() {
		// update audio texture
		if(flashFrame == true) {
			if(MathUtil.randBooleanWeighted(p, 0.65f)) deformMode = !deformMode;
			if(MathUtil.randBooleanWeighted(p, 0.65f)) nextAudioTexture();
			curAudioTexture().newRotation();
		}
		curAudioTexture().update();
		
		// post-process audio texture
		if(deformMode == false) {
			BrightnessFilter.instance(p).setBrightness(0.5f);
			BrightnessFilter.instance(p).applyTo(curAudioTexture().texture());
		} else {
			BlurHFilter.instance(p).setBlurByPercent(3f, curAudioTexture().texture().width);
			BlurVFilter.instance(p).setBlurByPercent(3f, curAudioTexture().texture().height);
			BlurHFilter.instance(p).applyTo(curAudioTexture().texture());
			BlurVFilter.instance(p).applyTo(curAudioTexture().texture());
			BlurHFilter.instance(p).applyTo(curAudioTexture().texture());
			BlurVFilter.instance(p).applyTo(curAudioTexture().texture());
			BlurHFilter.instance(p).applyTo(curAudioTexture().texture());
			BlurVFilter.instance(p).applyTo(curAudioTexture().texture());
			ContrastFilter.instance(p).setContrast(1.5f);
			ContrastFilter.instance(p).applyTo(curAudioTexture().texture());
		}
	}
	
	protected void drawAudioTextureToBuffer() {
		if(deformMode == true) {
			// deform main buffer
			DisplacementMapFilter.instance(p).setMap(curAudioTexture().texture());
			DisplacementMapFilter.instance(p).setAmp(0.009f);
			DisplacementMapFilter.instance(p).setMode(3);
			DisplacementMapFilter.instance(p).applyTo(mainBuffer);
		} else {
			// just blend it
			mainBuffer.blendMode(PBlendModes.SUBTRACT);
			ImageUtil.drawImageCropFill(curAudioTexture().texture(), mainBuffer, true);
			mainBuffer.blendMode(PBlendModes.BLEND);
			ContrastFilter.instance(p).setContrast(1.5f);
			ContrastFilter.instance(p).applyTo(mainBuffer);
		}
	}
	
	protected BaseTexture curAudioTexture() {
		return audioTextures[audioTextureIndex];
	}
	
	protected void nextAudioTexture() {
		audioTextureIndex++;
		if(audioTextureIndex >= audioTextures.length) audioTextureIndex = 0;
	}
	
	///////////////////////////////////////
	// DRAW LOOP
	///////////////////////////////////////
	
	public void drawApp() {
		p.background(0);
		lazyCreateBuffer();
		if(userBuffer != null) updateUserBuffer();
		if(audioTextures != null) updateAudioTexture();
		drawSilhouetteGraphics();
		drawMainBuffer();
		storeUserFrame();
		// draw to screen
		if(testPattern == true) keystone.drawTestPattern();
		keystone.update(p.g, true);
	}
	
	protected void drawMainBuffer() {
		mainBuffer.beginDraw();
		mainBuffer.noStroke();
		ImageUtil.cropFillCopyImage(rdBuffer, mainBuffer, true);
		if(audioTextures != null) drawAudioTextureToBuffer();
		if(sponsorImg != null) mainBuffer.image(sponsorImg, mainBuffer.width - sponsorImg.width, mainBuffer.height - sponsorImg.height);
		drawProgressBar();
		if(p.showDebug && audioTextures != null) mainBuffer.image(curAudioTexture().texture(), 0, mainBuffer.height - 90, 160, 90);
		mainBuffer.endDraw();
	}
	
	protected void drawProgressBar() {
		float flashProgress = ((float) p.frameCount % (float) flashFrameInterval) / (float) flashFrameInterval;
		mainBuffer.fill(0);
		mainBuffer.rect(0, 0, mainBuffer.width, 20);
		mainBuffer.fill(255);
		mainBuffer.rect(0, 0, mainBuffer.width * flashProgress, 8);
	}
	
	///////////////////////////////////////
	// KEYBOARD INPUT
	///////////////////////////////////////
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') testPattern = !testPattern;
		if(p.key == 'm') deformMode = !deformMode;
		if(p.key == 'r') keystone.resetCorners();
		if(p.key == ']') nextAudioTexture();
		if(p.key == '[') {
			audioTextureIndex--;
			if(audioTextureIndex < 0) audioTextureIndex = audioTextures.length - 1;
		}
	}

}
