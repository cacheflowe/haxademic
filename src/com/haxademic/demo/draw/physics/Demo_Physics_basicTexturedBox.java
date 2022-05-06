package com.haxademic.demo.draw.physics;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import fisica.FBox;
import fisica.FWorld;
import fisica.Fisica;

public class Demo_Physics_basicTexturedBox 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FWorld world;
	protected ArrayList<FBox> boxes = new ArrayList<>();

	protected void config() {
		Config.setAppSize(960, 960);
	}

	protected void firstFrame() {
		Fisica.init(this);

		world = new FWorld();
		world.setGravity(0, 400);
		world.setEdges();
		
		int rows = 10;
		int cols = 10;
		int numToCreate = rows * cols;
		int spacingX = p.width / cols;
		int spacingY = p.height / rows;
		
		for (int i = 0; i < numToCreate; i++) {
			float x = MathUtil.gridXFromIndex(i, cols) * spacingX + spacingX/2;
			float y = MathUtil.gridYFromIndex(i, cols) * spacingY + spacingY/2;
			float size = 60;//p.random(5, 40);
			FBox b = new FBox(size * p.random(0.5f, 1), size * p.random(0.5f, 1));
		    b.setPosition(x, y);
		    b.setDensity(p.random(0.3f, 0.7f));
		    b.setVelocity(0, 0);
		    b.setDamping(0.0f);
		    b.setNoStroke();
		    b.setFill(255);

			boxes.add(b);
			world.add(b);
		}
		DebugView.setValue("num circles", numToCreate);

	}

	protected void drawApp() {		
		p.background(0);
		PG.setDrawCenter(p);
		
		// set visual style & physics props
		world.setGravity(0, 0);
		for (int i = 0; i < boxes.size(); i++) {
			FBox b = boxes.get(i);
			b.setStroke(0, 255, 0);
			b.setStrokeWeight(2);
			b.setFill(30);
			b.setFriction(10f);
			b.setDamping(0);
			b.setDensity(50);
			b.setRestitution(0.7f);
		}
		
		// move blobs around
		float moveForce = 2000f;
		for (int i = 0; i < boxes.size(); i++) {
			FBox b = boxes.get(i);
			b.addForce(
				P.cos(p.frameCount/100 + i) * moveForce, 
				P.sin(p.frameCount/100 + i) * moveForce
			);
		}
		
		// add force toward center
		float centerX = p.width / 2f;
		float centerY = p.height / 2f;
		moveForce = 100.9f;
		for (int i = 0; i < boxes.size(); i++) {
			p.fill(ColorsHax.colorFromGroupAt(0, i));
			p.stroke(ColorsHax.colorFromGroupAt(0, i+1));
			FBox b = boxes.get(i);
			b.addForce(
				(centerX - b.getX()) * moveForce, 
				(centerY - b.getY()) * moveForce
			);
		}

		// update fisica simulation
		world.step();
//		world.draw();
		
		// draw manually
		p.fill(0);
		p.stroke(255);
		p.noStroke();
		for (int i = 0; i < boxes.size(); i++) {
			FBox box = boxes.get(i);
			float x = box.getX();
			float y = box.getY();
			float rot = box.getRotation();
			float w = box.getWidth();
			float h = box.getHeight();
			p.push();
			p.translate(x, y);
			p.rotate(rot);
			p.rect(0, 0, w, h);
			Shapes.drawTexturedRect(p.g, DemoAssets.justin(), w, h);
			p.pop();
		}
	}
	
}