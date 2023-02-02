package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class Stopwatch {

    protected int totalMs;
    protected int startTime;
    protected boolean isRunning = false;
    
    public Stopwatch() {
        totalMs = 0;
    }
    
    public void reset() {
        totalMs = 0;
        startTime = P.p.millis();
    }
    
    public void start() {
        if(isRunning) return;
        isRunning = true;
        startTime = P.p.millis();
    }
    
    public int stop() {
        if(!isRunning) return totalMs;
        isRunning = false;
        totalMs += P.p.millis() - startTime;
        return totalMs;
    }
    
    public int totalMs() {
        if(isRunning) {
            return totalMs + P.p.millis() - startTime;
        } else {
            return totalMs;
        }
    }
    
    public float totalHours() {
        return MathUtil.roundToPrecision(DateUtil.msToHours(totalMs()), 3);
    }
    
}
