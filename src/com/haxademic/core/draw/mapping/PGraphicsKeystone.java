package com.haxademic.core.draw.mapping;

import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.KeyEvent;

public class PGraphicsKeystone
extends BaseSavedQuadUI {

	protected PGraphics pg;
	protected float subDivideSteps;
	
	public PGraphicsKeystone( PApplet p, PGraphics pg, float subDivideSteps, String filePath ) {
		super(p.width, p.height, filePath);
		this.pg = pg;
		this.subDivideSteps = subDivideSteps;
	}
	
	public PGraphicsKeystone( PApplet p, PGraphics pg, float subDivideSteps, String filePath, String filePathFineOffsets ) {
		this(p, pg, subDivideSteps, filePath);
		initFineControl(filePathFineOffsets);
	}
	
	public PGraphics pg() {
		return pg;
	}
	
	public void fillSolidColor( PGraphics canvas, int fill ) {
		// default single mapped quad
		canvas.noStroke();
		canvas.fill(fill);
		canvas.beginShape(PConstants.QUAD);
		canvas.vertex(topLeft.x, topLeft.y, 0);
		canvas.vertex(topRight.x, topRight.y, 0);
		canvas.vertex(bottomRight.x, bottomRight.y, 0);
		canvas.vertex(bottomLeft.x, bottomLeft.y, 0);
		canvas.endShape();
	}
		
	public void setOffsetsX(float[] offsetsX) {
		this.offsetsX = offsetsX;
		if(this.offsetsX.length != subDivideSteps + 1) DebugUtil.printErr("PGraphicsKeystone.offsetsX[] must have one more element than subDivideSteps");
	}
	
	public void setOffsetsY(float[] offsetsY) {
		this.offsetsY = offsetsY;
		if(this.offsetsX.length != subDivideSteps + 1) DebugUtil.printErr("PGraphicsKeystone.offsetsY[] must have one more element than subDivideSteps");
	}
	
	public void update( PGraphics canvas ) {
		update(canvas, true, pg);
	}
	
	public void update( PGraphics canvas, boolean subdivide ) {
		update(canvas, subdivide, pg);
	}
	
	public void update( PGraphics canvas, boolean subdivide, PImage texture ) {
		update(canvas, subdivide, texture, 0, 0, texture.width, texture.height);
	}
	
	public void update( PGraphics canvas, boolean subdivide, PImage texture, float mapX, float mapY, float mapW, float mapH) {
		// draw to screen with pinned corner coords
		// inspired by: https://github.com/davidbouchard/keystone & http://marcinignac.com/blog/projectedquads-source-code/
		canvas.textureMode(PConstants.IMAGE);
		canvas.noStroke();
		if(selectedRowIndex != -1 || selectedColIndex != -1) canvas.stroke(255, 0, 0);
		canvas.fill(255);
		canvas.beginShape(PConstants.QUAD);
		canvas.texture(texture);
				
		if( subdivide == true ) {
			// subdivide quad for better resolution
			float stepsX = subDivideSteps;
			float stepsY = subDivideSteps;

			for( float x=0; x < stepsX; x += 1f ) {
				// calculate spread of mesh grid and uv coordinates
				float xPercent = x/stepsX;
				float xPercentNext = (x+1f)/stepsX;
				if( xPercentNext > 1 ) xPercentNext = 1;
				float uPercent = xPercent;
				float uPercentNext = xPercentNext;

				// add x offsets if array exists and values aren't zero
				if(offsetsX != null) {
					int fineIndex = (int) x;
					if(offsetsX[fineIndex] != 0) xPercent += offsetsX[fineIndex]; 
					if(offsetsX[fineIndex + 1] != 0) xPercentNext += offsetsX[fineIndex + 1];
				}
				
				for( float y=0; y < stepsY; y += 1f ) {
					// calculate spread of mesh grid and uv coordinates
					float yPercent = y/stepsY;
					float yPercentNext = (y+1f)/stepsY;
					if( yPercentNext > 1 ) yPercentNext = 1;
					float vPercent = yPercent;
					float vPercentNext = yPercentNext;

					// add y offsets if array exists and values aren't zero
					if(offsetsY != null) {
						int fineIndexY = (int) y;
						if(offsetsY[fineIndexY] != 0) yPercent += offsetsY[fineIndexY]; 
						if(offsetsY[fineIndexY + 1] != 0) yPercentNext += offsetsY[fineIndexY + 1];
					}
					
					// calc grid positions based on interpolating columns between corners
					float colTopX = interp(topLeft.x, topRight.x, xPercent);
					float colTopY = interp(topLeft.y, topRight.y, xPercent);
					float colBotX = interp(bottomLeft.x, bottomRight.x, xPercent);
					float colBotY = interp(bottomLeft.y, bottomRight.y, xPercent);
					
					float nextColTopX = interp(topLeft.x, topRight.x, xPercentNext);
					float nextColTopY = interp(topLeft.y, topRight.y, xPercentNext);
					float nextColBotX = interp(bottomLeft.x, bottomRight.x, xPercentNext);
					float nextColBotY = interp(bottomLeft.y, bottomRight.y, xPercentNext);
					
					// calc quad coords
					float quadTopLeftX = interp(colTopX, colBotX, yPercent);
					float quadTopLeftY = interp(colTopY, colBotY, yPercent);
					float quadTopRightX = interp(nextColTopX, nextColBotX, yPercent);
					float quadTopRightY = interp(nextColTopY, nextColBotY, yPercent);
					float quadBotRightX = interp(nextColTopX, nextColBotX, yPercentNext);
					float quadBotRightY = interp(nextColTopY, nextColBotY, yPercentNext);
					float quadBotLeftX = interp(colTopX, colBotX, yPercentNext);
					float quadBotLeftY = interp(colTopY, colBotY, yPercentNext);
					
					// draw subdivided quads
					canvas.vertex(quadTopLeftX, quadTopLeftY, 0, 	mapX + mapW * uPercent, 		mapY + mapH * vPercent);
					canvas.vertex(quadTopRightX, quadTopRightY, 0, 	mapX + mapW * uPercentNext, 	mapY + mapH * vPercent);
					canvas.vertex(quadBotRightX, quadBotRightY, 0, 	mapX + mapW * uPercentNext, 	mapY + mapH * vPercentNext);
					canvas.vertex(quadBotLeftX, quadBotLeftY, 0, 	mapX + mapW * uPercent, 		mapY + mapH * vPercentNext);
				}
			}
			
			// draw fine control debug lines
			if(offsetsX != null && selectedColIndex != -1) {
				float xPercent = selectedColIndex/stepsX;
				if(offsetsX[selectedColIndex] != 0) xPercent += offsetsX[selectedColIndex]; 

				float colTopX = interp(topLeft.x, topRight.x, xPercent);
				float colTopY = interp(topLeft.y, topRight.y, xPercent);
				float colBotX = interp(bottomLeft.x, bottomRight.x, xPercent);
				float colBotY = interp(bottomLeft.y, bottomRight.y, xPercent);

				canvas.stroke(0, 255, 0);
				canvas.vertex(colTopX, colTopY, 0, 		0, 0);
				canvas.vertex(colTopX, colTopY, 0, 		0, 0);
				canvas.vertex(colBotX, colBotY, 0, 		0, 0);
				canvas.vertex(colBotX, colBotY, 0, 		0, 0);
			}
			if(offsetsY != null && selectedRowIndex != -1) {
				float yPercent = selectedRowIndex/stepsY;
				if(offsetsY[selectedRowIndex] != 0) yPercent += offsetsY[selectedRowIndex]; 
				
				float rowTopX = interp(topLeft.x, bottomLeft.x, yPercent);
				float rowTopY = interp(topLeft.y, bottomLeft.y, yPercent);
				float rowBotX = interp(topRight.x, bottomRight.x, yPercent);
				float rowBotY = interp(topRight.y, bottomRight.y, yPercent);
				
				canvas.stroke(0, 255, 0);
				canvas.vertex(rowTopX, rowTopY, 0, 		0, 0);
				canvas.vertex(rowTopX, rowTopY, 0, 		0, 0);
				canvas.vertex(rowBotX, rowBotY, 0, 		0, 0);
				canvas.vertex(rowBotX, rowBotY, 0, 		0, 0);
			}
//			else canvas.noStroke();

		} else {
			// default single mapped quad
			canvas.vertex(topLeft.x, topLeft.y, 0, 			mapX, mapY);
			canvas.vertex(topRight.x, topRight.y, 0, 		mapX + mapW, mapY);
			canvas.vertex(bottomRight.x, bottomRight.y, 0, 	mapX + mapW, mapY + mapH);
			canvas.vertex(bottomLeft.x, bottomLeft.y, 0, 	mapX, mapY + mapH);
		}

		canvas.endShape();
		
		// draw UI after mapping
		drawDebug(canvas, false);
	}
	
	protected float interp( float lower, float upper, float n ) {
		return ( ( upper - lower ) * n ) + lower;
	}

	public void drawTestPattern() {
		pg.beginDraw();
		pg.noStroke();
		
		float spacingX = (float) pg.width / (float) subDivideSteps;
		float spacingY = (float) pg.height / (float) subDivideSteps;
		
		for( int x=0; x < subDivideSteps; x++) {
			for( int y=0; y < subDivideSteps; y++) {
				if( ( x % 2 == 0 && y % 2 == 1 ) || ( y % 2 == 0 && x % 2 == 1 ) ) {
					pg.fill(0, 160);
				} else {
					pg.fill(255, 160);
				}
				pg.rect(x * spacingX, y * spacingY, spacingX, spacingY);
			}
		}
		pg.endDraw();
	}
	
	//////////////////////////////////////////
	// FINE CONTROLS 
	//////////////////////////////////////////
	
	// fine subdivision controls
	protected float[] offsetsX = null;
	protected float[] offsetsY = null;
	protected int selectedRowIndex = -1;
	protected int selectedColIndex = -1;
	protected float offsetPushAmp = 0.0005f;
	protected String configFile;

	protected void initFineControl(String filePathFineOffsets) {
		configFile = filePathFineOffsets;
		buildMappingOffsets();
		loadConfig();
	}
	
	// init
	
	protected void buildMappingOffsets() {
		// build offsets arrays
		offsetsX = new float[(int) subDivideSteps + 1];
		for (int i = 0; i < offsetsX.length; i++) offsetsX[i] = 0;
		setOffsetsX(offsetsX);

		offsetsY = new float[(int) subDivideSteps + 1];
		for (int i = 0; i < offsetsY.length; i++) offsetsY[i] = 0;
		setOffsetsY(offsetsY);
	}
	
	// public
	
	public void setActive(boolean debug) {
		super.setActive(debug);
		if(!active) {
			 selectedRowIndex = -1;
			 selectedColIndex = -1;
		}
	}
	
	// config file

	protected void loadConfig() {
		if(FileUtil.fileExists(configFile)) {
			String[] textLines = FileUtil.readTextFromFile(configFile);
			String[] numbersStrArrayX = textLines[0].split(",");
			for (int i = 0; i < numbersStrArrayX.length; i++) {
				float offset = ConvertUtil.stringToFloat(numbersStrArrayX[i]);
				if(i < offsetsX.length) offsetsX[i] = offset;
			}
			String[] numbersStrArrayY = textLines[1].split(",");
			for (int i = 0; i < numbersStrArrayY.length; i++) {
				float offset = ConvertUtil.stringToFloat(numbersStrArrayY[i]);
				if(i < offsetsY.length) offsetsY[i] = offset;
			}
		}
	}
	
	protected void saveConfig() {
		String floatsStr = "";
		for (int i = 0; i < offsetsX.length; i++) {
			if(i > 0) floatsStr += ","; 
			floatsStr += offsetsX[i];
		}
		floatsStr += FileUtil.NEWLINE;
		for (int i = 0; i < offsetsY.length; i++) {
			if(i > 0) floatsStr += ","; 
			floatsStr += offsetsY[i];
		}
		FileUtil.writeTextToFile(configFile, floatsStr);
	}

	// key commands
	
	public void keyEvent(KeyEvent e) {
		super.keyEvent(e);
		if(active == false) return;
		if(e.getAction() == KeyEvent.PRESS) {
			// fine controls
			if(e.getKey() == 'Z') {
				selectedColIndex--;
				if(selectedColIndex < -1) selectedColIndex = offsetsX.length - 1;
			}
			if(e.getKey() == 'X') {
				selectedColIndex++;
				if(selectedColIndex >= offsetsX.length) selectedColIndex = -1;
			}
			if(e.getKey() == 'C') {
				selectedRowIndex--;
				if(selectedRowIndex < -1) selectedRowIndex = offsetsY.length - 1;
			}
			if(e.getKey() == 'V')  {
				selectedRowIndex++;
				if(selectedRowIndex >= offsetsY.length) selectedRowIndex = -1;
			}
			if(e.getKey() == 'O')  fineControlAdjust(offsetsX, selectedColIndex, -1f);
			if(e.getKey() == 'P') fineControlAdjust(offsetsX, selectedColIndex, 1f);
			if(e.getKey() == '{') fineControlAdjust(offsetsY, selectedRowIndex, -1f);
			if(e.getKey() == '}') fineControlAdjust(offsetsY, selectedRowIndex, 1f);
			if(e.getKey() == 'E') saveConfig();
		}
	}
	
	// navigate through rows/cols
	
	protected void fineControlAdjust(float[] offsets, int index, float amount) {
		amount *= offsetPushAmp;
		if(index != -1) {
			offsets[index] += amount;
		}
	}
	
}
