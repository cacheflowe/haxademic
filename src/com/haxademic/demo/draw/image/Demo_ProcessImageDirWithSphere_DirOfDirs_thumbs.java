package com.haxademic.demo.draw.image;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class Demo_ProcessImageDirWithSphere_DirOfDirs_thumbs
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String imagesPath;
	protected String imagesOutputPath;
	protected ArrayList<String> images = new ArrayList<String>();
	protected int imageProcessIndex = 0;
	protected PGraphics drawBuffer;
	protected PShape shapeIcos;
	
	protected void firstFrame() {
		// build draw buffer
		drawBuffer = PG.newPG(512, 512);
		// build sphere to texture for thumb
		shapeIcos = Icosahedron.createIcosahedron(p.g, 4, drawBuffer);
		PShapeUtil.scaleShapeToExtent(shapeIcos, drawBuffer.height/2f);

		// set images path
		imagesPath = "D:\\workspace\\dbg-sci-py\\_assets\\omniglobe\\assets\\content\\Content_Equirect\\__convert";
		imagesPath = "D:\\workspace\\dbg-sci-py\\_assets\\omniglobe\\assets\\content\\Content_Equirect\\WaterVideos\\thumbs";
		
		// create output directory
		imagesOutputPath = imagesPath + "_Equirect";
		FileUtil.createDir(imagesOutputPath);

		// search for images
		String[] directories = FileUtil.getDirsInDir(imagesPath);
		for (int i = 0; i < directories.length; i++) {
			P.out("####################### ", directories[i]);
			// if last path component starts with an underscore, 
			// it's a video image sequence, and we should only process the first image
			boolean isVideoFramesDir = true;
			if(FileUtil.getPathComponent(directories[i], 1).indexOf('_') == 0) isVideoFramesDir = false;

			// gather images from directory
			ArrayList<String> curImages = FileUtil.getFilesInDirOfTypes(directories[i], "jpg,png");
			for (int j = 0; j < curImages.size(); j++) {
				if(j == 0 || isVideoFramesDir == false) {	// store all images that need a thumbnail
					P.out(curImages.get(j));
					images.add(curImages.get(j));
				}
			}
		}
		P.out("Found " + images.size() + " images");
	}

	protected void drawApp() {
		p.background(0);
		processImages();
	}
	
	protected void processImages() {
		// every frame, load & process the next one
		if(imageProcessIndex < images.size()) {
			// load image
			String imgPath = images.get(imageProcessIndex);
			PImage img = p.loadImage(imgPath);
			
			// draw sphere to buffer
			drawBuffer.beginDraw();
			drawBuffer.background(0);
			drawBuffer.ortho();
			PG.setCenterScreen(drawBuffer);
			shapeIcos.setTexture(img);
			drawBuffer.shape(shapeIcos);
			drawBuffer.endDraw();
			
			// save image to output path
			String imgDir = FileUtil.getPathComponent(imgPath, 2);
			boolean isVideoFramesDir = imgDir.indexOf('_') != 0;
			
			String imgSavePath = imgPath.replace(".jpg", "_thumb.jpg");
			imgSavePath = imgSavePath.replace(".png", "_thumb.png");
			
			// either save up a directory, or next to original
			if(isVideoFramesDir) {
				String[] basePath = imgPath.split(imgDir);
				imgSavePath = basePath[0] + imgDir + ".jpg";
			}
			
			drawBuffer.save(imgSavePath);
			P.out("SAVE: "+ imgPath);
			P.out("SAVE TO: "+ imgSavePath);

			// draw to screen & show status
			p.image(drawBuffer, 0, 0);
			drawTextStatus("Processing: " + (imageProcessIndex + 1) + " / " + images.size());
			imageProcessIndex++;
		} else {
			drawTextStatus("Done");
		}
	}
	
	protected void drawTextStatus(String str) {
		p.fill(0, 180);
		p.rect(0, 0, p.width, 80);
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 42);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(str, 20, 20);
	}
	
}