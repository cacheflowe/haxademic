package com.haxademic.core.data.store;

public interface IAppStoreUpdatable {
	public void updatedAppStoreValue(String key, Number val);
	public void updatedAppStoreValue(String key, String val);
}