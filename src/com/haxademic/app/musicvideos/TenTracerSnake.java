package com.haxademic.app.musicvideos;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

public class TenTracerSnake
extends PAppletHax  
{	
	Block[] blocks;
	int numBlocks = 360;
		
	public void setup() {
		super.setup();
		blocks = new Block[numBlocks];
		for(int i=0; i < numBlocks; i++) {
			blocks[i] = new Block(i);
		}
		smooth();
	}
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.FPS, "30" );
		p.appConfig.setProperty( AppSettings.WIDTH, "1920" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "1080" );
	}
		
	// FRAME LOOP RENDERING ===================================================================================
	public void drawApp() {
		p.background(0);
//		p.fill( 255 );
//		p.noStroke();
//		p.rectMode( PConstants.CENTER );
//		DrawUtil.setBasicLights( p );
//		
//		// draw current frame and image filter
//		DrawUtil.setColorForPImage(this);
//		DrawUtil.setPImageAlpha(this, 1.0f);
		
		p.translate(width/2, height/2);
		p.rotate(p.frameCount / 20f);
		
		p.fill(random(0,255));
		p.smooth();
		for(int i=0; i < blocks.length; i++) {
			blocks[i].update();
		}

	}
		
	class Block {
		float x;
		float y;
		float color;
		float speed;
		float radians;
		int index;
		
		float radianDivisor = 100f;
		float radianIncDivisor = 800f;
		float speedDivisor = 60f;
		int connectBack = 15;

		public Block(int i) {
			index = i;
			x = 0;
			y = 0;
			color = 55f + 200f * index/numBlocks;
			speed = index / speedDivisor;
			radians = P.TWO_PI/radianDivisor * index;
		}

		void update() {
			fill(color);
			noStroke();
			x += sin(radians) * speed;
			y += cos(radians) * speed;
			
			radianIncDivisor = 1500f + 1200f * sin(p.frameCount/7f);
			radianDivisor = 200f + 150 * sin(p.frameCount/14f);
			
			radians += TWO_PI/radianIncDivisor * index/10f;
			
			strokeWeight(index/200f);
//			strokeWeight(1f);

			if(index > 0) {
				line(x, y, blocks[index-1].x, blocks[index-1].y);
				stroke(color);
			}

			if(index > connectBack) {
				line(x, y, blocks[index-connectBack].x, blocks[index-connectBack].y);
				stroke(color);
			}
		}
	}

}


