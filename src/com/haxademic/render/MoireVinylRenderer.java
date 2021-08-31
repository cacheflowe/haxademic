package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;

public class MoireVinylRenderer 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected boolean ANIMATION_MODE = false;
	protected int FRAMES = 60 * 20;
	protected boolean shouldRender = false;
	protected boolean drawBezier = false;
	
	protected String NUM_LINES = "NUM_LINES";
	protected String LINE_DETAIL = "LINE_DETAIL";
	protected String _yOscAmp = "yOscAmp";
	protected String _yOscFreq = "yOscFreq";
	protected String _yOscOffset = "yOscOffset";
	protected String _xDispAmp = "xDispAmp";
	protected String _xDispOffset1 = "xDispOffset1";
	protected String _xDispFreq = "xDispFreq";
	protected String _xDispFreq2 = "xDispFreq2";
	protected String _xDispOffset2 = "xDispOffset2";
	protected String _midSpreadAmp = "midSpreadAmp";
	protected String SHOW_DOORS = "SHOW_DOORS";
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, App.PG_W/2 );
		Config.setProperty( AppSettings.HEIGHT, App.PG_H/2 );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		if(ANIMATION_MODE == true) {
			Config.setAppSize(1024, 768);
			Config.setProperty( AppSettings.RENDERING_MOVIE, true );
			Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 1 );
			Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );	
		}
	}
	
	protected void firstFrame() {
		UI.addTitle("Resolution");
		UI.addSlider(NUM_LINES, 250, 10, 500, 1, false);
		UI.addSlider(LINE_DETAIL, 100, 10, 700, 10, false);
		UI.addTitle("Lines Distort");
		UI.addSlider(_yOscAmp, 83, 0, 150, 1, false);
		UI.addSlider(_yOscFreq, 74, 10, 1000, 2, false);
		UI.addSlider(_yOscOffset, 0.48f, 0, P.TWO_PI * 2, 0.01f, false); // p.frameCount * 0.02f;
		UI.addSlider(_xDispAmp, 45, 0, 300, 1, false);
		UI.addSlider(_xDispFreq, 67, 1, 1000, 1, false);
		UI.addSlider(_xDispOffset1, 0.48f, 0, P.TWO_PI * 2, 0.01f, false); // p.frameCount * 0.02f;
		UI.addSlider(_xDispFreq2, 69, 1, 1000, 1, false);
		UI.addSlider(_xDispOffset2, 5.7f, 0, P.TWO_PI * 2, 0.01f, false); // p.frameCount * 0.02f;
		UI.addSlider(_midSpreadAmp, 400, 0, 5000, 4, false);
		UI.addTitle("Simulation");
		UI.addToggle(SHOW_DOORS, false, false);
	}

	protected void drawApp() {
		// start context & render
		if(shouldRender == true) p.beginRecord(PDF, FileUtil.haxademicOutputPath() + "pdf/shirt-"+SystemUtil.getTimestampFine()+".pdf");
		p.background( 255 );
		
		// automated animation
		if(ANIMATION_MODE == true) {
			float loopOffset = P.PI;// -P.QUARTER_PI * 1.85f;
			UI.setValue(NUM_LINES, 80);
			UI.setValue(LINE_DETAIL, 100);
			UI.setValue(_yOscAmp, 20f + 20f * P.cos(loopOffset + FrameLoop.progressRads()));
			UI.setValue(_yOscFreq, 80f + 20f * P.sin(2.5f + loopOffset + FrameLoop.progressRads()));
			UI.setValue(_yOscOffset, FrameLoop.count(0.025f));
			UI.setValue(_xDispAmp, 20f + 20f * P.cos(loopOffset + FrameLoop.progressRads()));
			UI.setValue(_xDispFreq, 80f + 20f * P.sin(1 + loopOffset + FrameLoop.progressRads()));
			UI.setValue(_xDispFreq2, 80f + 20f * P.sin(1.5f + loopOffset + FrameLoop.progressRads()));
			UI.setValue(_xDispOffset2, FrameLoop.count(0.02f));
			UI.setValue(_midSpreadAmp, 300f + 300f * P.cos(loopOffset + FrameLoop.progressRads()));
		}

		// draw moire lines
		float numLines = UI.value(NUM_LINES); // 150f;
		DebugView.setValue("Line Height (pair)", (9.5f * 12f) / numLines);
		float verticalOverdraw = 10f;
		float spacing = (p.height + 2f * verticalOverdraw) / numLines;
		for (int i = 0; i < numLines; i++) {
			float y = -verticalOverdraw + i * spacing;
			float strokeW = spacing/2f;
			float horizDisplaceFreq = 0.03f;
			float vertDisplaceFreq = 0.02f;
			float vertDisplaceAmp = 300f;
			p.noFill();
			p.stroke(0);
			p.strokeWeight(strokeW);
			
			// draw bezier
			if(drawBezier == true) {
//				p.beginShape();
				p.bezier(
					0, y,
					p.width/4, y,
					p.width * (0.25f + 0.1f * P.sin(FrameLoop.count(0.01f) + i * horizDisplaceFreq)), y + P.sin(i * vertDisplaceFreq) * vertDisplaceAmp,
	//				p.width * (0.4f + 0.2f * P.sin(FrameLoop.count(0.006f) + i * horizDisplaceFreq)), y + P.cos(i * vertDisplaceFreq) * vertDisplaceAmp,
					p.width/2, y 
				);
				p.bezier(
					p.width/2, y,
					p.width * (0.75f - 0.1f * P.sin(FrameLoop.count(0.01f) + i * horizDisplaceFreq)), y - P.sin(i * vertDisplaceFreq) * vertDisplaceAmp,
	//					p.width * (0.4f + 0.2f * P.sin(FrameLoop.count(0.006f) + i * horizDisplaceFreq)), y + P.cos(i * vertDisplaceFreq) * vertDisplaceAmp,
					p.width * 0.75f, y,
					p.width, y 
				);
//				p.endShape();
			}
			
			// draw custom line
			else {
				p.beginShape();
				p.fill(0);
				p.noStroke();
				
				// params
				float xPad = 100;
				int subdivisions = UI.valueInt(LINE_DETAIL); // 100;	// means 100 black/white stripe pairs for 200 total lines
				if(shouldRender) subdivisions = 2000;
				float stepW = p.width / (float) subdivisions;
				float yOscAmp = UI.valueEased(_yOscAmp);
				float yOscFreq = UI.valueEased(_yOscFreq);
				float yOscOffset = UI.valueEased(_yOscOffset);
				float xDispAmp = UI.valueEased(_xDispAmp);
				float xDispFreq = UI.valueEased(_xDispFreq);
				float xDispOffset1 = UI.valueEased(_xDispOffset1);
				float xDispFreq2 = UI.valueEased(_xDispFreq2);
				float xDispOffset2 = UI.valueEased(_xDispOffset2);
				float midSpreadAmp = UI.valueEased(_midSpreadAmp);
				
				// left to right
				for(float x = -xPad; x < p.width+xPad; x += stepW) {
					float displaceAmp = (x < p.width / 2) ? Penner.easeInOutCubic(P.map(x, 0, width/2, 0, 1)) : Penner.easeInOutCubic(P.map(x, width/2, width, 1, 0)); 
					float vY = y + yOscAmp * P.sin(yOscOffset + x/yOscFreq) * displaceAmp;// + 100 * P.sin(x/300f);
					float growYMid = (y < p.height / 2) ? P.map(y, p.height/2, 0, 0, -1) : P.map(y, p.height/2, p.height, 0, 1);
					vY += growYMid * displaceAmp * midSpreadAmp;
					vY += 0.2f * displaceAmp * xDispAmp * P.cos(xDispOffset2 + y/xDispFreq2);
					float vX = x + displaceAmp * xDispAmp * P.sin(xDispOffset1 + x/xDispFreq) * P.sin(xDispOffset2 + y/xDispFreq2);
					p.vertex(vX, vY);
				}
				y += strokeW;	// for line thickness
				// back, right to left
				for(float x = p.width+xPad; x > -xPad; x -= stepW) {
					float displaceAmp = (x < p.width / 2) ? Penner.easeInOutCubic(P.map(x, 0, width/2, 0, 1)) : Penner.easeInOutCubic(P.map(x, width/2, width, 1, 0)); 
					float vY = y + yOscAmp * P.sin(yOscOffset + x/yOscFreq) * displaceAmp;// + 100 * P.sin(x/300f);
					float growYMid = (y < p.height / 2) ? P.map(y, p.height/2, 0, 0, -1) : P.map(y, p.height/2, p.height, 0, 1);
					vY += growYMid * displaceAmp * midSpreadAmp;
					vY += 0.2f * displaceAmp * xDispAmp * P.cos(xDispOffset2 + y/xDispFreq2);
					float vX = x + displaceAmp * xDispAmp * P.sin(xDispOffset1 + x/xDispFreq) * P.sin(xDispOffset2 + y/xDispFreq2);
					p.vertex(vX, vY);
				}
				p.endShape(P.CLOSE);
			}
			
		}
		
		// wrap up render 
		if(shouldRender == true) p.endRecord();
		shouldRender = false;
		
		// draw extras
		if(UI.valueToggle(SHOW_DOORS)) drawDebug();
	}
	
	protected void drawDebug() {
		
		// wall crease
		p.rect(p.width * App.WALL_SPLIT, 0, 2, p.height);
		// door left
//		p.fill(0, 155);
		p.stroke(0);
		p.noFill();
		p.rect(p.width * 0.225f, p.height, p.width * 0.16f, p.height * -0.7f);
		// door right
		p.rect(p.width * 0.625f, p.height, p.width * 0.16f, p.height * -0.7f);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') shouldRender = true;
	}
	
	public class App {
		public static final float WALL_LEFT_WIDTH_FT = 12.78125f;
		public static final float WALL_RIGHT_WIDTH_FT = 14.25f;
		public static final float WALL_SPLIT = WALL_LEFT_WIDTH_FT / (WALL_LEFT_WIDTH_FT + WALL_RIGHT_WIDTH_FT);
		public static final float WALL_SPLIT_RIGHT = 1f - WALL_SPLIT;
		public static final float WALL_CENTER_OFFSET = 0.5f - WALL_SPLIT;	// for shader centering
		public static final float WALL_HEIGHT_FT = 9.5f;
		public static final float WALL_HEIGHT_PX = 1200; 	// was 960??
		public static final float WALL_HEIGHT_FT_TO_PX = WALL_HEIGHT_PX / WALL_HEIGHT_FT;
		public static final float WALL_LEFT_WIDTH_PX = WALL_HEIGHT_FT_TO_PX * WALL_LEFT_WIDTH_FT;
		public static final float WALL_RIGHT_WIDTH_PX = WALL_HEIGHT_FT_TO_PX * WALL_RIGHT_WIDTH_FT;
		public static final int PG_W = (int) (WALL_LEFT_WIDTH_PX + WALL_RIGHT_WIDTH_PX);
		public static final int PG_H = (int) WALL_HEIGHT_PX;
//		public static final int APP_W = 1920*2; // PG_W/2
//		public static final int APP_H = 1200;   // PG_H/2
		public static final int APP_W = PG_W/2;
		public static final int APP_H = PG_H/2;
		public static final boolean CAMERAS_ACTIVE = false; 
	}
}
