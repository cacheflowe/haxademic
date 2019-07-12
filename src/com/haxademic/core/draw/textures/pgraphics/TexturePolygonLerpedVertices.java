package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PShapeTypes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TexturePolygonLerpedVertices
extends BaseTexture {

	protected float frames = 0;
	protected EasingFloat vertices = new EasingFloat(3, 6f);
	protected EasingFloat lineWeight = new EasingFloat(1, 6f);
	protected EasingFloat scaleV = new EasingFloat(0.5f, 6f);
	protected EasingFloat feedbackDist = new EasingFloat(0, 6f);
	protected EasingFloat feedbackDarken = new EasingFloat(2, 6f);

	
	public TexturePolygonLerpedVertices( int width, int height ) {
		super(width, height);
		
	}
	
	public void updateDraw() {
		// clear background when activated
		if(_newlyActive) _texture.background(0);
		
		// feedback & brightness
		feedbackDist.update();
		feedbackDarken.update();
		PG.feedback(_texture, (int) feedbackDist.value());
		BrightnessStepFilter.instance(P.p).setBrightnessStep(-feedbackDarken.value()/255f);
		BrightnessStepFilter.instance(P.p).applyTo(_texture);

		// set line weight
		_texture.noFill();
		_texture.strokeCap(P.SQUARE);
		lineWeight.update();
		_texture.strokeWeight(lineWeight.value());
		_texture.stroke(255);
		_texture.blendMode(PBlendModes.BLEND);
		
		// context & camera
		PG.setCenterScreen(_texture);
		PG.setDrawCenter(_texture);
		
		// draw polygon
		scaleV.update(true);
		float polySize = height * scaleV.value() * (1f + 0.1f * P.sin(P.p.frameCount * 0.05f));
		vertices.update(true);
		float segmentRads = P.TWO_PI / vertices.value();
		float rotOffset = P.HALF_PI + segmentRads/2f;
		_texture.rotate(P.PI - rotOffset);
		
		_texture.beginShape();
		for (float i = 0; i < vertices.value(); i++) {
			float curRads = i * segmentRads;
			float curX = P.cos(curRads) * polySize; 
			float curY = P.sin(curRads) * polySize; 
			_texture.vertex(curX, curY);
		}
		_texture.endShape(P.CLOSE);
	}
	
	public void updateTiming() {
		if(MathUtil.randBoolean(P.p)) scaleV.setTarget(MathUtil.randRangeDecimal(0.2f, 0.4f));
		if(MathUtil.randBoolean(P.p)) feedbackDarken.setTarget(MathUtil.randRange(1, 7));
		if(MathUtil.randBoolean(P.p)) {
			feedbackDist.setTarget(MathUtil.randRange(-5, 5));
			while(feedbackDist.target() == 0) feedbackDist.setTarget(MathUtil.randRange(-5, 5));
		}
		if(MathUtil.randBoolean(P.p)) {
			int verticesDir = MathUtil.randBoolean(P.p) ? -1 : 1;
			int newVertices = P.constrain(P.round(vertices.target()) + verticesDir, 3, 12);
			vertices.setTarget(newVertices);
		}
	}
	
	public void updateTimingSection() {
		if(MathUtil.randBoolean(P.p)) {
		}
	}
	
	public void newLineMode() {
		lineWeight.setTarget(MathUtil.randRange(1, 12));
	}

}
