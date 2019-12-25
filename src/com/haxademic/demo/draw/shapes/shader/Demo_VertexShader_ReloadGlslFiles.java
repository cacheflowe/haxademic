package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderCompiler;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.file.WatchDir;
import com.haxademic.core.file.WatchDir.IWatchDirListener;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class Demo_VertexShader_ReloadGlslFiles 
extends PAppletHax
implements IWatchDirListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape icosa;
	protected PImage texture;
	protected PShader shader;
	protected PShaderCompiler compiledShader;

	protected String vertShaderPath;
	protected String fragShaderPath;
	protected WatchDir watchVert;
	protected WatchDir watchFrag;
	protected boolean queueShaderReload = false;;

	protected void config() {
		int FRAMES = 340;
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 1024);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false );
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 3);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 4);
	}
	
	protected void firstFrame() {
		// set shader paths for compiling and watching
		vertShaderPath = FileUtil.getPath("haxademic/shaders/vertex/inline-vertcolordist-150-vert.glsl");
		fragShaderPath = FileUtil.getPath("haxademic/shaders/vertex/inline-vertcolordist-150-frag.glsl");
		rebuildShader();
		
		// watch shader files for saves
		watchVert = new WatchDir(FileUtil.getPath("haxademic/shaders/vertex/"), false, this);
		
		// shape to adjust with shader
		int detail = 9;
		icosa = Icosahedron.createIcosahedron(p.g, detail, null);// DemoAssets.textureJupiter());
		PShapeUtil.scaleShapeToHeight(icosa, p.height * 0.5f);
	}
	
	protected void rebuildShader() {
		// attempt to compile shader
		compiledShader = new PShaderCompiler(p, 
			FileUtil.readTextFromFile(vertShaderPath), 
			FileUtil.readTextFromFile(fragShaderPath)
		);
		
		// set as active shader if valid
		if(compiledShader.isValid()) {
			shader = compiledShader;
		}
	}
	
	protected void showShaderStatus() {
		if(compiledShader.isValid() == false) {
			FontCacher.setFontOnContext(p.g, FontCacher.getFont(DemoAssets.fontInterPath, 14), p.color(255, 0, 0), 1, PTextAlign.LEFT, PTextAlign.TOP);
			p.text("Shader error:" + compiledShader.compileMessage(), 20, 20);
		} else {
			FontCacher.setFontOnContext(p.g, FontCacher.getFont(DemoAssets.fontInterPath, 14), p.color(0, 255, 0), 1, PTextAlign.LEFT, PTextAlign.TOP);
			p.text("Shader compiled!", 20, 20);
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') rebuildShader();
	}
	
	public void drawApp() {
		// check reload queue
		if(queueShaderReload) {
			queueShaderReload = false;
			rebuildShader();
		}
		
		// set context
		pg.beginDraw();
		pg.background(0);
		pg.noLights();
		PG.setCenterScreen(pg);
		PG.basicCameraFromMouse(pg);
//		pg.rotateY(AnimationLoop.progressRads());
		
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		shader.set("time", FrameLoop.progressRads());
		shader.set("displaceAmp", 0.4f);
		shader.set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);

		// apply shader, draw shape
		pg.shader(shader);  
		pg.shape(icosa);
		pg.resetShader();
		pg.endDraw();
		
		// draw pg to screen
		p.image(pg, 0, 0);
		
		// show shader compile error messages
		showShaderStatus();
	}
	
	// IWatchDirListener callback
	
	@Override
	public void dirUpdated(int eventType, String filePath) {
		// P.out(eventType, filePath);
		if(filePath.equals(vertShaderPath) || filePath.equals(fragShaderPath)) {
			queueShaderReload = true;
		}
	}
		
}