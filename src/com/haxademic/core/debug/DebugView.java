package com.haxademic.core.debug;

import java.util.LinkedHashMap;
import java.util.Map;

import com.haxademic.core.app.P;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.system.DateUtil;
import com.haxademic.core.text.StringUtil;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class DebugView {
	
	protected PApplet p;
	protected static PFont debugFont;	
	protected static LinkedHashMap<String, String> debugLines = new LinkedHashMap<String, String>();
	protected static LinkedHashMap<String, String> helpLines = new LinkedHashMap<String, String>();
	protected static LinkedHashMap<String, PImage> textures = new LinkedHashMap<String, PImage>();
	protected static float padding = 20;
	protected static float debugPanelW = 0;
	protected static float MAX_PANEL_WIDTH = 500;
	protected static float helpPanelW = 0;
	protected static int fontSize = 11;
	protected static boolean active = false;
	protected static int frameOpened = 0;
	protected static int hideFrames = 60 * 60;
	protected static boolean autoHide = true;
	protected static String ipAddress;
	public static final String TITLE_PREFIX = "___";
	
	// Singleton instance
	
	public static DebugView instance;
	
	public static DebugView instance() {
		if(instance != null) return instance;
		instance = new DebugView(P.p);
		return instance;
	}
	
	// Constructor

	public DebugView(PApplet p) {
		this.p = p;
		active = Config.getBoolean(AppSettings.SHOW_DEBUG, false);
		// for some reason, these were crashing app launches, so they got threaded
		updateAppInfo();
		addKeyCommandInfo();
		// update every frame
		P.p.registerMethod(PRegisterableMethods.pre, this);
		P.p.registerMethod(PRegisterableMethods.post, this);
	}
	
	public static boolean active() {
		return active;
	}
	
	public static void active(boolean active) {
		DebugView.active = active;
		if(active) frameOpened = P.p.frameCount;
	}
	
	public static void autoHide(boolean autoHide) {
		DebugView.autoHide = autoHide;
	}
	
	public static void setValue(String key, String val) {
		debugLines.put(key, val);
	}
	
	public static void setValue(String key, float val) {
		debugLines.put(key, ""+val);
	}
	
	public static void setValue(String key, int val) {
		debugLines.put(key, ""+val);
	}
	
	public static void setValue(String key, boolean val) {
		String bool = (val == true) ? "true" : "false";
		debugLines.put(key, ""+bool);
	}
	
	public static void setTexture(String key, PImage texture) {
		if(texture != null) {
			textures.put(key, texture);
		} else {
			if(textures.containsKey(key)) textures.remove(key);
		}
	}
	
	public static void removeTexture(String key) {
		if(textures.containsKey(key)) textures.remove(key);
	}
	
	public static void setHelpLine(String key, String val) {
		helpLines.put(key, val);
	}
	
	public static float debugPanelW() {
		return debugPanelW + padding;
	}
	
	public static float helpPanelW() {
		return helpPanelW + padding;
	}
	
	protected void updateAppInfo() {
		debugLines.put(TITLE_PREFIX + " RUN TIME", "");
		debugLines.put("Frame", ""+p.frameCount);
		int runtimeSeconds = (int) DateUtil.uptimeSeconds();
		int days = P.floor(runtimeSeconds / DateUtil.dayInSeconds);
		String daysStr = (days > 0) ? days+" days + " : "";
		runtimeSeconds = runtimeSeconds % DateUtil.dayInSeconds;
		debugLines.put("Uptime", daysStr + DateUtil.timeFromSeconds(runtimeSeconds, true));
		debugLines.put(TITLE_PREFIX + " APP", "");
		debugLines.put("alwaysOnTop", ""+P.p.alwaysOnTop());
		debugLines.put("width", ""+P.p.width);
		debugLines.put("height", ""+P.p.height);
		debugLines.put(TITLE_PREFIX + " PERFORMANCE", "");
		debugLines.put("FPS", ""+P.round(p.frameRate));
		debugLines.put("Memory Allocated", StringUtil.formattedInteger(DebugUtil.memoryAllocated()));
		debugLines.put("Memory Free", StringUtil.formattedInteger(DebugUtil.memoryFree()));
		debugLines.put("Memory Max", StringUtil.formattedInteger(DebugUtil.memoryMax()));
		debugLines.put(TITLE_PREFIX + " NET", "");
		debugLines.put("IP Address", ipAddress);
		debugLines.put(TITLE_PREFIX + " CUSTOM", "");
	}
	
	protected void addKeyCommandInfo() {
		setHelpLine(DebugView.TITLE_PREFIX + "KEY COMMANDS:", "");
		setHelpLine("ESC |", "Quit");
		setHelpLine("[W]", "Show WebCam UI");
		setHelpLine("[F]", "Toggle `alwaysOnTop`");
		setHelpLine("[/]", "Toggle `DebugView`");
		setHelpLine("[\\]", "Toggle `PrefsSilders`");
		setHelpLine("[.]", "Audio input gain up");
		setHelpLine("[,]", "Audio input gain down");
		setHelpLine("[|]", "Save screenshot");
	}
	
	public static void updateInputs() {
		setValue("mouseX", P.p.mouseX);
		setValue("mouseY", P.p.mouseY);
		setValue("key", P.p.key);
		setValue("keyCode", P.p.keyCode);
		setValue("keyPressed", P.p.keyPressed);
	}
	
	protected String stringFromHashMap(LinkedHashMap<String, String> hashMap) {
		// build string by iterating over LinkedHashMap
		String string = "";
		for (Map.Entry<String, String> item : hashMap.entrySet()) {
		    String key = item.getKey();
		    String value = item.getValue();
		    if(key != null && value != null) {
			    	if(value.length() > 0) 	string += key + ": " + value + "\n";
			    	else 					string += key + "\n";
		    }
		}
		return string;
	}
	
	public void checkKeyCommands() {
		if(KeyboardState.instance().isKeyTriggered('/')) active(!active);
		if(KeyboardState.instance().isKeyTriggered('\\')) active(false);
	}
	
	public void pre() {
		if(P.p.frameCount == 1) {
			new Thread(new Runnable() { public void run() {
				debugFont = DemoAssets.fontInter(fontSize);
				ipAddress = IPAddress.getLocalAddress();
			}}).start();
		}
		checkKeyCommands();
	}
	
	public void post() {
		if(debugFont == null) return;
		if(active == false) return;
		if(autoHide && p.frameCount > frameOpened + hideFrames) active = false;
		
		p.pushMatrix();
		p.pushStyle();
		p.noLights();
		
		// update core app stats
		updateAppInfo();
		
		// set up flat drawing
		PG.setDrawCorner(p);
		PG.setDrawFlat2d(p, true);
		p.blendMode(PBlendModes.BLEND);

		// draw debug text block
		String debugStr = stringFromHashMap(debugLines);
		
		p.textFont(debugFont);
		p.textAlign(P.LEFT, P.TOP);
		p.textSize(fontSize);
		p.textLeading(17f);
		float textW = p.textWidth(debugStr) + padding;
		debugPanelW = P.max(debugPanelW, textW);
		debugPanelW = P.min(debugPanelW, MAX_PANEL_WIDTH);
		
		// push to scroll
		p.pushMatrix();

		// draw bg
		p.noStroke();
		p.fill(0,225);
		p.rect(0, 0, debugPanelW + padding, p.height);
		
		// scroll text - offset w/mouse y if too big to fit on screen
		float textH = debugLines.size() * fontSize * debugFont.ascent() * 3f;
		if(textH > p.height) {
			float heightDiff = p.height - textH;
			p.translate(0, P.map(Mouse.yNorm, 0, 1, 0, heightDiff));
		}
		
		// draw text
		p.fill(255);
		p.text(debugStr, 10, 10, textW, textH);
		
		// pop scroll
		p.popMatrix();
		
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
		int texIndex = 0;
		for (Map.Entry<String, PImage> item : textures.entrySet()) {
		    String imageName = item.getKey();
		    PImage image = item.getValue();
		    if(imageName != null && image != null) {
		    	// scale to fit
				float texW = image.width * MathUtil.scaleToTarget(image.height, texHeight);
				p.image(image, debugPanelW(), texHeight * texIndex, texW, texHeight);
				// write title
				String textureName = imageName + " (" + image.width + "x" + image.height + ")";
				p.fill(0, 140);
				p.rect(debugPanelW(), texHeight * texIndex, p.textWidth(textureName) + 8, 18);
				p.fill(255);
				p.textSize(11);
				p.text(textureName, debugPanelW() + 4, texHeight * texIndex + 2);
				// increment
			    texIndex++;
		    }
		}

		// reset context
		PG.setDrawFlat2d(p, false);
		p.popStyle();
		p.popMatrix();
	}
}
