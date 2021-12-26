package com.haxademic.core.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.pg32.PGraphics32;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PMatrix3D;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.Texture;

public class PG {
	
	//////////////////////////////
	// BUFFER INIT
	//////////////////////////////
	
	public static PGraphics newPG(int w, int h) {
		return newPG(w, h, true, true);
	}
	
	public static PGraphics newPG2DFast(int w, int h) {
		PGraphics newPG = P.p.createGraphics(w, h, PRenderers.P2D);
//		newPG.noSmooth();
	    ((PGraphicsOpenGL)newPG).textureSampling(2);
		newPG.beginDraw();
		newPG.background(0, 0);
		newPG.noStroke();
//		newPG.hint(PConstants.DISABLE_DEPTH_SORT);
//		newPG.hint(PConstants.DISABLE_DEPTH_TEST);
//		newPG.hint(PConstants.DISABLE_DEPTH_MASK);
		newPG.endDraw();
		PG.setTextureRepeat(newPG, false);
		return newPG;
	}
	
	public static PGraphics newDataPG(int w, int h) {
//		PGraphics newPG = P.p.createGraphics(w, h, PRenderers.P3D);
//		PGraphics newPG = P.p.createGraphics(w, h, P.P32);
		PGraphics newPG = PGraphics32.createGraphics(P.p, w, h);
		newPG.noSmooth();
	    ((PGraphicsOpenGL)newPG).textureSampling(2);
		newPG.beginDraw();
//		newPG.hint(P.DISABLE_TEXTURE_MIPMAPS);
		newPG.hint(PConstants.DISABLE_DEPTH_SORT);
		newPG.hint(PConstants.DISABLE_DEPTH_TEST);
		newPG.hint(PConstants.DISABLE_DEPTH_MASK);
		newPG.background(0, 0);
		newPG.noStroke();
		newPG.endDraw();
		// moved these calls into this block for a full test of options
//		OpenGLUtil.setTextureQualityLow(newPG);		// necessary for proper texel lookup in GLSL!
//		OpenGLUtil.optimize2D(newPG);
		return newPG;
	}
	
	public static PGraphics newPG(int w, int h, boolean smooth, boolean hasAlpha) {
		PGraphics newPG = P.p.createGraphics(w, h, PRenderers.P3D);
		if(smooth == false) newPG.noSmooth();
		if(hasAlpha == true) {
			newPG.beginDraw();
			newPG.background(0, 0);
			newPG.noStroke();
			newPG.endDraw();
		}
		PG.setTextureRepeat(newPG, true);
		return newPG;
	}
	
	public static PGraphics newPG32(int w, int h, boolean smooth, boolean hasAlpha) {
		PGraphics newPG = PGraphics32.createGraphics(P.p, w, h);
		if(smooth == false) newPG.noSmooth();	// turning off smoothing seems to benefit shader-only drawing
		if(hasAlpha == true) {
			newPG.beginDraw();
			newPG.background(0, 0);
			newPG.noStroke();
			newPG.endDraw();
		}
		PG.setTextureRepeat(newPG, true);
		return newPG;
	}
	
	//////////////////////////////
	// CONTEXT HELPERS
	//////////////////////////////
	
	public static void resetGlobalProps( PApplet p ) {
		resetGlobalProps(p.g);
	}
	
	public static void resetGlobalProps(PGraphics pg) {
		// p.resetMatrix();
		pg.colorMode( P.RGB, 255, 255, 255, 255 );
		pg.fill( 0, 255, 0, 255 );
		pg.stroke( 0, 255, 0, 255 );
		pg.strokeWeight( 1 );
		pg.camera();
		setDrawCenter(pg);
	}

	public static void push(PGraphics pg) {
		pg.pushMatrix();
		pg.pushStyle();
	}
	public static void pop(PGraphics pg) {
		pg.popStyle();
		pg.popMatrix();
	}
	
	public static void setCenterScreen(PApplet p) {
		setCenterScreen(p.g);
	}
	
	public static void setCenterScreen(PGraphics p) {
		p.translate( p.width/2, p.height/2, 0 );
	}

	public static void setDrawCorner( PApplet p ) { 
		setDrawCorner(p.g);
	}
	public static void setDrawCorner( PGraphics p ) {
		p.imageMode( PConstants.CORNER );
		p.rectMode( PConstants.CORNER );
		p.ellipseMode( PConstants.CORNER );
		p.shapeMode( PConstants.CORNER );
	}
	
