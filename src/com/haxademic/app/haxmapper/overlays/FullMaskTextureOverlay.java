package com.haxademic.app.haxmapper.overlays;

import java.awt.Rectangle;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.image.ImageUtil;

import processing.core.PConstants;
import processing.core.PGraphics;

public class FullMaskTextureOverlay {

	protected PGraphics pg;
	protected Rectangle bounds;
	protected BaseTexture _baseTexture;
	protected PGraphics _texture;
	protected float[] cropPosOffset;

	public FullMaskTextureOverlay( PGraphics pg, Rectangle bounds ) {
		this.pg = pg;
		this.bounds = bounds;
	}

	public void setTexture( BaseTexture baseTexture ) {
		_baseTexture = baseTexture;
		_texture = baseTexture.texture();
		cropPosOffset = ImageUtil.getOffsetAndSizeToCrop(bounds.width, bounds.height, _texture.width, _texture.height, true);
	}

	public PGraphics texture() {
		return pg;
	}

	public void drawOverlay() {
		if(_texture == null) return;
		DrawUtil.setPImageAlpha(pg, 0.6f); 	// light opacity overlay. 
		pg.beginShape(PConstants.QUAD);
		pg.texture(_texture);
		// crop to fill the mapped area with the current texture
		float left = bounds.x + cropPosOffset[0];
		float top = bounds.y + cropPosOffset[1];
		float right = left + cropPosOffset[2];
		float bottom = top + cropPosOffset[3];
		pg.vertex(left, top, 0, 		0, 0);
		pg.vertex(right, top, 0, 		_texture.width, 0);
		pg.vertex(right, bottom, 0, 	_texture.width, _texture.height);
		pg.vertex(left, bottom, 0, 		0, _texture.height);
		pg.endShape();
		DrawUtil.setPImageAlpha(pg, 1f);	// reset alpha for subsequent overlay drawing
	}

}