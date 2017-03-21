package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class TiledTexture {
	
	protected PImage texture;
	protected float uX;
	protected float vY;
	protected float speedX;
	protected float speedY;
	protected float sizeX;
	protected float sizeY;
	protected float rotation;
	
	// stored uv coords
	float tlX;
	float tlY;
	float trX;
	float trY;
	float brX;
	float brY;
	float blX;
	float blY;
	
	public TiledTexture(PImage image) {
		texture = image;
		uX = 0.5f * (float) texture.width;
		vY = 0.5f * (float) texture.height;
		setSpeed(0, 0);
		setSize(1f, 1f);
	}
	
	public void setSpeed(float x, float y) {
		speedX = x;
		speedY = y;
	}
	
	public void setOffset(float x, float y) {
		uX = (0.5f + x) * (float) texture.width;
		vY = (0.5f + y) * (float) texture.height;
	}
	
	public void setSize(float x, float y) {
		sizeX = x;
		sizeY = y;
	}
	
	public void setRotation(float rads) {
		rotation = rads;
	}
	
	public float textureWidth() {
		return (float) texture.width;
	}
	
	public float textureHeight() {
		return (float) texture.height;
	}
	
	public void update() {
		uX += speedX;
		if(uX < 0 && speedX < 0f) uX += texture.width;
		if(uX > texture.width && speedX > 0f) uX -= texture.width;
		
		vY += speedY;
		if(vY < 0 && speedY < 0f) vY += texture.height;
		if(vY > texture.height && speedY > 0f) vY -= texture.height;
	}
	
	public void drawCentered(PGraphics pg, float drawW, float drawH) {
		DrawUtil.setTextureRepeat(pg, true);
		float halfDrawW = drawW / 2f;
		float halfDrawH = drawH / 2f;
		float halfSizeX = halfDrawW * sizeX;
		float halfSizeY = halfDrawH * sizeY;
				
		pg.noStroke();
		pg.beginShape();
		pg.textureMode(P.IMAGE);
		pg.texture(texture);
		if(rotation == 0) {
			tlX = uX - halfSizeX;
			tlY = vY - halfSizeY;
			trX = uX + halfSizeX;
			trY = vY - halfSizeY;
			brX = uX + halfSizeX;
			brY = vY + halfSizeY;
			blX = uX - halfSizeX;
			blY = vY + halfSizeY;
		} else {
			float curRot = rotation + P.PI;
			float radius = MathUtil.getDistance(0, 0, halfSizeX, halfSizeY);
			float tlRads = MathUtil.getRadiansToTarget(uX - halfSizeX, vY - halfSizeY, uX, vY) + curRot;
			float trRads = MathUtil.getRadiansToTarget(uX + halfSizeX, vY - halfSizeY, uX, vY) + curRot;
			float brRads = MathUtil.getRadiansToTarget(uX + halfSizeX, vY + halfSizeY, uX, vY) + curRot;
			float blRads = MathUtil.getRadiansToTarget(uX - halfSizeX, vY + halfSizeY, uX, vY) + curRot;
			tlX = uX + radius * P.cos(tlRads);
			tlY = vY - radius * P.sin(tlRads);
			trX = uX + radius * P.cos(trRads);
			trY = vY - radius * P.sin(trRads);
			brX = uX + radius * P.cos(brRads);
			brY = vY - radius * P.sin(brRads);
			blX = uX + radius * P.cos(blRads);
			blY = vY - radius * P.sin(blRads);
		}
		pg.vertex(-halfDrawW, -halfDrawH, tlX, tlY);
		pg.vertex( halfDrawW, -halfDrawH, trX, trY);
		pg.vertex( halfDrawW,  halfDrawH, brX, brY);
		pg.vertex(-halfDrawW,  halfDrawH, blX, blY);
		pg.endShape();
	}
	
	public void drawDebug(PGraphics pg) {
		// fit to small box
		// draw background
		pg.stroke(0);
		pg.fill(0);
		pg.rect(0, 0, texture.width, texture.height);
		// draw image
		pg.image(texture, 0, 0, texture.width, texture.height);
		// show texture grab area
		float halfSizeX = (sizeX * (float) texture.width) / 2f;
		float halfSizeY = (sizeY * (float) texture.height) / 2f;
		pg.stroke(255, 0, 0, 200);
		pg.noFill();
		pg.beginShape();
		pg.vertex(tlX, tlY);
		pg.vertex(trX, trY);
		pg.vertex(brX, brY);
		pg.vertex(blX, blY);
		pg.vertex(tlX, tlY);
		pg.endShape();

		
	}
}