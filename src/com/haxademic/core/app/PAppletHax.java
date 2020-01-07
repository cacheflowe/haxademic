package com.haxademic.core.app;

import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputESS;
import com.haxademic.core.media.video.MovieBuffer;
import com.haxademic.core.render.Renderer;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UIButton;

import krister.Ess.AudioInput;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.video.Movie;

public class PAppletHax
extends PApplet {
	//	Simplest launch:
	//	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	//	Fancier launch:
	//	public static void main(String args[]) {
	//		PAppletHax.main(P.concat(args, new String[] { "--hide-stop", "--bgcolor=000000", Thread.currentThread().getStackTrace()[1].getClassName() }));
	//		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "--location=1920,0", "--display=1", ElloMotion.class.getName() });
	//	}

	// app
	public static String arguments[] = null;	// Args passed in via main() launch command
	protected static PAppletHax p;				// Global/static ref to PApplet - any class can access reference from this static ref. Easier access via `P.p`
	public PGraphics pg;						// Offscreen buffer that matches the app size by default

	////////////////////////
	// INIT
	////////////////////////
	
	public void settings() {
		p = this;
		printArgs();
		P.init(this);
		config();
		AppWindow.instance();
	}
	
	protected void printArgs() {
		if(arguments == null || arguments.length == 0) return;
		// print command line arguments
		P.out("=============");
		P.out("main() args:");
		for (String string : arguments) {
			P.out("# " + string);
		}
		P.out("=============");
	}
	
	public void setup() {
		AppWindow.instance().finishSetup();
		P.appInitialized();
	}
	
	////////////////////////
	// LAZY INIT ON FIRST FRAME
	////////////////////////
	
	
	protected void parentFirstFrame() {
		if( p.frameCount == 1 ) {
			if(P.isOpenGL()) {
				P.println("Using Java version: " + SystemUtil.getJavaVersion() + " and GL version: " + OpenGLUtil.getGlVersion(p.g));
				pg = PG.newPG(Config.getInt(AppSettings.PG_WIDTH, p.width), Config.getInt(AppSettings.PG_HEIGHT, p.height));
			}
			firstFrame();	// call override
		}
	}
	
	////////////////////////
	// OVERRIDES
	////////////////////////

	protected void config() {
		// ovverride this to add hard-coded Config properties 
	}

	protected void firstFrame() {
		// Use setupFirstFrame() instead of setup() to avoid 5000ms Processing/Java timeout in setup()
	}

	protected void drawApp() {
		P.println("Haxademic: YOU MUST OVERRIDE drawApp()");
	}
			
	////////////////////////
	// DRAW
	////////////////////////
	
	public void draw() {
		parentFirstFrame();
		
		p.pushMatrix();	// because drawApp can leave the context in a bad state for anything drawing via the "post" event
		P.store.setNumber(PEvents.DRAW_PRE, p.frameCount);	// mostly for Renderer to prep for rendering current frame
		drawApp();
		P.store.setNumber(PEvents.DRAW_POST, p.frameCount);
		p.popMatrix();
		
		if(P.renderer == PRenderers.PDF) finishPdfRender();
	}
	
	////////////////////////
	// RENDERING
	////////////////////////
	
	protected void finishPdfRender() {
		P.println("Finished PDF render.");
		p.exit();
	}
		
	////////////////////////
	// INPUT
	////////////////////////
	
	public void keyPressed() {
		// disable esc key - subclass must call super.keyPressed()
		if( p.key == P.ESC && ( Config.getBoolean(AppSettings.DISABLE_ESC_KEY, false) == true ) ) {   //  || Config.getBoolean(AppSettings.RENDERING_MOVIE, false) == true )
			key = 0;
		}
		
		// screenshot
		if (p.key == '|') Renderer.saveBufferToDisk(p.g, FileUtil.screenshotsPath());
		
		// let other objects know
		P.store.setString(PEvents.KEY_PRESSED, p.key+"");
	}
	
	public void mouseClicked() {
		P.store.setBoolean(PEvents.MOUSE_CLICKED, true);
	}
	
	////////////////////////
	// SHUTDOWN
	////////////////////////
	
	public void stop() {
		if(WebCam.instance != null) WebCam.instance().dispose();
		if(DepthCamera.instance != null) DepthCamera.instance().dispose();
		P.store.setBoolean(PEvents.EXIT, true);
		super.stop();
	}

	////////////////////////
	// PAPPLET LISTENERS
	////////////////////////
	
	// Movie playback
	public void movieEvent(Movie m) {
		m.read();
		MovieBuffer.moviesEventFrames.put(m, p.frameCount);
	}

	// ESS audio input
	public void audioInputData(AudioInput theInput) {
		if(AudioIn.audioInput instanceof AudioInputESS) {
			((AudioInputESS) AudioIn.audioInput).audioInputCallback(theInput);
		}
	}

	// UIControlPanel listeners

	public void uiButtonClicked(UIButton button) {
		P.out("uiButtonClicked: please override", button.id(), button.value());
	}

}
