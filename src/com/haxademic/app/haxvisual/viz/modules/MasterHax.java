package com.haxademic.app.haxvisual.viz.modules;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.VizCollection;
import com.haxademic.app.haxvisual.viz.elements.AppFrame2d;
import com.haxademic.app.haxvisual.viz.elements.BarsEQ2d;
import com.haxademic.app.haxvisual.viz.elements.BarsModEQ;
import com.haxademic.app.haxvisual.viz.elements.BlobSheetElement;
import com.haxademic.app.haxvisual.viz.elements.BwMotionIllusion;
import com.haxademic.app.haxvisual.viz.elements.BwShaderBg;
import com.haxademic.app.haxvisual.viz.elements.FireTunnelShader;
import com.haxademic.app.haxvisual.viz.elements.GlowwaveShader;
import com.haxademic.app.haxvisual.viz.elements.GridEQ;
import com.haxademic.app.haxvisual.viz.elements.KinectMeshHead;
import com.haxademic.app.haxvisual.viz.elements.LinesEQ;
import com.haxademic.app.haxvisual.viz.elements.MeshDeform;
import com.haxademic.app.haxvisual.viz.elements.OuterSphere;
import com.haxademic.app.haxvisual.viz.elements.RotatingRings;
import com.haxademic.app.haxvisual.viz.elements.RotatorShapes;
import com.haxademic.app.haxvisual.viz.elements.SphereClouds;
import com.haxademic.app.haxvisual.viz.elements.SphericalHarmonicsOscillator;
import com.haxademic.app.haxvisual.viz.elements.StarField;
import com.haxademic.app.haxvisual.viz.elements.SvgPattern2d;
import com.haxademic.app.haxvisual.viz.elements.VideoScrubberLayer;
import com.haxademic.app.haxvisual.viz.elements.WarpedImagesBackdrop;
import com.haxademic.app.haxvisual.viz.elements.WaveformPlane;
import com.haxademic.app.haxvisual.viz.elements.WaveformShapes;
import com.haxademic.app.haxvisual.viz.elements.WaveformSingle;
import com.haxademic.core.draw.color.ColorGroup;

public class MasterHax 
extends VizCollection
implements IVizModule {
	
	public MasterHax() {
		super();
	}

	public void addElements() {

//		_fgElements.add( new Invaders( p, toxi, _audioData ) );
		_fgElements.add( new WaveformSingle( p, toxi, _audioData ) );
		_fgElements.add( new WaveformPlane( p, toxi, _audioData ) );
		_fgElements.add( new WaveformShapes( p, toxi, _audioData ) );
		_fgElements.add( new RotatorShapes( p, toxi, _audioData ) );
		_fgElements.add( new MeshDeform( p, toxi, _audioData ) );
//		_fgElements.add( new ObjMesh( p, toxi, _audioData ) );
//		SphereTextureLines sphereLinesSmall = new SphereTextureLines( p, toxi, _audioData );
//		sphereLinesSmall.setDrawProps( 150, 0.2f );
//		_fgElements.add( sphereLinesSmall );
//		_fgElements.add( new CacheLogo( p, toxi, _audioData ) );
		_fgElements.add( new SphericalHarmonicsOscillator( p, toxi, _audioData ) );
//		_fgElements.add( new KinectMesh( p, toxi, _audioData, p.kinectWrapper ) );
		
		_bgElements.add( new VideoScrubberLayer( p, toxi, _audioData ) );
		_bgElements.add( new GlowwaveShader( p, toxi, _audioData ) );
		_bgElements.add( new BwMotionIllusion( p, toxi, _audioData ) );
		_bgElements.add( new BwShaderBg( p, toxi, _audioData ) );
		_bgElements.add( new BarsModEQ( p, toxi, _audioData ) );
		_bgElements.add( new RotatingRings( p, toxi, _audioData ) );
		_bgElements.add( new FireTunnelShader( p, toxi, _audioData ) );
		_bgElements.add( new BlobSheetElement( p, toxi, _audioData ) );
////		_bgElements.add( new BarsEQ( p, toxi, _audioData ) );
		_bgElements.add( new LinesEQ( p, toxi, _audioData ) );
		_bgElements.add( new WarpedImagesBackdrop( p, toxi, _audioData ) );
		_bgElements.add( new GridEQ( p, toxi, _audioData ) );
		
		_ambientElements.add( new SphereClouds( p, toxi, _audioData ) );
		_ambientElements.add( new StarField( p, toxi, _audioData ) );
		
//		_2dElements.add( new SvgPattern2d( p, toxi, _audioData ) );
//		_2dElements.add( new WordsOverlay2d( p, toxi, _audioData ) );
		
		_frame2dElements.add( new BarsEQ2d( p, toxi, _audioData ) );
		_frame2dElements.add( new AppFrame2d( p, toxi, _audioData ) );
		_frame2dElements.add( new SvgPattern2d( p, toxi, _audioData ) );
//		_frame2dElements.add( new WordsOverlay2d( p, toxi, _audioData ) );

		if( p.kinectWrapper != null ) _kinectElements.add( new KinectMeshHead( p, toxi, _audioData, p.kinectWrapper ) );
		
		_outerElements.add( new OuterSphere( p, toxi, _audioData ) );
//		SphereTextureLines sphereLines = new SphereTextureLines( p, toxi, _audioData );
//		sphereLines.setDrawProps( 4000, 5f );
//		_outerElements.add( sphereLines );

	}
	
	protected void pickNewColors() {

		if( _balletColors == null ) _balletColors = new ColorGroup( ColorGroup.NEON );
		_balletColors.setRandomGroup();
		_colorFG1 = _balletColors.getColorFromIndex( 0 );
		_colorFG2 = _balletColors.getColorFromIndex( 1 );
		_colorAmbient = _balletColors.getColorFromIndex( 2 );
		_colorBG1 = _balletColors.getColorFromIndex( 3 );
		_colorBG2 = _balletColors.getColorFromIndex( 4 );
		
		storeCurColors();
	}
	

	
}