package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.RadialRipplesFilter;
import com.haxademic.core.draw.filters.pshader.WobbleFilter;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;

public class Demo_Shapes_drawDashedLine_Grid 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		int FRAMES = 600;
		Config.setAppSize(960, 960);
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, (FRAMES * 1) + 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (FRAMES * 2) + 1 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	protected void drawApp() {
		PG.setTextureRepeat(p.g, true);
		p.background(ColorsHax.COLOR_GROUPS[5][0]);
		p.stroke(ColorsHax.COLOR_GROUPS[5][2]);
		
		drawZoomGrid();
//		drawBasicGrid();
		
		
		WobbleFilter.instance(p).setTime(p.frameCount * 0.01f);
		WobbleFilter.instance(p).setSpeed(2f); // Mouse.xNorm * 3f);
		WobbleFilter.instance(p).setStrength(Mouse.xNorm);
		WobbleFilter.instance(p).setSize(Mouse.yNorm * 5f);
//		WobbleFilter.instance(p).applyTo(p.g);
		
		RadialRipplesFilter.instance(p).setTime(p.frameCount * 0.01f);// * Mouse.yNorm);
		RadialRipplesFilter.instance(p).setAmplitude(Mouse.xNorm * 4f);
		RadialRipplesFilter.instance(p).applyTo(p.g);
	}

	protected void drawZoomGrid() {
		float progressHalf = (FrameLoop.progress() < 0.5f) ? FrameLoop.progress() * 2f : 1f - (FrameLoop.progress() - 0.5f) * 2f; 
		float progressHalfEased = Penner.easeInOutQuad(progressHalf);
		p.strokeWeight(FrameLoop.osc(0.01f, 4, 2));
		float gridW = p.width;
		float gridH = p.height;
//		float gridCells = FrameLoop.osc(0.01f, 1, 25);
		float gridCells = 1f + 25f * progressHalfEased;
		float spacing = p.width / gridCells; // 5 + p.width * Mouse.xNorm;
		float dashesPerCell = 4;//FrameLoop.osc(0.01f, 1, 6);
		float dashOffset = FrameLoop.count(0.06f) % 2f;
		float dashLength = spacing/dashesPerCell; //  spacing/2f;//0.05f * Mouse.x;
		float numRows = (float) gridH / spacing;
		float numCols = (float) gridW / spacing;
		float cellOffset = (P.floor(gridCells) % 2 == 0) ? 
				gridCells % 1 : 
				gridCells % 2; 	// expand outer cells
		p.translate(spacing/2f * cellOffset - spacing/2f, spacing/2f * cellOffset - spacing/2f);
//		p.translate(Mouse.x, Mouse.y);
		boolean roundsDashLength = false; // p.frameCount % 200 > 100;
		
		// draw rows & cols
		for (float row = -1; row <= numRows + 4; row++) {
			float y = row * spacing;
//			if(P.floor(gridCells) % 2 == 1 && row % 1 == 0) dashOffset *= -1f;
			Shapes.drawDashedLine(p.g, -spacing, y, 0, gridW + spacing, y, 0, dashLength, dashOffset, roundsDashLength, false);
		}
		for (float col = -1; col <= numCols + 4; col++) {
			float x = col * spacing;
			Shapes.drawDashedLine(p.g, x, -spacing, 0, x, gridH + spacing, 0, dashLength, dashOffset, roundsDashLength, false);
		}
	}
	
	protected void drawBasicGrid() {
		p.strokeWeight(1.5f);
		float spacing = 5 + p.width * Mouse.xNorm;
		float dashLength = spacing/8f; //  spacing/2f;//0.05f * Mouse.x;
		float numRows = (float) p.height / spacing;
		float numCols = (float) p.width / spacing;
		boolean roundsDashLength = false; // p.frameCount % 200 > 100;
		for (float row = 0; row < numRows; row++) {
			float y = row * spacing;
			Shapes.drawDashedLine(p.g, 0, y, 0, p.width, y, 0, dashLength, 0.5f, roundsDashLength, false);
		}
		for (float col = 0; col < numCols; col++) {
			float x = col * spacing;
			Shapes.drawDashedLine(p.g, x, 0, 0, x, p.height, 0, dashLength, 0.5f, roundsDashLength, false);
		}
	}
}