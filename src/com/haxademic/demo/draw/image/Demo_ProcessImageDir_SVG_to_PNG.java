package com.haxademic.demo.draw.image;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.draw.textures.pgraphics.shared.PGPool;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class Demo_ProcessImageDir_SVG_to_PNG
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String imagesPath;
	protected String imagesOutputPath;
	protected ArrayList<String> images;
	protected int imageProcessIndex = 0;
	
	protected void firstFrame() {
		// set images path
		imagesPath = "D:\\workspace\\kittredge-projection\\_assets\\holiday\\holiday-icons\\Holidays_icons_by_Icons8_SVG\\";
		
		// create output directory
		imagesOutputPath = imagesPath + "_png\\";
		FileUtil.createDir(imagesOutputPath);
		
		// load images from directory
		images = FileUtil.getFilesInDirOfTypes(imagesPath, "svg");
		for (int i = 0; i < images.size(); i++) {
			P.out(images.get(i));
		}
	}

	protected void drawApp() {
		p.background(0);
		
		// every frame, load & process the next one
		if(imageProcessIndex < images.size()) {
			// load image
			String imgPath = images.get(imageProcessIndex);
			PShape svg = p.loadShape(imgPath);
			PImage img = ImageUtil.shapeToGraphics(svg, 1);

			// draw svg into new PG
			float pgScale = 15;
			float svgScale = 9;
			PGraphics pg = PG.newPG(P.ceil((float) svg.width * pgScale), P.ceil((float) svg.height * pgScale));
			pg.beginDraw();
			pg.shape(svg, 20, 20, svg.width * svgScale, svg.height * svgScale);
			pg.endDraw();

			// crop empty space
			PImage imgCropped = ImageUtil.newImage(128, 128);
			int[] padding = new int[] { 2, 2, 2, 2 };
			int[] cropIn = new int[] { 0, 0, 0, 0 };
			ImageUtil.imageCroppedEmptySpace(pg, imgCropped, ImageUtil.EMPTY_INT, false, padding, cropIn, p.color(0, 0, 0, 0));

			// paste int standard size output container
			PGraphics newPg = PGPool.getPG(512, 512);
			newPg.beginDraw();
			newPg.clear();
			PG.setDrawCenter(newPg);
			PG.setCenterScreen(newPg);
			newPg.image(imgCropped, 0, 0);
			newPg.endDraw();

			// save image to output path
			newPg.save(imgPath.replace(imagesPath, imagesOutputPath).replace("svg", "png"));

			// draw to screen & show status
			p.image(newPg, 0, 0);
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