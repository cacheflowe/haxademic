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
import com.haxademic.core.draw.particle.IParticleFactory;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PShape;

public class Demo_ParticleSystem3d 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// particle system
	protected ParticleFactoryBasic3d particleFactory;
	protected ParticleSystem particles;
	protected PShape shape;
	protected PShape[] shapes;
	
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
		particleFactory = new ParticleFactoryBasic3d();
		particles = new ParticleSystem(particleFactory);
		particles.enableUI("Parti3d", false);
		
		// custom shape
//		PShape shapeTess = DemoAssets.shapeX().getTessellation();
//		PShapeUtil.repairMissingSVGVertex(shapeTess);
//		shape = PShapeUtil.createExtrudedShape(shapeTess, 100);
//		if(shape != null) {
//			PShapeUtil.centerShape(shape);
//			PShapeUtil.scaleShapeToHeight(shape, 300f);
//			shape.disableStyle();
//		}
//		
//		// more custom shapes
//		TextToPShape textToPShape = new TextToPShape(TextToPShape.QUALITY_MEDIUM);
//		String fontFile = FileUtil.getPath(DemoAssets.fontDSEG7Path);
//		shapes = new PShape[] {
//				textToPShape.stringToShape3d("0", 10, fontFile),
//				textToPShape.stringToShape3d("1", 10, fontFile),
//				textToPShape.stringToShape3d("2", 10, fontFile),
//				textToPShape.stringToShape3d("3", 10, fontFile),
//				textToPShape.stringToShape3d("4", 10, fontFile),
//				textToPShape.stringToShape3d("5", 10, fontFile),
//				textToPShape.stringToShape3d("6", 10, fontFile),
//				textToPShape.stringToShape3d("7", 10, fontFile),
//				textToPShape.stringToShape3d("8", 10, fontFile),
//				textToPShape.stringToShape3d("9", 10, fontFile),
//		};
//		for (int i = 0; i < shapes.length; i++) {
//			PShapeUtil.scaleShapeToHeight(shapes[i], 300f);
//			shapes[i].disableStyle();
//		}
		
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
	
	protected void drawApp() {
		// background
		p.background(0);
		
		// launch particle
		if(FrameLoop.frameModLooped(UI.valueInt(FRAME_LAUNCH_INTERVAL))) {
			for (int i = 0; i < UI.valueInt(LAUNCHES_PER_FRAME); i++) {
				
				// circular launch props
				float radialSpeed = MathUtil.randRangeDecimal(UI.value(SPEED_RADIAL) - 1f, UI.value(SPEED_RADIAL) + 1f);
				float rot = MathUtil.randRangeDecimal(0, P.TWO_PI);
				float speedX = P.cos(rot) * radialSpeed;
				float speedZ = P.sin(rot) * radialSpeed;
				
				// launch!
				Particle particle = particles.launchParticle(0, 0, 0);
				particleFactory.setColor(particle, ColorsHax.COLOR_GROUPS[UI.valueInt(COLOR_SET_INDEX)][MathUtil.randRange(0, ColorsHax.COLOR_GROUPS[UI.valueInt(COLOR_SET_INDEX)].length - 1)]);
				particle
					.setSpeedRange(speedX, speedX, UI.value(SPEED_Y) - 1, UI.value(SPEED_Y) + 1, speedZ, speedZ)
					.launch(0, 0, 0);
			}
		}
		
		// draw image/map base
		pg.beginDraw();
		pg.background(0);
		pg.noFill();
//		pg.ortho();
		PG.setBetterLights(pg);
		PG.setCenterScreen(pg);
		PG.setDrawCorner(pg);
		PG.basicCameraFromMouse(pg, 0.4f);
//		pg.rotateY(FrameLoop.osc(0.01f, -0.5f, 0.5f));
//		pg.rotateX(FrameLoop.osc(0.005f, -0.5f, 0.5f));
		pg.stroke(100);
		pg.sphere(1000);
		pg.noStroke();
		particles.drawParticles(pg, PBlendModes.BLEND);
		pg.endDraw();
		
		// post-process
		GodRays.instance(p).setDecay(0.8f);
		GodRays.instance(p).setWeight(0.3f);
		GodRays.instance(p).setRotation(Mouse.xEasedNorm * -3f);
		GodRays.instance(p).setAmp(0.2f);
		GodRays.instance(p).applyTo(pg);
		
		// draw to screen
		p.image(pg, 0, 0);
		
		// debug
		DebugView.setValue("particles.poolSize()", particles.poolSize());
		DebugView.setValue("particles.poolActiveSize()", particles.poolActiveSize());
	}
	
	
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
				newShape = PShapeUtil.createSphere(1, p.color(180, 180, 0));
			}
			return new Particle(newShape);
		}

	}

}