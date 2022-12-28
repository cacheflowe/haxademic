package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class Demo_PShapeUtil_svgToTexturedExtrusion 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PShape shapeSolid;
	protected PImage texture;
	protected PVector modelSize;
	protected boolean overrideColor = false;

	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
		// build shape and assign texture
//		shape = DemoAssets.objHumanoid();
		shape = p.loadShape(FileUtil.getPath("svg/eight.svg"));
		texture = ImageUtil.shapeToGraphicsJittered(shape, 20);
		
		
		
		// TODO: add jitter to the texture to avoid transparent pixels
		DebugView.setTexture("texture", texture);
		
		shapeSolid = p.loadShape(FileUtil.getPath("svg/eight-solid.svg"));
		shapeSolid = shapeSolid.getTessellation();
		PShapeUtil.repairMissingSVGVertex(shapeSolid);
		PShapeUtil.centerShape(shapeSolid);
		shapeSolid = PShapeUtil.createExtrudedShape(shapeSolid, 10);
		float targetH = P.round(p.height * 0.4f);
		P.out("targetH", targetH);
		PShapeUtil.scaleShapeToHeight(shapeSolid, targetH);
		PShapeUtil.centerShape(shapeSolid);
		
		// check size
		modelSize = new PVector(PShapeUtil.getWidth(shapeSolid), PShapeUtil.getHeight(shapeSolid), PShapeUtil.getDepth(shapeSolid));
		DebugView.setValue("shape.width", modelSize.x);
		DebugView.setValue("shape.height", modelSize.y);
		DebugView.setValue("shape.depth", modelSize.z);

		float maxExtent = PShapeUtil.getMaxExtent(shapeSolid);
//		PShapeUtil.addTextureUVToShape(shapeSolid, texture);
//		PShapeUtil.addTextureUVSpherical(shapeSolid, texture);
		PShapeUtil.addTextureUVExactWidthHeight(shapeSolid, texture, modelSize.x, modelSize.y);
		P.out(PShapeUtil.getMaxExtent(shapeSolid));
		
		shapeSolid.setTexture(texture);
		
		// normalize shape (scaling centers)
		P.out("vertexCount", PShapeUtil.vertexCount(shapeSolid));
	}
		
	protected void drawApp() {
		// clear the screen
		background(127);
		p.noStroke();
		PG.setCenterScreen(p);
		PG.setDrawCorner(p.g);
		PG.setBetterLights(p.g);
		PG.basicCameraFromMouse(p.g, 0.9f);

		// draw shape
		p.shape(shapeSolid);
		
		// debug shape
		PShapeUtil.debugVertices(pg, shapeSolid);
	}
	
}