package com.haxademic.core.system;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Timer;

import com.haxademic.core.app.P;

public class SystemUtil {
	
	public static final String WINDOWS_RESTART_COMMAND = "SHUTDOWN -r -t 10";
	
	public static String getJavaVersion() {
		return System.getProperty("java.version");
	}

	public static boolean isOSX() {
		return System.getProperty("os.name").contains("OS X");
	}
	
	public static void runGarbageCollection() {
		System.gc();
	}
	
	public static String getDateStamp() {
		// use P.nf to pad date components to 2 digits for more consistent ordering across systems
		return  String.valueOf( P.year() ) + "-" + 
				P.nf( P.month(), 2 ) + "-" + 
				P.nf( P.day(), 2 );
	}
	
	public static String getTimestamp() {
		// use P.nf to pad date components to 2 digits for more consistent ordering across systems
		return  String.valueOf( P.year() ) + "-" + 
				P.nf( P.month(), 2 ) + "-" + 
				P.nf( P.day(), 2 ) + "-" + 
				P.nf( P.hour(), 2 ) + "-" + 
				P.nf( P.minute(), 2 ) + "-" + 
				P.nf( P.second(), 2 );
	}

	public static String getTimestampFine() {
		return SystemUtil.getTimestamp() + "-" + P.nf( P.p.frameCount, 8 ); 
	}
	
	public static void setTimeout(ActionListener callback, int delay) {
		Timer deferredStateTimer = new Timer(delay, callback);
		deferredStateTimer.setRepeats(false);
		deferredStateTimer.start();
	}
	
	public static void runShellCommand(String command) {
		try {
			Process process;
			if(SystemUtil.isOSX()) { 
				process = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", command});
			} else {
				process = Runtime.getRuntime().exec(new String[] {"cmd", "/K", command});
			}
			process.isAlive();
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
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
					d.isAlive(); // removes eclipse warning
				}
			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	public static void copyStringToClipboard(String str) {
	    StringSelection selection = new StringSelection(str);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(selection, selection);
	}
	
	public static String getClipboardContents() {
	    String result = "";
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    Transferable contents = clipboard.getContents(null);
	    boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
	    if (hasTransferableText) {
	        try {
	            result = (String)contents.getTransferData(DataFlavor.stringFlavor);
	        }
	        catch (UnsupportedFlavorException e){
	            P.out(e.getLocalizedMessage());
	        } catch(IOException e){
	        	P.out(e.getLocalizedMessage());
	        }
	    }
	    return result;
	}
	
	public static void openWebPage(String url) {
		if(Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException e) { e.printStackTrace(); } 
			  catch (URISyntaxException e) { e.printStackTrace(); }
		}
	}
	
}
