package com.haxademic.core.image.filters;


import java.util.ArrayList;

import processing.core.PGraphics;
import processing.core.PImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.image.ImageUtil;

public class ImageHistogramFilter {
	
	protected PAppletHax p;
	protected int _width;
	protected int _height;
	protected int _rowSize;
	protected PGraphics _pg;
	protected PImage _image;
	protected ArrayList<ColorAndCount> _colors;
	
	protected float RGB_DIFF_THRESH = 0.04f;

	
	public ImageHistogramFilter( int width, int height, int rowSize ) {
		p = (PAppletHax) P.p;
		_width = width;
		_height = height;
		_rowSize = rowSize;
		_image = p.createImage( _width, _height, P.ARGB );
		_pg = p.createGraphics( _width, _height, P.P3D );
		_colors = new ArrayList<ColorAndCount>();
	}
	
	public PImage pg() {
		return _pg;
	}
	
	public PImage updateWithPImage( PImage source ) {
		drawPixels( source );
		return _image;
	}
	
	protected void drawPixels( PImage source ) {
		_pg.beginDraw();
		ImageUtil.clearPGraphics( _pg );
		DrawUtil.setDrawCorner(p);
		_pg.noStroke();
		_pg.fill(0,0);
		
		// clear barely-used colors, and reset counts
	    for( int i=0; i < _colors.size(); i++ ) {
			if( _colors.get(i).count < 10 ) _colors.remove(i); 
			else _colors.get(i).count = 0;
	    }
		
	    // analyze image's pixels and try to add the colors
		for( int x=0; x < source.width; x += _rowSize ) {
			for( int y=0; y < source.height; y += _rowSize ) {
				addColorToHistogram( ImageUtil.getPixelColor( source, x, y ) );
			}
		}
		
		// draw histogram bars
//		int colorW = P.ceil( _width / _colors.size() );
//		for( int i=0; i < _colors.size(); i++ ) {
//			_pg.fill( _colors.get(i).color );
//			_pg.rect( i*colorW, 0, colorW, _colors.get(i).count * 100f );  
//		}
		
		// redraw image with histogram colors
		for( int x=0; x < source.width; x += _rowSize ) {
			for( int y=0; y < source.height; y += _rowSize ) {
				_pg.fill( getClosestColorInHistogram( ImageUtil.getPixelColor( source, x, y ) ) );
				_pg.rect( x, y, _rowSize, _rowSize );
			}
		}
		_pg.endDraw();
		
		_image.copy( _pg, 0, 0, _width, _height, 0, 0, _width, _height );
	}
	
	protected void addColorToHistogram( int color ) {
		// if we find a similar color in the collection, increment it
	    boolean similarColorFound = false;
	    for( int i=0; i < _colors.size(); i++ ) {
	      if( !similarColorFound && ImageUtil.colorDifference( p, color, _colors.get(i).color ) < RGB_DIFF_THRESH ) {
	        similarColorFound = true;
	        _colors.get(i).count++;
	        break;
	      }
	    }
	    
	    // if the color has no matches, add to the array
	    if( !similarColorFound ) {
	    	_colors.add( new ColorAndCount( color ) );
	    }
	}
	
	protected int getClosestColorInHistogram( int color ) {
		// if we find a similar color in the collection, increment it
	    for( int i=0; i < _colors.size(); i++ ) {
	      if( ImageUtil.colorDifference( p, color, _colors.get(i).color ) < RGB_DIFF_THRESH ) {
	        return _colors.get(i).color;
	      }
	    }
	    return 0;
	}
	
	
	public class ColorAndCount {
		
		public int count = 1;
		public int color;
		
		public ColorAndCount( int color ) {
			this.color = color;
		}
	}
}


