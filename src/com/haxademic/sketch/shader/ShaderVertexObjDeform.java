package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.textures.pgraphics.TextureEQGrid;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class ShaderVertexObjDeform
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	PImage texture;
	BaseTexture audioTexture;
	PerlinTexture perlinTexture;
	PShape obj;
	float angle;
	PShader texShader;
	float _frames = 60;
	boolean _is3d = true;
	boolean _isAudio = true;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "false" );
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "640" );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "3" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+2) );

	}

	public void setup() {
		super.setup();	
		
		// create dynamic deformation texture
		audioTexture = new TextureEQGrid(800, 800);
		perlinTexture = new PerlinTexture(p, 200, 200);
		// audioTexture = new TextureEQConcentricCircles(800, 800);
		PGraphics displacementMap = (_isAudio == true) ? audioTexture.texture() : perlinTexture.texture();
		
		// create geometry
		if(_is3d == true) {
			obj = p.loadShape( FileUtil.getFile("models/unicorn-head-lowpoly.obj"));
			PShapeUtil.scaleShapeToExtent(obj, p.height * 0.25f);
			float modelExtent = PShapeUtil.getMaxExtent(obj);
			PShapeUtil.addTextureUVToShape(obj, displacementMap, modelExtent);
			obj.setTexture(displacementMap);
			
			// load shader
			texShader = loadShader(
					FileUtil.getFile("haxademic/shaders/vertex/brightness-displace-frag-texture.glsl"), 
					FileUtil.getFile("haxademic/shaders/vertex/brightness-displace-sphere-vert.glsl")
					);
		} else {
			obj = Shapes.createSheet(10, displacementMap);			
			// load shader
			texShader = loadShader(
					FileUtil.getFile("haxademic/shaders/vertex/brightness-displace-frag-texture.glsl"), 
					FileUtil.getFile("haxademic/shaders/vertex/brightness-displace-sheet-vert.glsl")
					);
		}
		
		texShader.set("displacementMap", displacementMap);
		texShader.set("displaceStrength", 1.0f);
	}

	public void drawApp() {
		background(0);

		// rendering
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		angle = P.TWO_PI * percentComplete;
		
		PGraphics displacementMap; 
		if(_isAudio == true) {
			audioTexture.update();
			displacementMap = audioTexture.texture();
		} else {
			perlinTexture.update(0.01f, 0.1f, P.sin(angle), P.cos(angle));
			displacementMap = perlinTexture.texture();
		}

		// debug audio texture draw
//		p.image(audioTexture.texture(), 0, 0);

		// set center screen & rotate
		translate(width/2, height/2, 0);
		// rotateX(0.3f * P.sin(percentComplete * P.TWO_PI));
//		rotateX(p.frameCount/20f);
		rotateZ(P.PI);
		rotateY(P.PI);
		rotateY(p.mouseX / 100f);
		rotateX(p.mouseY / 100f);

		
		// set shader properties & set on processing context
		texShader.set("displacementMap", displacementMap);
		p.noStroke();
//		obj.disableStyle();
		p.shader(texShader);  
		p.shape(obj);
		p.resetShader();
	}

}

