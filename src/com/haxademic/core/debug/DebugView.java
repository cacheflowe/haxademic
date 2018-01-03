package com.haxademic.core.debug;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.text.StringFormatter;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class DebugView {
	
	protected PApplet p;
	protected PFont debugFont;	
	protected LinkedHashMap<String, String> debugLines;
	protected LinkedHashMap<String, String> helpLines;
	protected ArrayList<PImage> textures;
	protected float padding = 20;
	protected float debugPanelW = 0;
	protected float helpPanelW = 0;
	protected int fontSize = 14;
	protected String ipAddress;

	public DebugView( PApplet p ) {
		this.p = p;
		ipAddress = IPAddress.getLocalAddress();
		debugFont = p.createFont("Arial", fontSize);
		debugLines = new LinkedHashMap<String, String>();
		helpLines = new LinkedHashMap<String, String>();
		textures = new ArrayList<PImage>();
		updateAppInfo();
	}
	
	protected void createFont() {
	}
	
	public void setValue(String key, String val) {
		debugLines.put(key, val);
	}
	
	public void setValue(String key, float val) {
		debugLines.put(key, ""+val);
	}
	
	public void setValue(String key, int val) {
		debugLines.put(key, ""+val);
	}
	
	public void setValue(String key, boolean val) {
		String bool = (val == true) ? "true" : "false";
		debugLines.put(key, ""+bool);
	}
	
	public void setHelpLine(String key, String val) {
		helpLines.put(key, val);
	}
	
	public void setTexture(PImage texture) {
		if(textures.contains(texture) == false) textures.add(texture);
	}
	
	public void removeTexture(PImage texture) {
		if(textures.contains(texture) == true) textures.remove(texture);
	}
	
	public float debugPanelW() {
		return debugPanelW + padding;
	}
	
	public float helpPanelW() {
		return helpPanelW + padding;
	}
	
	protected void updateAppInfo() {
		debugLines.put("RUN TIME", "");
		debugLines.put("Frame", ""+p.frameCount);
		debugLines.put("Time", StringFormatter.timeFromSeconds(p.millis() / 1000, true));
		debugLines.put("", "");
		debugLines.put("PERFORMANCE", "");
		debugLines.put("FPS", ""+P.round(p.frameRate));
		debugLines.put("Memory Allocated", StringFormatter.formattedInteger(DebugUtil.memoryAllocated()));
		debugLines.put("Memory Free", StringFormatter.formattedInteger(DebugUtil.memoryFree()));
		debugLines.put("Memory Max", StringFormatter.formattedInteger(DebugUtil.memoryMax()));
		debugLines.put("", "");
		debugLines.put("\nNET", "");
		debugLines.put("IP Address", ipAddress);
		debugLines.put("", "");
		debugLines.put("\nCUSTOM", "");
	}
	
	public void updateInputs() {
		setValue("mouseX", p.mouseX);
		setValue("mouseY", p.mouseY);
		setValue("key", p.key);
		setValue("keyCode", p.keyCode);
		setValue("keyPressed", p.keyPressed);
	}
	
	protected String stringFromHashMap(LinkedHashMap<String, String> hashMap) {
		// build string by iterating over LinkedHashMap
		String string = "";
		for (Map.Entry<String, String> item : hashMap.entrySet()) {
		    String key = item.getKey();
		    String value = item.getValue();
		    if(value.length() > 0) 	string += key + ": " + value + "\n";
		    else 					string += key + "\n";
		}
		return string;
	}
	
	public void draw() {
		// update core app stats
		updateAppInfo();
		
		// set up flat drawing
		DrawUtil.setDrawCorner(p);
		DrawUtil.setDrawFlat2d(p, true);

		// draw debug text block
		String debugStr = stringFromHashMap(debugLines);
		
		p.textFont( debugFont );
		p.textAlign(P.LEFT, P.TOP);
		p.textSize(fontSize);
		float textW = p.textWidth(debugStr) + padding;
		debugPanelW = P.max(debugPanelW, textW);
		p.noStroke();
		p.fill(0,225);
		p.rect(0, 0, debugPanelW + padding, p.height);
		p.fill(255);
		p.text(debugStr, 10, 10, textW, p.height - padding);
		
		// draw help lines
		if(helpLines.isEmpty() == false) {
			String helpStr = stringFromHashMap(helpLines);
	
			p.textAlign(P.LEFT, P.TOP);
			p.textSize(fontSize);
			textW = p.textWidth(helpStr) + padding;
			helpPanelW = P.max(helpPanelW, textW);
			p.noStroke();
			p.fill(0,225);
			p.rect(p.width - helpPanelW - 10, 0, helpPanelW + padding, p.height);
			p.fill(255);
			p.text(helpStr, p.width - helpPanelW, 10, helpPanelW, p.height - padding);
		}
		
		// draw textures
		float texHeight = 100;
		for (int i = 0; i < textures.size(); i++) {
			float texW = textures.get(i).width * MathUtil.scaleToTarget(textures.get(i).height, texHeight);
			p.image(textures.get(i), debugPanelW(), texHeight * i, texW, texHeight);
		}

		DrawUtil.setDrawFlat2d(p, false);
	}
}
