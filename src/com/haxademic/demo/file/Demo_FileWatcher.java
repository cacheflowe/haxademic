package com.haxademic.demo.file;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.file.WatchDir;
import com.haxademic.core.file.WatchDir.IWatchDirListener;

public class Demo_FileWatcher
extends PAppletHax
implements IWatchDirListener {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	
	protected WatchDir watchDir;
	
	public void setupFirstFrame() {
		watchDir = new WatchDir(FileUtil.getFile("images"), true, this);
	}

	public void drawApp() {
		p.background(0);
	}
	
	public void keyPressed() {
		super.keyPressed();
		// if(p.key == ' ') textureIndex++; 
	}

	// IWatchDirListener callback
	
	@Override
	public void dirUpdated(int eventType, String filePath) {
		P.out(eventType, filePath);
	}

}