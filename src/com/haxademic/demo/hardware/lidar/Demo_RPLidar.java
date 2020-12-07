package com.haxademic.demo.hardware.lidar;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.lidar.RPLidar;
import com.haxademic.core.hardware.lidar.RPLidar.LidarScan;

public class Demo_RPLidar
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	RPLidar lidarRP;

	protected void config() {
		Config.setAppSize(1280, 720);
	}

	protected void firstFrame() {
		lidarRP = new RPLidar("COM3");
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

		// update lidar
		lidarRP.update();
		LidarScan scan = lidarRP.getLatestScan();
		DebugView.setValue("scan.measurementCount", scan.measurementCount);
		
		// show lidar points
		for (int i = 0 ; i < scan.measurementCount ; i++) {
			
			// get lidar readings
			float angle = scan.measurements[i].angle;
			float distance = scan.measurements[i].distance;
			float quality = map((float)scan.measurements[i].quality, 0.0f, 63.0f, 0.0f, 1.0f);
			
			// remap values
			angle = radians(angle);
			boolean remapToSketch = true;
			if(remapToSketch) {
				distance = map(distance, 0.0f, 6000.0f, 0.0f, 1280 / 2.0f);
			}
			int colour = p.color(255.0f - 255 * quality, 255 * quality, 255f);

			// get position for screen
			float x = p.width / 2 + distance * sin(angle);
			float y = p.height / 2 - distance * cos(angle);
			// dot
			stroke(colour);
			strokeWeight(5);
			point(x, y);
			// line
			stroke(255);
			strokeWeight(1);
			line(p.width / 2, p.height / 2, x, y);
			// text
			fill(255);
//			text(angle, x, y);
		}
	}


}