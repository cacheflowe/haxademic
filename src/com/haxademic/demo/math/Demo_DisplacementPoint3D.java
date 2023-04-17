package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL;
import com.haxademic.core.math.easing.DisplacementPoint3D;
import com.haxademic.core.ui.UI;

public class Demo_DisplacementPoint3D
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float cellSize;
	protected float cubeSize;
	
	protected String DISPLACE_RANGE = "DISPLACE_RANGE";
	protected String FRICTION = "FRICTION";
	protected String ACCELERATION = "ACCELERATION";
	protected String DISPLACE_AMP = "DISPLACE_AMP";
	protected String INFLUENCE_PROXIMITY_RAMP = "INFLUENCE_PROXIMITY_RAMP";

	protected String DISPLACE_POINT = "DISPLACE_POINT";
	protected String SCENE_SCALE = "SCENE_SCALE";
	protected String SCENE_ROTATION = "SCENE_ROTATION";
	
	protected DisplacementPoint3D[] points;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1080);
		Config.setProperty( AppSettings.HEIGHT, 1080);
		Config.setProperty( AppSettings.SHOW_UI, true);
		Config.setProperty( AppSettings.RENDER_DEMO_SCREENSHOT_FRAME, 2000);
	}

	protected void firstFrame() {
		MidiDevice.init(LaunchControlXL.deviceName);

		// lay out grid
		// cube of cubes
		float spread = 1;
		float cubeDetail = 10;
		cellSize = p.height * 0.05f;
		cubeSize = cubeDetail * cellSize;
		float cubeSizeHalf = cubeSize / 2f;
		float cellSizeHalf = cellSize / 2f;
		points = new DisplacementPoint3D[P.round(cubeDetail * cubeDetail * cubeDetail)];
		int i = 0;
		for (float x=0; x < cubeDetail; x++) {
			for (float y=0; y < cubeDetail; y++) {
				for (float z=0; z < cubeDetail; z++) {		// only half a sphere
					// position
					float baseX = (-cubeSizeHalf + cellSizeHalf + x * cellSize);
					float baseY = (-cubeSizeHalf + cellSizeHalf + y * cellSize);
					float baseZ = (-cubeSizeHalf + cellSizeHalf + z * cellSize);
					baseX *= spread;
					baseY *= spread;
					baseZ *= spread;

					// color from dist
					points[i] = new DisplacementPoint3D(baseX, baseY, baseZ);
					i++;
				}
			}
		}

		// set up sliders
		UI.addSlider(DISPLACE_RANGE, pg.height * 0.2f, 1, 1000, 1, false, LaunchControlXL.SLIDERS[0]);
		UI.addSlider(FRICTION, 0.9f, 0.1f, 0.99f, 0.001f, false, LaunchControlXL.SLIDERS[1]);
		UI.addSlider(ACCELERATION, 0.1f, 0.01f, 0.99f, 0.001f, false, LaunchControlXL.SLIDERS[2]);
		UI.addSlider(DISPLACE_AMP, 1f, 0f, 5f, 0.01f, false, LaunchControlXL.SLIDERS[3]);
		UI.addSlider(INFLUENCE_PROXIMITY_RAMP, 1, 0, 1, 0.01f, false, LaunchControlXL.SLIDERS[4]);
		UI.addSlider(SCENE_SCALE, 1, 0, 2, 0.01f, false, LaunchControlXL.SLIDERS[5]);
		UI.addSliderVector(DISPLACE_POINT, 0, -pg.height, pg.height, 2f, false, LaunchControlXL.KNOBS_ROW_1[0], LaunchControlXL.KNOBS_ROW_1[1], LaunchControlXL.KNOBS_ROW_1[2]);
		UI.addSliderVector(SCENE_ROTATION, 0, -P.QUARTER_PI, P.QUARTER_PI, 0.001f, false, LaunchControlXL.KNOBS_ROW_2[0], LaunchControlXL.KNOBS_ROW_2[1], LaunchControlXL.KNOBS_ROW_2[2]);
	}

	protected void drawApp() {
		// set up context
		background(0);
		PG.setCenterScreen(p);
		PG.setDrawCenter(p);
		p.scale(UI.value(SCENE_SCALE));
		p.rotateX(UI.valueXEased(SCENE_ROTATION));
		p.rotateY(UI.valueYEased(SCENE_ROTATION));
		p.rotateZ(UI.valueZEased(SCENE_ROTATION));
		p.lights();
		
		p.push();
		p.translate(0, 0, 0);
		PG.drawOriginAxis(p.g, p.height * 0.5f, 10f);
		p.pop();

		// show repeller
		float displaceX = UI.valueXEased(DISPLACE_POINT);
		float displaceY = UI.valueYEased(DISPLACE_POINT);
		float displaceZ = UI.valueZEased(DISPLACE_POINT);
		p.push();
		p.noStroke();
		p.fill(255, 0, 0);
		p.translate(displaceX, displaceY, displaceZ);
		p.sphere(cellSize);
		p.pop();
		
		// update properties w/sliders & draw points
		for (int i = 0; i < points.length; i++) {
			// set properties 
			points[i].displaceRange(UI.value(DISPLACE_RANGE));
			points[i].friction(UI.value(FRICTION));
			points[i].acceleration(UI.value(ACCELERATION));
			points[i].displaceAmp(UI.value(DISPLACE_AMP));
			points[i].influenceProximityRamp(UI.value(INFLUENCE_PROXIMITY_RAMP));
			points[i].update(displaceX, displaceY, displaceZ);
		}
		
		// draw after updating all, so vertices match up between `update()` calls
		p.fill(0);
		p.stroke(255);
		for (int i = 0; i < points.length; i++) {
			// draw points
			float scaleDown = cellSize * points[i].resultDisplacedAmp();
			scaleDown = P.constrain(scaleDown, 0, cellSize);
			p.push();
			p.fill(255 * points[i].resultDisplacedAmp());
			p.translate(points[i].pos().x, points[i].pos().y, points[i].pos().z);
			p.box(cellSize - scaleDown);
			p.pop();
		}
	}
}
