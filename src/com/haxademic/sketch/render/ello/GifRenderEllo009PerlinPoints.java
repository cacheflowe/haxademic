package com.haxademic.sketch.render.ello;

import java.util.ArrayList;

import processing.core.PShape;
import processing.core.PVector;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class GifRenderEllo009PerlinPoints
extends PAppletHax{
	
	PShape _logo, _logoOrig;
	float _frames = 60;
	float _elloSize = 2;
	ArrayList<PVector> _outerPoints;
	ArrayList<PVector> _mouthPoints;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "500" );
		_appConfig.setProperty( "height", "500" );
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "true" );
		_appConfig.setProperty( "rendering_gif_framerate", "45" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", ""+Math.round(_frames) );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames*2) );
	}
	
	public void setup() {
		super.setup();
		p.background(255);
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-centered-complex-lofi.svg");
		
		_outerPoints = new ArrayList<PVector>();
		_mouthPoints = new ArrayList<PVector>();
		for (int j = 0; j < _logo.getChildCount(); j++) {
			for (int i = 0; i < _logo.getChild(j).getVertexCount() - 1; i++) {
				PVector v = _logo.getChild(j).getVertex(i);
			    if(i < 49) {
			    	_outerPoints.add(v);
			    } else {
			    	_mouthPoints.add(v);
			    }
			}
		}
	}
	
	public void drawApp() {
//		p.fill(255, 90);
//		p.rect(0, 0, p.width, p.height);
		p.background(0);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutCubic(percentComplete, 0, 1, 1);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);
		
		DrawUtil.setDrawCorner(p);
		p.translate(p.width/2f - 10, p.height/2f - 5); // points are slightly offset from center			
		_elloSize = p.width/2f;
		float amp = 5;
		float baseAmp = 3.f;
		float outerPointsInc = 1f / (float)_outerPoints.size();
		
		PVector midpoint = new PVector();
		float midpointAmp = 0;
		
		p.fill(255);
		p.noStroke();
				
		for(float j=0.5f; j < 10; j+=0.5) {
			for (float i = 0; i < _outerPoints.size()-1; i++) {
				PVector v = _outerPoints.get((int)i);
				amp = baseAmp + j + (0.6f * j) * p.noise( i + 0.5f * MathUtil.saw((i/_frames*128f) + (percentComplete * P.TWO_PI)) ); // +p.frameCount/_frames
				p.ellipse(v.x * amp, v.y * amp, 3, 3);
			}
		}
			
		for (int i = 0; i < _mouthPoints.size(); i++) {
			PVector v = _mouthPoints.get(i);
			amp = baseAmp + 0.2f * p.noise( i + 0.5f * MathUtil.saw((i/_frames*8f) + (percentComplete * P.TWO_PI)) ); // +p.frameCount/_frames
			p.ellipse(v.x * amp, v.y * amp, 3, 3);
		}


//		filter(BLUR);
		if( p.frameCount == _frames * 3 ) {
			if(_isRendering == true) {				
				_renderer.stop();
				P.println("render done!");
			}
		}

	}
}



