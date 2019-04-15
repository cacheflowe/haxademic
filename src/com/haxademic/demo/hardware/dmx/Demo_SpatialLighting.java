package com.haxademic.demo.hardware.dmx;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.dmx.DMXWrapper;

import processing.core.PImage;
import processing.core.PVector;

public class Demo_SpatialLighting 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// TODO --------------------------
	// * Add multi-ring lights - one of these objects would create the lights and move the centerpoints and radius
	// 		* Pass in an array of DMX start channels?
	// * Add individual light control override - use a PrefsText int[] array to store all of the offsets?
	// 		* Target channel mode - flash a specific channel to identify 
	// * Add mapping DMX debug visual (show DMX channel in 3d text)
	// * Add more floorplan / scenic images
	// * Try other volumetric color techniques 
	// * Add light model for simulation? sphere is fine for now
	// -------------------------------

	protected String SPATIAL_LIGHTING_PREFIX = "SL_";
	
	// room bounding box and texture helpers
	protected String ROOM_SIZE = 				SPATIAL_LIGHTING_PREFIX + "ROOM_SIZE";
	protected String FLOORPLAN_ALPHA = 			SPATIAL_LIGHTING_PREFIX + "FLOORPLAN_ALPHA";
	protected String ROOM_ROTATION = 			SPATIAL_LIGHTING_PREFIX + "ROOM_ROTATION";
	
	// color generation
	protected String NOISE_FREQ = 				SPATIAL_LIGHTING_PREFIX + "NOISE_FREQ";
	protected String NOISE_SCROLL_DIRECTION = 	SPATIAL_LIGHTING_PREFIX + "NOISE_DIRECTION";
	protected String NOISE_GRID_SPACING = 		SPATIAL_LIGHTING_PREFIX + "NOISE_GRID_SPACING";
	protected String NOISE_GRID_CUBE_SIZE = 	SPATIAL_LIGHTING_PREFIX + "NOISE_GRID_CUBE_SIZE";
	protected PVector noiseGlobalOffset = new PVector();

	// 3d space helpers
	float roomW = 500;
	float roomH = 300;
	float roomD = 300;
	protected PImage floorplan;

	// dmx communication
	protected DMXWrapper dmxWrapper;
	
	// dmx lights
	protected ArrayList<DMXLightRGB> lights = new ArrayList<DMXLightRGB>();
	protected DMXLightRGB light1;
	protected DMXLightRGB light2;
	protected DMXLightRGB light3;

	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280);
		p.appConfig.setProperty(AppSettings.HEIGHT, 720);
		p.appConfig.setProperty(AppSettings.SHOW_SLIDERS, true);
	}
	
	public void setupFirstFrame () {
		// load assets
		floorplan = DemoAssets.textureNebula(); 
		
		buildControls();
		initDMX();
	}
	
	protected void buildControls() {
		// room view
		p.prefsSliders.addSlider(FLOORPLAN_ALPHA, 1, 0, 1, 0.01f);
		p.prefsSliders.addSliderVector(ROOM_SIZE, 500, 200, 1500f, 1f, false);
		p.prefsSliders.addSliderVector(ROOM_ROTATION, 0, -1f, 1f, 0.001f, false);
		
		// noise for color
		p.prefsSliders.addSlider(NOISE_FREQ, 0.001f, 0.0001f, 0.003f, 0.00001f);
		p.prefsSliders.addSliderVector(NOISE_SCROLL_DIRECTION, 0, -0.01f, 0.01f, 0.0001f, false);
		p.prefsSliders.addSlider(NOISE_GRID_SPACING, 50, 10, 100, 0.1f);
		p.prefsSliders.addSlider(NOISE_GRID_CUBE_SIZE, 4, 0, 20, 0.1f);
		
		// light positions
		P.out(p.prefsSliders.toJSON());	
	}
	
	protected void initDMX() {
		// build DMX lights
		dmxWrapper = new DMXWrapper("COM7", 9600);

		// init lights
		light1 = new DMXLightRGB(4, true);
		light2 = new DMXLightRGB(1, true);
		light3 = new DMXLightRGB(7, true);
		
		// build array
		lights.add(light1);
		lights.add(light2);
		lights.add(light3);
	}
	
	public void drawApp() {
		// bg components
		p.background(0);
		
		// 3d rotation
		// p.ortho();
		p.perspective();
		p.lights();
		DrawUtil.setCenterScreen(p.g);
		DrawUtil.setDrawCenter(p.g);
		p.translate(0, 0, p.prefsSliders.value(ROOM_ROTATION + "_Z") * p.width);
		p.rotateX(p.prefsSliders.value(ROOM_ROTATION + "_X") * P.TWO_PI);
		p.rotateY(p.prefsSliders.value(ROOM_ROTATION + "_Y") * P.TWO_PI);
		
		///////////////////////////
		// DEFINE ROOM SIZE
		///////////////////////////
		roomW = p.prefsSliders.value(ROOM_SIZE + "_X");
		roomH = p.prefsSliders.value(ROOM_SIZE + "_Y");
		roomD = p.prefsSliders.value(ROOM_SIZE + "_Z");
		
		// draw bounding box
		p.noFill();
		p.stroke(255);
		Shapes.drawDashedBox(p.g, roomW, roomH, roomD, 20, true);
		p.noStroke();

		///////////////////////////
		// DRAW FLOOR PLAN
		///////////////////////////;
		float floorplanAlpha = p.prefsSliders.value(FLOORPLAN_ALPHA);
		if(floorplanAlpha > 0) {
			p.noStroke();
			DrawUtil.push(p.g);
			p.translate(0, roomH / 2f, 0);
			p.rotateX(P.HALF_PI);
			DrawUtil.setPImageAlpha(p.g, floorplanAlpha);
			p.image(floorplan, 0, 0, roomW, roomD);
			DrawUtil.resetPImageAlpha(p.g);
			DrawUtil.pop(p.g);
		}
		
		///////////////////////////
		// CALC VOLUMETRIC LIGHTS
		///////////////////////////
		noiseGlobalOffset.add(p.prefsSliders.value(NOISE_SCROLL_DIRECTION + "_X"), p.prefsSliders.value(NOISE_SCROLL_DIRECTION + "_Y"), p.prefsSliders.value(NOISE_SCROLL_DIRECTION + "_Z"));
		
		///////////////////////////
		// DRAW LIGHTS
		///////////////////////////
		
		DrawUtil.setDrawCorner(p.g);
		for (int i = 0; i < lights.size(); i++) {
			lights.get(i).update();
		}
		
		///////////////////////////
		// DRAW COLOR GUIDE/GRID
		///////////////////////////
		float noiseSpacing = p.prefsSliders.value(NOISE_GRID_SPACING);
		for (int x = 0; x <= roomW; x += noiseSpacing) {
			for (int y = 0; y <= roomH; y += noiseSpacing) {
				for (int z = 0; z <= roomD; z += noiseSpacing) {
					float gridX = -roomW/2f + x;
					float gridY = -roomH/2f + y;
					float gridZ = -roomD/2f + z;
					int cellColor = colorFromPosition(gridX, gridY, gridZ);
					p.fill(cellColor);
					p.pushMatrix();
					p.translate(gridX, gridY, gridZ);
					p.box(p.prefsSliders.value(NOISE_GRID_CUBE_SIZE) * (1f * P.p.brightness(cellColor)/255f));
					p.popMatrix();
				}	
			}
		}
	}
	
	protected int colorFromPosition(float x, float y, float z) {
		float noiseAmp = p.prefsSliders.value(NOISE_FREQ);

		return p.color(
			0 + 255f * p.noise(x * noiseAmp + noiseGlobalOffset.x), 
			0 + 255f * p.noise(y * noiseAmp + noiseGlobalOffset.y), 
			0 + 255f * p.noise(z * noiseAmp + noiseGlobalOffset.z)
		);

//		float noiseVal3d = p.noise(
//			x * noiseAmp + noiseGlobalOffset.x, 
//			y * noiseAmp + noiseGlobalOffset.y, 
//			z * noiseAmp + noiseGlobalOffset.z
//		);
//		return p.color(255f * noiseVal3d);
	}
	
	///////////////////////////
	// DMX LIGHT
	///////////////////////////
	
	public class DMXLightRGB {

		protected EasingColor color = new EasingColor("#000000", 10f); 
		protected int dmxChannel;
		protected String name;
		protected boolean hasSlider;
		protected String posSliderKey;
		protected float mapLow = 0;
		protected float mapHigh = 255;
		protected PVector position = new PVector();
		
		public DMXLightRGB(int dmxChannel) {
			this(dmxChannel, false);
		}
		
		public DMXLightRGB(int dmxChannel, boolean hasSlider) {
			this.dmxChannel = dmxChannel;
			this.name = ""+dmxChannel;
			this.hasSlider = hasSlider;
			
			if(this.hasSlider) {
				posSliderKey = SPATIAL_LIGHTING_PREFIX + "LIGHT_POS_" + dmxChannel;
				p.prefsSliders.addSliderVector(posSliderKey, 0, -1f, 1f, 0.001f, false);
			}
		}
		
		public int dmxChannel() { return dmxChannel; }
		public int color() { return color.colorInt(); }
		public int colorR() { return P.round(color.r()); }
		public int colorG() { return P.round(color.g()); }
		public int colorB() { return P.round(color.b()); }
		public int colorLuma() { return (int) P.p.brightness(color.colorInt()); }
		public float mapLow() { return mapLow; }
		public float mapHigh() { return mapHigh; }
		public DMXLightRGB setMapLowHigh(float mapLow, float mapHigh) { this.mapLow = mapLow; this.mapHigh = mapHigh; return this; }
		
		public void update() {
			// lerp color
			color.update();
			
			// use slider to set a single light's positino
			if(this.hasSlider) {
				position.set(p.prefsSliders.value(posSliderKey + "_X"), p.prefsSliders.value(posSliderKey + "_Y"), p.prefsSliders.value(posSliderKey + "_Z"));
			}
			
			// draw light in 3d space
			DrawUtil.push(p.g);
			p.fill(colorFromPosition(position.x * roomW, position.y * roomH, position.z * roomD));
			p.translate(position.x * roomW, position.y * roomH, position.z * roomD);
			p.sphere(20);
			// draw debug channel text
			p.translate(25, 0, 0);
			p.fill(0, 255, 0);
			p.text(name, 0, 0);
			DrawUtil.pop(p.g);
			
			// send dmx
			dmxWrapper.setValue(dmxChannel + 0, P.round(P.map(color.r(), 0, 255, mapLow, mapHigh)));
			dmxWrapper.setValue(dmxChannel + 1, P.round(P.map(color.g(), 0, 255, mapLow, mapHigh)));
			dmxWrapper.setValue(dmxChannel + 2, P.round(P.map(color.b(), 0, 255, mapLow, mapHigh)));
		}
	}

}
