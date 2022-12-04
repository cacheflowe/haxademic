package com.haxademic.core.text;

import com.haxademic.core.app.P;

public class StringTypewriterAnim {
    
    protected int curCharIndex = 0;
    protected int curFrame = 0;
    protected int advanceInterval;
    protected String text;
    
    public StringTypewriterAnim(String text, int advanceInterval) {
        this.text = text;
        this.advanceInterval = advanceInterval;
    }
    
    public void setAdvanceInterval(int advanceInterval) {
        this.advanceInterval = advanceInterval;
    }
    
    public void addText(String newText) {
        text += newText;
    }
    
    public void resetText(String newText) {
        text = newText;
        restart();
    }
    
    public void update() {
        if(curFrame >= advanceInterval) {
            curFrame = 0;
            curCharIndex++;
            curCharIndex = P.min(curCharIndex, text.length()); 
        }
        curFrame++;
    }
    
    public void restart() {
        curCharIndex = 0;
        curFrame = 0;
    }
    
    public String curString() {
        return text.substring(0, curCharIndex);
    }
}