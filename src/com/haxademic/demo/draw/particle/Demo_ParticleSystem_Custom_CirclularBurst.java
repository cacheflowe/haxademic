package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;

public class Demo_ParticleSystem_Custom_CirclularBurst
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

  protected ParticleSystem<Particle> particles = new ParticleSystem<Particle>();
	protected PImage[] particleImages;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}

	protected void firstFrame() {
		particles = new ParticleSystem<Particle>();
		particleImages = FileUtil.loadImagesArrFromDir(FileUtil.getPath("haxademic/images/particles/"), "png");
	}
	
	protected void drawApp() {
		background(0);
		
		// allow a reset
		if(KeyboardState.keyTriggered(' ')) particles.killAll();
		
		// draw image/map base
		pg.beginDraw();
		pg.background(0);
		PG.setDrawFlat2d(pg, true);
		PG.setDrawCenter(pg);
		if(FrameLoop.frameModLooped(30)) launchParticle();
		particles.updateAndDrawParticles(pg, PBlendModes.BLEND);
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
		
		// debug info
		DebugView.setValue("particles.poolSize()", particles.poolSize());
		DebugView.setValue("particles.poolActiveSize()", particles.poolActiveSize());
	}

  protected void launchParticle() {
    float xPos = Mouse.x;
    float yPos = Mouse.y;
    float zPos = 0;

    // get color for score
    int color = ColorsHax.WHITE;

    // launch!
    for(int i=0; i < 80; i++) {
      float randRads = P.p.random(0, P.TWO_PI);
      float randSpeed = P.p.random(3f, 15f);
      Particle particle = (Particle) particles.launchParticle(0, 0, 0);
      particle
        .setSpeedRange(0, 0, 0, 0, 0, 0)
        .setSpeed(P.cos(randRads) * randSpeed, P.sin(randRads) * randSpeed, 0)
        .setAcceleration(0.95f)
        .setGravity(0, 0, 0)
        .setRotation(0, 0, MathUtil.randRangeDecimal(0, P.TWO_PI), 0, 0, 0)
        .setRotationSpeed(0, 0, MathUtil.randRangeDecimal(-0.1f, 0.1f))
        .setLifespanRange(20, 70) 
        .setSizeRange(10, 100)
        .setColor(color)
        .setImage(DemoAssets.particle())
        .launch(xPos, yPos, zPos);
    }
  }
  
}