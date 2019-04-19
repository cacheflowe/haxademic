package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TextureSphere
extends BaseTexture {

	protected float frames = 0;
	protected EasingFloat lineWeight = new EasingFloat(2, 6);

	protected EasingFloat baseRadius = new EasingFloat(100, 6);
	protected EasingFloat boxSize = new EasingFloat(20, 6);
	protected EasingFloat numCubes = new EasingFloat(32, 6);
	protected int _baseColor = 0xffffffff;
	protected float startPhi = 0;

	
	public TextureSphere( int width, int height ) {
		super();
		buildGraphics( width, height );
	}
	
	public void updateDraw() {
		baseRadius.update(true);
		boxSize.update(true);
		numCubes.update(true);
		
		// draw transition result to texture
		_texture.background(0);
		_texture.stroke(255);
		lineWeight.update();
		_texture.strokeWeight(1);//lineWeight.value());
		
		// context & camera
		DrawUtil.setBetterLights(_texture);
		DrawUtil.setCenterScreen(_texture);
		DrawUtil.setDrawCenter(_texture);
		_texture.rotateX(-P.HALF_PI);
		
		// sphere coordinate vars
		float theta = 0;
		float phi = 0;
//		float thetaIncrement = 1f;
//		float phiIncrement = startPhi;
		float pointX = 0;
		float pointY = 0;
		float pointZ = 0;

		for( int i = 0; i < numCubes.value(); i++ ) {
			// get position of cube
			phi = P.acos( -1f + ( 2f * i - 1f ) / numCubes.value() );
     		theta = P.sqrt( numCubes.value() * P.PI ) * phi;
     		pointX = baseRadius.value() * P.cos(theta)* P.sin(phi);
     		pointY = baseRadius.value() * P.sin(theta)* P.sin(phi);
     		pointZ = baseRadius.value() * P.cos(phi);

     		// get size and alpha and draw cube
     		float size = boxSize.value() + 150f * P.p.audioFreq(i + 20);
//     		_texture.noStroke();
     		_texture.fill(_baseColor);//, fillAlpha * 127 );	// , fillAlpha
			
     		
     		_texture.pushMatrix();
     		_texture.translate(pointX, pointY, pointZ);
     		_texture.rotateX( P.TWO_PI / numCubes.value() * i );
     		_texture.rotateY( P.TWO_PI / numCubes.value() * i );
     		_texture.rotateZ( P.TWO_PI / numCubes.value() * i );
//			Shapes.drawStar( _texture, 7, size, size * 0.15f, size, 0);
			_texture.box(size, size, size);
			_texture.popMatrix();
		}

	}
	
	public void updateTiming() {
		if(MathUtil.randBoolean(P.p)) numCubes.setTarget(120 + 80f * P.sin(P.p.frameCount * 0.01f));
		if(MathUtil.randBoolean(P.p)) baseRadius.setTarget(MathUtil.randRangeDecimal(_texture.height * 0.25f, _texture.height * 0.4f));
	}
	
	public void updateTimingSection() {
		boxSize.setTarget(MathUtil.randRangeDecimal(10, 40));
//		wobbleFreq.setTarget(MathUtil.randRangeDecimal(0.001f, 0.01f));
//		wobbleAmp.setTarget(MathUtil.randRangeDecimal(-0.2f, 0.2f));
//		spacing.setTarget(MathUtil.randRangeDecimal(30, 70));
	}
	
	public void newLineMode() {
		lineWeight.setTarget(MathUtil.randRange(1, 12));
	}
	
}
