package com.haxademic.sketch.robbie.Unity;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.shared.InputTrigger;

import processing.core.PConstants;
import processing.core.PGraphics;
import spout.Spout;

public class UnityProcessingSpout {
	
	protected UnityProcessingWebSocketSpout p;
	protected PGraphics pg;
	
	protected UnityProcessingWebSocket ws;
	
	protected int UnityWidth;
	protected int UnityHeight;
	
	protected boolean createSpoutReceiver;
	protected Spout spoutReceiver;
	protected String spoutReceiverName = "Spout Sender"; // Name of GameObject with SpoutSender.cs
	protected PGraphics spoutReceiverTexture;
	
	protected boolean createSpoutSender;
	protected Spout spoutSender;
	protected String spoutSenderName = "Spout Processing"; // Name of Processing Spout instance to be read in Unity
	protected PGraphics spoutSenderTexture;
	// If set to 0, will default to Unity (w, h) resolution. If changed make sure update set SpoutReceiver Render Texture size in Unity
	protected int spoutSenderTextureWidth = 0;
	protected int spoutSenderTextureHeight = 0;
	
	protected InputTrigger key1 = (new InputTrigger()).addKeyCodes(new char[]{'1'});
	protected InputTrigger key2 = (new InputTrigger()).addKeyCodes(new char[]{'2'});
	
	public UnityProcessingSpout(int _UnityWidth, int _UnityHeight, boolean _createSpoutSender, boolean _createSpoutReceiver) {
		p = (UnityProcessingWebSocketSpout) P.p;
		pg = p.pg;
		
		UnityWidth = _UnityWidth;
		UnityHeight = _UnityHeight;
		createSpoutSender = _createSpoutSender;
		createSpoutReceiver = _createSpoutReceiver;
		if (spoutSenderTextureWidth == 0) spoutSenderTextureWidth = UnityWidth;
		if (spoutSenderTextureWidth == 0) spoutSenderTextureWidth = UnityWidth;
		
		createSpout();
	}
	
	public void createSpout() {
		if (createSpoutReceiver) {
			spoutReceiver = new Spout(p);
			spoutReceiver.createReceiver(spoutReceiverName);
			spoutReceiverTexture = p.createGraphics(UnityWidth, UnityHeight, PConstants.P2D);
			P.out("Spout receiver created");
		}
		if (createSpoutSender) {
			spoutSender = new Spout(p);
			spoutSender.createSender(spoutSenderName);
			spoutSenderTexture = p.createGraphics(spoutSenderTextureWidth, spoutSenderTextureWidth, PConstants.P2D);
			P.out("Spout sender created");
		}
	}
	
	public void drawSpout() {
		if (createSpoutReceiver) drawSpoutReceiver();
		if (createSpoutSender) drawSpoutSender();
	}
	
	public void drawSpoutReceiver() {
		spoutReceiverTexture = spoutReceiver.receiveTexture(spoutReceiverTexture);
		ImageUtil.cropFillCopyImage(spoutReceiverTexture, pg, true);
	}

	public void drawSpoutSender() {
		spoutSenderTexture.beginDraw();
		spoutSenderTexture.background(P.sin(p.frameCount * 0.03f) * 127/2 + 127/2);
		if(key1.on()) spoutSenderTexture.background(255);
		if(key2.on()) spoutSenderTexture.background(0);
		spoutSenderTexture.endDraw();
		spoutSender.sendTexture(spoutSenderTexture);
//		pg.image(spoutSenderTexture, 0, 0);
	}
	
	
	public void releaseSpout() {
		if (createSpoutReceiver) spoutReceiver.release();
		if (createSpoutSender) spoutSender.release();
		P.out("Spout textures cleared");
	}

}
