package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.camera.CameraUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_SheetDeformVariableTexSize 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PGraphics perlinBuffer;
	protected PGraphics materialBuffer;
	protected PShader perlinShader;
	protected PShader displacementShader;

	protected void firstFrame() {
		// create noise buffer
		perlinBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		materialBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		OpenGLUtil.setTextureRepeat(perlinBuffer);
		perlinShader = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/noise-simplex-2d-iq.glsl"));
		
		// build sheet mesh
		shape = Shapes.createSheet(150, 20000, 20000);
		shape.setTexture(perlinBuffer);
		DebugView.setValue("shape.getVertexCount();", shape.getVertexCount());
		
		// load shader
		displacementShader = loadShader(
			FileUtil.getPath("haxademic/shaders/vertex/brightness-displace-frag-other-texture.glsl"), 
			FileUtil.getPath("haxademic/shaders/vertex/brightness-displace-sheet-vert.glsl")
		);
	}

	protected void drawApp() {
		background(0);
		CameraUtil.setCameraDistance(p.g, 100, 20000);
		
		float scrollOffset = (float) p.frameCount * 0.007f;
		float perlinZoom = 10f;
		DebugView.setValue("scrollOffset", scrollOffset);;
		
		// update perlin texture
		perlinShader.set("offset", 0f, scrollOffset);
		perlinShader.set("zoom", perlinZoom);
		perlinBuffer.filter(perlinShader);
		DebugView.setTexture("perlinBuffer", perlinBuffer);
		
		// update material texture
		ImageUtil.cropFillCopyImage(DemoAssets.textureNebula(), materialBuffer, true);
		
		// adjust perlin
		ContrastFilter.instance().setContrast(1.5f);
		ContrastFilter.instance().applyTo(perlinBuffer);
		
		// rotate
		p.translate(p.width/2f, p.height * 1.75f, P.map(p.mouseX, 0, p.width, 0, -10000));
		p.rotateX(P.HALF_PI - 0.4f);
//		p.rotateX(1f + P.map(p.mouseY, 0, p.height, -0.3f, 0.3f));

		// draw mesh with texture or without
		displacementShader.set("displacementMap", perlinBuffer);
		displacementShader.set("textureMap", materialBuffer);
		displacementShader.set("textureOffset", 0f, scrollOffset / perlinZoom);
		displacementShader.set("displaceStrength", 500f);
		p.shader(displacementShader);  
//		shape.disableStyle();
//		shape.fill(0);
//		shape.stroke(127, 255, 127);
//		p.fill(0);
//		p.stroke(127, 255, 127);
		p.shape(shape);
		p.resetShader();
	}
		
}