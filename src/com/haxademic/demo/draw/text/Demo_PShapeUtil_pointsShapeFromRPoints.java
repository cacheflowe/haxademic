package com.haxademic.demo.draw.text;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.Renderer;

import geomerative.RCommand;
import geomerative.RFont;
import geomerative.RG;
import processing.core.PShape;
import processing.core.PVector;

public class Demo_PShapeUtil_pointsShapeFromRPoints
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected RFont font;
	protected PShape pointsShape;
	protected LinearFloat[] pointsSize;
	protected int wordIndex = 0;
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 1024);
		Config.setProperty(AppSettings.RENDERING_MOVIE, true);
	}


	protected void firstFrame() {
		// load font, get letter points
	    RG.init(this);
	    font = new RFont( FileUtil.getPath(DemoAssets.fontOpenSansPath), 72, RFont.CENTER);
	    RCommand.setSegmentLength(2.1f);
	    RCommand.setSegmentator(RCommand.UNIFORMLENGTH);
	    
	    buildWordShape("WHAT");
	    restartShape();
	}
	
	protected void buildWordShape(String word) {
	    // scale it up!
	    pointsShape = PShapeUtil.pointsShapeFromRPoints(font.toGroup(word).getPoints());
	    PShapeUtil.centerShape(pointsShape);
	    PShapeUtil.scaleShapeToHeight(pointsShape, p.height * 0.4f);
	    
	    // build animation queue
	    pointsSize = new LinearFloat[pointsShape.getVertexCount()];
	    for (int i = 0; i < pointsSize.length; i++) {
	    	pointsSize[i] = new LinearFloat(0, 0.03f);
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			restartShape();
		}
	}
	
	protected void restartShape() {
	    for (int i = 0; i < pointsSize.length; i++) {
	    	pointsSize[i].setTarget(1).setCurrent(0);
	    	pointsSize[i].setDelay(P.floor(i/3f));
		}
	}
	
	protected void shrinkShape() {
		for (int i = 0; i < pointsSize.length; i++) {
	    	pointsSize[i].setTarget(0).setCurrent(1);
	    	pointsSize[i].setDelay(P.floor(i/3f));
		}
	}

	protected void drawApp() {
		// context
	    p.background(0);
	    PG.setCenterScreen(p);
//	    PG.basicCameraFromMouse(p.g);
	    if(wordIndex == 0) { 
		    p.rotateX(-0.3f);
		    p.rotateY(-1.05f);
	    } else if(wordIndex == 1) { 
	    	p.rotateX(0.3f);
	    	p.rotateY(-1.05f);
	    } else if(wordIndex == 2) { 
	    	p.rotateX(0.65f);
	    	p.rotateY(0);
	    }
	    p.ortho();

	    // draw lines
	    p.stroke(255);
	    for ( int i = 0; i < pointsShape.getVertexCount(); i++ ) {
	    	pointsSize[i].update();
	    	float easedProgress = Penner.easeOutBack(pointsSize[i].value());
	    	
	    	if(easedProgress > 0) {
		    	float depth = 20f * easedProgress;
		    	PVector v = pointsShape.getVertex(i);
		    	p.strokeWeight(1f + 1f * easedProgress);
		        line(v.x, v.y, depth, v.x, v.y, -depth);
		        p.strokeWeight(1f + 4f * easedProgress);
		        point(v.x, v.y, depth);
		        point(v.x, v.y, -depth);
	    	}
	    }
	    
	    // reverse it
	    LinearFloat lastPoint = pointsSize[pointsSize.length - 1];
	    if(lastPoint.target() == 1 && lastPoint.value() == 1) {
	    	shrinkShape();
	    }
	    // restart or finish
	    if(lastPoint.target() == 0 && lastPoint.value() == 0) {
	    	if(wordIndex == 0) buildWordShape("WHUT");
	    	if(wordIndex == 1) { buildWordShape("WAT"); PShapeUtil.scaleShapeToHeight(pointsShape, p.height * 0.27f); }
	    	wordIndex++;
	    	if(wordIndex == 3) {
	    		if(Renderer.instance().isRendering) {
	    			Renderer.instance().videoRenderer.stop();
	    		}
	    	}
	    	else restartShape();
	    }
	    
//	    p.shape(pointsShape);
	}

}
