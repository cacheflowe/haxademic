package com.haxademic.core.debug;

import java.util.ConcurrentModificationException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.haxademic.core.app.AppWindow;
import com.haxademic.core.app.P;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.polygons.CollisionUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.DateUtil;
import com.haxademic.core.text.StringUtil;
import com.haxademic.core.ui.IUIControl;
import com.haxademic.core.ui.UI;
import com.haxademic.core.ui.UITextInput;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class DebugView
implements IAppStoreListener {
	
	protected PApplet p;
	public static PFont debugFont;	
	protected static PFont debugFontLg;	
	protected static LinkedHashMap<String, String> debugLines = new LinkedHashMap<String, String>();
	protected static LinkedHashMap<String, String> helpLines = new LinkedHashMap<String, String>();
	protected static LinkedHashMap<String, PImage> textures = new LinkedHashMap<String, PImage>();
	protected static float padding = 10;
	protected static float debugPanelW = 0;
	protected static float MAX_PANEL_WIDTH = 500;
	public static int MAX_H = 0;
	protected static float helpPanelW = 0;
	protected static int fontSize = 11;
	protected static int fontSizeLg = 36;
	protected static boolean active = false;
	protected static boolean pixelFont = true;
	
	protected static int MODE_DEBUG = 0;
	protected static int MODE_HELP = 1;
	protected static int mode = MODE_DEBUG;
	protected static int BG_ALPHA = 180;
	protected static int frameOpened = 0;
	protected static int hideFrames = 60 * 60;
	protected static String uptimeStr = "";
	protected static String pixelVal = "";
	protected static boolean autoHide = false;
	protected static String ipAddress;
	public static final String TITLE_PREFIX = "___";
	protected static String highlightedText = null;
	protected static String highlightedTextExternal = null;
	protected static PImage highlightedImage = null;
	
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
		P.store.addListener(this);
	}
	
	public static void switchToManual() {
		// if we want to draw in the normal drawApp frame, call this once, and call post() directly
		P.p.unregisterMethod(PRegisterableMethods.post, instance);
	}
	
	public static void logUptime() {
		if(FrameLoop.frameModMinutes(10)) P.out("Uptime:", DebugView.uptimeStr());
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
	
	public static void setHighlightedText(String val) {
		highlightedTextExternal = val;
		// shouldn't do this, but here we are, to match debug lines
		if(highlightedTextExternal != null) highlightedTextExternal += "\n";
	}
	
	public static float debugPanelW() {
		return debugPanelW;
	}
	
	public static float helpPanelW() {
		return helpPanelW;
	}
	
	public static String uptimeStr() {
		int runtimeSeconds = (int) DateUtil.uptimeSeconds();
		int days = P.floor(runtimeSeconds / DateUtil.dayInSeconds);
		String daysStr = (days > 0) ? days+" days + " : "";
		runtimeSeconds = runtimeSeconds % DateUtil.dayInSeconds;
		uptimeStr = daysStr + DateUtil.timeFromSeconds(runtimeSeconds, true);
		return uptimeStr;
	}
	
	protected void updateAppInfo() {
		setValue(TITLE_PREFIX + " RUN TIME", "");
		setValue("Frame", ""+p.frameCount);
		setValue("Uptime", uptimeStr());
		setValue(TITLE_PREFIX + " APP", "");
		setValue("alwaysOnTop", ""+AppWindow.instance().alwaysOnTop());
		setValue("width", ""+P.p.width);
		setValue("height", ""+P.p.height);
		setValue("pixelVal", pixelVal);
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
				else 										string += key + "\n";
			}
		}
		return string;
	}
	
	protected void drawValuesFromHashMap(LinkedHashMap<String, String> hashMap) {
		try {
			// build string by iterating over LinkedHashMap
			for (Map.Entry<String, String> item : hashMap.entrySet()) {
				String key = item.getKey();
				String value = item.getValue();
				if(value != null && value.length() > 100) value = value.substring(0, 99);	// limit long strings
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
		} catch(ConcurrentModificationException e) {
			P.error("DebugView.drawValuesFromHashMap() error :: ConcurrentModificationException");
		}
	}
	
	protected void drawTextLine(String textLine, boolean isTitle) {
		PGraphics pg = P.p.g;
		PG.setDrawCorner(pg);

		// set context
		pg.pushMatrix();
		pg.translate(controlX, controlY);
		
		// draw bg rect
		int fill = (isTitle) ? P.p.color(ColorsHax.TITLE_BG, BG_ALPHA) : P.p.color(ColorsHax.BUTTON_BG, BG_ALPHA);
		PG.drawStrokedRect(pg, IUIControl.controlW, controlH, 1, fill, ColorsHax.BUTTON_OUTLINE);

		// text label
		// pg.fill((pixelFont) ? 0xffbbbbbb : ColorsHax.BUTTON_TEXT);
		pg.fill(ColorsHax.BUTTON_TEXT);
		pg.text(textLine, IUIControl.TEXT_INDENT, (pixelFont) ? 3f : 1f); // , IUIControl.controlW, controlH

		// if mouse hover, draw big afterwards
		if(!isTitle && CollisionUtil.rectangleContainsPoint(Mouse.x, Mouse.y, controlX, controlY, IUIControl.controlW, controlH)) {
			highlightedText = textLine;
		}
		
		// reset context
		pg.popMatrix();

		// move to next box
		controlY += controlH - 1;
		if(controlY > maxH() - controlH) nextCol();
	}
	
	protected void drawImage(String imageName, PImage image) {
		PGraphics pg = P.p.g;

			// scale to fit
		float imgScale = MathUtil.scaleToTarget(image.width, IUIControl.controlW - padding * 2);
		float texW = image.width * imgScale;
		float texH = image.height * imgScale;
		if(texH > image.height) {
			texW = image.width;
			texH = image.height;
		}
		
		// if not enough room, move to next col
		if(controlY + texH > maxH()) nextCol();

		// draw title
		drawTextLine(imageName + " (" + image.width + " x " + image.height + ")", true);

		// set context
		pg.pushMatrix();
		pg.translate(controlX, controlY);
		
		// draw image
		PG.drawStrokedRect(pg, IUIControl.controlW, texH + padding * 2, 1, P.p.color(ColorsHax.BUTTON_BG, BG_ALPHA), ColorsHax.BUTTON_OUTLINE);
		p.image(image, padding, padding, texW, texH);
		
		// if mouse hover, draw big afterwards
		if(CollisionUtil.rectangleContainsPoint(Mouse.x, Mouse.y, controlX, controlY, IUIControl.controlW, (int) texH + (int) padding * 2)) {
			highlightedImage = image;
		}

		// reset context
		pg.popMatrix();

		// move to next box
		controlY += texH + padding * 2 - 1;
		if(controlY > P.p.height - texH) nextCol();
	}
	
	protected void drawHighlightedValue() {
		String highlightText = (highlightedTextExternal != null) ? highlightedTextExternal : highlightedText;

		if(highlightText != null) {
			PGraphics pg = P.p.g;
			pg.push();
			pg.translate(pg.width/2, pg.height - 70);

			// get text width
			FontCacher.setFontOnContext(P.p.g, debugFontLg, P.p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
			float textW = pg.textWidth(highlightText);
			textW = P.ceil(textW / 100) * 100; // round up to the nearest 100px width
			if(textW > pg.width * 0.8f) textW = pg.width * 0.8f;
			
			// draw bg rect
			PG.setDrawCenter(pg);
			PG.drawStrokedRect(pg, textW + 40, debugFontLg.getSize() * 2f, 2, P.p.color(0, 0, 0), ColorsHax.BUTTON_OUTLINE);

			// text label
			pg.text(highlightText, 0, debugFontLg.getSize() * 0.35f);
			
			pg.pop();
		}
	}
	
	protected void drawHighlightedImage() {
		if(highlightedImage != null) {
			PGraphics pg = P.p.g;
		
			pg.push();
			
			float padding2 = padding * 2;
			float totalW = highlightedImage.width + padding2;
			float totalH = highlightedImage.height + padding2;
			if((totalW < pg.width && totalH < pg.height) || P.p.mousePressed) {
				PG.setCenterScreen(pg);
				PG.setDrawCenter(pg);
				PG.drawStrokedRect(pg, totalW, totalH, 1, P.p.color(0, 0), ColorsHax.BUTTON_OUTLINE);
				p.image(highlightedImage, 0, 0);
			}  else {
				PG.setDrawCorner(pg);
				ImageUtil.drawImageCropFill(highlightedImage, p.g, false);
			}
			
			// draw bg rect
			// draw image
			
			pg.pop();
		}
	}
	
	protected void drawCrosshair() {
		PGraphics pg = P.p.g;
		pg.push();
		PG.setDrawCorner(pg);
		if(KeyboardState.keyOn('l')) {
			int w = pg.width * 2;
			int h = pg.height * 2;
			pg.push();
			pg.fill(255);
			pg.rect(Mouse.x - w/2, Mouse.y, w, 1);
			pg.rect(Mouse.x, Mouse.y - h/2, 1, h);
			DemoAssets.setDemoFont(pg);
			pg.stroke(255);
			pg.text(P.round(Mouse.x) + ", " + P.round(Mouse.y), Mouse.x + 10, Mouse.y - 30);
			pg.pop();
		}
		pg.pop();
	}
	
	protected void drawPixelValue() {
		PGraphics pg = P.p.g;
		pg.push();
		PG.setDrawCorner(pg);
		if(KeyboardState.keyOn('p')) {
			int col = pg.get(Mouse.x, Mouse.y);
			pixelVal = "[" + Mouse.x + ", " + Mouse.y + "]: " + P.hex(col);
		}
		pg.pop();
	}
	
	protected static int maxH() {
		return (MAX_H > 0) ? MAX_H : P.p.height;
	}

	protected static void nextCol() {
		controlY = 0;
		controlX += IUIControl.controlW - 1;
	}

	public void pre() {
		if(P.p.frameCount == 1) {
			new Thread(new Runnable() { public void run() {
				debugFont = DemoAssets.fontInter(fontSize);
				debugFontLg = P.p.createFont( FileUtil.getPath(DemoAssets.fontInterPath), fontSizeLg );
				ipAddress = IPAddress.getLocalAddress();
			}}).start();
		}
		drawPixelValue();
	}
	
	public void post() {
		if(debugFont == null) return;
		if(active == false) return;

		// do auto-hide
		if(autoHide && p.frameCount > frameOpened + hideFrames) active = false;
		
		// testing pixel font
		if(pixelFont) {
			debugFont = FontCacher.getFont("haxademic/fonts/Minecraftia-Regular.ttf", 8f);
		}
		
		// update core app stats
		updateAppInfo();
		
		p.push();
		// p.translate(-9999, 0);
		p.noLights();
		
		// set up flat drawing
		PG.setDrawCorner(p);
		PG.setDrawFlat2d(p, true);
		p.blendMode(PBlendModes.BLEND);
		
		p.push();
		// draw info boxes!
		// check to see if UI is already drawn on screen, and start from there
		highlightedText = null;
		highlightedImage = null;
		controlX = (UI.active()) ? UI.controlX : 0;
		controlY = (UI.active()) ? UI.controlY : 0;
		FontCacher.setFontOnContext(P.p.g, debugFont, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		if(mode == MODE_DEBUG) {
			drawValuesFromHashMap(debugLines); 
		} else {
			drawValuesFromHashMap(helpLines); 
		}
		p.pop();
		drawHighlightedValue();
		drawHighlightedImage();
		drawCrosshair();
		
		
		// reset context
		PG.setDrawFlat2d(p, false);
		p.pop();
	}
	
	/////////////////////////////
	// IAppStoreListener
	/////////////////////////////

	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {
		if(key.equals(PEvents.KEY_PRESSED)) {
			if(!UITextInput.active() && val.equals("/")) active(!active);
			if(val.equals("?")) mode = MODE_HELP; 
			else mode = MODE_DEBUG;
		}
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
