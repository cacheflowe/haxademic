package com.haxademic.sketch.render;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.MotionBlurPGraphics;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

@SuppressWarnings("serial")
public class BFLinewaveRenderStatic
extends PAppletHax{
	
	PImage _print;
	float _frames = 100;
	protected PGraphics _pg;
	protected MotionBlurPGraphics _pgMotionBlur;
	protected ArrayList<Linewave> _lines;
	
	protected boolean _shouldPrint = false;

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "900" );
		_appConfig.setProperty( "height", "900" );


		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+1) );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_print = p.loadImage(FileUtil.getHaxademicDataPath()+"images/BF.Linewave.1.jpg");
		_lines = new ArrayList<Linewave>();
		for (int i = 0; i < 35; i++) {
			_lines.add(new Linewave(277, 189, i));
		}
		buildCanvas();
	}

	protected void buildCanvas() {
		_pg = p.createGraphics( p.width, p.height, P.OPENGL );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);
		_pgMotionBlur = new MotionBlurPGraphics(3);
	}
	
	protected void drawGraphics() {
		DrawUtil.setDrawCorner(p);
		
		// timeline
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float frameOsc = P.sin( PConstants.TWO_PI * percentComplete);
		frameOsc = P.sin( PConstants.TWO_PI * 0.25f);

		// reference image
//		p.image(_print, 0, 0, p.width, p.height);
		
		// white bg
		p.fill(255, 255);
		p.noStroke();
		p.rect(266, 175, 373, 537);
		
		// draw lines
		int numLines = _lines.size();
		for (int i = 0; i < numLines; i++) {
			_lines.get(i).update(p, frameOsc);
		}
	}

	public void drawApp() {
		p.background(0);
		if( _shouldPrint ) p.beginRecord( P.PDF,  FileUtil.getHaxademicOutputPath() + "linewave-"+ SystemUtil.getTimestamp(p) +".pdf" );
		drawGraphics();
//		_pgMotionBlur.updateToCanvas(_pg, p.g, 1f);
		if( _shouldPrint == true ) {
			p.endRecord();
			_shouldPrint = false;
		}
	}

	public void keyPressed() {
		if( p.key == 'p' ) printPDF();
	}

	public void printPDF() {
		_shouldPrint = true;
	}
	
	public class Linewave {
		public float WIDTH = 350;
		public float HEIGHT = 7;
		public float SPACING = 13.6f;
		public float SPACING_RIGHT = 10;
		public float _index;
		public EasingFloat _x;
		public EasingFloat _y;
		public float _easeFactor = 4f;
		
		public Linewave(float x, float y, float index) {
			_x = new EasingFloat(x, _easeFactor);
			_y = new EasingFloat(y + SPACING * index, _easeFactor);
			_index = index;
		}
		
		public void update(PApplet pg, float frameOsc) {
			_x.update();
			_y.update();
			
			
			pg.noFill();
			pg.stroke(0);
			pg.strokeWeight(HEIGHT);
			p.strokeCap(P.SQUARE);

			float third = 1f/3f;
			float twoThird = 2f/3f;
			float sixth = 1f/6f;
			float curveAdd = 1.25f;
			curveAdd = curveAdd/2f + frameOsc * curveAdd/2f;
			
			pg.beginShape();
			pg.vertex(_x.value(), _y.value());
			pg.bezierVertex(
					_x.value() + WIDTH * sixth, _y.value(), 
					_x.value() + WIDTH * sixth, _y.value() + _index * curveAdd, 
					_x.value() + WIDTH * third, _y.value() + _index * curveAdd
					);
			pg.bezierVertex(
					_x.value() + WIDTH * (third + sixth), _y.value() + _index * curveAdd, 
					_x.value() + WIDTH * (third + sixth), _y.value(), 
					_x.value() + WIDTH * twoThird, _y.value()
					);
			pg.bezierVertex(
					_x.value() + WIDTH * (twoThird + sixth), _y.value(),
					_x.value() + WIDTH * (twoThird + sixth), _y.value() + _index * curveAdd, 
					_x.value() + WIDTH, _y.value() + _index * curveAdd
					);
			pg.endShape();
		}
	}
}

