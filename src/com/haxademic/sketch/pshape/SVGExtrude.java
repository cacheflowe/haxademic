
package com.haxademic.sketch.pshape;

import java.util.ArrayList;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.MeshUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.ElasticFloat3D;

import processing.core.PShape;
import processing.core.PVector;
import toxi.color.TColor;

public class SVGExtrude
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	float _frames = 160;

	protected TColor BLACK = TColor.newHex("000000"); // TColorInit.newRGBA( 0, 200, 234, 255 ); // TColor.newHex("FFFF00"); // DAFFCA // FED7ED
	protected TColor WHITE = TColor.newHex("ffffff"); // TColorInit.newRGBA( 0, 200, 234, 255 ); // TColor.newHex("FFFF00"); // DAFFCA // FED7ED
	PShape _svg;
	ArrayList<PVector> _outerPoints;
	ArrayList<PVector> _mouthPoints;

	protected ArrayList<ElasticFloat3D> _verticesElastic;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "600" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "600" );
		
		p.appConfig.setProperty( AppSettings.SUNFLOW, "true" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, "false" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, "low" );
		p.appConfig.setProperty( "sunflow_save_images", "false" );
		
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "45" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames + 1) );
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



