package com.haxademic.sketch.hardware;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.FlipHFilter;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PConstants;
import processing.core.PImage;
import processing.video.Capture;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.WETriangleMesh;

public class WebCamTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/**
	 * Processing web cam capture object
	 */
	protected Capture _webCam;

	protected int _camW = 1280;
	protected int _camH = 720;

	protected WETriangleMesh _mesh;

	public void setupFirstFrame () {
		initWebCam();
	}

	void initWebCam() {
		String[] cameras = Capture.list();
		if (cameras.length == 0) {
			println("There are no cameras available for capture.");
			exit();
		} else {
			println("Available cameras:");
			for (int i = 0; i < cameras.length; i++) println("["+i+"] "+cameras[i]);
			_webCam = new Capture(this, cameras[0]);
			_webCam.start();
		}      
	}

	public void drawApp() {
		//		if(p.frameCount == 1) _webCam = new Capture(this, camera);

		p.background( 0 );
		DrawUtil.setDrawCenter(p);
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setBasicLights( p );

		p.fill( 0, 0, 0, 255 );
		p.noStroke();


		p.translate( 0, 0, -1000 );
		p.rotateX( 0.02f*p.mouseY );
		p.rotateY( 0.02f*p.mouseX );

		if (_webCam.available()) { 
			// Reads the new frame
			_webCam.read(); 
//			drawDepthImage();
//			drawMesh();
			drawNativeMesh();
//			drawImage();
		} 
	}

	void drawImage(){
		p.image(_webCam, 0, 0);
	}

	void drawDepthImage(){
		int cellsize = 3;
		PImage img = _webCam.get();

		p.noStroke();

		int x, y, color;
		for ( int i = 0; i < _camW; i++) {
			for ( int j = 0; j < _camH; j++) {
				x = i;
				y = j;
				color = ImageUtil.getPixelColor( img, x, y );

				float z = p.brightness( color ) / 10f;

				// Translate to the location, set fill and stroke, and draw the rect
				p.pushMatrix();
				p.translate(-img.width/2 + x, -img.height/2 + y, z);
				p.fill( color, 255 );

				p.rect( 0, 0, cellsize, cellsize );
				p.popMatrix();
			}
		}
	}

	void drawMesh() {
		if( _mesh == null ) createMesh();
		PImage img = _webCam.get();

		// set draw props to draw texture mesh properly
		p.fill( 0 );
		p.noStroke();

		// iterate over all mesh triangles
		// and add their vertices
		p.beginShape(P.TRIANGLES);
		p.texture(img);
		float brightA, brightB, brightC = 0;
		for( Face f : _mesh.getFaces() ) {
			// get z-depth
			brightA = getBrightnessForTextureLoc( img, f.uvA.x, f.uvA.y );
			brightB = getBrightnessForTextureLoc( img, f.uvB.x, f.uvB.y );
			brightC = getBrightnessForTextureLoc( img, f.uvC.x, f.uvC.y );
			// draw vertices
			p.vertex(f.a.x,f.a.y,f.a.z+brightA,f.uvA.x,f.uvA.y);
			p.vertex(f.b.x,f.b.y,f.b.z+brightB,f.uvB.x,f.uvB.y);
			p.vertex(f.c.x,f.c.y,f.c.z+brightC,f.uvC.x,f.uvC.y);
		}
		p.endShape();
	}

	float getBrightnessForTextureLoc( PImage img, float x, float y ) {
		float loc = x + y * img.width;  //  p.Pixel array location
		int c = img.pixels[(int)loc];  // Grab the color
		return p.brightness(c) * 0.1f;
	}

	void createMesh() {
		_mesh = new WETriangleMesh();

		int cols = _camW;
		int rows = _camH;
		for ( int i = 0; i < cols - 1; i++) {
			for ( int j = 0; j < rows - 1; j++) {
				// position mesh out from center
				float x = i - _camW/2;
				float y = j - _camH/2;
				// create 2 faces and their UV texture coordinates
				_mesh.addFace( new Vec3D( x, y, 0 ), new Vec3D( x+1, y, 0 ), new Vec3D( x+1, y+1, 0 ), new Vec2D( i, j ), new Vec2D( i+1, j ), new Vec2D( i+1, j+1 ) );
				_mesh.addFace( new Vec3D( x, y, 0 ), new Vec3D( x+1, y+1, 0 ), new Vec3D( x, y+1, 0 ), new Vec2D( i, j ), new Vec2D( i+1, j+1 ), new Vec2D( i, j+1 )  );
			}
		}
	}

	/**
	 * Good old-fashioned Processing mesh
	 */
	void drawNativeMesh() {
		PImage img = _webCam.get();
		int x, y, color;
		p.beginShape(P.TRIANGLES);
		int skip = 10;
		for ( int i = 0; i < _camW - 1; i+=skip) {
			for ( int j = 0; j < _camH - 1; j+=skip) {
				x = i;  // x position
				y = j;  // y position
				color = ImageUtil.getPixelColor( img, x, y );

				float z = p.brightness(color);

				p.fill(color);
				p.noStroke();

				// draw grid out from center
				x = -img.width/2 + x;
				y = -img.height/2 + y;

				// draw trianges 
				p.vertex( x, y, z );
				p.vertex( x+skip, y, z );
				p.vertex( x+skip, y+skip, z );

				p.vertex( x, y, z );
				p.vertex( x, y+skip, z );
				p.vertex( x+skip, y+skip, z );

			}
		}
		p.endShape();
	}
}
