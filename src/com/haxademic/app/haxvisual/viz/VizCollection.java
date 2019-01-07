package com.haxademic.app.haxvisual.viz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.camera.CameraDefault;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.math.MathUtil;

import toxi.color.ColorList;
import toxi.color.ColorRange;
import toxi.color.TColor;
import toxi.color.theory.ColorTheoryRegistry;
import toxi.color.theory.ColorTheoryStrategy;

public class VizCollection 
extends ModuleBase
implements IVizModule
{
	// class props
	protected int _numAverages = 32;
	
	protected ColorList _colorList;
	protected TColor _colorFG1;
	protected TColor _colorFG2;
	protected TColor _colorAmbient;
	protected TColor _colorBG1;
	protected TColor _colorBG2;
	
	protected IVizElement _outerElement = null;
	protected IVizElement _bgElement = null;
	protected IVizElement _fgElement = null;
	protected IVizElement _ambientElement = null;
	protected IVizElement _2dElement = null;
	protected IVizElement _frame2dElement = null;
	protected IVizElement _kinectElement = null;
	
	protected Vector<IVizElement> _bgElements;
	protected Vector<IVizElement> _fgElements;
	protected Vector<IVizElement> _ambientElements;
	protected Vector<IVizElement> _outerElements;
	protected Vector<IVizElement> _2dElements;
	protected Vector<IVizElement> _frame2dElements;
	protected Vector<IVizElement> _kinectElements;
	
	protected ColorGroup _balletColors;
	
	protected int _numBigChanges = 0;
	protected boolean _isAutoPilot = false;
	protected boolean _isStressTesting = false;
	
	protected float _curCameraZ = 0;
	
	public VizCollection() {
		super();
//		initAudio();
		init();
	}

	public void init() {
		_curCamera = new CameraDefault( p, 0, 0, (int)_curCameraZ );
		_curCamera.reset();
		_curCamera.setTarget( 0, 0, 0 );

		_bgElements = new Vector<IVizElement>();
		_fgElements = new Vector<IVizElement>();
		_ambientElements = new Vector<IVizElement>();
		_outerElements = new Vector<IVizElement>();
		_2dElements = new Vector<IVizElement>();
		_frame2dElements = new Vector<IVizElement>();
		_kinectElements = new Vector<IVizElement>();
		
		addElements();
		pickElements();
		pickNewColors();
		pickMode();
	}
	
	public void addElements() {
		P.println("make sure to override addElements()");
	}

//	public void initAudio() {
//		audioData.setNumAverages( _numAverages );
//		audioData.setDampening( .13f );
//	}

	public void focus() {
//		initAudio();
	}

	public void update() {
		// start drawing at center
		DrawUtil.resetGlobalProps( p );
		
		// clear screen and set camera
		p.background(0,0,0,255f);
				
		// set camera
		_curCameraZ = MathUtil.easeTo(_curCameraZ, 0, 10);
		_curCamera.setPosition(0, 0, (int)_curCameraZ);
		_curCamera.setTarget( 0, 0, 0 );
		_curCamera.update();

		// draw shapes
		if( _outerElement != null ) _outerElement.update();
		if( _bgElement != null ) _bgElement.update();
		if( _fgElement != null ) _fgElement.update();
		if( _kinectElement != null ) _kinectElement.update();
		if( _ambientElement != null ) _ambientElement.update();	
		p.hint( P.DISABLE_DEPTH_TEST );
		if( _2dElement != null ) _2dElement.update();		
		if( _frame2dElement != null ) _frame2dElement.update();		
		p.hint( P.ENABLE_DEPTH_TEST );

//		debugDrawColorList();
		// lets us use the keyboard to funk it up
//		if( p.keyPressed ) handleKeyboardInput();
		
		if( _isAutoPilot == true ) handleAutoPilot();
		if( _isStressTesting == true ) handleStressTest();
	}
	
	protected void getNewColorList() {
		// get a new ColorList
		TColor col = ColorRange.LIGHT.getColor();
		// loop through strategies
		ArrayList<ColorTheoryStrategy> strategies = ColorTheoryRegistry.getRegisteredStrategies();
		for (Iterator<ColorTheoryStrategy> i=strategies.iterator(); i.hasNext();) {
			ColorTheoryStrategy s = (ColorTheoryStrategy) i.next();
			_colorList = ColorList.createUsingStrategy(s, col);
			_colorList = new ColorRange( _colorList ).addBrightnessRange(0.8f,1).getColors(null,100,0.05f);
			_colorList.sortByDistance(true);
		}
	}
	
	protected void debugDrawColorList() {
		DrawUtil.setTopLeft( p );
		// draw the color list for debug purposes
		int x = 0;
		for (Iterator<TColor> i = _colorList.iterator(); i.hasNext();) {
			TColor c = (TColor) i.next();
			p.fill(c.toARGB());
			x += 10;
			p.rect(x,0,x+10,10);
		}
	}
	
	protected void pickNewColors() {
		P.println("make sure to override pickNewColors()");
	}
	
	protected void handleAutoPilot() {
		int multiplier = 2;
		if( p.frameCount % ( p._fps * 3 * multiplier) == 0 ) {
			pickNewColors();
		}
		if( p.frameCount % ( p._fps * 6 * multiplier) == 0 ) {
			newLineMode();
		}
		if( p.frameCount % ( p._fps * 9 * multiplier) == 0 ) {
			newCamera();
		}
		if( p.frameCount % ( p._fps * 18 * multiplier) == 0 ) {
			pickMode();
		}
		if( p.frameCount % ( p._fps * 30 * multiplier) == 0 ) {
			pickElements();
			pickNewColors();
		}
	}
	
	protected void handleStressTest() {
		if( p.frameCount % 10 == 0 ) pickElements();
		for( int i=0; i < 100; i++ ) {
//			pickMode();
			pickNewColors();
			newLineMode();
			newCamera();
			updateSection();
			updateTiming();
		}
	}
	
	protected void storeCurColors() {
		if( _outerElement != null ) _outerElement.updateColorSet( _balletColors );
		if( _bgElement != null ) _bgElement.updateColorSet( _balletColors );
		if( _fgElement != null ) _fgElement.updateColorSet( _balletColors );
		if( _ambientElement != null ) _ambientElement.updateColorSet( _balletColors );		
		if( _2dElement != null ) _2dElement.updateColorSet( _balletColors );		
		if( _frame2dElement != null ) _frame2dElement.updateColorSet( _balletColors );		
		if( _kinectElement != null ) _kinectElement.updateColorSet( _balletColors );		
	}
	
	public void handleKeyboardInput() {
		if( p.key == 'a' || p.key == 'A' ){
			_isAutoPilot = !_isAutoPilot;
			P.println("_isAutoPilot = "+_isAutoPilot);
		}
		if( p.key == 'S' ){
			_isStressTesting = !_isStressTesting;
			P.println("_isStressTesting = "+_isStressTesting);
		}
		if ( p.key == 'c' || p.key == 'C') {
			pickNewColors();
		}
		if ( p.key == 'v' || p.key == 'V') {
			newCamera();
		}
		if ( p.key == 'n' || p.key == 'N') {
			updateTiming();
		}
		if ( p.key == 'm' || p.key == 'M') {
			pickMode();
			pickNewColors();
		}
		if ( p.key == 'f' || p.key == 'F') {
			updateSection();
		}
		if ( p.key == 'l' || p.key == 'L') {
			newLineMode();
		}
		if ( p.key == ' ') {
			pickElements();
			pickNewColors();
		}
	}
	
	protected void pickElements() {
		// stagger swapping on FG and BG elements - find cur index and increment to next
		// pick bg element
		if( _numBigChanges % 2 == 1 && _bgElements.size() > 0 ) {
			if( _bgElement != null ) _bgElement.pause();
			int curBGIndex = _bgElements.indexOf( _bgElement );
			curBGIndex = ( curBGIndex < _bgElements.size() - 1 ) ? curBGIndex + 1 : 0;
			_bgElement = _bgElements.get( curBGIndex );
		}
		
		// pick fg element
		if( _numBigChanges % 2 == 0 && _fgElements.size() > 0 ) {
			if( _fgElement != null ) _fgElement.pause();
			int curFGIndex = _fgElements.indexOf( _fgElement );
			curFGIndex = ( curFGIndex < _fgElements.size() - 1 ) ? curFGIndex + 1 : 0;
			_fgElement = _fgElements.get( curFGIndex );
		}
		
		// pick outer element - randomly turn it off
		if( _numBigChanges > 3 && _outerElements.size() > 0 ) {
			if( _outerElement != null ) _outerElement.pause();
			int curOuterIndex = _outerElements.indexOf( _outerElement );
			curOuterIndex = ( curOuterIndex < _outerElements.size() - 1 ) ? curOuterIndex + 1 : 0;
			_outerElement = _outerElements.get( curOuterIndex );
		}
		_outerElement = ( P.round( p.random( 0, 3 ) ) == 1 ) ? null : _outerElement;
		
		// pick ambient element
		if( _numBigChanges > 4 && _ambientElements.size() > 0 ) {
			if( _ambientElement != null ) _ambientElement.pause();
			int curAmbientIndex = _ambientElements.indexOf( _ambientElement );
			curAmbientIndex = ( curAmbientIndex < _ambientElements.size() - 1 ) ? curAmbientIndex + 1 : 0;
			_ambientElement = _ambientElements.get( curAmbientIndex );
		}
		// _ambientElement = ( MathUtil.randBoolean( p ) == true ) ? null : _ambientElement;
		
		// pick 2D element
		if( _2dElements.size() > 0 ) {	// _numBigChanges % 1 == 0 && 
			if( _2dElement != null ) _2dElement.pause();
			int cur2dIndex = _2dElements.indexOf( _2dElement );
			cur2dIndex = ( cur2dIndex < _2dElements.size() - 1 ) ? cur2dIndex + 1 : 0;
			_2dElement = _2dElements.get( cur2dIndex );
		}

		// pick frame 2D element
		if( _frame2dElements.size() > 0 ) { 
			if( _frame2dElement != null ) _frame2dElement.pause();
			int curIndex = _frame2dElements.indexOf( _frame2dElement );
			curIndex = ( curIndex < _frame2dElements.size() - 1 ) ? curIndex + 1 : 0;
			_frame2dElement = _frame2dElements.get( curIndex );
		}

		// pick kinect element
		if( _kinectElements.size() > 0 ) { 
			if( _kinectElement != null ) _kinectElement.pause();
			int curIndex = _kinectElements.indexOf( _kinectElement );
			curIndex = ( curIndex < _kinectElements.size() - 1 ) ? curIndex + 1 : 0;
			_kinectElement = _kinectElements.get( curIndex );
		}

		// keep track of changes
		_numBigChanges++;
	}
	
	protected void newCamera() {
		if( _outerElement != null && MathUtil.randBoolean( p ) == true ) _outerElement.updateCamera();
		if( _bgElement != null && MathUtil.randBoolean( p ) == true ) _bgElement.updateCamera();
		if( _fgElement != null && MathUtil.randBoolean( p ) == true ) _fgElement.updateCamera();
		if( _ambientElement != null && MathUtil.randBoolean( p ) == true ) _ambientElement.updateCamera();
		if( _2dElement != null && MathUtil.randBoolean( p ) == true ) _2dElement.updateCamera();
		if( _frame2dElement != null && MathUtil.randBoolean( p ) == true ) _frame2dElement.updateCamera();
		if( _kinectElement != null && MathUtil.randBoolean( p ) == true ) _kinectElement.updateCamera();
	}
	
	protected void newLineMode() {
		if( _outerElement != null && MathUtil.randBoolean( p ) == true ) _outerElement.updateLineMode();
		if( _bgElement != null && MathUtil.randBoolean( p ) == true ) _bgElement.updateLineMode();
		if( _fgElement != null && MathUtil.randBoolean( p ) == true ) _fgElement.updateLineMode();
		if( _ambientElement != null && MathUtil.randBoolean( p ) == true ) _ambientElement.updateLineMode();
		if( _2dElement != null && MathUtil.randBoolean( p ) == true ) _2dElement.updateLineMode();
		if( _frame2dElement != null && MathUtil.randBoolean( p ) == true ) _frame2dElement.updateLineMode();
		if( _kinectElement != null && MathUtil.randBoolean( p ) == true ) _kinectElement.updateLineMode();
	}
	
	protected void pickMode() {
		
		if( _outerElement != null ) _outerElement.reset();
		if( _bgElement != null ) _bgElement.reset();
		if( _fgElement != null ) _fgElement.reset();
		if( _ambientElement != null ) _ambientElement.reset();
		if( _2dElement != null ) _2dElement.reset();
		if( _frame2dElement != null ) _frame2dElement.reset();
		if( _kinectElement != null ) _kinectElement.reset();
				
		_curCamera.setPosition(0, 0, (int)_curCameraZ);
	}

	protected void updateTiming() {
		if( _outerElement != null ) _outerElement.updateTiming();
		if( _bgElement != null ) _bgElement.updateTiming();
		if( _fgElement != null ) _fgElement.updateTiming();
		if( _ambientElement != null ) _ambientElement.updateTiming();
		if( _2dElement != null ) _2dElement.updateTiming();
		if( _frame2dElement != null ) _frame2dElement.updateTiming();
		if( _kinectElement != null ) _kinectElement.updateTiming();
	}
	
	protected void updateSection() {
		if( _outerElement != null ) _outerElement.updateSection();
		if( _bgElement != null ) _bgElement.updateSection();
		if( _fgElement != null ) _fgElement.updateSection();
		if( _ambientElement != null ) _ambientElement.updateSection();
		if( _2dElement != null ) _2dElement.updateSection();
		if( _frame2dElement != null ) _frame2dElement.updateSection();
		if( _kinectElement != null ) _kinectElement.updateSection();
	}
	
	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount ) {
//		P.println("beat detect: "+isKickCount+" "+isSnareCount+" "+isHatCount+" "+isOnsetCount);
	}
	
}