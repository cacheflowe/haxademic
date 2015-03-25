package com.haxademic.sketch.test;

import java.util.ArrayList;

import processing.core.PConstants;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.assets.AsyncImageLoader;
import com.haxademic.core.assets.DirImageLoader;
import com.haxademic.core.components.Button;
import com.haxademic.core.components.IMouseable;
import com.haxademic.core.components.TextButton;
import com.haxademic.core.components.TextInput;
import com.haxademic.core.draw.text.CustomFontText2D;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.text.ValidateUtil;

@SuppressWarnings("serial")
public class ComponentsTest
extends PAppletHax{

	protected DirImageLoader _imageLoader;
	protected String _imagesDir;

	protected AsyncImageLoader asyncLoader;

	protected ArrayList<IMouseable> _mouseables;

	protected TextInput _emailInput;
	protected TextInput _initialsInput;
	protected TextInput _activeTextInput;

	protected void overridePropsFile() {
		_appConfig.setProperty( "images_dir", FileUtil.getHaxademicDataPath() + "images/kacheout/" );
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
		_appConfig.setProperty( "disable_esc", "false" );
		_appConfig.setProperty( "fills_screen", "false" );
	}

	public void setup() {
		super.setup();

		// monkey-patch TAB capture ability - Processing 2.0 broke this in 3D rendering contexts
		SystemUtil.p2TabKeyInputPatch();

		loadAndSaveImagesFromDir();

		// test async image loading
		asyncLoader = new AsyncImageLoader(p, "http://cacheflowe.com/images/site-header-single.gif");

		// build button
		_mouseables = new ArrayList<IMouseable>();
		_mouseables.add( new TextButton( p, "ONE", "1", 20, 20, 180, 60 ) );
		_mouseables.add( new TextButton( p, "TWO", "2", 20, 120, 180, 60 ) );

		// build text input
		String fontFile = FileUtil.getHaxademicDataPath() + "fonts/GothamBold.ttf";
		_emailInput = new TextInput( p, "email", 20, fontFile, p.color( 255, 255, 255 ), 30, CustomFontText2D.ALIGN_LEFT, 20, 220, 380, 60 );
		_initialsInput = new TextInput( p, "initials", 40, fontFile, p.color( 255, 255, 255 ), 10, CustomFontText2D.ALIGN_CENTER, 20, 300, 120, 60 );
		_mouseables.add( _emailInput );
		_mouseables.add( _initialsInput );
	}

	protected void loadAndSaveImagesFromDir() {
		// load images from a directory
		_imagesDir = _appConfig.getString( "images_dir", "" );
		_imageLoader = new DirImageLoader( p, _imagesDir, "png,gif,jpg" );

		// save the first image to the output path
		// make sure the output directory is there first.
		FileUtil.createDir( FileUtil.getHaxademicOutputPath() );
		//		_imageLoader.images.get(0).save( FileUtil.getHaxademicOutputPath() + "image-" + SystemUtil.getTimestampFine( p ) + ".png" );
	}

	public void drawApp() {
		// draw async-loaded image
		p.image( asyncLoader.image(), 220, 20 );

		// deal with buttons and text inputs
		for( int i=0; i < _mouseables.size(); i++ ) {
			_mouseables.get(i).update( p );
		}
	}

	public void mouseReleased() {
		for( int i=0; i < _mouseables.size(); i++ ) {
			if( _mouseables.get(i).checkRelease( p.mouseX, p.mouseY ) ) {
				if( _mouseables.get(i) instanceof TextInput ) {
					_activeTextInput = (TextInput) _mouseables.get(i);
				}
			}
		}
	}

	public void mousePressed() {
		for( int i=0; i < _mouseables.size(); i++ ) {
			_mouseables.get(i).checkPress( p.mouseX, p.mouseY );
		}
		_activeTextInput = null;
	}

	public void mouseMoved() {
		for( int i=0; i < _mouseables.size(); i++ ) {
			_mouseables.get(i).checkOver( p.mouseX, p.mouseY );
		}
	}

	public void keyPressed() {
		super.keyPressed();
		if( p.keyCode == P.BACKSPACE ) {
			if( p.key == PConstants.BACKSPACE ) P.println("BACKSPACE");
			if( p.key == PConstants.TAB ) P.println("TAB");
			if( p.key == PConstants.RETURN ) P.println("RETURN");
			if( p.key == PConstants.ENTER ) P.println("ENTER");
			if( p.key == PConstants.BACKSPACE ) {
				if( _activeTextInput != null ) {
					_activeTextInput.backspace();
				}
			} else if( p.keyCode == P.TAB ) {

				//		} else if( p.key == PConstants.TAB ) {
				P.println("TAB!!!!");
				if( _activeTextInput == _initialsInput ) {
					_initialsInput.blur();
					_emailInput.focus();
					_activeTextInput = _emailInput;
				} else if( _activeTextInput == _emailInput ) {
					_emailInput.blur();
					_initialsInput.focus();
					_activeTextInput = _initialsInput;
				}
			} else {
				if( _activeTextInput != null ) {
					if( _activeTextInput.id() == "email" ) {
						if( ValidateUtil.validateEmailCharacter( p.key+"" ) == true ) {
							_activeTextInput.keyPressed( p.key+"" );
						}
					} else if( _activeTextInput.id() == "initials" ) {
						if( _activeTextInput.length() < 3 && ValidateUtil.validateAlphanumericCharacter( p.key+"" ) == true ) {
							_activeTextInput.keyPressed( (p.key+"").toUpperCase() );
						}
					} 
				}
			}
		}
	}
}
