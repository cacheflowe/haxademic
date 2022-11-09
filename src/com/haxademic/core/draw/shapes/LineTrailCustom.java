package com.haxademic.core.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGL;

public class LineTrailCustom {
	
    protected PImage texture;
    protected PShape shape;
	protected float newSegmentDir = 0;
    protected int length; 
    protected float lineWeight; 
    protected float gapDist;
    protected float taperStart = 0f;
    protected int NO_FILL = -99; 
    protected PVector[] trail; 
    protected PVector utilVec = new PVector(); 

    public LineTrailCustom(int length, int lineWeight, float gapDist) {
        this.length = length;
        this.lineWeight = lineWeight;
        this.gapDist = gapDist;
        
        // tail coords
        trail = new PVector[length];
        for (int i = 0; i < length; i++) trail[i] = new PVector();
        
        // build PShape
        float textureStepY = 1f / length;
        float textureUV_Y = 0;
        float curY = 0;
//        PGL.bufferStreamingRetained = true;
        PGL.bufferUsageRetained = PGL.DYNAMIC_DRAW;
        shape = P.p.createShape();
        shape.beginShape(P.TRIANGLE_STRIP);
        shape.noStroke();
        shape.fill(255);
//        shape.texture(stripTexture);
        for (int i = 0; i < length; i++) {
            shape.vertex(0, i, 0, textureUV_Y); // initial position doesn't matter - we'll overwrite in the draw loop
            shape.vertex(lineWeight, curY, 1, textureUV_Y);
            textureUV_Y += textureStepY;
        }
        shape.endShape();
    }

    public LineTrailCustom setLineWeight(float lineWeight) {
        this.lineWeight = lineWeight;
        return this;
    }
    
    public LineTrailCustom setGapDist(float gapDist) {
        this.gapDist = gapDist;
        return this;
    }
    
    public LineTrailCustom setTaperStart(float taperStart) {
        this.taperStart = taperStart;
        return this;
    }
    
    public void reset(float x, float y) {
        utilVec.set(x, y);
        reset(utilVec);
    }
    
    public void reset(PVector newPos) {
        for (int i = 0; i < length; i++) trail[i].set(newPos);
    }

    public void update(PGraphics pg, PVector newPos) {
        update(pg, newPos, NO_FILL, NO_FILL);
    }

    public void update(PGraphics pg, float x, float y, int colorStart, int colorEnd) {
        utilVec.set(x, y);
        update(pg, utilVec, colorStart, colorEnd);
    }
    
