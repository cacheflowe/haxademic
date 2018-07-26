package com.haxademic.app.haxvisual.viz.modules;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.VizCollection;
import com.haxademic.app.haxvisual.viz.elements.BarsModEQ;
import com.haxademic.app.haxvisual.viz.elements.GridEQ;
import com.haxademic.app.haxvisual.viz.elements.LinesEQ;
import com.haxademic.app.haxvisual.viz.elements.MeshDeform;
import com.haxademic.app.haxvisual.viz.elements.RotatorShapes;
import com.haxademic.app.haxvisual.viz.elements.SphereClouds;
import com.haxademic.app.haxvisual.viz.elements.SphereTextureLines;
import com.haxademic.app.haxvisual.viz.elements.StarField;
import com.haxademic.core.draw.color.ColorGroup;

import toxi.color.ColorList;
import toxi.color.ColorRange;
import toxi.color.TColor;
import toxi.color.theory.ColorTheoryStrategy;
import toxi.color.theory.CompoundTheoryStrategy;

public class AmbientViz
extends VizCollection
implements IVizModule {

	public AmbientViz() {
		super();
	}

	public void addElements() {
//		_fgElements.add( new WaveformShapes( p, toxi ) );
		_fgElements.add( new RotatorShapes( p, toxi ) );
		_fgElements.add( new MeshDeform( p, toxi ) );
		_fgElements.add( new LinesEQ( p, toxi ) );

		_bgElements.add( new BarsModEQ( p, toxi ) );
		_bgElements.add( new GridEQ( p, toxi ) );

		_ambientElements.add( new SphereClouds( p, toxi ) );
		_ambientElements.add( new StarField( p, toxi ) );

//		_outerElements.add( new OuterSphere( p, toxi, _audioData ) );
		SphereTextureLines sphereLines = new SphereTextureLines( p, toxi );
		sphereLines.setDrawProps( 10000, 1f );
		_outerElements.add( sphereLines );

	}

	protected void pickNewColors() {
		float lighten = 0.4f;

		// get a single strategy
		TColor color = ColorRange.LIGHT.getColor();
		ColorTheoryStrategy strategy = new CompoundTheoryStrategy ();
		//		TColor color = ColorRange.BRIGHT.getColor();
		//		ColorTheoryStrategy strategy = new RightSplitComplementaryStrategy();
		_colorList = ColorList.createUsingStrategy(strategy, color);

		// store a few random colors
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

		_balletColors = new ColorGroup( -1 );
		_balletColors.createGroupWithTColors( _colorFG1, _colorFG2, _colorAmbient, _colorBG1, _colorBG2 );

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
