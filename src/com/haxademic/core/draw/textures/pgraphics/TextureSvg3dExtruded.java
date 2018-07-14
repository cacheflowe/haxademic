package com.haxademic.core.draw.textures.pgraphics;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.ColorizeFilter;
import com.haxademic.core.draw.filters.shaders.DisplacementMapFilter;
import com.haxademic.core.draw.filters.shaders.RotateFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.PointsDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class TextureSvg3dExtruded 
extends BaseTexture {
	
	protected ImageGradient gradient;
	protected PShape logoSvg; 
	protected PShape logo3d;
	protected PShape logoPoints;
	protected float logoPointsExtent;
	protected PGraphics logoTexture;
	protected EasingFloat colorLogoProgress = new EasingFloat(0, 0.1f);
	protected EasingFloat logoScale = new EasingFloat(1, 0.1f);
	protected EasingFloat logoRotY = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotX = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotZ = new EasingFloat(0, 0.1f);
	
	// textures to apply
	protected TextureShader noiseTexture;
	protected BaseTexture audioTexture;
	
	protected float thickness = 40;
	
	// draw modes & random getters
	enum DrawMode {
		Color,
		Textured,
		TextureRepeat,
		AudioTriangles,
		Displacement2d,
		Points,
	}
	protected DrawMode drawMode;
	
	private static final List<DrawMode> VALUES = Collections.unmodifiableList(Arrays.asList(DrawMode.values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();
	public static DrawMode randomDrawMode()  {
		return VALUES.get(RANDOM.nextInt(SIZE));
	}


	public TextureSvg3dExtruded( int width, int height ) {
		super();
		buildGraphics( width, height );

		// init draw mode
		drawMode = randomDrawMode();
		
		// build textures
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq, 0.0005f);
//		audioTexture = new TextureEQGrid(200, 200);
		audioTexture = new TextureEQBandDistribute(200, 200);

		// build color
//		gradient = new ImageGradient(ImageGradient.BLACK_HOLE());
		gradient = new ImageGradient(ImageGradient.PASTELS());
//		gradient = new ImageGradient(P.p.loadImage(FileUtil.getFile("images/textures/palette-sendgrid.png")));
		
		// create logo
		buildLogo();
	}
	
	protected void buildLogo() {
		logoSvg = P.p.loadShape( FileUtil.getFile("svg/sendgrid.svg")).getTessellation();
		PShapeUtil.repairMissingSVGVertex(logoSvg);
		
		PShapeUtil.centerShape(logoSvg);
		PShapeUtil.scaleShapeToHeight(logoSvg, _texture.height * 0.15f);
		
		PShape logoForTex = P.p.loadShape( FileUtil.getFile("svg/sendgrid.svg"));
		float scaleToTex = MathUtil.scaleToTarget(logoForTex.height, _texture.height * 0.15f);
		logoTexture = ImageUtil.shapeToGraphics(logoForTex, scaleToTex);
		
		// create extrusion
		// add UV coordinates to OBJ based on model extents
		logo3d = PShapeUtil.createExtrudedShape( logoSvg, thickness );
		logo3d.disableStyle();
		PShapeUtil.addTextureUVToShape(logo3d, audioTexture.texture());
		
		// create points version
		logoPoints = PShapeUtil.svgToUniformPointsShape(FileUtil.getFile("svg/sendgrid.svg"), 13);
//		logoPoints = PShapeUtil.createExtrudedShape( logoSvg, thickness );
		PShapeUtil.scaleShapeToHeight(logoPoints, _texture.height * 0.15f);
		logoPoints.disableStyle();
		logoPointsExtent = PShapeUtil.getMaxExtent(logoPoints);
		PShapeUtil.addTextureUVSpherical(logoPoints, audioTexture.texture());
	}

	public void newLineMode() {
		drawMode = randomDrawMode();
		audioTexture.newLineMode();
	}
	
	public void setColor(int color) {
		int newColor = gradient.getColorAtProgress(MathUtil.randRangeDecimal(0, 1));
		super.setColor(newColor);
		audioTexture.setColor(newColor);
	}
	
	public void preDraw() {
//		drawMode = DrawMode.Points;
		switch (drawMode) {
		case Color:
			break;
		case Textured:
		case AudioTriangles:
		case Points:
			audioTexture.update();
			break;
		case TextureRepeat:
			break;
		case Displacement2d:
			noiseTexture.shader().set("offset", 0f, P.p.frameCount * 0.025f);
			audioTexture.texture().filter(noiseTexture.shader());
			ColorizeFilter.instance(P.p).setTargetR(_colorEase.rNorm());
			ColorizeFilter.instance(P.p).setTargetG(_colorEase.gNorm());
			ColorizeFilter.instance(P.p).setTargetB(_colorEase.bNorm());
			ColorizeFilter.instance(P.p).applyTo(audioTexture.texture());
			break;
		default:
			break;
		}
		P.p.debugView.setTexture(audioTexture.texture());
	}

	public void updateDraw() {
		// reset context
		_texture.background(0);
		_texture.noStroke();
//		_texture.stroke(255);
		
		// update colors & pump scale on beat
		if(P.p.audioData.isBeat()) {
			colorLogoProgress.setTarget(MathUtil.randRangeDecimal(0, 1));
			logoScale.setCurrent(1.2f);
			logoScale.setTarget(1f);
			logoRotY.setTarget(MathUtil.randRangeDecimal(-0.3f, 0.3f));
			logoRotX.setTarget(MathUtil.randRangeDecimal(-0.3f, 0.3f));
			logoRotZ.setTarget(MathUtil.randRangeDecimal(-0.3f, 0.3f));
		}
		colorLogoProgress.update(true);
		_texture.fill(_colorEase.colorInt());
		
		// update lerping values
		logoScale.update(true);
		logoRotY.update(true);
		logoRotX.update(true);
		logoRotZ.update(true);
		
		// set context
		_texture.pushMatrix();
		DrawUtil.setDrawCorner(_texture);
		DrawUtil.setCenterScreen(_texture);
		_texture.rotateX(logoRotX.value());
		_texture.rotateY(logoRotY.value());
		_texture.rotateZ(logoRotZ.value());
		
		P.p.debugView.setValue("drawMode", drawMode.toString());
		switch (drawMode) {
		case Color:
			_texture.lights();
//			DrawUtil.setBetterLights(_texture);
//			DrawUtil.setBasicLights(_texture);
			PShapeUtil.drawTriangles(_texture, logo3d, null, logoScale.value());
			break;
		case Textured:
			_texture.fill(255);
			_texture.lights();
//			DrawUtil.setBetterLights(_texture);
//			DrawUtil.setBasicLights(_texture);
			PShapeUtil.drawTriangles(_texture, logo3d, audioTexture.texture(), logoScale.value());
			break;
		case AudioTriangles:
			DrawUtil.setBasicLights(_texture);
			_texture.fill(_colorEase.colorInt(0.4f));
			PShapeUtil.drawTriangles(_texture, logo3d, null, logoScale.value());
			PShapeUtil.drawTrianglesAudio(_texture, logo3d, logoScale.value() + 0.01f, _colorEase.colorInt());
			break;
		case Displacement2d:
			_texture.lights();
//			_texture.shape(logo3d, 0, 0);
			_texture.fill(_colorEase.colorInt());
			PShapeUtil.drawTriangles(_texture, logo3d, null, logoScale.value());
			// post-process wobble
			DisplacementMapFilter.instance(P.p).setMap(audioTexture.texture());
			DisplacementMapFilter.instance(P.p).setAmp(0.15f * P.p.audioFreq(100));
			DisplacementMapFilter.instance(P.p).setMode(3);
			DisplacementMapFilter.instance(P.p).applyTo(_texture);
			break;
		case Points:
//			P.println("Switch displacement to vertex shader");
			_texture.stroke(_colorEase.colorInt());
			_texture.fill(_colorEase.colorInt());
			
			// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
			// apply points deform/texture shader
			PointsDeformAndTextureFilter.instance(P.p).setColorMap(audioTexture.texture());
			PointsDeformAndTextureFilter.instance(P.p).setDisplacementMap(audioTexture.texture());
			PointsDeformAndTextureFilter.instance(P.p).setMaxPointSize(6f);
			PointsDeformAndTextureFilter.instance(P.p).setDisplaceAmp(2f);			// multiplied by obj extent
			PointsDeformAndTextureFilter.instance(P.p).setModelMaxExtent(logoPointsExtent * 2.1f);
			PointsDeformAndTextureFilter.instance(P.p).setSheetMode(true);
			PointsDeformAndTextureFilter.instance(P.p).setColorPointSizeMode(false);		// if color point size, use original color texture for point size. otherwise use displacement map color for point size
			_texture.noLights();
			logoPoints.setTexture(audioTexture.texture());
//			_texture.shape(obj);

			
			float numLayers = 6;
			float thickSpacing = -0.2f; // thickness / (numLayers - 1);
			float spacing = thickness / numLayers;
			for (float i = 0; i < numLayers; i++) {
				_texture.pushMatrix();
				_texture.strokeWeight(3f - thickSpacing * i);
				_texture.translate(0, 0, numLayers/2f * spacing - spacing * i);
				PointsDeformAndTextureFilter.instance(P.p).applyVertexShader(_texture);
				_texture.shape(logoPoints);
				_texture.resetShader();
				_texture.popMatrix();
			}
			
			// post-process wobble
//			DisplacementMapFilter.instance(P.p).setMap(audioTexture.texture());
//			DisplacementMapFilter.instance(P.p).setAmp(0.15f * P.p.audioFreq(100));
//			DisplacementMapFilter.instance(P.p).setMode(3);
//			DisplacementMapFilter.instance(P.p).applyTo(_texture);

			break;
		case TextureRepeat:
			float thicknesss = 40 + 20f * P.sin(P.p.frameCount * 0.05f);
			DrawUtil.setDrawCenter(_texture);
			_texture.noStroke();
			_texture.fill(255);
			float numLayersg = 6;
			float spacingg = (thicknesss / numLayersg) * (1f + P.p.audioFreq(20));
			for (float i = numLayersg - 1; i >= 0; i--) {
				float loopProgress = i * 1f / numLayersg;
				_texture.pushMatrix();
				_texture.translate(0, 0, numLayersg/2f * spacingg - spacingg * i);
				DrawUtil.setPImageAlpha(_texture, loopProgress);
				_texture.image(logoTexture, 0, 0, logoTexture.width * (1f + i * 0.05f), logoTexture.height * (1f + i * 0.05f));
				_texture.popMatrix();
			}
			DrawUtil.resetPImageAlpha(_texture);
			break;
		default:
			break;
		}

		// pop context
		_texture.noLights();
		_texture.popMatrix();

		// post-processing
		RotateFilter.instance(P.p).setZoom(P.p.mousePercentY() * 3f + 0.5f);
		RotateFilter.instance(P.p).setRotation(P.p.mousePercentX() * 2f * P.TWO_PI);
		RotateFilter.instance(P.p).applyTo(_texture);
		
		// black to transparent
		applyChromaBlackKnockout(_texture);
	}
	
}
