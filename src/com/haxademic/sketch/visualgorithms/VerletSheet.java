package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;

import processing.core.PVector;

public class VerletSheet
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// ported from: https://www.openprocessing.org/sketch/545247

	protected PointVerl[] points = new PointVerl[121];
	protected StickVerl[] sticks = new StickVerl[840];
	protected float bounce = 0.1f;
	protected float gravity = 0.5f;
	protected float friction = 0.955f;


	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 800);
		Config.setProperty(AppSettings.HEIGHT, 600);
	}

	protected void firstFrame() {
		//loop through x &y and affs 
		int pointIndex = 0;
		for(int y=0;y<=10;y++){
			for(int x=0;x<=10;x++){
				boolean pinned = (y==0&&x%5==0); //adds pinned points across the top 
				points[pointIndex] = new PointVerl(150+30*x, 70+30*y, pinned);
				pointIndex++;
			}
		}

		//loops through points and adds sticks between them
		int stickIndex = 0;
		for(int i=0;i<points.length;i++){
			for(int j=0;j<points.length;j++){
				if(j!=i){//if the point is not itself
					if(points[i].pos.dist(points[j].pos) <= 45) {
						sticks[stickIndex] = new StickVerl(points[i], points[j], points[i].pos.dist(points[j].pos));
						stickIndex++;
					}    
				}
			}
		}
		P.println("pointIndex", pointIndex);
		P.println("stickIndex", stickIndex);
	}

	public class PointVerl {
		public PVector pos;
		public PVector oldPos;
		public boolean pinned;

		public PointVerl(float x, float y, boolean pinned) {
			pos = new PVector(x, y);
			oldPos = new PVector(x, y);
			this.pinned = pinned;
		}
	}

	public class StickVerl {
		public PointVerl p0;
		public PointVerl p1;
		public float length;

		public StickVerl(PointVerl p1, PointVerl p2, float lengt) {
			this.p0 = p1;
			this.p1 = p2;
			this.length = lengt;
		}
	}



	protected void updatePoints() {
		//loop through points
		for(int i = 0; i < points.length; i++) {
			PointVerl p = points[i];
			if(!p.pinned){ //if point is NOT pinned

				// finds point velocity
				float vx = (p.pos.x - p.oldPos.x) * friction;
				float vy = (p.pos.y - p.oldPos.y) * friction;

				//sets point old x and y
				p.oldPos.set(p.pos);

				//apply velocity
				p.pos.x += vx;
				p.pos.y += vy;

				//apply gravity
				p.pos.y += gravity;

				if(p.pos.x > width) {
					p.pos.x = width;
					p.oldPos.x = p.pos.x + vx * bounce;
				} else {
					if(p.pos.x < 0) {
						p.pos.x = 0;
						p.oldPos.x = p.pos.x + vx * bounce;
					}
				}

				if(p.pos.y > height) {
					p.pos.y = height;
					p.oldPos.y = p.pos.y + vy * bounce;
				} else {
					if(p.pos.y < 0) {
						p.pos.y = 0;
						p.oldPos.y = p.pos.y + vy * bounce;
					}
				}
			} else {
				// added mouse control by cacheflowe
				if(i == 0) {
					p.pos.set(mouseX, mouseY);
				}
			}
		}
	};

	protected void updateSticks() {
		//loop through sticks
		for(int i = 0; i < sticks.length; i++) {
			StickVerl s = sticks[i];

			//finds dist between the two points 
			float df =  dist(s.p0.pos.x,s.p0.pos.y,s.p1.pos.x,s.p1.pos.y);

			//finds percent diffence between dist and length
			float percent = (s.length - df) / df / 2f;
			

			//calaculate adjusments
			float offsetX = (s.p1.pos.x - s.p0.pos.x) * percent;
			float offsetY = (s.p1.pos.y - s.p0.pos.y) * percent;

			if(!s.p0.pinned) {//apply adjusments if it is not pinned
				s.p0.pos.x -= offsetX;
				s.p0.pos.y -= offsetY;
			}
			
			if(!s.p1.pinned) {//apply adjusments if it is not pinned
				s.p1.pos.x += offsetX;
				s.p1.pos.y += offsetY;
			}

			//cuts stick if mouseispressed
			if(mousePressed&&
					(dist(s.p0.pos.x,s.p0.pos.y,mouseX,mouseY)<=15||
					dist(s.p1.pos.x,s.p1.pos.y,mouseX,mouseY)<=15)
					){
				//				sticks.splice(i,1);
			}    
		}
	};


	protected void drawApp() {
		p.background(255);
		p.strokeWeight(1);
		p.stroke(0);
		PG.setDrawCenter(p);
		
		updatePoints();//update points pos
		updateSticks();//update sticks pos

		for(int i = 0; i < points.length; i++) {
			PointVerl p = points[i];
			ellipse(p.pos.x,p.pos.y,5,5);
		}
		for(int i = 0; i < sticks.length; i++) {
			StickVerl s = sticks[i];
			line(s.p0.pos.x, s.p0.pos.y,s.p1.pos.x, s.p1.pos.y);
		}

	}

}

