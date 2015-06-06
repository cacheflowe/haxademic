package com.haxademic.core.draw.util;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.Texture;

import com.haxademic.core.app.P;

public class DrawUtil {
	/**
	 * Clears all drawing properties so we may always have the same starting
	 * point when drawing across classes.
	 * 
	 * @param p		Processing Applet for reference to p5 core
	 */
	public static void resetGlobalProps( PApplet p ) {
		// p.resetMatrix();
		p.colorMode( P.RGB, 255, 255, 255, 255 );
		p.fill( 0, 255, 0, 255 );
		p.stroke( 0, 255, 0, 255 );
		p.strokeWeight( 1 );
		p.camera();
		setDrawCenter(p);
	}
	public static void resetGlobalProps(PGraphics p) {
		// p.resetMatrix();
		p.colorMode( P.RGB, 255, 255, 255, 255 );
		p.fill( 0, 255, 0, 255 );
		p.stroke( 0, 255, 0, 255 );
		p.strokeWeight( 1 );
		p.camera();
		setDrawCenter(p);
	}

	public static void setCenter( PApplet p ) {
		// p.resetMatrix();
		p.translate( 0, 0, 0 );
	}
	public static void setCenter(PGraphics p) {
		// p.resetMatrix();
		p.translate( 0, 0, 0 );
	}

	public static void setCenterScreen( PApplet p ) {
		// p.resetMatrix();
		p.translate( p.width/2, p.height/2, 0 );
	}
	public static void setCenterScreen(PGraphics p) {
		// p.resetMatrix();
		p.translate( p.width/2, p.height/2, 0 );
	}

	public static void setTopLeft( PApplet p ) {
		// p.resetMatrix();
		p.translate( -p.width/2, -p.height/2, 0 );
	}
	public static void setTopLeft( PGraphics p ) {
		// p.resetMatrix();
		p.translate( -p.width/2, -p.height/2, 0 );
	}

	public static void setBasicLights( PApplet p ) {
		// setup lighting props
		p.shininess(500); 
		p.lights();
		p.ambientLight(0.2f,0.2f,0.2f, 0, 0, 6000);
		p.ambientLight(0.2f,0.2f,0.2f, 0, 0, -6000);
	}
	public static void setBasicLights( PGraphics p ) {
		// setup lighting props
		p.shininess(500); 
		p.lights();
		p.ambientLight(0.2f,0.2f,0.2f, 0, 0, 6000);
		p.ambientLight(0.2f,0.2f,0.2f, 0, 0, -6000);
	}

	public static void setBetterLights( PApplet p ) {
		setBetterLights(p.g);
	}
	public static void setBetterLights( PGraphics p ) {
		// setup lighting props
		p.lightSpecular(230, 230, 230); 
		p.directionalLight(200, 200, 200, -0.0f, -0.0f, 1); 
		p.directionalLight(200, 200, 200, 0.0f, 0.0f, -1); 
		p.specular(p.color(200)); 
		p.shininess(5.0f); 
	}
	
	
	public static void setDrawCorner( PApplet p ) { 
		setDrawCorner(p.g);
	}
	public static void setDrawCorner( PGraphics p ) {
		p.imageMode( PConstants.CORNER );
		p.rectMode( PConstants.CORNER );
		p.ellipseMode( PConstants.CORNER );
		p.shapeMode( PConstants.CORNER );
	}
	
	public static void setDrawCenter( PApplet p ) {
		setDrawCenter(p.g);
	}
	public static void setDrawCenter( PGraphics p ) {
		p.imageMode( PConstants.CENTER );
		p.rectMode( PConstants.CENTER );
		p.ellipseMode( PConstants.CENTER );
		p.shapeMode( PConstants.CENTER );
	}
	
	public static void setColorForPImage( PApplet p ) {
		setColorForPImage(p.g);
	}
	public static void setColorForPImage( PGraphics p ) {
		p.fill( 255, 255, 255, 255 );
	}
	
	public static void setPImageAlpha( PApplet p, float alpha ) {
		setPImageAlpha(p.g, alpha);
	};
	public static void setPImageAlpha( PGraphics p, float alpha ) {
		p.tint( 255, alpha * 255 );
	}
	
	public static void resetPImageAlpha( PApplet p ) {
		resetPImageAlpha(p.g);
	};
	public static void resetPImageAlpha( PGraphics p ) {
		p.tint( 255 );
	}
	
	public static void setDrawFlat2d( PApplet p, boolean is2d ) {
		setDrawFlat2d(p.g, is2d);
	};
	public static void setDrawFlat2d( PGraphics p, boolean is2d ) {
		if( is2d ) {
			p.hint( P.DISABLE_DEPTH_TEST );
		} else {
			p.hint( P.ENABLE_DEPTH_TEST );
		}
	}
	
	public static void setTextureRepeat( PApplet p, boolean doesRepeat ) {
		setTextureRepeat(p.g, doesRepeat);
	};
	public static void setTextureRepeat(PGraphics pg, boolean doesRepeat) {
		if( doesRepeat == true ) 
			(pg).textureWrap(Texture.REPEAT);
		else 
			(pg).textureWrap(Texture.CLAMP);
	}
	
}
