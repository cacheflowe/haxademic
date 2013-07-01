package com.haxademic.app.haxvisual.viz.modules;

import toxi.color.ColorList;
import toxi.color.ColorRange;
import toxi.color.TColor;
import toxi.color.theory.ColorTheoryStrategy;
import toxi.color.theory.CompoundTheoryStrategy;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.VizCollection;
import com.haxademic.app.haxvisual.viz.elements.BarsEQ;
import com.haxademic.app.haxvisual.viz.elements.GridEQ;
import com.haxademic.app.haxvisual.viz.elements.LinesEQ;
import com.haxademic.app.haxvisual.viz.elements.MeshDeform;
import com.haxademic.app.haxvisual.viz.elements.ObjMesh;
import com.haxademic.app.haxvisual.viz.elements.OuterSphere;
import com.haxademic.app.haxvisual.viz.elements.RotatingRings;
import com.haxademic.app.haxvisual.viz.elements.RotatorShapes;
import com.haxademic.app.haxvisual.viz.elements.SphereClouds;
import com.haxademic.app.haxvisual.viz.elements.SphericalHarmonicsOscillator;
import com.haxademic.app.haxvisual.viz.elements.WaveformPlane;
import com.haxademic.app.haxvisual.viz.elements.WaveformShapes;
import com.haxademic.core.draw.color.ColorGroup;

public class KaraokeViz
extends VizCollection
implements IVizModule {
	
	public KaraokeViz() {
		super();
	}
	
	public void addElements() {
		_fgElements.add( new MeshDeform( p, toxi, _audioData ) );
		_fgElements.add( new WaveformPlane( p, toxi, _audioData ) );
		_fgElements.add( new WaveformShapes( p, toxi, _audioData ) );
		_fgElements.add( new RotatorShapes( p, toxi, _audioData ) );
		_fgElements.add( new SphericalHarmonicsOscillator( p, toxi, _audioData ) );
		
		_bgElements.add( new RotatingRings( p, toxi, _audioData ) );
//		_bgElements.add( new BarsEQ( p, toxi, _audioData ) );
		_bgElements.add( new LinesEQ( p, toxi, _audioData ) );
		_bgElements.add( new GridEQ( p, toxi, _audioData ) );

		_ambientElements.add( new SphereClouds( p, toxi, _audioData ) );

		_outerElements.add( new OuterSphere( p, toxi, _audioData ) );
	}
	
	protected void pickNewColors() {
		float lighten = 0.4f;

		if( 1 == 1 ) {
			// get a single strategy
			TColor color = ColorRange.LIGHT.getColor();
			ColorTheoryStrategy strategy = new CompoundTheoryStrategy ();
//		TColor color = ColorRange.BRIGHT.getColor();
//		ColorTheoryStrategy strategy = new RightSplitComplementaryStrategy();
			_colorList = ColorList.createUsingStrategy(strategy, color);
			
			// store a few random colors
//		TColor color1 = _colorList.getRandom();
//		color1.lighten(0.3f);
			_colorFG1 = _colorList.get( 0 );
			_colorFG2 = _colorList.get( 1 );//_colorFG1.getAnalog(45,1);//_colorList.get( 1 );//.getRandom();	// color1.complement().toARGB()
			_colorAmbient = _colorList.get( 2 );//_colorFG2.getAnalog(45,1);//_colorList.get( 2 );
			_colorBG1 = _colorList.get( 3 );//_colorAmbient.getAnalog(45,1);//_colorList.get( 3 );
			_colorBG2 = _colorList.get( 4 );//_colorBG1.getAnalog(45,1);//_colorList.get( 4 );
			
			_colorFG1.adjustRGB( lighten, lighten, lighten );
			_colorFG2.adjustRGB( lighten, lighten, lighten );
			_colorAmbient.adjustRGB( lighten, lighten, lighten );
			_colorBG1.adjustRGB( lighten, lighten, lighten );
			_colorBG2.adjustRGB( lighten, lighten, lighten );

//			_colorFG1.lighten( lighten );
//			_colorFG2.lighten( lighten );
//			_colorAmbient.lighten( lighten );
//			_colorBG1.lighten( lighten );
//			_colorBG2.lighten( lighten );
			
//			if( _balletColors == null ) {
				_balletColors = new ColorGroup( -1 );
				_balletColors.createGroupWithTColors( _colorFG1, _colorFG2, _colorAmbient, _colorBG1, _colorBG2 );
//			}
		} else {
			if( _balletColors == null ) {
				_balletColors = new ColorGroup( ColorGroup.BALLET );
			}
			_balletColors.setRandomGroup();
			_colorFG1 = _balletColors.getColorFromIndex( 0 );
			_colorFG2 = _balletColors.getColorFromIndex( 1 );//_colorFG1.getAnalog(45,1);//_colorList.get( 1 );//.getRandom();	// color1.complement().toARGB()
			_colorAmbient = _balletColors.getColorFromIndex( 2 );//_colorFG2.getAnalog(45,1);//_colorList.get( 2 );
			_colorBG1 = _balletColors.getColorFromIndex( 3 );//_colorAmbient.getAnalog(45,1);//_colorList.get( 3 );
			_colorBG2 = _balletColors.getColorFromIndex( 4 );//_colorBG1.getAnalog(45,1);//_colorList.get( 4 );
		}
		
		_colorFG1.setBrightness( lighten ).lighten( lighten );
		_colorFG2.setBrightness( lighten ).lighten( lighten );
		_colorBG1.setBrightness( lighten ).lighten( lighten );
		_colorBG2.setBrightness( lighten ).lighten( lighten );
		_colorAmbient.setBrightness( lighten ).lighten( lighten );
		
		storeCurColors();
	}

	public void update() {
		super.update();
	}

	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount ) {
		super.beatDetect( isKickCount, isSnareCount, isHatCount, isOnsetCount );
	}
}
