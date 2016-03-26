package com.haxademic.core.system;

import processing.core.PApplet;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.haxademic.core.app.P;

public class SystemUtil {
	
	public static String getJavaVersion() {
		return System.getProperty("java.version");
	}

	public static String getTimestamp( PApplet p ) {
		// use P.nf to pad date components to 2 digits for more consistent ordering across systems
		return  String.valueOf( P.year() ) + "-" + 
				P.nf( P.month(), 2 ) + "-" + 
				P.nf( P.day(), 2 ) + "-" + 
				P.nf( P.hour(), 2 ) + "-" + 
				P.nf( P.minute(), 2 ) + "-" + 
				P.nf( P.second(), 2 );
	}

	public static String getTimestampFine( PApplet p ) {
		return SystemUtil.getTimestamp(p) + "-" + P.nf( p.frameCount, 8 ); 
	}
	
	// Patch TAB capture ability - Processing 2.0 broke this in 3D rendering contexts
	public static void p2TabKeyInputPatch() {
//		if(PJOGL.canvas != null) { PJOGL.canvas.setFocusTraversalKeysEnabled(false); }
	}
	
	public static void printRunningProcesses() {
	    try {
	        String line;
	        Process p = Runtime.getRuntime().exec("ps -e");
	        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        while ((line = input.readLine()) != null) {
	            System.out.println("## "+line); //<-- Parse data here.
	        }
	        input.close();
	    } catch (Exception err) {
	        err.printStackTrace();
	    }
	}
	
	public static boolean hasProcessContainingString(String processName) {
		try {
			String line;
			Process p = Runtime.getRuntime().exec("ps -e");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				if(line.indexOf(processName) != -1) {
					return true;
				}
			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
		return false;
	}
	
	public static void killProcessContainingString(String processName) {
		try {
			String line;
			Process p = Runtime.getRuntime().exec("ps -e");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				if(line.indexOf(processName) != -1) {
					P.println("found & killing: "+line);
					String processline = line.trim().split(" ")[0];
					String killCmd = "kill -9 "+processline;
					P.println("killCmd: "+killCmd);
					Process d = Runtime.getRuntime().exec(killCmd);
				}
			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	

}
