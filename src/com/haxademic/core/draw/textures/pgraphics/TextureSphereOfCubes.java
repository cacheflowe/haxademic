package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PStrokeCaps;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class TextureSphereOfCubes
extends BaseTexture {

	protected float frames = 0;
	protected EasingFloat lineWeight = new EasingFloat(2, 6);

	protected EasingFloat baseRadius = new EasingFloat(100, 20);
	protected EasingFloat boxSize = new EasingFloat(20, 12);
	protected EasingFloat numCubes = new EasingFloat(32, 20);
	protected EasingFloat twist = new EasingFloat(1, 20);
	protected int _baseColor = 0xffffffff;

	
	public TextureSphereOfCubes( int width, int height ) {
		super(width, height);
		
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
		PG.setBetterLights(_texture);
		PG.setCenterScreen(_texture);
		PG.setDrawCenter(_texture);
		_texture.rotateX(-P.HALF_PI);
		
		// draw sphere
		for( int i = 0; i < numCubes.value(); i++ ) {
//			float addRadius 
			
			// get sphere coordinate position
			float phi = P.acos( -1f + ( 2f * i - 1f ) / numCubes.value() );
			float theta = P.sqrt( numCubes.value() * P.PI ) * phi;
     		float pointX = baseRadius.value() * P.cos(theta)* P.sin(phi);
     		float pointY = baseRadius.value() * P.sin(theta)* P.sin(phi);
     		float pointZ = baseRadius.value() * P.cos(phi);

     		// get size and alpha and draw cube
     		float size = boxSize.value() * (1f + AudioIn.audioFreq(i + 20));
//     		size = P.min(size, 10);
     		float oscInc = P.p.frameCount * 0.04f;
			
     		_texture.fill(ColorsHax.COLOR_GROUPS[0][i % 4]);
     		
     		// position, rotate, draw
     		_texture.pushMatrix();
     		_texture.translate(pointX, pointY, pointZ);
     		_texture.rotateX( twist.value() * i );
     		_texture.rotateY( twist.value() * i );
     		_texture.rotateZ( twist.value() * i );
			_texture.box(
					size * (1f + 0.9f * P.sin(i+0 + oscInc)), 
					size * (1f + 0.9f * P.sin(i+1 + oscInc)), 
					size * (1f + 0.9f * P.sin(i+2 + oscInc))
			);
			_texture.popMatrix();
		}
	}
	
	public void updateTiming() {
		if(MathUtil.randBoolean()) twist.setTarget(MathUtil.randRangeDecimal(0, 0.25f));
		if(MathUtil.randBoolean()) {
			if(P.p.frameCount % 1200 < 600)
				baseRadius.setTarget(MathUtil.randRangeDecimal(height * 0.25f, height * 0.35f));
			else
				baseRadius.setTarget(MathUtil.randRangeDecimal(height * 0.85f, height * 1.2f));
		}
		
		if(MathUtil.randBoolean()) {
			float curNumCubes = numCubes.value();
			curNumCubes = curNumCubes + MathUtil.randRange(-10, 10);
			curNumCubes = P.constrain(curNumCubes, 50, 100);
//			float curNumCubes = baseRadius.target() * (0.4f + MathUtil.randRangeDecimal(-0.1f, 0.1f));
			numCubes.setTarget(curNumCubes);
		}
		
		float low = baseRadius.target() / 15f;
		float high = low * 2f;
		boxSize.setTarget(MathUtil.randRangeDecimal(low, high));
	}
	
	public void updateTimingSection() {
//		wobbleFreq.setTarget(MathUtil.randRangeDecimal(0.001f, 0.01f));
//		wobbleAmp.setTarget(MathUtil.randRangeDecimal(-0.2f, 0.2f));
//		spacing.setTarget(MathUtil.randRangeDecimal(30, 70));
	}
	
	public void newLineMode() {
		lineWeight.setTarget(MathUtil.randRange(0, 5));
	}
	
}
