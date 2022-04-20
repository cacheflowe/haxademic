package com.haxademic.demo.draw.physics;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.math.MathUtil;

import fisica.FCircle;
import fisica.FDistanceJoint;
import fisica.FWorld;
import fisica.Fisica;

public class Demo_Physics_connections 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FWorld world;
	protected ArrayList<FCircle> circles = new ArrayList<>();

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
			float size = p.random(20, 40);

			FCircle left = new FCircle(size);
			left.setPosition(x, y);
			world.add(left);

			FCircle right = new FCircle(size);
			right.setPosition(x + 40, y);
			world.add(right);

			FDistanceJoint joint = new FDistanceJoint(left, right);
			joint.setAnchor1(0, 0);
			joint.setAnchor2(0, 0);
			joint.setFrequency(10f);
			joint.setDamping(1f);
			joint.calculateLength();
			joint.setFill(0);
			world.add(joint);
		}
		DebugView.setValue("num circles", numToCreate);
	}

	protected void drawApp() {		
		p.background(0);

		// update & draw fisica simulation
		world.step();
		world.draw();
	}

}