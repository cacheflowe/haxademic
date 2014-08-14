package com.haxademic.app.haxmapper.distribution;

import java.awt.Point;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import com.haxademic.app.haxmapper.HaxMapper;
import com.haxademic.app.haxmapper.overlays.MeshLines;
import com.haxademic.app.haxmapper.overlays.MeshLines.MODE;
import com.haxademic.app.haxmapper.overlays.MeshParticles;
import com.haxademic.app.haxmapper.overlays.MeshSegmentScanners;
import com.haxademic.app.haxmapper.polygons.IMappedPolygon;
import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.math.MathUtil;

public class MappingGroup {

	protected PApplet p;
	protected PGraphics overlayPG;
	protected ArrayList<IMappedPolygon> _mappedPolygons;
	protected ArrayList<BaseTexture> _curTextures;
	protected MeshLines _meshLines;
	protected MeshParticles _meshParticles;
	protected MeshSegmentScanners _meshScanners;
	protected int _color;
	protected ColorHaxEasing _colorEase;
	protected int _textureIndex = 0;
	
	protected int _traverseFrame = 0;
	protected int _traverseMode = 0;
	protected int _traverseWireMode = 0;
	protected IMappedPolygon _traversePolygon;
	protected IMappedPolygon _traversePolygonLast;
	
	protected Point center = null;

	public MappingGroup( PAppletHax p, PGraphics overlayPG ) {
		this.p = p;
		this.overlayPG = overlayPG;
		_curTextures = new ArrayList<BaseTexture>();
		_mappedPolygons = new ArrayList<IMappedPolygon>();
		_meshLines = new MeshLines( overlayPG );
		_meshParticles = new MeshParticles( overlayPG );
		_color = P.p.color(255);
		_colorEase = new ColorHaxEasing( "#000000", 4 );
	}

	public void addPolygon( IMappedPolygon polygon ) {
		// store polygon
		_mappedPolygons.add(polygon);
		// add line segments
		PVector[] vertices = polygon.getVertices();
		PVector curVertex;
		PVector nextVertex;
		for(int i=0; i < vertices.length; i++) {
			curVertex = vertices[i];
			nextVertex = (i < vertices.length - 1) ? vertices[i+1] : vertices[0];
			_meshLines.addSegment(curVertex.x, curVertex.y, nextVertex.x, nextVertex.y);
		}
		// add particle vertices
		for(int i=0; i < vertices.length; i++) {
			curVertex = vertices[i];
			_meshParticles.addVertex(curVertex.x, curVertex.y);
		}
		// find neighbors
		for(int j=0; j < _mappedPolygons.size()-1; j++ ) {
			int vertexMatches = 0;
			IMappedPolygon checkPolygon = null;
			for(int i=0; i < vertices.length; i++) {
				checkPolygon = _mappedPolygons.get(j);
				for(int k=0; k < checkPolygon.getVertices().length; k++) {
					if(checkPolygon.getVertices()[k].x == vertices[i].x && checkPolygon.getVertices()[k].y == vertices[i].y ) {
						vertexMatches++;
					}
				}
			}
			if(vertexMatches >= 2) {
				checkPolygon.addNeighbor(polygon);
				polygon.addNeighbor(checkPolygon);
			}
		}
	}
	
	public void completePolygonImport() {
		_meshScanners = new MeshSegmentScanners( this.overlayPG, _meshLines.meshLineSegments() );
	}
	
	public void pushTexture( BaseTexture texture ) {
		if( _curTextures.indexOf( texture ) == -1 ) {
			_curTextures.add(texture);
		}
	}

	public BaseTexture shiftTexture() {
//		if( _curTextures.size() > HaxMapper.MAX_ACTIVE_TEXTURES ) {
		if( _curTextures.size() > HaxMapper.MAX_ACTIVE_TEXTURES_PER_GROUP ) {
			return _curTextures.remove(0);
		} else {
			return null;
		}
	}

	public void clearAllTextures() {
		while( _curTextures.size() > 0 ) _curTextures.remove( _curTextures.size() - 1 );
	}

	public ArrayList<IMappedPolygon> polygons() {
		return _mappedPolygons;
	}

	public ArrayList<BaseTexture> textures() {
		return _curTextures;
	}

	public IMappedPolygon randomPolygon() {
		return _mappedPolygons.get( MathUtil.randRange( 0, _mappedPolygons.size() - 1 ) );
	}
	
	public BaseTexture randomBaseTexture() {
		return _curTextures.get( MathUtil.randRange( 0, _curTextures.size() - 1 ) );
	}
	
	public void randomPolygonRandomRotate() {
		randomPolygon().rotateTexture();
	}
	
	public void randomPolygonRandomMappingStyle() {
		randomPolygon().randomTextureStyle();
	}
	
	public void randomTextureToRandomPolygon() {
		if( _curTextures.size() == 0 ) return;
		randomPolygon().setTexture( randomBaseTexture().texture() );
	}
	
