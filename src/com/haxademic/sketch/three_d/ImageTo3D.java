package com.haxademic.sketch.three_d;

import processing.core.PImage;
import toxi.geom.AABB;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.geom.mesh.WETriangleMesh;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.webcam.WebCamWrapper;
import com.haxademic.core.image.ImageUtil;

@SuppressWarnings("serial")
public class ImageTo3D 
extends PAppletHax {

	protected Triangle3D tri;
	protected TriangleMesh mesh;
	protected PImage image;
	protected boolean isWebCam;

	public void setup() {
		super.setup();
		image = p.loadImage( "../data/images/sean-dough-tiny-color2.png" );
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "sunflow", "false" );
		_appConfig.setProperty( "width", "1000" );
		_appConfig.setProperty( "height", "750" );
		_appConfig.setProperty( "rendering", "false" );
	}

	public void drawApp() {
		background(0);
		lights();
		translate(width/2, height/2, -900);
		p.noStroke();
	
		p.rotateX(mouseY*0.01f);
		p.rotateY(mouseX*0.01f);
//		p.rotateX(P.PI/5f);

		drawImgWebCam();
	}

	public void drawImgBoxes() {
		float size = 24f;
		for( int x=0; x < image.width; x++ ){
			for(int y=0; y < image.height; y++){
				WETriangleMesh mesh = new WETriangleMesh();
				int pixelColor = ImageUtil.getPixelColor( image, x, y );
				float pixelBrightness = p.brightness( pixelColor );
				AABB box = new AABB( size / 2f );
				box.set( -image.width*size/2 + x * size, -image.height*size/2 + y * size, 0 );
				mesh.addMesh( box.toMesh() );
				mesh.scale( new Vec3D( 1, 1, pixelBrightness / 30f ) );
				p.fill( pixelColor );
				p.toxi.mesh( mesh );
			}
		}
	}

	public void drawImgPyra() {
		float size = 30f;
		for( int x=0; x < image.width; x++ ){
			for(int y=0; y < image.height; y++){
				int pixelColor = ImageUtil.getPixelColor( image, x, y );
				float pixelBrightness = p.brightness( pixelColor );
				if( pixelColor != ImageUtil.BLACK_INT && pixelColor != ImageUtil.CLEAR_INT ) {
					p.fill( pixelColor );
					p.pushMatrix();
					float xScaled = -image.width*size/2 + x * size;
					float yScaled  = -image.height*size/2 + y * size;
					p.translate(xScaled, yScaled);
					Shapes.drawPyramid( p, pixelBrightness * 2, (int)size, false );
					p.popMatrix();
				}
			}
		}
	}
	
	public void drawImgWebCam() {
		// float size = 24f;
		WebCamWrapper.initWebCam( this, 640, 480 );
		image = ImageUtil.getScaledImage( WebCamWrapper.getImage(), 64, 48 );
		drawImgPyra();
//		drawImgBoxes();
	}

}
