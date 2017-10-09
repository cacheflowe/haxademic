package com.haxademic.core.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WindowsSystemUtil {

	public static void killOtherJavaApps() {
        try {
            // Run "netsh" Windows command
            Process process = Runtime.getRuntime().exec("tasklist"); // prepend cmd /c ?
            
            String javaProcessIDs = "";
            String line;
	        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        while ((line = input.readLine()) != null) {
	            if(line.indexOf("javaw.exe") != -1 || line.indexOf("cmd.exe") != -1) {
	            	// System.out.println("## "+line); //<-- Parse data here.
	            	if(javaProcessIDs.length() > 0) javaProcessIDs += ",";
	            	javaProcessIDs += line.split("\\s+")[1];
	            }
	        }
	        
	        String[] javaPIDs = javaProcessIDs.split(",");
	        if(javaPIDs.length > 1) {
				for (int i = 0; i < javaPIDs.length - 1; i++) {
					// P.println("javaPIDs[] killing:", i, "=", javaPIDs[i]);
					// P.println("kill command:", "Taskkill /PID "+ javaPIDs[i] +" /F");
		        	Process killProcess = Runtime.getRuntime().exec("Taskkill /PID "+ javaPIDs[i] +" /F");
				}
			}
	        
            // Get input streams
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Read command standard output
            String s;
//            System.out.println("Standard output: ");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // Read command errors
//            System.out.println("Standard error: ");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
	}
}
