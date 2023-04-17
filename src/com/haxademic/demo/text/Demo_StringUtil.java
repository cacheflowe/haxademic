package com.haxademic.demo.text;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.text.StringUtil;

public class Demo_StringUtil
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void firstFrame() {
	}

	protected void drawApp() {
		p.background(0);
		
		// test functions
		DemoAssets.setDemoFont(p.g);		
		p.text(
			"subStringByProgress: " + StringUtil.subStringByProgress("HELLOOOOOO", -1f + FrameLoop.count(0.02f) % 2f) + FileUtil.NEWLINE + 
			"formattedDecimal: " + StringUtil.formattedDecimal(""+1002.2333333f) + FileUtil.NEWLINE +
			"safeString: " + StringUtil.safeString("DRFTYGU@#43567$%^&*_-.jmohuuytt.png") + FileUtil.NEWLINE +
			"roundToPrecision: " + StringUtil.roundToPrecision(1, 3, true) + FileUtil.NEWLINE +
			"roundToPrecision: " + StringUtil.roundToPrecision(1, 3, false) + FileUtil.NEWLINE +
			"roundToPrecision: " + StringUtil.roundToPrecision(-2, 3, true) + FileUtil.NEWLINE +
			"roundToPrecision: " + StringUtil.roundToPrecision(-2, 3, false) + FileUtil.NEWLINE +
			"roundToPrecision: " + StringUtil.roundToPrecision(10.25555f, 2, true) + FileUtil.NEWLINE +
			"roundToPrecision: " + StringUtil.roundToPrecision(10.25555f, 2, false) + FileUtil.NEWLINE +
			"roundToPrecision: " + StringUtil.roundToPrecision(-10000.1f, 4, true) + FileUtil.NEWLINE +
			"roundToPrecision: " + StringUtil.roundToPrecision(-10000.1f, 4, false) + FileUtil.NEWLINE +
			""
		, 20, 20);
	}

}