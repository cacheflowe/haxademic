package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.polygons.Triangle3d;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;
import processing.core.PVector;

public class Demo_PShapeUtil_rayIntersectsTriangle_VoxelizeMesh 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PShape[] shapesCached;

	protected int FRAMES = 10 * 60;
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1024 );
		Config.setProperty( AppSettings.HEIGHT, 1024 );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
		// load shape, scale & center
		shape = DemoAssets.objSkullRealistic().getTessellation();
//		shape = PShapeUtil.createBox(100, 100, 100, 0xff00ff00).getTessellation();
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.55f);
		PShapeUtil.centerShape(shape);
		// voxelize!
		shapesCached = new PShape[] {
//				voxelizeMesh(shape, 70, 140*2).getTessellation(),
				voxelizeMesh(shape, 50, 150*2).getTessellation(),
				voxelizeMesh(shape, 38, 152*2).getTessellation(),
				voxelizeMesh(shape, 24, 144*2).getTessellation(),
				voxelizeMesh(shape, 16, 144*2).getTessellation(),
				voxelizeMesh(shape, 10, 150*2).getTessellation(),
//				voxelizeMesh(shape, 5, 150*2).getTessellation(),
				shape,
		};
		
		// scale them all to same bounding size
		for (int i = 0; i < shapesCached.length; i++) {
			PShapeUtil.scaleShapeToHeight(shapesCached[i], p.height * 0.55f);
		}
	}
	
	protected void drawApp() {
		// set context
		p.background(0);
//		PG.setBasicLights(p.g);
		PG.setCenterScreen(p.g);
		PG.setDrawCorner(p.g);
//		PG.basicCameraFromMouse(p.g);
		p.rotateX(-0.15f);
		p.rotateY(-0.66f + FrameLoop.progressRads());
		
		// draw shape
		int shapeIndex = P.round(2.5f + 2.5f * MathUtil.saw(FrameLoop.progressRads() * 2)); // P.round(p.frameCount * 0.1f) % shapesCached.length;
		shapesCached[shapeIndex].disableStyle();
		p.stroke(255);
		p.strokeWeight(2.1f);
		p.fill(0);
		p.shape(shapesCached[shapeIndex], 0, 0);
		
		// post process
		BloomFilter.instance().setStrength(0.5f);
		BloomFilter.instance().setBlurIterations(12);
		BloomFilter.instance().setBlendMode(BloomFilter.BLEND_SCREEN);
//		BloomFilter.instance().applyTo(p.g);

		GrainFilter.instance().setTime(p.frameCount * 0.01f);
		GrainFilter.instance().setCrossfade(0.1f);
		GrainFilter.instance().applyTo(p.g);
		BloomFilter.instance().applyTo(p.g);
		
		VignetteFilter.instance().setDarkness(0.7f);
		VignetteFilter.instance().applyTo(p.g);
	}
	
	protected PShape voxelizeMesh(PShape s, float spacing, float extent) {
		// iterate through 3d mesh of points
		Triangle3d[] faces = PShapeUtil.getTesselatedFaces(s);
		PShape group = p.createShape(P.GROUP);
		for (float x = -extent; x < extent; x+=spacing) {
			for (float y = -extent; y < extent; y+=spacing) {
				for (float z = -extent; z < extent; z+=spacing) {

					// check point against all triangles
					int numCollisions = 0;
					for (int i = 0; i < faces.length; i++) {
						// offset position and ray vector to prevent edge failures
						boolean collided = PShapeUtil.rayIntersectsTriangle(
								new PVector(x - 0.366f, y + 0.333f, z + 0.366f),	// point position 
								new PVector(-0.45f,-0.15f,-0.77f), 					// ray direction - had to tweak this to avoid false positives...
								faces[i].v1, faces[i].v2, faces[i].v3);				// triangle
						if(collided) numCollisions++;
					}
					
					// inside mesh!
					if(numCollisions % 2 == 1) {
						// add to group
						PShape subShape = Shapes.createBox(spacing/2);
						subShape.translate(x, y, z);
						group.addChild(subShape);
					}
				}
			}
		}
		return group;
	}
		
}
