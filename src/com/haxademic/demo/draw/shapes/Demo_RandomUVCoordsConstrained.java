package com.haxademic.demo.draw.shapes;

import java.awt.Point;
import java.awt.Rectangle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;

import processing.core.PVector;

public class Demo_RandomUVCoordsConstrained 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PVector[] _randTriangle = {new PVector(), new PVector(), new PVector()};
	protected PVector[] _randTriangleRotated;
	protected PVector[] _randTriangleUV;
	protected PVector[] _randTriangleUpdater = {new PVector(), new PVector(), new PVector()};
	protected Rectangle _triangleBB = new Rectangle();
	protected Rectangle _triangleRotatedBB = new Rectangle();
	protected Rectangle _triangleUVBB = new Rectangle();
	protected Point _triangleCenter;

	protected BaseTexture[] _textures;
	int textureW = 500;
	int textureH = 300;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}


	protected void firstFrame() {

		newTriangle();
	}

	protected void newTriangleFullExample() {
		// make a new triangle
		_randTriangle[0].set( MathUtil.randRange(0, p.width ), MathUtil.randRange(0, p.height ) );
		_randTriangle[1].set( MathUtil.randRange(0, p.width ), MathUtil.randRange(0, p.height ) );
		_randTriangle[2].set( MathUtil.randRange(0, p.width ), MathUtil.randRange(0, p.height ) );
		_triangleBB = createBoundingBox(_randTriangle);
		
		// make rotated triangle
		_triangleCenter = MathUtil.computeTriangleCenter(_randTriangle[0].x, _randTriangle[0].y, _randTriangle[1].x, _randTriangle[1].y, _randTriangle[2].x, _randTriangle[2].y);
		_randTriangleRotated = createRotatedPolygon(_randTriangle, _triangleCenter, MathUtil.randRangeDecimal(0, P.TWO_PI));
		_triangleRotatedBB = createBoundingBox(_randTriangleRotated);
		
		// fit rotated version in texture box
		float ratioW = (float)textureW / (float)_triangleRotatedBB.width;
		float ratioH = (float)textureH / (float)_triangleRotatedBB.height;
		float containRatio = (ratioW < ratioH) ? ratioW : ratioH;
		containRatio *= MathUtil.randRangeDecimal(0.25f, 1.0f);
		_randTriangleUV = createTopLeftTriangleCopy(_randTriangleRotated, _triangleRotatedBB);
		scalePolygon(_randTriangleUV, containRatio);
		_triangleUVBB = createBoundingBox(_randTriangleUV);
		
		// find random position within texture and move triangle & bb
		float moveX = (textureW - _triangleUVBB.width) * MathUtil.randRangeDecimal(0, 1);
		float moveY = (textureH - _triangleUVBB.height) * MathUtil.randRangeDecimal(0, 1);
		_triangleUVBB.x = (int) moveX;
		_triangleUVBB.y = (int) moveY;
		translatePolygon(_randTriangleUV, (int) moveX, (int) moveY);
	}
	
	protected void newTriangle() {
		// make a new triangle
		_randTriangle[0].set( MathUtil.randRange(0, p.width ), MathUtil.randRange(0, p.height ) );
		_randTriangle[1].set( MathUtil.randRange(0, p.width ), MathUtil.randRange(0, p.height ) );
		_randTriangle[2].set( MathUtil.randRange(0, p.width ), MathUtil.randRange(0, p.height ) );
		_triangleBB = createBoundingBox(_randTriangle);
		
		// make rotated triangle
		_triangleCenter = MathUtil.computeTriangleCenter(_randTriangle[0].x, _randTriangle[0].y, _randTriangle[1].x, _randTriangle[1].y, _randTriangle[2].x, _randTriangle[2].y);
		_randTriangleRotated = createRotatedPolygon(_randTriangle, _triangleCenter, MathUtil.randRangeDecimal(0, P.TWO_PI));
		_triangleRotatedBB = createBoundingBox(_randTriangleRotated);
		
		// fit rotated version in texture box
		float ratioW = (float)textureW / (float)_triangleRotatedBB.width;
		float ratioH = (float)textureH / (float)_triangleRotatedBB.height;
		float containRatio = (ratioW < ratioH) ? ratioW : ratioH;
		containRatio *= MathUtil.randRangeDecimal(0.25f, 1.0f);
		_randTriangleUV = createTopLeftTriangleCopy(_randTriangleRotated, _triangleRotatedBB);
		scalePolygon(_randTriangleUV, containRatio);
		_triangleUVBB = createBoundingBox(_randTriangleUV);
		
		// find random position within texture and move triangle & bb
		float moveX = (textureW - _triangleUVBB.width) * MathUtil.randRangeDecimal(0, 1);
		float moveY = (textureH - _triangleUVBB.height) * MathUtil.randRangeDecimal(0, 1);
		_triangleUVBB.x = (int) moveX;
		_triangleUVBB.y = (int) moveY;
		translatePolygon(_randTriangleUV, (int) moveX, (int) moveY);
		
		setRandomTexturePolygonToDestPolygon(_randTriangle, _triangleCenter, _randTriangleUpdater, textureW, textureH);
	}
	
	public void setRandomTexturePolygonToDestPolygon(PVector[] source, Point center, PVector[] destination, int textureW, int textureH) {
		// make rotated triangle
		copyPolygon(source, destination);
		rotatePolygon(destination, center, MathUtil.randRangeDecimal(0, P.TWO_PI));
		Rectangle randomPolyRotatedBB = createBoundingBox(destination);
		
		// fit rotated version in texture box
		float ratioW = (float)textureW / (float)randomPolyRotatedBB.width;
		float ratioH = (float)textureH / (float)randomPolyRotatedBB.height;
		float containRatio = (ratioW < ratioH) ? ratioW : ratioH;
		containRatio *= MathUtil.randRangeDecimal(0.25f, 1.0f);
		translatePolygon(destination, -randomPolyRotatedBB.x, -randomPolyRotatedBB.y);

		scalePolygon(destination, containRatio);
		Rectangle destinationBB = createBoundingBox(destination);
		
		// find random position within texture and move triangle & bb
		float moveX = (textureW - destinationBB.width) * MathUtil.randRangeDecimal(0, 1);
		float moveY = (textureH - destinationBB.height) * MathUtil.randRangeDecimal(0, 1);
		destinationBB.x = (int) moveX;
		destinationBB.y = (int) moveY;
		translatePolygon(destination, (int) moveX, (int) moveY);
	}
	
	protected Rectangle createBoundingBox(PVector[] points) {
		Rectangle rect = new Rectangle(new Point((int)points[0].x, (int)points[0].y));
		for (int i = 1; i < points.length; i++) {
			rect.add(points[i].x, points[i].y);
		}
		return rect;
	}
	
	protected PVector[] createRotatedPolygon(PVector[] points, Point center, float rotateRadians) {
		PVector[] rotated = new PVector[3];
		for (int i = 0; i < points.length; i++) {
		    double cosAngle = Math.cos(rotateRadians);
		    double sinAngle = Math.sin(rotateRadians);
		    double dx = (points[i].x-center.x);
		    double dy = (points[i].y-center.y);

		    rotated[i] = new PVector();
		    rotated[i].x = center.x + (int) (dx*cosAngle-dy*sinAngle);
		    rotated[i].y = center.y + (int) (dx*sinAngle+dy*cosAngle);
		}
		return rotated;
	}
	
	protected void rotatePolygon(PVector[] points, Point center, float rotateRadians) {
		for (int i = 0; i < points.length; i++) {
			double cosAngle = Math.cos(rotateRadians);
			double sinAngle = Math.sin(rotateRadians);
			double dx = (points[i].x-center.x);
			double dy = (points[i].y-center.y);
			
			points[i].x = center.x + (int) (dx*cosAngle-dy*sinAngle);
			points[i].y = center.y + (int) (dx*sinAngle+dy*cosAngle);
		}
	}
	
	protected PVector[] createTopLeftTriangleCopy(PVector[] points, Rectangle bb) {
		PVector[] copied = new PVector[3];
		for (int i = 0; i < points.length; i++) {
			copied[i] = new PVector(points[i].x - bb.x, points[i].y - bb.y);
		}
		return copied;
	}
	
	protected void translatePolygon(PVector[] points, int moveX, int moveY) {
		for (int i = 0; i < points.length; i++) {
			points[i].x = points[i].x + moveX;
			points[i].y = points[i].y + moveY;
		}
	}
	
	protected void copyPolygon(PVector[] source, PVector[] dest) {
		for (int i = 0; i < source.length; i++) {
			dest[i].x = source[i].x;
			dest[i].y = source[i].y;
		}
	}
	
	protected void scalePolygon(PVector[] points, float scale) {
		for (int i = 0; i < points.length; i++) {
			points[i].x = points[i].x * scale;
			points[i].y = points[i].y * scale;
		}
	}
	
	
	protected void drawTriangle(PVector[] points, Rectangle bb, int color) {
		PG.setDrawCenter(p);
		for (int i = 0; i < points.length; i++) {
			// draw vertices
			PVector point = points[i];
			p.fill(color);
			p.noStroke();
			p.ellipse(point.x, point.y, 5, 5);
			
			// connect vertices with lines
			PVector nextPoint = points[(i+1) % points.length];
			p.stroke(color);
			p.noFill();
			p.line(point.x, point.y, nextPoint.x, nextPoint.y);
		}
		// draw bounding box
		PG.setDrawCorner(p);
		p.stroke(color);
		p.noFill();
		p.rect(bb.x, bb.y, bb.width, bb.height);
	}
	
	protected void drawExampleTexture() {
		PG.setDrawCorner(p);
		p.stroke(255,255,0);
		p.noFill();
		p.rect(0, 0, textureW, textureH);
	}
	
	protected void drawApp() {
		background(0);
		drawExampleTexture();
		drawTriangle(_randTriangle, _triangleBB, p.color(0,255,0));
		drawTriangle(_randTriangleRotated, _triangleRotatedBB, p.color(0,0,255));
		drawTriangle(_randTriangleUV, _triangleUVBB, p.color(255,0,0));
		drawTriangle(_randTriangleUpdater, _triangleUVBB, p.color(255));
		if(p.frameCount % 100 == 0) {
			newTriangle();
		}
	}
}