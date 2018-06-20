//package com.haxademic.app.haxvisual.viz.modules;
//
//import toxi.color.ColorList;
//import toxi.color.ColorRange;
//import toxi.color.TColor;
//import toxi.color.theory.ColorTheoryStrategy;
//import toxi.color.theory.CompoundTheoryStrategy;
//
//import com.haxademic.app.haxvisual.viz.IVizModule;
//import com.haxademic.app.haxvisual.viz.VizCollection;
//import com.haxademic.app.haxvisual.viz.elements.LinesEQ;
//import com.haxademic.app.haxvisual.viz.elements.OuterSphere;
//import com.haxademic.app.haxvisual.viz.elements.SphereTextureLines;
//import com.haxademic.app.haxvisual.viz.elements.WaveformPlane;
//import com.haxademic.app.haxvisual.viz.elements.WaveformSingle;
//import com.haxademic.core.draw.color.ColorGroup;
//
//public class BrimLiski 
//extends VizCollection
//implements IVizModule {
//	
//	public BrimLiski() {
//		super();
//	}
//	
//	public void addElements() {
////		_fgElements.add( new MeshDeform( p, toxi, _audioData ) );
//		_fgElements.add( new WaveformSingle( p, toxi, audioData ) );
//		_fgElements.add( new WaveformPlane( p, toxi, audioData ) );
//		SphereTextureLines sphereLinesSmall = new SphereTextureLines( p, toxi, audioData );
//		sphereLinesSmall.setDrawProps( 150, 0.2f );
//		_fgElements.add( sphereLinesSmall );
//
//		_bgElements.add( new LinesEQ( p, toxi, audioData ) );
//		_bgElements.add( new OuterSphere( p, toxi, audioData ) );
//
////		_ambientElements.add( new SphereClouds( p, toxi, _audioData ) );
//
//		SphereTextureLines sphereLines = new SphereTextureLines( p, toxi, audioData );
//		sphereLines.setDrawProps( 2500, 1f );
//		_outerElements.add( sphereLines );
//	}
//	
//	protected void pickNewColors() {
//		float lighten = 0.4f;
//
//		// get a single strategy
//		TColor color = ColorRange.LIGHT.getColor();
//		ColorTheoryStrategy strategy = new CompoundTheoryStrategy ();
//		_colorList = ColorList.createUsingStrategy(strategy, color);
//
//		// store a few random colors
//		_colorFG1 = _colorList.get( 0 );
//		_colorFG2 = _colorList.get( 1 );//_colorFG1.getAnalog(45,1);//_colorList.get( 1 );//.getRandom();	// color1.complement().toARGB()
//		_colorAmbient = _colorList.get( 2 );//_colorFG2.getAnalog(45,1);//_colorList.get( 2 );
//		_colorBG1 = _colorList.get( 3 );//_colorAmbient.getAnalog(45,1);//_colorList.get( 3 );
//		_colorBG2 = _colorList.get( 4 );//_colorBG1.getAnalog(45,1);//_colorList.get( 4 );
//
//		_colorFG1.adjustRGB( lighten, lighten, lighten );
//		_colorFG2.adjustRGB( lighten, lighten, lighten );
//		_colorAmbient.adjustRGB( lighten, lighten, lighten );
//		_colorBG1.adjustRGB( lighten, lighten, lighten );
//		_colorBG2.adjustRGB( lighten, lighten, lighten );
//
//		_balletColors = new ColorGroup( -1 );
//		_balletColors.createGroupWithTColors( _colorFG1, _colorFG2, _colorAmbient, _colorBG1, _colorBG2 );
//
//		_colorFG1.setBrightness( lighten ).lighten( lighten );
//		_colorFG2.setBrightness( lighten ).lighten( lighten );
//		_colorBG1.setBrightness( lighten ).lighten( lighten );
//		_colorBG2.setBrightness( lighten ).lighten( lighten );
//		_colorAmbient.setBrightness( lighten ).lighten( lighten );
//		
//		storeCurColors();
//	}
//
//	public void update() {
//		super.update();
//	}
//
//	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount ) {
//		super.beatDetect( isKickCount, isSnareCount, isHatCount, isOnsetCount );
//	}
//}