    public void update(PGraphics pg, PVector newPos, int colorStart, int colorEnd) {
        // check new dir with last dir
        PVector lastPos = trail[0];
        if(lastPos.dist(newPos) > 0) {
            newSegmentDir = MathUtil.getRadiansToTarget(newPos.x, newPos.y, lastPos.x, lastPos.y);
        }
        
        // copy all positions towards tail end each step
        for (int i = length - 1; i > 0; i--) {
            trail[i].set(trail[i-1]);
        }
        trail[0].set(newPos);

        // prep context for immediate drawing, not using cached shape
        // until the Processing bug gets addressed
        pg.push();
        pg.noStroke();
        PG.setDrawFlat2d(pg, true);
        pg.blendMode(PBlendModes.BLEND);
        pg.beginShape(P.TRIANGLE_STRIP);

        
        // update tail vertices
        // start with current direction
        float segmentDir = newSegmentDir;
        boolean isDrawing = true;
        for (int i = length - 1; i > 0; i--) {
//      for (int i = 0; i < length; i++) {
            float segmentDirLast = segmentDir;
            // get direction to prev segment after first, which is lastest input
            if(i > 0) {
                segmentDir = MathUtil.getRadiansToTarget(trail[i-1].x, trail[i-1].y, trail[i].x, trail[i].y);
            }
            
            // color gradient calc
            float progress = (float) i / (float) length;
            int curColor = P.p.lerpColor(colorStart, colorEnd, progress);
            
            // taper trail towards the end
            float curW = lineWeight;
            if(progress > taperStart) {
                curW *= P.map(progress, taperStart, 1f, 1f, 0);
                if(curW < 0) curW = 0;
            }
            
            // check distance between point for potential gap
            boolean shouldDraw = true;
            if(i > 0 && i < length - 1) {
                if(trail[i].dist(trail[i-1]) > gapDist || trail[i].dist(trail[i+1]) > gapDist) {
                    shouldDraw = false;
                }
            }
            // get rect points from tail segment vectors
            float leftXCur = trail[i].x + curW * P.cos(segmentDir - P.HALF_PI);
            float leftYCur = trail[i].y + curW * P.sin(segmentDir - P.HALF_PI);
            float rightXCur = trail[i].x + curW * P.cos(segmentDir + P.HALF_PI);
            float rightYCur = trail[i].y + curW * P.sin(segmentDir + P.HALF_PI);
            
            // calculate triangle strip vertices - 2 per tail segment
            float vertX = leftXCur;
            float vertY = leftYCur;
            float vertZ = -progress * 5f;
            float vertXNext = rightXCur;
            float vertYNext = rightYCur;
            float vertZNext = -progress * 5f;
            
            
            // end & restart line segments when gap is too far
            if(isDrawing && !shouldDraw) {
                // end shape
                isDrawing = false;
                pg.endShape();
                
                // draw half circle at end of line segment
                pg.push();
                pg.fill(curColor);
                pg.translate(trail[i+1].x, trail[i+1].y, vertZ);
                float circleRot = segmentDirLast + P.HALF_PI; // + MathUtil.getRadiansToTarget(trail[i-1].x, trail[i-1].y, trail[i].x, trail[i].y);
                pg.arc(0, 0, curW * 2, curW * 2, circleRot, circleRot + P.PI);
                pg.pop();

            } else if(!isDrawing && shouldDraw) {
                // draw half circle at beginning of new line
                pg.push();
                pg.fill(curColor);
                pg.translate(trail[i].x, trail[i].y, vertZ);
                float circleRot = segmentDir - P.HALF_PI; // + MathUtil.getRadiansToTarget(trail[i-1].x, trail[i-1].y, trail[i].x, trail[i].y);
                pg.arc(0, 0, curW * 2, curW * 2, circleRot, circleRot + P.PI);
                pg.pop();
                
                // start shape
                isDrawing = true;
                pg.beginShape(P.TRIANGLE_STRIP);
            }
            
            // update cached PShape
            // however it seems that there's a bug and we can't update colors more than once
            /*
            shape.setFill(vertIndex, curColor);
            shape.setFill(vertIndex+1, curColor);
            shape.setVertex(vertIndex, vertX, vertY, vertZ);
            shape.setVertex(vertIndex+1, vertXNext, vertYNext, vertZNext);
            */
            
            // redraw vertices manually
            if(shouldDraw) {
    //            shape.texture(stripTexture);
                pg.fill(curColor);
                pg.vertex(vertX, vertY, vertZ, 0, progress);
                pg.vertex(vertXNext, vertYNext, vertZNext, 1, progress);
            } 
        }
        
        // close context for immediate drawing
        // (not using cached shape)
        if(isDrawing) {
            pg.endShape();
        }
        
        // optionally draw circular "head"
        // it's okay to be fully opaque for this circle only
        // TODO: but really it should be underneath the triangle strip!
        //       ... arc() mostly works, but doesn't when standing still
        PVector headPos = trail[1];
        float headDir = segmentDir + P.HALF_PI;
        pg.push();
        PG.setDrawCenter(pg);
        pg.fill(colorStart);
        pg.translate(headPos.x, headPos.y);
        pg.ellipse(0, 0, lineWeight * 2, lineWeight * 2);
//        pg.arc(0, 0, lineWeight * 2, lineWeight * 2, headDir, headDir + P.PI);
        pg.pop();
        
        // draw PShape
        // pg.shape(shape);
        
        pg.pop();
    }
    
    ////////////////////////////////
    // Smoothing
    ////////////////////////////////

    public void smoothLine() {
        smoothLine(0.05f, 0.75f);
    }
    
    public void smoothLine(float smoothAmp, float smoothReduceAmp) {
        for (int i = 1; i < length - 2; i++) {
            boolean shouldLerp = true;
            
            // get each point's prev/next segments
            PVector prevSegment = trail[i-1]; 
            PVector curSegment = trail[i];
            PVector nexSegment = trail[i+1];
            
            // don't lerp between disconnected line segments
            if(trail[i].dist(trail[i-1]) > gapDist || trail[i].dist(trail[i+1]) > gapDist) {
                shouldLerp = false;
            }
            
            if(shouldLerp) {
    
                // find midpoint between segments
                utilVec.set(prevSegment);
                utilVec.lerp(nexSegment, 0.5f);
    
                // lerp towards midpoint by some factor
                float progress = (float) i / length;
                float smoothLerp = smoothAmp * (1f - progress * smoothReduceAmp); // reduce smoothing towards end of line
                curSegment.lerp(utilVec, smoothLerp);
            }
        }
    }
}
