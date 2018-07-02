package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.pgraphics.TextureEQGrid;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_SphericalDeform 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected float modelHeight;
	protected PShader displaceVertShader;
	protected PImage displacementMap1;
	protected PImage displacementMap2;
	protected BaseTexture audioTexture;

	protected void overridePropsFile() {
	}

	public void setup() {
		super.setup();	
	}
	
	protected void setupFirstFrame() {
		// build obj PShape and scale to window
		// Note: Without getTesselation(), PShape.setTexture(PImage) is SUPER slow. 
		obj = DemoAssets.objSkullRealistic().getTessellation();
		
		// normalize shape
		modelHeight = p.height * 0.95f;
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToExtent(obj, modelHeight);
		
		// Set UV coords & add texture
		PShapeUtil.addTextureUVSpherical(obj, null);
		
		// load shader
		displaceVertShader = loadShader(
			FileUtil.getFile("haxademic/shaders/vertex/brightness-displace-frag-texture.glsl"), 
			FileUtil.getFile("haxademic/shaders/vertex/brightness-displace-sphere-vert.glsl")
		);
		
		// build 2 displacement maps
		audioTexture = new TextureEQGrid(800, 800);
		displacementMap1 = ImageUtil.imageToGraphics(DemoAssets.textureJupiter());
		obj.setTexture(displacementMap1);
		displacementMap2 = audioTexture.texture();
	}

	public void drawApp() {
		DrawUtil.setBetterLights(p);
		background(0);
		p.translate(p.width/2f, p.height/2f, -width*1.5f);
		p.rotateY(P.map(p.mouseX, 0, p.width, -1f, 1f));
		
		// update displacement texture - this must be set for shader to work
		audioTexture.update();
		PImage displacementMap = (p.frameCount % 200 < 100) ? displacementMap1 : displacementMap2;
		if(p.frameCount % 100 == 0) obj.setTexture(displacementMap);
		
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		displaceVertShader.set("displacementMap", displacementMap);
		displaceVertShader.set("displaceStrength", 0.5f + 0.5f * P.sin(p.frameCount * 0.02f));
		p.noLights();
		p.shader(displaceVertShader);  
		p.shape(obj);
		p.resetShader();
	}
		
}