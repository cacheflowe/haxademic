package com.haxademic.core.data.store;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Timer;

import com.haxademic.core.debug.DebugView;

import processing.core.PGraphics;
import processing.core.PImage;

public class AppStore {
	
	public static AppStore instance;
	
	protected HashMap<String, Number> numberStore;
	protected HashMap<String, String> stringStore;
	protected HashMap<String, Boolean> boolStore;
	protected HashMap<String, PImage> imageStore;
	protected HashMap<String, PGraphics> bufferStore;
	protected ArrayList<IAppStoreListener> listeners;

	public AppStore() {
		numberStore = new HashMap<String, Number>();
		stringStore = new HashMap<String, String>();
		boolStore = new HashMap<String, Boolean>();
		imageStore = new HashMap<String, PImage>();
		bufferStore = new HashMap<String, PGraphics>();
		listeners = new ArrayList<IAppStoreListener>();
	}
	
	public static AppStore instance() {
		if(instance != null) return instance;
		instance = new AppStore();
		return instance;
	}
	
	public void addListener(IAppStoreListener obj) {
		listeners.add(obj);
	}
	
	public void removeListener(IAppStoreListener obj) {
		listeners.remove(obj);
	}
	
	public void setNumber(String storeKey, Number val) {
		numberStore.put(storeKey, val);
		for (IAppStoreListener obj : listeners) {
			obj.updatedNumber(storeKey, val);
		}
	}
	
	public void setString(String storeKey, String val) {
		stringStore.put(storeKey, val);
		for (IAppStoreListener obj : listeners) {
			obj.updatedString(storeKey, val);
		}
	}
	
	public void setBoolean(String storeKey, Boolean val) {
		boolStore.put(storeKey, val);
		for (IAppStoreListener obj : listeners) {
			obj.updatedBoolean(storeKey, val);
		}
	}
	
	public void setImage(String storeKey, PImage val) {
		imageStore.put(storeKey, val);
		for (IAppStoreListener obj : listeners) {
			obj.updatedImage(storeKey, val);
		}
	}
	
	public void setBuffer(String storeKey, PGraphics val) {
		bufferStore.put(storeKey, val);
		for (IAppStoreListener obj : listeners) {
			obj.updatedBuffer(storeKey, val);
		}
	}
	
	public void setValueWithDelay(String storeKey, Number val, int delay) {
		Timer deferredStateTimer = new Timer(delay, new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0) {
				setNumber(storeKey, val);
			}
		});
		deferredStateTimer.setRepeats(false);
		deferredStateTimer.start();
	}
	
	public Number getNumber(String storeKey) {
		return numberStore.get(storeKey);
	}

	public String getString(String storeKey) {
		return stringStore.get(storeKey);
	}
	
	public float getFloat(String storeKey) {
		return numberStore.get(storeKey).floatValue();
	}

	public int getInt(String storeKey) {
		return numberStore.get(storeKey).intValue();
	}

	public boolean getBoolean(String storeKey) {
		return boolStore.get(storeKey).booleanValue();
	}

	public PImage getImage(String storeKey) {
		return imageStore.get(storeKey);
	}
	
	public PGraphics getBuffer(String storeKey) {
		return bufferStore.get(storeKey);
	}
	
	public void showStoreValuesInDebugView() {
		for (String key : numberStore.keySet()) {
			DebugView.setValue(key, numberStore.get(key).floatValue());
		}
		for (String key : stringStore.keySet()) {
			DebugView.setValue(key, stringStore.get(key));
		}
		for (String key : boolStore.keySet()) {
			DebugView.setValue(key, boolStore.get(key));
		}
	}
}
