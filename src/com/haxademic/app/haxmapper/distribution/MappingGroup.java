package com.haxademic.app.haxmapper.distribution;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import com.haxademic.app.haxmapper.HaxMapper;
import com.haxademic.app.haxmapper.overlays.MeshLines;
import com.haxademic.app.haxmapper.overlays.MeshLines.MODE;
import com.haxademic.app.haxmapper.overlays.MeshParticles;
import com.haxademic.app.haxmapper.overlays.MeshSegmentScanners;
import com.haxademic.app.haxmapper.polygons.IMappedPolygon;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class MappingGroup {

	protected PApplet p;
	protected PGraphics overlayPG;
	protected Rectangle mappingBounds;
	protected ArrayList<IMappedPolygon> _mappedPolygons;
	protected ArrayList<BaseTexture> _curTextures;
	protected MeshLines _meshLines;
	protected MeshParticles _meshParticles;
	protected MeshSegmentScanners _meshScanners;
	protected int _color;
	
	protected int _traverseFrame = 0;
	protected int _traverseMode = 0;
	protected int _traverseWireMode = 0;
	protected IMappedPolygon _traversePolygon;
	protected IMappedPolygon _traversePolygonLast;
	
	protected Point center = null;

	public MappingGroup( PAppletHax p, PGraphics overlayPG, Rectangle mappingBounds ) {
		this.p = p;
		this.overlayPG = overlayPG;
		this.mappingBounds = mappingBounds;
		_curTextures = new ArrayList<BaseTexture>();
		_mappedPolygons = new ArrayList<IMappedPolygon>();
		_meshLines = new MeshLines( overlayPG );
		_meshParticles = new MeshParticles( overlayPG );
		_color = P.p.color(255);
	}

	/////////////////////////////////////////////////////////////////
	// Build polygon, find neighbors, draw mask
	/////////////////////////////////////////////////////////////////


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
	
	public void drawShapeForMask(PGraphics maskPG) {
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).rawDrawPolygon(maskPG);
		}
	}
	
	/////////////////////////////////////////////////////////////////
	// Cycle textures
	/////////////////////////////////////////////////////////////////

	public boolean isUsingTexture(BaseTexture texture) {
		for(int i=0; i < _mappedPolygons.size(); i++ ) {
			if(_mappedPolygons.get(i).getTexture() == texture) return true;
		}
		return false;
	}
	
	protected BaseTexture getRandomTexture() {
		return _curTextures.get( MathUtil.randRange(0, _curTextures.size() - 1) );
	}

	public void pushTexture( BaseTexture texture, ArrayList<BaseTexture> activeTextures ) {
		// if group already has texture, move it to the end of the list, so we can shift the first off
		if(_curTextures.contains(texture) == true) _curTextures.remove(texture);
		_curTextures.add(texture);
		// remove any textures that aren't in the app's current active texture pool
		for(int i = _curTextures.size() - 1; i >= 0; i-- ) {
			if(activeTextures.contains(_curTextures.get(i)) == false) {
				_curTextures.remove(i);
			}
		}
		if( _curTextures.size() > HaxMapper.MAX_ACTIVE_TEXTURES_PER_GROUP ) _curTextures.remove(0);
		refreshTexturesForPolygons();
		// debugLogGroupTextures();
	}
	
	public void refreshTexturesForPolygons() {
		if( _curTextures.size() < 1 ) return;
		// remove inactive textures from local texture pool
		for(int i = _curTextures.size() - 1; i >= 0; i-- ) {
			if(_curTextures.get(i).isActive() == false) _curTextures.remove(i);
		}
		// make sure polygons that are no longer have an active texture switch out for a random group texture  
		for(int i=0; i < _mappedPolygons.size(); i++ ) {
			BaseTexture curPolyTexture = _mappedPolygons.get(i).getTexture();
			if(curPolyTexture == null || _curTextures.indexOf(curPolyTexture) == -1) {		// || _curTextures.indexOf(curPolyTexture) == -1
				BaseTexture newTex = getRandomTexture();
				_mappedPolygons.get(i).setTexture( newTex, mappingBounds );
			}
		}
	}

	public void pushTextureFront( BaseTexture texture ) {
		if( _curTextures.indexOf( texture ) == -1 ) {
			_curTextures.add(0, texture);
		}
	}
	
	public void debugLogGroupTextures() {
		P.println("%%% _curTextures ===============");
		for(int j = 0; j < _curTextures.size(); j++) {
			P.println(""+_curTextures.get(j).toString());
		}
		P.println("%%% end AFTER ===============");
	}

//	public BaseTexture shiftTexture() {
////		if( _curTextures.size() > HaxMapper.MAX_ACTIVE_TEXTURES ) {
//	}

	/////////////////////////////////////////////////////////////////
	// Texture-level post-processing effects
	/////////////////////////////////////////////////////////////////


	public void clearAllTextures() {
		_curTextures.clear();
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
		randomPolygon().setTexture( randomBaseTexture(), mappingBounds );
	}
	
	public void setAllPolygonsToTexture( int textureIndex ) {
		if( _curTextures.size() == 0 ) return;
//		P.println("setAllPolygonsToTexture()", textureIndex);
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).setTexture( _curTextures.get(textureIndex), mappingBounds );
		}
		refreshTexturesForPolygons();
	}
	
	public void setAllPolygonsToSameRandomTexture() {
		setAllPolygonsToTexture( MathUtil.randRange(0,_curTextures.size() - 1 ) ); 
	}
	
	public void setAllPolygonsTextureStyle( int textureStyle ) {
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).setTextureStyle( textureStyle );
		}
	}
	
	public void draw() {
		traverseUpdate();
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).draw(p.g);
		}
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
		while(newNeighbor == _traversePolygonLast && tries < 10) {
			newNeighbor = _traversePolygon.getRandomNeighbor();
			tries++;
		}
		_traversePolygonLast = _traversePolygon;
		_traversePolygon = newNeighbor;
		if(_traversePolygon != null) {
			_traversePolygon.setFlash(_traverseMode, _traverseWireMode);
		}
	}
	
	public int randomColor() {
		return p.color(p.random(180,255), p.random(180,255), p.random(180,255), 255f );
	}
	
	public void newColor() {
		// give textures, particles & scanners a new random color
		for( int i=0; i < _curTextures.size(); i++ ) {
			_curTextures.get(i).setColor( randomColor() );
		}
		int groupColor = randomColor();
		_meshLines.setColor( groupColor );
		_meshParticles.setColor( groupColor );
		_meshScanners.setColor( groupColor );
		
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
	
	public void rotateAllTexturesAndPolygons() {
		for( int i=0; i < _curTextures.size(); i++ ) {
			_curTextures.get(i).newRotation();
		}
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).rotateTexture();
		}
	}

	public void newRotation() {
		_mappedPolygons.get(MathUtil.randRange(0, _mappedPolygons.size()-1)).rotateTexture();
	}
	
	public void resetRotation() {
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).resetRotation();
		}
	}
	
}
