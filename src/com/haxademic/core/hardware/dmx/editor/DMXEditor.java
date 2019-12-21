
package com.haxademic.core.hardware.dmx.editor;

import java.io.IOException;
import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.mapping.SavedPointUI;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.dmx.DMXFixture.DMXMode;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.text.ValidateUtil;
import com.haxademic.core.ui.UITextInput;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class DMXEditor
implements IAppStoreListener {

	/////////////////////////////////
	// PROPERTIES
	/////////////////////////////////
	
	// UI points & selection
	protected ArrayList<SavedPointUI> points = new ArrayList<SavedPointUI>();
	protected boolean draggingNewPoint = false;
	protected PVector mousePoint = new PVector();
	protected int pointIndex = 0;
	protected String textConfigPath;
	
	// lights
	protected DMXUniverse dmxUniverseDefault;
	protected DMXMode dmxMode;
	protected ArrayList<ILight> lights = new ArrayList<ILight>();
	protected int selectedLightIndex = 0;
	protected ILight activeLight;
	
	// UITextInput
	protected UITextInput channelInput;	
	protected boolean showInfo = true;

	// textures
	protected PGraphics pgUI;
	protected PGraphics textureMap;
	protected PImage floorplan;
	
	// keyboard input 
	protected InputTrigger triggerDimmer = (new InputTrigger()).addKeyCodes(new char[]{'q'});
	protected InputTrigger triggerCreateDrag = (new InputTrigger()).addKeyCodes(new char[]{'w'});
	protected InputTrigger triggerLightsUIDisable = (new InputTrigger()).addKeyCodes(new char[]{'e'});
	protected InputTrigger triggerLightsUIOn = (new InputTrigger()).addKeyCodes(new char[]{'r'});
	protected InputTrigger triggerShowDmxChannels = (new InputTrigger()).addKeyCodes(new char[]{'t'});
	protected InputTrigger triggerShowLightIndex = (new InputTrigger()).addKeyCodes(new char[]{'y'});
	protected InputTrigger triggerFullOverlay = (new InputTrigger()).addKeyCodes(new char[]{'i'});
	protected InputTrigger triggerDarkWhenSelected = (new InputTrigger()).addKeyCodes(new char[]{'o'});

	/////////////////////////////////
	// INIT
	/////////////////////////////////
	
	public DMXEditor(String port, int baudRate, DMXMode dmxMode, String configPath, PGraphics pgUI, PGraphics textureMap, PImage floorplan) {
		// init state		
		P.store.setBoolean(DIMMED_FLOORPLAN, true);
		P.store.setBoolean(DRAG_CREATE_MODE, false);
		P.store.setBoolean(LIGHTS_UI_DISABLE, false);
		P.store.setBoolean(LIGHTS_UI_STAY_ON, false);
		P.store.setBoolean(SHOW_DMX_CHANNELS, true);
		P.store.setBoolean(SHOW_LIGHT_INDEX, false);
		P.store.setBoolean(TEXTURE_OVERLAY, true);
		P.store.setBoolean(DIMMED_FLOORPLAN, true);
		P.store.setNumber(MODE_3D_PROGRESS, 0);
		P.store.addListener(this);
		
		// dmx
		this.dmxMode = dmxMode;
		dmxUniverseDefault = new DMXUniverse(port, baudRate);
		
		// build screens / objects
		this.pgUI = pgUI;
		this.textureMap = textureMap;
		this.floorplan = floorplan;
		
		// dmx channel text input
		channelInput = new UITextInput("dmxChannel", DemoAssets.fontOpenSansPath, PTextAlign.LEFT, 20, pgUI.height - 80, 80, 60);
		channelInput.filter(ValidateUtil.NOT_NUMERIC);

		// load text config
		textConfigPath = FileUtil.getFile(configPath);
		if(FileUtil.fileExists(textConfigPath)) {
			loadLightsConfig();
		}
		
		// keyboard & mouse events for SavedPointUI
		P.p.registerMethod("mouseEvent", this); // add mouse listeners
		P.p.registerMethod("keyEvent", this);
		addKeyCommandInfo();
	}	
	
	protected void addKeyCommandInfo() {
		DebugView.setHelpLine("\n" + DebugView.TITLE_PREFIX + "Custom Key Commands", "");
		DebugView.setHelpLine("[Q] |", "Dim floorplan toggle");
		DebugView.setHelpLine("[W] |", "Create Drag mode");
		DebugView.setHelpLine("[E] |", "Lights UI Disable");
		DebugView.setHelpLine("[R] |", "Lights UI stay on");
		DebugView.setHelpLine("[T] |", "Show DMX channels");
		DebugView.setHelpLine("[Y] |", "Show light index");
		DebugView.setHelpLine("[I] |", "Texture Overlay");
		DebugView.setHelpLine("[DEL] |", "Delete hovered light");
		DebugView.setHelpLine("[`] |", "Toggle info");
	}
	
	/////////////////////////////////
	// INIT LIGHTS
	/////////////////////////////////
	
	
	protected void loadLightsConfig() {
		String[] configLines = FileUtil.readTextFromFile(textConfigPath);
		if(configLines == null) return;
		for (int i = 0; i < configLines.length; i++) {
			if(configLines[i].length() > 6) {	// ignore empty lines
				String[] lineComponents = configLines[i].split(",");
				if(lineComponents.length > 4) buildLightBar(lineComponents);
//				else buildLightDigit(lineComponents);
			}
		}
	}
	
	protected void saveLightsToFile() {
		String[] saveString = new String[lights.size()];
		for (int i = 0; i < lights.size(); i++) {
			saveString[i] = lights.get(i).toSaveString();
		}
		// make a backup
		if(FileUtil.fileExists(textConfigPath)) {
			try { FileUtil.copyFile(textConfigPath, textConfigPath.replace(".txt", "-bak-" + SystemUtil.getTimestamp() + ".txt"));
			} catch (IOException e) { e.printStackTrace(); }
		}
		// save new config
		FileUtil.createDir(FileUtil.pathForFile(textConfigPath));
		FileUtil.writeTextToFile(textConfigPath, FileUtil.textLinesJoined(saveString));
	}

	
	protected void buildLightBar(String[] lineComponents) {
		int point1X = ConvertUtil.stringToInt(lineComponents[0]);
		int point1Y = ConvertUtil.stringToInt(lineComponents[1]);
		int point2X = ConvertUtil.stringToInt(lineComponents[2]);
		int point2Y = ConvertUtil.stringToInt(lineComponents[3]);
		int dmxChannel = ConvertUtil.stringToInt(lineComponents[4]);
		
		SavedPointUI point1 = new SavedPointUI(0, 0, null);
		point1.setPosition(point1X, point1Y);
		point1.setActive(false);
		points.add(point1);

		SavedPointUI point2 = new SavedPointUI(0, 0, null);
		point2.setPosition(point2X, point2Y);
		point2.setActive(false);
		points.add(point2);
		
		LightBar light = new LightBar(dmxUniverseDefault, dmxMode, point1.position(), point2.position());
		light.setDmxChannel(dmxChannel);
		lights.add(light);
	}
	
	/////////////////////////////////
	// PUBLIC
	/////////////////////////////////
	
	public void showInfo(boolean showInfo) {
		this.showInfo = showInfo;
	}
	public void showDmxChannels(boolean showDmxChannels) {
		P.store.setBoolean(SHOW_DMX_CHANNELS, showDmxChannels);
	}
	public void showLightIndex(boolean showLightIndex) {
		P.store.setBoolean(SHOW_LIGHT_INDEX, showLightIndex);
	}
	public void lightsUIDisable(boolean lightsUIDisable) {
		P.store.setBoolean(LIGHTS_UI_DISABLE, lightsUIDisable);
	}
	public void showTextureOverlay(boolean showTextureOverlay) {
		P.store.setBoolean(TEXTURE_OVERLAY, showTextureOverlay);
	}
	
	/////////////////////////////////
	// INPUT
	/////////////////////////////////
	
	protected void checkInputs() {
		if(triggerDimmer.triggered()) P.store.setBoolean(DIMMED_FLOORPLAN, !P.store.getBoolean(DIMMED_FLOORPLAN));
		if(triggerCreateDrag.triggered()) P.store.setBoolean(DRAG_CREATE_MODE, !P.store.getBoolean(DRAG_CREATE_MODE));
		if(triggerLightsUIDisable.triggered()) P.store.setBoolean(LIGHTS_UI_DISABLE, !P.store.getBoolean(LIGHTS_UI_DISABLE));
		if(triggerLightsUIOn.triggered()) P.store.setBoolean(LIGHTS_UI_STAY_ON, !P.store.getBoolean(LIGHTS_UI_STAY_ON));
		if(triggerShowDmxChannels.triggered()) { P.store.setBoolean(SHOW_DMX_CHANNELS, !P.store.getBoolean(SHOW_DMX_CHANNELS)); P.store.setBoolean(SHOW_LIGHT_INDEX, false); }
		if(triggerShowLightIndex.triggered()) { P.store.setBoolean(SHOW_LIGHT_INDEX, !P.store.getBoolean(SHOW_LIGHT_INDEX)); P.store.setBoolean(SHOW_DMX_CHANNELS, false); }
		if(triggerFullOverlay.triggered()) P.store.setBoolean(TEXTURE_OVERLAY, !P.store.getBoolean(TEXTURE_OVERLAY));
	}
	
	////////////////////////
	// SavedPointUI Mouse interface
	////////////////////////
	
	public void mouseEvent(MouseEvent event) {
		mousePoint.set(event.getX(), event.getY());
		checkMouseDragPoint(event);
	}
	
	public void checkMouseDragPoint(MouseEvent event) {
		boolean createDragMode = P.store.getBoolean(DRAG_CREATE_MODE);
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			if(createDragMode && isHoveringPoint() == false) {
				// check points to see if one is hovered. cancel if so
				points.add(newPoint());
				draggingNewPoint = true;
			}
			break;
		case MouseEvent.RELEASE:
			// create a 2nd point on release
			if(createDragMode && draggingNewPoint) {
				points.add(newPoint());
				draggingNewPoint = false;
				lights.add(new LightBar(dmxUniverseDefault, dmxMode, points.get(points.size() - 2).position(), points.get(points.size() - 1).position())); // add last 2 points PVectors to new LightFixture
			}
			break;
		case MouseEvent.MOVE:
			// set hovered point as active
			boolean foundOne = false;
			for (int i = 0; i < points.size(); i++) {
				if(points.get(i).isHovered()) {
					pointIndex = i;
					foundOne = true;
				}
			}
			if(foundOne == false) pointIndex = -1;
			setActivePoint();
			break;
		case MouseEvent.DRAG:
			// if creating a new line, draw line from first point
			break;
		}
	}
	
	protected SavedPointUI newPoint() {
		SavedPointUI newPoint = new SavedPointUI(0, 0, null);
		newPoint.setPosition((int) mousePoint.x, (int) mousePoint.y);
		newPoint.setActive(false);
		return newPoint;
	}
	
	protected boolean isHoveringPoint() {
		for (int i = 0; i < points.size(); i++) {
			if(points.get(i).isHovered()) return true;
		}
		return false;
	}
	
	protected void setActivePoint() {
		if(P.store.getBoolean(LIGHTS_UI_DISABLE)) return;
		for (int i = 0; i < points.size(); i++) {
			points.get(i).setActive(i == pointIndex);
		}
	}
	
	// light removal
	
	protected void removeLight(LightBar light) {
		// remove SavedPointUI points and LightFixture
		points.remove(pointFromPVector(light.point1()));
		points.remove(pointFromPVector(light.point2()));
		lights.remove(light);
	}
	
	protected SavedPointUI pointFromPVector(PVector pvec) {
		for (int i = 0; i < points.size(); i++) {
			if(pvec.equals(points.get(i).position())) {
				return points.get(i);
			}
		}
		return null;
	}
	
	protected void deleteActiveLight() {
		for (int i = 0; i < lights.size(); i++) {
			ILight light = lights.get(i);
			if(light.isActive() && light instanceof LightBar) {
				removeLight((LightBar) light);
				return;
			}
		}
	}
	
	////////////////////////
	// KEYBOARD
	////////////////////////
	
	public void keyEvent(KeyEvent e) {
		if(e.getAction() == KeyEvent.PRESS) {
			if(e.getKey() == ']') {
				pointIndex++;
				if(pointIndex >= points.size()) pointIndex = 0;
				setActivePoint();
			}
			if(e.getKey() == '[') {
				pointIndex--;
				if(pointIndex < 0) pointIndex = points.size() - 1;
				setActivePoint();
			}
			if(e.getKey() == 's') saveLightsToFile();
			if(e.getKeyCode() == 147) deleteActiveLight();
			if(e.getKeyCode() == '`') showInfo = !showInfo;
		}
	}
	
	/////////////////////////////////
	// DRAW
	/////////////////////////////////
	
	protected void sampleColors() {
		// update light colors from texture
		textureMap.loadPixels();
		for (int i = 0; i < lights.size(); i++) {
			ILight light = lights.get(i);
			light.sampleColorTexture(pgUI, textureMap);
		}
	}

	
	public void update() {
		// update state
		checkInputs();
		sampleColors();
		
		// main app canvas context setup
		pgUI.beginDraw();
		pgUI.background(0);
		drawFloorPlan();
		drawTextureOverlay();
		drawUI();
		pgUI.endDraw();
		
	}
	
	protected void drawFloorPlan() {
		if(P.store.getBoolean(DIMMED_FLOORPLAN)) PG.setPImageAlpha(pgUI, 0.5f);
		ImageUtil.drawImageCropFill(floorplan, pgUI, true);
		PG.resetPImageAlpha(pgUI);
	}
	
	protected void drawTextureOverlay() {
		if(P.store.getBoolean(TEXTURE_OVERLAY)) {
			PG.setPImageAlpha(pgUI, 0.5f);
			pgUI.image(textureMap, 0, 0, pgUI.width, pgUI.height);
			PG.resetPImageAlpha(pgUI);
		}
	}
	
	protected void drawUI() {
		boolean pointsUiDisabled = P.store.getBoolean(LIGHTS_UI_DISABLE);
				
		// draw points
		SavedPointUI activePoint = null;
		if(pointsUiDisabled == false) {
			for (int i = 0; i < points.size(); i++) {
				SavedPointUI point = points.get(i); 
				point.drawDebug(pgUI, false);
				if(point.isActive()) activePoint = point;
			}
			
			// tell UI points to stay locked on
			if(P.store.getBoolean(LIGHTS_UI_STAY_ON)) {
				for (int i = 0; i < points.size(); i++) {
					points.get(i).resetInteractionTimeout(); // never disappear
				}
			}
		}
		
		PG.setDrawCenter(pgUI);
		
		// draw/update lights
		ILight newActiveLight = null;
		PVector activePointPostion = (activePoint == null || pointsUiDisabled) ? null : activePoint.position();
		DebugView.setValue("activePointPostion", (activePointPostion == null) ? null : activePointPostion.toString());
		for (int i = 0; i < lights.size(); i++) {
			ILight light = lights.get(i);
			light.setActive(activePointPostion);
			if(light.isActive()) newActiveLight = light;
			light.update(pgUI, i);
		}
		
		// update dmx channel input when we select a new (or no) light
		if(activeLight != newActiveLight) {
			activeLight = newActiveLight;
			if(activeLight != null) {
				channelInput.text("" + activeLight.dmxChannel());
				channelInput.focus();
				UITextInput.ACTIVE_INPUT = channelInput;
			} else {
				channelInput.text("");
				channelInput.blur();
				UITextInput.ACTIVE_INPUT = null;
			}
		}
		
		// if active light, send it's value into the dmx channel setting for the active light. make sure we have a number in the text input
		if(activeLight != null && channelInput.text().length() > 0) {
			activeLight.setDmxChannel(ConvertUtil.stringToInt(channelInput.text()));
		}
		
		// temporary dragging point for a new edge being created
		if(draggingNewPoint) {
			pgUI.strokeWeight(4);
			pgUI.stroke(255, 0, 0);
			pgUI.line(points.get(points.size()-1).position().x, points.get(points.size()-1).position().y, mousePoint.x, mousePoint.y);
		}
		
		// set context for flat text
		PG.setDrawCorner(pgUI);
		PG.setDrawFlat2d(pgUI, true);
		
		// info
		if(showInfo) {
			pgUI.noStroke();
			pgUI.fill(0, 100);
			pgUI.rect(0, 0, 250, 250);
			int infoX = 20;
			int infoY = 15;
			drawText(
					"Q - Floorplan dimmed:  " + P.store.getBoolean(DIMMED_FLOORPLAN) + FileUtil.NEWLINE +
					"W - Drag create mode:  " + P.store.getBoolean(DRAG_CREATE_MODE) + FileUtil.NEWLINE + 
					"E - Lights UI Disable: " + P.store.getBoolean(LIGHTS_UI_DISABLE) + FileUtil.NEWLINE + 
					"R - Lights UI stay on: " + P.store.getBoolean(LIGHTS_UI_STAY_ON) + FileUtil.NEWLINE + 
					"T - Show DMX channels: " + P.store.getBoolean(SHOW_DMX_CHANNELS) + FileUtil.NEWLINE + 
					"Y - Show light index: "  + P.store.getBoolean(SHOW_LIGHT_INDEX) + FileUtil.NEWLINE + 
					"I - Overlay Texture: "   + P.store.getBoolean(TEXTURE_OVERLAY) + FileUtil.NEWLINE + 
					"DEL - Delete UI Point" + FileUtil.NEWLINE + 
					"[ - Prev UI Point" + FileUtil.NEWLINE + 
					"] - Next UI Point" + FileUtil.NEWLINE + 
					"S - Save to disk" + FileUtil.NEWLINE +
					"` - Toggle info" + FileUtil.NEWLINE, 
					infoX, infoY);
	
			// channel text input
			drawText("DMX channel:", channelInput.x(), channelInput.y() - 25);
			channelInput.update(pgUI);
		}
		
		// reset context
		PG.setDrawFlat2d(pgUI, false);
	}
	
	protected void drawText(String str, float x, float y) {
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 14);
		FontCacher.setFontOnContext(pgUI, font, ColorsHax.BUTTON_TEXT, 1.3f, PTextAlign.LEFT, PTextAlign.TOP);
		pgUI.text(str, x, y);
	}
	
	/////////////////////////////////
	// APPSTORE LISTENERS
	/////////////////////////////////

	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
	
	/////////////////////////////////
	// APP CONFIG & EVENTS & CONSTANTS
	/////////////////////////////////

	// config
	
	public static final String DIMMED_FLOORPLAN = "DIMMED_FLOORPLAN";
	public static final String DRAG_CREATE_MODE = "DRAG_CREATE_MODE";
	public static final String LIGHTS_UI_DISABLE = "LIGHTS_UI_DISABLE";
	public static final String LIGHTS_UI_STAY_ON = "LIGHTS_UI_STAY_ON";
	public static final String SHOW_DMX_CHANNELS = "SHOW_DMX_CHANNELS";
	public static final String SHOW_LIGHT_INDEX = "SHOW_LIGHT_INDEX";
	public static final String MODE_3D_PROGRESS = "MODE_3D_PROGRESS";
	public static final String TEXTURE_OVERLAY = "TEXTURE_FULL_OVERLAY";
	
	// buffers
	
	public static final String LIGHTS_TEXTURE = "LIGHTS_TEXTURE";

}
