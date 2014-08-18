package com.haxademic.sketch.test;

import java.awt.Rectangle;
import java.util.ArrayList;

import processing.core.PApplet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

@SuppressWarnings("serial")
public class RectangleSubdivisionTest
extends PAppletHax {

	protected ArrayList<Rectangle> _rectangles;
	protected int _numDivisions = 100;
	
	public static void main(String args[]) {
		_isFullScreen = false;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", RectangleSubdivisionTest.class.getName() });
	}

	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "fills_screen", "false" );
		_appConfig.setProperty( "fullscreen", "false" );
		_appConfig.setProperty( "width", "800" );
		_appConfig.setProperty( "height", "600" );
	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );
		newRectanges();
	}
	
	public void newRectanges() {
		// start with 1 rect
		_rectangles = new ArrayList<Rectangle>();
		_rectangles.add(new Rectangle(0, 0, p.width, p.height));
		
		// split, remove, and add split children
		for( int i=0; i < _numDivisions; i++ ) {
			int randIndex = MathUtil.randRange(0, _rectangles.size()-1);
			Rectangle rectToSplit = _rectangles.get(randIndex);
			if(rectToSplit.width > 30 || rectToSplit.height > 30) {
				_rectangles.remove(randIndex);
				if(rectToSplit.width > rectToSplit.height) {
					float horizSplit = Math.round(10f * MathUtil.randRangeDecimal(0.3f, 0.7f))/10f;	// quantized split
					P.println(horizSplit);
					_rectangles.add(new Rectangle(rectToSplit.x, rectToSplit.y, Math.round(rectToSplit.width * horizSplit), rectToSplit.height));
					_rectangles.add(new Rectangle(rectToSplit.x + _rectangles.get(_rectangles.size()-1).width, rectToSplit.y, Math.round(rectToSplit.width * (1f-horizSplit)), rectToSplit.height));
				} else {
					float vertSplit = Math.round(10f * MathUtil.randRangeDecimal(0.3f, 0.7f))/10f;	// quantized split
					P.println(vertSplit);
					_rectangles.add(new Rectangle(rectToSplit.x, rectToSplit.y, rectToSplit.width, Math.round(rectToSplit.height * vertSplit)));
					_rectangles.add(new Rectangle(rectToSplit.x, rectToSplit.y + _rectangles.get(_rectangles.size()-1).height, rectToSplit.width, Math.round(rectToSplit.height * (1f-vertSplit)) ));
				}
			}
		}
		
		// debug print
		for (Rectangle rect : _rectangles) {
			P.println(rect.x, rect.y, rect.width, rect.height);
		}
	}

	public void drawApp() {
		background(0);
		
		p.noFill();
		p.stroke(255);
		
		for (Rectangle rect : _rectangles) {
			p.rect(rect.x, rect.y, rect.width, rect.height);
		}
	}
	
	public void exportVertices() {
		String export = "";
		export += "#group#\n";
		for (Rectangle rect : _rectangles) {
			export += "#poly#";
			export += rect.x+","+rect.y+",";
			export += (rect.x+rect.width)+","+rect.y+",";
			export += (rect.x+rect.width)+","+(rect.y+rect.height)+",";
			export += rect.x+","+(rect.y+rect.height);
			export += "\n";
		}
		FileUtil.writeTextToFile(FileUtil.getHaxademicDataPath() + "text/mapping/mapping-"+SystemUtil.getTimestamp(p)+".txt", export);
	}

	
	public void mousePressed() {
		super.mousePressed();
		newRectanges();
	}
	
	public void keyPressed() {
		if(p.key == 'e') {
			exportVertices();
		}
	}

}