	public void setAllPolygonsToTexture( int textureIndex ) {
		_textureIndex = textureIndex;
		if( _curTextures.size() < 1 ) return;
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).setTexture( _curTextures.get(_textureIndex).texture() );
		}
	}
	
	public void reloadTextureAtIndex() {
		if( _curTextures.size() < 1 ) return;
		// subtract since we shift the texture pool - this keeps texture around if it still exists in pool
		_textureIndex--;
		if( _textureIndex < 0 ) _textureIndex = _curTextures.size();
		
		int safeIndex = 0;
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			safeIndex = _textureIndex % _curTextures.size();
			_mappedPolygons.get(j).setTexture( _curTextures.get(safeIndex).texture() );
		}
	}
	
	public void setAllPolygonsToSameRandomTexture() {
		setAllPolygonsToTexture( MathUtil.randRange(0,_curTextures.size() - 1 ) ); 
	}
	
	public void setAllPolygonsToPoolTexture() {
		setAllPolygonsToTexture( MathUtil.randRange(0,_curTextures.size() - 1 ) ); 
	}

	public void setAllPolygonsTextureStyle( int textureStyle ) {
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).setTextureStyle( textureStyle );
		}
	}
	
	public void draw() {
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).draw(p.g);
		}
	}
	
	public void getAudioPixelColor() {
		_colorEase.update();
	}
	
	public int colorEaseInt() {
		return _colorEase.colorInt();
	}

	public void pulseColor() {
		 //int curColor = _colorEase.targetInt();
		 _colorEase.setCurrentColorInt( P.p.color(0) );
	}

	public void drawOverlay() {
		_meshLines.update();
		if( _meshLines.mode() == MODE.MODE_PARTICLES) {
			_meshParticles.update();
		} else if( _meshLines.mode() == MODE.MODE_SEGMENT_SCANNERS) {
			_meshScanners.update();
		}
	}

	// update things for the group! ------------------------------------------
	public void newMode() {
		// make some decisions on how to make the group awesome
		// sometimes multiple groups should do the same larger things...

		for( int i=0; i < _curTextures.size(); i++ ) {
			_curTextures.get(i).newMode();
		}
	}

	public void traverseStart() {
		if(MathUtil.randBoolean(p) == false) return;
		_traverseFrame = 0;
		_traverseMode = MathUtil.randRange(0, 1);
		_traverseWireMode = MathUtil.randRange(0, 1);
		_traversePolygon = randomPolygon();
		_traversePolygonLast = _traversePolygon;
	}
	
	public void traverseUpdate() {
		if(_traversePolygon == null) return;
		if(_traverseFrame > 20) return;
		_traverseFrame++;

		IMappedPolygon newNeighbor = _traversePolygon.getRandomNeighbor();
		int tries = 0;
		while(newNeighbor == _traversePolygonLast && tries < 5) {
			newNeighbor = _traversePolygon.getRandomNeighbor();
			tries++;
		}
		_traversePolygonLast = _traversePolygon;
		_traversePolygon = newNeighbor;
		_traversePolygon.setFlash(_traverseMode, _traverseWireMode);
	}
	
	public int randomColor() {
		return p.color(p.random(180,255), p.random(180,255), p.random(180,255), 255f );
	}
	
	public void newAudioPixelColor() {
		int groupColor = randomColor();
		_colorEase.setTargetColorIntWithBrightnessAndSaturation( groupColor, 0.5f );
	}
	
	public void newColor() {
		// give textures a new random color
		for( int i=0; i < _curTextures.size(); i++ ) {
			_curTextures.get(i).setColor( randomColor() );
		}
		int groupColor = randomColor();
		_meshLines.setColor( groupColor );
		_meshParticles.setColor( groupColor );
		_meshScanners.setColor( groupColor );
//		_colorEase.setTargetColorIntWithBrightnessAndSaturation( groupColor, 0.5f );
		
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).setColor( randomColor() );
		}
	}

	public void newLineMode() {
		for( int i=0; i < _curTextures.size(); i++ ) {
			_curTextures.get(i).newLineMode();
		}
		_meshLines.updateLineMode();
		_meshParticles.updateLineMode();
	}

	public void resetLineModeToIndex( int index ) {
		_meshLines.resetLineMode( index );
		_meshParticles.resetLineMode( index );
	}
	
	public void newRotation() {
		for( int i=0; i < _curTextures.size(); i++ ) {
			_curTextures.get(i).newRotation();
		}
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).rotateTexture();
		}
	}

	public void newRandomRotation() {
		_curTextures.get(MathUtil.randRange(0, _curTextures.size()-1)).newRotation();
		_mappedPolygons.get(MathUtil.randRange(0, _mappedPolygons.size()-1)).rotateTexture();
	}
	
	public void resetRotation() {
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).rotateTexture();
		}
	}
	
}
