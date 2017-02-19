package com.haxademic.sketch.pshape;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.filters.shaders.KaleidoFilter;
import com.haxademic.core.draw.filters.shaders.RadialRipplesFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.draw.util.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class PShapeIcosahedronTextured 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shapeIcos;
	protected PImage texture;
	protected float _frames = 170;
	protected PShader texShader;
	protected PShader bgShader;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(_frames) );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, false );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, 45 );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, Math.round(_frames) );
	}

	public void setup() {
		super.setup();	

		// load texture
		texture = p.loadImage(FileUtil.getFile("images/jupiter-360.jpg"));
		
		// create icosahedron
		shapeIcos = Icosahedron.createIcosahedron(p, 7, texture);
		PShapeUtil.scaleSvgToExtent(shapeIcos, p.height/4f);
		
		// sphere deformation shader. uses the sphere's texture as the displacement map
		texShader = loadShader(
			FileUtil.getFile("shaders/vertex/brightness-displace-frag-texture.glsl"), 
			FileUtil.getFile("shaders/vertex/brightness-displace-sphere-vert.glsl")
		);
		texShader.set("displacementMap", texture);
		texShader.set("displaceStrength", 0.3f);
		
		// start field generative texture
		bgShader = P.p.loadShader( FileUtil.getFile("shaders/textures/stars-scroll.glsl")); 
		bgShader.set("time", 0 );
	}

	public void drawApp() {
		background(255);
		
		// wrap-around textures
		OpenGLUtil.setTextureRepeat(p.g);
		
		// loop progress
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		// star field w/special effects
		bgShader.set("time", 900 + P.sin( P.TWO_PI * percentComplete) );
		p.filter(bgShader);
//		MirrorFilter.instance(p).applyTo(p);
		KaleidoFilter.instance(p).applyTo(p);
		RadialRipplesFilter.instance(p).setTime(p.frameCount/50f);
		RadialRipplesFilter.instance(p).setAmplitude(0.35f + 0.35f * P.sin(-P.PI/2f + P.TWO_PI * percentComplete));
		RadialRipplesFilter.instance(p).applyTo(p);
		
		// draw icosahedron
		p.pushMatrix();
		p.translate(p.width/2f, p.height/2f);
		p.rotateY(percentComplete * P.TWO_PI);
		p.rotateZ(0.05f + 0.05f * P.sin(-P.PI/2f + P.TWO_PI * percentComplete));
		
		// apply vertex shader & draw icosahedron
		texShader.set("displaceStrength", 0.2f + 0.2f * P.sin(-P.PI/2f + P.TWO_PI * percentComplete));
		p.shader(texShader);  
		p.shape(shapeIcos);
		p.resetShader();
		p.popMatrix();
		
		// a little light & vignetter effect
		DrawUtil.setBetterLights(p);
		VignetteFilter.instance(p).applyTo(p);
	}
		
}