package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.ColorHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.MotionBlurPGraphics;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.JoonsWrapper;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Demo_MotionBlurPGraphics
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics _pg;
	protected MotionBlurPGraphics _pgMotionBlur;

	float _frames = 40;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );

		p.appConfig.setProperty( AppSettings.SUNFLOW, true );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, false );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, AppSettings.SUNFLOW_QUALITY_LOW );
		p.appConfig.setProperty( AppSettings.SUNFLOW_SAVE_IMAGES, false );

		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, ""+Math.round(_frames) );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames*2) );
//		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
//		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+2) );
}

	public void setup() {
		super.setup();	
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		buildCanvas();
	}

	protected void buildCanvas() {
		_pg = p.createGraphics( p.width, p.height, P.P3D );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);
		_pgMotionBlur = new MotionBlurPGraphics(10);
	}

	protected void drawGraphicsNative( PGraphics pg ) {
		DrawUtil.setDrawCenter(pg);
		pg.beginDraw();
		pg.clear();
		DrawUtil.setBasicLights(pg);
		
		float frameRadians = P.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radiansComplete = P.TWO_PI * percentComplete;
		float easedPercent = Penner.easeInOutCubic(percentComplete, 0, 1, 1);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);

//		pg.background(0);
		pg.translate(p.width/2, p.height/2 + p.height * 0.04f);
		pg.blendMode(P.SCREEN);
//		pg.blendMode(P.MULTIPLY);

//		pg.rotateY(p.mouseX * 0.01f);
//		pg.rotateX(p.mouseY * 0.01f);
		pg.rotateX(4.3f + 0.01f * P.sin(percentComplete * P.TWO_PI));
//		pg.rotateX(4.3f);
		
		pg.noStroke();
		
		int numDiscs = 25;
		float spacing = p.width * 0.019f;
		pg.translate(0, 0, spacing * numDiscs * 0.3f);
		for (int i = 0; i < numDiscs; i++) {			
			float percentDone = percentComplete + ((float)i/numDiscs);
			float size = p.width * 0.3f + p.width * 0.01f * P.sin(percentDone * P.TWO_PI);
			int discColor = p.color(200 + (i*5), 100 + (i*5), 170 - (i*5), 60);
			pg.fill(discColor);

			pg.translate(0,0,-spacing);
//			Shapes.drawDisc3D(pg, size, size * 0.9f - size * (P.sin(percentDone * P.TWO_PI)+1) * 0.15f, spacing, 40, discColor, discColor);
			Shapes.drawDisc(pg, size, size * 0.9f - size * (P.sin(percentDone * P.TWO_PI)+1) * 0.15f, 6); 
		}
		
		pg.endDraw();
	}
	
	protected void drawGraphicsSunflow( PApplet pg ) {
		p.noStroke();
		DrawUtil.setDrawCenter(p);
		
		joons.jr.background(JoonsWrapper.BACKGROUND_AO);
		joons.jr.background(0, 0, 0);

		float frameRadians = P.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radiansComplete = P.TWO_PI * percentComplete;
		float easedPercent = Penner.easeInOutCubic(percentComplete, 0, 1, 1);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);
		
		setUpRoom();
		
		p.translate(0, p.height * 0.1f, -480);
		
		// white bg block
//		p.pushMatrix();
//		p.translate(0,0,-1500);
//		DrawUtil.setDrawCenter(p);
//		p.fill(255);
//		_jw.jr.fill( JoonsWrapper.MATERIAL_SHINY, 255, 255, 255 );
//		p.box(p.width*10, p.width*10, 2);
//		p.popMatrix();
		
		p.rotateX(4.6f + P.sin(radiansComplete)*0.01f);

		int numDiscs = 30;
		float spacing = p.width * 0.019f;
		p.translate(0, 0, spacing * numDiscs * 0.3f);
		for (int i = 0; i < numDiscs; i++) {			
			float percentDone = percentComplete + ((float)i/numDiscs);
			float size = p.width * 0.3f + p.width * 0.01f * P.sin(percentDone * P.TWO_PI);
			int discColor = p.color(200 + (i*5), 100 + (i*5), 170 - (i*5), 0.3f);
			p.fill(discColor);

			p.translate(0,0,-spacing);
			joons.jr.fill( JoonsWrapper.MATERIAL_SHINY, ColorHax.redFromColorInt(discColor), ColorHax.greenFromColorInt(discColor), ColorHax.blueFromColorInt(discColor) );
			drawDisc3D(size, size * 0.9f - size * (P.sin(percentDone * P.TWO_PI)+1) * 0.15f, spacing * 0.25f, 140, discColor, discColor);
//			drawDisc(size, size * 0.9f - size * (P.sin(percentDone * P.TWO_PI)+1) * 0.15f, 140); 
		}
	}


	public void drawApp() {
		if(p.appConfig.getBoolean("sunflow", false) == false) {
			p.background(0);
			drawGraphicsNative(_pg);
			_pgMotionBlur.updateToCanvas(_pg, p.g, 1f);
		} else {			
			drawGraphicsSunflow(p);			
		}
	}

	protected void setUpRoom() {
		pushMatrix();
		translate(0, 0, 0);
		float radiance = 20;
		int samples = 16;
		int grey = 30;
		joons.jr.background("cornell_box", 
				12000, 6000, 6000,	// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				grey, grey, grey, // left rgb
				grey, grey, grey, // right rgb
				grey, grey, grey, // back rgb
				grey, grey, grey, // top rgb
				grey, grey, grey  // bottom rgb
		); 
		popMatrix();		
	}
	
	
	public static void drawDisc( float radius, float innerRadius, int numSegments )
	{
		p.pushMatrix();

		// draw triangles
		
		for( int i = 0; i < numSegments; i++ )
		{
			p.beginShape(P.TRIANGLES);
			float segmentCircumference = (2f*P.PI) / numSegments;
			
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, 0 );
			p.vertex( P.sin( i * segmentCircumference ) * radius, P.cos( i * segmentCircumference ) * radius, 0 );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius, 0 );
			
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, 0 );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, 0 );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius, 0 );
			p.endShape();
		}
		
		
		p.popMatrix();
	}

	public static void drawDisc3D( float radius, float innerRadius, float cylinderHeight, int numSegments, int color, int wallcolor ) {
		float segmentCircumference = (2f*P.PI) / numSegments;
		float halfHeight = cylinderHeight / 2;

		// top/bottom discs
		p.pushMatrix();
		p.translate(0, 0, halfHeight);
		drawDisc(radius, innerRadius, numSegments);
		p.popMatrix();
		
		p.pushMatrix();
		p.translate(0, 0, -halfHeight);
		drawDisc(radius, innerRadius, numSegments);
		p.popMatrix();
		
		p.beginShape(P.TRIANGLES);
		for( int i = 0; i < numSegments; i++ )
		{
//			if( wallcolor > 0 ) p.fill( wallcolor );
			// outer wall
			p.vertex( P.sin( i * segmentCircumference ) * radius, P.cos( i * segmentCircumference ) * radius, halfHeight );
			p.vertex( P.sin( i * segmentCircumference ) * radius, P.cos( i * segmentCircumference ) * radius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
			
			p.vertex( P.sin( i * segmentCircumference ) * radius, P.cos( i * segmentCircumference ) * radius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius, -halfHeight );
			
			// only draw inner radius if needed
			if( innerRadius > 0 )
			{
//				if( wallcolor > 0 ) p.fill(wallcolor);
				// inner wall
				p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, halfHeight );
				p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, -halfHeight );
				p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
				
				p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, -halfHeight );
				p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
				p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
			}
		}
		
		p.endShape();
	}


}
