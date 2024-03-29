
package com.haxademic.demo.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.GLBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;

public class Demo_OpenGLUtil_setBlendModeAll
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// These custom blend mode behave differently between the main Applet drawing surface and an offscreen PGraphics buffer. Hence the drawBuffer toggle below.
	// Also! These custom blend modes often require drawing with some alpha transparency to really take full effect. There are 2 collections of presets that work better in either alpha situation.
	
	protected int blendSrcIndex = 0;
	protected int blendDestIndex = 0;
	protected int blendEquationIndex = 0;
	protected boolean mouseControlled = true;
	protected boolean drawBuffer = false;
	
	protected PGraphics buffer;

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') randomBlendParams();
		if(p.key == 'c') printBlendIndices();
		if(p.key == 'm') mouseControlled = !mouseControlled;
		if(p.key == 'b') drawBuffer = !drawBuffer;
	}
	
	protected void firstFrame() {
		buffer = p.createGraphics(p.width, p.height, PRenderers.P2D);
	}

	protected void randomBlendParams() {
		blendSrcIndex = MathUtil.randRange(0, GLBlendModes.blendFunctions.length - 1);
		blendDestIndex = MathUtil.randRange(0, GLBlendModes.blendFunctions.length - 1);
		blendEquationIndex = MathUtil.randRange(0, GLBlendModes.blendEquations.length - 1);
	}

	protected void loadPresetFromMouse(float progress) {
		int[] preset = GLBlendModes.presets[P.floor(progress * GLBlendModes.presets.length)];
		blendSrcIndex = preset[0];
		blendDestIndex = preset[1];
		blendEquationIndex = preset[2];
	}
	
	protected void printBlendIndices() {
		P.println(blendSrcIndex, ", ", blendDestIndex, ", ", blendEquationIndex);
	}

	protected void drawApp() {
		DebugView.setValue("mouseControlled", mouseControlled);
		DebugView.setValue("drawBuffer", drawBuffer);
		
		p.background(0);
		PGraphics pg = (drawBuffer) ? buffer : p.g;
		
		// set context
		if(drawBuffer) pg.beginDraw();
		pg.background(0);
		pg.noStroke();
		PG.setDrawCenter(pg);

		// show presets or use spacebar to find new presets
		if(mouseControlled) loadPresetFromMouse(Mouse.xNorm);
		
		// draw under image
		ImageUtil.drawImageCropFill(DemoAssets.smallTexture(), pg, true);
		
		// set custom blend mode on context
		OpenGLUtil.setBlending( pg, true );
		OpenGLUtil.setBlendModeCustom(pg, GLBlendModes.blendFunctions[blendSrcIndex], GLBlendModes.blendFunctions[blendDestIndex], GLBlendModes.blendEquations[blendEquationIndex]);
//		GLBlendModes.setBlendModeFromPreset(pg, P.floor(Mouse.xNorm * GLBlendModes.presets.length));

		// draw shapes
		float numShapes = 100;
		for( float i=0; i < numShapes; i++ ) {
			float red = i/numShapes * 255f;
			float green = 255f - i/numShapes * 255f;
			float blue = 255f - i/numShapes * 255f;
			pg.fill(red, green, blue, 20);

			float radius = 180 + 26f * P.sin(i+p.frameCount*0.02f);
			float radians = ((i+p.frameCount*0.25f)/P.TWO_PI) * 0.5f;// * P.sin((i/10f+p.frameCount/10f));
			float xRot = P.sin(radians);
			float yRot = P.cos(radians);
			pg.pushMatrix();
			pg.translate(pg.width/2f + xRot * radius, pg.height/2f + yRot * radius);
			pg.rotate(-radians);
			pg.rect(0, 0, radius/3f, radius/3f);
			pg.popMatrix();
		}

		// draw over image
//		PG.setPImageAlpha(pg,  0.5f);
		ImageUtil.drawImageCropFill(DemoAssets.textureJupiter(), pg, true);
//		PG.resetPImageAlpha(pg);
		
		// reset blending to default
		OpenGLUtil.setBlending( pg, false );
		pg.endDraw();
		
		// draw buffer to screen
		if(drawBuffer) {
			PG.setDrawCorner(p);
			p.image(pg, 0, 0);
		}
	}

}
