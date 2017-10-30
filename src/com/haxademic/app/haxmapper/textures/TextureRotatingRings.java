package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.data.Point3D;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TextureRotatingRings 
extends BaseTexture {

	protected int NUM_RINGS = 10;
	
	protected Point3D _rotation = new Point3D( 0, 0, 0 );
	protected Point3D _rotationTarget = new Point3D( 0, 0, 0 );

	protected boolean _isWireframe = false;
	
	protected EasingFloat _radius = new EasingFloat(100, 6);

	public TextureRotatingRings( int width, int height ) {
		super();

		buildGraphics( width, height );
	}
	
	public void newLineMode() {
		_isWireframe = MathUtil.randBoolean(P.p);
	}
	
	public void newRotation() {
		float circleSegments = 4f;
		float circleSegment = (float) ( Math.PI * 2f ) / circleSegments;
		
		_rotationTarget.x = circleSegment * P.round( MathUtil.randRangeDecimal( 0, circleSegments ) );
		_rotationTarget.y = circleSegment * P.round( MathUtil.randRangeDecimal( 0, circleSegments ) );
		_rotationTarget.z = circleSegment * P.round( MathUtil.randRangeDecimal( 0, circleSegments ) );
	}
	
	protected void updateRotation() {
		_rotation.easeToPoint( _rotationTarget, 6 );
		_texture.rotateX( _rotation.x );
		_texture.rotateY( _rotation.y );
		_texture.rotateZ( _rotation.z );
	}
	

	public void newMode() {
	}

	public void updateTimingSection() {
		_radius.setTarget(MathUtil.randRangeDecimal(_texture.width * 0.7f, _texture.width * 4f));
	}

	public void updateDraw() {
		_texture.clear();
		
		DrawUtil.resetGlobalProps( _texture );
		DrawUtil.setCenterScreen( _texture );
		_texture.pushMatrix();
		
		// rotation for entire scene
		_radius.update();
		updateRotation();
		
		// disc properties	
		int discPrecision = 24;
		float discRadius = _radius.value();
		float circleSegment = (float) P.PI / (float) NUM_RINGS;
		
		// wireframe modes
		if( _isWireframe == true ) 
			_texture.noFill();
		else
			_texture.noStroke();
		_texture.strokeWeight( 2 );
		
		// draw rings
		for( int i = 0; i < NUM_RINGS; i++ ) {
			// get eq val for alpha
			float ringEQVal = P.p._audioInput.getFFT().spectrum[(i*10) % P.p._audioInput.getFFT().spectrum.length];
			float alphaMultiplier = 2.3f * 255f;

			// set colors
			if( _isWireframe == false ) {
				_texture.fill( _color, ringEQVal * alphaMultiplier );
				_texture.noStroke();
			}
			if( _isWireframe == true ) {
				_texture.stroke( _color, ringEQVal * alphaMultiplier );
				_texture.noFill();
			}
			
			// draw disc, with thickness based on eq 
			float eqThickness = 100 + ( ringEQVal * 5000 );
			_texture.pushMatrix();			
			_texture.rotateY( i * circleSegment );
			Shapes.drawDisc3D( _texture, discRadius, discRadius + 100, eqThickness, discPrecision, -1, -1 );
			_texture.popMatrix();
			
			// draw orbiting star per ring
//			p.pushMatrix();
//			p.fill( _baseColor.toARGB() );//_ringColors[i].colorIntWithAlpha(ringAlpha, 0) );
//			float starX = innerRadius * scale * p.sin( p.frameCount * ringSpacingIndex * 0.01f );
//			float starY = innerRadius * scale * p.cos( p.frameCount * ringSpacingIndex * 0.01f );
//			p.translate( starX, starY, 0 );
//			p.rotateZ( i * (2*p.PI)/NUM_RINGS );
//			p.rotateY( i * (2*p.PI)/NUM_RINGS );
//			Shapes.drawStar( p, 5f, 50f * ringEQVal, 10f, 50 + 50 * ringEQVal, 0f);
//			p.popMatrix();
		}
		
		_texture.popMatrix();
	}
	
}
