package com.haxademic.demo.draw.physics;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import fisica.FBlob;
import fisica.FBody;
import fisica.FWorld;
import fisica.Fisica;
import processing.core.PVector;

public class Demo_Physics_blobsCentered 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FWorld world;
	protected ArrayList<FBlob> blobs = new ArrayList<>();
	protected int numBlobs = 0;

	protected void config() {
		Config.setAppSize(960, 960);
		//		Config.setProperty(AppSettings.LOOP_FRAMES, 120);
	}

	protected void firstFrame() {
		Fisica.init(this);

		world = new FWorld();
		world.setGravity(0, 400);
//		world.setEdges();
//		world.setAutoDebugDraw(true);
	}

	protected void drawApp() {		
		p.background(0);
		PG.setDrawCenter(p);

		world.setGravity(0, 0);

		int rows = 6;
		int cols = 6;
		int numToCreate = rows * cols;
		int spacingX = p.width / cols;
		int spacingY = p.height / rows;
		
//		if ((frameCount % 100) == 1 && numBlobs < 40) {
		if (frameCount == 1) {
			for (int i = 0; i < numToCreate; i++) {
				FBlob b = new FBlob();
				float s = random(30, 100);
				int verts = P.round(s * 0.8f);
				float x = MathUtil.gridXFromIndex(numBlobs, cols) * spacingX + spacingX/2;
				float y = MathUtil.gridYFromIndex(numBlobs, cols) * spacingY + spacingY/2;
	//			float x = p.random(0, p.width);
	//			float y = p.random(0, p.height);
				// TODO: make sure they don't overlap an existing blob
				b.setAsCircle(x, y, s, verts);
				b.setStroke(255,0,0);
				b.setStrokeWeight(2);
				b.setFill(255);
				b.setFriction(0);
				blobs.add(b);
				world.add(b);
				numBlobs++;
			}
		}
		DebugView.setValue("numBlobs", numBlobs);
		
		// move blobs around
		float moveForce = 3f;
		for (int i = 0; i < blobs.size(); i++) {
			FBlob b = blobs.get(i);
			b.addForce(
				P.cos(p.frameCount/100 + i) * moveForce, 
				P.sin(p.frameCount/100 + i) * moveForce
			);
		}
		
		// add force toward center
		float centerX = p.width / 2f;
		float centerY = p.height/ 2f;
		moveForce = 2f;
		for (int i = 0; i < blobs.size(); i++) {
			p.fill(ColorsHax.colorFromGroupAt(0, i));
			p.stroke(ColorsHax.colorFromGroupAt(0, i+1));
			FBlob b = blobs.get(i);
			PVector blobPosition = blobCenter(b, true);
			if(p.frameCount % 600 < 300) {
				b.setForce(
					(centerX - blobPosition.x) * moveForce, 
					(centerY - blobPosition.y) * moveForce
				);
			} else {
				float x = spacingX/2 + MathUtil.gridXFromIndex(i, cols) * spacingX;
				float y = spacingY/2 + MathUtil.gridYFromIndex(i, cols) * spacingY;
				b.setForce(
					(x - blobPosition.x) * moveForce, 
					(y - blobPosition.y) * moveForce
				);
			}
		}

		// update fisica simulation
		world.step();
//		world.draw();
	}
	
	protected PVector utilVec = new PVector();
	public PVector blobCenter(FBlob b, boolean draw) {
		// get blob's verts
		@SuppressWarnings("unchecked")
		ArrayList<FBody> vertBodies = b.getVertexBodies();
		int numVerts = vertBodies.size();
		
		// do center of mass calc
		float x = 0;
		float y = 0;
		p.beginShape();
		p.texture(DemoAssets.smallTexture());
		p.textureMode(P.NORMAL);
		for (int i = 0; i < numVerts; i++) {
			FBody vertBody = vertBodies.get(i);
			
			// adjust physics motion
//			vertBody.setDamping(1);
//			vertBody.setDensity(2);
//			vertBody.setFriction(1);
//			vertBody.setRestitution(0);
//			if ((frameCount % 100) == 1) {
//				vertBody.addImpulse(p.random(-50, 50), p.random(-50, 50));
//			}

			float circleProgress = (float)i / numVerts;
			float curRads = circleProgress * P.TWO_PI;
			float curX = vertBody.getX();
			float curY = vertBody.getY();
//			float nxtX = vertBodies.get((i+1) % numVerts).getX();
//			float nxtY = vertBodies.get((i+1) % numVerts).getY();
			x += curX;
			y += curY;
			
			// draw if needed
			if(draw) {
				float u = 0.5f + 0.45f * P.cos(curRads);
				float v = 0.5f + 0.45f * P.sin(curRads);
				p.vertex(curX, curY, 0, u, v);
//				p.ellipse(curX, curY, 4, 4);
//				p.line(curX, curY, nxtX, nxtY);
			}
		}
		p.endShape(P.CLOSE);
		utilVec.set(x / numVerts, y / numVerts);
		
		return utilVec;
	}

}