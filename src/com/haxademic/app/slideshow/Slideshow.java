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
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.BrightnessBumper;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;

public class Slideshow
extends PAppletHax
{
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// MAIN CANVAS
	protected PGraphics pgBg;
	protected PGraphicsKeystone pgKeystone;
	protected int backgroundColor = 0xff000000;
	protected BrightnessBumper brightnessBumper;
	
	// PATHS
//	public static String slidesDir = FileUtil.getFile("images/_sketch/aiga-slides");
//	public static String slidesDir = "/Users/cacheflowe/Documents/workspace/presentations/aiga-freelance";
//	public static String slidesDir = "D:\\workspace\\presentations\\_ctd-class-01";
	public static String SLIDES_DIR = "D:\\workspace\\presentations\\denver-creative-tech-08-2019";
	public static String FONT_FILE = "haxademic/fonts/Raleway-Regular.ttf";
		
	// CONFIG
	public static boolean DEBUG_MODE = false;
	protected int LOADING_INTERVAL = 5;
	protected int STRESS_INTERVAL = 5 * 60;
	protected boolean stressTesting = false;

	// SLIDE COMPONENTS
	protected ArrayList<String> mediaFiles;
	protected ArrayList<SlideImage> slideImages;
	protected ArrayList<SlideImage> slideImagesBg;
	protected ArrayList<SlideImage> slideImagesFg;
	protected SlideCaption slideCaption;
	protected SlideTitle slideTitle;
	
	// LOADING
	protected String loadingFile = "";
	protected int numToLoad = 0;
	protected boolean parsedDirectories = false;
	protected boolean preloaded = false;
	protected EasingFloat preloadX = new EasingFloat(0, 20);
	protected LinearFloat preloadBarOff = new LinearFloat(0, 0.01f);

	// Crossfade: 1st slide: `-no_fade_out-` (and don't `-no_exit_delay-`) -> 2nd slide: `-no_queue_delay-` (and don't `-no_fade_in-`)

	/////////////////////////////////////////////////////////////
	// SETUP
	/////////////////////////////////////////////////////////////

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.ALWAYS_ON_TOP, false );
		Config.setProperty( AppSettings.FULLSCREEN, true );
	}

	protected void firstFrame() {
		brightnessBumper = new BrightnessBumper();
		buildDrawingSurface();
		buildState();
		new Thread(new Runnable() { public void run() {
			loadImages();
			slideCaption = new SlideCaption();
			slideTitle = new SlideTitle();
		}}).start();
		addKeyCommandInfo();
	}
	
	protected void addKeyCommandInfo() {
		DebugView.setHelpLine("\n" + DebugView.TITLE_PREFIX + "Custom Key Commands", "");
		DebugView.setHelpLine("[R] |", "Reload slides");
		DebugView.setHelpLine("[D] |", "Keystone/DEBUG toggle");
		DebugView.setHelpLine("[S] |", "Stress test [DANGER]");
		DebugView.setHelpLine("[BACKSPACE] |", "Reset keystone");
		DebugView.setHelpLine("[RIGHT] |", "Next slide");
		DebugView.setHelpLine("[DOWN] |", "Next slide");
		DebugView.setHelpLine("[LEFT] |", "Prev slide");
		DebugView.setHelpLine("[UP] |", "Prev slide");
	}

	protected void buildDrawingSurface() {
		// build extra PG for background layer
		pgBg = PG.newPG(pg.width, pg.height);
		
		// keystone the off-screen buffer
		pgKeystone = new PGraphicsKeystone(p, pg, 12, FileUtil.getFile("text/keystone-slideshow.txt"));
		pgKeystone.setActive(DEBUG_MODE);
	}
	
	protected void buildState() {
		P.store.setNumber(SlideshowState.SLIDE_INDEX.id(), -1);
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
		mediaFiles = new ArrayList<String>();
		slideImages = new ArrayList<SlideImage>();
		slideImagesBg = new ArrayList<SlideImage>();
		slideImagesFg = new ArrayList<SlideImage>();
		String[] directories = FileUtil.getDirsInDir(SLIDES_DIR);
		Arrays.sort(directories);
		for (int i = 0; i < directories.length; i++) {
			loadSlidesFromDir(directories[i]);
		}
		parsedDirectories = true;
		for (int i = 0; i < mediaFiles.size(); i++) {
			String fileName = mediaFiles.get(i);
			loadingFile = fileName;
			SlideImage newSlide = new SlideImage(fileName, slideImages.size());
			slideImages.add(newSlide);
			if(fileName.indexOf("background") == -1) slideImagesFg.add(newSlide);
			else slideImagesBg.add(newSlide);
		}
		preloaded = true;
	}
	
	protected void loadSlidesFromDir(String imagesPath) {
		String[] mediaFilesInDir = getFilesAndDirsInDir(imagesPath);
		Arrays.sort(mediaFilesInDir);
		numToLoad += mediaFilesInDir.length;
		for (int i = 0; i < mediaFilesInDir.length; i++) {
			P.out(mediaFilesInDir[i]);
			mediaFiles.add(mediaFilesInDir[i]);
		}
	}

	public String[] getFilesAndDirsInDir( String directory ) {
		File dir = new File( directory );
		FileFilter fileFilter = new FileFilter() {
		    public boolean accept(File file) {
		        return (file.isDirectory() && !file.getName().equals("_removed")) || file.getName().endsWith("png") || file.getName().endsWith("gif") || file.getName().endsWith("jpg") || file.getName().endsWith("mov") || file.getName().endsWith("mp4");
		    }
		};
		File[] files = dir.listFiles(fileFilter);
		String[] fileNames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			if(files[i].toString().indexOf("\\._") == -1) {		// ignore weird hidden files
				fileNames[i] = files[i].toString();
			}
		}
		return fileNames;
	}

	protected void reloadSlides() {
		P.store.setNumber(SlideshowState.SLIDE_INDEX.id(), -1); 
		loadImages();
		preloadX.setTarget(0);
		preloadX.setCurrent(0);
		preloadBarOff.setCurrent(0);
		preloadBarOff.setTarget(0);
	}


	/////////////////////////////////////////////////////////////
	// INPUT
	/////////////////////////////////////////////////////////////

	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'r') reloadSlides();
		if(p.key == 'd') { DEBUG_MODE = !DEBUG_MODE; pgKeystone.setActive(DEBUG_MODE); }
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
		int curIndex = getSlideIndex();
		if(curIndex >= 0) {
			return slideImages.get(curIndex).willAutoAdvance();
		} else {
			return false;
		}
	}
	
	public int getSlideIndex() {
		return P.store.getInt(SlideshowState.SLIDE_INDEX.id());
	}
	
	public void nextSlide() {
		// check current slide
		int curIndex = getSlideIndex();
		if(curIndex >= 0 && preloaded == true && slideImages.get(curIndex).canAdvanceAfterLoop() == true) {					// queue up the current slide to advance the show if it's configured for that
			slideImages.get(curIndex).advanceAfterComplete();
		} else {																											// normal slide incrementing below
			curIndex++;
			if(curIndex >= numToLoad) {
				curIndex = -1;
//				if(stressTesting == false) preloaded = true;
			}
			P.store.setNumber(SlideshowState.SLIDE_INDEX.id(), curIndex);
		}
	}
	
	public void prevSlide() {
		int curIndex = getSlideIndex();
		curIndex--;
		if(curIndex < -1) curIndex = slideImages.size() - 1;
		P.store.setNumber(SlideshowState.SLIDE_INDEX.id(), curIndex);
	}
	
	/////////////////////////////////////////////////////////////
	// MAIN DRAW LOOP
	/////////////////////////////////////////////////////////////

	public void drawApp() {
		// auto cycle slides
		if(p.frameCount % STRESS_INTERVAL == 0 && stressTesting == true) if(!waitingForAutoAdvance()) nextSlide();

		// draw slides
		p.background(backgroundColor);		
		drawBackgroundSlides();
		drawSlides();
		
		// keystone
		if(DEBUG_MODE == true) pgKeystone.drawTestPattern();
		pgKeystone.update(p.g, true);
		brightnessBumper.applyTo(p.g);
		
		// debug
		DebugView.setValue("Slide index", P.store.getNumber(SlideshowState.SLIDE_INDEX.id()).intValue() + " / " + slides().size());
		// if(DEBUG_MODE == true || preloaded == false) { debugDrawSlides(); }
		
		// preload
		drawPreloader();
	}
		
	protected void drawBackgroundSlides() {
		pgBg.beginDraw();
		pgBg.clear();
		PG.setDrawCenter(pgBg);
		pgBg.translate(pgBg.width/2, pgBg.height/2, 0);
				
		for (int i = 0; i < slideImagesBg.size(); i++) {
			slideImagesBg.get(i).update(pgBg);
		}
		
		try {
			pgBg.endDraw();
		} catch (NullPointerException e) {
			P.println("NullPointerException :: ", e.getMessage());
		}
	}
	
	protected void drawSlides() {
		if(parsedDirectories == false) return;
		
		// prep buffer
		pg.beginDraw();
		pg.clear();
		pg.pushMatrix();
		
		// draw from center
		PG.setDrawCenter(pg);
		pg.translate(pg.width/2, pg.height/2, 0);
		
		// draw the background layer before drawing FG layers
		pg.image(pgBg, 0, 0); 
		
		// draw the foreground slides
		for (int i = 0; i < slideImagesFg.size(); i++) {
			slideImagesFg.get(i).update(pg);
		}
		pg.popMatrix();
		
		// draw caption on top
		if(slideTitle != null) {
			slideTitle.update(pg); 
			slideCaption.update(pg);
		}
		
		// finish buffer drawing
		try {
			pg.endDraw();
		} catch (NullPointerException e) {
			P.println("NullPointerException :: ", e.getMessage());
		}
	}
	
	protected void drawPreloader() {
		// progress bar
		if(preloadBarOff.value() == 1) return; 
		preloadX.update();
		preloadBarOff.update();

		float loadProgress = (preloaded) ? 1f : (float) slides().size() / (float) numToLoad;
		preloadX.setTarget(p.width * loadProgress);
		if(loadProgress == 1) preloadBarOff.setTarget(1);
		float animOutY = 10f * Penner.easeInOutCubic(preloadBarOff.value());
		float animOutAlpha = 255f;// - 255f * preloadComplete.value();
//		if(loadProgress == 1 && getSlideIndex() != 0) nextSlide();
		
		p.fill(0, animOutAlpha);
		p.rect(0, 0, p.width, p.height);
		p.fill(0, 200, 0);
		p.rect(0, p.height - 10 + animOutY, preloadX.value(), 10);
		p.fill(255);
		
		// draw text
//		int curIndex = getSlideIndex();
//		if(preloaded == false) {
//			p.fill(255);
//			p.stroke(255);
//			p.text("LOADING: " + curIndex + " / " + slides().size() + " / " + numToLoad + " :: " + loadingFile, 20, 0);
//		}
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
			if(x > pg.width) {
				x = 0;
				y += 100;
			}
		}
	}
	
}
