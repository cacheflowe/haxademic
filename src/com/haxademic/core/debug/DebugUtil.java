package com.haxademic.core.debug;

import javax.swing.JOptionPane;

import com.haxademic.core.app.P;

public class DebugUtil {
	
	public static void showMemoryUsage() {
		Runtime runtime = Runtime.getRuntime();  
		  
		long maxMemory = runtime.maxMemory();  
		long allocatedMemory = runtime.totalMemory();  
		long freeMemory = runtime.freeMemory();  
		   
		System.out.println("-- MEMORY USAGE -----------------");  
		System.out.println("-- free memory: " + freeMemory / 1024);  
		System.out.println("-- allocated memory: " + allocatedMemory / 1024);  
		System.out.println("-- max memory: " + maxMemory /1024);  
		System.out.println("-- total free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / 1024);
		System.out.println("---------------------------------");  
	}
	
	public static void print( String str ) {
		System.out.println( str );  
	}

	public static void alert( String message ) {
		// more info: http://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
		JOptionPane.showMessageDialog( P.p.frame, message );
	}
	
}
