package com.haxademic.app.haxmapper.textures;

import java.util.ArrayList;

import com.haxademic.app.haxvisual.HaxVisualTwo;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BlurBasicFilter;
import com.haxademic.core.draw.filters.shaders.BlurProcessingFilter;
import com.haxademic.core.draw.filters.shaders.VignetteAltFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat3d;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class TextureSphereAudioTextures
extends BaseTexture {

	PGraphics sphereTexture;
	
	protected EasingFloat3d _rotation = new EasingFloat3d( 0, 0, 0, 7f );
	protected final float _ninteyDeg = P.PI / 6f;
	
	protected BaseTexture[] audioTextures;
	protected int audioTexIndex = 0;
	protected ArrayList<BaseTexture> _curTexturePool;
	protected int texturePoolIndex = 0;
	protected boolean audioMode = false;
	
	protected PShape shape;
	protected PShape shapeTessellated;
	protected PShape shapeIcos;
	
	PShader texShader;
	PShader vignette;



	public TextureSphereAudioTextures( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		sphereTexture = P.p.createGraphics(1024, 512, P.P2D);
		sphereTexture.noSmooth();
//		buildAudioTextures();
//		pickRandomTexture();
		createNewSphere();
		
		vignette = P.p.loadShader(FileUtil.getFile("shaders/filters/vignette-inverse.glsl"));
	}
	
	public void setCurTexturePool(ArrayList<BaseTexture> curTexturePool) {
		_curTexturePool = curTexturePool;
		pickRandomTexture();
	}
	
	protected void createNewSphere() {
		P.p.sphereDetail(40);
		shape = P.p.createShape(P.SPHERE, _texture.height/3f);
		shapeTessellated = shape.getTessellation();
		
		float extent = PShapeUtil.getMaxExtent(shape);
		
//		shape.setTexture(sphereTexture);
//		shapeTessellated.setTexture(sphereTexture);
		
		shapeIcos = Icosahedron.createIcosahedron(_texture, 7, sphereTexture);
		PShapeUtil.scaleShapeToExtent(shapeIcos, extent);
		
//		objSolid = new PShapeSolid(shapeIcos);

//		PShapeUtil.addUVsToPShape(shape, extent);
//		PShapeUtil.addUVsToPShape(shapeTessellated, extent);
		
		texShader = P.p.loadShader(
				FileUtil.getFile("shaders/vertex/brightness-displace-frag-texture.glsl"), 
				FileUtil.getFile("shaders/vertex/brightness-displace-sphere-vert.glsl")
				);
		texShader.set("displacementMap", _texture);

	}
	
	protected void pickRandomTexture() {
		texturePoolIndex = MathUtil.randRange(0, _curTexturePool.size() - 1);
	}
	
	public void newRotation() {
		_rotation.setTargetX( _ninteyDeg * MathUtil.randRangeDecimal( -1, 1 ) );
		_rotation.setTargetY( _ninteyDeg * MathUtil.randRangeDecimal( -1, 1 ) );
		_rotation.setTargetZ( _ninteyDeg * MathUtil.randRangeDecimal( -1, 1 ) );
	}
	
	public void newMode() {
		pickRandomTexture();
	}
	
	public void preDraw() {
		sphereTexture.beginDraw();
		DrawUtil.fadeToBlack(sphereTexture, 10);
//		sphereTexture.background(0);
		sphereTexture.endDraw();

		// draw texture to sphere buffer
		ImageUtil.cropFillCopyImage(_curTexturePool.get(texturePoolIndex).texture(), sphereTexture, true);
		
		// effects
		sphereTexture.filter(_chroma);
//		HaxVisualTwo.applyFilterToTexture(sphereTexture, 0);
		VignetteAltFilter.instance(P.p).setSpread(0.95f);
		VignetteAltFilter.instance(P.p).setDarkness(3.f);
		VignetteAltFilter.instance(P.p).applyTo(sphereTexture);
//		BlurBasicFilter.instance(P.p).applyTo(sphereTexture);
		BlurProcessingFilter.instance(P.p).setBlurSize(6);
		BlurProcessingFilter.instance(P.p).setSigma(6);
		BlurProcessingFilter.instance(P.p).applyTo(sphereTexture);
		BlurProcessingFilter.instance(P.p).applyTo(sphereTexture);
	}
	
	public void updateDraw() {
		_texture.clear();

		// test show audio texture
		//_texture.image(sphereTexture, 0, 0);

		float frames = 600;
		float percentComplete = ((float)(P.p.frameCount%frames)/frames);

		// icosahedron
		// set position to center
		_texture.pushMatrix();
		_texture.translate(_texture.width/2f, _texture.height/2f);
		
		// shadow
		Gradients.radial(_texture, _texture.height * 2.5f, _texture.height * 2.5f, P.p.color(0,200), P.p.color(1, 0), 50);
		Gradients.radial(_texture, _texture.height * 1.5f, _texture.height * 1.5f, P.p.color(0,200), P.p.color(1, 0), 50);
		
		_rotation.update();
		_texture.rotateY( -P.HALF_PI + _rotation.x() );
		_texture.rotateX( _rotation.y() );
		_texture.rotateZ( _rotation.z() );
//		objSolid.deformWithAudioByNormals();
//		_texture.shape(objSolid.shape());
//		PShapeUtil.drawTrianglesWithTexture(_texture, objSolid.shape(), sphereTexture, 3f); // img

		float scaleOsc = P.p.frameCount * 0.001f;
		
		texShader.set("displacementMap", sphereTexture);
		texShader.set("displaceStrength", 0.75f + 0.5f * P.sin(scaleOsc));

		_texture.shader(texShader);  

//		_texture.scale(0.65f + 0.45f * P.sin(P.PI + scaleOsc));
		_texture.scale(0.65f + 0.2f * P.sin(P.PI + scaleOsc));
		_texture.shape(shapeIcos);
//		_texture.shape(shape);
//		_texture.shape(shapeTessellated);
		_texture.popMatrix();

	}
}
