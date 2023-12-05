package com.haxademic.demo.draw.image;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.mapping.SavedRectangle;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.MediaMetaData;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PFont;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class Demo_ProcessImageDir_COCO_Annotate
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// create stills from video
	/*
		var min = 6;
		var sec = 24;
		var totalSec = min * 60 + sec;
		var cmdStr = "";
		for(var i=0; i < totalSec; i++) {
			cmdStr += `videoToImagePoster.sh IMG_7915.MOV ${i}\n`
		}
		console.log(cmdStr);
	 */

	// paths
	protected String imagesPath = "D:\\workspace\\rev-hilton-wafflevision\\_assets\\waffles\\on-site-waffles-1";
	protected String outputPath = imagesPath + "\\_dataset_export"; 
	protected String dataPathTraining = outputPath + "\\train"; 
	protected String imagePathTraining = dataPathTraining + "\\images"; 
	protected String dataPathValidation = outputPath + "\\validation"; 
	protected String imagePathValidation = dataPathValidation + "\\images"; 
	protected String jsonFileName = "\\labels.json";
	
	// file list
//	protected ArrayList<String> images = new ArrayList<String>();
	protected ArrayList<LoadedImage> images = new ArrayList<LoadedImage>();
	
	// current image
	protected int imageIndex = 0;
	protected int exportCount = 0;
	protected int START_INDEX = 0;
	
	// panning controls
	protected float imageScale = 1;
	protected float offsetX = 0;
	protected float offsetY = 0;
	protected float rotation = 0;

	protected boolean showInfo = false;
	protected boolean showGrid = false;
	protected boolean queueExport = false;
	
	// output
	protected int numExported = 0;
	
	// custom 
	protected int bgColor = 0xffffffff;
	
	// COCO rectangle UI
	protected SavedRectangle rectangle;
	protected boolean showRectangle = true;

	// COCO data
	protected JSONObject outputDataTrain;
	protected JSONObject outputDataValidate;
	protected String keyCategories = "categories";
	protected String keyImages = "images";
	protected String keyAnnotations = "annotations";
	protected int WAFFLE_CAT_ID = 1;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}
	
	protected void firstFrame() {
		// build draw buffer
		DebugView.setTexture("pg", pg);
		// create output directory
		FileUtil.createDir(outputPath);
		loadImages();
		setIndex(START_INDEX);
		// build rectangle UI
		rectangle = new SavedRectangle("coco", true);
		initData();
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
		imageScale = MathUtil.scaleToTarget(curImage().image().width, pg.width);
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
		float keyScale = (KeyboardState.keyOn(16)) ? 1 : 10;
		if(KeyboardState.keyTriggered('1')) {
			int newIndex = (imageIndex - P.round(1)) % images.size(); //  * keyScale
			if(newIndex < 0) newIndex = images.size() - 1;
			setIndex(newIndex);
		}
		if(KeyboardState.keyTriggered('2')) {
			int newIndex = (imageIndex + P.round(1)) % images.size(); //  * keyScale
			setIndex(newIndex);
		}
		if(KeyboardState.keyTriggered('r')) resetControls();
		// if(KeyboardState.keyTriggered(147)) { curImage().delete(); updateNumExported(); }
		if(KeyboardState.keyTriggered(' ')) { P.out("SPACE"); showGrid = false; queueExport = true; updateNumExported(); }
		
		if(KeyboardState.keyOn('a') || KeyboardState.keyOn(37)) offsetX += 1f * keyScale;
		if(KeyboardState.keyOn('d') || KeyboardState.keyOn(39)) offsetX -= 1f * keyScale;
		if(KeyboardState.keyOn('w') || KeyboardState.keyOn(38)) offsetY += 1f * keyScale;
		if(KeyboardState.keyOn('s') || KeyboardState.keyOn(40)) offsetY -= 1f * keyScale;
		if(KeyboardState.keyOn('q')) rotation -= 0.0025f * keyScale;
		if(KeyboardState.keyOn('e')) rotation += 0.0025f * keyScale;
		if(KeyboardState.keyOn('z')) imageScale -= 0.0025f * (keyScale * 0.3f);
		if(KeyboardState.keyOn('c')) imageScale += 0.0025f * (keyScale * 0.3f);
		if(KeyboardState.keyTriggered('i')) showInfo = !showInfo;
		if(KeyboardState.keyTriggered('g')) showGrid = !showGrid;
		
		// set help text
		DebugView.setHelpLine("[1]", "PREV Img");
		DebugView.setHelpLine("[2]", "NEXT Img");
		DebugView.setHelpLine("[R]", "RESET Img");
		DebugView.setHelpLine("[SPACE]", "SAVE Img");
		DebugView.setHelpLine("[DEL]", "Delete Export");
		DebugView.setHelpLine("[A,W,D,S]", "MOVE Img");
		DebugView.setHelpLine("[Q,E]", "ROTATE Img");
		DebugView.setHelpLine("[Z,C]", "SCALE Img");
		DebugView.setHelpLine("[I]", "Info toggle");
		DebugView.setHelpLine("[G]", "Grid toggle");
		
		// customKeyCommands();
	}
	
	protected void drawApp() {
		p.background(40);
		runKeyCommands();
		
		pg.beginDraw();
		pg.background(bgColor);
		
		// draw image
		pg.push();
		PG.setDrawCenter(pg);
		PG.setCenterScreen(pg);
		PImage img = images.get(imageIndex).image();
		pg.translate(offsetX, offsetY);
		pg.rotate(rotation);
		pg.image(img, 0, 0, img.width * imageScale, img.height * imageScale);
		pg.pop();
		// draw grid
//		if(KeyboardState.keyOn('g')) {
		if(showGrid) {
			PG.setDrawCorner(pg);
			PG.drawGrid(pg, 0x00000000, 0x66ffffff, 20, 20, 1, false);
			PG.drawGridCircles(pg, 0x00000000, 0x66ffffff, 20, 1, false);
		}
		// end
		pg.endDraw();
		
		// draw buffer to screen
		ImageUtil.cropFillCopyImage(pg, p.g, false);

		// draw rectangle
		p.stroke(255, 0, 0);
		p.strokeWeight(2);
		p.noFill();
		p.rect(rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
		
		// draw status text
		if(showInfo) {
			drawTextStatus(
				"Current  = " + (imageIndex + 1) + " / " + images.size() + FileUtil.NEWLINE + 
				"Exported = " + numExported + " / " + images.size() + FileUtil.NEWLINE +
				"exportCount = " + exportCount + " / " + images.size() + FileUtil.NEWLINE +
				"--------------------------" + FileUtil.NEWLINE +
				curImage().info()
			);
		}

		// exported indicator
		p.push();
		p.fill(curImage().exported() ? p.color(0,255,0) : p.color(255,0,0));
		p.ellipse(p.width - 80, 30, 50, 50);
		p.pop();
		
		// do export if needed
		if(queueExport) {
			// export image
			curImage().export();
			// flip flag back
			exportCount++;
			queueExport = false;
		}
	}
	
	protected void drawTextStatus(String str) {
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 18);
		FontCacher.setFontOnContext(p.g, font, p.color(0), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(str, 20, 18);
		p.text(str, 20, 22);
		p.fill(255);
		p.text(str, 20, 20);
	}
	
	
	///////////////////////////////////
	// Custom per dataset prep
	///////////////////////////////////
	
	protected void customFit() {
		// fit to width rather than height
		float scaleW = MathUtil.scaleToTarget(curImage().image().width, pg.width);
		float scaleH = MathUtil.scaleToTarget(curImage().image().height, pg.height);
		imageScale = P.max(scaleW, scaleH);
	}
	
	protected void customKeyCommands() {
		if(KeyboardState.keyTriggered('3')) {
			imageScale = MathUtil.scaleToTarget(curImage().image().width, pg.width);
			float imgH = pg.height * imageScale;
			offsetY = -50;// (imgH - pg.height) * -0.005f;
		}	
	}
	
	///////////////////////////////////
	// Image class
	// Helps us keep track of our exports
	///////////////////////////////////
	
	public class LoadedImage {
		
		protected String path;
		protected String fileName;
		protected PImage image;
		protected ArrayList<String> metadata;
		protected String info = "";
		protected boolean exported = false;
		
		public LoadedImage(String path) {
			this.path = path;
			fileName = FileUtil.fileNameFromPath(path); 
		}
		
		public void setActive() {
			if(image == null) {
				image = P.p.loadImage(path);
				
				metadata = MediaMetaData.getMetaDataForMedia(path, true);
				P.out("== ", path);
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
			// switch output directories so training data is about 8x larger than validation data
			String outputImagePath = (exportCount % 8 == 0) ? imagePathValidation : imagePathTraining;
			String outputDataPath = (exportCount % 8 == 0) ? dataPathValidation : dataPathTraining;
			JSONObject outputData = (exportCount % 8 == 0) ? outputDataValidate : outputDataTrain;
			if(!FileUtil.fileOrPathExists(outputDataPath)) FileUtil.createDir(outputDataPath);
			if(!FileUtil.fileOrPathExists(outputImagePath)) FileUtil.createDir(outputImagePath);

			// create temporary filename with timestamp so we can export an image multiple times
			// also convert/save as .jpg
			String curImgName = fileName
			.replaceAll("png", "jpg").replaceAll("jpeg", "jpg")
			.replaceAll(" ", "-")
			.replaceAll(".jpg", "-" + SystemUtil.getTimestampFine() + ".jpg");

			// save json for COCO dataset
			addImageToJsonData(outputData, curImgName);
			FileUtil.writeTextToFile(outputDataPath + jsonFileName, outputData.toString());

			// save file
			pg.save(outputImagePath + FileUtil.SEPARATOR + curImgName);
			exported = true;

			// save image status
			updateInfo();
		}
	}


	///////////////////////////////////
	// Output data & helpers
	///////////////////////////////////
	
	protected void initData() {
		// TODO: load existing data if we have it??
		outputDataTrain = emptyTrainingData();
		outputDataValidate = emptyTrainingData();

		// build arrays
		// outputData = new JSONObject();
		// outputData.setJSONArray(keyCategories, new JSONArray());
		// outputData.setJSONArray(keyAnnotations, new JSONArray());
		// outputData.setJSONArray(keyImages, new JSONArray());

		// populate categories
		// outputData.getJSONArray(keyCategories).append(new JSONObject().setInt("id", 0).setString("name", "background"));
		// outputData.getJSONArray(keyCategories).append(new JSONObject().setInt("id", WAFFLE_CAT_ID).setString("name", "waffles"));
		P.out(outputDataTrain.toString());
		P.out(outputDataValidate.toString());
	}

	protected JSONObject emptyTrainingData() {
		return JsonUtil.jsonFromString("""
			{
				"annotations": [],
				"images": [],
				"categories": [
					{
						"id": 0,
						"name": "background"
					},
					{
						"id": 1,
						"name": "waffles"
					}
				]
			}
		""");
	}

	protected void addImageToJsonData(JSONObject outputData, String imageFileName) {
		// add image reference to data
		JSONArray imagesArray = outputData.getJSONArray(keyImages);
		imagesArray.append(new JSONObject().setInt("id", exportCount).setString("file_name", imageFileName));
		
		// add annotation
		JSONArray annotationsArray = outputData.getJSONArray(keyAnnotations);
		JSONArray rectangleJson = new JSONArray();
		rectangleJson.append(rectangle.x()).append(rectangle.y()).append(rectangle.width()).append(rectangle.height());
		annotationsArray.append(new JSONObject().setInt("image_id", exportCount).setJSONArray("bbox", rectangleJson).setInt("category_id", WAFFLE_CAT_ID));		

		// print current json export
		P.out(outputData.toString());
	}
	
}