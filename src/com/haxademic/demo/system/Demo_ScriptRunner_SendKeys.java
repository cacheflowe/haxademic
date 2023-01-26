package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.shell.IScriptCallback;
import com.haxademic.core.system.shell.ScriptRunner;

public class Demo_ScriptRunner_SendKeys 
extends PAppletHax
implements IScriptCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ScriptRunner scriptRunner;
	
	protected void firstFrame() {
//		scriptRunner = new ScriptRunner("image-sequence-to-video", this);
//		scriptRunner.runWithParams(FileUtil.getFile("haxademic/images/floaty-blob.anim"));
		
		scriptRunner = new ScriptRunner("windows-sendkeys", this);
	}

	protected void drawApp() {
		p.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		if(p.frameCount % 600 == 0) {
			scriptRunner.runWithParams("{F11}");
		}
		if(p.frameCount % 600 == 300) {
			scriptRunner.runWithParams("{ESCAPE}");
		}
	}
	
	public void mouseClicked() {
//		scriptRunner.runWithParams("{ESC}");
		scriptRunner.runWithParams("{F11}");
	}
	
	@Override
	public void scriptComplete() {
		P.out("Script complete!");
	}

}
