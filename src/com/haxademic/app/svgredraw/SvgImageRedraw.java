package com.haxademic.app.svgredraw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class SvgImageRedraw 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage img;
	
	// Add collection of SVG libraries - probably create a new object type
	// Each library has thei own `prevRows` tracking for non-repetition
	// - Fill up each `prevRows` as we draw icons, no matter which one is being drawn into currently
	// Update `debugDrawSvgs()` to show multiple collections
	// Create image map, and lerp randomness between gradient colors
	// At the beginning of `renderPdf()`, choose which collection to pull from, based on the image map gradient
	
	String svgDirectory = "svg/bw5/";
	ArrayList<SvgRanked> _svgs;
	SvgRanked _blackDot;
	SvgRanked _whiteDot;
	
	PGraphics analyzeCanvas;
	float analyzeSize = 30;
	float shapeSizeDebug = 30;

	float iconInches = 0.5f;
	float imagePrintWidth = 22.5f;
	float shapeSize;
	float imgScale = 1f;
	float shapeDrawScale = 1f;

	int whitenessMode = 1;
	boolean mapsTo255 = true;
		
	String outputFile = "la";
	float numRowSplits = 3;
	boolean splitFiles = false;

	float colorRangeLow = 54;
	float colorRangeHigh = 228;
	float blackThresh = 0.01f;

	int noRepeatVariance = 25;
	int numColsToCheckBack = 5;
	int numRowsToCheckBack = 5;
	int bailedOnUniqueAttempts = 0;
	ArrayList<ArrayList<Integer>> prevRows;
	ArrayList<Integer> currRow;

	boolean kobeTest = false;
	boolean friendTest = false;
	boolean rendering = true;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "1000" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "1000" );
	}

	public void setup() {
		super.setup();	
		p.smooth();
		
		if(kobeTest == true) {
			img = p.loadImage(FileUtil.getFile("images/_kobeface_01_small.jpg"));
			svgDirectory = "svg/bw9_all_icons/";
//			directory = "svg/bw10_test_center/";
			iconInches = 0.5f;
			imgScale = 0.26f;
			outputFile = "kobe";
			shapeDrawScale = 0.9f;
		} else if(friendTest == true) {
			img = p.loadImage(FileUtil.getFile("images/_adriana.png"));
			outputFile = "adriana";
			svgDirectory = "svg/bw2/";
			iconInches = 1.5f;
			shapeDrawScale = 0.9f;
		} else {
			img = p.loadImage(FileUtil.getFile("images/_billboard-contrast.jpg"));

			shapeDrawScale = 0.75f;
			iconInches = 1.675f;
			imagePrintWidth = 39f;

//			String iconsLevel = "1"; 
			String athlete = "billboard"; 
			// directory = "svg/bw12/level"+iconsLevel+"/";
			svgDirectory = "svg/bw13/"; // testing wtf black holes
//			outputFile = athlete+"_level"+iconsLevel+"_"+iconInches+"inch";
			outputFile = "2015-08-24_"+athlete+"_"+iconInches+"inch";

//			splitFiles = true;
//			numRowSplits = 2;
			
			// transform to blurred img
			PGraphics pg = ImageUtil.imageToGraphics(img);
			BlurHFilter.instance(p).applyTo(pg);
			BlurHFilter.instance(p).setBlur(1f / (pg.width*0.25f));
			BlurVFilter.instance(p).applyTo(pg);
			BlurVFilter.instance(p).setBlur(1f / (pg.width*0.25f));
			img = pg.get();
		}
		
		shapeSize = (float) img.width / ((imagePrintWidth * 12f) / iconInches);	// for 18.5 feet wide
		shapeSize = shapeSize * (1f / imgScale);
		P.println("shapeSize: ",shapeSize);
		
		prevRows = new ArrayList<ArrayList<Integer>>();
		currRow = new ArrayList<Integer>();
		
		analyzeCanvas = p.createGraphics((int)analyzeSize, (int)analyzeSize);

		// load svgs
		ArrayList<String> files = FileUtil.getFilesInDirOfType(FileUtil.getHaxademicDataPath() + svgDirectory, "svg");
		P.println("Loading and analyzing "+files.size()+" svgs");
		_svgs = new ArrayList<SvgRanked>();
		for (String file : files) {
			PShape shape = p.loadShape( FileUtil.getHaxademicDataPath() + svgDirectory + file );
//			PShapeUtil.scaleSvgToExtent(shape, shapeSize);
//			PShapeUtil.centerSvg(shape);
			SvgRanked rankedSvg = new SvgRanked(shape, 1.0f, file);
			if(file.indexOf("black-dot.svg") != -1) {
				_blackDot = rankedSvg;
			} else if(file.indexOf("_white-dot.svg") != -1) {
				_whiteDot = rankedSvg;
			} else {
				_svgs.add(rankedSvg);
			}

			if(file.indexOf("scaled-100") != -1) {
//				// copy 100 to 60 for more options
//				shape = p.loadShape( FileUtil.getHaxademicDataPath() + directory + file );
//				rankedSvg = new SvgRanked(shape, 0.6f);
//				if(file.indexOf("white") != -1) rankedSvg.isWhite = true;
//				_svgs.add(rankedSvg);
				
//				// copy 100 to 70 for more options
//				shape = p.loadShape( FileUtil.getHaxademicDataPath() + directory + file );
//				rankedSvg = new SvgRanked(shape, 0.7f);
//				if(file.indexOf("white") != -1) rankedSvg.isWhite = true;
//				_svgs.add(rankedSvg);
//				
				// copy 100 to 80 for more options
				shape = p.loadShape( FileUtil.getHaxademicDataPath() + svgDirectory + file );
				rankedSvg = new SvgRanked(shape, 0.8f, file);
				if(file.toLowerCase().indexOf("white") != -1) rankedSvg.isWhite = true;
				_svgs.add(rankedSvg);
//				
//				// copy 100 to 90 for more options
//				shape = p.loadShape( FileUtil.getHaxademicDataPath() + directory + file );
//				rankedSvg = new SvgRanked(shape, 0.9f);
//				if(file.indexOf("white") != -1) rankedSvg.isWhite = true;
////				rankedSvg.shape.translate(shapeSizeDebug*(1f-rankedSvg.scale), shapeSizeDebug*(1f-rankedSvg.scale));
//				_svgs.add(rankedSvg);
			}
		}
		
		// sort icons for easy pixel whiteness comparison/redrawing 
		Collections.sort(_svgs, new CustomComparator());
		
		// debug print whiteness analysis
		for (int i = 0; i < _svgs.size() - 1; i++) {
			P.println(i, "whiteness:", _svgs.get(i).whiteness, " | whitenessOrig:", _svgs.get(i).whitenessOrig);
		}
	}
	
	public class CustomComparator implements Comparator<SvgRanked> {
	    @Override
	    public int compare(SvgRanked o1, SvgRanked o2) {
	        return Math.round(o1.whiteness - o2.whiteness);
	    }
	}

	public void drawApp() {
		background(0, 70, 0);
		
		// debug draw image
		PG.setDrawCorner(p);
		p.image(img, 0, p.height - img.height);
		
		// debug draw shapes
		debugDrawSvgs();
		if(rendering == true && frameCount == 5) {
			renderPdf();
			rendering = false;
		}
	}
		
	protected void debugDrawSvgs() {
		if(p.frameCount == 3) p.beginRecord(P.PDF, FileUtil.getHaxademicOutputPath() + "_testIcons.pdf");
		PG.setDrawCenter(p);
		int x = 0;
		int y = 0;
		for (SvgRanked shape : _svgs) {
			shape.draw(p, x + shapeSizeDebug/2f, y + shapeSizeDebug/2f, shapeSizeDebug);
			x += shapeSizeDebug;
			if(x > p.width - shapeSizeDebug) {
				y += shapeSizeDebug;
				x = 0;
			}
		}	
		if(p.frameCount == 3) p.endRecord();
	}
		
	protected void renderPdf() {
		PG.setDrawCenter(p);
		int halfShapeSize = (int)(shapeSize/2f);
		int shapesDrawn = 0;
		int colsDrawn = 0;
		int rowsDrawn = 0;
		int rowSplit = P.ceil(img.height / shapeSize / numRowSplits);
		int fileNum = 0;
		float shapeDrawSize = shapeSize * shapeDrawScale;
		
		String splitFileAdd = (splitFiles == true) ? "_row-"+fileNum : "";
		p.beginRecord(P.PDF, FileUtil.getHaxademicOutputPath() + "_"+outputFile+"_shapeSize-"+(int)shapeSize+"_whiteMode-"+whitenessMode+"_maps255-"+mapsTo255+"_variance-"+noRepeatVariance+splitFileAdd+".pdf");
		for (float y = halfShapeSize; y <= img.height - halfShapeSize; y += shapeSize) {
			P.println("Processing row ",y);
			if(rowsDrawn > 0) {
				ArrayList<Integer> prevRow = new ArrayList<Integer>();
				prevRow.addAll(currRow);
				prevRows.add(prevRow);
			}
			currRow.clear();
			for (float x = halfShapeSize; x <= img.width - halfShapeSize; x += shapeSize) {
				// get current pixel lightness
				int pixelColor = img.get((int)x, (int)y);
				// if(shapesDrawn < 100) P.println("pixelColor: ",pixelColor);
				
				// handle pure black pixels
				// if(pixelColor == ImageUtil.BLACK_INT) {
				if(ColorUtil.redFromColorInt(pixelColor) < blackThresh && ColorUtil.greenFromColorInt(pixelColor) < blackThresh && ColorUtil.blueFromColorInt(pixelColor) < blackThresh) {
					p.shape(_blackDot.shape, x, y, shapeDrawSize, shapeDrawSize);
					if(ColorUtil.redFromColorInt(pixelColor) != 0 && ColorUtil.greenFromColorInt(pixelColor) != 0 && ColorUtil.blueFromColorInt(pixelColor) != 0)
						P.println("Too Black ",ColorUtil.redFromColorInt(pixelColor), ColorUtil.greenFromColorInt(pixelColor), ColorUtil.blueFromColorInt(pixelColor));
					currRow.add(-1);
				} else {
					// calculate color
					float lightness = 0;
					lightness += ColorUtil.redFromColorInt(pixelColor);
					lightness += ColorUtil.greenFromColorInt(pixelColor);
					lightness += ColorUtil.blueFromColorInt(pixelColor);
					lightness = lightness / 3f;
	
					// find index for closest lightess - try one of 2 modes
					if(whitenessMode == 1) {
						boolean foundShape = false;
						for (int i = 0; i < _svgs.size() - 1; i++) {
							if(_svgs.get(i).whiteness > lightness) {
								int index = indexNonOverlap(i);
								SvgRanked svg = _svgs.get(index);
								svg.draw(p, x, y, shapeDrawSize);
								// p.shape(svg.shape, x, y, shapeDrawSize * svg.scale, shapeDrawSize * svg.scale);
								foundShape = true;
								currRow.add(index);
								break;
							}
						}
						if(foundShape == false) {
							int index = _svgs.size() - 1;
							index = indexNonOverlap(index);
							SvgRanked svg = _svgs.get(index);
							svg.draw(p, x, y, shapeDrawSize);
							// p.shape(svg.shape, x, y, shapeDrawSize * svg.scale, shapeDrawSize * svg.scale);
							currRow.add(index);
						}
						if(foundShape == false) P.println("NO SHAPE DRAWN?!?!?!");
					} else {
						int index = P.round(P.map(lightness, 0, 255, 0, _svgs.size() - 1));
						index = indexNonOverlap(index);
						SvgRanked svg = _svgs.get(index);
						svg.draw(p, x, y, shapeDrawSize);
						// p.shape(svg.shape, x, y, shapeDrawSize * svg.scale, shapeDrawSize * svg.scale);
						currRow.add(index);
					}
				}
				
				// count up
				shapesDrawn++;
				if(y == halfShapeSize) colsDrawn++;
			}
			// count rows for splitting into multiple files
			rowsDrawn++;
			
			// split into multiple files based on row
			if(splitFiles == true && rowsDrawn % rowSplit == 0) {
				fileNum++;
				p.endRecord();
				p.beginRecord(P.PDF, FileUtil.getHaxademicOutputPath() + "_"+outputFile+"_shapeSize-"+(int)shapeSize+"_whiteMode-"+whitenessMode+"_maps255-"+mapsTo255+"_variance-"+noRepeatVariance+"_row-"+fileNum+".pdf");
			}
		}
		
		P.println("Rendering PDF");
		p.endRecord();
		P.println("PDF rendered with "+shapesDrawn+" shapes and "+colsDrawn+" columns. Whoa.");
		P.println("Printed at "+imagePrintWidth+"', this makes each icon "+((imagePrintWidth*imgScale*12f)/(float)colsDrawn)+" inches in size.");
		P.println("Bailed on unique attempts: ",bailedOnUniqueAttempts);

	}
	
	public int indexNonOverlap(int indexAttempt) {
		int attempts = 0;
		float crawlIndex = 0;
		int origAttempt = indexAttempt;
		while(indexIsUnique(indexAttempt) == false) {
			attempts++;
			if(attempts > noRepeatVariance * 2f) {
				bailedOnUniqueAttempts++;
				if(indexAttempt < 0) indexAttempt = 0;
				if(indexAttempt >= _svgs.size()) indexAttempt = _svgs.size() - 1;
				return indexAttempt;
			}
			// indexAttempt = origAttempt + MathUtil.randRange(-noRepeatVariance, noRepeatVariance);
			crawlIndex += 0.5f; // half is down, whole number is up
			if(crawlIndex % 1f > 0) {
				indexAttempt = origAttempt + P.ceil(crawlIndex);
			} else {
				indexAttempt = origAttempt - P.ceil(crawlIndex);
			}
		}
		return indexAttempt;
	}
	
	protected boolean indexIsUnique(int indexAttempt) {		
		// check prev in current row
		if(currRow.size() > 0) {
			for(int colIndex = currRow.size()-1; colIndex > currRow.size() - numColsToCheckBack; colIndex--) {
				if(colIndex >= 0) {
					if(indexAttempt == currRow.get(colIndex)) {
						// P.println("Checking prev in row "+indexAttempt+" == "+currRow.get(colIndex));
						return false;
					}
				}
			}
		}
		
		// check prev rows
		int curColIndex = currRow.size() - 1;
		if(prevRows.size() > 0) {
			for(int rowIndex = prevRows.size()-1; rowIndex > prevRows.size() - numRowsToCheckBack; rowIndex--) {
				if(rowIndex >= 0) {
					
					// check each col in prev row
					ArrayList<Integer> prevRow = prevRows.get(rowIndex);
					for(int colIndex = curColIndex + numColsToCheckBack; colIndex > curColIndex - numColsToCheckBack; colIndex--) {
						if(colIndex >= 0 && colIndex < prevRow.size()-1) {
							if(indexAttempt == prevRow.get(colIndex)) {
								return false;
							}
						}
					}
				}
			}
		}
		
		// boundaries protection
		if(indexAttempt < 0) return false;
		if(indexAttempt >= _svgs.size()) return false;
		
		// good!
		return true;
	}
	
	public class SvgRanked {
		public PShape shape;
		public String file;
		public float whitenessOrig = 0;
		public float whiteness = 0;
		public float scale = 1.0f;
		public boolean isWhite = false;
		public SvgRanked(PShape shape, float scale, String file) {
			this.shape = shape;
			this.scale = scale;
			this.file = file;
			
			// draw shape
			analyzeCanvas.beginDraw();
			analyzeCanvas.clear();
			analyzeCanvas.background(255);
			PG.setDrawCenter(analyzeCanvas);
			draw(analyzeCanvas, analyzeSize/2, analyzeSize/2, analyzeSize);
			analyzeCanvas.endDraw();
			
			float numPixels = analyzeCanvas.width * analyzeCanvas.height;
			for (int x = 0; x < analyzeCanvas.width - 1; x++) {
				for (int y = 0; y < analyzeCanvas.height - 1; y++) {
					int pixelColor = analyzeCanvas.get(x, y);
					whiteness += ColorUtil.redFromColorInt(pixelColor);
					whiteness += ColorUtil.greenFromColorInt(pixelColor);
					whiteness += ColorUtil.blueFromColorInt(pixelColor);
				}
			}
			whiteness = whiteness / (numPixels * 3f);
			whitenessOrig = whiteness;
			
			// extra mapping since analyzed values only go from 9-173
//			if(mapsTo255 == true) whiteness = P.map(whiteness, 9, 173, 0, 255);
			if(mapsTo255 == true) whiteness = P.map(whiteness, colorRangeLow, colorRangeHigh, 0, 255);
			
			// remove layers that we want to slay
			PShape bg = this.shape.getChild("Background");
			if(bg != null) {
				this.shape.removeChild(this.shape.getChildIndex(bg));
			}
			bg = this.shape.getChild("Guides");
			if(bg != null) {
				this.shape.removeChild(this.shape.getChildIndex(bg));
			}

		}
		
		public void draw(PApplet canvas, float x, float y, float drawSize) {
			if(scale < 1.0f) {
				if(isWhite) {
					canvas.shape(_whiteDot.shape, x, y, drawSize, drawSize);
				} else {
					canvas.shape(_blackDot.shape, x, y, drawSize, drawSize);
				}
			}
			if(rendering == true) {
				// super jacked repositioning for exporting a pdf. not sure why
				canvas.shape(shape, x + drawSize * 0.5f * (1-(scale/1)), y + drawSize * 0.5f *(1-(scale/1)), drawSize * scale, drawSize * scale);
				// P.println("drew file: "+file);
			} else {
				canvas.shape(shape, x, y, drawSize * scale, drawSize * scale);
			}
		}
		
		public void draw(PGraphics canvas, float x, float y, float drawSize) {
			if(scale < 1.0f) {
				if(isWhite) {
					canvas.shape(_whiteDot.shape, x, y, drawSize, drawSize);
				} else {
					canvas.shape(_blackDot.shape, x, y, drawSize, drawSize);
				}
			}
			canvas.pushMatrix();
			canvas.translate(x, y);
			canvas.shape(shape, 0, 0, drawSize * scale, drawSize * scale);
			canvas.popMatrix();
		}
	}
}