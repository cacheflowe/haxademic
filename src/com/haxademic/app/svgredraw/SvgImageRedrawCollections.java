package com.haxademic.app.svgredraw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class SvgImageRedrawCollections 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// Each group needs their own colorRangeLow/colorRangeHigh?? 
	
	protected PImage img;
	protected PImage imgGroupMap;
	
	protected int groupIndex = 0;
	String[] svgDirectories;
	int[] svgGroupMapColors;
	ArrayList<SvgCollection> _svgsCollections;
	
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

	float colorRangeLow = 56;
	float colorRangeHigh = 231;
	float blackThresh = 0.01f;

	int noRepeatVariance = 20;
	int numColsToCheckBack = 5;
	int numRowsToCheckBack = 5;
	int bailedOnUniqueAttempts = 0;
	int rescuedLessUniqueSuccess = 0;
	ArrayList<ArrayList<String>> prevRows;
	ArrayList<String> currRow;

	boolean rendering = true;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "1800" );
		Config.setProperty( AppSettings.HEIGHT, "1000" );
	}

	public void firstFrame() {
	
		p.smooth();
		
		// load image and configure size
		img = p.loadImage(FileUtil.getPath("images/_grovemural_08-26-15.jpg"));
		imgGroupMap = p.loadImage(FileUtil.getPath("images/_grovemural_08-24-15_map.jpg"));
		svgDirectories = new String[]{"svg/bw15/level3/", "svg/bw15/level2/", "svg/bw15/level1/"};
		svgGroupMapColors = new int[]{ColorUtil.colorFromHex("#000000"), ColorUtil.colorFromHex("#808080"), ColorUtil.colorFromHex("#ffffff")};
		imagePrintWidth = 20.25f;
		iconInches = 0.675f;
		shapeDrawScale = 0.95f;
		outputFile = "2015-08-26_la_"+iconInches+"inch";
		splitFiles = true;
		numRowSplits = 6;
		
		// transform to blurred img
		PGraphics pg = ImageUtil.imageToGraphics(img);
		BlurHFilter.instance(p).setBlurByPercent(0.36f, pg.width);
		BlurHFilter.instance(p).applyTo(pg);
		BlurVFilter.instance(p).setBlurByPercent(0.36f, pg.height);
		BlurVFilter.instance(p).applyTo(pg);
		img = pg.get();
		
		// calculate svg draw size base on target output width
		shapeSize = (float) img.width / ((imagePrintWidth * 12f) / iconInches);	// for 18.5 feet wide
//		shapeSize = shapeSize * (1f / imgScale);
//		P.println("shapeSize: ",shapeSize);
		
		// set up arrays to track non-repetition
		prevRows = new ArrayList<ArrayList<String>>();
		currRow = new ArrayList<String>();

		// load collections & analyze as they're created
		analyzeCanvas = p.createGraphics((int)analyzeSize, (int)analyzeSize);
		_svgsCollections = new ArrayList<SvgCollection>();
		for (String dir : svgDirectories) {
			_svgsCollections.add(new SvgCollection(dir));
		}
	}
	
	public class SvgCollection {
		public ArrayList<SvgRanked> _svgs;
		public SvgCollection(String dir) {
			// load svgs
			ArrayList<String> files = FileUtil.getFilesInDirOfType(FileUtil.getHaxademicDataPath() + dir, "svg");
			P.println("Loading and analyzing "+files.size()+" svgs");
			
			_svgs = new ArrayList<SvgRanked>();
			for (String file : files) {
				PShape shape = p.loadShape( FileUtil.getHaxademicDataPath() + dir + file );
				SvgRanked rankedSvg = new SvgRanked(shape, 1.0f, file);
				if(file.indexOf("black-dot.svg") != -1) {
					_blackDot = rankedSvg;
				} else if(file.indexOf("_white-dot.svg") != -1) {
					_whiteDot = rankedSvg;
				} else {
					// add normal svgs
					_svgs.add(rankedSvg);
					
					// add scaled versions
					// if(file.indexOf("scaled-100") != -1) {
						shape = p.loadShape( FileUtil.getHaxademicDataPath() + dir + file );
						rankedSvg = new SvgRanked(shape, 0.8f, file);
						_svgs.add(rankedSvg);
					// }
				}
			}
			
			// sort icons for easy pixel whiteness comparison/redrawing 
			Collections.sort(_svgs, new CustomComparator());
			
			// debug print whiteness analysis
			for (int i = 0; i < _svgs.size() - 1; i++) {
				P.println(i, "whiteness:", _svgs.get(i).whiteness, " | whitenessOrig:", _svgs.get(i).whitenessOrig);
			}
		}
		
		public ArrayList<SvgRanked> svgs() {
			return _svgs;
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
		p.image(img, 0, p.height - p.mouseY * 3f);
		
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
		for (SvgCollection collection : _svgsCollections) {
			for (SvgRanked shape : collection.svgs()) {
				shape.draw(p, x + shapeSizeDebug/2f, y + shapeSizeDebug/2f, shapeSizeDebug);
				x += shapeSizeDebug;
				if(x > p.width - shapeSizeDebug) {
					y += shapeSizeDebug;
					x = 0;
				}
			}
			y += shapeSizeDebug;
			x = 0;
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
				ArrayList<String> prevRow = new ArrayList<String>();
				prevRow.addAll(currRow);
				prevRows.add(prevRow);
			}
			currRow.clear();
			for (float x = halfShapeSize; x <= img.width - halfShapeSize; x += shapeSize) {
				// get current pixel color
				int pixelColor = img.get((int)x, (int)y);
				int mapColor = imgGroupMap.get((int)x, (int)y);
				

				// pick current svg set based on map image -----------------------------
				int mapGray = ColorUtil.redFromColorInt(mapColor);
				int groupGray = -1;
				int nextGroupGray = -1;
				
				ArrayList<SvgRanked> curSvgs = null;
				// set us to the exact group if we're not on a gradient
				for (int i = 0; i < svgGroupMapColors.length; i++) {
					groupGray = ColorUtil.redFromColorInt(svgGroupMapColors[i]);
					if(mapGray == groupGray) {
						curSvgs = _svgsCollections.get(i).svgs();
					}
				}
				// otherwise, find a weighted, random group based on which color we're in between
				if(curSvgs == null) {
					for (int i = 0; i < svgGroupMapColors.length - 1; i++) {
						groupGray = ColorUtil.redFromColorInt(svgGroupMapColors[i]);
						nextGroupGray = ColorUtil.redFromColorInt(svgGroupMapColors[i+1]);
						if(mapGray > groupGray && mapGray < nextGroupGray) {
							float groupLerpPosition = P.map(mapGray, groupGray, nextGroupGray, 0, 1f);
							if(MathUtil.randRangeDecimal(0, 1) > groupLerpPosition) {
								curSvgs = _svgsCollections.get(i).svgs();
							} else {
								curSvgs = _svgsCollections.get(i+1).svgs();
							}
						}
					}
				}
				
				// debug on first column
//				if(x == halfShapeSize) {
//					P.println("mapGray:",mapGray);
//					for (int i = 0; i < svgGroupMapColors.length; i++) {
//						groupGray = ColorUtil.redFromColorInt(svgGroupMapColors[i]);
//						P.println("groupGray",i,groupGray);
//					}
//				}
				
				// handle pure black pixels
				// if(pixelColor == ImageUtil.BLACK_INT) {
				if(ColorUtil.redFromColorInt(pixelColor) < blackThresh && ColorUtil.greenFromColorInt(pixelColor) < blackThresh && ColorUtil.blueFromColorInt(pixelColor) < blackThresh) {
					p.shape(_blackDot.shape, x, y, shapeDrawSize, shapeDrawSize);
					if(ColorUtil.redFromColorInt(pixelColor) != 0 && ColorUtil.greenFromColorInt(pixelColor) != 0 && ColorUtil.blueFromColorInt(pixelColor) != 0)
						P.println("Too Black ",ColorUtil.redFromColorInt(pixelColor), ColorUtil.greenFromColorInt(pixelColor), ColorUtil.blueFromColorInt(pixelColor));
					currRow.add("");
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
						for (int i = 0; i < curSvgs.size() - 1; i++) {
							if(curSvgs.get(i).whiteness > lightness) {
								int index = indexNonOverlap(curSvgs, i, numColsToCheckBack, numRowsToCheckBack);
								SvgRanked svg = curSvgs.get(index);
								svg.draw(p, x, y, shapeDrawSize);
								// p.shape(svg.shape, x, y, shapeDrawSize * svg.scale, shapeDrawSize * svg.scale);
								foundShape = true;
								currRow.add(svg.file);
								break;
							}
						}
						if(foundShape == false) {
							int index = curSvgs.size() - 1;
							index = indexNonOverlap(curSvgs, index, numColsToCheckBack, numRowsToCheckBack);
							SvgRanked svg = curSvgs.get(index);
							svg.draw(p, x, y, shapeDrawSize);
							// p.shape(svg.shape, x, y, shapeDrawSize * svg.scale, shapeDrawSize * svg.scale);
							currRow.add(svg.file);
						}
						if(foundShape == false) P.println("NO SHAPE DRAWN?!?!?!");
					} else {
						int index = P.round(P.map(lightness, 0, 255, 0, curSvgs.size() - 1));
						index = indexNonOverlap(curSvgs, index, numColsToCheckBack, numRowsToCheckBack);
						SvgRanked svg = curSvgs.get(index);
						svg.draw(p, x, y, shapeDrawSize);
						// p.shape(svg.shape, x, y, shapeDrawSize * svg.scale, shapeDrawSize * svg.scale);
						currRow.add(svg.file);
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
		P.println("Rescued unique attempts: ",rescuedLessUniqueSuccess);

	}
	
	public int indexNonOverlap(ArrayList<SvgRanked> curSvgs, int indexAttempt, int checkBackCols, int checkBackRows) {
		int attempts = 0;
		float crawlIndex = 0;
		int origAttempt = indexAttempt;
		while(indexIsUnique(curSvgs, indexAttempt, checkBackCols, checkBackRows) == false) {
			attempts++;
			if(attempts > noRepeatVariance * 2f) {
				bailedOnUniqueAttempts++;
				if(indexAttempt < 0) indexAttempt = 0;
				if(indexAttempt >= curSvgs.size()) indexAttempt = curSvgs.size() - 1;
				if(checkBackCols == numColsToCheckBack) {
					// reduce rows to check against and try again
					return indexNonOverlap(curSvgs, origAttempt, (int)Math.ceil(checkBackCols/2f), (int)Math.ceil(checkBackRows/2f));
				} else {
					return indexAttempt;
				}
			}
			crawlIndex += 0.5f; // half is down, whole number is up
			if(crawlIndex % 1f > 0) {
				indexAttempt = origAttempt + P.ceil(crawlIndex);
				indexAttempt = safeIndexForCollection(indexAttempt, curSvgs);
			} else {
				indexAttempt = origAttempt - P.ceil(crawlIndex);
				indexAttempt = safeIndexForCollection(indexAttempt, curSvgs);
			}
		}
		if(checkBackCols < numColsToCheckBack) rescuedLessUniqueSuccess++;
		return indexAttempt;
	}
	
	protected int safeIndexForCollection(int indexAttempt, ArrayList<SvgRanked> curSvgs) {
		if(indexAttempt < 0) indexAttempt = 0;
		if(indexAttempt >= curSvgs.size()) indexAttempt = curSvgs.size() - 1;
		return indexAttempt;
	}
	
	protected boolean indexIsUnique(ArrayList<SvgRanked> curSvgs, int indexAttempt, int checkBackCols, int checkBackRows) {		
		// check prev in current row
		if(currRow.size() > 0) {
			for(int colIndex = currRow.size()-1; colIndex > currRow.size() - checkBackCols; colIndex--) {
				if(colIndex >= 0) {
					if(curSvgs.get(indexAttempt).file == currRow.get(colIndex)) {
						// P.println("Checking prev in row "+indexAttempt+" == "+currRow.get(colIndex));
						return false;
					}
				}
			}
		}
		
		// check prev rows
		int curColIndex = currRow.size() - 1;
		if(prevRows.size() > 0) {
			for(int rowIndex = prevRows.size()-1; rowIndex > prevRows.size() - checkBackRows; rowIndex--) {
				if(rowIndex >= 0) {
					
					// check each col in prev row
					ArrayList<String> prevRow = prevRows.get(rowIndex);
					for(int colIndex = curColIndex + checkBackCols; colIndex > curColIndex - checkBackCols; colIndex--) {
						if(colIndex >= 0 && colIndex < prevRow.size()-1) {
							if(curSvgs.get(indexAttempt).file == prevRow.get(colIndex)) {
								return false;
							}
						}
					}
				}
			}
		}
		
		// boundaries protection
		if(indexAttempt < 0) return false;
		if(indexAttempt >= curSvgs.size()) return false;
		
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
			
			if(file.toLowerCase().indexOf("white") != -1) {
				isWhite = true;
			}
			
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