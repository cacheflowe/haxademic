package com.haxademic.demo.data;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.data.store.AppStore;
import com.haxademic.core.data.store.IAppStoreListener;

public class Demo_AppStore
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame() {
		AppStore.instance().addListener(this);
	}
	
	public void drawApp() {
		background(0);
		AppStore.instance().setNumber("frameCount", p.frameCount);
		AppStore.instance().setBoolean("Frame over 100", (p.frameCount % 200) > 100);
		AppStore.instance().setNumber("mousePercentX()", p.mousePercentX());
		AppStore.instance().setNumber("mousePercentY()", p.mousePercentY());
		// for (int i = 0; i < 50; i++) { AppStore.instance().setNumber("test_"+i, i); }
		AppStore.instance().showStoreValuesInDebugView();
	}

	@Override
	public void updatedNumber(String key, Number val) {
//		p.debugView.setValue(key, val.floatValue());
	}

	@Override
	public void updatedString(String key, String val) {
//		p.debugView.setValue(key, val);
	}

	@Override
	public void updatedBoolean(String key, Boolean val) {
//		p.debugView.setValue(key, val);
	}	

}

