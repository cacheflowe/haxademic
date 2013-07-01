package com.haxademic.sketch.toxi_tests;

import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import toxi.color.ColorList;
import toxi.color.ColorRange;
import toxi.color.TColor;
import toxi.color.theory.ColorTheoryRegistry;
import toxi.color.theory.ColorTheoryStrategy;
import toxi.color.theory.CompoundTheoryStrategy;
import toxi.geom.mesh.TriangleMesh;
import toxi.math.waves.AbstractWave;
import toxi.processing.ToxiclibsSupport;

public class ToxiColors
extends PApplet{
	TriangleMesh mesh = new TriangleMesh();

	AbstractWave modX, modY;

	boolean isWireFrame;
	boolean showNormals;

	ToxiclibsSupport toxi;

	protected ColorList _colorList;
	protected TColor _colorFG1;
	protected TColor _colorFG2;
	protected TColor _colorAmbient;
	protected TColor _colorBG1;
	protected TColor _colorBG2;

	protected PApplet p = this;
	
	
	public void setup() {
	  size(800,600, OPENGL);
	  toxi = new ToxiclibsSupport(this);
	  pickNewColors();
	}

	public void draw() {
	  background(0);
	  lights();
	  translate(width / 2, height / 2, 0);
//	  rotateX(mouseY * 0.01f);
//	  rotateY(mouseX * 0.01f);
	  debugDrawColorList();
	}

	protected void getNewColorList() {
		// get a new ColorList
		TColor col = ColorRange.BRIGHT.getColor();
		// loop through strategies
		ArrayList strategies = ColorTheoryRegistry.getRegisteredStrategies();
		for (Iterator i=strategies.iterator(); i.hasNext();) {
			ColorTheoryStrategy s = (ColorTheoryStrategy) i.next();
			_colorList = ColorList.createUsingStrategy(s, col);
			_colorList = new ColorRange( _colorList ).addBrightnessRange(0.8f,1).getColors(null,100,0.05f);
			_colorList.sortByDistance(true);
		}
	}
	
	protected void debugDrawColorList() {
//		DrawUtil.setTopLeft( p );
		// draw the color list for debug purposes
		int x = 0;
		int size = 30;
		for (Iterator<TColor> i = _colorList.iterator(); i.hasNext();) {
			TColor c = (TColor) i.next();
			p.fill(c.toARGB());
			x += size;
			p.rect(x,0,x+size,size);
		}
		
		
		x = 0;
		p.fill(_colorFG1.toARGB());
		p.rect(x,size,x+size,size);
		x += size;
		p.fill(_colorFG2.toARGB());
		p.rect(x,size,x+size,size);
		x += size;
		p.fill(_colorAmbient.toARGB());
		p.rect(x,size,x+size,size);
		x += size;
		p.fill(_colorBG1.toARGB());
		p.rect(x,size,x+size,size);
		x += size;
		p.fill(_colorBG2.toARGB());
		p.rect(x,size,x+size,size);
	}
	
	protected void pickNewColors() {
		// get a single strategy
		TColor color = ColorRange.WEAK.getColor();
		ColorTheoryStrategy strategy = new CompoundTheoryStrategy ();
		_colorList = ColorList.createUsingStrategy(strategy, color);

		for (Iterator<TColor> i = _colorList.iterator(); i.hasNext();) {
			TColor c = (TColor) i.next();
			c.lighten( 0.2f );
		}

		
		
		// store a few random colors
//		TColor color1 = _colorList.getRandom();
//		color1.lighten(0.3f);
		_colorFG1 = _colorList.get( 0 );
		_colorFG2 = _colorFG1.getAnalog(45,1);//_colorList.get( 1 );//.getRandom();	// color1.complement().toARGB()
		_colorAmbient = _colorFG2.getAnalog(45,1);//_colorList.get( 2 );
		_colorBG1 = _colorAmbient.getAnalog(45,1);//_colorList.get( 3 );
		_colorBG2 = _colorBG1.getAnalog(45,1);//_colorList.get( 4 );
		
		float lighten = 0.10f;
		_colorFG1.lighten( lighten );
		_colorFG2.lighten( lighten );
		_colorBG1.lighten( lighten );
		_colorBG2.lighten( lighten );
		_colorAmbient.lighten( lighten );
	}


	public void keyPressed() {
	  if (key == 'c') {
		  pickNewColors();
	  }
	}
}
