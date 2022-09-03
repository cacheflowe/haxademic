package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.IParticleFactory;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleFactory;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.video.Movie;

public class Demo_ParticleSystem_FromMap 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie video;
	protected IParticleFactory particleFactory;
	protected ParticleSystem particles;
	protected boolean loadParticlesDir = true;
	protected boolean is3d = true;
	protected PGraphics map;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}

	protected void firstFrame() {
		// create map
		video = DemoAssets.movieKinectSilhouette();
		video.loop();
		video.speed(0.4f);
		
		map = PG.newPG(p.width/2, p.height/2);
		DebugView.setTexture("map", map);
		
		// create particle system
		PImage[] particleImages = new PImage[] { DemoAssets.particle() };
		if(loadParticlesDir) {
			particleImages = FileUtil.loadImagesArrFromDir(FileUtil.getPath("haxademic/images/particles/"), "png");
		}

		particleFactory = (is3d) ?
			new ParticleFactoryBasic3d() :
			new ParticleFactory(particleImages);
		particles = new ParticleSystem(particleFactory);
		particles.enableUI("PARTY_1_", false);	// add sliders
	}
	
	protected void drawApp() {
		background(0);
		PG.setCenterScreen(p);
		PG.setDrawCenter(p);
		PG.basicCameraFromMouse(p.g, 0.5f);
		if(!is3d) {
			PG.setDrawFlat2d(p.g, true);
			PG.setDrawCenter(p.g);
		}
		else      PG.setBetterLights(p.g);
		
		// update map & launch particles
		// responsive scale if map is different size than destination
		drawLaunchMap(pg);
		particles.launchParticlesFromMap(map, (float) p.height / map.height); // 2f * Mouse.xNorm);	// <- playing with scale here 
		
		// draw map
		/*
		p.blendMode(PBlendModes.ADD);
		PG.setPImageAlpha(p, 0.2f);
		p.image(map, 0, 0);
		PG.resetPImageAlpha(p);
		*/
		
		// draw particles
		p.translate(-p.height/2, -p.height/2);	//  <- use height for both for responsive map/dest calc
		particles.drawParticles(p.g, PBlendModes.BLEND);
		
		// post-process
		GodRays.instance(p).setDecay(0.8f);
		GodRays.instance(p).setWeight(0.3f);
		GodRays.instance(p).setRotation(Mouse.xEasedNorm * -3f);
		GodRays.instance(p).setAmp(0.2f);
		GodRays.instance(p).applyTo(p.g);
		
		// debug info
		DebugView.setValue("particles.poolSize()", particles.poolSize());
		DebugView.setValue("particles.poolActiveSize()", particles.poolActiveSize());
	}
	
	protected void drawLaunchMap(PGraphics pg) {
		// draw video
		map.beginDraw();
		map.background(0);
		if(video.width > 100) ImageUtil.cropFillCopyImage(video, map, false);
		
		// draw other map (ring)
		map.push();
		map.background(0);
		PG.setCenterScreen(map);
		PG.setDrawCenter(map);
		map.strokeWeight(FrameLoop.osc(0.015f, 70, 20));
		map.stroke(255);
		map.noFill();
		float ringSize = FrameLoop.osc(0.015f, map.height * 0.3f, map.height * 0.7f);
		map.ellipse(0, 0, ringSize, ringSize);
		map.pop();
		
		// draw other map (number)
		map.push();
		map.background(0);
		PG.setCenterScreen(map);
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, map.height * 0.8f);
		FontCacher.setFontOnContext(map, font, p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		String frameC = (""+P.round(p.frameCount / 150f));
		map.text(frameC.substring(frameC.length() - 1), 0, -map.height * 0.15f);
		map.pop();
		
		// prep pixelreading for launching
		map.endDraw();
		map.loadPixels();
	}
	
	///////////////////////////////////
	// Simplest custom 3d particle overrides
	///////////////////////////////////
	
	
	public class ParticleFactoryBasic3d
	implements IParticleFactory {
		
		public ParticleFactoryBasic3d() {}
		
		public Particle randomize(Particle particle) {
			return particle;
		}

		public Particle setColor(Particle particle, int color) {
			PShapeUtil.setBasicShapeStyles(particle.shape(), color, 0, 0);
			return particle;
		}
		
		public Particle initNewParticle() {
			PShape newShape = null;
			boolean isCube = MathUtil.randBoolean();
			if(isCube) {
				newShape = PShapeUtil.createBox(1, 1, 1, p.color(180, 180, 0));
			} else {
				p.sphereDetail(8);
				newShape = PShapeUtil.createSphere(1, p.color(180, 180, 0));
			}
			return new Particle(newShape);
		}

	}

			
}