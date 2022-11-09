package com.haxademic.core.draw.shapes;

import com.haxademic.core.app.P;

import processing.core.PGraphics;
import processing.core.PVector;

public class LineTrail {

    protected int size; 
    protected int NO_FILL = -99; 
    protected PVector[] trail; 
    protected PVector utilVec = new PVector(); 

    public LineTrail(int size) {
        this.size = size;
        trail = new PVector[size];
        for (int i = 0; i < size; i++) trail[i] = new PVector();
    }

    public void reset(float x, float y) {
        utilVec.set(x, y);
        reset(utilVec);
    }
    
    public void reset(PVector newPos) {
        for (int i = 0; i < size; i++) trail[i].set(newPos);
    }

    public void update(PGraphics pg, PVector newPos) {
        update(pg, newPos, NO_FILL, NO_FILL);
    }

    public void update(PGraphics pg, float x, float y, int colorStart, int colorEnd) {
        utilVec.set(x, y);
        update(pg, utilVec, colorStart, colorEnd);
    }
    
    public void update(PGraphics pg, PVector newPos, int colorStart, int colorEnd) {
        // copy all positions towards tail end each step
        for (int i = size - 1; i > 0; i--) {
            trail[i].set(trail[i-1]);
        }
        trail[0].set(newPos);

        // render
        pg.beginShape();
        for (int i = 0; i < size - 1; i++) {
            PVector curSegment = trail[i]; 
            PVector nexSegment = trail[i+1]; 
            if(curSegment.dist(nexSegment) != 0) {
                if(colorStart != NO_FILL) {
                    float progress = (float) i / (float) size;
                    pg.stroke(P.p.lerpColor(colorStart, colorEnd, progress));
                }
                pg.vertex(curSegment.x, curSegment.y,  curSegment.z);
            }
        }
        pg.endShape();
    }

    public void smoothLine() {
        smoothLine(0.05f, 0.75f);
    }
    
    public void smoothLine(float smoothAmp, float smoothReduceAmp) {
        for (int i = 1; i < size - 2; i++) {
            // get each point's prev/next segments
            PVector prevSegment = trail[i-1]; 
            PVector curSegment = trail[i];
            PVector nexSegment = trail[i+1];

            // find midpoint between segments
            utilVec.set(prevSegment);
            utilVec.lerp(nexSegment, 0.5f);

            // lerp towards midpoint by some factor
            float progress = (float) i / size;
            float smoothLerp = smoothAmp * (1f - progress * smoothReduceAmp); // reduce smoothing towards end of line
            curSegment.lerp(utilVec, smoothLerp);
        }
    }
}
