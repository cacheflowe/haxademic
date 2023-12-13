package com.haxademic.demo.draw.particle;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.AlphaStepFilter;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.DitherColorBandsPatricio;
import com.haxademic.core.draw.filters.pshader.EdgeColorFadeFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.MediaTimecodeTrigger;
import com.haxademic.core.media.MediaTimecodeTrigger.IMediaTimecodeTriggerDelegate;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;
import com.haxademic.core.render.Renderer;
import com.haxademic.core.render.VideoRenderer;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Demo_ParticleSystem_Custom_AudioRender
extends PAppletHax
implements IMediaTimecodeTriggerDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

  // buffers / layers
  protected PGraphics pgParticles;
  protected PGraphics pgText;
  protected PGraphics pgOut;

  // gradient colors
  protected int[] greens = new int[] {
      0xff95FF61, // lightest: green gecko
      0xff00FC00, // volt?
      0xff00C457, // Darker: pantone 345c
  };
  protected ImageGradient imageGradient;
  protected float gradientProgress = 0;

  // particle system
  @SuppressWarnings("rawtypes")
  protected ParticleSystem<ParticleCustom> particles;
  protected PImage[] particlesTex;
  protected int noiseSeed = 10;
  protected PVector launchPos = new PVector();
  protected float launchNoiseProgress = 0;
  protected int particleBlurIters = 10;

  // audio levels
  protected float audioLevel = 0;
  protected int numAvgs = 7;
  protected int particlesToLaunch = 0;
  protected FloatBuffer[] audioAvgs = new FloatBuffer[numAvgs];

  // title cards
  protected PVector titlePos = new PVector();
  protected float titlePosRads = 0;
  protected PImage[] titleImages;
  protected int curTitleIndex = 0;
  protected LinearFloat titleShowProgress = new LinearFloat(0, 0.01f);
  protected ArrayList<MediaTimecodeTrigger> timecodeTriggers;
  protected String AUDIO_UPDATE = "AUDIO_UPDATE";
  protected String AUDIO_RESTART = "AUDIO_RESTART";
  protected String SPEAKER_START = "SPEAKER_START";
  protected String SPEAKER_END = "SPEAKER_END";


  protected void config() {
    Config.setAppSize(1920, 1080);
    Config.setPgSize(3840, 2160);
    Config.setProperty(AppSettings.RENDERING_MOVIE, true);
    Config.setProperty(AppSettings.RENDER_SIMULATION, false);       // simulates the final render, and scrubs through audio to keep pace. annoying to listen to, but good for testing
    Config.setProperty(AppSettings.RENDER_AUDIO_SIMULATION, true); // untethers the audio from actual framerate. good for testing but not representative of final render if framerate is lagging
    Config.setProperty(AppSettings.RENDER_AUDIO_FILE, DemoAssets.audioBrimBeatPath); // -snippet
    VideoRenderer.setOutputImages();
    VideoRenderer.IMAGE_EXTENSION = "png";
  }

  @SuppressWarnings("rawtypes")
  protected void firstFrame() {
    // context
    pgParticles = PG.newPG(pg.width, pg.height, false, false);
    pgText = PG.newPG(pg.width, pg.height);
    pgOut = PG.newPG(1920, 1080); // attempt to fix render crashes. didn't fix it
    PG.setTextureRepeat(pgParticles, false);
    PG.setTextureRepeat(pgText, false);

    // rendering
    Renderer.instance().videoRenderer.setPG(pgOut);

    // audio
    AudioUtil.setPrimaryMixer();
    AudioIn.instance(AudioInputLibrary.ESS); // for real-time mic input?
    // AudioIn.instance();
    for (int i = 0; i < audioAvgs.length; i++)
      audioAvgs[i] = new FloatBuffer(3);

    // visual elements
    particlesTex = new PImage[] {
        DemoAssets.particleLight(),
        DemoAssets.particleMedium(),
        DemoAssets.particleHeavy(),
    };
    particles = new ParticleSystem<ParticleCustom>(ParticleCustom.class);
    buildGradient();
    p.noiseSeed(noiseSeed);

    // speaker titles & timecodes
    titlePos.set(pgText.width / 2f, pgText.height / 2f);
    float endFadeOut = 2;
    timecodeTriggers = new ArrayList<MediaTimecodeTrigger>();
    timecodeTriggers.add(new MediaTimecodeTrigger(AUDIO_UPDATE, 0 +   0f, AUDIO_RESTART, this));
    timecodeTriggers.add(new MediaTimecodeTrigger(AUDIO_UPDATE, 0 +   0f, SPEAKER_END, this));
    addTriggerEndAndStart(0, 7.4f);
    addTriggerEndAndStart(0, 25.8f);
    addTriggerEndAndStart(0, 41.4f);
    addTriggerEndAndStart(1, 1.2f);
    addTriggerEndAndStart(1, 17.4f);
    addTriggerEndAndStart(1, 40.6f);
    addTriggerEndAndStart(1, 48.5f);
    addTriggerEndAndStart(2, 0.3f);
    addTriggerEndAndStart(2, 9.8f);
    addTriggerEndAndStart(2, 24.1f);
    addTriggerEndAndStart(2, 36.5f);
    addTriggerEndAndStart(3, 1.1f);
    addTriggerEndAndStart(3, 17.7f);
    addTriggerEndAndStart(3, 25.5f);
    addTriggerEndAndStart(3, 32.4f);
    addTriggerEndAndStart(4, 10.4f);
    addTriggerEndAndStart(4, 19.7f);
    addTriggerEndAndStart(4, 34.9f);
    addTriggerEndAndStart(4, 54.5f);
    addTriggerEndAndStart(5, 10.4f);
    addTriggerEndAndStart(5, 25.7f);
    addTriggerEndAndStart(5, 54.3f);
    addTriggerEndAndStart(6, 5.6f);
    addTriggerEndAndStart(6, 27.8f);
    addTriggerEndAndStart(6, 37.4f);
    // maybe don't do last speaker end to let things trail off slower?
    timecodeTriggers.add(new MediaTimecodeTrigger(AUDIO_UPDATE, 7 * 60 + 1.6f - endFadeOut, SPEAKER_END, this));
  }

  protected void addTriggerEndAndStart(float minutes, float seconds) {
    float time = (minutes * 60) + seconds;
    float endFadeOut = 2.5f;
    timecodeTriggers.add(new MediaTimecodeTrigger(AUDIO_UPDATE, time - endFadeOut, SPEAKER_END, this));
    timecodeTriggers.add(new MediaTimecodeTrigger(AUDIO_UPDATE, time, SPEAKER_START, this));
  }

  protected void buildGradient() {
    // looping gradient texture
    PGraphics gradientTexture = Gradients.textureFromColorArray(512, 8, greens, true);
    DebugView.setTexture("gradient", gradientTexture);
    // gradient retriever
    imageGradient = new ImageGradient(gradientTexture);
  }

  public void keyPressed() {
    super.keyPressed();
    if (p.key == ' ') particles.killAll();
    if (p.key == 't') newTitlePos();
    if (p.key == 's') P.out("TIME", curAudioTime());
  }

  protected void drawApp() {
    p.background(0, 50, 0);

    // allow a reset
    // if (KeyboardState.keyTriggered(' ')) particles.killAll();

    // handle audio
    updateAudioInput();
    updateTimecodeTriggers();

    // draw buffers
    drawOverLayer();
    drawParticlesLayer();
    drawFinalComposite();

    // draw to screen
    PG.setDrawCorner(p.g);
    // p.image(pg, 0, 0);
    ImageUtil.cropFillCopyImage(pg, p.g, false);

    // debug info
    DebugView.setValue("particles.poolSize()", particles.poolSize());
    DebugView.setValue("particles.poolActiveSize()", particles.poolActiveSize());
  }

  ///////////////////////////////////////////////
  // AUDIO INPUT
  ///////////////////////////////////////////////

  protected void updateAudioInput() {
    // update audio averages
    int eqStartIndex = 20;
    float eqStep = 20;
    float totalAmp = 0;
    for (int i = 0; i < audioAvgs.length; i++) {
      int eqIndex = P.floor(eqStartIndex + (i * eqStep));
      float eq = AudioIn.audioFreq(eqIndex);
      eq = P.constrain(eq, 0, 0.2f); // tends to be the range of the frequencies we're looking at (20-160) / 512
      eq *= 7f;
      audioAvgs[i].update(eq);
      totalAmp += audioAvgs[i].average();
    }

    // update audio level avg of avgs
    audioLevel = totalAmp / audioAvgs.length;
  }

  protected float curAudioTime() {
    return Renderer.instance().videoRenderer.audioPosition();
  }

  protected void updateTimecodeTriggers() {
    for (int i = 0; i < timecodeTriggers.size(); i++) {
      timecodeTriggers.get(i).update(AUDIO_UPDATE, curAudioTime());
    }
    // titleShowProgress.setInc(0.01f);
    titleShowProgress.update();
  }

  ///////////////////////////////////////////////
  // DRAW TEXT & EQ LAYER
  ///////////////////////////////////////////////

  protected void drawOverLayer() {
    pgText.beginDraw();
    pgText.background(0, 0);
    drawCurTitle();
    postFxOverLayer();
    pgText.endDraw();
  }

  protected void drawCurTitle() {
    if(titleShowProgress.value() == 0) return;
    if(curTitleIndex < 0) return;
    
    // draw current text card
    // with centered audio bars
    pgText.push();
    pgText.translate(titlePos.x, titlePos.y);

    // draw debug box
    pgText.push();
    pgText.noFill();
    pgText.fill(255, 0, 0);
    PG.setDrawCenter(pgText);
    // pgText.rect(0,0, titleImages[curTitleIndex].width, titleImages[curTitleIndex].height + 140);
    pgText.pop();

    // draw title elements
    PG.setDrawCorner(pgText);
    drawEqBars();
    PG.setDrawCenter(pgText);
    pgText.image(titleImages[curTitleIndex], 0, 60);
    pgText.pop();
  }

  protected void drawEqBars() {
    pgText.push();
    pgText.noStroke();
    float barW = 9;
    float barH = 6;
    float barSpacing = 25;
    float totalW = barSpacing * numAvgs;
    float offsetX = -totalW / 2;
    float offsetY = -130;
    for (int i = 0; i < numAvgs; i++) {
      float eq = audioAvgs[i].average();
      float curBarH = 10 + barH * eq * 10f;
      pgText.fill(0);
      pgText.rect(offsetX + i * barSpacing, offsetY + -curBarH / 2f, barW, curBarH, barW/2f);
    }
    pgText.pop();
  }

  protected void postFxOverLayer() {
    // alpha fade
    float alphaStep = P.map(titleShowProgress.value(), 0, 1, -1f, 0);
    AlphaStepFilter.instance().setAlphaStep(alphaStep);
    AlphaStepFilter.instance().applyTo(pgText);

    // blur
    float blurSize = P.map(titleShowProgress.value(), 0, 1, 50, 0);
    float blurSigma = P.map(titleShowProgress.value(), 0, 1, 15, 0);
    if(blurSize > 0 && blurSigma > 0) {
      BlurProcessingFilter.instance().setBlurSize((int) blurSize);
      BlurProcessingFilter.instance().setSigma(blurSigma);
      BlurProcessingFilter.instance().applyTo(pgText);
    }
    
  }

  //////////////////////////////////////////////////////
  // Debug info
  //////////////////////////////////////////////////////

  protected void drawDebugLayer() {
    PFont font = FontCacher.getFont(DemoAssets.fontMonospacePath, 34);
    FontCacher.setFontOnContext(pg, font, p.color(0), 1.3f, PTextAlign.LEFT, PTextAlign.BOTTOM);
    pg.text(
        "curTitleIndex: " + curTitleIndex + FileUtil.NEWLINE +
        "audioPosition(): " + curAudioTime() + FileUtil.NEWLINE +
        "particlesToLaunch: " + particlesToLaunch + FileUtil.NEWLINE +
        "audioLevel: " + audioLevel + FileUtil.NEWLINE + "",
        100, pg.height - 20);

    // draw audio bar
    pg.push();
    pg.fill(0);
    pg.rect(100, pg.height - 40, audioLevel * 500f, 10);
    pg.pop();
  }

  ///////////////////////////////////////////////
  // VISUAL COMPOSITE
  ///////////////////////////////////////////////

  protected void drawFinalComposite() {
    pg.beginDraw();
    pg.image(pgParticles, 0, 0);
    DitherColorBandsPatricio.instance().setAmp(0.2f);
    DitherColorBandsPatricio.instance().applyTo(pg);
    pg.image(pgText, 0, 0);
    // drawDebugLayer();
    pg.endDraw();
    ImageUtil.copyImage(pg, pgOut);
  }

  ///////////////////////////////////////////////
  // DRAW PARTICLES LAYER
  ///////////////////////////////////////////////

  protected void drawParticlesLayer() {
    pgParticles.beginDraw();
    if (p.frameCount < 10) pgParticles.background(255);
    PG.setDrawFlat2d(pgParticles, true); // fixes slight z-movement to keep
                                         // stacking order

    PG.setDrawCenter(pgParticles);
    launchParticles();
    preProcess();
    particles.sortParticlesByAge();
    particles.updateAndDrawParticles(pgParticles, PBlendModes.BLEND);
    {
      // debug circle to show launch point
      pgParticles.fill(255);
      pgParticles.noStroke();
      // pgParticles.circle(launchPos.x, launchPos.y, 10);
    }
    PG.setDrawCorner(pgParticles);
    pgParticles.endDraw();
  }

  protected void preProcess() {
    // apply fx
    BlurProcessingFilter.instance().setSigma(40);
    BlurProcessingFilter.instance().setBlurSize(30);
    for (int i = 0; i < particleBlurIters; i++) {
      BlurProcessingFilter.instance().applyTo(pgParticles);
    }

    float brightFade = P.map(titleShowProgress.value(), 0, 1, 10f / 255, 2f / 255);
    BrightnessStepFilter.instance().setBrightnessStep(brightFade);
    BrightnessStepFilter.instance().applyTo(pgParticles);

    EdgeColorFadeFilter.instance().setEdgeColor(1f, 1f, 1f);
    EdgeColorFadeFilter.instance().setSpreadX(0.025f);
    EdgeColorFadeFilter.instance().setSpreadY(0.05f);
    // EdgeColorFadeFilter.instance().applyTo(pgParticles);
  }

  //////////////////////////////////////////////////////
  // IMediaTimecodeTriggerDelegate callback
  //////////////////////////////////////////////////////

  public void mediaTimecodeTriggered(String mediaId, float time, String action) {
    P.out(mediaId, time, action);
    if(action == AUDIO_RESTART) {
      curTitleIndex = -1;
      gradientProgress = 0;
      launchNoiseProgress = 0;
      titleShowProgress.setCurrent(0).setTarget(0);
    } else if(action == SPEAKER_START) {
      speakerStart();
    } else if(action == SPEAKER_END) {
      titleShowProgress.setTarget(0);
    }
  }

  protected void newTitlePos() {
    // move noise forward until we find a decent new title location
    int attempts = 0;
    float minTitleSpacing = pg.height * 0.3f;
    while(attempts < 1000 && titlePos.dist(launchPos) < minTitleSpacing) {
      launchNoiseProgress += 0.1f;
      float noiseX = p.noise(launchNoiseProgress);
      float noiseY = p.noise(launchNoiseProgress + 1000f);
      float launchX = P.map(noiseX, 0, 1, pg.width * 0.2f, pg.width * 0.8f);
      float launchY = P.map(noiseY, 0, 1, pg.height * 0.2f, pg.height * 0.8f);
      launchPos.set(launchX, launchY);
      // P.out((int) launchX, (int) launchY);
      attempts++;
    }
    P.out("attempts:", attempts);
  }

  protected void speakerStart() {
    curTitleIndex++;
    curTitleIndex = curTitleIndex % titleImages.length;
    titleShowProgress.setTarget(1);

    // jump launch pos forward
    // launchNoiseProgress += 1000;
    newTitlePos();
    launchParticles(); // updates launchPos, which gets set next
    titlePos.set(launchPos);

    // launch some random particles around the launch point
    for (int i = 0; i < 5; i++) {
      float curX = titlePos.x;
      float curY = titlePos.y;

      float offsetDir = p.random(P.TWO_PI);
      float offsetAmp = p.random(pg.height * 0.2f, pg.height * 0.4f);
      float offsetX = P.cos(offsetDir) * offsetAmp;
      float offsetY = P.sin(offsetDir) * offsetAmp * 1.4f;
      float particleSizeSm = pg.height * 0.25f;
      float particleSizeLg = pg.height * 0.75f;

      // launch particle
      ParticleCustom particle = (ParticleCustom) particles.launchParticle(curX + offsetX, curY + offsetY, 0);
      particle
          .setSpeedRange(0, 0, 0, 0, -0.002f, -0.002f)
          .setAcceleration(0.99f, 0.99f, 1)
          .setGravityRange(0, 0, 0, 0, 0, 0)
          .setLifespanRange(200, 300)
          .setSizeRange(particleSizeSm, particleSizeLg)
          .setColor(imageGradient.getColorAtProgress(gradientProgress % 1f))
          .setImage(particlesTex[MathUtil.randIndex(particlesTex.length)])
          .randomize();
    }
  }

  @SuppressWarnings("rawtypes")
  protected void launchParticles() {
    if(curAudioTime() < 2) return;

    // particle launch position
    float noiseSpeed = 0.015f;
    launchNoiseProgress += noiseSpeed * audioLevel * 1f;
    float noiseX = p.noise(launchNoiseProgress);
    float noiseY = p.noise(launchNoiseProgress + 1000f);
    float launchX = P.map(noiseX, 0, 1, pg.width * 0.2f, pg.width * 0.8f);
    float launchY = P.map(noiseY, 0, 1, pg.height * 0.2f, pg.height * 0.8f);
    launchPos.set(launchX, launchY);

    // particle props/ranges
    float particleSizeSm = pg.height * 0.25f;
    float particleSizeLg = pg.height * 0.5f;
    float lifespanCurve = (titleShowProgress.target() == 1) ? 1 : titleShowProgress.value();  // fade down, but immdiately fade up
    float lifespanSm = P.map(lifespanCurve, 0, 1, 40, 100);
    float lifespanLg = P.map(lifespanCurve, 0, 1, 90, 220);

    float gravityDir = p.random(P.TWO_PI);
    float gravAmp = 0.2f;
    float gravityX = P.cos(gravityDir) * gravAmp * 1.5f;
    float gravityY = P.sin(gravityDir) * gravAmp;

    float speedDir = p.random(P.TWO_PI);
    float speedAmp = 0.3f * audioLevel * 4f;
    float speedX = P.cos(speedDir) * speedAmp;
    float speedY = P.sin(speedDir) * speedAmp * 1.4f;

    gradientProgress += audioLevel * 0.007f;

    // audio amp determine how many particles to launch
    // int audioAmp = (int) (AudioIn.amplitude() * 300);
    particlesToLaunch = P.round(audioLevel * 6f);
    if(particlesToLaunch > 4) particlesToLaunch = 4; 

    for (int i = 0; i < particlesToLaunch; i++) {
      // every other particle is launched from the title, vesus the wandering launch point
      float curX = (i%2 == 0)  ? launchX : titlePos.x;
      float curY = (i%2 == 0)  ? launchY : titlePos.y;

      // add some random offset to the launch point
      if(i%3 == 2) {
        float offsetDir = p.random(P.TWO_PI);
        float offsetAmp = p.random(pg.height * 0.1f, pg.height * 0.2f);
        float offsetX = P.cos(offsetDir) * offsetAmp;
        float offsetY = P.sin(offsetDir) * offsetAmp * 1.4f;
        curX += offsetX;
        curY += offsetY;
      }

      // launch particle
      ParticleCustom particle = (ParticleCustom) particles.launchParticle(curX, curY, 0);
      particle
          .setSpeedRange(0, speedX, 0, speedY, -0.002f, -0.002f)
          .setAcceleration(0.99f, 0.99f, 1)
          .setGravityRange(0, gravityX, 0, gravityY, 0, 0)
          .setRotationRange(0, 0, 0, 0, 0, 0)
          .setLifespanRange(lifespanSm, lifespanLg)
          .setLifespanSustain(0)
          .setSizeRange(particleSizeSm, particleSizeLg)
          // .setColor(imageGradient.getColorAtProgress((audioLevel * 15f) %
          // .setColor(imageGradient.getColorAtProgress(FrameLoop.count(0.004f) % 1f))
          .setColor(imageGradient.getColorAtProgress(gradientProgress % 1f))
          // 1f))
          .setImage(particlesTex[MathUtil.randIndex(particlesTex.length)])
          .randomize();
    }
  }

  //////////////////////////////////////
  // Custom particle
  // Constructor can't be passed any params, for generic instantiation.
  // NEEDS TO BE A STATIC CLASS if nested in another class,
  // because inner classes don't work with generic instantiation:
  // https://stackoverflow.com/a/17485341
  //////////////////////////////////////

  public static class ParticleCustom<T>
      extends Particle {

    public ParticleCustom() {
      super();
    }

    protected void drawParticle(PGraphics pg) {
      // size tweaks based on lifespan progress...
      boolean scalingUp = (lifespanProgress.target() == 1);
      // scale up, but alpha fade out instead of scale down
      float curSize = (scalingUp) ? 
          size * Penner.easeOutExpo(lifespanProgress.value()) : 
          size;
      
      // temp: override to keep full scale, and fade alpha instead
      curSize = size;
      // grow to 2x size by end of particle, slowly
      curSize *= (1f + ageProgress()); 
      // curSize = size * Penner.easeOutCirc(lifespanProgress.value());
      float alpha = (scalingUp) ? 
          255 * lifespanProgress.value() * 5 : // faster fade-in
          255 * lifespanProgress.value();
      alpha = P.constrain(alpha, 0, 255);

      // draw different types of shapes
      pg.tint(color, alpha);
      pg.image(image, 0, 0, curSize, curSize);
      pg.tint(255);
    }
  }
}