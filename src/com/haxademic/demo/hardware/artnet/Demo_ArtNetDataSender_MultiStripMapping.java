package com.haxademic.demo.hardware.artnet;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.mapping.SavedPointUI;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.hardware.dmx.artnet.MappedLightStrip;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Demo_ArtNetDataSender_MultiStripMapping
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// mapping & UI
	protected ArtNetDataSender artNetDataSender;
	protected int numStrips = 6;
	protected int lightsPerStrip = 100;
	protected int numPixels = numStrips * lightsPerStrip;
	
	protected ArrayList<MappedLightStrip> lights = new ArrayList<MappedLightStrip>();
	protected int selectedLightIndex = 0;
	protected MappedLightStrip activeLight;

	// texture
	protected SimplexNoise3dTexture noise3d;
	
	// TODO: 
	// - Build into own editor class like DMXEditor
	// - Add better visuals to show LED strip points in UI
	// - Add key commands and text UI back in from DMXEditor
	// - Add more channel info to visual light strip UI (universe start channel, point 1/2, etc)
	// - Save/recall from text file 
	// - Add a brightness slider for texture AND/OR for final output data

	protected void firstFrame() {
		noise3d = new SimplexNoise3dTexture(p.width, p.height);
		artNetDataSender = new ArtNetDataSender("192.168.1.100", 0, numPixels);
		buildLightBarMapUI();
	}
	
	protected void buildLightBarMapUI() {
		int centerX = p.width / 2;
		int centerY = p.height / 2;
		// zig zag flip/reverse for current physical strip layout
		buildLightBar(centerX + 100, centerY + 100 - 100, centerX + 60, centerY - 100 - 100);
		buildLightBar(centerX + 60, centerY + 100 - 100, centerX + 60, centerY - 100 - 100);
		buildLightBar(centerX + 20, centerY - 100 - 100, centerX + 20, centerY + 100 - 100);
		buildLightBar(centerX - 20, centerY + 100 - 100, centerX - 20, centerY - 100 - 100);
		buildLightBar(centerX - 60, centerY - 100 - 100, centerX - 60, centerY + 100 - 100);
		buildLightBar(centerX - 100, centerY - 100 - 100, centerX - 60, centerY + 100 - 100);
		
		// keyboard & mouse events for SavedPointUI
		P.p.registerMethod("mouseEvent", this); // add mouse listeners
		P.p.registerMethod("keyEvent", this);
	}
	
	
	protected void updateTexture() {
		// update noise map
		noise3d.offsetZ(p.frameCount / 10f);
		noise3d.update(6f, 0, FrameLoop.count(0.005f), 0, FrameLoop.count(0.001f), false, false);
		
		// post-process noise map
//		ContrastFilter.instance().setContrast(3f);
		ContrastFilter.instance().setOnContext(noise3d.texture());
		ColorizeFromTexture.instance().setTexture(ImageGradient.BLACK_HOLE());
		ColorizeFromTexture.instance().setOnContext(noise3d.texture());
		
		// overdraw circles
		PGraphics map = noise3d.texture();
		
		// replace with circles
		map.beginDraw();
		PG.setCenterScreen(map);
		PG.setDrawCenter(map);
		int index = 0;
		for (int i = map.width * 3 + (p.frameCount*2) % 40; i > 0; i-= 20) {
			if(index % 2 == 0) {
				map.fill(0, 0, 100);
			} else {
				map.fill(100);
			}
			map.ellipse(0, 0, i, i);
			index++;
		}
		map.endDraw();
		/* */
		
		// replace with rotating bar
		map.beginDraw();
		map.background(0);
		PG.setCenterScreen(map);
		PG.setDrawCorner(map);
		map.fill(255);
		map.rotate(FrameLoop.count(0.06f));
		map.rect(0, 0, p.width * 3, 20);
		map.fill(255, 255, 0);
		map.rotate(P.PI);
		map.rect(0, 0, p.width * 3, 20);
		map.endDraw();
		
		// overdraw mouse
		/*
		map.beginDraw();
		map.background(20);
		PG.setDrawCorner(map);
		map.fill(255);
		map.rect(Mouse.x, 0, 10, map.height);
		map.rect(0, Mouse.y, map.width, 100);
		map.endDraw();
		*/
		
		// bring brightness down because voltage problems
		BrightnessFilter.instance().setBrightness(0.1f);
//		BrightnessFilter.instance().applyTo(map);
	}
	
	// animate lights!

	protected void drawApp() {
		background(0);
		
		// draw mapped texture with UI on top 
		updateTexture();
		p.image(noise3d.texture(), 0, 0);
		drawUI();
		
		// send ArtNet data
		artNetDataSender.send();
		artNetDataSender.drawDebug(p.g);
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
		PGraphics pgUI = p.g;
		boolean pointsUiDisabled = false; // P.store.getBoolean(LIGHTS_UI_DISABLE);
				
		// draw points
		SavedPointUI activePoint = null;
		if(pointsUiDisabled == false) {
			for (int i = 0; i < points.size(); i++) {
				SavedPointUI point = points.get(i); 
				point.drawDebug(pgUI, false);
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
		
		// prepare image to grab pixels
		PGraphics map = noise3d.texture();
		map.loadPixels();
		
		// draw/update lights
		MappedLightStrip newActiveLight = null;
		PVector activePointPostion = (activePoint == null || pointsUiDisabled) ? null : activePoint.position();
		DebugView.setValue("activePointPostion", (activePointPostion == null) ? null : activePointPostion.toString());
		for (int i = 0; i < lights.size(); i++) {
			MappedLightStrip light = lights.get(i);
			light.setActive(activePointPostion);
			if(light.isActive()) newActiveLight = light;
			light.update(pgUI, map, artNetDataSender);
		}
		
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
//			if(e.getKey() == 's') saveLightsToFile();
//			if(e.getKeyCode() == 147) deleteActiveLight();
//			if(e.getKeyCode() == '`') showInfo = !showInfo;
		}
	}

}