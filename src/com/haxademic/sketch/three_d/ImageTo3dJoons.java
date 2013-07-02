package com.haxademic.sketch.three_d;

import java.util.ArrayList;

import joons.JoonsRenderer;
import processing.core.PImage;
import toxi.geom.Triangle3D;
import toxi.geom.mesh.TriangleMesh;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.webcam.WebCamWrapper;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

@SuppressWarnings("serial")
public class ImageTo3dJoons 
extends PAppletHax {

	protected Triangle3D tri;
	protected TriangleMesh mesh;
	protected PImage image;
	protected boolean isWebCam;
	
	
	JoonsRenderer jr;
	boolean rendered = false;
	boolean autoRender = true;
	
	int _objectMaterialIndex = 0;

	float eyeX = 0;
	float eyeY = 120;
	float eyeZ = 40;
	float centerX = 0;
	float centerY = 0;
	float centerZ = 0; // was 40
	float upX = 0;
	float upY = 0;
	float upZ = -1;
	float fov = PI / 4; 
	float aspect = (float) 1;//1.7777;  // was 1.3333
	float zNear = 5;
	float zFar = 10000;
	
	protected ArrayList<Integer> _colors;


	public void setup() {
		super.setup();
		image = p.loadImage( "../data/images/justin-tiny-color1.png" );
		
		jr = new JoonsRenderer(this,width,height);//just declare like this.
		jr.setRenderSpeed(1);//render speed is 1 by default. Set it to 2 for x2 speed. Set it to any number. Lowers quality.
		_colors = new ArrayList<Integer>();
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "sunflow", "false" );
		_appConfig.setProperty( "width", "1000" );
		_appConfig.setProperty( "height", "1000" );
		_appConfig.setProperty( "rendering", "false" );
	}

	public void drawApp() {
		background(0);
		lights();
		p.noStroke();
		
		
		
		beginRecord("joons.OBJWriter","");//just call like this. Leave the second parameter as "".
		
		_objectMaterialIndex = 0;
		_colors.clear();
		
		perspective(fov, aspect, zNear, zFar);//call perspective() before camera()!!
		camera(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);

	
//		p.rotateX(mouseY*0.01f);
		p.rotateX(5f);
		
//		p.rotateY(mouseX*0.01f);

//		drawImgWebCam();
		drawImgPyra();
		
		
		
		
		
		
		endRecord();

		if( autoRender ) {
//			jr.setShader("object0", "ModeBlue");
//			if(x<image.width && y<image.height){
//				jr.setShader("object"+_objectMaterialIndex, "ModeBlue");
//				_objectMaterialIndex++;
//			}
			for(int i=0; i < _colors.size(); i++ ) {
				makeJoonsColor( "Color-"+i, _colors.get(i) );
			}

			jr.setSC(FileUtil.getHaxademicDataPath()+"joons/ambient.sc");	// http://sfwiki.geneome.net/index.php5?title=Main_Page
			rendered=jr.render("bucket");
			// render using render("ipr") to render quick and rough,
			// and render("bucket") to render slow and smooth
			// if successfully rendered, render() returns true
			
			// lots more info here: https://code.google.com/p/joons-renderer/wiki/3_Advanced_Use
			jr.display();
			saveFrame(FileUtil.getHaxademicOutputPath()+SystemUtil.getTimestamp(p)+"render.png");
		}

	}

	public void drawImgPyra() {
		float size = 2f;
		for( int x=0; x < image.width; x++ ){
			for(int y=0; y < image.height; y++){
				int pixelColor = ImageUtil.getPixelColor( image, x, y );
				float pixelBrightness = p.brightness( pixelColor );
//				if( pixelColor != ImageUtil.BLACK_INT && pixelColor != ImageUtil.CLEAR_INT ) {
										
					p.fill( pixelColor );
					p.pushMatrix();
					float xScaled = -image.width*size/2f + x * size;
					float yScaled  = -image.height*size/2f + y * size;
					p.translate(xScaled, yScaled);
					Shapes.drawPyramid( p, pixelBrightness * 0.1f, size, false );
					p.popMatrix();
					
					_colors.add( pixelColor );
					noSmooth();
					
					// set Joons color for pixel
//					makeJoonsColor( "Color-"+x+"-"+y, pixelColor );
//					noSmooth();
//				}
			}
		}
	}
	
	protected void makeJoonsColor( String id, int c ) {
//		color c = color(240, 130, 20);
		int alpha = (c >> 24) & 0xFF;
		int red   = (c >> 16) & 0xFF;
		int green = (c >> 8)  & 0xFF;
		int blue  =  c        & 0xFF;

		
		
		jr.addAfterShader("shader {");
		jr.addAfterShader("   name "+id);
		jr.addAfterShader("   type diffuse");
		jr.addAfterShader("   diff "+ roundForShader(red) +" "+ roundForShader(green) +" "+ roundForShader(blue) +"");
		jr.addAfterShader("}");
		jr.addAfterShader("");
		
//		jr.addAfterShader("shader {");
//		jr.addAfterShader("   name "+id);
//		jr.addAfterShader("   type shiny");
//		jr.addAfterShader("   diff "+ roundForShader(red) +" "+ roundForShader(green) +" "+ roundForShader(blue) +"");
//		jr.addAfterShader("   refl 0.5");
//		jr.addAfterShader("}");
//		jr.addAfterShader("");
		
		
		jr.setShader("object"+_objectMaterialIndex, id);
		_objectMaterialIndex++;
	}
	
	protected float roundForShader( float value ) {
		value /= 255f;
		return Math.round(1000f*value)/1000f;
	}
	
	public void drawImgWebCam() {
		// float size = 24f;
		WebCamWrapper.initWebCam( this, 640, 480 );
		image = ImageUtil.getScaledImage( WebCamWrapper.getImage(), 64, 48 );
		drawImgPyra();
//		drawImgBoxes();
	}

}
