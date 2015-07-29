package com.haxademic.sketch.pshape;

import processing.core.PImage;
import processing.core.PShape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.draw.util.PShapeUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class PShapeObjDeformTest 
extends PAppletHax {

	protected PShape obj;
	protected PShapeSolid objSolid;
	protected PImage img;
	protected float _frames = 60;

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "400" );
		_appConfig.setProperty( "height", "400" );
		_appConfig.setProperty( "fills_screen", "false" );
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "45" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+1) );
	}

	public void setup() {
		super.setup();	
		p.smooth(OpenGLUtil.SMOOTH_HIGH);

		// load texture
		img = p.loadImage(FileUtil.getFile("images/justin-spike-portrait-02-smaller.png"));
		
		// build obj PShape and scale to window
		obj = p.loadShape( FileUtil.getFile("models/skull-realistic.obj"));	
		// obj = p.loadShape( FileUtil.getFile("models/skull-realistic.obj"));	
		PShapeUtil.scaleObjToExtent(obj, p.height * 0.3f);
		
		// add UV coordinates to OBJ
		float modelExtent = PShapeUtil.getObjMaxExtent(obj);
		PShapeUtil.addTextureUVToObj(obj, img, modelExtent);
		// obj.setTexture(img);
		
		// build solid, deformable PShape object
		objSolid = new PShapeSolid(obj);
	}

	public void drawApp() {
		background(255);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);

		// blending test 
//		if(P.round(p.frameCount/20) % 2 == 0) {
			OpenGLUtil.setBlending(p.g, true);
			OpenGLUtil.setBlendMode(p.g, OpenGLUtil.Blend.ALPHA_REVEAL);
//		} else {
//			OpenGLUtil.setBlending(p.g, false);
//			OpenGLUtil.setBlendMode(p.g, OpenGLUtil.Blend.DEFAULT);
//		}
		
		// wireframe hotness!
//		if(P.round(p.frameCount/40) % 2 == 0) {
//			OpenGLUtil.setWireframe(p.g, true);
//		} else {
//			OpenGLUtil.setWireframe(p.g, false);
//		}
		
		// setup lights
		p.lightSpecular(230, 230, 230); 
		p.directionalLight(200, 200, 200, -0.0f, -0.0f, 1); 
		p.directionalLight(200, 200, 200, 0.0f, 0.0f, -1); 
		p.specular(color(255)); 
		p.shininess(5.0f); 

		// rotate
		p.translate(p.width/2f, p.height/2.1f);
//		p.translate(0, 0, -1000);
		p.rotateX(P.sin(-percentComplete * P.TWO_PI) * 0.3f);
//		p.rotateY(-percentComplete * P.TWO_PI);
		p.rotateZ(P.PI);

		
		// draw!
//		objSolid.updateWithAudio(true);
//		float mouz = p.mouseX / 20f;
//		P.println(mouz);
		objSolid.updateWithTrig(true, percentComplete * 2f, 0.04f, 17.4f);
		p.noStroke();
//		p.fill(200, 255, 200);
//		p.stroke(255);
//		p.strokeWeight(0.4f);
		p.shape(objSolid.shape());
	}
		
}