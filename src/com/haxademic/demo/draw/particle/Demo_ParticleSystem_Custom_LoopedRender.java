package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.DitherColorBandsPatricio;
import com.haxademic.core.draw.filters.pshader.EdgeColorFadeFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.render.Renderer;
import com.haxademic.core.render.VideoRenderer;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ParticleSystem_Custom_LoopedRender
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

  // buffers / layers
  protected PGraphics pgParticles;

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
  protected int noiseSeed = 20;


  protected void config() {
    int frames = 60 * 90;  // 90 second loop
    Config.setAppSize(1920, 1080);
    Config.setPgSize(3840, 2160);
    Config.setProperty(AppSettings.LOOP_FRAMES, frames); 
    Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, P.round(1 + frames * 1));
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(1 + frames * 2));
    VideoRenderer.setOutputImages();
    VideoRenderer.IMAGE_EXTENSION = "jpg";
  }

  @SuppressWarnings("rawtypes")
  protected void firstFrame() {
    // context
    pgParticles = PG.newPG(pg.width, pg.height, false, false);
    PG.setTextureRepeat(pgParticles, false);

    // rendering
    // Renderer.instance().videoRenderer.setPG(pg);

    // visual elements
    particlesTex = new PImage[] {
        DemoAssets.particleLight(),
        DemoAssets.particleMedium(),
        DemoAssets.particleHeavy(),
    };
    particles = new ParticleSystem<ParticleCustom>(ParticleCustom.class);
    buildGradient();
    p.noiseSeed(noiseSeed);
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
  }

  protected void drawApp() {
    p.background(0, 50, 0);

    // allow a reset
    // if (KeyboardState.keyTriggered(' ')) particles.killAll();

    // draw buffers
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
  // VISUAL COMPOSITE
  ///////////////////////////////////////////////

  protected void drawFinalComposite() {
    pg.beginDraw();
    pg.image(pgParticles, 0, 0);
    DitherColorBandsPatricio.instance().setAmp(0.2f);
    DitherColorBandsPatricio.instance().applyTo(pg);
    // drawDebugLayer();
    pg.endDraw();
  }

  ///////////////////////////////////////////////
  // DRAW PARTICLES LAYER
  ///////////////////////////////////////////////

  protected void drawParticlesLayer() {
    pgParticles.beginDraw();
    if (p.frameCount < 10) pgParticles.background(255);
    PG.setDrawFlat2d(pgParticles, true); // fixes slight z-movement to keep stacking order

    PG.setDrawCenter(pgParticles);
    launchParticles();
    preProcess();
    particles.sortParticlesByAge();
    particles.updateAndDrawParticles(pgParticles, PBlendModes.BLEND);
    PG.setDrawCorner(pgParticles);
    pgParticles.endDraw();
  }

  protected void preProcess() {
    // apply fx
    BlurProcessingFilter.instance().setSigma(40);
    BlurProcessingFilter.instance().setBlurSize(30);
    int blurIters = 10;
    for (int i = 0; i < blurIters; i++) {
      BlurProcessingFilter.instance().applyTo(pgParticles);
    }

    float brightFade = 3f / 255f;
    BrightnessStepFilter.instance().setBrightnessStep(brightFade);
    BrightnessStepFilter.instance().applyTo(pgParticles);

    EdgeColorFadeFilter.instance().setEdgeColor(1f, 1f, 1f);
    EdgeColorFadeFilter.instance().setSpreadX(0.025f);
    EdgeColorFadeFilter.instance().setSpreadY(0.05f);
    // EdgeColorFadeFilter.instance().applyTo(pgParticles);
  }

  @SuppressWarnings("rawtypes")
  protected void launchParticles() {
    
    // audio amp determine how many particles to launch
    // int audioAmp = (int) (AudioIn.amplitude() * 300);
    int particlesToLaunch = 1; 
    
    for (int i = 0; i < particlesToLaunch; i++) {
      
      // particle launch position
      float noiseX = FrameLoop.noiseLoop(0.8f, 200 + i*2000, 0, 4f);
      float noiseY = FrameLoop.noiseLoop(0.8f, 1000 + i*2000, 0, 4f);
      float launchX = P.map(noiseX, 0, 1, pg.width * 0.2f, pg.width * 0.8f);
      float launchY = P.map(noiseY, 0, 1, pg.height * 0.0f, pg.height * 1.0f);
  
      // particle props/ranges
      float particleSize = pg.height * 0.25f;
      float lifespan = 420;
      lifespan = 360 + 60f * P.sin(5f * FrameLoop.progressRads()); // override to create more whitespace
      DebugView.setValue("lifespan", lifespan);
  
      float gravityDir = noiseX * P.TWO_PI * 40f;
      float gravAmp = 0.025f;
      float gravityX = P.cos(gravityDir) * gravAmp * 1.5f;
      float gravityY = P.sin(gravityDir) * gravAmp;
  
      float speedDir = noiseY * P.TWO_PI * 40f;
      float speedAmp = 0.04f * noiseX;
      float speedX = P.cos(speedDir) * speedAmp;
      float speedY = P.sin(speedDir) * speedAmp * 1.4f;
  
      int particleIndex = P.round(noiseX * 50f) % (particlesTex.length - 1);

      // every other particle is launched from the title, vesus the wandering launch point
      float curX = launchX;
      float curY = launchY;

      float gradientLoops = 4;

      // launch particle
      ParticleCustom particle = (ParticleCustom) particles.launchParticle(curX, curY, -0.01f * i);
      particle
          .setSpeedRange(0, speedX, 0, speedY, -0.003f, -0.003f)
          .setAcceleration(0.995f, 0.995f, 1)
          .setGravityRange(gravityX, gravityX, gravityY, gravityY, 0, 0)
          .setRotationRange(0, 0, 0, 0, 0, 0)
          .setLifespan(lifespan)
          .setLifespanSustain(0)
          .setSize(particleSize)
          .setColor(imageGradient.getColorAtProgress((FrameLoop.progress() * gradientLoops + i/3f) % 1f))
          .setImage(particlesTex[particleIndex])
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
      curSize *= (1f + ageProgress() * 2); 
      // curSize = size * Penner.easeOutCirc(lifespanProgress.value());
      float alpha = (scalingUp) ? 
          255 * lifespanProgress.value() * 1 : // faster fade-in (not anymore for this version!)
          255 * lifespanProgress.value();
      alpha = P.constrain(alpha, 0, 255);

      // draw different types of shapes
      pg.tint(color, alpha);
      pg.image(image, 0, 0, curSize, curSize);
      pg.tint(255);
    }
  }
}