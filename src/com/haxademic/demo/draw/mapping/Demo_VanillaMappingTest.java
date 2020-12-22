package com.haxademic.demo.draw.mapping;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PShape;
import processing.core.PVector;

public class Demo_VanillaMappingTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PShape _curShape;
	protected ArrayList<PShape> _shapes;
	protected boolean _isPressed = false;
	protected boolean _debugging = true;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "1024" );
		Config.setProperty( AppSettings.HEIGHT, "768" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.FILLS_SCREEN, "true" );
		Config.setProperty( AppSettings.FULLSCREEN, "true" );
	}
	
	protected void firstFrame() {
		AudioIn.instance();
		
		_shapes = new ArrayList<PShape>();
		
		p.strokeWeight( 1 );
	}
	
	protected void drawApp() {
		p.background(0);
		
		// draw already-drawn shapes
		for (int i=0; i < _shapes.size(); i++) {
			// get shape and set audio-reactive fill --------------
			PShape shape = _shapes.get(i);
			shape.setFill(p.color(255, AudioIn.audioFreq(i * 10 + 10) * 2000));
			p.shape( shape );

			
			if( _debugging == true ) {
				// draw wireframe and handles -------------------------
				PVector v = null;
				PVector nextV = null;
				int numVertices = shape.getVertexCount();
				for (int j = 0; j < shape.getVertexCount(); j++) {
					v = shape.getVertex(j);
					p.ellipse( v.x, v.y, 6, 6 );
					if( j < numVertices - 1 ) {
						nextV = shape.getVertex(j+1);
						p.line( v.x, v.y, nextV.x, nextV.y );
					}
				}
				p.line( shape.getVertex(0).x, shape.getVertex(0).y, shape.getVertex(numVertices-1).x, shape.getVertex(numVertices-1).y );
			}
		}
		
		// draw mouse point when pressed
		if( _isPressed ) {
			p.fill(255);
			p.noStroke();
			p.ellipse( p.mouseX, p.mouseY, 6, 6 );
		}
		
		// draw currently-drawing shape
		if( _curShape != null ) {
			PVector v = null;
			PVector lastV = null;
			p.noFill();
			p.stroke(255);
			for (int i = 0; i < _curShape.getVertexCount(); i++) {
				v = _curShape.getVertex(i);
				p.ellipse( v.x, v.y, 6, 6 );
				if( i > 0 ) {
					lastV = _curShape.getVertex(i-1);
					p.line( v.x, v.y, lastV.x, lastV.y );
				}
			}
			
			// draw last vertex to mouse if pressed
			if( _isPressed && v != null ) {
				p.line( p.mouseX, p.mouseY, v.x, v.y );
			}
		}
	}
	
	public void mousePressed() {
		super.mousePressed();
		_isPressed = true;
	}
	
	public void mouseReleased() {
		super.mouseReleased();
		_isPressed = false;
		
		if (_curShape == null) {
			_curShape = p.createShape();
			_curShape.noStroke();  
			_curShape.setFill(color(255, 200));
			_curShape.beginShape();
			_curShape.vertex( p.mouseX, p.mouseY );
		} else {
			_curShape.vertex( p.mouseX, p.mouseY );
		}
	}
	
	public void keyPressed() {
		if(p.key == ' ') {
			if( _curShape != null ) {
				_shapes.add(_curShape);
				_curShape.endShape(P.CLOSE);
				_curShape = null;
			}
		} else if(p.key == 'd') {
			_debugging = !_debugging;
		}
	}

}