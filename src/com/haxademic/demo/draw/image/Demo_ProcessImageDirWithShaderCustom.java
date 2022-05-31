package com.haxademic.demo.draw.image;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.image.ImageCacher;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.draw.textures.pgraphics.shared.PGPool;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ProcessImageDirWithShaderCustom
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String imagesPath;
	protected String imagesOutputPath;
	protected ArrayList<String> images;
	protected int imageProcessIndex = 0;
	
	protected PShaderHotSwap shader;
	
	protected void firstFrame() {
		// set images path
		imagesPath = "D:\\workspace\\dbg-sci-py\\_assets\\omniglobe\\assets\\content\\Content\\_Page Globes";
		
		// create output directory
		imagesOutputPath = imagesPath + "_processed";
		FileUtil.createDir(imagesOutputPath);
		
		// load images from directory
		images = FileUtil.getFilesInDirOfTypes(imagesPath, "jpg,png");
		for (int i = 0; i < images.size(); i++) {
			P.out(images.get(i));
		}
		
		// custom image processing
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/polar-split-disk-to-equirectangular.glsl"));
	}

	protected void drawApp() {
		p.background(0);
//		previewImage();
		processImages();
	}
	
	protected void previewImage() {
		// load image
//		PImage img = ImageCacher.get(images.get(imageProcessIndex), false);
		PImage img = ImageCacher.get(images.get(7), false);
		
		// draw image w/filtering
		PGraphics drawBuffer = PGPool.getPG(img.width, img.height);
		drawBuffer.beginDraw();
		drawBuffer.clear();
		ImageUtil.copyImage(img, drawBuffer);
		shader.update();
		drawBuffer.filter(shader.shader());
		drawBuffer.endDraw();
		
		// match to this example
		p.image(ImageCacher.get(images.get(6), false), 0, 0, drawBuffer.width, drawBuffer.height);
		
		// draw to screen
		PG.setPImageAlpha(p, 0.95f);
		p.image(drawBuffer, 0, 0);
		PG.resetPImageAlpha(p);
	}
	
	protected void processImages() {
		// every frame, load & process the next one
		if(imageProcessIndex < images.size()) {
			// load image
			String imgPath = images.get(imageProcessIndex);
			PImage img = p.loadImage(imgPath);
			
			// draw image to buffer w/processing
			PGraphics drawBuffer = PGPool.getPG(img.width, img.height);
			drawBuffer.beginDraw();
			drawBuffer.clear();
			ImageUtil.copyImage(img, drawBuffer);
			shader.update();
			drawBuffer.filter(shader.shader());
			drawBuffer.endDraw();
			
			// save image to output path
			drawBuffer.save(imgPath.replace(imagesPath, imagesOutputPath));
			

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