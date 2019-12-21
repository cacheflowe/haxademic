package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pshader.KaleidoFilter;
import com.haxademic.core.draw.filters.pshader.RadialRipplesFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class JupitersNearMiss 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shapeIcos;
	protected PImage texture;
	protected float _frames = 170;
	protected PShader bgShader;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.FILLS_SCREEN, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(_frames) );
		Config.setProperty( AppSettings.RENDERING_GIF, false );
		Config.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, 45 );
		Config.setProperty( AppSettings.RENDERING_GIF_QUALITY, 1 );
		Config.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, Math.round(_frames) );
	}

	public void firstFrame() {
	

		// load texture
		texture = p.loadImage(FileUtil.getFile("haxademic/images/spherical/jupiter.jpg"));
		
		// create icosahedron
		shapeIcos = Icosahedron.createIcosahedron(p.g, 7, texture);
		PShapeUtil.scaleShapeToExtent(shapeIcos, p.height/4f);
		
		// start field generative texture
		bgShader = P.p.loadShader( FileUtil.getFile("haxademic/shaders/textures/stars-scroll.glsl")); 
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
		// deform mesh
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(texture);
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(0.2f + 0.2f * P.sin(-P.PI/2f + P.TWO_PI * percentComplete));
		MeshDeformAndTextureFilter.instance(p).setSheetMode(false);
		MeshDeformAndTextureFilter.instance(p).applyTo(p);

		// draw mesh
		p.shape(shapeIcos);
		p.resetShader();
		p.popMatrix();
		
		// a little light & vignetter effect
		VignetteFilter.instance(p).applyTo(p);
	}
		
}