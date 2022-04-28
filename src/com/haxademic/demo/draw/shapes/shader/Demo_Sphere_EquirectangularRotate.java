package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class Demo_Sphere_EquirectangularRotate
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage textureOrig;
	protected PGraphics texture;
	protected PShape sphere;
	protected PShaderHotSwap equirectangularRotateShader;
	

	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.LOOP_FRAMES, 900);
	}
	
	protected void firstFrame() {
		// load textures
		textureOrig = P.getImage("images/textures/space/moon-layers/color.jpg");
		texture = ImageUtil.imageToGraphics(textureOrig);
		DebugView.setTexture("texture", texture);
		
		// load shader
		equirectangularRotateShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/equirectangular-rotate.glsl"));
		
		// create sphere
		p.sphereDetail(40);
		sphere = createShape(P.SPHERE, p.height * 0.35f);
//		sphere.setStroke(false);
		sphere.setSpecular(color(125));
		sphere.setShininess(10);
		sphere.setTexture(texture);
	}
	
	protected void drawApp() {
		// set context
		background(0);
		PG.setDrawCorner(p);
		PG.setCenterScreen(p);
		
		// update shader
		equirectangularRotateShader.shader().set("rotXAxis", FrameLoop.count(0.003f));
		equirectangularRotateShader.update();
		
		// copy texture again
		texture.beginDraw();
		ImageUtil.copyImage(textureOrig, texture);
		PG.drawGrid(texture, 0x00000000, 0xffffffff, 60, 40, 1, false);
		texture.endDraw();
		
		// run rotation shader
		texture.filter(equirectangularRotateShader.shader());
		
		// draw from center
		p.pushMatrix();
		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI));
		p.shape(sphere);
		p.popMatrix();	  
	}

}
