package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.dmx.DMXWrapper;
import com.haxademic.core.hardware.kinect.KinectRegionGrid;
import com.haxademic.core.hardware.kinect.KinectSize;
import com.haxademic.core.math.easing.LinearFloat;

public class Demo_DmxKinectStiletto
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXWrapper dmx;

	protected boolean audioActive = false;
	protected LinearFloat fadeOut = new LinearFloat(0, 0.05f);
	
	protected KinectRegionGrid kinectRegionGrid;
	protected LinearFloat dimmer = new LinearFloat(0, 0.05f);

	protected void overridePropsFile() {
		// kinect init
		p.appConfig.setProperty(AppSettings.KINECT_V2_WIN_ACTIVE, true);
	}

	public void setupFirstFrame() {
		// dmx setup
		// dmx = new DMXWrapper();
		
		// kinect init
		int KINECT_MIN_DIST = 	p.appConfig.getInt( "kinect_min_mm", 500 );
		int KINECT_MAX_DIST = 	p.appConfig.getInt( "kinect_max_mm", 1500 );
		int KINECT_TOP = 		p.appConfig.getInt( "kinect_top_pixel", 0 );
		int KINECT_BOTTOM = 	p.appConfig.getInt( "kinect_bottom_pixel", KinectSize.HEIGHT );
		int KINECT_PLAYER_GAP = p.appConfig.getInt( "kinect_player_gap", 0 );
		int NUM_PLAYERS = 		p.appConfig.getInt( "num_players", 1 );
		int KINECT_PIXEL_SKIP = p.appConfig.getInt( "kinect_pixel_skip", 20 );
		int PLAYER_MIN_PIXELS = p.appConfig.getInt( "player_min_pixels", 10 );
		
		// build input!
		kinectRegionGrid = new KinectRegionGrid(NUM_PLAYERS, 1, KINECT_MIN_DIST, KINECT_MAX_DIST, KINECT_PLAYER_GAP, KINECT_TOP, KINECT_BOTTOM, KINECT_PIXEL_SKIP, PLAYER_MIN_PIXELS);
	}

	public void drawApp() {
		background(0);
		if(audioActive) {
			// audio eq
			dmx.setValue(1, P.constrain(P.round(255 * p.audioFreq(10)), 0, 255));
			dmx.setValue(2, P.constrain(P.round(255 * p.audioFreq(20)), 0, 255));
			dmx.setValue(3, P.constrain(P.round(255 * p.audioFreq(40)), 0, 255));
			dmx.setValue(4, P.constrain(P.round(255 * p.audioFreq(60)), 0, 255));
			dmx.setValue(5, P.constrain(P.round(255 * p.audioFreq(80)), 0, 255));
			dmx.setValue(6, P.constrain(P.round(255 * p.audioFreq(100)), 0, 255));
		} else {
			// color cycle
//			dmx.setValue(1, round(127 + 127 * P.sin(p.frameCount * 0.2f)));
//			dmx.setValue(2, round(127 + 127 * P.sin(p.frameCount * 0.08f)));

//			dmx.setValue(3, round(127 + 127 * P.sin(p.frameCount * 0.02f)));
//			dmx.setValue(1, round(127 + 127 * P.sin(P.PI + p.frameCount * 0.1f)));
//			dmx.setValue(2, round(127 + 127 * P.sin(P.PI + p.frameCount * 0.1f)));
//			dmx.setValue(3, round(127 + 127 * P.sin(P.PI + p.frameCount * 0.1f)));

//			if(p.frameCount % 25 == 0) {
//			if(p.audioData.isBeat()) {
//				fadeOut.setCurrent(1);
//				fadeOut.setTarget(0);
//			}
//			fadeOut.update();
//			float easedFade = Penner.easeInExpo(fadeOut.value(), 0, 1, 1);
//			
//			dmx.setValue(1, round(255 * easedFade));
//			dmx.setValue(2, round(255 * easedFade));
//			dmx.setValue(3, round(255 * easedFade));
//			dmx.setValue(4, round(127 + 127 * P.sin(p.frameCount * 0.2f)));
//			dmx.setValue(5, round(127 + 127 * P.sin(p.frameCount * 0.2f)));
//			dmx.setValue(6, round(127 + 127 * P.sin(p.frameCount * 0.2f)));
			
			// silhouette
			// Set address: Addr
			// Set DMX mode: CHSE: CH12
			// 1: pan (0-540)
			// 2: pan (fine)
			// 3: tilt (0-270)
			// 4: tilt (fine)
			// 5: pan/tilt speed (fast -> slow)
			// 6: strobe (slow -> fast)
			// 7: dimmer
			// 8: zoom (wide -> narrow)
			// 9: red
			// 10: green
			// 11: blue
			// 12: white
			
			kinectRegionGrid.update(false);
			boolean hasUser = kinectRegionGrid.getRegion(0).isActive();
			float userX = (hasUser) ? kinectRegionGrid.getRegion(0).controlX() : 0;
			
			p.debugView.setValue("userX", userX);
			p.debugView.setValue("hasUser", hasUser);
			p.debugView.setValue("p.mousePercentX()", p.mousePercentX());
			p.debugView.setValue("p.mousePercentY()", p.mousePercentY());
			
			float panVal = P.map(userX, -1, 1, 0.735f, 0.585f);
			float tiltVal = (hasUser) ? 0 : 0.5f;
			float dimVal = (hasUser) ? 1 : 0;
			dimmer.setTarget(dimVal);
			dimmer.update();
			
			dmx.setValue(1, round(255 * panVal));
			dmx.setValue(3, round(255 * tiltVal));
			dmx.setValue(5, 230);
			dmx.setValue(7, round(255 * dimmer.value()));
			dmx.setValue(8, round(255 * p.mousePercentY()));
			dmx.setValue(9, round(127 + 127 * P.sin(p.frameCount * 0.02f)));
			dmx.setValue(10, round(127 + 127 * P.sin(p.frameCount * 0.01f)));
			dmx.setValue(11, round(127 + 127 * P.sin(p.frameCount * 0.03f)));
			dmx.setValue(12, 255);

		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') audioActive = !audioActive;
	}
}





