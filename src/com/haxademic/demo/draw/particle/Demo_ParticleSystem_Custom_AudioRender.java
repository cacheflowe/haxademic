package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.render.Renderer;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ParticleSystem_Custom_AudioRender
extends PAppletHax {
    public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

    // TODO
    // - Canvas/context
    //   - Split particles & text into own PG layers
    // - Tweak particle falloff and pre-effects
    //   - Voice amplitude should have effect on falloff, so falloff is slow while speaking, and dissipates quickly when not talking!
    // - Add font & custom text with audio bars component
    //   - Build a test trigger on an interval to launch text "particles" (maybe not actual Particle instances)
    // - Particle look
    //   - Attach green gradient position to voice amplitude
    // - Particle behavior/movement
    //   - Particles should launch within a radius from 
    //   - Particles should wander with noise field (prety zoomed-out)
    //   - Make particles size/speed responsive to canvas size
    // - Audio input smoothing
    //   - Better global Amplitude. Can we use an average of some FFT values to calculate a better (quickly) smoothed amplitude?
    //   - Find better averages to use for bars - might want to try different strategies here
    //
    //
    // - AUdio
    //   - Ambient soundbed
    //     - https://artlist.io/song/85720/vacuum
    //     - https://artlist.io/song/103533/particles
    //     - https://artlist.io/song/78409/breath-within
    //   - Sound file cleanup
    //     - https://podcast.adobe.com/enhance
    
    
    protected PGraphics pgText;
    
    protected int[] greens = new int[] {
        0xff95FF61, // lightest: green gecko
        0xff00FC00, // volt?
        0xff00C457, // Darker: pantone 345c
    };
    protected ImageGradient imageGradient;

    @SuppressWarnings("rawtypes")
    protected ParticleSystem<ParticleCustom> particles;

    protected void config() {
        Config.setAppSize(1920, 1080);
        Config.setPgSize(3840, 2160);
//        Config.setProperty( AppSettings.RENDERING_MOVIE, true );
//        Config.setProperty( AppSettings.RENDER_AUDIO_SIMULATION, true );
//        Config.setProperty( AppSettings.RENDER_AUDIO_FILE, P.path("haxademic/audio/cacheflowe_bigger_loop.wav") );
//        Config.setProperty( AppSettings.RENDER_AUDIO_FILE, P.path("audio/nike-women.wav") );
    }

    @SuppressWarnings("rawtypes")
    protected void firstFrame() {
        // context
        pgText = PG.newPG(pg.width, pg.height);
        Renderer.instance().videoRenderer.setPG(pg);
        
        // audio
        AudioUtil.setPrimaryMixer();
//        AudioIn.instance();
        AudioIn.instance(AudioInputLibrary.ESS); // for real-time mic input?

        // visual elements
        particles = new ParticleSystem<ParticleCustom>(ParticleCustom.class);
        buildGradient();
        
    }

    protected void buildGradient() {
        // looping gradient texture
        PGraphics gradientTexture = Gradients.textureFromColorArray(512, 8, greens, true); 
        DebugView.setTexture("gradient", gradientTexture);
        // gradient retriever
        imageGradient = new ImageGradient(gradientTexture);
    }

    protected void drawParticlesLayer() {
        pg.beginDraw();
        if(p.frameCount < 10) pg.background(0);
        PG.setDrawFlat2d(pg, true);
        PG.setDrawCenter(pg);
        drawParticles();
        preProcess();
        particles.updateAndDrawParticles(pg, PBlendModes.BLEND);
        pg.endDraw();    
    }
    
    protected void drawApp() {
        background(0);

        // allow a reset
        if(KeyboardState.keyTriggered(' ')) particles.killAll();

        // draw buffers
        drawParticlesLayer();

        // draw to screen
        p.image(pg, 0, 0);
        p.image(pgText, 0, 0);
        
        drawEqBars();

        // debug info
        DebugView.setValue("particles.poolSize()", particles.poolSize());
        DebugView.setValue("particles.poolActiveSize()", particles.poolActiveSize());
    }

    protected void preProcess() {
        BlurProcessingFilter.instance().setSigma(20);
        BlurProcessingFilter.instance().setBlurSize(20);
        BlurProcessingFilter.instance().applyTo(pg);
        BrightnessStepFilter.instance().setBrightnessStep(-80/255f);
        BrightnessStepFilter.instance().applyTo(pg);
    }

    protected void drawEqBars() {
        p.push();
        p.noStroke();
        PG.setCenterScreen(p);
        float numBars = 7;
        int eqStartIndex = 100;
        float eqStep = 40; // 512f / numElements;
        float barW = 6;
        float barH = 6;
        float barSpacing = 20;
        int eqIndex = 0;
        for(int i=0; i < numBars; i++) {
            eqIndex = P.floor(eqStartIndex + i * eqStep);
            float eq = AudioIn.audioFreq(eqIndex);
            float curBarH = 10 + barH * eq * 10f;
            p.fill(255f);
            p.rect(i * barSpacing, -curBarH / 2f, barW, curBarH, 3);
        }
        p.pop();
    }
    
    @SuppressWarnings("rawtypes")
    protected void drawParticles() {
        float launchX = pg.width / 2 + P.cos(FrameLoop.count(0.02f)) * 200;
        float launchY = pg.height / 2 + P.sin(FrameLoop.count(0.02f)) * 200;

        int audioAmp = (int) (AudioIn.amplitude() * 300);
        if(audioAmp > 0) {
            for(int i=0; i < audioAmp * 3; i++) {
                int[] colorss = new int[] {
                        0xff00FC00,
                        0xff00C457,
                        0xff95FF61,
                };

                PImage[] particlesTex = new PImage[] {
                        //    		            DemoAssets.particleLight(),
                        DemoAssets.particleMedium(),
                        DemoAssets.particleHeavy(),
                };

                ParticleCustom particle = (ParticleCustom) particles.launchParticle(launchX, launchY, 0);
                particle
                    .setSpeedRange(-2, 2, -2, 2, 0, 0)
                    .setAcceleration(0.97f, 0.97f, 1)
                    .setGravityRange(0, 0, 0, 0, 0, 0)
                    .setRotationRange(0, 0, 0, 0, 0, 0)
                    .setLifespanRange(30, 40)
                    .setLifespanSustain(0)
                    .setSizeRange(100, 300)
                    .setColor(colorss[MathUtil.randIndex(colorss.length)])
                    .setColor(imageGradient.getColorAtProgress(FrameLoop.count(0.002f) % 1f))
                    .setImage(particlesTex[MathUtil.randIndex(particlesTex.length)])
                    .randomize();
            }
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
            // scale up, but alpha fade out instead of scale down
            boolean scalingUp = (lifespanProgress.target() == 1);
            float curSize = (scalingUp) ?
                    size * Penner.easeOutExpo(lifespanProgress.value()) :
                        size;
            // curSize = size * Penner.easeOutCirc(lifespanProgress.value());
            float alpha = (lifespanProgress.target() == 1) ? 255 : 255 * lifespanProgress.value();

            // draw different types of shapes
            pg.tint(color, alpha);
            pg.image(image, 0, 0, curSize, curSize);
            pg.tint(255);
        }		

    }
}