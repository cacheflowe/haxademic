package com.haxademic.core.debug;

import java.util.LinkedHashMap;
import java.util.Map;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.text.StringFormatter;

import processing.core.PApplet;
import processing.core.PFont;

public class DebugView {
	
	protected PApplet p;
	protected PFont debugFont;	
	protected LinkedHashMap<String, String> debugLines;
	protected LinkedHashMap<String, String> helpLines;
	protected float helpWidthMax = 0;
	protected int fontSize = 14;
	protected String ipAddress;

	public DebugView( PApplet p ) {
		this.p = p;
		ipAddress = IPAddress.getLocalAddress();
		createFont();
		debugLines = new LinkedHashMap<String, String>();
		helpLines = new LinkedHashMap<String, String>();
		updateAppInfo();
	}
	
	protected void createFont() {
		p.textMode( P.SCREEN );
		debugFont = p.createFont("Arial", fontSize);
	}
	
	public void addValue(String key, String val) {
		debugLines.put(key, val);
	}
	
	public void addValue(String key, float val) {
		debugLines.put(key, ""+val);
	}
	
	public void addValue(String key, int val) {
		debugLines.put(key, ""+val);
	}
	
	public void addValue(String key, boolean val) {
		String bool = (val == true) ? "true" : "false";
		debugLines.put(key, ""+bool);
	}
	
	public void addHelpLine(String key, String val) {
		helpLines.put(key, val);
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
		addValue("mouseX", p.mouseX);
		addValue("mouseY", p.mouseY);
		addValue("key", p.key);
		addValue("keyCode", p.keyCode);
		addValue("keyPressed", p.keyPressed);
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
		float textW = p.textWidth(debugStr) + 20;
		p.noStroke();
		p.fill(0,225);
		p.rect(0, 0, textW + 20, p.height);
		p.fill(255);
		p.text(debugStr, 10, 10, textW, p.height - 20);
		
		// draw help lines
		if(helpLines.isEmpty() == false) {
			String helpStr = stringFromHashMap(helpLines);
	
			p.textAlign(P.LEFT, P.TOP);
			p.textSize(fontSize);
			textW = p.textWidth(helpStr) + 20;
			helpWidthMax = P.max(helpWidthMax, textW);
			p.noStroke();
			p.fill(0,225);
			p.rect(p.width - helpWidthMax - 10, 0, helpWidthMax + 20, p.height);
			p.fill(255);
			p.text(helpStr, p.width - helpWidthMax, 10, helpWidthMax, p.height - 20);
		}

		DrawUtil.setDrawFlat2d(p, false);
	}
}
