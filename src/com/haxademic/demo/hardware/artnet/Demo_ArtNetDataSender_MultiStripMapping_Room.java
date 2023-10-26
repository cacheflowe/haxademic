package com.haxademic.demo.hardware.artnet;

import java.io.IOException;
import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.mapping.SavedPointUI;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.hardware.dmx.artnet.MappedLightStrip;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Demo_ArtNetDataSender_MultiStripMapping_Room
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// mapping & UI
	protected ArtNetDataSender artNetDataSender;
	protected int numStrips = 20;
	protected int lightsPerStrip = 300;
	protected int numPixels = numStrips * lightsPerStrip;
	
	protected ArrayList<MappedLightStrip> lights = new ArrayList<MappedLightStrip>();
	protected int selectedLightIndex = 0;
	protected MappedLightStrip activeLight;

	// texture
	protected SimplexNoise3dTexture noise3d;
	
	// app size
	// - 13' x 7' = 27' wide, 8' tall
	// - strip length: 16'
	protected int APP_W = 2700;
	protected int APP_H = 800;
	protected int STRIP_W = 1600;

	protected PGraphics map;

	protected void config() {
		Config.setAppSize(APP_W, APP_H);
	}

	protected void firstFrame() {
		noise3d = new SimplexNoise3dTexture(p.width, p.height);
		artNetDataSender = new ArtNetDataSender("192.168.1.100", 0, numPixels);
		buildLightBarMapUI();
		map = PG.newPG(pg.width, pg.height);
		DebugView.setTexture("pg", pg);
		DebugView.setTexture("map", map);
	}
	
	protected void buildLightBarMapUI() {
		
		// keyboard & mouse events for SavedPointUI
		P.p.registerMethod("mouseEvent", this); // add mouse listeners
		P.p.registerMethod("keyEvent", this);

		// load saved opints
		textConfigPath = FileUtil.getPath("text/dmx/arnet-strips-lights-editor.txt");
		if (FileUtil.fileExists(textConfigPath)) {
			loadLightsConfig();
		} else {
			for(int i=0; i < numStrips; i++) {
				int x = 100 + 20 * i;
				int y = 100;
				buildLightBar(x, y, x, y * 2);
			}
		}
	}
	
	
	protected void updateTexture() {
		// update noise map
		noise3d.offsetZ(p.frameCount / 10f);
		noise3d.update(1f, 0, FrameLoop.count(0.001f), 0, FrameLoop.count(0.005f), false, false);
		// ContrastFilter.instance().setContrast(3f);
		ContrastFilter.instance().applyTo(noise3d.texture());
		ColorizeFromTexture.instance().setTexture(ImageGradient.BLACK_HOLE());
		ColorizeFromTexture.instance().applyTo(noise3d.texture());
		
		// overdraw circles
		// PGraphics map = noise3d.texture();
		
		// replace with rotating bar
		map.beginDraw();
		map.background(0);
		PG.setCenterScreen(map);
		PG.setDrawCorner(map);
		map.fill(255);
		map.rotate(FrameLoop.count(0.01f));
		map.rect(0, 0, map.width * 3, 20);
		map.fill(255, 255, 0);
		map.rotate(P.PI);
		map.rect(0, 0, map.width * 3, 20);
		map.endDraw();

		// replace map
		if(p.key == ' ') ImageUtil.copyImage(noise3d.texture(), map);
		
		// bring brightness down because voltage problems
		BrightnessFilter.instance().setBrightness(0.1f);
//		BrightnessFilter.instance().applyTo(map);
	}
	
	// animate lights!

	protected void drawApp() {
		background(50);
		
		// draw mapped texture with UI on top 
		updateTexture();
		// p.image(noise3d.texture(), 0, 0);
		drawUI();
		
		// send ArtNet data
		artNetDataSender.send();
		// artNetDataSender.drawDebug(p.g);

		draw3dViz();
	}

	protected void draw3dViz() {
		// 13' x 7' = 27' wide, 8' tall
		// set context
		p.push();
		p.lights();
		PG.setBasicLights(p.g);
		PG.setDrawCenter(p.g);
		PG.setCenterScreen(p.g);
		p.translate(0, 0, -300);
		PG.basicCameraFromMouse(p.g, 0.2f, 0.2f);

		
		// draw back wall
		float u1 = (1300/2) / 2700f;
		float u2 = (2700 - (1300/2)) / 2700f;
		p.push();
		p.translate(0, 0, -350);
		Shapes.drawTexturedRect(p.g, pg, 1300, 800, u1, 0, u2, 0, u2, 1, u1, 1);
		p.pop();
		
		// draw left wall
		p.push();
		p.translate(-1300/2, 0, 0);
		p.rotateY(P.HALF_PI);
		Shapes.drawTexturedRect(p.g, pg, 700, 800, 0, 0, u1, 0, u1, 1, 0, 1);
		p.pop();

		// draw right wall
		p.push();
		p.translate(1300/2, 0, 0);
		p.rotateY(-P.HALF_PI);
		Shapes.drawTexturedRect(p.g, pg, 700, 800, u2, 0, 1, 0, 1, 1, u2, 1);
		p.pop();

		// draw humanoid
		p.push();
		p.translate(0, 730, 0);
		p.rotateY(-P.HALF_PI);
		if(FrameLoop.frameModLooped(100)) {
			PShapeUtil.scaleShapeToHeight(DemoAssets.objHumanoid(), 600); 
			DemoAssets.objHumanoid().disableStyle();
		}
		// p.scale(100);
		p.shape(DemoAssets.objHumanoid());
		p.pop();



		p.pop();
	}
	
	//////////////////////////////
	// UI points & selection
	//////////////////////////////
	
	protected ArrayList<SavedPointUI> points = new ArrayList<SavedPointUI>();
	protected boolean draggingNewPoint = false;
	protected PVector mousePoint = new PVector();
	protected int pointIndex = 0;
	protected String textConfigPath;
	
	protected void buildLightBar(String[] lineComponents) {
		int point1X = ConvertUtil.stringToInt(lineComponents[0]);
		int point1Y = ConvertUtil.stringToInt(lineComponents[1]);
		int point2X = ConvertUtil.stringToInt(lineComponents[2]);
		int point2Y = ConvertUtil.stringToInt(lineComponents[3]);
//		int dmxChannel = ConvertUtil.stringToInt(lineComponents[4]);
		
		buildLightBar(point1X, point1Y, point2X, point2Y);
	}
	
	protected void buildLightBar(int point1X, int point1Y, int point2X, int point2Y) {
		// create 2 draggable points
		SavedPointUI point1 = new SavedPointUI(0, 0, null);
		point1.setPosition(point1X, point1Y);
		point1.setActive(false);
		points.add(point1);

		SavedPointUI point2 = new SavedPointUI(0, 0, null);
		point2.setPosition(point2X, point2Y);
		point2.setActive(false);
		points.add(point2);
		
		// add points to LightStrip
		MappedLightStrip light = new MappedLightStrip(lightsPerStrip, lights.size() * lightsPerStrip, point1.position(), point2.position());
//		light.setDmxChannel(dmxChannel);
		lights.add(light);
	}
	
	////////////////////////
	// SavedPointUI Mouse interface
	////////////////////////
	
	public void mouseEvent(MouseEvent event) {
		mousePoint.set(event.getX(), event.getY());
		checkMouseDragPoint(event);
	}
	
	public void checkMouseDragPoint(MouseEvent event) {
		boolean createDragMode = false; // P.store.getBoolean(DRAG_CREATE_MODE);
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			if(createDragMode && isHoveringPoint() == false) {
				// check points to see if one is hovered. cancel if so
				points.add(newPoint());
				draggingNewPoint = true;
			}
			break;
		case MouseEvent.RELEASE:
			// create a 2nd point on release
			if(createDragMode && draggingNewPoint) {
				points.add(newPoint());
				draggingNewPoint = false;
				// lights.add(new LightBar(dmxUniverseDefault, dmxMode, points.get(points.size() - 2).position(), points.get(points.size() - 1).position())); // add last 2 points PVectors to new LightFixture
			}
			break;
		case MouseEvent.MOVE:
			// set hovered point as active
			boolean foundOne = false;
			for (int i = 0; i < points.size(); i++) {
				if(points.get(i).isHovered()) {
					pointIndex = i;
					foundOne = true;
				}
			}
			if(foundOne == false) pointIndex = -1;
			setActivePoint();
			break;
		case MouseEvent.DRAG:
			// if creating a new line, draw line from first point
			break;
		}
	}
	
	protected SavedPointUI newPoint() {
		SavedPointUI newPoint = new SavedPointUI(0, 0, null);
		newPoint.setPosition((int) mousePoint.x, (int) mousePoint.y);
		newPoint.setActive(false);
		return newPoint;
	}
	
	protected boolean isHoveringPoint() {
		for (int i = 0; i < points.size(); i++) {
			if(points.get(i).isHovered()) return true;
		}
		return false;
	}
	
	protected void setActivePoint() {
//		if(P.store.getBoolean(LIGHTS_UI_DISABLE)) return;
		for (int i = 0; i < points.size(); i++) {
			points.get(i).setActive(i == pointIndex);
		}
	}
	
	protected void drawUI() {
		map.loadPixels();
		
		PGraphics pgUI = pg; // p.g;
		boolean openContext = pgUI == pg;
		boolean pointsUiDisabled = false; // P.store.getBoolean(LIGHTS_UI_DISABLE);
				
		// draw UI points
		SavedPointUI activePoint = null;
		if(pointsUiDisabled == false) {
			for (int i = 0; i < points.size(); i++) {
				SavedPointUI point = points.get(i); 
				// point.drawDebug(pgUI, openContext);
				if(point.isActive()) activePoint = point;
			}
			
			// tell UI points to stay locked on
//			if(P.store.getBoolean(LIGHTS_UI_STAY_ON)) {
//				for (int i = 0; i < points.size(); i++) {
//					points.get(i).resetInteractionTimeout(); // never disappear
//				}
//			}
			
		}
		
		PG.setDrawCenter(pgUI);
		
		// draw/update lights
		if(openContext) pg.beginDraw();
		if(openContext) pg.background(0);
		MappedLightStrip newActiveLight = null;
		PVector activePointPostion = (activePoint == null || pointsUiDisabled) ? null : activePoint.position();
		DebugView.setValue("activePointPostion", (activePointPostion == null) ? null : activePointPostion.toString());
		for (int i = 0; i < lights.size(); i++) {
			MappedLightStrip light = lights.get(i);
			light.setActive(activePointPostion);
			if(light.isActive()) newActiveLight = light;
			light.update(pgUI, map, artNetDataSender);
		}
		if(openContext) pg.endDraw();
		
		// update dmx channel input when we select a new (or no) light
		/*
		if(activeLight != newActiveLight) {
			activeLight = newActiveLight;
			if(activeLight != null) {
				channelInput.set("" + activeLight.dmxChannel());
				channelInput.focus();
				UITextInput.ACTIVE_INPUT = channelInput;
			} else {
				channelInput.set("");
				channelInput.blur();
				UITextInput.ACTIVE_INPUT = null;
			}
		}
		*/
		
		// if active light, send it's value into the dmx channel setting for the active light. make sure we have a number in the text input
		/*
		if(activeLight != null && channelInput.valueString().length() > 0) {
			activeLight.setDmxChannel(ConvertUtil.stringToInt(channelInput.valueString()));
		}
		*/
		
		// temporary dragging point for a new edge being created
		if(draggingNewPoint) {
			pgUI.strokeWeight(4);
			pgUI.stroke(255, 0, 0);
			pgUI.line(points.get(points.size()-1).position().x, points.get(points.size()-1).position().y, mousePoint.x, mousePoint.y);
		}
		
		// set context for flat text
		PG.setDrawCorner(pgUI);
		PG.setDrawFlat2d(pgUI, true);
		
		// info
		/*
		if(showInfo) {
			pgUI.noStroke();
			pgUI.fill(0, 100);
			pgUI.rect(0, 0, 250, 250);
			int infoX = 20;
			int infoY = 15;
			drawText(
					"Q - Floorplan dimmed:  " + P.store.getBoolean(DIMMED_FLOORPLAN) + FileUtil.NEWLINE +
					"W - Drag create mode:  " + P.store.getBoolean(DRAG_CREATE_MODE) + FileUtil.NEWLINE + 
					"E - Lights UI Disable: " + P.store.getBoolean(LIGHTS_UI_DISABLE) + FileUtil.NEWLINE + 
					"R - Lights UI stay on: " + P.store.getBoolean(LIGHTS_UI_STAY_ON) + FileUtil.NEWLINE + 
					"T - Show DMX channels: " + P.store.getBoolean(SHOW_DMX_CHANNELS) + FileUtil.NEWLINE + 
					"Y - Show light index: "  + P.store.getBoolean(SHOW_LIGHT_INDEX) + FileUtil.NEWLINE + 
					"I - Overlay Texture: "   + P.store.getBoolean(TEXTURE_OVERLAY) + FileUtil.NEWLINE + 
					"DEL - Delete UI Point" + FileUtil.NEWLINE + 
					"[ - Prev UI Point" + FileUtil.NEWLINE + 
					"] - Next UI Point" + FileUtil.NEWLINE + 
					"S - Save to disk" + FileUtil.NEWLINE +
					"` - Toggle info" + FileUtil.NEWLINE, 
					infoX, infoY);
	
			// channel text input
			drawText("DMX channel:", channelInput.x(), channelInput.y() - 25);
			channelInput.draw(pgUI);
		}
		*/
		
		// reset context
//		PG.setDrawFlat2d(pgUI, false);
	}
	
	////////////////////////
	// KEYBOARD
	////////////////////////
	
	public void keyEvent(KeyEvent e) {
		if(e.getAction() == KeyEvent.PRESS) {
			if(e.getKey() == ']') {
				pointIndex++;
				if(pointIndex >= points.size()) pointIndex = 0;
				setActivePoint();
			}
			if(e.getKey() == '[') {
				pointIndex--;
				if(pointIndex < 0) pointIndex = points.size() - 1;
				setActivePoint();
			}
			if(e.getKey() == 's') saveLightsToFile();
//			if(e.getKeyCode() == 147) deleteActiveLight();
//			if(e.getKeyCode() == '`') showInfo = !showInfo;
		}
	}

	////////////////////////
	// SAVE / RECALL
	////////////////////////
	
	protected void loadLightsConfig() {
		String[] configLines = FileUtil.readTextFromFile(textConfigPath);
		if(configLines == null) return;
		for (int i = 0; i < configLines.length; i++) {
			String[] lineComponents = configLines[i].split(",");
			String lightType = lineComponents[0]; // TODO: could add metadata here for ArtNet indexes & length
			if(lineComponents.length == 4) {
				buildLightBar(lineComponents);
			}
			else P.error("Couldn't load ArtNetEditor text line: " + configLines[i]);
		}
	}


	protected void saveLightsToFile() {
		String[] saveString = new String[lights.size()];
		for (int i = 0; i < lights.size(); i++) {
			saveString[i] = lights.get(i).toSaveString();
		}
		// make a backup
		if(FileUtil.fileExists(textConfigPath)) {
			try { FileUtil.copyFile(textConfigPath, textConfigPath.replace(".txt", "-bak-" + SystemUtil.getTimestamp() + ".txt"));
			} catch (IOException e) { e.printStackTrace(); }
		}
		// save new config
		FileUtil.createDir(FileUtil.pathForFile(textConfigPath));
		FileUtil.writeTextToFile(textConfigPath, FileUtil.textLinesJoined(saveString));
		P.out("ArtNetEditor: Saved lights config");
	}

}