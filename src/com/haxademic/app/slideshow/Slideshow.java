package com.haxademic.app.slideshow;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

import com.haxademic.app.slideshow.slides.SlideImage;
import com.haxademic.app.slideshow.slides.SlideshowState;
import com.haxademic.app.slideshow.text.SlideCaption;
import com.haxademic.app.slideshow.text.SlideTitle;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.data.store.AppStore;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.BrightnessBumper;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.MouseShutdown;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;

public class Slideshow
extends PAppletHax
{
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// MAIN CANVAS
	protected PGraphics buffer;
	protected PGraphics bufferBg;
	protected PGraphicsKeystone pgKeystone;
	protected int backgroundColor = ColorUtil.colorFromHex("#000000");
	public static String fontFile = "fonts/_sketch/AkzidenzGroteskBQ-Reg.ttf";
//	public static String slidesDir = FileUtil.getFile("images/_sketch/slideshow/hovercraft-dev");
	public static String slidesDir = "/Users/cacheflowe/Documents/workspace/presentations/aiga-freelance";
	protected int BUFFER_W = 2688;
	protected int BUFFER_H = 896;
	
	// CONFIG
	public static boolean DEBUG_MODE = false;
	protected int LOADING_INTERVAL = 5;
	protected int STRESS_INTERVAL = 5 * 60;

	// APP STATE
	public AppStore appStore;

	// Custom objects
	protected ArrayList<SlideImage> slideImages;
	protected ArrayList<SlideImage> slideImagesBg;
	protected ArrayList<SlideImage> slideImagesFg;
	protected SlideCaption slideCaption;
	protected SlideTitle slideTitle;
	protected boolean preloaded = false;
	protected EasingFloat preloadX = new EasingFloat(0, 20);
	protected LinearFloat preloadBarOff = new LinearFloat(0, 0.01f);
	protected boolean stressTesting = false;
	protected BrightnessBumper brightnessBumper;

	// Crossfade: 1st slide: `-no_fade_out-` (and don't `-no_exit_delay-`) -> 2nd slide: `-no_queue_delay-` (and don't `-no_fade_in-`)

	/////////////////////////////////////////////////////////////
	// SETUP
	/////////////////////////////////////////////////////////////

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, P.round(BUFFER_W/2f) );
		p.appConfig.setProperty( AppSettings.HEIGHT, P.round(BUFFER_H/2f) );
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.RETINA, false );
		p.appConfig.setProperty( AppSettings.AUDIO_DEBUG, true );
//		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
//		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, true );
		p.appConfig.setProperty( AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
	}

	protected void setScreenPosition() {
//		if(p.appConfig.getInt("screen_x", -1) != -1) {
			surface.setSize(1920, 1080);
			surface.setLocation(1920, 0);  // location has to happen after size, to break it out of fullscreen
//			surface.setAlwaysOnTop(true);
//		}
	}

	protected void setupFirstFrame() {
		MouseShutdown.instance();
		brightnessBumper = new BrightnessBumper();
		buildDrawingSurface();
		buildState();
		loadImages();
		slideCaption = new SlideCaption();
		appStore.addListener(slideCaption);
		slideTitle = new SlideTitle();
		appStore.addListener(slideTitle);
	}
	
	protected void buildDrawingSurface() {
		// set applet drawing quality
		P.println(OpenGLUtil.getGlVersion(p.g));
		OpenGLUtil.setTextureQualityHigh(p.g);

		BUFFER_W = p.width;
		BUFFER_H = p.height;
		
		// init mappable main drawing canvas
		buffer = p.createGraphics(BUFFER_W, BUFFER_H, P.P3D);
		buffer.smooth(AppSettings.SMOOTH_HIGH);
		buffer.noStroke();
		OpenGLUtil.setTextureQualityHigh(buffer);
		
		bufferBg = p.createGraphics(BUFFER_W, BUFFER_H, P.P3D);
		bufferBg.smooth(AppSettings.SMOOTH_HIGH);
		bufferBg.noStroke();
		OpenGLUtil.setTextureQualityHigh(bufferBg);
		
		// keystone the off-screen buffer
		pgKeystone = new PGraphicsKeystone(p, buffer, 12, FileUtil.getFile("text/keystone-slideshow.txt"));
	}
	
	protected void buildState() {
		appStore = new AppStore();
		appStore.setNumber(SlideshowState.SLIDE_INDEX.id(), -1);
	}
	
	/////////////////////////////////////////////////////////////
	// GETTERS
	/////////////////////////////////////////////////////////////
	
	public ArrayList<SlideImage> slides() {
		return slideImages;
	}
	
	/////////////////////////////////////////////////////////////
	// LOAD MEDIA
	/////////////////////////////////////////////////////////////

	protected void loadImages() {
		slideImages = new ArrayList<SlideImage>();
		slideImagesBg = new ArrayList<SlideImage>();
		slideImagesFg = new ArrayList<SlideImage>();
		String[] directories = FileUtil.getDirsInDir(slidesDir);
		Arrays.sort(directories);
		for (int i = 0; i < directories.length; i++) {
			loadSlidesFromDir(directories[i]);
		}
	}

