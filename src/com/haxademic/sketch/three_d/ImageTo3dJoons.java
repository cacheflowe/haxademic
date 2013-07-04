package com.haxademic.sketch.three_d;

import processing.core.PImage;
import toxi.geom.Triangle3D;
import toxi.geom.mesh.TriangleMesh;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class ImageTo3dJoons 
extends PAppletHax {

	protected Triangle3D tri;
	protected TriangleMesh mesh;
	protected PImage image;
	protected boolean isWebCam;
	
	
	boolean autoRender = true;
		
	protected JoonsWrapper _jw;

	public void setup() {
		super.setup();
		image = p.loadImage( FileUtil.getHaxademicDataPath() + "images/justin-small-bw.png" );
		_jw = new JoonsWrapper( p, width, height, JoonsWrapper.QUALITY_HIGH );
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "sunflow", "false" );
		_appConfig.setProperty( "width", "1000" );
		_appConfig.setProperty( "height", "1200" );
		_appConfig.setProperty( "rendering", "false" );
	}

	public void drawApp() {
		background(0);
//		lights();
		p.noStroke();
		
		
		_jw.startFrame();
		
		p.rotateX(5.03f);
//		p.rotateX(-0.8f);
//		p.rotateX(mouseY*0.01f);
//		P.println(mouseY*0.01f);
//		p.rotateY(mouseX*0.01f);

		_jw.drawRoomWithSizeAndColor( width, height, JoonsWrapper.MATERIAL_MIRROR, -1, p.color( 60, 60, 60) );
		
		// mirror ball
//		pushMatrix();
//		translate(0,-50,0);
//		fill(255);
//		sphere(30);
//		popMatrix();
//		// always call after drawing shapes
//		_jw.addColorForObject( JoonsWrapper.MATERIAL_MIRROR, -1, p.color( 255, 255, 255 ), true );

	
		// draw pyramids
		float size = 1.4f;
		float height = 0;
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
				height = pixelBrightness * 0.1f;
//				if( height < 2 ) height = 2;
				
				
				// pyramids
//				Shapes.drawPyramid( p, height, size, false );
//				_jw.addColorForObject( JoonsWrapper.MATERIAL_SHINY, pixelColor, 1, false );
				
				// spheres
				sphere(size*0.5f * pixelBrightness/255f);
				_jw.addColorForObject( JoonsWrapper.MATERIAL_CONSTANT, pixelColor, 1, true );
				
				
				p.popMatrix();
			}
		}
		
		// render frame
		if( autoRender == true ) _jw.endFrame();
	}

}
