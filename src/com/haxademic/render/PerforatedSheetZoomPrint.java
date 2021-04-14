package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.ColorDistortionFilter;
import com.haxademic.core.draw.filters.pshader.RadialRipplesFilter;
import com.haxademic.core.draw.filters.pshader.SphereDistortionFilter;
import com.haxademic.core.draw.filters.pshader.WobbleFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.render.Renderer;

import processing.core.PImage;

public class PerforatedSheetZoomPrint 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage logoText;
	protected PImage logoSmiley;
	
	
	protected void config() {
		int FRAMES = 600;
		Config.setAppSize(1024, 1024);
		Config.setPgSize(4096, 4096);
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, (FRAMES * 1) + 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (FRAMES * 2) + 1 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}
	
	protected void firstFrame() {
		logoText = P.getImage("images/_sketch/hyht_text.png");
		logoSmiley = P.getImage("images/_sketch/hyht_smiley.png");
	}

	protected void drawApp() {
		p.background(0);
		PG.setTextureRepeat(pg, true);
		pg.beginDraw();
		pg.background(ColorsHax.COLOR_GROUPS[5][0]);
		pg.stroke(ColorsHax.COLOR_GROUPS[5][2]);
		pg.background(255);
		pg.stroke(0);
		
		drawZoomGrid();
//		drawBasicGrid();
		pg.endDraw();
//		InvertFilter.instance(p).applyTo(pg);
		
		if(FrameLoop.progress() > 0.25f && FrameLoop.progress() < 0.65f) {
			float effectProgress = P.map(FrameLoop.progress(), 0.25f, 0.65f, 0f, 1f);
			float progressHalf = (effectProgress < 0.5f) ? effectProgress * 2f : 1f - (effectProgress - 0.5f) * 2f; 
			float progressHalfEased = Penner.easeInOutCubic(progressHalf);
			
			WobbleFilter.instance(p).setTime(p.frameCount * 0.01f);
			WobbleFilter.instance(p).setSpeed(1f); // Mouse.xNorm * 3f);
			WobbleFilter.instance(p).setStrength(progressHalfEased * 0.1f);
			WobbleFilter.instance(p).setSize(9.5f);
			WobbleFilter.instance(p).applyTo(pg);
			
			RadialRipplesFilter.instance(p).setTime(p.frameCount * 0.005f);// * Mouse.yNorm);
			RadialRipplesFilter.instance(p).setAmplitude(progressHalfEased * 0.3f);
//			RadialRipplesFilter.instance(p).applyTo(pg);
		}
		if(FrameLoop.progress() > 0.15f && FrameLoop.progress() < 0.8f) {
			float effectProgress = P.map(FrameLoop.progress(), 0.15f, 0.8f, 0f, 1f);
			float progressHalf = (effectProgress < 0.5f) ? effectProgress * 2f : 1f - (effectProgress - 0.5f) * 2f; 
			float progressHalfEased = Penner.easeInOutCubic(progressHalf);
			
			ColorDistortionFilter.instance(p).setAmplitude(progressHalfEased * 2.5f);
			ColorDistortionFilter.instance(p).applyTo(pg);
			
			SphereDistortionFilter.instance(p).setAmplitude(progressHalfEased * 1.25f);
			SphereDistortionFilter.instance(p).applyTo(pg);
		}
		
		ImageUtil.copyImage(pg, p.g);
		
		if(KeyboardState.keyTriggered(' ')) Renderer.saveBufferToDisk(pg);
	}

	protected void drawZoomGrid() {
		// split for 
		float progressHalf = (FrameLoop.progress() < 0.5f) ? FrameLoop.progress() * 2f : 1f - (FrameLoop.progress() - 0.5f) * 2f; 
		float progressHalfEased = Penner.easeInOutQuad(progressHalf);
		
		pg.strokeWeight(1f * 4f - 3.5f * progressHalfEased);
		float gridW = pg.width;
		float gridH = pg.height;
//		float gridCells = FrameLoop.osc(0.01f, 1, 25);
		float gridCells = 3f + 20f * progressHalfEased;
		gridCells = 15f;
		float spacing = pg.width / gridCells; // 5 + pg.width * Mouse.xNorm;
		float dashesPerCell = 8;//FrameLoop.osc(0.01f, 1, 6);
		float dashOffset = 0.5f;//(FrameLoop.progress() * 12f) % 2f;
		float dashLength = spacing/dashesPerCell; //  spacing/2f;//0.05f * Mouse.x;
		float numRows = (float) gridH / spacing;
		float numCols = (float) gridW / spacing;
		float cellOffset = (P.floor(gridCells) % 2 == 0) ? 
				gridCells % 1 : 
				gridCells % 2; 	// expand outer cells
		pg.translate(spacing/2f * cellOffset - spacing/2f, spacing/2f * cellOffset - spacing/2f);
//		pg.translate(Mouse.x, Mouse.y);
		boolean roundsDashLength = false; // pg.frameCount % 200 > 100;
		
		// draw rows & cols
		for (float row = -1; row <= numRows + 4; row++) {
			float y = row * spacing;
//			if(P.floor(gridCells) % 2 == 1 && row % 1 == 0) dashOffset *= -1f;
			Shapes.drawDashedLine(pg, -spacing, y, 0, gridW + spacing, y, 0, dashLength, dashOffset, roundsDashLength, false);
		}
		for (float col = -1; col <= numCols + 4; col++) {
			float x = col * spacing;
			Shapes.drawDashedLine(pg, x, -spacing, 0, x, gridH + spacing, 0, dashLength, dashOffset, roundsDashLength, false);
		}
		
		// draw inner
		PG.setDrawCenter(pg);
		float drawRows = numRows + 4;
		float drawCols = numCols + 4;
		for (float row = -1; row <= drawRows; row++) {
			float y = row * spacing;
			for (float col = -1; col <= drawCols; col++) {
				float x = col * spacing;
				
				
				float distInCells = p.dist(drawRows / 2, drawCols / 2, drawRows/2-row, drawCols/2-col);
//				pg.ellipse(x, y, spacing, spacing);
				pg.push();
				pg.translate(x + spacing/2f, y + spacing/2f);
				pg.image(logoSmiley, 0, 0, spacing, spacing);
//				pg.fill(255,0,0);
//				pg.text(row + ", " + distInCells, 10, 10);
				pg.rotate(FrameLoop.progressRads() * -1f);
				pg.image(logoText, 0, 0, spacing, spacing);
				pg.pop();
			}
		}
	}
	
	protected void drawBasicGrid() {
		pg.strokeWeight(1.5f);
		float spacing = 5 + pg.width * Mouse.xNorm;
		float dashLength = spacing/8f; //  spacing/2f;//0.05f * Mouse.x;
		float numRows = (float) pg.height / spacing;
		float numCols = (float) pg.width / spacing;
		boolean roundsDashLength = false; // pg.frameCount % 200 > 100;
		for (float row = 0; row < numRows; row++) {
			float y = row * spacing;
			Shapes.drawDashedLine(pg, 0, y, 0, pg.width, y, 0, dashLength, 0.5f, roundsDashLength, false);
		}
		for (float col = 0; col < numCols; col++) {
			float x = col * spacing;
			Shapes.drawDashedLine(pg, x, 0, 0, x, pg.height, 0, dashLength, 0.5f, roundsDashLength, false);
		}
	}
}