	public static void setDrawCenter( PApplet p ) {
		setDrawCenter(p.g);
	}
	
	public static void setDrawCenter( PGraphics p ) {
		p.imageMode( PConstants.CENTER );
		p.rectMode( PConstants.CENTER );
		p.ellipseMode( PConstants.CENTER );
		p.shapeMode( PConstants.CENTER );
	}
	
	public static void setDrawFlat2d( PApplet p, boolean is2d ) {
		setDrawFlat2d(p.g, is2d);
	};
	
	public static void setDrawFlat2d( PGraphics p, boolean is2d ) {
		if( is2d ) {
			p.hint( P.DISABLE_DEPTH_TEST );
		} else {
			p.hint( P.ENABLE_DEPTH_TEST );
		}
	}
	
	public static void setTextureRepeat( PApplet p, boolean doesRepeat ) {
		setTextureRepeat(p.g, doesRepeat);
	};
	
	public static void setTextureRepeat(PGraphics pg, boolean doesRepeat) {
		if( doesRepeat == true ) 
			pg.textureWrap(Texture.REPEAT);
		else 
			pg.textureWrap(Texture.CLAMP);
	}
	
	//////////////////////////////
	// LIGHTING
	//////////////////////////////
	
	public static void setBasicLights( PApplet p ) {
		setBasicLights(p.g);
	}
	
	public static void setBasicLights( PGraphics pg ) {
		pg.shininess(500); 
		pg.lights();
		pg.ambientLight(25, 25, 25, 0, 0, 6000);
		pg.ambientLight(10, 10, 10, 0, 0, -6000);
	}

	public static void setBetterLights( PApplet p ) {
		setBetterLights(p.g);
	}
	
	public static void setBetterLights( PGraphics p ) {
		// setup lighting props
		p.ambient(127);
		p.lightSpecular(130, 130, 130); 
		p.directionalLight(200, 200, 200, -0.0f, -0.0f, 1); 
		p.directionalLight(200, 200, 200, 0.0f, 0.0f, -1); 
		p.specular(p.color(200)); 
		p.shininess(5.0f); 
	}
	
	//////////////////////////////
	// IMAGE ALPHA
	//////////////////////////////
		
	public static void setColorForPImage( PApplet p ) {
		setColorForPImage(p.g);
	}
	public static void setColorForPImage( PGraphics p ) {
		p.fill( 255, 255, 255, 255 );
	}
	
	public static void setPImageAlpha( PApplet p, float alpha ) {
		setPImageAlpha(p.g, alpha);
	};
	public static void setPImageAlpha( PGraphics p, float alpha ) {
		p.tint( 255, alpha * 255 );
	}
	
	public static void resetPImageAlpha( PApplet p ) {
		resetPImageAlpha(p.g);
	};
	public static void resetPImageAlpha( PGraphics p ) {
		p.tint( 255 );
	}
	
	//////////////////////////////
	// "CAMERA" HELPERS
	//////////////////////////////
	
	public static void basicCameraFromMouse(PGraphics pg) {
		basicCameraFromMouse(pg, 1f);
	}
	
	public static void basicCameraFromMouse(PGraphics pg, float amp) {
		pg.rotateX(P.map(Mouse.yEasedNorm, 0, 1, P.PI * amp, -P.PI * amp));
		pg.rotateY(P.map(Mouse.xEasedNorm, 0, 1, -P.PI * amp, P.PI * amp));
	}
	
	public static PMatrix3D getModelviewInv(PGraphics pg) {
		return ((PGraphicsOpenGL) pg).modelviewInv;
	}
	
	//////////////////////////////
	// DRAWING HELPERS
	//////////////////////////////

	public static void fadeInOut(PGraphics pg, int color, int startFrame, int stopFrame, int transitionFrames) {
		int frames = stopFrame - startFrame;
		PG.setDrawCorner(pg);
		if(P.p.frameCount <= startFrame + transitionFrames) {
			pg.fill(color, P.map(P.p.frameCount, 1f, transitionFrames, 255f, 0));
			pg.rect(0,0,pg.width, pg.height);
		} else if(P.p.frameCount >= frames - transitionFrames) {
			pg.fill(color, P.map(P.p.frameCount, frames - transitionFrames, frames, 0, 255f));
			pg.rect(0, 0, pg.width, pg.height);
		} 
	}
	
	// only works properly on PGraphics buffers
	// feedback distance should only be even numbers
	public static void feedback(PGraphics pg, float feedbackDistance) {
		feedback(pg, -1, -1, feedbackDistance);
	}
	
