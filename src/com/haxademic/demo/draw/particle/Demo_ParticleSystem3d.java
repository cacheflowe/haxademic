package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PShape;

@SuppressWarnings("rawtypes")
public class Demo_ParticleSystem3d 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/*
	 * ParticleSystem notes:
	 * - @SuppressWarnings("rawtypes") used above because ParticleSystem is a generic class
	 * - This demo uses the built-in ParticleSystem & Particle class, but adds
	 *   custom behavior on the standard Particle.launchParticle()
	 */

	// particle system
	protected ParticleSystem particles;
	protected TextToPShape textToPShape;
	
	// ui
	protected String FRAME_LAUNCH_INTERVAL = "FRAME_LAUNCH_INTERVAL";
	protected String LAUNCHES_PER_FRAME = "LAUNCHES_PER_FRAME";
	protected String SPEED_RADIAL = "SPEED_RADIAL";
	protected String SPEED_Y = "SPEED_Y";
	protected String COLOR_SET_INDEX = "COLOR_SET_INDEX";
	protected String SHAPE = "SHAPE";
	
	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame() {
		// particle system init
		particles = new ParticleSystem();
		particles.enableUI("Parti3d", false);

		// shape helpers
		textToPShape = new TextToPShape(TextToPShape.QUALITY_MEDIUM);
				
		// build UI
		UI.addTitle("ParticleSystem3d Controls");
		UI.addSlider(FRAME_LAUNCH_INTERVAL, 1, 1, 60, 1, false);
		UI.addSlider(LAUNCHES_PER_FRAME, 1, 1, 60, 1, false);
		UI.addSlider(SPEED_RADIAL, 1, 1, 15, 0.05f, false);
		UI.addSlider(SPEED_Y, 0, -5, 5, 0.005f, false);
		UI.addSlider(COLOR_SET_INDEX, 0, 0, ColorsHax.COLOR_GROUPS.length - 1, 1, false);
		UI.addSlider(SHAPE, 1, 1, 3, 1, false);
		UI.addWebInterface(false);
	}
	
	protected void launchParticles() {
		if(FrameLoop.frameModLooped(UI.valueInt(FRAME_LAUNCH_INTERVAL))) {
			for (int i = 0; i < UI.valueInt(LAUNCHES_PER_FRAME); i++) {
				// launch! let the particle system init.randomize an available particle 
				Particle particle = particles.launchParticle(0, 0, 0);
				
				// Add extra partcle setters/behavior based on local controls outside of ParticleSystem
				// here we've overridden the speed range to launch radially. This is slightly inefficient
				// because we're regenerating random number a 2nd time, but it does allow for outside overriding.
				// This should probably be done within a custom Particle subclass or ParticleFactory.randomize(),
				// but here are some circular launch props:
				float radialSpeed = MathUtil.randRangeDecimal(UI.value(SPEED_RADIAL) - 1f, UI.value(SPEED_RADIAL) + 1f);
				float rot = MathUtil.randRangeDecimal(0, P.TWO_PI);
				float speedX = P.cos(rot) * radialSpeed;
				float speedZ = P.sin(rot) * radialSpeed;
				particle
					.setColor(ColorsHax.COLOR_GROUPS[UI.valueInt(COLOR_SET_INDEX)][MathUtil.randRange(0, ColorsHax.COLOR_GROUPS[UI.valueInt(COLOR_SET_INDEX)].length - 1)])
					.setSpeedRange(speedX, speedX, UI.value(SPEED_Y) - 1, UI.value(SPEED_Y) + 1, speedZ, speedZ)
					.launch(0, 0, 0);

				// set particle shape, making sure to let the particle keep its shape if it already has one!
				if(particle.shape() == null) {
					PShape newShape = null;
					boolean isCube = MathUtil.randBoolean();
					if (isCube) {
						newShape = PShapeUtil.createBox(1, 1, 1, p.color(180, 180, 0));
					} else if (MathUtil.randBoolean()) {
						PShape shapeTess = DemoAssets.shapeX().getTessellation();
						PShapeUtil.repairMissingSVGVertex(shapeTess);
						PShapeUtil.centerShape(shapeTess);
						PShapeUtil.scaleShapeToHeight(shapeTess, 1);
						shapeTess.disableStyle();
						newShape = PShapeUtil.createExtrudedShape(shapeTess, 1);
					} else if (MathUtil.randBoolean()) {
						newShape = newNumber(MathUtil.randRange(0, 9) + "");
					} else {
						p.sphereDetail(8);
						newShape = PShapeUtil.createSphere(1, p.color(180, 180, 0));
					}
					// add shape to particle
					particle.setShape(newShape);
				}
			}
		}	
	}

	public PShape newNumber(String number) {
		String fontFile = FileUtil.getPath(DemoAssets.fontDSEG7Path);
		PShape shape = textToPShape.stringToShape3d(number, 25, fontFile);	// 100 is the unfortunate text font default size
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, 1);
		return shape;
	}

	
	protected void drawApp() {
		launchParticles();
		
		// background
		p.background(0);
				
		// set context
		pg.beginDraw();
		pg.background(0);
		pg.noFill();
		PG.setBetterLights(pg);
		PG.setCenterScreen(pg);
		PG.setDrawCorner(pg);
		PG.basicCameraFromMouse(pg, 0.4f);
		
		// draw outer sphere
		pg.stroke(100);
		pg.sphere(1000);
		pg.noStroke();
		
		// draw particles
		particles.updateAndDrawParticles(pg, PBlendModes.BLEND);
		pg.endDraw();
		
		// post-process
		GodRays.instance().setDecay(0.8f);
		GodRays.instance().setWeight(0.3f);
		GodRays.instance().setRotation(Mouse.xEasedNorm * -3f);
		GodRays.instance().setAmp(0.2f);
		// GodRays.instance().applyTo(pg);
		
		// draw to screen
		p.image(pg, 0, 0);
		
		// debug
		DebugView.setValue("particles.poolSize()", particles.poolSize());
		DebugView.setValue("particles.poolActiveSize()", particles.poolActiveSize());
	}

}