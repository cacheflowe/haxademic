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
		System.out.println("-- max memory: " + maxMemory / 1024);  
		System.out.println("-- total free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / 1024);
		System.out.println("---------------------------------");  
	}
	
	public static int memoryMax() {
		Runtime runtime = Runtime.getRuntime();  
		return Math.round(runtime.maxMemory() / 1024f);  
	}
	
	public static int memoryAllocated() {
		Runtime runtime = Runtime.getRuntime();  
		return Math.round(runtime.totalMemory() / 1024f);  
	}
	
	public static int memoryFree() {
		Runtime runtime = Runtime.getRuntime();  
		return Math.round(runtime.freeMemory() / 1024f);  
	}
	
	public static void print( String str ) {
		System.out.println( str );  
	}
	
	public static void printErr( String str ) {
		System.err.println( ":[" + str );  
	}

	public static void alert( String message ) {
		// more info: http://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
		JOptionPane.showMessageDialog( P.p.frame, message );
	}
	
	public static void printBig(String debugString) {
		P.println("===================================");
		P.println("== " + debugString);
		P.println("===================================");
	}

}
