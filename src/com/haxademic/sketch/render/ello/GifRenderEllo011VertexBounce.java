package com.haxademic.sketch.render.ello;

import java.util.ArrayList;

import toxi.color.TColor;
import toxi.geom.mesh.Vertex;
import toxi.geom.mesh.WETriangleMesh;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.DrawMesh;
import com.haxademic.core.draw.mesh.MeshUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.ElasticFloat3D;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class GifRenderEllo011VertexBounce
extends PAppletHax{
	
	float _frames = 40;

	protected TColor COLOR = TColor.newHex("000000"); // TColorInit.newRGBA( 0, 200, 234, 255 ); // TColor.newHex("FFFF00"); // DAFFCA // FED7ED
	
	protected WETriangleMesh _mesh, _meshOrig;
	protected ArrayList<ElasticFloat3D> _verticesElastic;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "400" );
		_appConfig.setProperty( "height", "400" );
		
		_appConfig.setProperty( "sunflow", "true" );
		_appConfig.setProperty( "sunflow_active", "true" );
		_appConfig.setProperty( "sunflow_quality", "high" );
		_appConfig.setProperty( "sunflow_save_images", "false" );
		
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "45" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", "52" );
	}
	
	public void setup() {
		super.setup();
		p.background(255);
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_mesh = MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, FileUtil.getHaxademicDataPath() + "svg/ello.svg", -1, 20, 0.35f ), 20 );
		_meshOrig = _mesh.copy();
		
		_verticesElastic = new ArrayList<ElasticFloat3D>();
		int numVertices = _mesh.getNumVertices();
		Vertex v;
		ElasticFloat3D elasticFloat;
		for( int i = 0; i < numVertices; i++ ) {
			if( _mesh.getVertexForID( i ) != null ) {
				v = _mesh.getVertexForID( i );
				elasticFloat = new ElasticFloat3D(0, 0, -50, 0.6f + P.sin(v.x/28f)*0.1f, 0.35f + P.cos(v.y/30f)*0.1f);
				elasticFloat.setTarget(v.x, v.y, v.z);
				_verticesElastic.add(elasticFloat);
			}
		}
	}
	
	public void drawApp() {		
		
		// update all back to zero on a certain frame
		if(p.frameCount == 40) {
			for( int i=0; i < _verticesElastic.size(); i++ ) {
				ElasticFloat3D elasticFloat = _verticesElastic.get(i);
				elasticFloat.setTarget(0, 0, 0);
				elasticFloat.setAccel(0.6f);
				elasticFloat.setFriction(0.4f);
			}
		}
		
		// update vertices elasticness
		for( int i=0; i < _verticesElastic.size(); i++ ) {
			_verticesElastic.get(i).update();
			Vertex v;
			if( _mesh.getVertexForID( i ) != null ) {
				v = _mesh.getVertexForID( i );
				v.x = _verticesElastic.get(i).x();
				v.y = _verticesElastic.get(i).y();
				v.z = _verticesElastic.get(i).z();
			}
		}


		
		float frameRadians = P.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radiansComplete = P.TWO_PI * percentComplete;


//		_jw.jr.background(JoonsWrapper.BACKGROUND_GI);
		_jw.jr.background(JoonsWrapper.BACKGROUND_AO);
		_jw.jr.background(0, 0, 0); //background(gray), or (r, g, b), like Processing.
		setUpRoom();
		
		translate(0, 0, -500);
		p.rotateX(0.4f);


		p.noStroke();
		
		// draw box bg
		DrawUtil.setDrawCenter(p);
		_jw.jr.fill( JoonsWrapper.MATERIAL_SHINY, 255, 255, 255 );
		p.box(p.width*10, p.width*10, 2);
		
		// oscillate and draw logo
//		translate(0, 0, 15 + P.sin(radiansComplete) * 15);
//		p.rotateX(0.2f * P.sin(P.PI/2f + radiansComplete));
		translate(0, 0, 15);
		p.rotateX(0.05f);

		_jw.jr.fill( JoonsWrapper.MATERIAL_SHINY, COLOR.red() * 255, COLOR.green() * 255, COLOR.blue() * 255 );
		DrawMesh.drawToxiMeshFacesNative( p, _mesh );
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
		int grey = 30;
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



