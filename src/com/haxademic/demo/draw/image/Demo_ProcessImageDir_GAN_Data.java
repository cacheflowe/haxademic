package com.haxademic.demo.draw.image;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.MediaMetaData;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ProcessImageDir_GAN_Data
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// paths
	protected String imagesPath = "D:\\workspace\\haxademic.js\\server\\downloads-nike";
	protected String outputPathSuffix = "\\_export"; 
	protected String imagesOutputPath = imagesPath + outputPathSuffix; 
	
	// file list
//	protected ArrayList<String> images = new ArrayList<String>();
	protected ArrayList<LoadedImage> images = new ArrayList<LoadedImage>();
	
	// current image
	protected int imageIndex = 0;
	protected PGraphics drawBuffer;
	
	// panning controls
	protected float imageScale = 1;
	protected float offsetX = 0;
	protected float offsetY = 0;
	protected float rotation = 0;

	protected boolean showGrid = false;
	protected boolean queueExport = false;
	
	// output
	protected int outputSize = 1024;
	protected int numExported = 0;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}
	
	protected void firstFrame() {
		// build draw buffer
		drawBuffer = PG.newPG(outputSize, outputSize);
		DebugView.setTexture("drawBuffer", drawBuffer);
		// create output directory
		FileUtil.createDir(imagesOutputPath);
		loadImages();
		setIndex(0);
	}
	
	protected void loadImages() {
		// search for images in directory
		P.out("####################### ", imagesPath);
		ArrayList<String> curImages = FileUtil.getFilesInDirOfTypes(imagesPath, "jpg,jpeg,png");
		for (int j = 0; j < curImages.size(); j++) {
			String imagePath = curImages.get(j);
			P.out(imagePath);
			LoadedImage loadedImage = new LoadedImage(imagePath);
			images.add(loadedImage);
			if(loadedImage.exported()) numExported++;
		}
		P.out("Found " + images.size() + " images");
	}

	protected void setIndex(int index) {
		imageIndex = index;
		curImage().setActive();
		resetControls();
		showGrid = true;
		customFit();
	}
	
	protected void resetControls() {
		imageScale = MathUtil.scaleToTarget(curImage().image().height, drawBuffer.height);
		offsetX = 0;
		offsetY = 0;
		rotation = 0;
	}
	
	protected LoadedImage curImage() {
		return images.get(imageIndex);
	}
	
	protected void updateNumExported() {
		numExported = 0;
		for (int i = 0; i < images.size(); i++) {
			if(images.get(i).exported()) numExported++;
		}
	}
	
	protected void runKeyCommands() {
		float keyScale = (KeyboardState.keyOn(16)) ? 10 : 1;
		if(KeyboardState.keyTriggered('1')) {
			int newIndex = (imageIndex - P.round(1 * keyScale)) % images.size();
			if(newIndex < 0) newIndex = images.size() - 1;
			setIndex(newIndex);
		}
		if(KeyboardState.keyTriggered('2')) {
			int newIndex = (imageIndex + P.round(1 * keyScale)) % images.size();
			setIndex(newIndex);
		}
		if(KeyboardState.keyTriggered('3')) resetControls();
		if(KeyboardState.keyTriggered(147)) { curImage().delete(); updateNumExported(); }
		if(KeyboardState.keyOn(' ')) { showGrid = false; queueExport = true; updateNumExported(); }
		
		if(KeyboardState.keyOn('a') || KeyboardState.keyOn(37)) offsetX += 1f * keyScale;
		if(KeyboardState.keyOn('d') || KeyboardState.keyOn(39)) offsetX -= 1f * keyScale;
		if(KeyboardState.keyOn('w') || KeyboardState.keyOn(38)) offsetY += 1f * keyScale;
		if(KeyboardState.keyOn('s') || KeyboardState.keyOn(40)) offsetY -= 1f * keyScale;
		if(KeyboardState.keyOn('q')) rotation -= 0.0025f * keyScale;
		if(KeyboardState.keyOn('e')) rotation += 0.0025f * keyScale;
		if(KeyboardState.keyOn('z')) imageScale -= 0.0025f * keyScale;
		if(KeyboardState.keyOn('c')) imageScale += 0.0025f * keyScale;
		if(KeyboardState.keyTriggered('g')) showGrid = !showGrid;
		
		customKeyCommands();
	}
	
	protected void drawApp() {
		p.background(40);
		runKeyCommands();
		
		drawBuffer.beginDraw();
		drawBuffer.background(0, 255, 0);
		// draw image
		drawBuffer.push();
		PG.setDrawCenter(drawBuffer);
		PG.setCenterScreen(drawBuffer);
		PImage img = images.get(imageIndex).image();
		drawBuffer.translate(offsetX, offsetY);
		drawBuffer.rotate(rotation);
		drawBuffer.image(img, 0, 0, img.width * imageScale, img.height * imageScale);
		drawBuffer.pop();
		// draw grid
//		if(KeyboardState.keyOn('g')) {
		if(showGrid) {
			PG.setDrawCorner(drawBuffer);
			PG.drawGrid(drawBuffer, 0x00000000, 0x66ffffff, 20, 20, 1, false);
			PG.drawGridCircles(drawBuffer, 0x00000000, 0x66ffffff, 20, 1, false);
			customGrid();
		}
		// end
		drawBuffer.endDraw();
		
		// draw buffer to screen
		ImageUtil.cropFillCopyImage(drawBuffer, p.g, false);
		
		// draw status text
		drawTextStatus(
			"Current  = " + (imageIndex + 1) + " / " + images.size() + FileUtil.NEWLINE + 
			"Exported = " + numExported + " / " + images.size() + FileUtil.NEWLINE +
			"--------------------------" + FileUtil.NEWLINE +
			curImage().info()
		);

		// exported indicator
		p.push();
		p.fill(curImage().exported() ? p.color(0,255,0) : p.color(255,0,0));
		p.ellipse(300, 50, 50, 50);
		p.pop();
		
		// do export if needed
		if(queueExport) {
			curImage().export();
			queueExport = false;
		}
	}
	
	protected void drawTextStatus(String str) {
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 24);
		FontCacher.setFontOnContext(p.g, font, p.color(127), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(str, 20, 20);
	}
	
	
	///////////////////////////////////
	// Custom per dataset prep
	///////////////////////////////////
	
	protected void customFit() {
		// fit to width rather than height
		imageScale = MathUtil.scaleToTarget(curImage().image().width, drawBuffer.width);
		// move up to anchor at bottom
		float imgH = drawBuffer.height * imageScale;
		offsetY = (imgH - drawBuffer.height) / 2;
	}
	
	protected void customGrid() {
		// red line under sneaker
		drawBuffer.push();
		drawBuffer.fill(255,0,0);
		drawBuffer.rect(0, 874, drawBuffer.width, 4);
		drawBuffer.pop();
	}
	
	protected void customKeyCommands() {
		if(KeyboardState.keyTriggered('3')) {
			imageScale = MathUtil.scaleToTarget(curImage().image().width, drawBuffer.width);
			float imgH = drawBuffer.height * imageScale;
			offsetY = -50;// (imgH - drawBuffer.height) * -0.005f;
		}	
	}
	
	///////////////////////////////////
	// Image class
	// Helps us keep track of our exports
	///////////////////////////////////
	
	public class LoadedImage {
		
		protected String path;
		protected String pathExport;
		protected String fileName;
		protected PImage image;
		protected ArrayList<String> metadata;
		protected String info = "";
		protected boolean exported = false;
		
		public LoadedImage(String path) {
			this.path = path;
			fileName = FileUtil.fileNameFromPath(path); 
//			this.pathExport = imagesOutputPath + FileUtil.SEPARATOR + fileName.replace("png", "jpg").replace("jpeg", "jpg");
			this.pathExport = imagesOutputPath + FileUtil.SEPARATOR + fileName.replace("jpg", "png").replace("jpeg", "png");
			if(FileUtil.fileOrPathExists(pathExport)) exported = true;
			
		}
		
		public void setActive() {
			if(image == null) {
				image = P.p.loadImage(path);
				
				metadata = MediaMetaData.getMetaDataForMedia(path, true);
				P.out("== ", path);
				P.out("-- ", pathExport);
				updateInfo();
			}
		}
		
		protected void updateInfo() {
			info = fileName + FileUtil.NEWLINE;
			info += "Exported: " + exported + FileUtil.NEWLINE;
			for (int i = 0; i < metadata.size(); i++) {
				info += metadata.get(i) + FileUtil.NEWLINE;
			}
		}
		
		public PImage image() {
			return image;
		}
		
		public String info() {
			return info;
		}
		
		public boolean exported() {
			return exported;
		}
		
		public void export() {
			drawBuffer.save(this.pathExport);
			exported = true;
			updateInfo();
		}
		
		public void delete() {
			FileUtil.deleteFile(this.pathExport);
			exported = false;
			updateInfo();
		}
	}
	
}