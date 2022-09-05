package com.haxademic.core.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.cage.zxing4p3.ZXING4P;
import com.haxademic.core.app.P;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.http.HttpInputState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.net.UIControlsHandler;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.net.WebViewWindow;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class UI
implements IAppStoreListener {

	protected static LinkedHashMap<String, IUIControl> controls;
	
	public static int controlX = 0;
	public static int controlY = 0;

	protected static boolean active = false;

	protected static WebServer server;
	public static final String KEY_CONTROLS = "ui_controls";
	public static final String KEY_TYPE = "type";
	public static final String KEY_ID = "id";
	public static final String KEY_VALUE = "value";
	public static final String KEY_VALUE_MIN = "value_low";
	public static final String KEY_VALUE_MAX = "value_high";
	public static final String KEY_VALUE_STEP = "value_step";
	public static final String KEY_VALUE_TOGGLES = "value_toggles";
	public static final String KEY_VALUE_LAYOUT_W = "layout_width";
	
	// Singleton instance
	
	public static UI instance;
	
	public static UI instance() {
		if(instance != null) return instance;
		instance = new UI();
		return instance;
	}
	
	// Constructor

	public UI() {
		active = Config.getBoolean(AppSettings.SHOW_UI, false);
		controls = new LinkedHashMap<String, IUIControl>();
		P.p.registerMethod(PRegisterableMethods.pre, this);
		P.p.registerMethod(PRegisterableMethods.post, this);
		P.store.addListener(this);
	}
	
	////////////////////////
	// ADD web controls
	////////////////////////
	
	public static void addWebInterface(boolean debugWebRequests) {
		if(server != null) return;
		if(HttpInputState.instance != null) {
			server = HttpInputState.defaultServer(); 
		} else {
			server = new WebServer(new UIControlsHandler(), debugWebRequests);
		}
		HttpInputState.instance(server);
		
		ZXING4P qr = new ZXING4P();
		String webServerAddress = WebServer.getServerAddress();
		DebugView.setTexture("Launch UI", qr.generateQRCode(webServerAddress + "ui/", 256, 256));
	}
	
	public static void launchWebUIWindow() {
		WebViewWindow.launchWebView(WebServer.getServerAddress() + "ui/");
	}
	
	////////////////////////
	// ADD controls
	////////////////////////
	
	public static void addTitle(String title) {
		controls.put(title, new UITitle(title, controlX, controlY, IUIControl.controlW, IUIControl.controlH));
		controlY += IUIControl.controlSpacing;
		if(controlY > P.p.height - IUIControl.controlH) nextCol();
	}
	
	public static void addTextfield(String key, String value, boolean saves) {
		controls.put(key, new UITextInput(key, value, DemoAssets.fontOpenSansPath, PTextAlign.LEFT, controlX, controlY, IUIControl.controlW, IUIControl.controlH, saves));
		controlY += IUIControl.controlSpacing;
		if(controlY > P.p.height - IUIControl.controlH) nextCol();
	}
	
	public static void addToggle(String key, boolean value, boolean saves) {
		int valInt = (value == true) ? 1 : 0; 
		addSlider(key, valInt, 0, 1, 1, saves);
	}
	
	public static void addSlider(String key, float value, float valueLow, float valueHigh, float dragStep) {
		addSlider(key, value, valueLow, valueHigh, dragStep, true);
	}
	
	public static void addSlider(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves) {
		addSlider(key, value, valueLow, valueHigh, dragStep, saves, -1);
	}
	
	public static void addSlider(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves, int midiCCNote) {
		controls.put(key, new UISlider(key, value, valueLow, valueHigh, dragStep, controlX, controlY, IUIControl.controlW, IUIControl.controlH, saves, midiCCNote));
		controlY += IUIControl.controlSpacing;
		if(controlY > P.p.height - IUIControl.controlH) nextCol();
	}
	
	public static void addSliderVector(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves) {
		addSliderVector(key, value, valueLow, valueHigh, dragStep, saves, -1, -1, -1);
	}
	
	public static void addSliderVector(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves, int midiCCNote1, int midiCCNote2, int midiCCNote3) {
		float controlWidthDivided = (float) IUIControl.controlW / 3f;
		int controlHStack = P.round(IUIControl.controlH * 1.6f);
		controls.put(key + "_X", new UISlider(key + "_X", value, valueLow, valueHigh, dragStep, P.ceil(controlX + 0 * controlWidthDivided), controlY, P.ceil(controlWidthDivided)-1, controlHStack, saves, midiCCNote1));
		controls.put(key + "_Y", new UISlider(key + "_Y", value, valueLow, valueHigh, dragStep, P.ceil(controlX + 1 * controlWidthDivided)-2, controlY, P.ceil(controlWidthDivided), controlHStack, saves, midiCCNote2));
		controls.put(key + "_Z", new UISlider(key + "_Z", value, valueLow, valueHigh, dragStep, P.ceil(controlX + 2 * controlWidthDivided)-2, controlY, P.ceil(controlWidthDivided)+1, controlHStack, saves, midiCCNote3));
		controls.get(key + "_X").layoutW(0.333f);
		controls.get(key + "_Y").layoutW(0.333f);
		controls.get(key + "_Z").layoutW(0.333f);
		controlY += controlHStack - 1;
		if(controlY > P.p.height - controlHStack) nextCol();
	}
	
	protected static void nextCol() {
		controlY = 0;
		controlX += IUIControl.controlW - 1;
	}
	
	public static void removeControl(String key) {
		controls.remove(key);
	}
	
	////////////////////////
	// ADD BUTTONS
	////////////////////////
	
	public static void addButton(String key, boolean toggles) {
		addButton(key, toggles, -1);
	}
	
	public static void addButton(String key, boolean toggles, int midiNote) {
		controls.put(key, new UIButton(null, key, controlX, controlY, IUIControl.controlW, IUIControl.controlH, toggles, midiNote));
		controlY += IUIControl.controlSpacing;
	}
	
	public static void addButtons(String[] keys, boolean toggles) {
		addButtons(keys, toggles, null);
	}
	
	public static void addButtons(String[] keys, boolean toggles, int[] midiNotes) {
		float layoutW = 1f / keys.length;
		float controlWidthDivided = IUIControl.controlW / keys.length;
		for (int i = 0; i < keys.length; i++) {
			int buttonX = P.round(controlX + i * controlWidthDivided);
			int midiNote = (midiNotes != null && midiNotes.length > i) ? midiNotes[i] : -1;
			UIButton newButton = new UIButton(null, keys[i], buttonX, controlY, P.round(controlWidthDivided), IUIControl.controlH, toggles, midiNote);
			newButton.layoutW(layoutW);
			controls.put(keys[i], newButton);
		}
		controlY += IUIControl.controlSpacing;
	}
	
	////////////////////////
	// GET/SET VALUES
	////////////////////////
	
	public static IUIControl get(String key) {
		return controls.get(key);
	}

	public static boolean has(String key) {
		return controls.containsKey(key);
	}
	
	public static void setValue(String key, float val) {
		controls.get(key).set(val);
	}
	
	public static void setValue(String key, String val) {
		((UITextInput) controls.get(key)).set(val);
	}
	
	public static void setValueToggle(String key, boolean val) {
		controls.get(key).set((val) ? 1 : 0);
	}
	
	public static void setValueToggleInverse(String key) {
		boolean val = valueToggle(key);
		controls.get(key).set((val) ? 0 : 1);
	}
	
	public static void setRandomValue(String key) {
		((UISlider) controls.get(key)).setRandomValue();
	}
	
	public static void setValueX(String key, float val) {
		controls.get(key+"_X").set(val);
	}
	
	public static void setValueY(String key, float val) {
		controls.get(key+"_Y").set(val);
	}
	
	public static void setValueZ(String key, float val) {
		controls.get(key+"_Z").set(val);
	}
	
	public static void setRandomValueInt(String key) {
		((UISlider) controls.get(key)).setRandomValueInt();
	}
	
	public static float value(String key) {
		return controls.get(key).value();
	}
	
	public static float valueRounded(String key, int roundDecimal) {
		return MathUtil.roundToPrecision(controls.get(key).value(), roundDecimal);
	}
	
	public static float valueEased(String key) {
		return controls.get(key).valueEased();
	}
	
	public static void setEasingFactor(String key, float easeFactor) {
		((UISlider) controls.get(key)).setEasingFactor(easeFactor);
	}
	
	public static int valueInt(String key) {
		return P.round(controls.get(key).value());
	}
	
	public static String valueString(String key) {
		return controls.get(key).valueString();
	}
	
	public static boolean valueToggle(String key) {
		return P.round(controls.get(key).value()) == 1;
	}
	
	public static float valueX(String key) {
		return controls.get(key+"_X").value();
	}
	
	public static float valueY(String key) {
		return controls.get(key+"_Y").value();
	}
	
	public static float valueZ(String key) {
		return controls.get(key+"_Z").value();
	}
	
	public static float valueXEased(String key) {
		return controls.get(key+"_X").valueEased();
	}
	
	public static float valueYEased(String key) {
		return controls.get(key+"_Y").valueEased();
	}
	
	public static float valueZEased(String key) {
		return controls.get(key+"_Z").valueEased();
	}
	
	////////////////////////
	// DRAW/ACTIVATE/DEACTIVATE
	////////////////////////
	
	public void pre() {
		// update control values (whether UI is showing or not) 
		DebugView.setHighlightedText(null);
		for (IUIControl control : controls.values()) {
			control.update();
			
			// add DebugView highlight text on hover of a UI control
			if(control.hovered()) {
				DebugView.setHighlightedText(control.id() + ": " + MathUtil.roundToPrecision(control.value(), 4));
			}
		}
	}
	
	public void post() {
		// draw if UI is active
		if(active && P.renderer != PRenderers.PDF) {
			PG.setDrawFlat2d(P.p.g, true);
			P.p.g.noLights();
			for (IUIControl control : controls.values()) {
				control.draw(P.p.g);
			}
			PG.setDrawFlat2d(P.p.g, false);
		}
	}

	public static void active(boolean val) {
		active = val;
	}

	public static boolean active() {
		return active;
	}
	
	////////////////////////
	// EXPORT
	////////////////////////
	
	public static String configToJSON() {
		// build JSON array
		JSONArray array = new JSONArray();
		for (HashMap.Entry<String, IUIControl> entry : controls.entrySet()) {	// With LinkedHashMap, keys are in order
			// String key = entry.getKey();
			IUIControl control = entry.getValue();

			JSONObject controlJson = new JSONObject();
			controlJson.setString(KEY_TYPE, control.type());
			controlJson.setString(KEY_ID, control.id());
			if(control.type() == IUIControl.TYPE_TEXTFIELD ||	control.type() == IUIControl.TYPE_TITLE) {
				controlJson.setString(KEY_VALUE, control.valueString());
			} else {
				controlJson.setFloat(KEY_VALUE, control.value());
			}
			controlJson.setFloat(KEY_VALUE_MIN, control.valueMin());
			controlJson.setFloat(KEY_VALUE_MAX, control.valueMax());
			controlJson.setFloat(KEY_VALUE_STEP, control.step());
			controlJson.setFloat(KEY_VALUE_TOGGLES, control.toggles());
			controlJson.setFloat(KEY_VALUE_LAYOUT_W, control.layoutW());
			array.append(controlJson);
		}
		JSONObject outerJsonObject = new JSONObject();
		outerJsonObject.setJSONArray(KEY_CONTROLS, array);
		return outerJsonObject.toString();
	}
	
	public static String valuesToJSON() {
		return valuesToJSON(new String[] {}, false);
	}
	
	public static String valuesToJSON(String[] filters, boolean singleLine) {
		// get sorted key list
        Set<String> names = controls.keySet(); 
//        System.out.println("HashSet before sorting : " + names); 
        // Sorting HashSet using List 
        List<String> tempList = new ArrayList<String>(names);
        Collections.sort(tempList); 
		
        // loop through keys
//		JSONObject json = new JSONObject();
		String jsonOutput = "{" + FileUtil.NEWLINE;
		for (IUIControl control : controls.values()) {
//		for (String hashKey : tempList) {
			// get key/val
//			IUIControl control = controls.get(hashKey);
			String key = control.id();
			String val;
			boolean isTitle = control.type() == IUIControl.TYPE_TITLE; 
			if(isTitle) {
				val = "";
			} else if(control.type() == IUIControl.TYPE_TEXTFIELD) {
				val = "\"" + control.valueString() + "\"";
			} else {
				val = ""+control.value();
			}
			// check keys against filter (or if no filter)
			boolean filterFound = false;
			for (int i = 0; i < filters.length; i++) {
				if(key.indexOf(filters[i]) != -1) filterFound = true;
			}

			// add to json string
			if((filters.length == 0 || filterFound) && control.type() == IUIControl.TYPE_TITLE == false) {
//				json.setFloat(key, val);
				jsonOutput += "\t" + "\"" + key + "\": " + val + "," + FileUtil.NEWLINE;
			}
		}
		// remove last comma
		jsonOutput = jsonOutput.substring(0, jsonOutput.length() - 3) + FileUtil.NEWLINE;
		jsonOutput += "}" + FileUtil.NEWLINE;
		if(singleLine) {
			return JsonUtil.jsonToSingleLine(jsonOutput);
		} else {
			return jsonOutput;
		}
//		return String.join("\n", sortedLines);
	}
	
	public static void loadValuesFromJSON(String jsonStr) {
		loadValuesFromJSON(JsonUtil.jsonFromString(jsonStr));
	}
	
	public static void loadValuesFromJSON(JSONObject jsonData) {
//		P.out(jsonData.toString());
//		P.out(JsonUtil.isValid(jsonData.toString()));
		Iterator<?> iterator = jsonData.keys().iterator();
		while(iterator.hasNext()) {
		    String key = (String) iterator.next();
		    if(controls.containsKey(key)) {
		    	controls.get(key).set(jsonData.getFloat(key));
		    } else {
		    	P.out("UI.loadValuesFromJSON() Error: couldn't find key: ", key);
		    }
		}
	}

	/////////////////////////////
	// IAppStoreListener
	/////////////////////////////

	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {
		if(key.equals(PEvents.KEY_PRESSED) && !UITextInput.active() && val.equals("\\")) active = !active;
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}