package com.haxademic.demo.hardware.lidar;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.lidar.RPLidar;
import com.haxademic.core.hardware.lidar.RPLidar.LidarMeasurement;
import com.haxademic.core.hardware.lidar.RPLidar.LidarScan;
import com.haxademic.core.ui.UI;

public class Demo_RPLidar
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected RPLidar lidarRP;
	protected String UI_ROTATION_OFFSET = "UI_ROTATION_OFFSET";

	protected void config() {
		Config.setAppSize(1280, 720);
	}

	protected void firstFrame() {
		RPLidar.DEBUG = true;
		lidarRP = new RPLidar("COM7");
		
		UI.addTitle("RPLidar");
		UI.addSlider(UI_ROTATION_OFFSET, 0, -P.TWO_PI, P.TWO_PI, 0.01f, false);
	}

	public void keyPressed() {
		super.keyPressed();
		//		if(p.keyCode == 8) P.println("DELETE");
		P.println("key:", p.key, "keyCode:", p.keyCode);
		char lowerKey = Character.toLowerCase(key);

		switch(lowerKey) {
		case 'x':
			lidarRP.stopScan(); 
			break;
		case 'g':
			lidarRP.startScan();
			break;
		}
	}

	protected void drawApp() {
		p.background(0);
		PG.setCenterScreen(p);

		// update lidar
		lidarRP.update();
		LidarScan scan = lidarRP.getLatestScan();
		
		// average number of lidar points is ~276
		// so we can round up to 360 to have a lerping
		DebugView.setValue("scan.measurementCount", scan.measurementCount);
		
		// show lidar points
		for (int i = 0 ; i < scan.measurementCount ; i++) {
			
			// get lidar readings
			LidarMeasurement lidarPoint = scan.measurements[i];
			float angle = P.radians(lidarPoint.angle) + UI.value(UI_ROTATION_OFFSET);
			float distance = lidarPoint.distance;
			float quality = map((float)lidarPoint.quality, 0.0f, 63.0f, 0.0f, 1.0f);
			
			// remap values
			boolean remapToSketch = true;
			if(remapToSketch) {
				distance = map(distance, 0.0f, 6000.0f, 0.0f, p.height / 2.0f);
			}
			int colour = p.color(255.0f - 255 * quality, 255 * quality, 255f);

			// get position for screen
			float x = distance * P.cos(angle);
			float y = distance * P.sin(angle);
			// dot
			stroke(colour);
			strokeWeight(5);
			point(x, y);
			// line
			stroke(255);
			strokeWeight(1);
			line(0, 0, x, y);
			// text
//			fill(255);
//			text(angle, x, y);
		}
	}

	public void stop() {
		lidarRP.stopScan();
		super.stop();
	}
	
	public void dispose() {
		lidarRP.stopScan();
		super.dispose();
	}
}