//	protected void loadSlidesFromDir(String imagesPath) {
//		ArrayList<String> images = FileUtil.getFilesInDirOfTypes(imagesPath, "png,mp4,mov,gif");
//		for (int i = 0; i < images.size(); i++) {
//			String fileName = images.get(i);
//			SlideImage newSlide = new SlideImage(fileName, slideImages.size());
//			slideImages.add(newSlide);
//			if(fileName.indexOf("background") == -1) slideImagesFg.add(newSlide);
//			else slideImagesBg.add(newSlide);
//			appStore.registerStatable(newSlide);
//		}
//	}
	
	protected void loadSlidesFromDir(String imagesPath) {
		
		String[] imagesAndDirs = getFilesAndDirsInDir(imagesPath);
		Arrays.sort(imagesAndDirs);
		for (int i = 0; i < imagesAndDirs.length; i++) {
			String fileName = imagesAndDirs[i];
			if(fileName.indexOf("\\._") == -1) {
				P.println(fileName);
				SlideImage newSlide = new SlideImage(fileName, slideImages.size());
				slideImages.add(newSlide);
				if(fileName.indexOf("background") == -1) slideImagesFg.add(newSlide);
				else slideImagesBg.add(newSlide);
				appStore.addListener(newSlide);
			}
		}
	}

	
	public String[] getFilesAndDirsInDir( String directory ) {
		File dir = new File( directory );
		FileFilter fileFilter = new FileFilter() {
		    public boolean accept(File file) {
		        return file.isDirectory() || file.getName().endsWith("png") || file.getName().endsWith("gif") || file.getName().endsWith("jpg") || file.getName().endsWith("mov") || file.getName().endsWith("mp4");
		    }
		};
		File[] files = dir.listFiles(fileFilter);
		String[] fileNames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			fileNames[i] = files[i].toString();
		}
		return fileNames;
	}


	/////////////////////////////////////////////////////////////
	// INPUT
	/////////////////////////////////////////////////////////////

	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') DEBUG_MODE = !DEBUG_MODE;
		if(p.key == 's') stressTesting = !stressTesting;
		if(p.key == ' ') if(!waitingForAutoAdvance()) nextSlide();
		if(p.keyCode == 8) pgKeystone.resetCorners();
		if (p.key == P.CODED && keyCode == P.RIGHT) if(!waitingForAutoAdvance()) nextSlide();
		if (p.key == P.CODED && keyCode == P.DOWN) if(!waitingForAutoAdvance()) nextSlide();
		if (p.key == P.CODED && keyCode == P.LEFT) prevSlide();
		if (p.key == P.CODED && keyCode == P.UP) prevSlide();
	}

	public void mouseClicked() {
		super.mouseClicked();
		if(!waitingForAutoAdvance()) nextSlide();
	}

	/////////////////////////////////////////////////////////////
	// CYCLE THROUGH SLIDES
	/////////////////////////////////////////////////////////////
	
	protected boolean waitingForAutoAdvance() {
		int curIndex = appStore.getNumber(SlideshowState.SLIDE_INDEX.id()).intValue();
		if(curIndex >= 0) {
			return slideImages.get(curIndex).willAutoAdvance();
		} else {
			return false;
		}
	}
	
	public int getSlideIndex() {
		return appStore.getInt(SlideshowState.SLIDE_INDEX.id());
	}
	
	public void nextSlide() {
		// check current slide
		int curIndex = appStore.getNumber(SlideshowState.SLIDE_INDEX.id()).intValue();
		if(curIndex >= 0 && preloaded == true && slideImages.get(curIndex).canAdvanceAfterLoop() == true) {					// queue up the current slide to advance the show if it's configured for that
			slideImages.get(curIndex).advanceAfterComplete();
		} else {																											// normal slide incrementing below
			curIndex++;
			if(curIndex >= slideImages.size()) {
				curIndex = -1;
				if(stressTesting == false) preloaded = true;
			}
			appStore.setNumber(SlideshowState.SLIDE_INDEX.id(), curIndex);
		}
	}
	
	public void prevSlide() {
		int curIndex = appStore.getNumber(SlideshowState.SLIDE_INDEX.id()).intValue();
		curIndex--;
		if(curIndex < -1) curIndex = slideImages.size() - 1;
		appStore.setNumber(SlideshowState.SLIDE_INDEX.id(), curIndex);
	}
	
	/////////////////////////////////////////////////////////////
	// MAIN DRAW LOOP
	/////////////////////////////////////////////////////////////

	public void drawApp() {
		// deferred init
//		if(p.frameCount == 100) setScreenPosition();
//		if(p.frameCount == 2) AppUtil.setTitle(p, "Slideshow");
//		if(p.frameCount == 3) DrawUtil.setDrawFlat2d(buffer, true);
		
		// auto cycle slides
		if(p.frameCount % LOADING_INTERVAL == 0 && preloaded == false) nextSlide();
		if(p.frameCount % STRESS_INTERVAL == 0 && stressTesting == true) if(!waitingForAutoAdvance()) nextSlide();

		// draw slides
		p.background(backgroundColor);		
		drawBackgroundSlides();
		drawSlides();
		
		// keystone
		if(DEBUG_MODE == true) pgKeystone.drawTestPattern();
//		pgKeystone.update(p.g, true);
		p.g.image(buffer, 0, 0);
		brightnessBumper.applyTo(p.g);
		
		// debug
		if(DEBUG_MODE == true || preloaded == false) {
			p.debugView.setValue("Slide index", appStore.getNumber(SlideshowState.SLIDE_INDEX.id()).intValue());
			// debugDrawSlides();
		}
		
		drawPreloader();
	}
		
	protected void drawBackgroundSlides() {
		bufferBg.beginDraw();
		bufferBg.clear();
		DrawUtil.setDrawCenter(bufferBg);
		bufferBg.translate(bufferBg.width/2, bufferBg.height/2, 0);
				
		for (int i = 0; i < slideImagesBg.size(); i++) {
			slideImagesBg.get(i).update(bufferBg);
		}
		
		try {
			bufferBg.endDraw();
		} catch (NullPointerException e) {
			P.println("NullPointerException :: ", e.getMessage());
		}
	}
	
	protected void drawSlides() {
		// prep buffer
		buffer.beginDraw();
		buffer.clear();
		buffer.pushMatrix();
		
		// draw from center
		DrawUtil.setDrawCenter(buffer);
		buffer.translate(buffer.width/2, buffer.height/2, 0);
		
		// draw the background layer before drawing FG layers
		buffer.image(bufferBg, 0, 0); 
		
		// draw the foreground slides
		for (int i = 0; i < slideImagesFg.size(); i++) {
			slideImagesFg.get(i).update(buffer);
		}
		buffer.popMatrix();
		
		// draw caption on top
		slideTitle.update(buffer); 
		slideCaption.update(buffer); 
		
		// finish buffer drawing
		try {
			buffer.endDraw();
		} catch (NullPointerException e) {
			P.println("NullPointerException :: ", e.getMessage());
		}
	}
	
	protected void drawPreloader() {
		if(preloadBarOff.value() == 1) return; 
		preloadX.update();
		preloadBarOff.update();

		int curIndex = appStore.getNumber(SlideshowState.SLIDE_INDEX.id()).intValue();
		float loadProgress = (preloaded) ? 1f : (float) curIndex / (float) slideImages.size();
		preloadX.setTarget(p.width * loadProgress);
		if(loadProgress == 1) preloadBarOff.setTarget(1);
		float animOutY = 10f * Penner.easeInOutCubic(preloadBarOff.value(), 0, 1, 1);
		float animOutAlpha = 255f;// - 255f * preloadComplete.value();
		
		p.fill(0, animOutAlpha);
		p.rect(0, 0, p.width, p.height);
		p.fill(0, 200, 0);
		p.rect(0, p.height - 10 + animOutY, preloadX.value(), 10);
		p.fill(255);
	}

	/////////////////////////////////////////////////////////////
	// DEBUG
	/////////////////////////////////////////////////////////////

	protected void debugDrawSlides() {
		int x = 0;
		int y = 0;
		for (int i = 0; i < slideImages.size(); i++) {
			if(slideImages.get(i).image() != null) {
				p.image(slideImages.get(i).image(), x, y, 100, 100);
			}
			x += 100;
			if(x > buffer.width) {
				x = 0;
				y += 100;
			}
		}
	}
	
}
