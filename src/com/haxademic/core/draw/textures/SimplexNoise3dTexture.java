package com.haxademic.core.draw.textures;

import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pshader.TextureShader;

import processing.core.PGraphics;
import processing.core.PVector;

public class SimplexNoise3dTexture {

	protected TextureShader noiseTexture;
	protected PGraphics noiseBuffer;
	
	protected float zoom = 1;
	protected float rotation = 0;
	protected PVector offset = new PVector();
	protected boolean fractalMode = false;
	protected boolean repeatXMode = false;

	public SimplexNoise3dTexture(int w, int h) {
		this(w, h, false);
	}
	
	public SimplexNoise3dTexture(int w, int h, boolean is32bit) {
		noiseTexture = new TextureShader(TextureShader.noise_simplex_3d);
	    noiseBuffer = (is32bit) ? PG.newPG32(w, h, true, false) : PG.newPG(w, h);
	}

	public PGraphics texture() {
		return noiseBuffer;
	}
	
	public void fractalMode(boolean fractalMode) {
		this.update(zoom, rotation, offset.x, offset.y, offset.z, fractalMode, repeatXMode);
	}
	
	public void zoom(float zoom) {
		this.update(zoom, rotation, offset.x, offset.y, offset.z, fractalMode, repeatXMode);
	}
	
	public void rotation(float rotation) {
		this.update(zoom, rotation, offset.x, offset.y, offset.z, fractalMode, repeatXMode);
	}
	
	public void offsetX(float offsetX) {
		this.update(zoom, rotation, offsetX, offset.y, offset.z, fractalMode, repeatXMode);
	}
	
	public void offsetY(float offsetY) {
		this.update(zoom, rotation, offset.x, offsetY, offset.z, fractalMode, repeatXMode);
	}
	
	public void offsetZ(float offsetZ) {
		this.update(zoom, rotation, offset.x, offset.y, offsetZ, fractalMode, repeatXMode);
	}
	
	public void update(float zoom, float rotation, float offsetX, float offsetY, float offsetZ, boolean fractalMode, boolean repeatXMode) {
		this.zoom = zoom;
		this.rotation = rotation;
		this.offset.set(offsetX, offsetY, offsetZ);
		this.fractalMode = fractalMode;
		this.repeatXMode = repeatXMode;
		
		this.update();
	}

	public void update() {
		noiseTexture.shader().set("offset", offset.x, offset.y, offset.z);
		noiseTexture.shader().set("rotation", rotation);
		noiseTexture.shader().set("zoom", zoom);
		noiseTexture.shader().set("fractalMode", (fractalMode) ? 1 : 0);
		noiseTexture.shader().set("repeatXMode", (repeatXMode) ? 1 : 0);
		
		noiseBuffer.filter(noiseTexture.shader());
	}
}
