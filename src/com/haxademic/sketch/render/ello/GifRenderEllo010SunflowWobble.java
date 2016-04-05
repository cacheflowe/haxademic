package com.haxademic.sketch.render.ello;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.DrawMesh;
import com.haxademic.core.draw.mesh.MeshUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.system.FileUtil;

import toxi.color.TColor;
import toxi.geom.mesh.WETriangleMesh;

public class GifRenderEllo010SunflowWobble
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	float _frames = 40;

	protected TColor COLOR = TColor.newHex("000000"); // TColorInit.newRGBA( 0, 200, 234, 255 ); // TColor.newHex("FFFF00"); // DAFFCA // FED7ED
	
	WETriangleMesh _mesh;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "500" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "500" );
		
		p.appConfig.setProperty( AppSettings.SUNFLOW, "true" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, "false" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, "low" );
		p.appConfig.setProperty( "sunflow_save_images", "false" );
		
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "45" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}
	
	public void setup() {
		super.setup();
		p.background(255);
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_mesh = MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, FileUtil.getHaxademicDataPath() + "svg/ello.svg", -1, 20, 0.5f ), 20 );
	}
	
	public void drawApp() {		
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
		DrawUtil.setDrawCenter(p);
		joons.jr.fill( JoonsWrapper.MATERIAL_SHINY, 255, 255, 255 );
		p.box(p.width*10, p.width*10, 2);
		
		// oscillate and draw logo
		translate(0, 0, 15 + P.sin(radiansComplete) * 15);
		p.rotateX(0.2f * P.sin(P.PI/2f + radiansComplete));

		joons.jr.fill( JoonsWrapper.MATERIAL_SHINY, COLOR.red() * 255, COLOR.green() * 255, COLOR.blue() * 255 );
		DrawMesh.drawToxiMeshFacesNative( p, _mesh );
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



