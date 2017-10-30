package com.haxademic.sketch.three_d;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.toxi.MeshPool;
import com.haxademic.core.draw.toxi.MeshUtilToxi;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat3d;
import com.haxademic.core.render.Renderer;

import geomerative.RFont;
import geomerative.RG;
import processing.core.PApplet;
import processing.core.PConstants;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class MeshLoaderDemo 
extends PApplet
{
	ToxiclibsSupport toxi;
	PApplet p;
		
	Renderer _render;
	
//	OBJModel _model;
	MeshPool _meshPool;
	WETriangleMesh _mesh;
//	float _rot;
	EasingFloat3d _rot;
	int _meshIndex;
	ArrayList<String> _modelIds;
	boolean _wireFrame = false;
	
	boolean isSunflow = false;

	public void setup () {
		p = this;
		// set up stage and drawing properties
		if( isSunflow == true ) {
			p.size( 1200, 800, "hipstersinc.P5Sunflow" );				//size(screen.width,screen.height,P3D);
		} else {
			p.size( 1200, 800, P.P3D );				//size(screen.width,screen.height,P3D);
//			OpenGLUtil.setQuality( p, OpenGLUtil.MEDIUM );
		}
		p.frameRate( 30 );
		p.colorMode( PConstants.RGB, 255, 255, 255, 255 );
		p.background( 0 );
		p.smooth();
		
		p.rectMode(PConstants.CENTER);
		p.noStroke();
		toxi = new ToxiclibsSupport( p );
		
		// set up renderer
//		_render = new Renderer( this, 30, Renderer.OUTPUT_TYPE_MOVIE );
//		_render.startRenderer();
		
		_rot = new EasingFloat3d( 0, 0, 0, 10f );
		
		
		_meshPool = new MeshPool( p );
		
		// 3d text with different options
		if( RG.initialized() == false ) RG.init( p );
		RFont helloDenverFont = new RFont( "../data/fonts/HelloDenverDisplay-Regular.ttf", 200, RFont.CENTER);
		RFont bitLowFont = new RFont( "../data/fonts/bitlow.ttf", 200, RFont.CENTER);
//		_meshPool.addMesh( "HAI", MeshUtil.mesh2dFromTextFont( p, null, "../data/fonts/bitlow.ttf", 200, "HAI", -1, 2, 1f ), 1 );
//		WETriangleMesh helloTextMesh = MeshUtil.mesh2dFromTextFont( p, helloDenverFont, null, -1, "HELLO", -1, 3, 1f );
//		_meshPool.addMesh( "HELLO", helloTextMesh, 1 );
//		_meshPool.addMesh( "HELLO_3D", MeshUtil.getExtrudedMesh( helloTextMesh, 20 ), 1 );
//		
//		_meshPool.addMesh( "PRESENTS_TEXT", MeshUtil.mesh2dFromTextFont( p, bitLowFont, null, 200, "MODE SET", -1, 2, 0.4f ), 1 );
//
//		_meshPool.addMesh( "POLY_HOLE_PENT", MeshUtil.meshFromOBJ( p, "../data/models/poly-hole-penta.obj", 1f ), 70f );
//		_meshPool.addMesh( "POLY_HOLE_SQUARE", MeshUtil.meshFromOBJ( p, "../data/models/poly-hole-square.obj", 1f ), 70f );
//		_meshPool.addMesh( "POLY_HOLE_TRI", MeshUtil.meshFromOBJ( p, "../data/models/poly-hole-tri.obj", 1f ), 70f );

		
//		_meshPool.addMesh( "DESIGN_BY", MeshUtil.mesh2dFromTextFont( p, bitLowFont, null, 200, "DESIGN BY:", -1, 2, 0.3f ), 1 );
//		_meshPool.addMesh( "JON_DESIGN", MeshUtil.mesh2dFromTextFont( p, bitLowFont, null, 200, "JON TRAISTER", -1, 2, 0.4f ), 1 );
//		_meshPool.addMesh( "RYAN_DESIGN", MeshUtil.mesh2dFromTextFont( p, bitLowFont, null, 200, "RYAN POLICKY", -1, 2, 0.4f ), 1 );

		//		_meshPool.addMesh( "COUNTDOWN_TEXT_1", MeshUtil.getExtrudedMesh( MeshUtil.mesh2dFromTextFont( p, bitLowFont, null, 200, "1", -1, 2, 3f ), 4 ), 1 );
//		_meshPool.addMesh( "COUNTDOWN_TEXT_2", MeshUtil.getExtrudedMesh( MeshUtil.mesh2dFromTextFont( p, bitLowFont, null, 200, "2", -1, 2, 3f ), 4 ), 1 );
//		_meshPool.addMesh( "COUNTDOWN_TEXT_3", MeshUtil.getExtrudedMesh( MeshUtil.mesh2dFromTextFont( p, bitLowFont, null, 200, "3", -1, 2, 3f ), 4 ), 1 );
		
		// .svg vectors
		WETriangleMesh cacheSVG = MeshUtilToxi.meshFromSVG( p, "../data/svg/cacheflowe-logo.svg", -1, 10, 1f );
//		_meshPool.addMesh( "CACHE", cacheSVG, 0.5f );
//		_meshPool.addMesh( "CACHE_EXTRUDE", MeshUtil.getExtrudedMesh( cacheSVG, 20 ), 1 );
//		_meshPool.addMesh( "DIAMOND_2D", MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, "../data/svg/diamond.svg", -1, 3, 0.5f ), 20 ), 1 );
//		_meshPool.addMesh( "CACHEFLOWE_LOGOTYPE", MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, "../data/svg/cacheflowe-logotype.svg", -1, 6, 0.7f ), 4 ), 1 );
//		_meshPool.addMesh( "MODE_SET_LOGOTYPE", MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, "../data/svg/modeset-logotype.svg", -1, 6, 0.7f ), 4 ), 1 );
		_meshPool.addMesh( "WUKI", MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, "../data/svg/wuki-2.svg", -1, 6, 0.7f ), 4 ), 1 );
