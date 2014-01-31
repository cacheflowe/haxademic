package com.haxademic.sketch.render;

import toxi.color.TColor;
import toxi.geom.mesh.WETriangleMesh;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.TColorInit;
import com.haxademic.core.draw.mesh.DrawMesh;
import com.haxademic.core.draw.mesh.MeshUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class RenderLogo
extends PAppletHax{
	

//	protected TColor MODE_SET_BLUE = TColorInit.newRGBA( 0, 200, 234, 255 );
	protected TColor COLOR = TColor.newHex("FF007E"); // TColorInit.newRGBA( 0, 200, 234, 255 ); // TColor.newHex("FFFF00"); // DAFFCA // FED7ED
	
	WETriangleMesh _mesh;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "sunflow", "true" );
		_appConfig.setProperty( "sunflow_active", "true" );
		_appConfig.setProperty( "sunflow_quality", "high" );

		
		_appConfig.setProperty( "width", "1280" );
		_appConfig.setProperty( "height", "1280" );
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "fps", "30" );
	}

	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);

		_mesh = MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, FileUtil.getHaxademicDataPath() + "svg/guiceworks-logo.svg", -1, 20, 1.5f ), 14 );
	}


	public void drawApp() {
		p.background(0);
		p.lights();

		_jw.jr.background(JoonsWrapper.BACKGROUND_GI);
		setUpRoom();
		
		translate(0, 0, -2500);
//		p.rotateX(0.1f);

//		p.fill(255);
//		p.stroke(255);
//		_jw.jr.fill( JoonsWrapper.MATERIAL_DIFFUSE, COLOR.red() * 255f, COLOR.green() * 255f, COLOR.blue() * 255f );
//		DrawMesh.drawToxiMeshFacesNative( p, _mesh );

	
		for( int i=0; i < 14; i++) {
		
			translate(0, 0, -20);
			p.fill(255);
			p.noStroke();
			if( i%2 == 0 ) {
				_jw.jr.fill( JoonsWrapper.MATERIAL_DIFFUSE, COLOR.red() * (255 - i*15), COLOR.green() * (255 - i*15), COLOR.blue() * (255 - i*15) );
			} else {
				_jw.jr.fill( JoonsWrapper.MATERIAL_SHINY, 0, 0, 0, 1f);
			}
			DrawMesh.drawToxiMeshFacesNative( p, _mesh );
			scale(1.1f);
		}
}

	protected void setUpRoom() {
		pushMatrix();
		translate(0, 0, -2000);
		float radiance = 20;
		int samples = 16;
		_jw.jr.background("cornell_box", 
				12000, 6000, 6000,	// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				40, 40, 40, // left rgb
				40, 40, 40, // right rgb
				60, 60, 60, // back rgb
				60, 60, 60, // top rgb
				60, 60, 60  // bottom rgb
		); 
		popMatrix();		
	}
}