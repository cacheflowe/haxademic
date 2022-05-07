package com.haxademic.demo.draw.physics;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;

import fisica.FBox;
import fisica.FWorld;
import fisica.Fisica;
import processing.core.PImage;

public class Demo_Physics_texturedBox_mouseInfluence 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FWorld world;
	protected ArrayList<FBox> boxes = new ArrayList<>();
	protected ArrayList<PImage> images;

	protected void config() {
		Config.setAppSize(1024, 1024);
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
			float size = 30;//p.random(5, 40);
			FBox b = new FBox(size, size * 1.75f);
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

		images = new ArrayList<PImage>(); // FileUtil.loadImagesFromDir(FileUtil.getPath("image-path"), "jpg,png");
		images.add(P.getImage("haxademic/images/cursor-finger-trans.png"));
		images.add(P.getImage("haxademic/images/cursor-hand.png"));
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
			b.setDensity(30);
			b.setRestitution(0.5f);
		}
		
		// move blobs around
		float moveForce = 5000f;
		for (int i = 0; i < boxes.size(); i++) {
			FBox b = boxes.get(i);
			float dist = P.dist(Mouse.x, Mouse.y, b.getX(), b.getY());
			float forceByDist = (moveForce - dist * 20f);
			if(i == 0) DebugView.setValue("forceByDist", forceByDist);
			if(forceByDist < 0) forceByDist = 0;
			b.addForce(
				Mouse.xSpeed * forceByDist * 10f, 
				Mouse.ySpeed * forceByDist * 10f
			);
		}
		
		// add force toward center
		float centerX = p.width / 2f;
		float centerY = p.height / 2f;
		moveForce = 200f;
		for (int i = 0; i < boxes.size(); i++) {
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
			Shapes.drawTexturedRect(p.g, images.get(i % images.size()), w, h);
			p.pop();
		}
	}
	
}