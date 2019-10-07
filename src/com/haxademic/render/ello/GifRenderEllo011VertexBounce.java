package com.haxademic.render.ello;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.toxi.DrawToxiMesh;
import com.haxademic.core.draw.toxi.MeshUtilToxi;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.ElasticFloat3D;
import com.haxademic.core.render.JoonsWrapper;

import toxi.color.TColor;
import toxi.geom.mesh.Vertex;
import toxi.geom.mesh.WETriangleMesh;

public class GifRenderEllo011VertexBounce
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	float _frames = 40;

	protected TColor COLOR = TColor.newHex("000000"); // TColorInit.newRGBA( 0, 200, 234, 255 ); // TColor.newHex("FFFF00"); // DAFFCA // FED7ED
	
	protected WETriangleMesh _mesh, _meshOrig;
	protected ArrayList<ElasticFloat3D> _verticesElastic;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "400" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "400" );
		
		p.appConfig.setProperty( AppSettings.SUNFLOW, "true" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, "true" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, "high" );
		p.appConfig.setProperty( "sunflow_save_images", "false" );
		
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "45" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, "52" );
	}
	
	public void setup() {
		super.setup();
		p.background(255);
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_mesh = MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, FileUtil.getHaxademicDataPath() + "svg/ello.svg", -1, 20, 0.35f ), 20 );
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
		joons.jr.background(JoonsWrapper.BACKGROUND_AO);
		joons.jr.background(0, 0, 0); //background(gray), or (r, g, b), like Processing.
		setUpRoom();
		
		translate(0, 0, -500);
		p.rotateX(0.4f);


		p.noStroke();
		
		// draw box bg
		PG.setDrawCenter(p);
		joons.jr.fill( JoonsWrapper.MATERIAL_SHINY, 255, 255, 255 );
		p.box(p.width*10, p.width*10, 2);
		
		// oscillate and draw logo
//		translate(0, 0, 15 + P.sin(radiansComplete) * 15);
//		p.rotateX(0.2f * P.sin(P.PI/2f + radiansComplete));
		translate(0, 0, 15);
		p.rotateX(0.05f);

		joons.jr.fill( JoonsWrapper.MATERIAL_SHINY, COLOR.red() * 255, COLOR.green() * 255, COLOR.blue() * 255 );
		DrawToxiMesh.drawToxiMeshFacesNative( p, _mesh );
	}
	
	protected void makeLightSource() {
		joons.jr.fill("light", 255, 255, 255);
		sphere(10);
		translate(-1000, 0, 0);
	}
	
	protected void setUpRoom() {
		pushMatrix();
		translate(0, 0, 0);
		float radiance = 20;
		int samples = 16;
		int grey = 30;
		joons.jr.background("cornell_box", 
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



