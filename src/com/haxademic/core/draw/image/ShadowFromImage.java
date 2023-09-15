package com.haxademic.core.draw.image;

import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeOpaquePixelsFilter;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class ShadowFromImage {

  protected PGraphics shadow;
  protected int blurSize;
  protected float blurSigma;
  protected int blurSteps;
  protected float shadowRed = 0;
  protected float shadowGreen = 0;
  protected float shadowBlue = 0;
  protected float shadowAlpha;

  public ShadowFromImage(PImage img) {
    this(img, 20, 20, 10, 0.9f);
  }

  public ShadowFromImage(PImage img, int blurSize, float blurSigma, int blurSteps, float shadowAlpha) {
    shadow = PG.newPG(img.width * 2, img.height * 2);
    this.blurSize = blurSize;
    this.blurSigma = blurSigma;
    this.blurSteps = blurSteps;
    this.shadowAlpha = shadowAlpha;
    update(img);
  }

  // getters/setters
  // setters are chainable

  public ShadowFromImage blurSize(int blurSize) { this.blurSize = blurSize; return this; }
  public int blurSize() { return blurSize; }
  public ShadowFromImage blurSigma(float blurSigma) { this.blurSigma = blurSigma; return this; }
  public float blurSigma() { return blurSigma; }
  public ShadowFromImage blurSteps(int blurSteps) { this.blurSteps = blurSteps; return this; }
  public int blurSteps() { return blurSteps; }
  public ShadowFromImage shadowAlpha(float shadowAlpha) { this.shadowAlpha = shadowAlpha; return this; }
  public float shadowAlpha() { return shadowAlpha; }
  public ShadowFromImage shadowRed(float shadowRed) { this.shadowRed = shadowRed; return this; }
  public float shadowRed() { return shadowRed; }
  public ShadowFromImage shadowGreen(float shadowGreen) { this.shadowGreen = shadowGreen; return this; }
  public float shadowGreen() { return shadowGreen; }
  public ShadowFromImage shadowBlue(float shadowBlue) { this.shadowBlue = shadowBlue; return this; }
  public float shadowBlue() { return shadowBlue; }

  public PImage image() {
    return shadow;
  }

  public String debugText() {
    return  "Blur size: " + blurSize + FileUtil.NEWLINE +
            "Blur sigma: " + blurSigma + FileUtil.NEWLINE +
            "Blur steps: " + blurSteps + FileUtil.NEWLINE +
            "Shadow alpha: " + shadowAlpha;
  }

  // re-draw shadow if source image changes

  public void update(PImage img) {
    shadow.beginDraw();
    shadow.clear();
    PG.setDrawCenter(shadow);

    shadow.image(img, shadow.width/2, shadow.height/2);

    BlurProcessingFilter.instance().setBlurSize(blurSize);
    BlurProcessingFilter.instance().setSigma(blurSigma);
    for (int i = 0; i < blurSteps; i++) {
      BlurProcessingFilter.instance().applyTo(shadow);
    }

    ColorizeOpaquePixelsFilter.instance().setColor(shadowRed, shadowGreen, shadowBlue, shadowAlpha);
    ColorizeOpaquePixelsFilter.instance().applyTo(shadow);

    shadow.endDraw();
  }

}

