
package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;

@SuppressWarnings("serial")
public class OpenGLBlendTest
extends PAppletHax {

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "800" );
		_appConfig.setProperty( "height", "800" );
	}

	public void setup() {
		super.setup();	
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
	}


	public void drawApp() {
		p.background(0);
		p.noStroke();
		
		OpenGLUtil.setBlending( p.g, true );
		OpenGLUtil.setBlendMode( p.g, OpenGLUtil.Blend.ADD_SATURATE );

		DrawUtil.setDrawCenter(p);
		DrawUtil.setBasicLights(p);

//		p.blendMode(P.SCREEN);
//		p.blendMode(P.MULTIPLY);

		float numEls = 2000;
		
		for( float i=0; i < numEls; i++ ) {
			float red = i/numEls * 255f;
			float green = 255f - i/numEls * 255f;
			float blue = 255f - i/numEls * 255f;
			p.fill(red, green, blue, 20);
			
			float radius = 180 + 26 * P.sin(i * 0.2f);
			float radians = ((i+p.frameCount)/P.TWO_PI) * 0.5f;// * P.sin((i/10f+p.frameCount/10f));
			float xRot = P.sin(radians);
			float yRot = P.cos(radians);
			p.pushMatrix();
			p.translate(p.width/2f + xRot * radius, p.height/2f + yRot * radius);
			p.rotate(-radians);
			p.ellipse(
					0,
					0,
					radius/3f,
					radius/3f
			);
			p.popMatrix();
		}

//		OpenGLUtil.enableBlending( p, false );
//		OpenGLUtil.setBlendMode( p, OpenGLUtil.NORMAL );
	}

}
