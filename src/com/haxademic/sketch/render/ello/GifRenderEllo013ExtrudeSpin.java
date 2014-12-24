
package com.haxademic.sketch.render.ello;

import java.util.ArrayList;

import processing.core.PShape;
import processing.core.PVector;
import toxi.color.TColor;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.MeshUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.ElasticFloat3D;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class GifRenderEllo013ExtrudeSpin
extends PAppletHax{
	
	float _frames = 160;

	protected TColor BLACK = TColor.newHex("000000"); // TColorInit.newRGBA( 0, 200, 234, 255 ); // TColor.newHex("FFFF00"); // DAFFCA // FED7ED
	protected TColor WHITE = TColor.newHex("ffffff"); // TColorInit.newRGBA( 0, 200, 234, 255 ); // TColor.newHex("FFFF00"); // DAFFCA // FED7ED
	PShape _logo, _logoOrig;
	float _elloSize = 2;
	ArrayList<PVector> _outerPoints;
	ArrayList<PVector> _mouthPoints;

	protected ArrayList<ElasticFloat3D> _verticesElastic;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "400" );
		_appConfig.setProperty( "height", "400" );
		
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
		p.g.smooth(OpenGLUtil.SMOOTH_HIGH);
		
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
//		p.background(255);
		float frameRadians = P.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radiansComplete = P.TWO_PI * percentComplete;


		_jw.jr.background(JoonsWrapper.BACKGROUND_GI);
//		_jw.jr.background(JoonsWrapper.BACKGROUND_AO);
		_jw.jr.background(0, 0, 0); //background(gray), or (r, g, b), like Processing.
		setUpRoom();
		
		translate(-5, -5, -200);
//		p.rotateX(mouseY*0.01f);
		p.rotateX(0.6f);
		// p.rotateX(0.4f * P.sin(P.PI/2f + radiansComplete * 2f));
		p.rotateZ(radiansComplete);
		p.rotateY(radiansComplete);

		
		// draw box bg
//		DrawUtil.setDrawCenter(p);
//		_jw.jr.fill( JoonsWrapper.MATERIAL_SBLACK 255, 255, 255BLACK/		p.box(p.widthBLACKp.width*10, 2);
		
		// DRAW CIRCLE  ---------------
		_jw.jr.fill( JoonsWrapper.MATERIAL_SHINY, BLACK.red() * 255, BLACK.green() * 255, BLACK.blue() * 255 );
		p.stroke(WHITE.toARGB(), 80);
		p.fill(BLACK.toARGB());
		MeshUtil.drawExtrudedPShape(p, _outerPoints, 5 + 2f * P.sin(P.TWO_PI * percentComplete));


		// DRAW MOUTH  ---------------
		_jw.jr.fill( JoonsWrapper.MATERIAL_SHINY, WHITE.red() * 255, WHITE.green() * 255, WHITE.blue() * 255 );
		p.stroke(BLACK.toARGB(), 80);
		p.fill(WHITE.toARGB());
		MeshUtil.drawExtrudedPShape(p, _mouthPoints, 9.1f + 2f * P.sin(P.PI + P.TWO_PI * percentComplete));
	}
	
	protected void makeLightSource() {
		_jw.jr.fill("light", 255, 255, 255);
		sphere(10);
		translate(-1000, 0, 0);
	}
	
	protected void setUpRoom() {
		pushMatrix();
		translate(0, 0, 0);
		float radiance = 20;
		int samples = 16;
		int grey = 200;
		_jw.jr.background("cornell_box", 
				12000, 6000, 6000,	// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				grey, grey, grey, // left rgb
				grey, grey, grey, // right rgb
				grey, grey, grey, // back rgb
				grey, grey, grey, // top rgb
				grey, grey, grey  // bottom rgb
		); 
		popMatrix();		
	}

}