	public static void feedback(PGraphics pg, int color, float colorFade, float feedbackDistance) {
		PG.setDrawCorner(pg);
		PG.setDrawFlat2d(pg, true);
		pg.copy(
			pg, 
			0, 
			0, 
			pg.width, 
			pg.height, 
			P.round(-feedbackDistance), 
			P.round(-feedbackDistance), 
			P.round(pg.width + feedbackDistance * 2f), 
			P.round(pg.height + feedbackDistance * 2f)
		);
		if(color != -1) {
			pg.fill(color, colorFade * 255f);
			pg.noStroke();
			pg.rect(0, 0, pg.width, pg.height);
		}
		PG.setDrawFlat2d(pg, false);
	}
	
	// from: http://p5art.tumblr.com/post/144205983628/a-small-transparency-tip
	public static void fadeToBlack(PGraphics pg, float blackVal) {
		pg.blendMode(P.SUBTRACT);
		pg.fill(blackVal);
		pg.rect(0, 0, pg.width * 3, pg.height * 3);
		pg.blendMode(P.BLEND);
	}
	
	public static void zoomReTexture(PGraphics pg, float amount) {
		float w = (float) pg.width;
		float h = (float) pg.height;
		float newW = w * amount;
		float newH = h * amount;
		pg.copy(
				(int) (w * 0.5f - newW * 0.5f), 
				(int) (h * 0.5f - newH * 0.5f),
				(int) newW, 
				(int) newH, 
				0, 0, pg.width, pg.height);
	}
	
	public static void rotateRedraw(PGraphics pg, float radians) {
		pg.beginDraw();
		PG.setDrawCenter(pg);
		pg.pushMatrix();
		PG.setCenterScreen(pg);
		pg.rotate(radians);
		pg.image(pg, 0, 0);
		pg.popMatrix();
		PG.setDrawCorner(pg);
		pg.endDraw();
	}
	
	public static void drawStrokedRect(PGraphics pg, float w, float h, float strokeWeight, int colorBg, int colorStroke) {
		int prevRectMode = pg.rectMode;
		pg.push();
		
		// make sure rect stroke is drawing from the same top left as rect bg
		if(prevRectMode == PConstants.CENTER) pg.translate(P.round(-w/2), P.round(-h/2));
		pg.rectMode(PConstants.CORNER);
		
		// rect bg
		pg.noStroke();
		pg.fill(colorBg);
		pg.rect(0, 0, w, h);
		
		// pixel-perfect stroke by drawing 4 rects
		pg.fill(colorStroke);
		pg.rect(0, 0, w, strokeWeight);					// top
		pg.rect(0, h - strokeWeight, w, strokeWeight);	// bottom
		pg.rect(0, 0, strokeWeight, h);					// left
		pg.rect(w - strokeWeight, 0, strokeWeight, h);	// right
		pg.rectMode(prevRectMode);						// reset rectMode to whatever it was before

		pg.pop();
	}
	
	//////////////////////////////
	// PATTERNS
	//////////////////////////////
	
	public static void drawTestPattern(PGraphics pg) {
		pg.beginDraw();
		pg.noStroke();
		
		int cellSize = pg.pixelWidth / 20;
		int twoCells = cellSize * 2;
		for( int x=0; x < pg.width; x+= cellSize) {
			for( int y=0; y < pg.height; y+= cellSize) {
				if( ( x % twoCells == 0 && y % twoCells == 0 ) || ( x % twoCells == cellSize && y % twoCells == cellSize ) ) {
					pg.fill(0);
				} else {
					pg.fill(255);
				}
				pg.rect(x,y,cellSize,cellSize);
			}
		}
		pg.endDraw();
	}

	public static void drawGrid(PGraphics pg, int bgColor, int strokeColor, float cols, float rows, float strokeSize) {
		drawGrid(pg, bgColor, strokeColor, cols, rows, strokeSize, true);
	}
	public static void drawGrid(PGraphics pg, int bgColor, int strokeColor, float cols, float rows, float strokeSize, boolean openContext) {
		// update texture
		if(openContext) {
			pg.beginDraw();
			pg.background(bgColor);
		}
		pg.push();
		pg.fill(strokeColor);
		pg.noStroke();
		float cellW = (float) pg.width / (float) cols;
		cellW -= strokeSize / cols;
		float cellH = (float) pg.height / (float) rows;
		cellH -= strokeSize / rows;
		for (float x = 0; x <= pg.width; x += cellW) {
			pg.rect(x, 0, strokeSize, pg.height);
		}
		for (float y = 0; y <= pg.height; y += cellH) {
			pg.rect(0, y, pg.width, strokeSize);
		}
		pg.pop();
		if(openContext) pg.endDraw();
	}
	