//		_meshPool.addMesh( "CDW_LOGO", MeshUtil.meshFromSVG( p, "../data/svg/create-denver-logo.svg", -1, 3, 0.6f ), 1 );
//
//		_meshPool.addMesh( "GUICEWORKS_LOGO", MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, "../data/svg/guiceworks-logo.svg", -1, 20, 0.7f ), 20 ), 1 );
//
//		_meshPool.addMesh( "BIKE_COMMUTER", MeshUtil.meshFromSVG( p, "../data/svg/bike-commuter.svg", -1, 7, 0.5f ), 1 );
		
		// img models
//		_meshPool.addMesh( "KACHEOUT", MeshUtil.meshFromImg( p, "../data/images/kacheout/kacheout.gif", 1f ), 20f );
//		_meshPool.addMesh( "MUSIC_NOTE", MeshUtil.meshFromImg( p, "../data/images/music.gif", 1f ), 40f );
//		_meshPool.addMesh( "UFO", MeshUtil.meshFromImg( p, "../data/images/kacheout/invader-01.gif", 1f ), 30f );
//		
//		// .obj models
//		_meshPool.addMesh( "POINTER", MeshUtil.meshFromOBJ( p, "../data/models/pointer_cursor_2_hollow.obj", 1f ), 1.5f );
//		_meshPool.addMesh( "DIAMOND", MeshUtil.meshFromOBJ( p, "../data/models/diamond.obj", 1f ), 1.2f );
//		_meshPool.addMesh( "INVADER", MeshUtil.meshFromOBJ( p, "../data/models/invader.obj", 1f ), 45 );
//		_meshPool.addMesh( "LEGO_MAN", MeshUtil.meshFromOBJ( p, "../data/models/lego-man.obj", 1f ), 30 );
//		_meshPool.addMesh( "DISCOVERY", MeshUtil.meshFromOBJ( p, "../data/models/the-discovery-multiplied-seied.obj", 1f ), 900 );
//		_meshPool.addMesh( "SOCCER_BALL", MeshUtil.meshFromOBJ( p, "../data/models/soccer_ball.obj", 1f ), 100 );
//		_meshPool.addMesh( "TOPSECRET", MeshUtil.meshFromOBJ( p, "../data/models/topsecret-seied.obj", 1f ), 400 );
//		_meshPool.addMesh( "MODE_SET", MeshUtil.meshFromOBJ( p, "../data/models/mode-set.obj", 1f ), 150 );
//		_meshPool.addMesh( "SPIROGRAPH", MeshUtil.meshFromOBJ( p, "../data/models/spirograph-seied.obj", 1f ), 150 );
//		_meshPool.addMesh( "CACHEFLOWE", MeshUtil.meshFromOBJ( p, "../data/models/cacheflowe-3d.obj", 1f ), 150 );
		_meshPool.addMesh( "chicken", MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/chicken.obj", 1f ), 50 );
		_meshPool.addMesh( "library_chair", MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/library-chair.obj", 1f ), 50 );
		_meshPool.addMesh( "strat.obj", MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/strat.obj", 1f ), 50 );
		_meshPool.addMesh( "octahedron.obj", MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/octahedron.obj", 1f ), 50 );

		
//		_objPool.loadObj( "SUBMISH_HORIZ", 		200, 	"./models/submish-rotated.obj" );
//		_objPool.loadObj( "SHUTTLE", 			30, 	"./models/Space Shuttle.obj" );
//		_objPool.loadObj( "SPEAKER", 			200, 	"./models/speaker.obj" );
//		_objPool.loadObj( "HOUSE", 				150, 	"./models/monopoly-house.obj" );
//		_objPool.loadObj( "CAR_65", 			100, 	"./models/car65.obj" );
//		_objPool.loadObj( "BANANA", 			0.5f, 	"./models/banana.obj" );
		
		
		_modelIds = _meshPool.getIds();
		_mesh = _meshPool.getMesh( _modelIds.get( 0 ) );

//		ThreeDeeUtil.SmoothToxiMesh( p, _mesh, 2 );
		
		DebugUtil.showMemoryUsage();
//		if( isSunflow == false ) OpenGLUtil.setQuality( p, OpenGLUtil.HIGH );
	}
	
	public void keyPressed() {
		// cycle through images
		if( key == ' ' ) {
			_meshIndex++;
			if( _meshIndex >= _modelIds.size() ) _meshIndex = 0;
			_mesh = _meshPool.getMesh( _modelIds.get( _meshIndex ) );
		}
		if( key == 'w' ) {
			_wireFrame = !_wireFrame;
		}
	}

	public void draw() {
		DrawUtil.setBasicLights( p );
		// draw background and set to center
		if( isSunflow == false ) p.background(0,0,0,255);
		p.translate(p.width/2, p.height/2, 0);
		
		
		// rotate with mouse
		_rot.setTargetX( p.mouseX/100f );
		_rot.setTargetY( p.mouseY/100f );
//		_rot.setTargetZ( (p.mouseY+p.mouseX)/100f );
		_rot.update();
		p.rotateZ( _rot.x() );
		p.rotateY( _rot.y() );
		p.rotateZ( _rot.z() );
		
		// draw WETriangleMesh
		if( _wireFrame ) {
			p.stroke(255,249,0, 255);	// cacheflowe yellow
			p.noFill();
		} else {
			p.fill(255, 255);		// white
//			p.fill(0,200,234, 255);	// mode set blue
//			p.fill(255,249,0, 255);	// cacheflowe yellow
//			p.fill(237,30,121, 255);// guiceworks pink	#ed1e79
			p.noStroke();
		}

		// draw to screen
		toxi.mesh( _mesh );
		
		
		
		// render movie
		if( _render != null ) {
			_render.renderFrame();
			if( p.frameCount == 300 ) {
				P.println( "done!" );
				_render.stop();
				exit();
			} else {
				for( int i = 0; i < 100; i++ ) P.println( "rendering frame: " + p.frameCount );
			}
		}
	}
}
