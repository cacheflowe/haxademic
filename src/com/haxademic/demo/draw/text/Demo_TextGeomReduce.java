package com.haxademic.demo.draw.text;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import geomerative.RCommand;
import geomerative.RFont;
import geomerative.RG;
import geomerative.RGroup;
import geomerative.RPoint;

public class Demo_TextGeomReduce
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	RFont font;

	protected void firstFrame() {
	    RG.init(this);
	    font = new RFont( FileUtil.getPath("haxademic/fonts/bitlow.ttf"), 72, RFont.CENTER);
	}

	protected void drawApp() {
	    background(255);
	    translate(width/2,height/2);

	    RGroup grp = font.toGroup("CACHEFLOWE!");
	    
	    //RCommand.setSegmentStep(random(0,3));
	    //RCommand.setSegmentator(RCommand.UNIFORMSTEP);
	    
	    RCommand.setSegmentLength(17 + 15f * MathUtil.saw(p.frameCount * 0.01f));
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
