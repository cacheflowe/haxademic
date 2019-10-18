package com.haxademic.core.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.net.IJsonRequestCallback;
import com.haxademic.core.net.JsonHttpRequest;
import com.haxademic.core.net.JsonPoller;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.system.SystemUtil;

import processing.data.JSONObject;

public class CachedJsonPoller
implements IJsonRequestCallback {

	public interface ICachedJsonPollerDelegate {
		public void jsonUpdated(String json);
		public void jsonNotUpdated(String json);
		public void jsonNotValid(String json);
	}
	
	protected JsonPoller jsonPoller;
	protected String remoteJsonURL;
	protected String localJsonPath;
	protected int intervalSeconds;
	protected ICachedJsonPollerDelegate delegate;
	protected String curJsonStr;
	protected JSONObject curJsonObj;
		
	public CachedJsonPoller(String remoteJsonURL, String localJsonPath, int intervalSeconds, ICachedJsonPollerDelegate delegate) {
		// store props
		this.remoteJsonURL = remoteJsonURL;
		this.localJsonPath = localJsonPath;
		this.intervalSeconds = intervalSeconds;
		this.delegate = delegate;
		loadJsonFromDisk();
	}
	
	// load from disk
	
	public String curJsonStr() {
		return curJsonStr;
	}
	
	protected void loadJsonFromDisk() {
		// load local json before we poll the server and trigger a data caching
		if(FileUtil.fileExists(localJsonPath)) {
			String[] jsonLines = FileUtil.readTextFromFile(localJsonPath);
			curJsonStr = FileUtil.textLinesJoined(jsonLines);
			
			// launch web app & delay polling since we've just loaded the cached json
			SystemUtil.setTimeout(delayedPollInit, intervalSeconds * 1000);
		} else {
			// if not local file, we're probably setting this up for the first time, so start polling
			initPolling();
		}
	}
	
	protected ActionListener delayedPollInit = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			initPolling();
		}
	};
	
	public void initPolling() {
		if(intervalSeconds <= 0 || jsonPoller != null) return;  
		// Check for local config json, then create json request manager.
		// Start polling the admin after initial local cached data load.
		jsonPoller = new JsonPoller(remoteJsonURL, intervalSeconds * 1000, this);
	}
	
	////////////////////////////////////////
	// IJsonRequestCallback callbacks 
	////////////////////////////////////////
	
	public void postSuccess(String responseText, int responseCode, String requestId, int responseTime) {
		if(JsonUtil.isValid(responseText)) {
			// compare old & new JSON
			String newJsonStr = JSONObject.parse(responseText).toString();
			String oldJsonStr = (curJsonStr != null && curJsonStr.length() > 0) ? JSONObject.parse(curJsonStr).toString() : null;	// if nothing was read to disk, old json is null for comparison
			// if old & new aren't equal, store it and send it out!
			if(newJsonStr.equals(oldJsonStr) == false) {
				FileUtil.createDir(FileUtil.pathForFile(localJsonPath));	// create json dir if this is a fresh install. it'll get overwritten byt hte full sync, but this prevents errors
				curJsonStr = newJsonStr;
				FileUtil.writeTextToFile(localJsonPath, curJsonStr);
				this.delegate.jsonUpdated(curJsonStr);
			} else {
				this.delegate.jsonNotUpdated(curJsonStr);
			}
		} else {
			this.delegate.jsonNotValid(curJsonStr);
		}
	}

	public void postFailure(String responseText, int responseCode, String requestId, int responseTime, String errorMessage) {
		P.out("ConfigDataPoller: API Unreachable:");
		// P.out(App.LOG, "-- " + errorMessage);
	}	
	
	public void aboutToRequest(JsonHttpRequest request) {
		
	}
	
}
