package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.shell.IScriptCallback;
import com.haxademic.core.system.shell.ScriptRunner;

public class Demo_ScriptRunner 
extends PAppletHax
implements IScriptCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ScriptRunner scriptRunner;
	
	public void firstFrame() {
//		scriptRunner = new ScriptRunner("image-sequence-to-video", this);
//		scriptRunner.runWithParams(FileUtil.getFile("haxademic/images/floaty-blob.anim"));
		
		scriptRunner = new ScriptRunner("normalize-wav-file", this);
		scriptRunner.runWithParams(FileUtil.getFile("audio/drums/bass.wav"), 0);
	}

	public void drawApp() {
		p.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
	}
	
	@Override
	public void scriptComplete() {
		P.out("Script complete!");
	}

}
