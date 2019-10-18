package com.haxademic.demo.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.net.CachedJsonPoller;
import com.haxademic.core.net.CachedJsonPoller.ICachedJsonPollerDelegate;
import com.haxademic.core.net.JsonUtil;

public class Demo_CachedJsonPoller
extends PAppletHax
implements ICachedJsonPollerDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected CachedJsonPoller polled;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false );
	}

	public void setupFirstFrame() {
		polled = new CachedJsonPoller("http://localhost.dbg-www.com/data/json/pdus.json", FileUtil.getFile("text/json/pdus.json"), 5, this);
	}
	
	public void drawApp() {
		// background
		pg.beginDraw();
		PG.setDrawCenter(pg);
		pg.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		pg.fill(255);
		pg.endDraw();
		p.image(pg, 0, 0);
	}
		
	////////////////////////////////////////
	// ICachedJsonPollerDelegate methods
	////////////////////////////////////////
	
	public void jsonUpdated(String json) {
		P.out("jsonUpdated", JsonUtil.jsonToSingleLine(json));
	}
	
	public void jsonNotUpdated(String json) {
		P.out("jsonNotUpdated", JsonUtil.jsonToSingleLine(json));
	}
	
	public void jsonNotValid(String json) {
		P.out("jsonNotValid", json);
	}
	
}
