package com.haxademic.demo.math;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.SphericalCoord;

import processing.core.PVector;

public class Demo_SpherePoint
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected float sphereRadius;
	protected ArrayList<SpherePoint> spheres = new ArrayList<SpherePoint>();
	protected PVector[] spherePointsFib;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 800);
		p.appConfig.setProperty(AppSettings.HEIGHT, 800);
	}

	public void setupFirstFrame() {
		sphereRadius = p.width * 0.4f;
		spherePointsFib = SphericalCoord.buildFibonacciSpherePoints(200);
		addSpheres();
	}
	
	public void drawApp() {
		p.background(200);
		PG.setBetterLights(p);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);
		
		// draw mesh sphere to show positions
		p.stroke(0,100);
		p.strokeWeight(1);
		p.noFill();
		p.pushMatrix();
		p.rotateX(P.HALF_PI);
		p.sphere(sphereRadius);
		p.popMatrix();
		
		// draw fibonacci points
		p.noFill();
		p.stroke(255,0,0);
		p.strokeWeight(10);
		for (int i = 0; i < spherePointsFib.length; i++) {
			p.point(spherePointsFib[i].x * sphereRadius, spherePointsFib[i].y * sphereRadius, spherePointsFib[i].z * sphereRadius);
		}
		
		// update objects
		p.strokeWeight(2);
		for (int i = 0; i < spheres.size(); i ++) {
			spheres.get(i).update();
		};
	}

	public void mousePressed() {
		super.mousePressed();
		addSpheres();
	}
	
	public void addSpheres() {
		for(int i=0; i < 30; i++) {
			SpherePoint newSphere = new SpherePoint();
			newSphere.setPosition(spherePointsFib[MathUtil.randRange(0, spherePointsFib.length - 1)]);
			spheres.add(newSphere);
		}
	};

	class SpherePoint {
		// spherical coordinates
		SphericalCoord pos;
		SphericalCoord posInv;
		
		// motion
		float thetaSpeed = 0;
		float phiSpeed = 0;
		float noiseIncT = P.p.random(1000);
		float noiseIncP = P.p.random(1000);

		public SpherePoint() {
			pos = new SphericalCoord();
			posInv = new SphericalCoord();
			thetaSpeed = random(-0.01f, 0.01f);
			phiSpeed = random(-0.01f, 0.01f);
		}
		
		public void setPosition(PVector newPos) {
			pos.setCartesian(newPos.x, newPos.y, newPos.z); 
			posInv.setCartesian(-pos.cartesian.x, -pos.cartesian.y, -pos.cartesian.z);
		}

		public void update() {
			// update position
			pos.addSpherical(
				(-0.5f + p.noise(noiseIncT + p.frameCount * 0.01f)) * 0.02f,
				(-0.5f + p.noise(noiseIncP + p.frameCount * 0.01f)) * 0.1f
			);
			posInv.setCartesian(-pos.cartesian.x, -pos.cartesian.y, -pos.cartesian.z);

			// draw sphere
			p.noStroke();
			p.fill(0, 0, 0);
			pushMatrix();
			translate(pos.cartesian.x * sphereRadius, pos.cartesian.y * sphereRadius, pos.cartesian.z * sphereRadius);
			sphere(10);
			popMatrix();
			
			// show opposite
			p.fill(0, 0, 255);
			pushMatrix();
			translate(posInv.cartesian.x * sphereRadius, posInv.cartesian.y * sphereRadius, posInv.cartesian.z * sphereRadius);
			sphere(10);
			popMatrix();
			
			// line between
			p.noFill();
			p.stroke(0, 255, 0);
			p.line(
				pos.cartesian.x * sphereRadius, pos.cartesian.y * sphereRadius, pos.cartesian.z * sphereRadius, 
				posInv.cartesian.x * sphereRadius, posInv.cartesian.y * sphereRadius, posInv.cartesian.z * sphereRadius
			);
		}
	}
	
}
