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
  
  public StringRandomCharAnim(String text) {
      this(text, 1);
  }
  
  public StringRandomCharAnim(String text, int advanceInterval) {
      this(text, advanceInterval, 100);
  }
  
  public StringRandomCharAnim(String text, int advanceInterval, int maxFrames) {
    this.text = text;
    this.curText = text;
    this.advanceInterval = advanceInterval;
    this.maxFrames = maxFrames;
    curTextArray = curText.toCharArray();
    letterFrames = new int[text.length()];
    restart();
  }
  
  public StringRandomCharAnim setAdvanceInterval(int advanceInterval) {
    this.advanceInterval = advanceInterval;
    return this;
  }

  public StringRandomCharAnim setMaxFrames(int maxFrames) {
    this.maxFrames = maxFrames;
    return this;
  }

  protected void randomizeLetterFrames() {
    // build array of frameCounts when we'll stop setting random characters
    for (int i = 0; i < text.length(); i++) {
      letterFrames[i] = MathUtil.randRange(maxFrames / 10, maxFrames);
    }
  }
  
  public StringRandomCharAnim resetText(String newText) {
    if(text.length() != newText.length()) { // if new text is different length, create a new char array
      letterFrames = new int[newText.length()];
      curTextArray = newText.toCharArray();
    }
    text = newText;
    restart();
    return this;
  }
  
  public StringRandomCharAnim restart() {
    curFrame = 0;
    randomizeLetterFrames();
    updateCharacters();
    return this;
  }
  
  public StringRandomCharAnim update() {
    if(FrameLoop.frameModLooped(advanceInterval)) {
      curFrame++;
      updateCharacters();
    }
    return this;
  }

  protected StringRandomCharAnim updateCharacters() {
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
    return this;
  }
  
  public String curString() {
    return curText;
  }
}