package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.particle.Particle3d;
import com.haxademic.core.draw.particle.ParticleSystem3d;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PShape;

public class Demo_ParticleSystem3d 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// particle system
	protected ParticleSystem3d particles;
	protected PShape shape;
	protected PShape[] shapes;
	
	// ui
	protected String FRAME_LAUNCH_INTERVAL = "FRAME_LAUNCH_INTERVAL";
	protected String LAUNCHES_PER_FRAME = "LAUNCHES_PER_FRAME";
	protected String SPEED_RADIAL = "SPEED_RADIAL";
	protected String SPEED_Y = "SPEED_Y";
	protected String GRAVITY = "GRAVITY";
	protected String ACCELERATION = "ACCELERATION";
	protected String LIFESPAN = "LIFESPAN";
	protected String ROTATION = "ROTATION";
	protected String ROTATION_SPEED = "ROTATION_SPEED";
	protected String SIZE = "SIZE";
	protected String COLOR_SET_INDEX = "COLOR_SET_INDEX";
	protected String SHAPE = "SHAPE";
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame() {
		// particle system init
		particles = new ParticleSystem3d();
		
		// custom shape
		PShape shapeTess = DemoAssets.shapeX().getTessellation();
		PShapeUtil.repairMissingSVGVertex(shapeTess);
		shape = PShapeUtil.createExtrudedShape(shapeTess, 100);
		if(shape != null) {
			PShapeUtil.centerShape(shape);
			PShapeUtil.scaleShapeToHeight(shape, 300f);
			shape.disableStyle();
		}
		
		// more custom shapes
		TextToPShape textToPShape = new TextToPShape(TextToPShape.QUALITY_MEDIUM);
		String fontFile = FileUtil.getPath(DemoAssets.fontDSEG7Path);
		shapes = new PShape[] {
				textToPShape.stringToShape3d("0", 10, fontFile),
				textToPShape.stringToShape3d("1", 10, fontFile),
				textToPShape.stringToShape3d("2", 10, fontFile),
				textToPShape.stringToShape3d("3", 10, fontFile),
				textToPShape.stringToShape3d("4", 10, fontFile),
				textToPShape.stringToShape3d("5", 10, fontFile),
				textToPShape.stringToShape3d("6", 10, fontFile),
				textToPShape.stringToShape3d("7", 10, fontFile),
				textToPShape.stringToShape3d("8", 10, fontFile),
				textToPShape.stringToShape3d("9", 10, fontFile),
		};
		for (int i = 0; i < shapes.length; i++) {
			PShapeUtil.scaleShapeToHeight(shapes[i], 300f);
			shapes[i].disableStyle();
		}
		
		// build UI
		UI.addTitle("ParticleSystem3d Controls");
		UI.addSlider(FRAME_LAUNCH_INTERVAL, 1, 1, 60, 1, false);
		UI.addSlider(LAUNCHES_PER_FRAME, 1, 1, 60, 1, false);
		UI.addSlider(SPEED_RADIAL, 1, 1, 15, 0.05f, false);
		UI.addSlider(SPEED_Y, 0, -5, 5, 0.005f, false);
		UI.addSlider(GRAVITY, 0, -0.2f, 0.2f, 0.001f, false);
		UI.addSlider(ACCELERATION, 1, -0.5f, 1.5f, 0.001f, false);
		UI.addSlider(LIFESPAN, 50, 10, 200, 1, false);
		UI.addSliderVector(ROTATION, 0, -P.PI, P.PI, 0.01f, false);
		UI.addSliderVector(ROTATION_SPEED, 0, -0.1f, 0.1f, 0.001f, false);
		UI.addSlider(SIZE, 20, 10, 100, 1, false);
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
				
				// circular launch
				float radialSpeed = MathUtil.randRangeDecimal(UI.value(SPEED_RADIAL) - 1f, UI.value(SPEED_RADIAL) + 1f);
				float rot = MathUtil.randRangeDecimal(0, P.TWO_PI);
				float speedX = P.cos(rot) * radialSpeed;
				float speedZ = P.sin(rot) * radialSpeed;
				
				// launch!
				Particle3d parti = particles.launch()
					.setSpeed(speedX, speedX, UI.value(SPEED_Y) - 1, UI.value(SPEED_Y) + 1, speedZ, speedZ)
					.setGravity(0, 0, UI.value(GRAVITY) - 0.01f, UI.value(GRAVITY) + 0.01f, 0, 0)
					.setAcceleration(UI.value(ACCELERATION))
					.setLifespan(UI.valueInt(LIFESPAN) - 10, UI.valueInt(LIFESPAN) + 10)
					.setRotation(
							MathUtil.randRangeDecimal(0, P.TWO_PI * UI.valueX(ROTATION)), MathUtil.randRangeDecimal(0, P.TWO_PI * UI.valueY(ROTATION)), MathUtil.randRangeDecimal(0, P.TWO_PI * UI.valueZ(ROTATION)), 
							0, UI.valueY(ROTATION_SPEED), 0)
					.setSize(UI.valueInt(SIZE) - 10, UI.valueInt(SIZE) + 10)
					.setColor(ColorsHax.COLOR_GROUPS[UI.valueInt(COLOR_SET_INDEX)][MathUtil.randRange(0, ColorsHax.COLOR_GROUPS[UI.valueInt(COLOR_SET_INDEX)].length - 1)])
					.launch(0, 0, 0);
				
				// shape selection after the launch - basic shape or custom PShape
				if(UI.valueInt(SHAPE) <= 2) {
					if(UI.valueInt(SHAPE) == 1) {
						parti.setShape(false); // cube
					} else {
						parti.setShape(MathUtil.randBooleanWeighted(0.2f)); // most likely a cube instead of sphere
					}
				} else {
					parti.setShape(shapes[MathUtil.randIndex(shapes.length)]);	// random shape
				}
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
		PG.basicCameraFromMouse(pg, 0.3f);
//		pg.rotateY(FrameLoop.osc(0.01f, -0.5f, 0.5f));
//		pg.rotateX(FrameLoop.osc(0.005f, -0.5f, 0.5f));
		pg.stroke(100);
		pg.sphere(1000);
		pg.noStroke();
		particles.drawParticles(pg);
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
	}
}