package com.haxademic.core.app;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

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
import com.haxademic.core.system.Console;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UITextInput;

import krister.Ess.AudioInput;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;
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
	protected static PAppletHax p;						// Global/static ref to PApplet - any class can access reference from this static ref. Easier access via `P.p`
	public PGraphics pg;											// Offscreen buffer that matches the app size by default
	protected int startupTime;

	////////////////////////
	// INIT
	////////////////////////
	
	public void settings() {
		p = this;
		startupTime = p.millis();
		P.init(this);
		config();
		AppWindow.instance();
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
				P.outInitLineBreak();
				P.outInit("PAppletHax starting....");				
				P.outInitLineBreak();
				P.outInit("System info ---------------------");
				OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();	// from JavaInfo
				P.outInit("- OS name: "+os.getName() + " | version: " + os.getVersion());
				P.outInit("- Architecture:", os.getArch());
				P.outInit("- Available processor cores:", os.getAvailableProcessors());
				P.outInit("- Java version:", SystemUtil.getJavaVersion());
				P.outInitLineBreak();
				P.outInit("Graphics init -------------------");
				P.outInit("- Processing renderer:", P.renderer);
				P.outInit("- GL version:", OpenGLUtil.getGlVersion(p.g));
				PGraphicsOpenGL pg = (PGraphicsOpenGL)g;
				P.outInit("OPENGL_VENDOR:", PGraphicsOpenGL.OPENGL_VENDOR);
				P.outInit("OPENGL_RENDERER:", PGraphicsOpenGL.OPENGL_RENDERER);
				P.outInit("OPENGL_VERSION:", PGraphicsOpenGL.OPENGL_VERSION);
				P.outInit("GLSL_VERSION:", PGraphicsOpenGL.GLSL_VERSION);
				P.outInit("- App Size:", p.width, "x", p.height);
				boolean is32Bit = buildMainPg();
				P.outInit("- pg Size: ", pg.width, "x", pg.height);
				if(is32Bit) P.outInit("- 32-bit pg");
				P.outInitLineBreak();
				P.outInit("PAppletHax started in:", (p.millis() - startupTime)+"ms");				
				P.outInitLineBreak();
				P.outColor(Console.GREEN_BACKGROUND, "###########################################");
				P.out("");
			} else {
				P.outInit("Processing special renderer:", P.renderer);
			}
			firstFrame();	// call override
		}
	}
	
	protected boolean buildMainPg() {
		int pgW = Config.getInt(AppSettings.PG_WIDTH, p.width);
		int pgH = Config.getInt(AppSettings.PG_HEIGHT, p.height);
		boolean is32Bit = Config.getBoolean(AppSettings.PG_32_BIT, false);
		pg = (is32Bit) ? PG.newPG32(pgW, pgH, true, true) : PG.newPG(pgW, pgH);
		return is32Bit;
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
		P.error("Haxademic: YOU MUST OVERRIDE drawApp()");
	}
	
	////////////////////////
	// DRAW
	////////////////////////
	
	public void draw() {
		parentFirstFrame();
		
		p.push();	// because drawApp can leave the context in an unpredictable state for anything drawing via the "post" event
		P.store.setNumber(PEvents.DRAW_PRE, p.frameCount);	// mostly for Renderer to prep for rendering current frame
		drawApp();
		P.store.setNumber(PEvents.DRAW_POST, p.frameCount);
		p.pop();
		
		if(P.renderer == PRenderers.PDF) finishPdfRender();
	}
	
	////////////////////////
	// RENDERING
	////////////////////////
	
	protected void finishPdfRender() {
		// P.out(ConsoleColors.GREEN, "Finished PDF render.", ConsoleColors.RESET);
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
		if (p.key == '|' && !UITextInput.active()) Renderer.saveBufferToDisk(p.g, FileUtil.screenshotsPath());
		
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
		super.stop();
	}

	public void dispose() {
		P.store.setBoolean(PEvents.EXIT, true);
		super.dispose();
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

}
