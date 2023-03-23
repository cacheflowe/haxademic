package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.image.ImageCacher;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleFactory;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.render.FrameLoop;

import KinectPV2.KinectPV2;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_KinectV2_Silhouette_ShrimpDaddy
extends PAppletHax {

    public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

    protected KinectPV2 kinect;

    protected PGraphics bufferRgb;
    protected PGraphics bufferMask;
    
    protected ParticleSystemCustom particles;



    protected void config() {
        Config.setProperty( AppSettings.WIDTH, 1280 );
        Config.setProperty( AppSettings.HEIGHT, 960 );
        Config.setProperty( AppSettings.SHOW_DEBUG, true );
        Config.setProperty( AppSettings.FILLS_SCREEN, false );
    }

    protected void firstFrame() {
        kinect = new KinectPV2(p);
        kinect.enableDepthImg(true);
        kinect.enableColorImg(true);
        kinect.enableDepthMaskImg(true);
        kinect.enableBodyTrackImg(true);
        kinect.enableInfraredImg(true);
        kinect.init();

        // init buffers
        bufferRgb = PG.newPG(p.width, p.height);
        bufferMask = PG.newPG(p.width, p.height);
        
        // init particles        
        particles = new ParticleSystemCustom();

    }

    protected void drawApp() {
        if(p.frameCount == 1) p.background(0);

        // get kinect images
        PImage rgb = kinect.getColorImage();
        PImage silhouette = kinect.getBodyTrackImage();
        DebugView.setTexture("kinect.getColorImage()", rgb);
        DebugView.setTexture("kinect.getBodyTrackImage()", silhouette);
        
        // do masking
        ImageUtil.cropFillCopyImage(rgb, bufferRgb, true);
        ImageUtil.cropFillCopyImage(silhouette, bufferMask, true);
        InvertFilter.instance().setOnContext(bufferMask);
        bufferRgb.mask(bufferMask);
        DebugView.setTexture("bufferRgb", bufferRgb);
        
        // launch particles
        if(FrameLoop.frameModLooped(60)) {
            for (int i = 0; i < 5; i++) {
                ParticleCustom particle = (ParticleCustom) particles.launchParticle(p.width/2, p.height/2, 0);
                particle.setImage(bufferRgb);
            }
        }
        
        // load bg image
        PImage bgImg = ImageCacher.get("images/_sketch/tatum-kid-court.png");
        
        // draw composite
        pg.beginDraw();
        ImageUtil.cropFillCopyImage(bgImg, pg, true);
        pg.background(0);
        particles.updateAndDrawParticles(pg, PBlendModes.LIGHTEST);
        pg.image(bufferRgb, 0, 0);
        pg.endDraw();
        
        // draw to screen 
        ImageUtil.copyImage(pg, p.g);
    }

    
    
    //////////////////////////////////////
    // Custom particle system
    //////////////////////////////////////

    public class ParticleFactoryCustom
    extends ParticleFactory {
        
        public ParticleFactoryCustom() {
            super();
        }
        
        public Particle initNewParticle() {
            return new ParticleCustom();
        }
    }

    public class ParticleSystemCustom
    extends ParticleSystem {

        public ParticleSystemCustom() {
            super(new ParticleFactoryCustom());
        }
        
        protected void randomize(Particle particle) {
            particle
                .setSpeed(0, 0, 0)
                .setAcceleration(1)
                .setGravity(0, -0.4f, 0)
                .setGravityRange(0, 0, 0.2f, 0.4f, 0, 0)
                .setSpeed(0, 0, 0)
                .setSpeedRange(-5, 5, -10f, -15f, 0, 0)
                .setRotationRange(0, 0, 0, 0, 0, 0)
                .setRotationSpeedRange(0, 0, 0, 0, -0.01f, 0.01f) // -1, 1)
                .setLifespanRange(30, 70)
                .setColor(P.p.color(P.p.random(0, 255), P.p.random(0, 255), P.p.random(0, 255)));
            // shared launch config & call
//            particleFactory.randomize(particle);
        }
        
    }
    
    //////////////////////////////////////
    // Custom particle
    //////////////////////////////////////
    
    public class ParticleCustom
    extends Particle {
        
        protected PGraphics texture;
        
        public ParticleCustom() {}
        
        public Particle setImage(PImage image) {
            // copy to PGraphics, and use the pg as the particle image
            if(texture == null) texture = PG.newPG(p.width, p.height);
            texture.beginDraw();
            texture.clear();
            ImageUtil.copyImage(image, texture);
            texture.endDraw();
            DebugView.setTexture("particle", texture);
            this.image = texture;
            return this;
        }
        
        protected void drawParticle(PGraphics pg) {
            float lifeProgress = lifespanProgress.value();
            DebugView.setValue("lifeProgress", lifeProgress);
            
            // draw different types of shapes
            pg.push();
            PG.setDrawCenter(pg);
            pg.tint(this.color);
            pg.image(image, 0, 0);
            pg.pop();
        }       
        
    }
}
