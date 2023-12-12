package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
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
	
	public void draw() {
		// clear background when activated
		if(_newlyActive) pg.background(0);
		
		// feedback & brightness
		feedbackDist.update();
		feedbackDarken.update();
		PG.feedback(pg, (int) feedbackDist.value());
		BrightnessStepFilter.instance().setBrightnessStep(-feedbackDarken.value()/255f);
		BrightnessStepFilter.instance().applyTo(pg);

		// set line weight
		pg.noFill();
		pg.strokeCap(P.SQUARE);
		lineWeight.update();
		pg.strokeWeight(lineWeight.value());
		pg.stroke(255);
		pg.blendMode(PBlendModes.BLEND);
		
		// context & camera
		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		
		// draw polygon
		scaleV.update(true);
		float polySize = height * scaleV.value() * (1f + 0.1f * P.sin(P.p.frameCount * 0.05f));
		vertices.update(true);
		float segmentRads = P.TWO_PI / vertices.value();
		float rotOffset = P.HALF_PI + segmentRads/2f;
		pg.rotate(P.PI - rotOffset);
		
		pg.beginShape();
		for (float i = 0; i < vertices.value(); i++) {
			float curRads = i * segmentRads;
			float curX = P.cos(curRads) * polySize; 
			float curY = P.sin(curRads) * polySize; 
			pg.vertex(curX, curY);
		}
		pg.endShape(P.CLOSE);
	}
	
	public void updateTiming() {
		if(MathUtil.randBoolean()) scaleV.setTarget(MathUtil.randRangeDecimal(0.2f, 0.4f));
		if(MathUtil.randBoolean()) feedbackDarken.setTarget(MathUtil.randRange(1, 7));
		if(MathUtil.randBoolean()) {
			feedbackDist.setTarget(MathUtil.randRange(-5, 5));
			while(feedbackDist.target() == 0) feedbackDist.setTarget(MathUtil.randRange(-5, 5));
		}
		if(MathUtil.randBoolean()) {
			int verticesDir = MathUtil.randBoolean() ? -1 : 1;
			int newVertices = P.constrain(P.round(vertices.target()) + verticesDir, 3, 12);
			vertices.setTarget(newVertices);
		}
	}
	
	public void updateTimingSection() {
		if(MathUtil.randBoolean()) {
		}
	}
	
	public void newLineMode() {
		lineWeight.setTarget(MathUtil.randRange(1, 12));
	}

}
