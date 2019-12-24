package com.haxademic.render.joons;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.render.Renderer;

import processing.core.PImage;
import toxi.geom.Triangle3D;
import toxi.geom.mesh.TriangleMesh;

public class ImageTo3dJoons 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Triangle3D tri;
	protected TriangleMesh mesh;
	protected PImage image;
	protected boolean isWebCam;
	

	protected void config() {
		Config.setProperty( AppSettings.SUNFLOW, "true" );
		Config.setProperty( AppSettings.SUNFLOW_ACTIVE, "false" );
		Config.setProperty( AppSettings.SUNFLOW_QUALITY, "high" );
		Config.setProperty( "sunflow_save_images", "true" );
		Config.setProperty( AppSettings.WIDTH, "1300" );
		Config.setProperty( AppSettings.HEIGHT, "1000" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void drawApp() {
		background(0);
		lights();
		p.noStroke();
		
		image = DemoAssets.smallTexture();
				
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
//				if( Config.getBoolean("sunflow", false) == true ) _jw.addColorForObject( JoonsWrapper.MATERIAL_SHINY, pixelColor, 1, false );
				
				// spheres
				sphere(size*0.5f * pixelBrightness/255f);
				if( Renderer.instance().joons != null ) Renderer.instance().joons.addColorForObject( JoonsWrapper.MATERIAL_DIFFUSE, pixelColor, 1, true );
				
				
				p.popMatrix();
			}
		}
	}

}
