package com.haxademic.core.debug;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.text.StringUtil;

import processing.core.PFont;
import processing.core.PGraphics;

public class TimePlot {

    protected PGraphics buffer;
    protected float minVal;
    protected float maxVal;
    protected float lastPlotY;
    protected boolean started = false;;
    protected boolean clearDirty = false;;

    public TimePlot(int w, int h, float minVal, float maxVal) {
        this.minVal = minVal;
        this.maxVal = maxVal;
        buildBuffer(w, h);
    }
    
    protected void buildBuffer(int w, int h) {
        buffer = PG.newPG(w, h);
        buffer.beginDraw();
        buffer.background(0);
        buffer.endDraw();
    }
    
    public PGraphics image() {
        return buffer;
    }
    
    public void clear() {
        clearDirty = true;
    }
    
    public void update(float newVal) {
        // start context
        buffer.beginDraw();
        if(clearDirty) {
            buffer.background(0);
            clearDirty = false;
        }

        // scroll left
        buffer.copy(0, 0, buffer.width, buffer.height, -1, 0, buffer.width, buffer.height);
        
        // map incoming value to val range
        float plotX = buffer.width - 5;
        float plotY = P.map(newVal, minVal, maxVal, buffer.height, 0);
        plotY = P.constrain(plotY, 2, buffer.height - 2);
        if(!started) lastPlotY = plotY; // make sure first value doesn't jump
        buffer.strokeWeight(2);
        buffer.stroke(0, 255, 0);
        buffer.line(plotX - 1, lastPlotY, plotX, plotY);
        lastPlotY = plotY;
        
        // draw y-axis lines
        buffer.stroke(0, 0);
        buffer.noStroke(); 
        buffer.fill(0, 0, 255); 
        buffer.rect(0, buffer.height * 0, buffer.width, 2);
        buffer.rect(0, buffer.height * 0.25f - 1, buffer.width, 1);
        buffer.rect(0, buffer.height * 0.5f - 2, buffer.width, 2);
        buffer.rect(0, buffer.height * 0.75f - 1, buffer.width, 1);
        buffer.rect(0, buffer.height * 1 - 2, buffer.width, 2);
        
        // axis labels
        // background
        float labelBgW = 60;
        float labelBgH = 20;
        buffer.fill(0);
        buffer.rect(0, 2, labelBgW, labelBgH);
        buffer.rect(0, buffer.height/2 + -20, labelBgW, labelBgH - 2);
        buffer.rect(0, buffer.height/2 + 0, labelBgW, labelBgH);
        buffer.rect(0, buffer.height - 22, labelBgW, labelBgH);
        // text - axis vals first
        PFont font = FontCacher.getFont(DemoAssets.fontMonospacePath, 16);
        FontCacher.setFontOnContext(buffer, font, 0xffffffff, 1f, PTextAlign.LEFT, PTextAlign.TOP);
        DebugView.setValue("minVal", minVal);
        buffer.text(maxVal, 4, 3);
        buffer.text(printVal(P.lerp(minVal, maxVal, 0.5f)), 4, buffer.height/2 + 2);
        buffer.text(minVal, 4, buffer.height - 20);
        // current value in green
        FontCacher.setFontOnContext(buffer, font, 0xff00ff00, 1f, PTextAlign.LEFT, PTextAlign.TOP);
        buffer.text(printVal(newVal), 4, buffer.height/2 - 19);

        // done!
        buffer.endDraw();
        started = true;
    }
    
    protected String printVal(float val) {
        int precision = 3;
        boolean padRight = false;
        return StringUtil.roundToPrecision(val, precision, padRight);

    }

}