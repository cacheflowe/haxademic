package com.haxademic.sketch.text;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

@SuppressWarnings("serial")
public class Text3DPG
extends PApplet {

	PFont ff;
	PImage ww;
	ArrayList<PVector> positions = new ArrayList<PVector>();
	String sentence;
	int fontSize = 12;

	public void setup()
	{
		size(800, 600, P3D);
		ff = createFont("../data/fonts/bitlow.ttf",fontSize);	//"Arial"
		sentence = "Hello. What's up?";
		ww = crImage();
		scan();
	}

	public void draw() {
		background(255);
		lights();
		translate(mouseX, height/2, mouseY); 
		for(int i = 0; i < positions.size()-1; i++) {
			PVector ps = positions.get(i);
			pushMatrix();
			translate(ps.x, ps.y, 0);
			noStroke();
			fill(200);
			box(1, 1, 5);
			popMatrix();
		}
	}

	PImage crImage() {
		PGraphics pg = createGraphics(400,20,JAVA2D);
		pg.beginDraw();
		pg.background(255);
		pg.fill(250,0,0);
		pg.textAlign(CENTER);
		pg.textFont(ff, fontSize);
		pg.text(sentence, 0, 0, 400, 20);
		pg.endDraw();
		PImage w = createImage(400,20,RGB);
		copy(pg, 0, 0, 400, 20, 0, 0, 400, 20);
		return w;
	}

	void scan()
	{
		ww = crImage();

		positions.clear();
		for(int x = 0; x < ww.width; x++) {
			for(int y = 0; y < ww.height; y++) {
				if(get(x,y) != -65794){
					positions.add(new PVector(x,y,0));    
				}
			}
		}
	}

	public void keyPressed() {
//		if(textWidth(sentence) <= 210){
//			sentence += key;
//			crImage();
//			scan();
//		}
//		else
//			sentence = "";
	}

}
