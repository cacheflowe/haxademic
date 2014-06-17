package com.haxademic.app.haxmapper.distribution;

import java.awt.Point;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;

import com.haxademic.app.haxmapper.overlays.MeshLines;
import com.haxademic.app.haxmapper.polygons.IMappedPolygon;
import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.math.MathUtil;

public class MappingGroup {

	protected PApplet p;
	protected ArrayList<IMappedPolygon> _mappedPolygons;
	protected ArrayList<BaseTexture> _curTextures;
	protected MeshLines _meshLines;
	protected int _color;
	protected ColorHaxEasing _colorEase;
	protected int _textureIndex = 0;
	protected IMappedPolygon _selectedPolygon;
	protected float _selectedPolygonTextureX = 0;
	protected float _selectedPolygonTextureY = 0;
	protected Point center = null;

	public MappingGroup( PAppletHax p, PGraphics overlayPG ) {
		this.p = p;
		_curTextures = new ArrayList<BaseTexture>();
		_mappedPolygons = new ArrayList<IMappedPolygon>();
		_meshLines = new MeshLines( overlayPG );
		_color = P.p.color(255);
		_colorEase = new ColorHaxEasing( "#000000", 4 );
	}

	public void addPolygon( IMappedPolygon polygon ) {
		_mappedPolygons.add(polygon);
	}

	public void addMeshSegment( float x1, float y1, float x2, float y2 ) {
		_meshLines.addSegment( x1, y1, x2, y2 );
	}

	public void pushTexture( BaseTexture texture ) {
		if( _curTextures.indexOf( texture ) == -1 ) {
			_curTextures.add(texture);
		}
	}

	public BaseTexture shiftTexture() {
		if( _curTextures.size() > 0 ) {
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

	public void setAllPolygonsTextureStyle( int textureStyle ) {
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).setTextureStyle( textureStyle );
		}
	}
	
	public void draw() {
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			IMappedPolygon triangle = _mappedPolygons.get(j);
			triangle.draw(p.g);
		}
	}
	
	public void getAudioPixelColor() {
		// use a pixel for audiopixel
//		if( _selectedPolygon == null ) return;
//		center = _selectedPolygon.getCenter();
//		int color = ImageUtil.getPixelColor( P.p, center.x, center.y );
//		P.println(_selectedPolygon, color, center.x, center.y);
//		_colorEase.setTargetColorIntWithBrightnessAndSaturation( color, 1.2f );

		//		PGraphics texture = _mappedPolygons.get( _selectedPolygonIndex ).getTexture();
//		if( texture != null ) {
//			P.println(Math.round(_selectedPolygonTextureX * texture.width), Math.round(_selectedPolygonTextureY * texture.height));
//			P.println(_selectedPolygonIndex, ImageUtil.getPixelColor( texture, Math.round(_selectedPolygonTextureX * texture.width), Math.round(_selectedPolygonTextureY * texture.height) ) );
//			texture.loadPixels();
//			P.println( texture.pixels[0] );
//			_colorEase.setTargetColorIntWithBrightnessAndSaturation( ImageUtil.getPixelColor( texture, Math.round(_selectedPolygonTextureX * texture.width), Math.round(_selectedPolygonTextureY * texture.height) ), 0.5f );
//			_colorEase.setTargetColorIntWithBrightnessAndSaturation( ImageUtil.getPixelColor( texture, Math.round(_selectedPolygonTextureX * texture.width), Math.round(_selectedPolygonTextureY * texture.height) ), 0.5f );
//		}
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
	}

	// update things for the group! ------------------------------------------
	public void newMode() {
		// make some decisions on how to make the group awesome
		// sometimes multiple groups should do the same larger things...

		for( int i=0; i < _curTextures.size(); i++ ) {
			_curTextures.get(i).newMode();
		}
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
//		_colorEase.setTargetColorIntWithBrightnessAndSaturation( groupColor, 0.5f );
		_selectedPolygon = randomPolygon();
		_selectedPolygonTextureX = MathUtil.randRangeDecimal(0f, 1f);
		_selectedPolygonTextureY = MathUtil.randRangeDecimal(0f, 1f);
		
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).setColor( randomColor() );
		}
	}

	public void newLineMode() {
		for( int i=0; i < _curTextures.size(); i++ ) {
			_curTextures.get(i).newLineMode();
		}
		_meshLines.updateLineMode();
	}

	public void resetLineModeToIndex( int index ) {
		_meshLines.resetLineMode( index );
	}
	
	public void newRotation() {
		for( int i=0; i < _curTextures.size(); i++ ) {
			_curTextures.get(i).newRotation();
		}
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).rotateTexture();
		}
	}

	public void resetRotation() {
		for(int j=0; j < _mappedPolygons.size(); j++ ) {
			_mappedPolygons.get(j).rotateTexture();
		}
	}
	
}
