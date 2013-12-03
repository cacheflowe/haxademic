package com.haxademic.core.draw.color;

import java.util.Vector;

import toxi.color.TColor;

import com.haxademic.core.math.MathUtil;

public class ColorGroup {
	public Vector<Vector<TColor>> _colorSets;
	protected int _curSet = 0;
	public static int NONE = -1;
	public static int BALLET = 0;
	public static int KACHE_OUT = 1;
	public static int NEON = 2;

	public ColorGroup( int set ) {
		getColors( set );
	}
	
	public void getColors( int set ) {
		_colorSets = new Vector<Vector<TColor>>();
		if( set == BALLET ) getBallet();
		if( set == KACHE_OUT ) getKacheOut();
		if( set == NEON ) getNeon();
		if( set != NONE ) setRandomGroup();
	}
	
	public void createGroupWithTColors( TColor tColor1, TColor tColor2, TColor tColor3, TColor tColor4, TColor tColor5 ) {
		Vector<TColor> group = new Vector<TColor>() ;
		group.addElement( tColor1 );
		group.addElement( tColor2 );
		group.addElement( tColor3 );
		group.addElement( tColor4 );
		group.addElement( tColor5 );
		_colorSets.add(  group );
		setRandomGroup();
	}
	
	public void setRandomGroup() {
		_curSet = MathUtil.randRange( 0, _colorSets.size() - 1 );
	}
	
	public TColor getRandomColor() {
		return getCurGroup().get(  MathUtil.randRange( 0, getCurGroup().size() - 1 ) );
	}
	
	public Vector<TColor> getCurGroup() {
		return _colorSets.get( _curSet );
	}
	
	public TColor getColorFromIndex( int index ) {
		return _colorSets.get( _curSet ).get( index );
	}

	public TColor getColorFromGroup( int group, int color ) {
		return _colorSets.get( group ).get( color );
	}

	public Vector<TColor> getGroupFromIndex( int index ) {
		return _colorSets.get( index );
	}

	public Vector<TColor> createGroupWithHexes( String hex1, String hex2, String hex3, String hex4, String hex5 ) {
		Vector<TColor> group = new Vector<TColor>() ;
		group.addElement( TColor.newHex( hex1 ) );
		group.addElement( TColor.newHex( hex2 ) );
		group.addElement( TColor.newHex( hex3 ) );
		group.addElement( TColor.newHex( hex4 ) );
		group.addElement( TColor.newHex( hex5 ) );
		return group;
	}
	
	public void getBallet() {
		_colorSets = new Vector<Vector<TColor>>();
		_colorSets.add( createGroupWithHexes( "9c2c63", "d073a2", "e5a8ff", "fffde2", "cebf6d" ) );
		_colorSets.add( createGroupWithHexes( "c67dff", "807bbf", "4e6c7f", "40a293", "ab322a" ) );
		_colorSets.add( createGroupWithHexes( "f2f2f2", "f4d88b", "dcb88e", "c3844c", "ab322a" ) );
		_colorSets.add( createGroupWithHexes( "5b1f37", "f469f3", "f4a4f3", "f3d9f3", "0d0d0d" ) );
		_colorSets.add( createGroupWithHexes( "cfd9d6", "beb5b5", "f6d68b", "edc3b8", "faf3e3" ) );
		_colorSets.add( createGroupWithHexes( "2d2e47", "cdf200", "d9eabe", "e1efff", "d74400" ) );
		_colorSets.add( createGroupWithHexes( "624b33", "fff8e2", "ffe86c", "a4fef9", "ffcebb" ) );
		_colorSets.add( createGroupWithHexes( "232522", "d27f83", "dd9b8a", "d8e1bc", "e8fffb" ) );
	}
	
	public void getKacheOut() {
		_colorSets = new Vector<Vector<TColor>>();
		_colorSets.add( createGroupWithHexes( "9c2c63", "d073a2", "e5a8ff", "fffde2", "cebf6d" ) );
		_colorSets.add( createGroupWithHexes( "c67dff", "807bbf", "4e6c7f", "40a293", "ab322a" ) );
		_colorSets.add( createGroupWithHexes( "f2f2f2", "f4d88b", "dcb88e", "c3844c", "ab322a" ) );
	}
	
	public void getNeon() {
		_colorSets = new Vector<Vector<TColor>>();
		_colorSets.add( createGroupWithHexes( "02ffff", "00fa00", "fdfd04", "fd5002", "fe007c" ) );
		_colorSets.add( createGroupWithHexes( "d865fe", "00fffe", "28fb00", "feff02", "ff00ff" ) );
		_colorSets.add( createGroupWithHexes( "02b8ff", "fffa02", "ff0091", "858585", "c182f4" ) );
		_colorSets.add( createGroupWithHexes( "e9b000", "d1ff36", "ff6409", "eb00aa", "4c00ff" ) );
		_colorSets.add( createGroupWithHexes( "e2ea63", "ffdd51", "00ff00", "72e9b7", "83e4ff" ) );
		_colorSets.add( createGroupWithHexes( "8eff0a", "00ff00", "3531ff", "00bfff", "350076" ) );
		_colorSets.add( createGroupWithHexes( "ff006a", "fffa00", "0085ff", "c2b6ae", "ffffff" ) );
		_colorSets.add( createGroupWithHexes( "ff61cd", "67009c", "cbff00", "656565", "cccccc" ) );
		_colorSets.add( createGroupWithHexes( "fbfbf9", "00e800", "008d00", "005a00", "002800" ) );
		_colorSets.add( createGroupWithHexes( "31cbff", "f6e098", "ff5734", "9a39eb", "58f74a" ) );
		_colorSets.add( createGroupWithHexes( "a91ae5", "fff600", "81ff00", "ea27c1", "000000" ) );
		_colorSets.add( createGroupWithHexes( "dc1a27", "f5328f", "008c7f", "def427", "ed8100" ) );
//		_colorSets.add( createGroupWithHexes( "XXXXX", "XXXXX", "XXXXX", "XXXXX", "XXXXX" ) );
		
		// lighten...
		float lighten = 0.4f;
		for( int i=0; i < _colorSets.size() - 1; i++ ) {
			for( int j=0; j < _colorSets.get(i).size() - 1; j++ ) {
				_colorSets.get(i).get(j).adjustRGB( lighten, lighten, lighten );
			}
		}

	}
	
}
