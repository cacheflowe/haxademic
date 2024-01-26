package com.haxademic.core.text;

import com.haxademic.core.math.MathUtil;
import com.haxademic.core.render.FrameLoop;

public class StringRandomCharAnim {
    
  protected int curFrame = 0;
  protected int advanceInterval;
  protected int maxFrames = 100;
  protected String text;
  protected String curText;
  protected char[] curTextArray;
  protected int[] letterFrames; // timing helper
  protected final String randChars="`1234567890-=~!@#$%^&*()_+{}[]:<,>.?/`";
  
  public StringRandomCharAnim(String text, int advanceInterval) {
    this.text = text;
    this.curText = text;
    this.advanceInterval = advanceInterval;
    curTextArray = curText.toCharArray();
    letterFrames = new int[text.length()];
    restart();
  }
  
  public void setAdvanceInterval(int advanceInterval) {
    this.advanceInterval = advanceInterval;
  }

  public void setMaxFrames(int maxFrames) {
    this.maxFrames = maxFrames;
  }

  protected void randomizeLetterFrames() {
    // build array of frameCounts when we'll stop setting random characters
    for (int i = 0; i < text.length(); i++) {
      letterFrames[i] = MathUtil.randRange(maxFrames / 10, maxFrames);
    }
  }
  
  public void resetText(String newText) {
    if(text.length() != newText.length()) { // if new text is different length, create a new char array
      letterFrames = new int[newText.length()];
      curTextArray = newText.toCharArray();
    }
    text = newText;
    restart();
  }
  
  public void restart() {
    curFrame = 0;
    randomizeLetterFrames();
    updateCharacters();
  }
  
  public void update() {
    if(FrameLoop.frameModLooped(advanceInterval)) {
      curFrame++;
      updateCharacters();
    }
  }

  protected void updateCharacters() {
    // loop through characters array, choosing a random character until
    // framecount has passed the random frame to stop randomizing
    for (int i = 0; i < text.length(); i++) {
      if (curFrame < letterFrames[i]) {
        int randIndex = MathUtil.randIndex(randChars.length());
        curTextArray[i] = randChars.charAt(randIndex);
      } else {
        curTextArray[i] = text.charAt(i);
      }
    }

    curText = String.valueOf(curTextArray);
  }
  
  public String curString() {
    return curText;
  }
}