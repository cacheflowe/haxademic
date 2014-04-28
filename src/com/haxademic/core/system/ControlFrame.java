package com.haxademic.core.system;

// from: https://gist.github.com/eskimoblood/10414654

import java.awt.Frame;

import processing.core.PApplet;
import controlP5.ControlP5;

@SuppressWarnings("serial")
public class ControlFrame 
extends PApplet {

	Frame f;
	int w;
	int h;
	int x;
	int y;
	Object _parent;
	String _name;

	public void setup() {
		size(w, h);
		frameRate(30);
	}

	public void draw() {
		if( frameCount == 1 ) {
			createControlP5(_parent, _name);
		}
		if(	f.isFocused() == true ) {
			background(0);
		} else {
			background(20);
		}
	}

	public ControlFrame(Object parent, int w, int h, int x, int y) {
		_parent = parent;
		_name = parent.getClass().getSimpleName();
		this.w = w;
		this.h = h;
		this.x = x;
		this.y = y;

		createFrame(_name);
	}

	private void createFrame(String name) {
		f = new Frame(name);
		f.add(this);

		this.init();

		f.setTitle(name);
		f.setSize(w, h);
		f.setLocation(x, y);
		f.setResizable(false);
		f.setVisible(true);
	}

	private void createControlP5(Object parent, String name) {
		ControlP5 cp5 = new ControlP5(this);
		cp5.addControllersFor(name, parent);
	}
}