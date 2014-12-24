package com.haxademic.core.render;

import java.util.ArrayList;

import processing.core.PGraphics;
import processing.core.PImage;

import com.haxademic.core.render.DrawCommand.Command;

public class HaxMotionBlur {
	
	int[][] result;
	float t;
	int _blurFrames = 8;
	float shutterAngle = 0.6f;
	int numFrames = 48;
	
	protected ArrayList<PImage> _pastFrames;
	
	public HaxMotionBlur(int blurFrames) {
		_pastFrames = new ArrayList<PImage>();
		this._blurFrames = blurFrames;
	}

	public void render(PGraphics p, Command drawCommand) {
		// draw the new frame
		drawCommand.execute(p, t);
		
		// save current frame to buffer
		_pastFrames.add(p.get());
		if(_pastFrames.size() > _blurFrames) {
			_pastFrames.remove(0);
		}

		// clear out "offscreen" pixel grid
		if(result == null) result = new int[p.width*p.height][3];
		for (int i=0; i<p.width*p.height; i++)
			for (int a=0; a<3; a++)
				result[i][a] = 0;
		
		// loop through any past frames and add them into the offscreen pixel grid 
		for (int f=0; f < _pastFrames.size(); f++) {
			PImage pastFrame = _pastFrames.get(f);
			pastFrame.loadPixels();
			
			for (int i=0; i<pastFrame.pixels.length; i++) {
				result[i][0] += pastFrame.pixels[i] >> 16 & 0xff;
				result[i][1] += pastFrame.pixels[i] >> 8 & 0xff;
				result[i][2] += pastFrame.pixels[i] & 0xff;
			}
		}
	
		// copy offscreen pixel grid to PApplet
		p.loadPixels();
		for (int i=0; i < p.pixels.length; i++)
			p.pixels[i] = 
				0xff << 24 | 
				(result[i][0]/_blurFrames) << 16 | 
				(result[i][1]/_blurFrames) << 8 | 
				(result[i][2]/_blurFrames);
		p.updatePixels();
		
		// redraw current frame on top of blur
		drawCommand.execute(p, t);
	}

}
