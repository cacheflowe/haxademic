package com.haxademic.sketch.three_d;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.JoonsWrapper;

import processing.core.PImage;
import toxi.geom.Triangle3D;
import toxi.geom.mesh.TriangleMesh;

public class ImageTo3dJoons 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Triangle3D tri;
	protected TriangleMesh mesh;
	protected PImage image;
	protected boolean isWebCam;
	

	public void setup() {
		super.setup();
		image = p.loadImage( FileUtil.getHaxademicDataPath() + "images/dawn-pattern.png" );
	}
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SUNFLOW, "true" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, "false" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, "high" );
		p.appConfig.setProperty( "sunflow_save_images", "true" );
		p.appConfig.setProperty( AppSettings.WIDTH, "1300" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "1000" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void drawApp() {
		background(0);
		lights();
		p.noStroke();
		
				
//		p.rotateX(5.03f);
		p.rotateX(5.7f);
//		p.rotateX(6.35f);
//		p.rotateX(-0.8f);
//		p.rotateX(mouseY*0.01f);
//		P.println(mouseY*0.01f);

		// draw a dark room
//		if( _jw != null ) _jw.drawRoomWithSizeAndColor( width, height, JoonsWrapper.MATERIAL_MIRROR, -1, p.color( 60, 60, 60) );
	
		// draw pyramids
		float size = 3.5f;
		for( int x=0; x < image.width; x++ ){
			for(int y=0; y < image.height; y++){
				int pixelColor = ImageUtil.getPixelColor( image, x, y );
				float pixelBrightness = p.brightness( pixelColor );
//				if( pixelColor != ImageUtil.BLACK_INT && pixelColor != ImageUtil.CLEAR_INT ) {
				
				p.fill( pixelColor );
				p.pushMatrix();
				float xScaled = -image.width*size/2f + (float) x * size;
				float yScaled  = -image.height*size/2f + (float) y * size;
				p.translate(xScaled, yScaled);				
				
				// pyramids
//				Shapes.drawPyramid( p, height, size, false );
//				if( p.appConfig.getBoolean("sunflow", false) == true ) _jw.addColorForObject( JoonsWrapper.MATERIAL_SHINY, pixelColor, 1, false );
				
				// spheres
				sphere(size*0.5f * pixelBrightness/255f);
				if( joons != null ) joons.addColorForObject( JoonsWrapper.MATERIAL_DIFFUSE, pixelColor, 1, true );
				
				
				p.popMatrix();
			}
		}
	}

}
