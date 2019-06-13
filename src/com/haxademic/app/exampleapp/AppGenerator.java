package com.haxademic.app.exampleapp;

import java.io.File;
import java.io.IOException;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;

public class AppGenerator
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String newProjectName = "new-hax-project";
	protected StringBufferLog log = new StringBufferLog(20, 16);
	
	protected File haxPath;
	protected String parentDir;
	protected String newProjectDir;
	
	protected void setupFirstFrame() {
		// get haxademic & parent dirs
		haxPath = new File(FileUtil.getHaxademicPath());
		parentDir = haxPath.getParent();
	}
	
	public void drawApp() {
		p.background(0);
		
		// info
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 20);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 2f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text("Press spacebar to generate project:" + FileUtil.NEWLINE + 
			   newProjectName + FileUtil.NEWLINE +
			   "Please be careful." + FileUtil.NEWLINE
			   , 20, 20);

		// print log
		log.printToScreen(p.g, 20, 200);
	}
	
	protected void generateProject() {
		// create project dir
		newProjectDir = parentDir + FileUtil.SEPARATOR + newProjectName;
		boolean projectDirCreated = FileUtil.createDir(newProjectDir);
		log.update("==============================");
		log.update("New Project dir created: "+projectDirCreated);
		log.update("-> " + newProjectName);
		FileUtil.createDir(newProjectDir + FileUtil.safePath("/output"));
		
		// copy example app
		String exampleAppPackage = "/src/com/haxademic/app/exampleapp";
		String exampleAppClass = "/ExampleApp.java";
		String destSrcPath = newProjectDir + FileUtil.safePath(exampleAppPackage);
		String srcAppPath = haxPath + FileUtil.safePath(exampleAppPackage + exampleAppClass);
		String destAppPath = destSrcPath + FileUtil.safePath(exampleAppClass);
		boolean srcDirCreated = FileUtil.createDir(destSrcPath);
		log.update("==============================");
		log.update("src dir created:" + srcDirCreated);
		log.update("-> " + destSrcPath);
		try {
			FileUtil.copyFile(srcAppPath, destAppPath);
			log.update("==============================");
			log.update("app class created:");
			log.update("-> " + destAppPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// copy haxademic assets
		copyDir("/data/haxademic");
		copyDir("/www");
		copyDir("/scripts");

		// copy select libs
		copyDir("/lib/beads");
		copyDir("/lib/dmxP512");
		copyDir("/lib/Ess");
		copyDir("/lib/haxademic");
		copyDir("/lib/java_websocket");
		copyDir("/lib/jetty");
		copyDir("/lib/minim");
		copyDir("/lib/processing-3");
		copyDir("/lib/themidibus");
		copyDir("/lib/UMovieMaker");
		
		// copy project files
		copyFile(".gitignore");
		try {
			String classPathMinimal = FileUtil.safePath(haxPath + "/.classpath-example");
			String classPathDest = FileUtil.safePath(newProjectDir + "/.classpath");
			FileUtil.copyFile(classPathMinimal, classPathDest);
			log.update("==============================");
			log.update("Minimal .classpath copied:");
			log.update("-> "+classPathDest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// copy & update .project file
		copyFile(".project");
		try {
			FileUtil.replaceStringInFile(newProjectDir + "/.project", "haxademic", newProjectName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// we did it!
		log.update("==============================");
		log.update("DONE!");
		log.update("==============================");
	}
	
	protected void copyFile(String file) {
		String origHaxPath = FileUtil.safePath(haxPath + "/" + file);
		String destPath = FileUtil.safePath(newProjectDir + "/" + file);
		try {
			FileUtil.copyFile(origHaxPath, destPath);
			log.update("==============================");
			log.update("Project file copied:");
			log.update("-> "+origHaxPath);
			log.update("-> "+destPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void copyDir(String dir) {
		try {
			String origHaxPath = haxPath + FileUtil.safePath(dir);
			String destPath = newProjectDir + FileUtil.safePath(dir);
			FileUtil.createDir(destPath);
			FileUtil.copyFolder(origHaxPath, destPath);
			log.update("==============================");
			log.update("Haxademic "+ dir +" copied to project:");
			log.update("-> "+origHaxPath);
			log.update("-> "+destPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			new Thread(new Runnable() { public void run() {
				generateProject();
			}}).start();
		}
	}
	
}