package com.haxademic.core.system;

import java.io.IOException;

import com.haxademic.core.app.P;

import processing.core.PApplet;

public class AppRestart {

	/** 
	 * Sun property pointing the main class and its arguments. 
	 * Might not be defined on non Hotspot VM implementations.
	 */
	public static final String SUN_JAVA_COMMAND = "sun.java.command";

	public static void quit( PApplet p ) {
		P.println("Attempting to quit...");
		p.exit();
	}
	
	public static void restart( PApplet p ) {
		try {
			P.println("Attempting to restart...");
			restartApplication( p, null);
		} catch (IOException e) {
			P.println("Error: couldn't restart");
		}
	}

	/**
	 * Restart the current Java application - currently this only seems to work once and fails after that...
	 * @param runBeforeRestart some custom code to be run before restarting
	 * @throws IOException
	 */
	public static void restartApplication( PApplet p, Runnable runBeforeRestart) throws IOException {
		try {
			final String cmd = AppUtil.getAppRunCommandRelative();
			P.out(cmd);
			// execute the command in a shutdown hook, to be sure that all the
			// resources have been disposed before restarting the application
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						Runtime.getRuntime().exec(cmd.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			// execute some custom code before restarting
			if (runBeforeRestart!= null) {
				runBeforeRestart.run();
			}
			// exit
			P.p.exit();
		} catch (Exception e) {
			// something went wrong
			throw new IOException("Error while trying to restart the application", e);
		}
	}

}
