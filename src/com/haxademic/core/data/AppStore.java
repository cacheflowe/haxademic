package com.haxademic.core.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Timer;

public class AppStore {
	
	protected HashMap<String, Number> store;
	protected ArrayList<IAppStoreUpdatable> updatables;

	public AppStore() {
		store = new HashMap<String, Number>();
		updatables = new ArrayList<IAppStoreUpdatable>();
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

	public float getValueF(String storeKey) {
		return store.get(storeKey).floatValue();
	}

	public int getValueI(String storeKey) {
		return store.get(storeKey).intValue();
	}

}
