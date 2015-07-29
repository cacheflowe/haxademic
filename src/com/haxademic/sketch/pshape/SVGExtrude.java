
package com.haxademic.sketch.pshape;

import java.util.ArrayList;

import processing.core.PShape;
import processing.core.PVector;
import toxi.color.TColor;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.MeshUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.ElasticFloat3D;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class SVGExtrude
extends PAppletHax{
	
	float _frames = 160;

	protected TColor BLACK = TColor.newHex("000000"); // TColorInit.newRGBA( 0, 200, 234, 255 ); // TColor.newHex("FFFF00"); // DAFFCA // FED7ED
	protected TColor WHITE = TColor.newHex("ffffff"); // TColorInit.newRGBA( 0, 200, 234, 255 ); // TColor.newHex("FFFF00"); // DAFFCA // FED7ED
	PShape _svg;
	ArrayList<PVector> _outerPoints;
	ArrayList<PVector> _mouthPoints;

	protected ArrayList<ElasticFloat3D> _verticesElastic;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "600" );
		_appConfig.setProperty( "height", "600" );
		
		_appConfig.setProperty( "sunflow", "true" );
		_appConfig.setProperty( "sunflow_active", "false" );
		_appConfig.setProperty( "sunflow_quality", "low" );
		_appConfig.setProperty( "sunflow_save_images", "false" );
		
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "45" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames + 1) );
	}
	
	public void setup() {
		super.setup();
		p.background(255);
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		p.noStroke();
		p.g.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_svg = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/pink-eye.svg");
		
		P.println("_svg.getChildCount(): "+_svg.getChildCount());
	}
	
	public void drawApp() {		
		p.background(255);
		float frameRadians = P.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radiansComplete = P.TWO_PI * percentComplete;

		translate(0, 0, -400);
//		p.rotateX(mouseY*0.01f);
		p.rotateX(0.6f);
//		p.rotateZ(radiansComplete);
//		p.rotateY(radiansComplete);

		
		// DRAW CIRCLE  ---------------
//		p.stroke(WHITE.toARGB(), 80);
//		p.fill(BLACK.toARGB());
		for (int j = 0; j < _svg.getChildCount(); j++) {
			_outerPoints = new ArrayList<PVector>();
			int fill = 0;
			for (int i = 0; i < _svg.getChild(j).getVertexCount() - 1; i++) {
				PVector v = _svg.getChild(j).getVertex(i);
				fill = _svg.getChild(j).getFill(0);
//				_svg.getChild(j).
//				P.println("v:", v);
//				P.println("fill:", fill);
				_outerPoints.add(v);
			}
			p.fill(fill);
			MeshUtil.drawExtrudedPShape(p, _outerPoints, 10 + j);
			_outerPoints.clear();
		}


		// DRAW MOUTH  ---------------
//		p.stroke(BLACK.toARGB(), 80);
//		p.fill(WHITE.toARGB());
//		MeshUtil.drawExtrudedPShape(p, _mouthPoints, 9.1f + 2f * P.sin(P.PI + P.TWO_PI * percentComplete));
	}
	
}



