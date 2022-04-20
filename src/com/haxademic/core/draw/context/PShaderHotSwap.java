package com.haxademic.core.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.file.WatchDir;
import com.haxademic.core.file.WatchDir.IWatchDirListener;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class PShaderHotSwap
implements IWatchDirListener {

	protected PShader shader;
	protected PShaderCompiler compiledShader;

	protected String vertShaderPath;
	protected String fragShaderPath;
	protected WatchDir watchVert;
	protected WatchDir watchFrag;
	protected boolean queueShaderReload = false;

	public PShaderHotSwap(String fragShaderPath) {
		this(null, fragShaderPath);
	}
	
	public PShaderHotSwap(String vertShaderPath, String fragShaderPath) {
		this.vertShaderPath = vertShaderPath;
		this.fragShaderPath = fragShaderPath;
		rebuildShader();
		watchVert = new WatchDir(FileUtil.pathForFile(fragShaderPath), false, this);
	}

	public PShader shader() {
		return shader;
	}

	public void update() {
		if(queueShaderReload) {
			queueShaderReload = false;
			rebuildShader();
		}
	}
	
	public boolean isValid() {
		return compiledShader.isValid();
	}

	protected void rebuildShader() {
		// attempt to compile shader
		// different paths for fragment-only, and vertex shader combo
		if(vertShaderPath == null) {
			compiledShader = PShaderCompiler.loadShader(fragShaderPath);
		} else {
			compiledShader = new PShaderCompiler(P.p, 
				FileUtil.readTextFromFile(vertShaderPath), 
				FileUtil.readTextFromFile(fragShaderPath)
			);
		}

		// set as active shader if valid
		if(compiledShader.isValid()) {
			shader = compiledShader;
		}
	}

	public void showShaderStatus(PGraphics pg) {
		pg.push();
		pg.noStroke();
		pg.fill(0, 180);
		pg.rect(10, 10, 200, 36);
		if(compiledShader.isValid() == false) {
			FontCacher.setFontOnContext(pg, FontCacher.getFont(DemoAssets.fontInterPath, 14), P.p.color(255, 0, 0), 1, PTextAlign.LEFT, PTextAlign.TOP);
			pg.text("Shader error:" + compiledShader.compileMessage(), 20, 20);
		} else {
			FontCacher.setFontOnContext(pg, FontCacher.getFont(DemoAssets.fontInterPath, 14), P.p.color(0, 255, 0), 1, PTextAlign.LEFT, PTextAlign.TOP);
			pg.text("Shader compiled!", 20, 20);
		}
		pg.pop();
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