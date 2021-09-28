package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.PointsDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_PointsDeformAndTextureFilter_Simple
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// original code from: https://processing.org/tutorials/pshader/
	
	protected PShader pointShader;
	protected PShape shape;
	protected float objExtent;
	protected PImage texture;
	protected PGraphics noiseBuffer;
	protected TextureShader noiseTexture;


	protected void config() {
		Config.setProperty( AppSettings.LOOP_FRAMES, 280 );
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
	}

	protected void firstFrame() {
		// noise texture
		noiseBuffer = p.createGraphics(p.width, p.height, PRenderers.P2D);
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq, 0.0005f);
		DebugView.setTexture("noiseBuffer", noiseBuffer);

		// build points shape
		shape = p.createShape();
		shape.beginShape(PConstants.POINTS);
		shape.noFill();
		float spread = 5f; 
		shape.strokeWeight(spread * 0.75f);
		texture = DemoAssets.smallTexture();
		for (int x = 0; x < texture.width; x++) {
			for (int y = 0; y < texture.height; y++) {
				int pixelColor = ImageUtil.getPixelColor(texture, x, y);
				shape.stroke(pixelColor);
				shape.vertex(x * spread, y * spread);
			}
		}
		shape.endShape();
			
		// get extra values
		shape.disableStyle();	// make sure to disableStyle() if we're re-texturing with a shader
		PShapeUtil.centerShape(shape);
		PShapeUtil.addTextureUVToShape(shape, texture);	// necessary for vertex shader `varying vertTexCoord`
		objExtent = PShapeUtil.getMaxExtent(shape);
	}

	protected void drawApp() {
		// update displacement texture
		noiseTexture.shader().set("zoom", 2.5f + 1.5f * P.sin(FrameLoop.progressRads()));
		noiseTexture.shader().set("rotation", FrameLoop.progressRads());
		noiseBuffer.filter(noiseTexture.shader());
		// blur texture for smooothness
		BlurProcessingFilter.instance(p).setBlurSize(5);
		BlurProcessingFilter.instance(p).applyTo(noiseBuffer);
		
		// set context
		p.background(0);
		PG.setDrawCorner(p);	// draw from corner if the PShape is pre-centered
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);
		
		// set up shader
		// apply points deform/texture shader
		PointsDeformAndTextureFilter.instance(p).setColorMap(texture);
		PointsDeformAndTextureFilter.instance(p).setDisplacementMap(noiseBuffer);
		PointsDeformAndTextureFilter.instance(p).setMaxPointSize(6f);
		PointsDeformAndTextureFilter.instance(p).setDisplaceAmp(100 * P.sin(FrameLoop.progressRads()));			// multiplied by obj extent
		PointsDeformAndTextureFilter.instance(p).setModelMaxExtent(objExtent * 2.1f);		// texture mapping UV
		PointsDeformAndTextureFilter.instance(p).setSheetMode(true);
		PointsDeformAndTextureFilter.instance(p).setColorPointSizeMode(false);		// if color point size, use original color texture for point size. otherwise use displacement map color for point size
		PointsDeformAndTextureFilter.instance(p).applyTo(p);
		
		// draw shape
		p.stroke(255); // make sure we reset stroke
		p.shape(shape, 0, 0);
		p.resetShader();
	}

}
