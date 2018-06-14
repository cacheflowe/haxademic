
package com.haxademic.demo.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.GLBlendModes;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.math.MathUtil;

public class Demo_OpenGLUtil_setBlendModeAll
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int blendSrcIndex = 0;
	protected int blendDestIndex = 0;
	protected int blendEquationIndex = 0;
	protected boolean mouseControlled = true;

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') randomBlendParams();
		if(p.key == 'c') printBlendIndices();
		if(p.key == 'm') mouseControlled = !mouseControlled;
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

	public void drawApp() {
		// set context
		p.background(0);
		p.noStroke();
		DrawUtil.setDrawCenter(p);

		// show presets or use spacebar to find new presets
		if(mouseControlled) loadPresetFromMouse(p.mousePercentX());
		
		// set custom blend mode on context
		OpenGLUtil.setBlending( p.g, true );
		OpenGLUtil.setBlendModeCustom(p.g, GLBlendModes.blendFunctions[blendSrcIndex], GLBlendModes.blendFunctions[blendDestIndex], GLBlendModes.blendEquations[blendEquationIndex]);
		// GLBlendModes.setBlendModeFromPreset(p.g, P.floor(p.mousePercentX() * GLBlendModes.presets.length));

		// draw shapes
		float numShapes = 100;
		for( float i=0; i < numShapes; i++ ) {
			float red = i/numShapes * 255f;
			float green = 255f - i/numShapes * 255f;
			float blue = 255f - i/numShapes * 255f;
			p.fill(red, green, blue, 20);

			float radius = 180 + 26f * P.sin(i+p.frameCount*0.02f);
			float radians = ((i+p.frameCount*0.25f)/P.TWO_PI) * 0.5f;// * P.sin((i/10f+p.frameCount/10f));
			float xRot = P.sin(radians);
			float yRot = P.cos(radians);
			p.pushMatrix();
			p.translate(p.width/2f + xRot * radius, p.height/2f + yRot * radius);
			p.rotate(-radians);
			p.rect(0, 0, radius/3f, radius/3f);
			p.popMatrix();
		}

		// reset blending to default
		OpenGLUtil.setBlending( p.g, false );
	}

}
