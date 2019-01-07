package com.haxademic.sketch.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.draw.toxi.MeshUtilToxi;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.vendor.Toxiclibs;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import toxi.geom.Sphere;
import toxi.geom.mesh.WETriangleMesh;

public class GifRenderEllo004SphereTextureMap
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	
	Sphere _sphere;
	WETriangleMesh _sphereMesh, _deformMesh;
	PImage _texture;

	PShape _logo;
	PShape _logoInverse;
	float _frames = 30;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "500" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "500" );

		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_sphere = new Sphere( p.width/2f );
		_sphere = new Sphere( p.width * 1.75f );
//		AABB box = new AABB( _baseRadius );
		_sphereMesh = new WETriangleMesh();
		_sphereMesh.addMesh( _sphere.toMesh( 60 ) );
		MeshUtilToxi.calcTextureCoordinates( _sphereMesh );
		_deformMesh = _sphereMesh.copy();

		_texture = p.loadImage(FileUtil.getHaxademicDataPath()+"images/ello-large-fill-squish.png");
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-inverse.svg");
	}
	
	public void drawApp() {
		p.background(255);
//		p.fill(255, 40);
//		p.rect(0, 0, p.width, p.height);
		p.noStroke();
		
//		ambientLight(102, 102, 102);
//		lightSpecular(204, 204, 204);
//		directionalLight(102, 102, 102, 0, 0, -1);
//		specular(255, 255, 255);
//		emissive(51, 51, 51);
//		ambient(50, 50, 50);
//		shininess(50.0f); 
		

		
		float frameRadians = PConstants.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedScale = Penner.easeInOutQuart(percentComplete, 0, 1, 1);

		float frameOsc = P.sin( PConstants.TWO_PI * percentComplete);
//		float elloSize = (float)(p.width/1.5f + 7f * frameOsc);
		float elloSize = (float)(p.width/1.5f);
		
		DrawUtil.setDrawCorner(p);
		
		p.translate(p.width/2, p.height/2);
		
		p.pushMatrix();
		
		p.translate( 0, 0, -p.width*2.7f );
		p.rotateZ(P.PI);
		
//		// spin it - y-axis
//		p.rotateY(9.5f - percentComplete * 3.35f); 
//		// spin it - x-axis
//		p.rotateY(7.825f);
//		p.rotateZ(-P.PI/2f - -percentComplete * P.PI); 
//		// shake "no"
//		p.rotateY(P.PI/2 + P.sin(percentComplete * P.TWO_PI) * 0.2f); 
//		// shake "yes"
//		p.rotateY(P.PI/2); 
//		p.rotateZ(P.sin(percentComplete * P.TWO_PI) * 0.1f);
//		// mouse it
//		p.rotateY(p.mouseX / 20f); 
		
//		MeshUtil.deformMeshWithAudio( _sphereMesh, _deformMesh, p._audioInput, 10 );
	
		// draw texture. if tinting happened, reset after drawing
		if( _texture != null ) MeshUtilToxi.drawToxiMesh( p, Toxiclibs.instance(p).toxi, _deformMesh, _texture );
		
		p.popMatrix();
	}
}



