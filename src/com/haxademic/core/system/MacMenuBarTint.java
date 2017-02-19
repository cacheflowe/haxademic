package com.haxademic.core.system;

import java.io.IOException;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;

public class MacMenuBarTint {

	public static Boolean LAUNCHED = false;

	public static void launchTint() {
		// open Menu Bar Tint app if it exists
		if( FileUtil.fileOrPathExists( "/Applications/Menu Bar Tint.app/" ) ) {
			try {
				MacMenuBarTint.LAUNCHED = true;
				Runtime.getRuntime().exec( new String[] { "/bin/sh", "-c", "open /Applications/Menu\\ Bar\\ Tint.app/" } );
			} catch (IOException e) { 
				MacMenuBarTint.LAUNCHED = false;
				e.printStackTrace(); 
			}
		} else {
			P.println("## Download the free Menu Bar Tint app from: http://manytricks.com/menubartint/");
			P.println("## This will launch automatically once installed.");
		}
	}

	public static void shutDownTint() {
		if( MacMenuBarTint.LAUNCHED == true ) { 
			try {
				Runtime.getRuntime().exec( new String[] { "/bin/sh", "-c", "kill $(ps aux | grep Menu\\ Bar\\ Tint | awk '{print $2}')" } );
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
}
