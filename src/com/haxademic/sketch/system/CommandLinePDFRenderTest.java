package com.haxademic.sketch.system;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PShape;

public class CommandLinePDFRenderTest 
extends PAppletHax {
	public static String arguments[];
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// TODO:
	// [DONE] Import svg
	// [DONE] Export pdf
	// [DONE] tweak svg layers - set color
	// [DONE] output svg (conert pdf?)
    // [DONE] pattern generation
	// [DONE] test with PImages instead of PShapes
    // [DONE] run on command line
    // half toning 

	
	protected ArrayList<PShape> _svgs;
	protected ArrayList<PGraphics> _imgs;
	protected int _curShapeIndex = 0;
	protected EasingFloat _timeEaser = new EasingFloat(100, 15);
//	protected PImage img;

	protected boolean shouldRecord = false;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERER, P.PDF );
		p.appConfig.setProperty( AppSettings.PDF_RENDERER_OUTPUT_FILE, FileUtil.getHaxademicOutputPath() + "/_pdf/output.pdf" );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
//		p.appConfig.setProperty( AppSettings.FPS, 30 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 600 );
	}
	
	public void setup() {
		super.setup();
		
//		img = p.loadImage(FileUtil.getFile("images/bread.png"));
		
		PShape newShape = p.createShape(RECT, 0, 0, 80, 80);
		newShape.setFill(p.color(255));
		
		_svgs = new ArrayList<PShape>();
		_imgs = new ArrayList<PGraphics>();
		
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/nike/camo-m.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/nike/camo-a.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/nike/camo-k.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/nike/camo-w.svg" ) );
		
		// output args
		P.println("arguments length", arguments.length);
		for (String string : arguments) {
			P.println(string);
		}
	}
	
	protected boolean isHeadless() {
		return renderer == PRenderers.PDF;
	}
	
	public void drawApp() {
		p.background(100);
		
		// update anim params
		float frameCountDivided = p.frameCount * 0.01f;
		_timeEaser.update();

		// set up to render pdf
		if(shouldRecord == true) {
			p.beginRecord(P.PDF, FileUtil.getHaxademicOutputPath() + "/_pdf/frame-####.pdf");
		}
		
		// create image buffers
		if(p.frameCount == 1 && isHeadless() == false) {
			for (int i = 0; i < _svgs.size(); i++) {
				PGraphics imgCopy = ImageUtil.shapeToGraphics(_svgs.get(i));
				_imgs.add(imgCopy);
			}
		}
		
		// update phsapes in realtime
		for (PShape shape : _svgs) {
			for(int i = 0; i < shape.getChildCount(); i++) {
				PShape subShape = shape.getChildren()[i];
				subShape.setFill(p.color(P.map(p.mouseX, 0, p.width, 0, 255),255,255));
				subShape.setFill(p.color(
					127f + 127f * P.sin(i + frameCountDivided),
					127f + 127f * P.cos(i + frameCountDivided),
					127f + 127f * P.sin(-i + frameCountDivided)
				));
			}
			shape.toString();
		}
		
		// debug - redraw text buffer copies
		PShape curShape = null;
		PGraphics curImage = null;
		if(isHeadless() == false) {
	//		_imgs.clear();
			for (int i = 0; i < _svgs.size(); i++) {
	//			PGraphics imgCopy = ImageUtil.shapeToGraphics(_svgs.get(i));
	//			_imgs.add(imgCopy);
	//			p.shape(_svgs.get(i));
				curShape = _svgs.get(i);
				curImage = _imgs.get(i);
				curImage.beginDraw();
				curImage.clear();
				curImage.shape(curShape);
				curImage.endDraw();
			}
		}


		curShape = _svgs.get( _curShapeIndex );
		if(isHeadless() == false) curImage = _imgs.get( _curShapeIndex );
		float size = 200f + 100f * P.sin(_timeEaser.value()/100f);
		float spacing = size * 0.6f;
		float ratioW = size / curShape.width;
		float ratioH = size / curShape.height;
		float shorterRatio = ratioW > ratioH ? ratioH : ratioW;

//		curShape.disableStyle();
		float newW = shorterRatio * curShape.width;
		float newH = shorterRatio * curShape.height;

		p.fill(0);
		p.noStroke();

		int shapeIndex = 0;
		for(float i=0; i < 200; i++) {
//			for( float j=-300; j < p.height + 300; j+= spacing ) {
				shapeIndex++;
				if(shapeIndex >= _svgs.size()) shapeIndex = 0;
				curShape = _svgs.get( shapeIndex );
				if(isHeadless() == false) curImage = _imgs.get( shapeIndex );

				p.pushMatrix();
				p.translate(
					-p.width/2f + p.noise(_timeEaser.value() * 0.02f + i * 2.1f) * p.width * 2f, 
					-p.height/2f + p.noise(_timeEaser.value() * 0.02f + i * 10.1f) * p.height * 2f
				);
				p.rotate(P.TWO_PI * P.sin((_timeEaser.value() * p.noise(i))/75f));
				p.scale( 2f + 0.5f * P.sin((_timeEaser.value() + i)/75f));
								
				if(isHeadless() == false) {
					p.image( curImage, -newW/2f, -newH/2f, newW, newH );
				} else {
					p.shape( curShape, -newW/2f, -newH/2f, newW, newH );
				}
				p.popMatrix();
//			}			
		}
		
		// auto-advance animations
		if((p.frameCount - 15) % 130 == 0) {
			_timeEaser.setTarget(_timeEaser.value() + 30);
		}
		
		// render pdf
		if(shouldRecord == true) {
			p.endRecord();
			shouldRecord = false;
		}
	}
	
	public void keyPressed() {
		if(p.key == ' ') {
			_curShapeIndex++;
			if( _curShapeIndex >= _svgs.size() ) _curShapeIndex = 0; 
		} else if (p.key == 'm') {
			_timeEaser.setTarget(_timeEaser.value() + 30);
		} else if (p.key == 'r') {
			shouldRecord = true;
		}
	}
}