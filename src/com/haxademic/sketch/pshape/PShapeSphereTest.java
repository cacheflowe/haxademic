package com.haxademic.sketch.pshape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;

public class PShapeSphereTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PShape shapeTessellated;
	protected PShape shapeIcos;
	protected PImage img;
	protected float _frames = 160;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "800" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "800" );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "45" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}

	public void setup() {
		super.setup();	
		p.smooth(OpenGLUtil.SMOOTH_HIGH);

		PImage img = p.loadImage(FileUtil.getFile("images/globe.jpg"));
		
		p.sphereDetail(40);
		shape = p.createShape(P.SPHERE, p.width/10f);
		shapeTessellated = shape.getTessellation();
		
		
		float extent = PShapeUtil.getSvgMaxExtent(shape);
		
		shape.setTexture(img);
		shapeTessellated.setTexture(img);
		
		shapeIcos = Icosahedron.createIcosahedron(p.g, 4, img);
		PShapeUtil.scaleSvgToExtent(shapeIcos, extent);
		
		PShapeUtil.addUVsToPShape(shape, extent);
		PShapeUtil.addUVsToPShape(shapeTessellated, extent);
	}

	public void drawApp() {
		background(0);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);

		// setup lights
		DrawUtil.setBetterLights(p);
		
		// icosahedron
		p.pushMatrix();
		p.translate(p.width/2f, p.height/4f);
		p.rotateY(percentComplete * P.TWO_PI);
		p.shape(shapeIcos);
		p.popMatrix();

		// original
		p.pushMatrix();
		p.translate(p.width/4f, p.height/1.5f);
		p.rotateY(-percentComplete * P.TWO_PI);
		p.shape(shape);
		p.popMatrix();

		// tessellated
		p.pushMatrix();
		p.translate(p.width - p.width/4f, p.height/1.5f);
		p.rotateY(percentComplete * P.TWO_PI);
		p.rotateZ(P.PI);
		p.shape(shapeTessellated);
		p.popMatrix();
	}
		
}