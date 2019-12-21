package com.haxademic.render;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.MotionBlurPGraphics;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.SystemUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class BFLinewaveRenderStatic
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PImage _print;
	float _frames = 100;
	protected PGraphics _pg;
	protected MotionBlurPGraphics _pgMotionBlur;
	protected ArrayList<Linewave> _lines;
	
	protected boolean _shouldPrint = false;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "900" );
		Config.setProperty( AppSettings.HEIGHT, "900" );


		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		Config.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		Config.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		Config.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}
	
	public void firstFrame() {

		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_print = p.loadImage(FileUtil.getHaxademicDataPath()+"images/BF.Linewave.1.jpg");
		_lines = new ArrayList<Linewave>();
		for (int i = 0; i < 35; i++) {
			_lines.add(new Linewave(277, 189, i));
		}
		buildCanvas();
	}

	protected void buildCanvas() {
		_pg = p.createGraphics( p.width, p.height, P.P3D );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);
		_pgMotionBlur = new MotionBlurPGraphics(3);
	}
	
	protected void drawGraphics() {
		PG.setDrawCorner(p);
		
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
		if( _shouldPrint ) p.beginRecord( P.PDF,  FileUtil.getHaxademicOutputPath() + "linewave-"+ SystemUtil.getTimestamp() +".pdf" );
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

