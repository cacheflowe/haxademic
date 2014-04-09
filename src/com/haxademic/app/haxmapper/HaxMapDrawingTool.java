package com.haxademic.app.haxmapper;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

@SuppressWarnings("serial")
public class HaxMapDrawingTool
extends PAppletHax {

	protected PShape _curShape;
	protected ArrayList<PShape> _shapes;
	protected ArrayList<ArrayList<PShape>> _shapeGroups;
	protected boolean _isPressed = false;
	protected boolean _debugging = true;
	protected String _inputFileLines[];

	protected ArrayList<PShape> _draggingShapes;
	
	protected final static int MODE_TRIANGLES = 0;
	protected final static int MODE_RECTS = 1;
	protected int _mode = 0;
	
	protected int curMouseX = 0;
	protected int curMouseY = 0;

	public static void main(String args[]) {
		_isFullScreen = true;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "com.haxademic.app.haxmapper.HaxMapDrawingTool" });
	}

	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "fills_screen", "true" );
		_appConfig.setProperty( "fullscreen", "true" );
		_appConfig.setProperty( "mapping_file", FileUtil.getHaxademicDataPath() + "text/mapping/mapping-2014-03-23-23-24-17.txt" );
		_appConfig.setProperty( "mapping_file", FileUtil.getHaxademicDataPath() + "text/mapping/mapping-2014-04-06-21-30-19.txt" );
	}

	public void setup() {
		super.setup();

		_shapes = new ArrayList<PShape>();
		_shapeGroups = new ArrayList<ArrayList<PShape>>();
		loadVertices();
		
		_draggingShapes = new ArrayList<PShape>();

		p.strokeWeight( 1 );
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
	}

	// loop -------------------------------------------------------------
	public void drawApp() {
		p.background(0);

		drawExistingShapes();
		handleMouse();
		drawCurrentShape();
		drawMode();
	}
	
	// group management -------------------------------------------------
	public void newGroup() {
		_shapeGroups.add( new ArrayList<PShape>() );
	}
	
	public ArrayList<PShape> curGroup() {
		return _shapeGroups.get(_shapeGroups.size()-1);
	}
	
	// draw -------------------------------------------------------------
	public void drawMode() {
		p.noStroke();
		p.fill(255);
		if( _mode == MODE_TRIANGLES ) {
			p.triangle(80, 50, 110, 100, 50, 100);
		} else if( _mode == MODE_RECTS ) {
			p.rect(50, 50, 50, 50);
		}
	}
	
	public void drawExistingShapes() {
		// draw already-drawn shapes
		p.noFill();
		p.stroke(255);

		for (int i=0; i < _shapes.size(); i++) {
			// get shape and set audio-reactive fill --------------
			PShape shape = _shapes.get(i);
			shape.setFill(p.color(255, p.audioIn.getEqAvgBand((i * 10 + 10) % 512) * 2000));
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
	}
	
	public void drawCurrentShape() {
		// draw currently-drawing shape
		if( _curShape != null ) {
			PVector v = null;
			PVector lastV = null;
			p.noFill();
			p.stroke(80,255,80);
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
				p.line( curMouseX, curMouseY, v.x, v.y );
			}
		}
	}

	// mouse handling -------------------------------------------------------------
	public void handleMouse() {
		// draw mouse point when pressed
		if( _isPressed ) {
			if( _draggingShapes.size() > 0 ) {
				// move the vertices of the clicked shapes
				PShape curShape = null;
				for( int i=0; i < _draggingShapes.size(); i++ ) {
					curShape = _draggingShapes.get(i);
					for (int j = 0; j < curShape.getVertexCount(); j++) {
						if(curShape.getVertex(j).z == 1) {
							curShape.setVertex(j, p.mouseX, p.mouseY, 1);
						}
					}
				}
			} else {
				// check current mouse position
				curMouseX = p.mouseX;
				curMouseY = p.mouseY;
				
				// find closest vertex to mouse
				float closestPoint = 9999;
				PShape curShape = null;
				PVector curVertex = null;
				PVector closestVertex = null;
				for (int i=0; i < _shapes.size(); i++) {
					curShape = _shapes.get(i);
					for (int j = 0; j < curShape.getVertexCount(); j++) {
						curVertex = curShape.getVertex(j);
						float mouseDistToVertex = MathUtil.getDistance(p.mouseX, p.mouseY, curVertex.x, curVertex.y);
						if( mouseDistToVertex < closestPoint  ) {
							closestPoint = mouseDistToVertex;
							closestVertex = curVertex;
						}
					}
				}
				if( closestVertex != null && closestPoint < 15 ) {
					curMouseX = (int) closestVertex.x;
					curMouseY = (int) closestVertex.y;
				}
				
				// draw mouse point
				p.fill(80,255,80);
				p.noStroke();
				p.ellipse( curMouseX, curMouseY, 6, 6 );
			}
		}
	}
	
	public void mousePressed() {
		super.mousePressed();
		_isPressed = true;
		
		
		// find closest vertex to mouse
		float closestPoint = 10;
		PShape curShape = null;
		PVector curVertex = null;
		for (int i=0; i < _shapes.size(); i++) {
			curShape = _shapes.get(i);
			for (int j = 0; j < curShape.getVertexCount(); j++) {
				curVertex = curShape.getVertex(j);
				float mouseDistToVertex = MathUtil.getDistance(p.mouseX, p.mouseY, curVertex.x, curVertex.y);
				if( mouseDistToVertex < closestPoint ) {
					// set z to 1 so that we know that we're dragging (this is a silly hack)
					curShape.setVertex(j, curShape.getVertex(j).x, curShape.getVertex(j).y, 1);
					_draggingShapes.add( curShape );
				}
			}
		}
	}

	public void mouseReleased() {
		super.mouseReleased();
		_isPressed = false;

		if( _draggingShapes.size() > 0 ) {
			while( _draggingShapes.size() > 0 ) {
				// remove and set z back to 0
				PShape curShape = _draggingShapes.remove(_draggingShapes.size()-1);
				for (int j = 0; j < curShape.getVertexCount(); j++) curShape.setVertex(j, curShape.getVertex(j).x, curShape.getVertex(j).y, 0);
			}
		} else {			
			if (_curShape == null) {
				_curShape = p.createShape();
				_curShape.setFill(color(255, 200));
				_curShape.beginShape();
				_curShape.noStroke();  
				_curShape.vertex( curMouseX, curMouseY );
			} else {
				_curShape.vertex( curMouseX, curMouseY );
			}
			// auto-close shape
			if( _mode == MODE_TRIANGLES ) {
				if( _curShape.getVertexCount() == 3 ) {
					closeShape();
				}
			} else if( _mode == MODE_RECTS ) {
				if( _curShape.getVertexCount() == 4 ) {
					closeShape();
				}
			}
		}
	}
	
	public void closeShape() {
		_shapes.add(_curShape);
		curGroup().add(_curShape);
		_curShape.endShape(P.CLOSE);
		_curShape = null;
	}
	
	public void exportVertices() {
		String export = "";
		for( int i=0; i < _shapeGroups.size(); i++ ) {
			export += "#group#\n";
			ArrayList<PShape> curGroup = _shapeGroups.get(i);
			for( int j=0; j < curGroup.size(); j++ ) {
				PShape curShape = curGroup.get(j);
				PVector vertex;
				export += "#poly#";
				for (int k = 0; k < curShape.getVertexCount(); k++) {
					vertex = curShape.getVertex(k);
					if( k > 0 ) export += ",";
					export += vertex.x+","+vertex.y;
				}
				export += "\n";
			}
		}
		FileUtil.writeTextToFile(FileUtil.getHaxademicDataPath() + "text/mapping/mapping-"+SystemUtil.getTimestamp(p)+".txt", export);
	}
	
	public void loadVertices() {
		if( _appConfig.getString("mapping_file", "") != "" ) {
			_inputFileLines = loadStrings(_appConfig.getString("mapping_file", ""));
			for( int i=0; i < _inputFileLines.length; i++ ) {
				String inputLine = _inputFileLines[i]; 
				// count lines that contain characters
				if( inputLine.indexOf("#group#") != -1 ) {
					newGroup();
				} else if( inputLine.indexOf("#poly#") != -1 ) {
					inputLine = inputLine.replace("#poly#", "");
					String polyPoints[] = inputLine.split(",");

					_curShape = p.createShape();
					_curShape.setFill(color(255, 200));
					_curShape.beginShape();
					_curShape.noStroke();  
					if(polyPoints.length == 6) {
						_curShape.vertex( ConvertUtil.stringToFloat( polyPoints[0] ), ConvertUtil.stringToFloat( polyPoints[1] ) );
						_curShape.vertex( ConvertUtil.stringToFloat( polyPoints[2] ), ConvertUtil.stringToFloat( polyPoints[3] ) );
						_curShape.vertex( ConvertUtil.stringToFloat( polyPoints[4] ), ConvertUtil.stringToFloat( polyPoints[5] ) );
					} else {
						_curShape.vertex( ConvertUtil.stringToFloat( polyPoints[0] ), ConvertUtil.stringToFloat( polyPoints[1] ) );
						_curShape.vertex( ConvertUtil.stringToFloat( polyPoints[2] ), ConvertUtil.stringToFloat( polyPoints[3] ) );
						_curShape.vertex( ConvertUtil.stringToFloat( polyPoints[4] ), ConvertUtil.stringToFloat( polyPoints[5] ) );
						_curShape.vertex( ConvertUtil.stringToFloat( polyPoints[6] ), ConvertUtil.stringToFloat( polyPoints[7] ) );
					}
					closeShape();
				}  
			}
			
		} else {
			newGroup();
		}

	}

	// keyboard handling -------------------------------------------------------------
	public void keyPressed() {
		if(p.key == 'd') {
			// show debugging lines & points
			_debugging = !_debugging;
		} else if(p.key == 'm') {
			// swap rect/triangle mode
			_mode = (_mode == MODE_TRIANGLES) ? MODE_RECTS : MODE_TRIANGLES;
		} else if(p.key == 'g') {
			// add a new group
			newGroup();
		} else if(p.key == 'e') {
			// add a new group
			exportVertices();
		} else if(p.key == PConstants.BACKSPACE) {
			// remove last object (undo)
			if( _shapes.size() > 0 ) {
				_shapes.remove(_shapes.size() - 1);
				if(curGroup().size() == 0) _shapeGroups.remove(_shapeGroups.size() - 1);
				curGroup().remove(curGroup().size() - 1);
			}
		}
	}

}