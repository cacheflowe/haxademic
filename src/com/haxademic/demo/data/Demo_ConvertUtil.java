package com.haxademic.demo.data;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.media.DemoAssets;

public class Demo_ConvertUtil
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void drawApp() {
		background(0);

		DemoAssets.setDemoFont(p.g);

		String outputStr = "";
		outputStr += "ConvertUtil.stringToInt(\"10\") = " + ConvertUtil.stringToInt("10") + "\n";
		outputStr += "ConvertUtil.stringToFloat(\"100.01\") = " + ConvertUtil.stringToFloat("100.01") + "\n";
		outputStr += "ConvertUtil.intToString(\"-15\") = " + ConvertUtil.intToString(-15) + "\n";
		outputStr += "ConvertUtil.stringToBoolean(\"true\") = " + ConvertUtil.stringToBoolean("true") + "\n";
		p.text(outputStr, 20, 20, 400, 200);
	}	

}

