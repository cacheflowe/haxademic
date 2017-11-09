package com.haxademic.core.data.store;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Timer;

public class AppStore {
	
	public static AppStore instance;
	
	protected HashMap<String, Number> store;
	protected HashMap<String, String> stringStore;
	protected ArrayList<IAppStoreUpdatable> updatables;

	public AppStore() {
		store = new HashMap<String, Number>();
		stringStore = new HashMap<String, String>();
		updatables = new ArrayList<IAppStoreUpdatable>();
	}
	
	public static AppStore instance() {
		if(instance != null) return instance;
		instance = new AppStore();
		return instance;
	}
	
	public void registerStatable(IAppStoreUpdatable obj) {
		updatables.add(obj);
	}
	
	public void setValue(String storeKey, Number val) {
		store.put(storeKey, val);
		for (IAppStoreUpdatable obj : updatables) {
			obj.updatedAppStoreValue(storeKey, val);
		}
	}
	
	public void setValue(String storeKey, String val) {
		stringStore.put(storeKey, val);
		for (IAppStoreUpdatable obj : updatables) {
			obj.updatedAppStoreValue(storeKey, val);
		}
	}
	
	public void setValueWithDelay(String storeKey, Number val, int delay) {
		Timer deferredStateTimer = new Timer(delay, new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0) {
				setValue(storeKey, val);
			}
		});
		deferredStateTimer.setRepeats(false);
		deferredStateTimer.start();
	}
	
	public Number getValue(String storeKey) {
		return store.get(storeKey);
	}

	public String getValueS(String storeKey) {
		return stringStore.get(storeKey);
	}
	
	public float getValueF(String storeKey) {
		return store.get(storeKey).floatValue();
	}

	public int getValueI(String storeKey) {
		return store.get(storeKey).intValue();
	}

}
