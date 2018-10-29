package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TileRepeat
extends BaseVideoFilter {
	
	// tiling image props
	protected int animIndex = -1;
	protected TiledTexture tiledTexture;
	protected float easeFactor = 12f;
	protected EasingFloat drawSize = new EasingFloat(1, easeFactor);
	protected EasingFloat tileSize = new EasingFloat(1, easeFactor);
	protected EasingFloat tileRot = new EasingFloat(0, easeFactor);
	protected EasingFloat tileOffsetX = new EasingFloat(0, easeFactor);
	protected EasingFloat tileOffsetY = new EasingFloat(0, easeFactor);
	
	public TileRepeat( int width, int height ) {
		super(width, height);
	}
	
	public void update() {
		// lazy init textred drawing object
		if(tiledTexture == null) {
			tiledTexture = new TiledTexture(sourceBuffer);
		}

		destBuffer.beginDraw();
		destBuffer.noStroke();

		checkAnimationMode();
		// doRandomConfig();
		drawTiledTexture();

		destBuffer.endDraw();
	}
	
	protected void loadConfig(float newDrawSize, float newTileSize, float newTileRot, float newTileOffsetX, float newTileOffsetY) {
		drawSize.setTarget(newDrawSize);
		tileSize.setTarget(newTileSize);
		tileRot.setTarget(newTileRot);
		tileOffsetX.setTarget(newTileOffsetX);
		tileOffsetY.setTarget(newTileOffsetY);
	}
	
	protected void checkAnimationMode() {
		float playbackTime = (P.p.frameCount / 60f) % 32;
		// switch modes on movie timing
		if(animIndex != 0 && playbackTime > 1.8f && playbackTime < 4f) {
			// 00.0: tunnel spread
			animIndex = 0;
			tileOffsetY.setCurrent(0);
			loadConfig(0.5f, 1f, 0, 0, 0);
		} else if(animIndex == 0 && playbackTime > 4.3f) {
			// 04.2: scroll up / duplicate
			animIndex = 1;
			loadConfig(1f, 1f, 0, 0, 2.558f);
		} else if(animIndex == 1 && playbackTime > 6.77f) {
			// 07.3: scroll a little more
			animIndex = 2;
			loadConfig(1f, 1f, 0, 0, 3.347f);
		} else if(animIndex == 2 && playbackTime > 8.7f) {
			// 09.1: scroll a little more
			animIndex = 3;
			loadConfig(1f, 1f, 0, 0, 4.365f);
		} else if(animIndex == 3 && playbackTime > 12.25f) {
			// 12.5: spin & zoom out
			drawSize.setCurrent(7);
			tileOffsetY.setCurrent(0);
			tileRot.setCurrent(P.PI);
			animIndex = 4;
			loadConfig(1f, 1f, 0, 0, 0);
		} else if(animIndex == 4 && playbackTime > 14.35f) {
			// 14.5: spin & zoom out with 2nd layer upside down behind
			drawSize.setCurrent(7);
			tileRot.setCurrent(P.PI);
			animIndex = 5;
			loadConfig(0.7f, 1.4f, 0, 0, 0);
		} else if(animIndex == 5 && playbackTime > 17) {
			// 17.0: zoom out & duplicate
			animIndex = 6;
			loadConfig(1f, 2.93f, 0, 0, 0.496f);
		} else if(animIndex == 6 && playbackTime > 22.65f) {
			// 23.0: zoom to normal and scroll up
			animIndex = 7;
			loadConfig(1f, 1f, 0, 0, 2f);
		} else if(animIndex == 7 && playbackTime > 29.1f) {
			// 28.5: scroll up again
			animIndex = -1;
			loadConfig(0.7f, 1f, 0, 0, 3f);
		}
	}
	
	protected void doRandomConfig() {
		if(P.p.frameCount % 120 == 0) {
//			newDrawSize, newTileSize, newTileRot, newTileOffsetX, newTileOffsetY
			loadConfig(
					MathUtil.randRangeDecimal(0.5f, 1.25f), 
					MathUtil.randRangeDecimal(0.5f, 1.25f), 
					MathUtil.randRange(0,1) * P.PI, 
					MathUtil.randRangeDecimal(0f, 8f), 
					MathUtil.randRangeDecimal(0f, 8f)
			);
			loadConfig(
					MathUtil.randRangeDecimal(0.75f, 1f), 
					MathUtil.randRangeDecimal(0.5f, 1.5f), 
					MathUtil.randRangeDecimal(-2f, 2f), 
					MathUtil.randRangeDecimal(-5, 5), 
					MathUtil.randRangeDecimal(-5, 5)
			);
		}
	}
	
	protected void drawTiledTexture() {
		
		// update lerped values
		drawSize.update(true);
		tileSize.update(true);
		tileRot.update(true);
		tileOffsetX.update(true);
		tileOffsetY.update(true);

		// update tiled image props
		destBuffer.pushMatrix();
		DrawUtil.setCenterScreen(destBuffer);
		// draw upside-down background layer
		if(animIndex == 5 || animIndex == 6) {
			tiledTexture.setSize(1, 1);
			tiledTexture.setRotation(P.PI);
			tiledTexture.setOffset(0, 0);
			tiledTexture.drawCentered(destBuffer, destBuffer.width, destBuffer.height);
		}
		// draw punch-in layers
		if(animIndex == -1 || animIndex == 0 || animIndex == 1) {
			float punchSteps = 4;
			float stepSize = (1f - drawSize.value()) / punchSteps;
			for (int i = 0; i < punchSteps; i++) {
				float stepDrawSize = 1f - (float) i * stepSize;
				tiledTexture.setSize(tileSize.value() - (punchSteps - 1 - i) * stepSize, tileSize.value() - (punchSteps - 1 - i) * stepSize);
				tiledTexture.setRotation(tileRot.value());
				tiledTexture.setOffset(tileOffsetX.value(), tileOffsetY.value());
				tiledTexture.drawCentered(destBuffer, destBuffer.width * stepDrawSize, destBuffer.height * stepDrawSize);
			}
		}
		// set animated properties
		tiledTexture.setSize(tileSize.value(), tileSize.value());
		tiledTexture.setRotation(tileRot.value());
		tiledTexture.setOffset(tileOffsetX.value(), tileOffsetY.value());
		tiledTexture.drawCentered(destBuffer, destBuffer.width * drawSize.value(), destBuffer.height * drawSize.value());
		destBuffer.popMatrix();
	}

}
