package com.haxademic.core.draw.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.FlipVFilter;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;
import spout.Spout;

public class SpoutTexture {

	protected Spout spout;
	protected PGraphics texture;
	protected PGraphics textureFlipped;
	protected String channelID;
	protected boolean flipY = false;

	public SpoutTexture(int w, int h) {
		this(w, h, null);
	}
	
	public SpoutTexture(int w, int h, String channelID) {
		spout = new Spout(P.p);
		texture = PG.newPG(w, h);
		if(channelID != null) {
			this.channelID = channelID;
			spout.setReceiverName(channelID);
			spoutReconnect();
		}
	}
	
	public void setFlipY() {
		flipY = true;
		textureFlipped = PG.newPG(texture.width, texture.height);
	}
	
	protected void spoutReconnect() {	
		spout.closeReceiver();
		spout.createReceiver(channelID);
	}

	public PGraphics texture() {
		return (flipY) ? textureFlipped : texture;
	}

	public void update() {
		if(spout.receiveTexture()) {
			PGraphics curTexture = spout.receiveTexture(texture);
			if(texture.width != curTexture.width || texture.height != curTexture.height) {
				texture = PG.newPG(curTexture.width, curTexture.height);
				if(flipY) PG.newPG(texture.width, texture.height);
			}
		}
		if(flipY) {
			ImageUtil.copyImage(texture, textureFlipped);
			FlipVFilter.instance(P.p).applyTo(textureFlipped);
		}
	}
}
