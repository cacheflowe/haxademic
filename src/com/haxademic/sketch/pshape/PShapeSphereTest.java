package com.haxademic.sketch.pshape;

import processing.core.PImage;
import processing.core.PShape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.draw.util.PShapeUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class PShapeSphereTest 
extends PAppletHax {

	protected PShape shape;
	protected PShape shapeTessellated;
	protected PShape shapeIcos;
	protected PImage img;
	protected float _frames = 160;

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "800" );
		_appConfig.setProperty( "height", "800" );
		_appConfig.setProperty( "fills_screen", "false" );
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "45" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+1) );
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
		
		shapeIcos = Icosahedron.createIcosahedron(p, 4, img);
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