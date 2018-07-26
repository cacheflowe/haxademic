package com.haxademic.core.data.store;

public interface IAppStoreListener {
	public void updatedNumber(String key, Number val);
	public void updatedString(String key, String val);
	public void updatedBoolean(String key, Boolean val);
}