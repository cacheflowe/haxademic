package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_PShapeUtil_OBJNormalizationAndTexturing 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected PShapeSolid objSolid;
	protected PImage img;
	protected float modelHeight;
	protected float _frames = 360;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
	}

	protected void setupFirstFrame() {
		// build obj PShape and scale to window
		img = DemoAssets.textureJupiter();
		obj = DemoAssets.objHumanoid();
		obj = DemoAssets.objSkullRealistic();
		
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToHeight(obj, p.height * 0.5f);
		
		// add UV coordinates to OBJ based on model extents
		float modelExtent = PShapeUtil.getMaxExtent(obj);
		modelHeight = PShapeUtil.getMaxAbsY(obj);
		PShapeUtil.addTextureUVToShape(obj, img, modelExtent, true);
		obj.setTexture(img);
//			PShapeUtil.addTextureUVSpherical(obj, img);
		
		// build solid, deformable PShape object
		objSolid = new PShapeSolid(obj);
	}

	public void drawApp() {
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		background(0);
		PG.setBetterLights(p);
		PG.setDrawCorner(p);
		
		// rotate
		p.translate(p.width/2f, p.height/2f, 0);
		p.rotateY(0.4f * P.sin(percentComplete * P.TWO_PI)); // -P.HALF_PI +

		
		// draw mesh with texture or without
		// if a texture is set, drawing with p.shape() is super slow, so we can manually draw by looping over vertices
		// if no texture, p.shape() is fine
		p.noStroke();
		p.fill(255);
		if(Mouse.xNorm < 0.5f) {
			// texture mapped with decent performance:
			PShapeUtil.drawTriangles(p.g, objSolid.shape(), img, 1f); // img			
		} else {
			// pshape drawing + audioreactive
//			objSolid.setVertexColorWithAudio(255);
			p.shape(objSolid.shape());
		}
		
		// draw ground
		PG.setDrawCenter(p);
		p.translate(0, modelHeight, 0);
		p.rotateX(P.HALF_PI);
		p.fill(255);
		p.rect(0, 0, modelHeight * 3f, modelHeight * 3f);
	}
		
}