package com.haxademic.core.draw.textures.pgraphics;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFilter;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.PointsDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PShape;

public class TextureSvg3dExtruded 
extends BaseTexture {
	
	protected float shapeHeight;
	protected ImageGradient gradient;
	protected PShape logoSvg; 
	protected PShape logo3d;
	protected PShape logoPoints;
	protected float logoPointsExtent;
	protected PGraphics meshTextures[];
	protected PGraphics curMeshTexture;
	protected EasingFloat colorLogoProgress = new EasingFloat(0, 0.1f);
	protected EasingFloat logoScale = new EasingFloat(1, 0.1f);
	protected EasingFloat logoRotY = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotX = new EasingFloat(0, 0.1f);
	protected EasingFloat logoRotZ = new EasingFloat(0, 0.1f);
	
	// textures to apply
	protected TextureShader noiseTexture;
	protected BaseTexture audioTexture;
	
	protected EasingFloat rotateZoom = new EasingFloat(1f, 0.1f);
	protected EasingFloat rotateRot = new EasingFloat(0f, 0.1f);
	
	protected float thickness = 40;
	
	// draw modes & random getters
	enum DrawMode {
		Color,
		Textured,
//		TextureRepeat,
//		AudioTriangles,
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

		// build color
//		gradient = new ImageGradient(ImageGradient.BLACK_HOLE());
		gradient = new ImageGradient(ImageGradient.PASTELS());
//		gradient = new ImageGradient(P.p.loadImage(FileUtil.getFile("images/textures/palette-sendgrid.png")));
		
		// create logo
		buildLogo();
	}
	
	protected void buildLogo() {
		shapeHeight = _texture.height * 0.15f;
		
		logoSvg = DemoAssets.shapeX().getTessellation();
		PShapeUtil.repairMissingSVGVertex(logoSvg);
		
		PShapeUtil.centerShape(logoSvg);
		PShapeUtil.scaleShapeToHeight(logoSvg, shapeHeight);
		
		meshTextures = new PGraphics[] {
			buildTextureFromSvg(DemoAssets.shapeX()),
			buildTextureFromSvg(DemoAssets.shapeFractal()),
//			buildTextureFromSvg(P.p.loadShape( FileUtil.getFile("images/_sketch/sendgrid/svg/sendgrid-logo-02.svg"))),
//			buildTextureFromSvg(P.p.loadShape( FileUtil.getFile("images/_sketch/sendgrid/svg/sendgrid-logo-03.svg"))),
//			buildTextureFromSvg(P.p.loadShape( FileUtil.getFile("images/_sketch/sendgrid/svg/sendgrid-logo-04.svg"))),
//			buildTextureFromSvg(P.p.loadShape( FileUtil.getFile("images/_sketch/sendgrid/svg/sendgrid-logo-05.svg"))),
		};
		curMeshTexture = meshTextures[0];
		
		// build audio texture to size of logo texture
//		audioTexture = new TextureEQGrid(logoTexture.width, logoTexture.height);
		audioTexture = new TextureEQBandDistribute(curMeshTexture.width, curMeshTexture.height);
//		audioTexture = new TextureEQConcentricCircles(curMeshTexture.width, curMeshTexture.height);
//		P.p.debugView.setTexture(audioTexture.texture());

		
		// create extrusion
		// add UV coordinates to OBJ based on model extents
		logo3d = PShapeUtil.createExtrudedShape( logoSvg, thickness ).getTessellation();
//		logo3d.disableStyle();
		PShapeUtil.centerShape(logo3d);
		float logoW = PShapeUtil.getWidth(logo3d);
		float logoH = PShapeUtil.getHeight(logo3d);
//		PShapeUtil.addTextureUVExactWidthHeight(logo3d, DemoAssets.justin(), logoW, logoH);//audioTexture.texture());
		PShapeUtil.addTextureUVExactWidthHeight(logo3d, meshTextures[0], logoW, logoH);//audioTexture.texture());
//		logo3d.setTexture(curMeshTexture);
		
		// create points version
		logoPoints = PShapeUtil.svgToUniformPointsShape(FileUtil.getFile(DemoAssets.shapeXPath), 4);
//		logoPoints = PShapeUtil.createExtrudedShape( logoSvg, thickness );
		PShapeUtil.scaleShapeToHeight(logoPoints, shapeHeight);
		PShapeUtil.centerShape(logoPoints);
		logoPoints.disableStyle();
		logoPointsExtent = PShapeUtil.getMaxExtent(logoPoints);
//		PShapeUtil.addTextureUVExactWidthHeight(logoPoints, curMeshTexture, logoW, logoH);//audioTexture.texture());
	}
	
	protected PGraphics buildTextureFromSvg(PShape svg) {
		// create logo texture with a slight under-blur
		float scaleToTex = MathUtil.scaleToTarget(svg.height, shapeHeight);
		PGraphics shapeTextureTemp = ImageUtil.shapeToGraphics(svg, scaleToTex);
		PGraphics shapeTexture = ImageUtil.shapeToGraphics(svg, scaleToTex);
		BlurProcessingFilter.instance(P.p).setBlurSize(4);
		BlurProcessingFilter.instance(P.p).setSigma(3);
		BlurProcessingFilter.instance(P.p).applyTo(shapeTexture);
		shapeTexture.beginDraw();
		shapeTexture.background(255);
		shapeTexture.image(shapeTextureTemp, 0, 0);
		shapeTexture.image(shapeTextureTemp, 0, 1);	// repeat down 1px to cover boxes
		shapeTexture.image(shapeTextureTemp, 1, 0);
		shapeTexture.image(shapeTextureTemp, 1, 1);	// repeat down 1px to cover boxes
		shapeTexture.endDraw();
//		P.p.debugView.setTexture(curMeshTexture);
		return shapeTexture;
	}

	public void newLineMode() {
		drawMode = randomDrawMode();
		curMeshTexture = meshTextures[MathUtil.randRange(0, meshTextures.length - 1)];
		audioTexture.newLineMode();
	}
	
	public void setColor(int color) {
		int newColor = gradient.getColorAtProgress(MathUtil.randRangeDecimal(0, 1));
		super.setColor(newColor);
		audioTexture.setColor(newColor);
		
//		if(MathUtil.randBooleanWeighted(P.p, 0.3f)) {
			rotateZoom.setTarget(MathUtil.randRange(1f, 5f));
			if(rotateZoom.target() == 2) rotateZoom.setTarget(1);
			if(rotateZoom.target() == 4) rotateZoom.setTarget(3);
			rotateRot.setTarget(MathUtil.randRangeDecimal(-0.1f, 0.1f));
//		}
	}
	
	public void preDraw() {
//		drawMode = DrawMode.AudioTriangles;
//		case TextureRepeat:
		switch (drawMode) {
		case Color:
			break;
		case Textured:
//		case AudioTriangles:
			audioTexture.update();
			// add slight bit of original texture
			BlendTowardsTexture.instance(P.p).setBlendLerp(0.5f);
			BlendTowardsTexture.instance(P.p).setSourceTexture(curMeshTexture);
			BlendTowardsTexture.instance(P.p).applyTo(audioTexture.texture());
			break;
		case Points:
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
//		P.p.debugView.setTexture(audioTexture.texture());
	}

	public void updateDraw() {
		// reset context
		if(drawMode == DrawMode.Displacement2d) {
			_texture.background(0);
		} else {
			_texture.clear();
		}
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
		
		switch (drawMode) {
		case Color:
//			_texture.lights();
			_texture.fill(255);
			DrawUtil.setBetterLights(_texture);
//			DrawUtil.setBasicLights(_texture);
//			PShapeUtil.drawTriangles(_texture, logo3d, DemoAssets.justin(), logoScale.value());
//			_texture.shape(logo3d);
			PShapeUtil.drawTriangles(_texture, logo3d, curMeshTexture, logoScale.value());
			break;
		case Textured:
			_texture.fill(255);
//			_texture.lights();
			DrawUtil.setBetterLights(_texture);
//			DrawUtil.setBasicLights(_texture);
			PShapeUtil.drawTriangles(_texture, logo3d, audioTexture.texture(), logoScale.value());
			break;
//		case AudioTriangles:
//			DrawUtil.setBasicLights(_texture);
//			_texture.fill(_colorEase.colorInt(0.2f));
//			PShapeUtil.drawTriangles(_texture, logo3d, curMeshTexture, logoScale.value());
//			PShapeUtil.drawTrianglesAudio(_texture, logo3d, logoScale.value() + 0.01f, _colorEase.colorInt());
//			break;
		case Displacement2d:
//			DrawUtil.setBetterLights(_texture);
			DrawUtil.setBasicLights(_texture);
//			_texture.shape(logo3d, 0, 0);
			_texture.fill(_colorEase.colorInt());
			_texture.shape(logo3d);
//			PShapeUtil.drawTriangles(_texture, logo3d, curMeshTexture, logoScale.value());
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
			PointsDeformAndTextureFilter.instance(P.p).setColorMap(meshTextures[0]);
			PointsDeformAndTextureFilter.instance(P.p).setDisplacementMap(audioTexture.texture());
			PointsDeformAndTextureFilter.instance(P.p).setMaxPointSize(1.f);
			PointsDeformAndTextureFilter.instance(P.p).setModelMaxExtent(logoPointsExtent * 2.01f);	// texture mapping UV
			PointsDeformAndTextureFilter.instance(P.p).setDisplaceAmp(50f);			// multiplied by obj extent
			PointsDeformAndTextureFilter.instance(P.p).setSheetMode(true);
//			PointsDeformAndTextureFilter.instance(P.p).setSheetMode(false);
			PointsDeformAndTextureFilter.instance(P.p).setColorPointSizeMode(false);		// if color point size, use original color texture for point size. otherwise use displacement map color for point size
			_texture.noLights();
//			logoPoints.setTexture(audioTexture.texture());
//			_texture.shape(obj);

			
			float numLayers = 6;
			float thickSpacing = -0.2f; // thickness / (numLayers - 1);
			float spacing = thickness / numLayers / 4f;
			for (float i = 0; i < 1; i++) {
				_texture.pushMatrix();
				_texture.strokeWeight(2f - thickSpacing * i);
				_texture.translate(0, 0, numLayers/2f * spacing - spacing * i);
				PointsDeformAndTextureFilter.instance(P.p).applyTo(_texture);
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
//		case TextureRepeat:
//			float thicknesss = 40 + 20f * P.sin(P.p.frameCount * 0.05f);
//			DrawUtil.setDrawCenter(_texture);
//			_texture.noStroke();
//			_texture.fill(255);
//			float numLayersg = 6;
//			float spacingg = (thicknesss / numLayersg) * (1f + P.p.audioFreq(20));
//			for (float i = numLayersg - 1; i >= 0; i--) {
//				float loopProgress = i * 1f / numLayersg;
//				_texture.pushMatrix();
//				_texture.translate(0, 0, numLayersg/2f * spacingg - spacingg * i);
//				DrawUtil.setPImageAlpha(_texture, loopProgress);
//				_texture.image(logoTexture, 0, 0, logoTexture.width * (1f + i * 0.05f), logoTexture.height * (1f + i * 0.05f));
//				_texture.popMatrix();
//			}
//			DrawUtil.resetPImageAlpha(_texture);
//			break;
		default:
			break;
		}

		// pop context
		_texture.noLights();
		_texture.popMatrix();

		// post-processing
		rotateZoom.update(true);
		rotateRot.update(true);
		RotateFilter.instance(P.p).setZoom(rotateZoom.value());
		RotateFilter.instance(P.p).setRotation(rotateRot.value());
		RotateFilter.instance(P.p).applyTo(_texture);
		
		// black to transparent
		if(drawMode == DrawMode.Displacement2d) {
			applyChromaBlackKnockout(_texture);
		} else {
		}
	}
	
}
