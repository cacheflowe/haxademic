package com.haxademic.sketch.render;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.toxi.DrawToxiMesh;
import com.haxademic.core.draw.toxi.MeshUtilToxi;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.JoonsWrapper;

import toxi.color.TColor;
import toxi.geom.mesh.WETriangleMesh;

public class RenderLogo
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	

//	protected TColor MODE_SET_BLUE = TColorInit.newRGBA( 0, 200, 234, 255 );
//	protected TColor COLOR = TColor.newHex("FF007E"); // TColorInit.newRGBA( 0, 200, 234, 255 ); // TColor.newHex("FFFF00"); // DAFFCA // FED7ED
	protected TColor COLOR = TColor.newHex("ffffff"); // TColorInit.newRGBA( 0, 200, 234, 255 ); // TColor.newHex("FFFF00"); // DAFFCA // FED7ED
	
	WETriangleMesh _mesh;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SUNFLOW, "true" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, "false" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, "high" );
		p.appConfig.setProperty( "sunflow_save_images", "true" );

		
		p.appConfig.setProperty( AppSettings.WIDTH, "1280" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "1280" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.FPS, "30" );
	}

	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);

		_mesh = MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, FileUtil.getFile("svg/cacheflowe-logo.svg"), -1, 20, 0.9f ), 40 );
//		_mesh = MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, "/Users/cacheflowe/Documents/workspace/haxademic/output/fractal-2014-11-19-19-57-24-01.svg", -1, 20, 1.2f ), 80 );
//		_mesh = MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, FileUtil.getHaxademicDataPath() + "svg/fractal-2013-09-26-20-11-32.svg", -1, 20, 1.2f ), 40 );
//		_mesh = MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, FileUtil.getHaxademicDataPath() + "svg/ello.svg", -1, 20, 1.2f ), 40 );
	}


	public void drawApp() {
//		p.background(0);
//		p.lights();

//		_jw.jr.background(JoonsWrapper.BACKGROUND_GI);
		joons.jr.background(JoonsWrapper.BACKGROUND_AO);
		joons.jr.background(0, 0, 0); //background(gray), or (r, g, b), like Processing.
		setUpRoom();
//		makeLightSource();
		
		translate(0, 0, -2300);
//		p.rotateX(0.3f);

//		p.fill(255);
//		p.stroke(255);
//		_jw.jr.fill( JoonsWrapper.MATERIAL_DIFFUSE, COLOR.red() * 255f, COLOR.green() * 255f, COLOR.blue() * 255f );
//		DrawMesh.drawToxiMeshFacesNative( p, _mesh );

	
		for( int i=0; i < 20; i++) {
		
			translate(0, 0, -60);
			p.fill(255);
			p.noStroke();
			if( i%2 == 0 ) {
				joons.jr.fill( JoonsWrapper.MATERIAL_SHINY, COLOR.red() * (255 - i*10), COLOR.green() * (255 - i*10), COLOR.blue() * (255 - i*10) );
			} else {
				joons.jr.fill( JoonsWrapper.MATERIAL_SHINY, 0, 0, 0, 1f);
			}
			p.pushMatrix();
			p.rotateX(0.2f);
			DrawToxiMesh.drawToxiMeshFacesNative( p, _mesh );
			p.popMatrix();
			scale(1.115f);
//			scale(0.9f);
		}
	}

	protected void makeLightSource() {
		joons.jr.fill("light", 200, 255, 200);
		sphere(10);
		translate(-2300, 0, 0);
	}
	
	protected void setUpRoom() {
		pushMatrix();
		translate(0, 0, -2000);
		float radiance = 20;
		int samples = 16;
//		_jw.jr.background("cornell_box", 
//				12000, 6000, 8000,	// width, height, depth
//				radiance, radiance, radiance, samples,  // radiance rgb & samples
//				40, 40, 40, // left rgb
//				40, 40, 40, // right rgb
//				60, 60, 60, // back rgb
//				60, 60, 60, // top rgb
//				60, 60, 60  // bottom rgb
//		); 
		joons.jr.background("cornell_box", 
				12000, 6000, 6000,	// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				0,0,0, // left rgb
				0,0,0, // right rgb
				0,0,0, // back rgb
				0,0,0, // top rgb
				0,0,0  // bottom rgb
		); 
//		_jw.jr.background("cornell_box", 
//				12000, 6000, 6000,	// width, height, depth
//				radiance, radiance, radiance, samples,  // radiance rgb & samples
//				255,255,255, // left rgb
//				255,255,255, // right rgb
//				255,255,255, // back rgb
//				255,255,255, // top rgb
//				255,255,255  // bottom rgb
//		); 
		popMatrix();		
	}
}