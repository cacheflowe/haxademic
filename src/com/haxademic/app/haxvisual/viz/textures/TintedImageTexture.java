package com.haxademic.app.haxvisual.viz.textures;

import java.util.ArrayList;

import processing.core.PImage;
import toxi.color.TColor;

import com.haxademic.app.haxvisual.viz.IAudioTexture;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.color.TColorBlendBetween;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

public class TintedImageTexture
implements IAudioTexture
{
	
	protected AudioInputWrapper _audioInput;
	protected PImage _image;
	protected ArrayList<PImage> _images;
	protected TColorBlendBetween _color;
	protected float _alpha = 0;
	protected int _imageIndex = 0;
	
	public TintedImageTexture() {
		String imgBase = "images/cacheflowe-art/";
		ArrayList<String> files = FileUtil.getFilesInDirOfType( FileUtil.getHaxademicDataPath() + imgBase, "png" );
		_images = new ArrayList<PImage>();
		for( int i=0; i < files.size(); i++ ) {
			_images.add( P.p.loadImage( FileUtil.getHaxademicDataPath() + imgBase + files.get(i) ) );
		}

		_image = new PImage( 512, 512 );
		_color = new TColorBlendBetween( TColor.BLACK.copy(), TColor.BLACK.copy() );
	}
	
	public void updateTexture( AudioInputWrapper audioInput ) {
		_alpha = 1; //audioInput.getFFT().spectrum[ 10 ] * 1.7f;
	}
	
	public PImage getTexture() {
		P.p.tint( _color.argbWithPercent( _alpha ), 255 );
		return _images.get( _imageIndex );
	}
	
	public void updateColorSet( ColorGroup colors ) {
		if( P.p.frameCount % 3 == 0 ) {
			_imageIndex = MathUtil.randRange( 0, _images.size() - 1 );
		}
		_color.setColors( TColor.BLACK.copy(), colors.getRandomColor() );
		_color.lightenColor( 0.3f );
	}
	
	public void dispose() {
		
	}

	public void init() {
		// TODO Auto-generated method stub
	}

	public void update() {
		// TODO Auto-generated method stub
	}

	public void reset() {
		// TODO Auto-generated method stub
	}

	public void pause() {
		// TODO Auto-generated method stub
	}

	public void updateLineMode() {
		// TODO Auto-generated method stub
	}

	public void updateCamera() {
		// TODO Auto-generated method stub
	}
	
	public void updateTiming() {
		// TODO Auto-generated method stub
	}
	
	public void updateSection() {
		// TODO Auto-generated method stub
	}
}
