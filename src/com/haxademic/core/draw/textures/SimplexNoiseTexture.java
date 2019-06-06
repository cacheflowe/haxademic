package com.haxademic.core.draw.textures;

import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pshader.TextureShader;

import processing.core.PGraphics;
import processing.core.PVector;

public class SimplexNoiseTexture {

	protected TextureShader noiseTexture;
	protected PGraphics noiseBuffer;
	
	protected float zoom = 1;
	protected float rotation = 0;
	protected PVector offset = new PVector();

	public SimplexNoiseTexture(int w, int h) {
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq);
	    noiseBuffer = PG.newPG(w, h);
	}

	public PGraphics texture() {
		return noiseBuffer;
	}
	
	public void zoom(float zoom) {
		this.update(zoom, rotation, offset.x, offset.y);
	}
	
	public void rotation(float rotation) {
		this.update(zoom, rotation, offset.x, offset.y);
	}
	
	public void offsetX(float offsetX) {
		this.update(zoom, rotation, offsetX, offset.y);
	}
	
	public void offsetY(float offsetY) {
		this.update(zoom, rotation, offset.x, offsetY);
	}
	
	public void update(float zoom, float rotation, float offsetX, float offsetY) {
		this.zoom = zoom;
		this.rotation = rotation;
		this.offset.set(offsetX, offsetY);
		
		this.update();
	}

	public void update() {
		noiseTexture.shader().set("offset", offset.x, offset.y);
		noiseTexture.shader().set("rotation", rotation);
		noiseTexture.shader().set("zoom", zoom);

		noiseBuffer.filter(noiseTexture.shader());
	}
}
