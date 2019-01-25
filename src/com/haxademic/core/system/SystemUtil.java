package com.haxademic.core.system;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Timer;

import com.haxademic.core.app.P;

import processing.core.PApplet;
import processing.core.PImage;

public class SystemUtil {
	
	public static String getJavaVersion() {
		return System.getProperty("java.version");
	}

	public static boolean isOSX() {
		return System.getProperty("os.name").contains("OS X");
	}
	
	public static void runGarbageCollection() {
		System.gc();
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
	
	public static void setTimeout(ActionListener callback, int delay) {
		Timer deferredStateTimer = new Timer(delay, callback);
		deferredStateTimer.setRepeats(false);
		deferredStateTimer.start();
	}
	
	public static void runOSXCommand(String command) {
		try {
			Process process = Runtime.getRuntime().exec( new String[] { "/bin/sh", "-c", command } );
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
	
	public String getClipboardContents() {
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
	
	public static PImage getScreenshot(int x, int y, int width, int height) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		//DisplayMode mode = gs[0].getDisplayMode();
		Rectangle bounds = new Rectangle(x, y, width, height);
		BufferedImage desktop = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		try {
			desktop = new Robot(gs[0]).createScreenCapture(bounds);
		}
		catch(AWTException e) {
			System.err.println("Screen capture failed.");
		}

		return new PImage(desktop);
	}

	public static void clickScreen(int x, int y) {
		try {
			Robot clickRobot = new Robot();
			clickRobot.mouseMove(x, y);
			clickRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			clickRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
