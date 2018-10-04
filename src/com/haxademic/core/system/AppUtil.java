package com.haxademic.core.system;

import java.io.File;
import java.lang.management.ManagementFactory;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class AppUtil {
	
	public static void setFrameBackground(PApplet p, float r, float g, float b) {
		if(p.frame != null) {
			p.frame.setBackground(new java.awt.Color(r,g,b));
		}
	}
	
	public static void removeChrome(PApplet p) {
		if(p.frame != null) {
			p.frame.removeNotify();
			p.frame.setUndecorated(true);
			p.frame.addNotify();
		}
	}
	
	public static void setTitle(PApplet p, String title) {
		if(p.frame != null) {
			p.getSurface().setTitle(title);
		}
	}

	public static void setAppToDockIcon(PApplet p) {
		if(System.getProperty("os.name").contains("Mac OS") == false) return;
//		Application application = Application.getApplication();
//		application.setDockIconImage((BufferedImage)p.get().getNative());
	}
	
	public static void setPImageToDockIcon(PImage img) {
		if(System.getProperty("os.name").contains("Mac OS") == false) return;
//		Application application = Application.getApplication();
//		application.setDockIconImage((BufferedImage)img.get().getNative());
	}
	
	public static void requestForeground(final PApplet p) {
		if(System.getProperty("os.name").contains("Mac OS") == false) return;
//		Application application = Application.getApplication();
//		application.requestForeground(true);
		
		
		if(p.frame != null) {
			java.awt.EventQueue.invokeLater(new Runnable() {
			    @Override
			    public void run() {
//			    	int sta = p.frame.getExtendedState() & ~JFrame.ICONIFIED & JFrame.NORMAL;
//
//			        p.frame.setExtendedState(sta);
			        p.frame.setAlwaysOnTop(true);
			        p.frame.toFront();
			        p.frame.requestFocus();
//			    	p.frame.repaint();
			    	 
//			    	p.frame.setExtendedState(JFrame.ICONIFIED);
//			    	p.frame.setExtendedState(JFrame.NORMAL);
//			    	p.frame.setState(java.awt.Frame.ICONIFIED);
//			    	p.frame.setState(java.awt.Frame.NORMAL);
			    }
			});
		}

	}
	
	/** 
	 * Sun property pointing the main class and its arguments. 
	 * Might not be defined on non Hotspot VM implementations.
	 */
	public static final String SUN_JAVA_COMMAND = "sun.java.command";
	
	public static String getAppRunCommand() {
		// java binary
		// String java = System.getProperty("java.home") + "/bin/java";
//		String java = "java";
		
		String java = "\"" + System.getProperty("java.home") + "/bin/java" + "\"";

		// vm arguments
		//			List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
		StringBuffer vmArgsOneLine = new StringBuffer();
		for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			// if it's the agent argument : we ignore it otherwise the
			// address of the old application and the new one will be in conflict
			if (!arg.contains("-agentlib")) {
				vmArgsOneLine.append(arg);
				vmArgsOneLine.append(" ");
			}
		}
		// init the command to execute, add the vm args
		final StringBuffer cmd = new StringBuffer("" + java + " " + vmArgsOneLine);

		// program main and program arguments
		String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
		// program main is a jar
		if (mainCommand[0].endsWith(".jar")) {
			// if it's a jar, add -jar mainJar
			cmd.append("-jar " + new File(mainCommand[0]).getPath());
		} else {
			// else it's a .class, add the classpath and mainClass
			// crap the classpath with quotes, in case there are spaces
			cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
		}
		// finally add program arguments
		for (int i = 1; i < mainCommand.length; i++) {
			cmd.append(" ");
			cmd.append(mainCommand[i]);
		}
		return cmd.toString();
	}
	
	public static String getAppRunCommandRelative() {
		String absoluteRunCommand = AppUtil.getAppRunCommand();
		String projectPath = FileUtil.getHaxademicPath() + File.separator;
		return absoluteRunCommand.replace(projectPath, "");
	}
	
	public static void writeRunScript(String scriptDestinationPath) {
		boolean isWindows = P.platform == PConstants.WINDOWS;
		if(isWindows) writeRunScriptForWindows(scriptDestinationPath);
		else writeRunScriptForBash(scriptDestinationPath);
	}
	
	public static void writeRunScriptForWindows(String scriptDestinationPath) {
		String fileExtension = ".cmd";
		String runScriptPath = FileUtil.getHaxademicPath() + File.separator + scriptDestinationPath + fileExtension;
		String scriptStr = "REM @echo off" + "\n";
		scriptStr += "cd .." + "\n";
		scriptStr += "timeout 3" + "\n\n";
		scriptStr += AppUtil.getAppRunCommandRelative() + "\n\n";
		scriptStr += "cd scripts" + "\n";
		scriptStr += "pause" + "\n\n";
		FileUtil.writeTextToFile(runScriptPath, scriptStr);
	}
	
	public static void writeRunScriptForBash(String scriptDestinationPath) {
		String fileExtension = ".sh";
		String runScriptPath = FileUtil.getHaxademicPath() + File.separator + scriptDestinationPath + fileExtension;
		String scriptStr = "";
		scriptStr += "cd .." + "\n\n";
		scriptStr += AppUtil.getAppRunCommandRelative() + "\n\n";
		FileUtil.writeTextToFile(runScriptPath, scriptStr);
		SystemUtil.runOSXCommand("chmod 777 "+runScriptPath);
	}
	
}
