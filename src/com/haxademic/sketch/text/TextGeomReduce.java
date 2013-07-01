package com.haxademic.sketch.text;
import geomerative.RCommand;
import geomerative.RFont;
import geomerative.RG;
import geomerative.RGroup;
import geomerative.RPoint;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class TextGeomReduce
extends PApplet{

	RFont font;

	public void setup()
	{
	    size(400,400);
	    smooth();
	    
	    RG.init(this);

	    font = new RFont( "../data/fonts/bitlow.ttf", 72, RFont.CENTER);

	    frameRate( 20 );
	}

	public void draw()
	{
	    background(255);
	    translate(width/2,height/2);

	    RGroup grp = font.toGroup("HFGello!");
	    
	    // die folgenden einstellungen beinflussen wieviele punkte die
	    // polygone am ende bekommen werden.

	    //RCommand.setSegmentStep(random(0,3));
	    //RCommand.setSegmentator(RCommand.UNIFORMSTEP);
	    
	    RCommand.setSegmentLength(frameCount % 50);
	    RCommand.setSegmentator(RCommand.UNIFORMLENGTH);
	    
	    //RCommand.setSegmentAngle(random(0,HALF_PI));
	    //RCommand.setSegmentator(RCommand.ADAPTATIVE);

	    RPoint[] pnts = grp.getPoints();

	    ellipse(pnts[0].x, pnts[0].y, 5, 5);
	    for ( int i = 1; i < pnts.length; i++ )
	    {
	        line( pnts[i-1].x, pnts[i-1].y, pnts[i].x, pnts[i].y );
	        ellipse(pnts[i].x, pnts[i].y, 5, 5);
	    }
	}

}
