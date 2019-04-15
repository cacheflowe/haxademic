package com.haxademic.demo.hardware.dmx;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.hardware.dmx.DMXWrapper;

import processing.core.PGraphics;
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
	protected String ORTHO_VIEW = 				SPATIAL_LIGHTING_PREFIX + "ORTHO_VIEW";
	
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
	protected PImage elevation;

	// dmx communication
	protected DMXWrapper dmxWrapper;
	protected String LIGHT_DEBUG_CHANNEL = 		SPATIAL_LIGHTING_PREFIX + "LIGHT_DEBUG_CHANNEL";

	// dmx lights
	protected ArrayList<DMXLightRGB> lights = new ArrayList<DMXLightRGB>();

	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280);
		p.appConfig.setProperty(AppSettings.HEIGHT, 720);
		p.appConfig.setProperty(AppSettings.SHOW_SLIDERS, true);
	}
	
	public void setupFirstFrame () {
		loadAssets();
		buildControls();
		initDMX();
	}
	
	protected void loadAssets() {
		floorplan = DemoAssets.textureNebula();	
		elevation = DemoAssets.textureJupiter();	
	}
	
	protected void buildControls() {
		// room view
		p.prefsSliders.addSlider(FLOORPLAN_ALPHA, 1, 0, 1, 0.01f);
		p.prefsSliders.addSliderVector(ROOM_SIZE, 500, 200, 1500f, 1f, false);
		p.prefsSliders.addSliderVector(ROOM_ROTATION, 0, -1f, 1f, 0.001f, false);
		p.prefsSliders.addSlider(ORTHO_VIEW, 0, 0, 1f, 1f, false);
		
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
		p.prefsSliders.addSlider(LIGHT_DEBUG_CHANNEL, 0, 0, 255, 1f);

		// init lights
		lights.add(new DMXLightRGB(1, true));
		lights.add(new DMXLightRGB(4, true));
		lights.add(new DMXLightRGB(7, true));
	}
	
	public void drawApp() {
		p.background(0);
		
		pg.beginDraw();
		setContext();
		buildRoom();
		drawFloorplan();
		updateLightSource();
		updateDrawLights();
		drawColorGrid();
		pg.endDraw();
		
		p.image(pg, 0, 0);
	}
	
	protected void setContext() {
		pg.background(0);
		if(p.prefsSliders.value(ORTHO_VIEW) > 0.5f) pg.ortho();
		else pg.perspective();
		pg.lights();
		DrawUtil.setCenterScreen(pg);
		DrawUtil.setDrawCenter(pg);
		pg.translate(0, 0, p.prefsSliders.value(ROOM_ROTATION + "_Z") * pg.width);
		pg.rotateX(p.prefsSliders.value(ROOM_ROTATION + "_X") * P.TWO_PI);
		pg.rotateY(p.prefsSliders.value(ROOM_ROTATION + "_Y") * P.TWO_PI);	
	}
	
	protected void buildRoom() {
		roomW = p.prefsSliders.value(ROOM_SIZE + "_X");
		roomH = p.prefsSliders.value(ROOM_SIZE + "_Y");
		roomD = p.prefsSliders.value(ROOM_SIZE + "_Z");
		
		// draw bounding box
		pg.noFill();
		pg.stroke(255);
		Shapes.drawDashedBox(pg, roomW, roomH, roomD, 20, true);
		pg.noStroke();
	}
	
	protected void drawFloorplan() {
		float floorplanAlpha = p.prefsSliders.value(FLOORPLAN_ALPHA);
		if(floorplanAlpha > 0) {
			pg.noStroke();
			
			// floor
			if(floorplan != null) {
				DrawUtil.push(pg);
				pg.translate(0, roomH / 2f, 0);
				pg.rotateX(P.HALF_PI);
				DrawUtil.setPImageAlpha(pg, floorplanAlpha);
				pg.image(floorplan, 0, 0, roomW, roomD);
				DrawUtil.resetPImageAlpha(pg);
				DrawUtil.pop(pg);
			}
			
			// elevation
			if(elevation != null) {
				DrawUtil.push(pg);
				pg.translate(0, 0, -roomD / 2f);
				DrawUtil.setPImageAlpha(pg, floorplanAlpha);
				pg.image(elevation, 0, 0, roomW, roomH);
				DrawUtil.resetPImageAlpha(pg);
				DrawUtil.pop(pg);
			}
		}
	}
	
	protected void updateDrawLights() {
		DrawUtil.setDrawCorner(pg);
		for (int i = 0; i < lights.size(); i++) {
			lights.get(i).update(pg);
		}
	}
	
	protected void updateLightSource() {
		// update scroll direction
		noiseGlobalOffset.add(p.prefsSliders.value(NOISE_SCROLL_DIRECTION + "_X"), p.prefsSliders.value(NOISE_SCROLL_DIRECTION + "_Y"), p.prefsSliders.value(NOISE_SCROLL_DIRECTION + "_Z"));
	}
	
	protected void drawColorGrid() {
		float noiseSpacing = p.prefsSliders.value(NOISE_GRID_SPACING);
		for (int x = 0; x <= roomW; x += noiseSpacing) {
			for (int y = 0; y <= roomH; y += noiseSpacing) {
				for (int z = 0; z <= roomD; z += noiseSpacing) {
					float gridX = -roomW/2f + x;
					float gridY = -roomH/2f + y;
					float gridZ = -roomD/2f + z;
					int cellColor = colorFromPosition(gridX, gridY, gridZ);
					pg.fill(cellColor);
					pg.pushMatrix();
					pg.translate(gridX, gridY, gridZ);
					pg.box(p.prefsSliders.value(NOISE_GRID_CUBE_SIZE) * (1f * P.p.brightness(cellColor)/255f));
					pg.popMatrix();
				}	
			}
		}
	}
	
	///////////////////////////
	// COLOR GENERATOR 3D
	///////////////////////////

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
		
		public void update(PGraphics pg) {
			updatePosition();
			updateColor();
			drawLight(pg);
			sendDMX();
		}
		
		protected void updatePosition() {
			if(this.hasSlider) {
				position.set(p.prefsSliders.value(posSliderKey + "_X"), p.prefsSliders.value(posSliderKey + "_Y"), p.prefsSliders.value(posSliderKey + "_Z"));
			}	
		}
		
		protected void updateColor() {
			int colorOut = colorFromPosition(position.x * roomW, position.y * roomH, position.z * roomD);
			color.setTargetInt(colorOut);
			
			// override color if testing channel
			if(dmxChannel == p.prefsSliders.valueInt(LIGHT_DEBUG_CHANNEL)) {
				int colorComponent = (P.round(p.frameCount / 8) % 2 == 0) ? 0 : 255;
				color.setCurrentInt(p.color(colorComponent));
			}
			
			// lerp color
			color.update();	
		}
		
		protected void drawLight(PGraphics pg) {
			DrawUtil.push(pg);
			pg.fill(color.colorInt());
			pg.translate(position.x * roomW, position.y * roomH, position.z * roomD);
			pg.sphere(20);
			// draw debug channel text
			pg.translate(25, 0, 0);
			pg.fill(0, 255, 0);
			pg.text(name, 0, 0);
			// pop
			DrawUtil.pop(pg);
		}
		
		protected void sendDMX() {
			dmxWrapper.setValue(dmxChannel + 0, P.round(P.map(color.r(), 0, 255, mapLow, mapHigh)));
			dmxWrapper.setValue(dmxChannel + 1, P.round(P.map(color.g(), 0, 255, mapLow, mapHigh)));
			dmxWrapper.setValue(dmxChannel + 2, P.round(P.map(color.b(), 0, 255, mapLow, mapHigh)));
		}
	}

}
