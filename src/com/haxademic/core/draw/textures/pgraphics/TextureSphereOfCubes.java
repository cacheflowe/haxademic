package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PStrokeCaps;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TextureSphereOfCubes
extends BaseTexture {

	protected float frames = 0;
	protected EasingFloat lineWeight = new EasingFloat(2, 6);

	protected EasingFloat baseRadius = new EasingFloat(100, 6);
	protected EasingFloat boxSize = new EasingFloat(20, 6);
	protected EasingFloat numCubes = new EasingFloat(32, 6);
	protected EasingFloat twist = new EasingFloat(1, 6);
	protected int _baseColor = 0xffffffff;
	protected float startPhi = 0;

	
	public TextureSphereOfCubes( int width, int height ) {
		super();
		buildGraphics( width, height );
	}
	
	public void updateDraw() {
		baseRadius.update(true);
		boxSize.update(true);
		numCubes.update(true);
		twist.update(true);
		
		// draw transition result to texture
		_texture.background(0);
		_texture.stroke(255);
		lineWeight.update();
		_texture.strokeWeight(lineWeight.value());
		_texture.strokeCap(PStrokeCaps.SQUARE);
//		_texture.noStroke();
		_texture.fill(_baseColor);//, fillAlpha * 127 );	// , fillAlpha
		if(lineWeight.value() > 3) _texture.noFill();
		
		// context & camera
		DrawUtil.setBetterLights(_texture);
		DrawUtil.setCenterScreen(_texture);
		DrawUtil.setDrawCenter(_texture);
		_texture.rotateX(-P.HALF_PI);
		
		// draw sphere
		for( int i = 0; i < numCubes.value(); i++ ) {
//			float addRadius 
			
			// get sphere coordinate position
			float phi = P.acos( -1f + ( 2f * i - 1f ) / numCubes.value() );
			float theta = P.sqrt( numCubes.value() * P.PI ) * phi;
     		float pointX = baseRadius.value() * 1.2f * P.cos(theta)* P.sin(phi);
     		float pointY = baseRadius.value() * P.sin(theta)* P.sin(phi);
     		float pointZ = baseRadius.value() * P.cos(phi);

     		// get size and alpha and draw cube
     		float size = boxSize.value() * (1f + P.p.audioFreq(i + 20));
     		float oscInc = P.p.frameCount * 0.03f;
			
     		// position, rotate, draw
     		_texture.pushMatrix();
     		_texture.translate(pointX, pointY, pointZ);
     		_texture.rotateX( twist.value() * i );
     		_texture.rotateY( twist.value() * i );
     		_texture.rotateZ( twist.value() * i );
			_texture.box(
					size * (1f + 0.6f * P.sin(i+0 + oscInc)), 
					size * (1f + 0.6f * P.sin(i+1 + oscInc)), 
					size * (1f + 0.6f * P.sin(i+2 + oscInc))
			);
			_texture.popMatrix();
		}
	}
	
	public void updateTiming() {
		if(MathUtil.randBoolean(P.p)) twist.setTarget(MathUtil.randRangeDecimal(0, 0.25f));
		if(MathUtil.randBoolean(P.p)) baseRadius.setTarget(MathUtil.randRangeDecimal(_texture.height * 0.25f, _texture.height * 0.254f));
		
		if(MathUtil.randBoolean(P.p)) {
			float curNumCubes = numCubes.value();
			curNumCubes = curNumCubes + MathUtil.randRange(-10, 10);
			curNumCubes = P.constrain(curNumCubes, 50, 100);
			numCubes.setTarget(curNumCubes);
		}
	}
	
	public void updateTimingSection() {
		boxSize.setTarget(MathUtil.randRangeDecimal(10, 40));
//		wobbleFreq.setTarget(MathUtil.randRangeDecimal(0.001f, 0.01f));
//		wobbleAmp.setTarget(MathUtil.randRangeDecimal(-0.2f, 0.2f));
//		spacing.setTarget(MathUtil.randRangeDecimal(30, 70));
	}
	
	public void newLineMode() {
		lineWeight.setTarget(MathUtil.randRange(0, 5));
	}
	
}