	public static void drawRainbow(PGraphics pg) {
		int colors[] = ColorsHax.PRIDE;
		pg.beginDraw();
		pg.noStroke();
		float colorHeight = P.round(pg.height / colors.length);
		for (int i = 0; i < colors.length; i++) {
			pg.fill(colors[i]);
			pg.rect(0, colorHeight * i, pg.width, colorHeight);
		}
		pg.endDraw();
	}
	
	public static void pixelFlushPattern(PGraphics pg) {
		pixelFlushPattern(pg, true);
	}
	public static void pixelFlushPattern(PGraphics pg, boolean openContext) {
		// run at night to help fix burn-in
		if(openContext) {
			pg.beginDraw();
			pg.background(0);
		}
		pg.noStroke();
		int cellSize = 50;
		int offset = -P.p.frameCount % cellSize;
		for( int x=offset; x < pg.width; x+= cellSize) {
			for( int y=offset; y < pg.height; y+= cellSize) {
				int r = MathUtil.randBoolean() ? 0 : 255;
				int g = MathUtil.randBoolean() ? 0 : 255;
				int b = MathUtil.randBoolean() ? 0 : 255;
				pg.fill(r, g, b);
				pg.rect(x,y,cellSize,cellSize);
			}
		}
		if(openContext) pg.endDraw();
	}
	
	public static void drawOriginAxis(PGraphics pg) {
		drawOriginAxis(pg, pg.height / 6f, 4);
	}
	
	public static void drawOriginAxis(PGraphics pg, float armSize, float thickness) {
		// size calc
		float tipThickness = thickness * 2f;
		float tipSize = thickness * 4f;
		float boxSize = thickness * 2f;
		// font
		PFont debugFont = FontCacher.getFont(DemoAssets.fontInterPath, thickness * 3f);
		// context start
		pg.push();
		pg.noStroke();
		// center box
		pg.fill(0xffffff00);
		pg.box(boxSize);
		// y
//		pg.fill(0xff00ff00);
		FontCacher.setFontOnContext(pg, debugFont, 0xff00ff00, 1f, PTextAlign.LEFT, PTextAlign.TOP);
		pg.pushMatrix();
		pg.translate(0, -armSize / 2f, 0);
		pg.box(thickness, armSize, thickness);
		pg.translate(0, -armSize / 2, 0);
		pg.pushMatrix();
		pg.translate(0, -debugFont.getSize(), 0);
		pg.text("Y", tipThickness, 0);
		pg.popMatrix();
		pg.rotateX(P.HALF_PI);
		Shapes.drawPyramid(pg, tipSize, tipThickness, true);
		pg.popMatrix();
		// x
		pg.fill(0xffff0000);
		pg.pushMatrix();
		pg.translate(armSize / 2f, 0, 0);
		pg.box(armSize, thickness, thickness);
		pg.translate(armSize / 2f, 0, 0);
		pg.pushMatrix();
		pg.translate(0, -boxSize * 2.75f, 0);
		pg.text("X", 0, 0);
		pg.popMatrix();
		pg.rotateY(P.HALF_PI);
		Shapes.drawPyramid(pg, tipSize, tipThickness, true);
		pg.popMatrix();
		// z
		pg.fill(0xff0000ff);
		pg.pushMatrix();
		pg.translate(0, 0, armSize / 2f);
		pg.box(thickness, thickness, armSize);
		pg.translate(0, 0, armSize / 2f);
		pg.pushMatrix();
		pg.translate(0, -debugFont.getSize() / 2f * 1.3f, 0);
		pg.text("Z", tipThickness, 0);
		pg.popMatrix();
		pg.rotateZ(P.HALF_PI);
		Shapes.drawPyramid(pg, tipSize, tipThickness, true);
		pg.popMatrix();
		// context end
		pg.pop();
	}

	public static void drawBorder(PGraphics pg, int color, float padd) {
		pg.push();
		PG.setDrawCorner(pg);
		pg.fill(color);
		pg.noStroke();
		pg.rect(0, 0, pg.width, padd);
		pg.rect(0, 0, padd, pg.height);
		pg.rect(0, pg.height - padd, pg.width, padd);
		pg.rect(pg.width - padd, 0, padd, pg.height);
		pg.pop();

	}
}
