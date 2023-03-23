package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.textures.pgraphics.TextureEQConcentricCircles;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_MeshDeformAndTextureFilter_SimpleSpherical 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected PImage displacementMap1;
	protected PImage displacementMap2;
	protected BaseTexture audioTexture;

	protected void firstFrame() {
		AudioIn.instance();
		
		// build obj PShape and scale to window
		// Note: Without getTesselation(), PShape.setTexture(PImage) is SUPER slow. 
		obj = DemoAssets.objSkullRealistic().getTessellation();
		
		// normalize shape & set UV coords & texture
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToHeight(obj, p.height * 0.75f);
		PShapeUtil.addTextureUVSpherical(obj, null);
		obj.setTexture(displacementMap1);
		
		// build 2 displacement maps
		audioTexture = new TextureEQConcentricCircles(800, 800);
		displacementMap1 = ImageUtil.imageToGraphics(DemoAssets.textureJupiter());
		displacementMap2 = audioTexture.texture();
	}

	protected void drawApp() {
		background(0);
		PG.setBetterLights(p);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);
		
		// update displacement texture - this must be set for shader to work
		audioTexture.update();
		PImage displacementMap = (p.frameCount % 200 < 100) ? displacementMap1 : displacementMap2;
		DebugView.setTexture("displacementMap", displacementMap);
		
		// deform mesh
		MeshDeformAndTextureFilter.instance().setDisplacementMap(displacementMap);
		MeshDeformAndTextureFilter.instance().setDisplaceAmp(0.5f + 0.5f * P.sin(p.frameCount * 0.02f));
		MeshDeformAndTextureFilter.instance().setSheetMode(false);
		MeshDeformAndTextureFilter.instance().setOnContext(p);
		// set texture using PShape method
		if(p.frameCount % 100 == 1) obj.setTexture(displacementMap);
		
		// draw mesh
		p.noLights();
		p.shape(obj);
		p.resetShader();
	}
		
}