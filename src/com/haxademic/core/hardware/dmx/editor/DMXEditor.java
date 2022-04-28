
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
import com.haxademic.core.ui.UI;
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
	// config
	/////////////////////////////////
	
	public static final String FLOORPLAN_ALPHA = "FLOORPLAN_ALPHA";
	public static final String TEXTURE_ALPHA = "TEXTURE_ALPHA";
	public static final String DMX_MODE = "DMX_MODE";
	public static final String DRAG_CREATE_MODE = "DRAG_CREATE_MODE";
	public static final String LIGHTS_UI_DISABLE = "LIGHTS_UI_DISABLE";
	public static final String LIGHTS_UI_STAY_ON = "LIGHTS_UI_STAY_ON";
	public static final String SHOW_DMX_CHANNELS = "SHOW_DMX_CHANNELS";
	public static final String SHOW_LIGHT_INDEX = "SHOW_LIGHT_INDEX";
	public static final String SHOW_GRID = "SHOW_GRID";
	public static final String RAINBOW_ON_HOVER = "RAINBOW_ON_HOVER";
	public static final String QUANTIZE_SIZE = "QUANTIZE_SIZE";

	/////////////////////////////////
	// PROPERTIES
	/////////////////////////////////
	
	// UI points & selection
	protected ArrayList<SavedPointUI> points = new ArrayList<SavedPointUI>();
	protected boolean draggingNewPoint = false;
	protected PVector mousePoint = new PVector();
	protected PVector mouseStartPoint = new PVector();
	protected int pointIndex = 0;
	protected String textConfigPath;
	
	// lights
	protected DMXUniverse dmxUniverseDefault;
	protected DMXMode dmxMode = DMXMode.RGB;
	protected ArrayList<ILight> lights = new ArrayList<ILight>();
	protected int selectedLightIndex = 0;
	protected ILight activeLight;
	
	// UITextInput
	protected UITextInput channelInput;	
	protected boolean showUI = true;

	// textures
	protected PGraphics pgUI;
	protected PGraphics textureMap;
	protected PImage floorplan;
	
	// keyboard input 
	protected InputTrigger triggerCreateDrag;
	protected InputTrigger triggerLightsUIDisable;
	protected InputTrigger triggerLightsUIOn;
	protected InputTrigger triggerShowDmxChannels;
	protected InputTrigger triggerShowLightIndex;
	protected InputTrigger triggerRainbowOnHover;
	protected InputTrigger triggerShowGrid;

	/////////////////////////////////
	// INIT
	/////////////////////////////////
	
	public DMXEditor(String port, int baudRate, String configPath, PGraphics pgUI, PGraphics textureMap, PImage floorplan) {
		// init state		
		P.store.addListener(this);
		
		// dmx
		dmxUniverseDefault = new DMXUniverse(port, baudRate);
		
		// build screens / objects
		this.pgUI = pgUI;
		this.textureMap = textureMap;
		this.floorplan = floorplan;
		
		// dmx channel text input
		channelInput = new UITextInput("dmxChannel", DemoAssets.fontOpenSansPath, PTextAlign.LEFT, 20, pgUI.height - 80, 80, 60);
		channelInput.filter(ValidateUtil.NOT_NUMERIC);

		// load text config
		textConfigPath = FileUtil.getPath(configPath);
		if(FileUtil.fileExists(textConfigPath)) {
			loadLightsConfig();
		}
		
		// keyboard & mouse events for SavedPointUI
		P.p.registerMethod("mouseEvent", this); // add mouse listeners
		P.p.registerMethod("keyEvent", this);
		addKeyCommands();
		buildUI();
	}	
	
	protected void buildUI() {
		UI.addTitle("DMXEditor");
		UI.addSlider(FLOORPLAN_ALPHA, 1f, 0, 1, 0.01f, false);
		UI.addSlider(TEXTURE_ALPHA, 0.5f, 0, 1, 0.01f, false);
		UI.addSlider(QUANTIZE_SIZE, 20, 1, 100, 1, false);
		UI.addSlider(DMX_MODE, 0, 0, DMXMode.values().length - 1, 1, false);
		UI.addToggle(DRAG_CREATE_MODE, false, false);
		UI.addToggle(LIGHTS_UI_DISABLE, false, false);
		UI.addToggle(LIGHTS_UI_STAY_ON, false, false);
		UI.addToggle(SHOW_DMX_CHANNELS, true, false);
		UI.addToggle(SHOW_LIGHT_INDEX, false, false);
		UI.addToggle(SHOW_GRID, false, false);
		UI.addToggle(RAINBOW_ON_HOVER, false, false);
	}
	
	/////////////////////////////////
	// KEYBOARD INPUT
	/////////////////////////////////
	
	protected void addKeyCommands() {
		triggerCreateDrag = (new InputTrigger()).addKeyCodes(new char[]{'q'});
		triggerLightsUIDisable = (new InputTrigger()).addKeyCodes(new char[]{'w'});
		triggerLightsUIOn = (new InputTrigger()).addKeyCodes(new char[]{'e'});
		triggerShowDmxChannels = (new InputTrigger()).addKeyCodes(new char[]{'r'});
		triggerShowLightIndex = (new InputTrigger()).addKeyCodes(new char[]{'t'});
		triggerRainbowOnHover = (new InputTrigger()).addKeyCodes(new char[]{'y'});
		triggerShowGrid = (new InputTrigger()).addKeyCodes(new char[]{'u'});
		
		DebugView.setHelpLine("\n" + DebugView.TITLE_PREFIX + "DMXEditor Key Commands", "");
		DebugView.setHelpLine("[Q] |", "Create Drag mode");
		DebugView.setHelpLine("[W] |", "Lights UI Disable");
		DebugView.setHelpLine("[E] |", "Lights UI stay on");
		DebugView.setHelpLine("[R] |", "Show DMX channels");
		DebugView.setHelpLine("[T] |", "Show light index");
		DebugView.setHelpLine("[Y] |", "Rainbow on hover");
		DebugView.setHelpLine("[U] |", "Show Grid");
		DebugView.setHelpLine("[I] |", "Quantize to Grid");
		DebugView.setHelpLine("[DEL] |", "Delete hovered light");
		DebugView.setHelpLine("[`] |", "Toggle info");
	}
	
	protected void checkInputs() {
		if(triggerCreateDrag.triggered()) UI.setValueToggleInverse(DRAG_CREATE_MODE);
		if(triggerLightsUIDisable.triggered()) UI.setValueToggleInverse(LIGHTS_UI_DISABLE);
		if(triggerLightsUIOn.triggered()) UI.setValueToggleInverse(LIGHTS_UI_STAY_ON);
		if(triggerShowDmxChannels.triggered()) { UI.setValueToggleInverse(SHOW_DMX_CHANNELS); UI.setValueToggleInverse(SHOW_LIGHT_INDEX); }
		if(triggerShowLightIndex.triggered()) { UI.setValueToggleInverse(SHOW_LIGHT_INDEX); UI.setValueToggleInverse(SHOW_DMX_CHANNELS); }
		if(triggerRainbowOnHover.triggered()) UI.setValueToggleInverse(RAINBOW_ON_HOVER);
		if(triggerShowGrid.triggered()) UI.setValueToggleInverse(SHOW_GRID);
	}
	
	/////////////////////////////////
	// INIT LIGHTS
	/////////////////////////////////
	
	
	protected void loadLightsConfig() {
		String[] configLines = FileUtil.readTextFromFile(textConfigPath);
		if(configLines == null) return;
		for (int i = 0; i < configLines.length; i++) {
			if(configLines[i].length() > 2) {	// ignore empty lines
				String[] lineComponents = configLines[i].split(",");
				String lightType = lineComponents[0];
				if(lightType.equals(LightPoint.class.getSimpleName())) {
					buildLightPoint(lineComponents);
				} else if(lightType.equals(LightBar.class.getSimpleName())) {
					buildLightBar(lineComponents);
				}
				else P.error("Couldn't load DMXEditor text line: " + configLines[i]);
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
		P.out("DMXEditor: Saved lights config");
	}

	
	protected void buildLightPoint(String[] lineComponents) {
		String lightType = lineComponents[0];
		int dmxChannel = ConvertUtil.stringToInt(lineComponents[1]);
		DMXMode dmxMode = DMXMode.valueOf(lineComponents[2]);

		int point1X = ConvertUtil.stringToInt(lineComponents[3]);
		int point1Y = ConvertUtil.stringToInt(lineComponents[4]);
		
		SavedPointUI point1 = new SavedPointUI(0, 0, null);
		point1.setPosition(point1X, point1Y);
		point1.setActive(false);
		points.add(point1);
		
		LightPoint light = new LightPoint(dmxUniverseDefault, dmxMode, point1.position());
		light.setDmxChannel(dmxChannel);
		lights.add(light);
	}
	
	protected void buildLightBar(String[] lineComponents) {
		String lightType = lineComponents[0];
		int dmxChannel = ConvertUtil.stringToInt(lineComponents[1]);
		DMXMode dmxMode = DMXMode.valueOf(lineComponents[2]);

		int point1X = ConvertUtil.stringToInt(lineComponents[3]);
		int point1Y = ConvertUtil.stringToInt(lineComponents[4]);
		int point2X = ConvertUtil.stringToInt(lineComponents[5]);
		int point2Y = ConvertUtil.stringToInt(lineComponents[6]);
		
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
	
	public DMXUniverse dmxUniverse() {
		return dmxUniverseDefault;
	}
	public void showInfo(boolean showInfo) {
		this.showUI = showInfo;
	}
	public void showDmxChannels(boolean showDmxChannels) {
		UI.setValueToggle(SHOW_DMX_CHANNELS, showDmxChannels);
	}
	public void showLightIndex(boolean showLightIndex) {
		UI.setValueToggle(SHOW_LIGHT_INDEX, showLightIndex);
	}
	public void lightsUIDisable(boolean lightsUIDisable) {
		UI.setValueToggle(LIGHTS_UI_DISABLE, lightsUIDisable);
	}
	public void showTextureOverlay(float textureAlpha) {
		UI.setValue(TEXTURE_ALPHA, textureAlpha);
	}
	public void toggleUI() {
		showUI = !showUI;
	}
	
	////////////////////////
	// SavedPointUI Mouse interface
	////////////////////////
	
	public void mouseEvent(MouseEvent event) {
		mousePoint.set(event.getX(), event.getY());
		checkMouseDragPoint(event);
	}
	
	public void checkMouseDragPoint(MouseEvent event) {
		boolean createDragMode = UI.valueToggle(DRAG_CREATE_MODE);
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			mouseStartPoint.set(event.getX(), event.getY());
			if(createDragMode && isHoveringPoint() == false) {
				// check points to see if one is hovered. cancel if so
				points.add(newPoint());
				draggingNewPoint = true;
			}
			break;
		case MouseEvent.RELEASE:
			// create a fixture on release
			if(createDragMode && draggingNewPoint) {
				draggingNewPoint = false;
				if(mousePoint.dist(mouseStartPoint) > 10) {
					points.add(newPoint());	// add 2nd point
					lights.add(new LightBar(dmxUniverseDefault, dmxMode, points.get(points.size() - 2).position(), points.get(points.size() - 1).position())); // add last 2 points PVectors to new LightFixture
				} else {
					lights.add(new LightPoint(dmxUniverseDefault, dmxMode, points.get(points.size() - 1).position())); // add last point PVectors to new LightFixture
				}
			}
			quantizeToGrid();
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
		if(UI.valueToggle(LIGHTS_UI_DISABLE)) return;
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
	
	protected void removeLight(LightPoint light) {
		// remove SavedPointUI points and LightFixture
		points.remove(pointFromPVector(light.point()));
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
			} else if(light.isActive() && light instanceof LightPoint) {
				removeLight((LightPoint) light);
				return;
			}
		}
	}

	protected void quantizeToGrid() {
		int gridSize = UI.valueInt(QUANTIZE_SIZE);
		for (int i = 0; i < points.size(); i++) {
			int curX = P.round((points.get(i).position().x / gridSize)) * gridSize;
			int curY = P.round((points.get(i).position().y / gridSize)) * gridSize;
			points.get(i).setPosition(curX, curY);
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
			if(e.getKey() == 'i') quantizeToGrid();
			if(e.getKey() == 's') saveLightsToFile();
			if(e.getKeyCode() == 147) deleteActiveLight();
			if(e.getKeyCode() == '`') toggleUI();
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
		drawGrid();
		pgUI.endDraw();
		
	}
	
	protected void drawFloorPlan() {
		float floorplanAlpha = UI.value(FLOORPLAN_ALPHA);
		if(floorplanAlpha > 0) {
			PG.setPImageAlpha(pgUI, floorplanAlpha);
			ImageUtil.drawImageCropFill(floorplan, pgUI, true);
			PG.resetPImageAlpha(pgUI);
		}
	}
	
	protected void drawTextureOverlay() {
		float textureAlpha = UI.value(TEXTURE_ALPHA);
		if(textureAlpha > 0) {
			PG.setPImageAlpha(pgUI, textureAlpha);
			pgUI.image(textureMap, 0, 0, pgUI.width, pgUI.height);
			PG.resetPImageAlpha(pgUI);
		}
	}
	
	protected void drawUI() {
		boolean pointsUiDisabled = UI.valueToggle(LIGHTS_UI_DISABLE);
				
		// draw points
		SavedPointUI activePoint = null;
		if(pointsUiDisabled == false) {
			for (int i = 0; i < points.size(); i++) {
				SavedPointUI point = points.get(i); 
				point.drawDebug(pgUI, false);
				if(point.isActive()) activePoint = point;
			}
			
			// tell UI points to stay locked on
			if(UI.valueToggle(LIGHTS_UI_STAY_ON)) {
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
				channelInput.set("" + activeLight.dmxChannel());
				channelInput.focus();
				UITextInput.ACTIVE_INPUT = channelInput;
			} else {
				channelInput.set("");
				channelInput.blur();
				UITextInput.ACTIVE_INPUT = null;
			}
		}
		
		// if active light, send it's value into the dmx channel setting for the active light. make sure we have a number in the text input
		if(activeLight != null && channelInput.valueString().length() > 0) {
			activeLight.setDmxChannel(ConvertUtil.stringToInt(channelInput.valueString()));
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
		if(showUI) {
			pgUI.noStroke();
			pgUI.fill(0, 100);
			pgUI.rect(0, 0, 250, 340);
			int infoX = 20;
			int infoY = 15;
			drawText(
					"Floorplan alpha:  " + UI.valueRounded(FLOORPLAN_ALPHA, 2) + FileUtil.NEWLINE +
					"Texture alpha: "   + UI.valueRounded(TEXTURE_ALPHA, 2) + FileUtil.NEWLINE + 
					"Quantize size: "   + UI.valueInt(QUANTIZE_SIZE) + FileUtil.NEWLINE + 
					"DMXMode:  " + dmxMode.name() + FileUtil.NEWLINE + 
					"[Q] - Drag create mode:  " + UI.valueToggle(DRAG_CREATE_MODE) + FileUtil.NEWLINE + 
					"[W] - Lights UI Disable: " + UI.valueToggle(LIGHTS_UI_DISABLE) + FileUtil.NEWLINE + 
					"[E] - Lights UI stay on: " + UI.valueToggle(LIGHTS_UI_STAY_ON) + FileUtil.NEWLINE + 
					"[R] - Show DMX channels: " + UI.valueToggle(SHOW_DMX_CHANNELS) + FileUtil.NEWLINE + 
					"[T] - Show light index: "  + UI.valueToggle(SHOW_LIGHT_INDEX) + FileUtil.NEWLINE + 
					"[Y] - Rainbow Hover: "   + UI.valueToggle(RAINBOW_ON_HOVER) + FileUtil.NEWLINE + 
					"[U] - Show Grid: "   + UI.valueToggle(SHOW_GRID) + FileUtil.NEWLINE + 
					"[I] - Quantize to Grid" + FileUtil.NEWLINE + 
					"[DEL] - Delete UI Point" + FileUtil.NEWLINE + 
					"[[] - Prev UI Point" + FileUtil.NEWLINE + 
					"[]] - Next UI Point" + FileUtil.NEWLINE + 
					"[S] - Save to disk" + FileUtil.NEWLINE +
					"[`] - Toggle info" + FileUtil.NEWLINE, 
					infoX, infoY);
	
			// channel text input
			drawText("DMX channel:", channelInput.x(), channelInput.y() - 25);
			channelInput.draw(pgUI);
		}
		
		// active point coordinates
		if(showUI && activePoint != null) {
			int y = 350;
			pgUI.noStroke();
			pgUI.fill(0, 100);
			pgUI.rect(0, y, 250, 30);
			int infoX = 20;
			int infoY = y + 5;
			drawText("Active Point: " + P.round(activePoint.position().x) + ", " + P.round(activePoint.position().y), infoX, infoY);
	
			// channel text input
			drawText("DMX channel:", channelInput.x(), channelInput.y() - 25);
		}
		
		// reset context
		PG.setDrawFlat2d(pgUI, false);
	}
	
	protected void drawText(String str, float x, float y) {
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 14);
		FontCacher.setFontOnContext(pgUI, font, ColorsHax.BUTTON_TEXT, 1.3f, PTextAlign.LEFT, PTextAlign.TOP);
		pgUI.text(str, x, y);
	}
	
	protected void drawGrid() {
		if(UI.valueToggle(SHOW_GRID)) {
			int cols = pgUI.width / 20;
			int rows = pgUI.height / 20;
			PG.drawGrid(pgUI, 0x00000000, 0x88ffffff, cols, rows, 1, false);
		}
	}
	
	/////////////////////////////////
	// APPSTORE LISTENERS
	/////////////////////////////////

	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
	
}
