package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PVector;

public class TextureAudioTube 
extends BaseTexture {

	protected PVector _rotation = new PVector( 0, 0, 0 );
	protected PVector _rotationTarget = new PVector( 0, 0, 0 );
	protected EasingFloat _radius = new EasingFloat(0, 10);
	protected EasingFloat _spacing = new EasingFloat(10, 6);

	public TextureAudioTube( int width, int height ) {
		super();
		buildGraphics( width, height );
	}
	
	public void newLineMode() {
	}
	
	public void newRotation() {
		float circleSegments = 8f;
		float circleSegment = (float) ( Math.PI * 2f ) / circleSegments;
		
		_rotationTarget.x = circleSegment * P.round( MathUtil.randRangeDecimal( 0, circleSegments ) );
		_rotationTarget.y = (MathUtil.randBoolean(P.p) == true) ? 0 : circleSegment;
		if(MathUtil.randBoolean(P.p) == true) _rotationTarget.y = P.PI/2f;
		if(MathUtil.randBoolean(P.p) == true) _rotationTarget.y *= -1;
		_rotationTarget.z = (MathUtil.randBoolean(P.p) == true) ? 0 : circleSegment;
		if(MathUtil.randBoolean(P.p) == true) _rotationTarget.z *= -1;
		// override for now:
//		_rotationTarget.x = P.PI/2f;
		_rotationTarget.y = P.PI/2f;
	}
	
	protected void updateRotation() {
		_rotation.lerp(_rotationTarget, 0.2f );
		_texture.rotateY( _rotation.y );
//		_texture.rotateZ( _rotation.z );
		_texture.rotateX( _rotation.x );
	}

	public void updateTiming() {
		if(P.abs(_rotationTarget.y % P.PI/2f) < 0.01f) {
			_radius.setTarget(MathUtil.randRangeDecimal(_texture.width/25f, _texture.width/15f));
		} else {
			_radius.setTarget(MathUtil.randRangeDecimal(_texture.width, _texture.width * 2));
		}
		_spacing.setTarget(MathUtil.randRangeDecimal(_texture.width/10f, _texture.width/5f));
	}

	public void updateDraw() {
		_texture.clear();
		
		_texture.ambientLight(102, 102, 102);
		_texture.lightSpecular(204, 204, 204);
		_texture.directionalLight(102, 102, 102, 0, 0, -1);
		_texture.specular(255, 255, 255);
		_texture.emissive(51, 51, 51);
		_texture.ambient(50, 50, 50);
		_texture.shininess(20.0f);
		
		
		DrawUtil.resetGlobalProps( _texture );
		DrawUtil.setCenterScreen( _texture );
		_texture.pushMatrix();
		
		_radius.update();
		_spacing.update();
		updateRotation();
		
//		drawEQ(100,8,_texture.height/3f,_texture.width/100f,2);
		drawEQSmoothed(50,8,_radius.value(),_spacing.value(),8,2,2);
		
		_texture.popMatrix();
	}
	
	protected void drawEQ(int numBands, int discReso, float radius, float spacing, float amp) {
		float startX = -spacing * numBands/2f;
		
		_texture.noStroke();
		_texture.fill(200, 200, 200);

//		_texture.rotateY(P.p.mouseX/100f);
//		_texture.rotateX(P.p.mouseY/100f);
		
		// draw EQ
		float radSegment = P.TWO_PI / discReso;
		for (int i = 1; i < numBands; i++) {
			float lastEqVal = radius + radius * amp * P.p.audioFreq(i-1);
			float eqVal = radius + radius * amp * P.p.audioFreq(i);
			float curX = startX + i * spacing;
			float lastX = startX + (i-1) * spacing;
			
			_texture.beginShape(P.TRIANGLE_STRIP);
			for (int j = 0; j <= discReso; j++) {
				float curRads = j * radSegment + (i/10f); // last bit for a twist
				_texture.vertex(lastX, P.sin(curRads) * lastEqVal, P.cos(curRads) * lastEqVal);
				_texture.vertex(curX, P.sin(curRads) * eqVal, P.cos(curRads) * eqVal);
			}
			_texture.endShape();
		}

	}
	
	
	protected void drawEQSmoothed(int numBands, int discReso, float radius, float spacing, float smoothsteps, float amp, float smoothEasing) {
		float startX = -spacing * numBands/2f;
		
		_texture.noStroke();
		_texture.fill(255, 255, 255);
		
		int from = _texture.color(0);
		int to = _color; //_texture.color(255);

//		_texture.stroke(200, 200, 200);
//		_texture.fill(0, 0, 0);
//		_texture.noFill();
//		_texture.rotateY(P.p.mouseX/100f);
//		_texture.rotateX(P.p.mouseY/100f);

		// draw EQ
		float spacingSubDiv = spacing / (smoothsteps);
		float radSegment = P.TWO_PI / discReso;
		for (int i = 1; i < numBands; i++) {
			
			float lastEqVal = radius + radius * amp * P.p.audioFreq(i-1);
			float eqVal = radius + radius * amp * P.p.audioFreq(i);
			float curX = startX + i * spacing;
			float lastX = startX + (i-1) * spacing;
			
			float ampDiff = eqVal - lastEqVal;
			
			_texture.fill( _texture.lerpColor(from, to, (float)i/numBands) );
			
//			P.println("lastEqVal",lastEqVal);
//			P.println("eqVal",eqVal);
//			P.println("ampDiff",lastEqVal+ampDiff);
			
			for (float subDivision = 1; subDivision <= smoothsteps; subDivision++) {
				
				// interpolate the amplitude
				float lastEqSubDiv = lastEqVal + ampDiff * MathUtil.easePowPercent((subDivision-1f)/smoothsteps, smoothEasing);
				float curEqSubDiv = lastEqVal + ampDiff * MathUtil.easePowPercent((subDivision)/smoothsteps, smoothEasing);
				
				// break up subdivision spacing
				float subDivLastX = lastX + spacingSubDiv * (subDivision-1);
				float subDivCurX = lastX + spacingSubDiv * subDivision;
				
//				P.println("% ",(subDivision-1f)/smoothsteps);
//				P.println("% ",(subDivision)/smoothsteps);
//				if(subDivision == smoothsteps) P.println("curEqSubDiv",curEqSubDiv);
//				P.println("subDivLastX ",subDivLastX);
//				P.println("subDivCurX ",subDivCurX);
				
//				_texture.noFill();
//				if(subDivision == smoothsteps) _texture.fill(255, 255, 255);

				
				_texture.beginShape(P.TRIANGLE_STRIP);
				for (int j = 0; j <= discReso; j++) {
					float curRads = j * radSegment;
					_texture.vertex(subDivLastX, P.sin(curRads) * lastEqSubDiv, P.cos(curRads) * lastEqSubDiv);
					_texture.vertex(subDivCurX, P.sin(curRads) * curEqSubDiv, P.cos(curRads) * curEqSubDiv);
				}
				_texture.endShape();
				
			}
		}
		
	}


}
