package com.haxademic.core.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.data.JSONObject;

public class CachedJsonPoller
implements IJsonRequestCallback {

	public interface ICachedJsonPollerDelegate {
		public void jsonUpdated(String json);
		public void jsonNotUpdated(String json);
		public void jsonNotValid(String json);
		public void jsonRequestNetError(String error);
	}
	
	protected JsonPoller jsonPoller;
	protected String remoteJsonURL;
	protected String localJsonPath;
	protected int intervalSeconds;
	protected ICachedJsonPollerDelegate delegate;
	protected String curJsonStr;
	protected String newJsonStr;
	protected JSONObject curJsonObj;
		
	public CachedJsonPoller(String remoteJsonURL, String localJsonPath, int intervalSeconds, ICachedJsonPollerDelegate delegate) {
		// store props
		this.remoteJsonURL = remoteJsonURL;
		this.localJsonPath = localJsonPath;
		this.intervalSeconds = intervalSeconds;
		this.delegate = delegate;
		loadJsonFromDisk();
	}
	
	public void setDelegate(ICachedJsonPollerDelegate delegate) {
		this.delegate = delegate;
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
			
			// delay polling since we've just loaded the cached json - this counts as the first "polling"
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
	
	public void initDeferredPolling(int intervalSeconds) {
		this.intervalSeconds = intervalSeconds;
		initPolling();
	}
	
	////////////////////////////////////////
	// IJsonRequestCallback callbacks 
	////////////////////////////////////////
	
	protected void writeNewJsonToDisk() {
		FileUtil.createDir(FileUtil.pathForFile(localJsonPath));	// make sure local dir exists
		curJsonStr = newJsonStr;
		FileUtil.writeTextToFile(localJsonPath, curJsonStr);
	}
	
	public void postSuccess(String responseText, int responseCode, String requestId, int responseTime) {
		if(JsonUtil.isValid(responseText)) {
			// compare old & new JSON
			newJsonStr = JSONObject.parse(responseText).toString();
			String oldJsonStr = (curJsonStr != null && curJsonStr.length() > 0) ? JSONObject.parse(curJsonStr).toString() : null;	// if nothing was read to disk, old json is null for comparison
			// if old & new aren't equal, store it and send it out!
			if(newJsonStr.equals(oldJsonStr) == false) {
				writeNewJsonToDisk();
				if(this.delegate != null) this.delegate.jsonUpdated(curJsonStr);
			} else {
				if(this.delegate != null) this.delegate.jsonNotUpdated(curJsonStr);
			}
		} else {
			if(this.delegate != null) this.delegate.jsonNotValid(curJsonStr);
		}
	}

	public void postFailure(String responseText, int responseCode, String requestId, int responseTime, String errorMessage) {
		if(this.delegate != null) this.delegate.jsonRequestNetError(errorMessage);
	}	
	
	public void aboutToRequest(JsonHttpRequest request) {
		
	}
	
}
