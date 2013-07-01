package com.haxademic.sketch.three_d;

import processing.core.PApplet;
import toxi.geom.Matrix4x4;
import toxi.geom.Quaternion;
import toxi.geom.Vec3D;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.TColorInit;

public class BoxConnect
extends PAppletHax  
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Auto-initialization of the main class.
	 * @param args
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "com.haxademic.sketch.three_d.BoxConnect" });
	}
		
	public void setup() {
		super.setup();
		
		// Sunflow needs to use colors between 0-1. Default processing color mode is 0-255. 
		// TColor likes the former, so we normalize with TColorInit, depending on the rendering mode.
		// This way we can always use normal 0-255 RGB color blending in either case.
		if( _graphicsMode == P.OPENGL ) {
			p.colorMode( P.RGB, 1f, 1f, 1f, 1f );
		} else {
			p.colorMode( P.RGB, 255f, 255f, 255f, 255f );
		}
		
		// for sunflow, we need to set these before the first draw()
		p.background( 0 );
		p.smooth();
		p.noStroke();
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "sunflow", "false" );
		_appConfig.setProperty( "rendering", "false" );
	}
		
	public void drawApp() {
		if(frameCount < 3) {
	//		DrawUtil.setBasicLights( p );
			// draw background and set to center
			if( _graphicsMode == P.OPENGL ) p.background(0,0,0,255);
			
			p.translate(p.width/2, p.height/2, -400);
	//		p.rotateY(p.frameCount/100f);
			
			p.noStroke();
			p.fill( TColorInit.newRGBA( 0, 200f, 234f, 255f ).toARGB() );
			
			
			Vec3D origVec = new Vec3D();
			Vec3D targetVec = new Vec3D(100, 100, -100);
			Matrix4x4 matrix = new Matrix4x4(); 
			matrix.applyToSelf(origVec); 
			Matrix4x4 orient = Quaternion.getAlignmentQuat(targetVec, Vec3D.Z_AXIS).toMatrix4x4(matrix);
			orient.applyTo(origVec);
			p.pushMatrix();
			p.rotateZ(origVec.headingXY());
			p.rotateY(origVec.headingXZ());
			
			p.box( 200f );
			
			p.popMatrix();
		}
	}


}
