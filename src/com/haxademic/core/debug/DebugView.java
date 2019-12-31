package com.haxademic.core.debug;

import java.util.LinkedHashMap;
import java.util.Map;

import com.haxademic.core.app.AppWindow;
import com.haxademic.core.app.P;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.system.DateUtil;
import com.haxademic.core.text.StringUtil;
import com.haxademic.core.ui.IUIControl;
import com.haxademic.core.ui.UI;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
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
	
	protected static int MODE_DEBUG = 0;
	protected static int MODE_HELP = 1;
	protected static int mode = MODE_DEBUG;
	protected static int BG_ALPHA = 200;
	protected static int frameOpened = 0;
	protected static int hideFrames = 60 * 60;
	protected static boolean autoHide = true;
	protected static String ipAddress;
	public static final String TITLE_PREFIX = "___";
	
	public static int controlX = 0;
	public static int controlY = 0;
	public static final int controlH = 16;

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
		return debugPanelW;
	}
	
	public static float helpPanelW() {
		return helpPanelW;
	}
	
	protected void updateAppInfo() {
		setValue(TITLE_PREFIX + " RUN TIME", "");
		setValue("Frame", ""+p.frameCount);
		int runtimeSeconds = (int) DateUtil.uptimeSeconds();
		int days = P.floor(runtimeSeconds / DateUtil.dayInSeconds);
		String daysStr = (days > 0) ? days+" days + " : "";
		runtimeSeconds = runtimeSeconds % DateUtil.dayInSeconds;
		setValue("Uptime", daysStr + DateUtil.timeFromSeconds(runtimeSeconds, true));
		setValue(TITLE_PREFIX + " APP", "");
		setValue("alwaysOnTop", ""+AppWindow.instance().alwaysOnTop());
		setValue("width", ""+P.p.width);
		setValue("height", ""+P.p.height);
		setValue(TITLE_PREFIX + " PERFORMANCE", "");
		setValue("FPS", ""+P.round(p.frameRate));
		setValue("Memory Allocated", StringUtil.formattedInteger(DebugUtil.memoryAllocated()));
		setValue("Memory Free", StringUtil.formattedInteger(DebugUtil.memoryFree()));
		setValue("Memory Max", StringUtil.formattedInteger(DebugUtil.memoryMax()));
		setValue(TITLE_PREFIX + " NET", "");
		setValue("IP Address", ipAddress);
		updateInputs();
		setValue(TITLE_PREFIX + " CUSTOM", "");
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
	
	protected static void updateInputs() {
		setValue(DebugView.TITLE_PREFIX + "PROCESSING", "");
		setValue("p.mouseX", P.p.mouseX);
		setValue("p.mouseY", P.p.mouseY);
		setValue("p.key", P.p.key);
		setValue("p.keyCode", P.p.keyCode);
		setValue("p.keyPressed", P.p.keyPressed);
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
	
	protected void drawValuesFromHashMap(LinkedHashMap<String, String> hashMap) {
		// build string by iterating over LinkedHashMap
		for (Map.Entry<String, String> item : hashMap.entrySet()) {
			String key = item.getKey();
			String value = item.getValue();
			if(key != null && value != null) {
				String textLine = (value.length() > 0) ?
						key + ": " + value + "\n" :
						key + "\n";
				boolean isTitle = textLine.indexOf(TITLE_PREFIX) == 0; 
				if(isTitle) textLine = textLine.substring(TITLE_PREFIX.length()).trim();
				drawTextLine(textLine, isTitle);
			}
		}
		
		if(mode == MODE_DEBUG) {
			for (Map.Entry<String, PImage> item : textures.entrySet()) {
			    String imageName = item.getKey();
			    PImage image = item.getValue();
			    if(imageName != null && image != null) {
					drawImage(imageName, image);
			    }
			}
		}

	}
	
	protected void drawTextLine(String textLine, boolean isTitle) {
		PGraphics pg = P.p.g;
		PG.setDrawCorner(pg);

		// outline
		pg.noStroke();
		pg.fill(ColorsHax.BUTTON_OUTLINE);
		pg.rect(controlX, controlY, IUIControl.controlW, controlH);

		// background
		pg.fill((isTitle) ? ColorsHax.TITLE_BG : P.p.color(ColorsHax.BUTTON_BG, BG_ALPHA));
		pg.rect(controlX+1, controlY+1, IUIControl.controlW-2, controlH-2);

		// text label
		pg.fill(ColorsHax.BUTTON_TEXT);
		pg.text(textLine, controlX + IUIControl.TEXT_INDENT, controlY + 1f, IUIControl.controlW, controlH);

		// move to next box
		controlY += controlH - 1;
		if(controlY > P.p.height - controlH) nextCol();
	}
	
	protected void drawImage(String imageName, PImage image) {
    	// scale to fit
		float padding = 10;
		float imgScale = MathUtil.scaleToTarget(image.width, IUIControl.controlW);
		float texH = image.height * imgScale;
		float texW = image.width * imgScale;
		
		// if not enough room, move to next col
		if(controlY + texH > P.p.height) nextCol();

		// draw title
		drawTextLine(imageName + " (" + image.width + " x " + image.height + ")", true);

		// draw image
		p.fill(P.p.color(ColorsHax.BUTTON_BG, BG_ALPHA));
		p.rect(controlX, controlY, IUIControl.controlW, texH);
		p.image(image, controlX + padding, controlY + padding, texW - padding * 2, texH - padding * 2);

		// move to next box
		controlY += texH;
		if(controlY > P.p.height - texH) nextCol();
	}
	
	protected static void nextCol() {
		controlY = 0;
		controlX += IUIControl.controlW;
	}

	
	public void checkKeyCommands() {
		if(KeyboardState.instance().isKeyTriggered('/')) {
			active(!active);
			if(P.p.key == '?') mode = MODE_HELP; 
			else mode = MODE_DEBUG;
		}
//		if(KeyboardState.instance().isKeyTriggered('?')) { active(!active); mode = MODE_HELP; }
//		if(KeyboardState.instance().isKeyTriggered('\\')) active(false);
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
		
		// draw info boxes!
		controlX = (UI.active()) ? UI.controlX : 0;
		controlY = (UI.active()) ? UI.controlY : 0;
		FontCacher.setFontOnContext(P.p.g, debugFont, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		if(mode == MODE_DEBUG) {
			drawValuesFromHashMap(debugLines); 
		} else {
			drawValuesFromHashMap(helpLines); 
		}
		
		// reset context
		PG.setDrawFlat2d(p, false);
		p.popStyle();
		p.popMatrix();
	}
}
