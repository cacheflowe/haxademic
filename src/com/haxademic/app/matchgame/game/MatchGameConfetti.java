package com.haxademic.app.matchgame.game;

import toxi.color.TColor;
import toxi.math.noise.PerlinNoise;

import com.haxademic.app.matchgame.MatchGame;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.DrawUtil;

public class MatchGameConfetti {
	protected MatchGame p;
	protected ConfettiParticle[] _particles;
	protected int _numParticles;
	protected boolean _active;

	public MatchGameConfetti( int numParticles ) {
		p = (MatchGame) P.p;
		_numParticles = numParticles;
		_active = false;
		
		// create particles
		_particles = new ConfettiParticle[_numParticles];
		for( int i = 0; i < _numParticles; i++ ) {
			_particles[i] = new ConfettiParticle( p.width/2, p.height/2 );
		}
	}
	
	public void update() {
		if( _active == true ) {
			p.noStroke();
			DrawUtil.setDrawCenter(p);
			boolean stillActive = false;
			for (int i = 0; i < _numParticles; i++) {
				if( _particles[i].update() == true ) {
					stillActive = true;
				}
			}
			if( stillActive == false ) _active = false;
		}
	}
	
	public void explode() {
		_active = true;
		reset();
		for (int i = 0; i < _numParticles; i++) {
			_particles[i].setActive();
		}
	}
	
	public void reset() {
		for (int i = 0; i < _numParticles; i++) {
			_particles[i].reset();
		}
	}
}

// A Cell object
class ConfettiParticle {
	protected MatchGame p;
	protected boolean _isActive;
	protected float _originX;
	protected float _originY;
	protected float _x;
	protected float _y;
	protected float _speedX;
	protected float _speedY;
	protected float _maxSpeedY;
	protected float _speedRot;
	protected float _rot;
	protected float _w;
	protected float _h;
	protected PerlinNoise _perlin;
	protected float _perlinOffset;
	protected float _perlinWind;
	
	protected TColor _color;
	protected TColor BLUE_DARK = TColor.newHex("0f568b");
	protected TColor BLUE_LIGHT = TColor.newHex("9cb3c7");
	protected TColor GREY_MEDIUM = TColor.newHex("d4d4d4");

	public ConfettiParticle( float x, float y ) {
		p = (MatchGame) P.p;
		_isActive = false;
		_originX = x;
		_originY = y;
		_perlin = new PerlinNoise();
		
		reset();
	} 
	
	public void setActive() {
		_isActive = true;
	}
	
	public void reset() {
		_isActive = false;
		
		// start position and set size
		_x = _originX;
		_y = _originY;
		_w = p.random( 6, 24 );
		_h = _w / 2f;
		
		// motion vars
		_speedX = p.random( -25, 25 );
		_speedY = p.random( -70, -30 );
		_maxSpeedY = p.random( 3f, 5f );
		_speedRot = p.random( -.15f, .15f );
		_rot = 0;
		_perlinOffset = p.random( 0f, 1000f );
		
		// random color
		int randColor = P.round( p.random( 0, 2 ) );
		if( randColor == 0 ) {
			_color = BLUE_DARK;
		} else if( randColor == 1 ) {
			_color = BLUE_LIGHT;
		} else {
			_color = GREY_MEDIUM;
		}
	}

	public boolean update() {
		if( _isActive == false ) return false;
			
		// draw setup
		p.pushMatrix();
					
		// move and rotate
		p.translate( _x, _y, 24 );	// max width of confetti to keep particles in front of main game graphics
		p.rotateX( _rot );
		p.rotateY( _rot );
		p.rotateZ( _rot );

		// color and draw confetti
		p.fill( _color.toARGB() );
		p.rect(0,0,_w,_h);
		p.popMatrix();
		
		// apply perlin wind on the way down
		if( _speedY > 0 )
			_perlinWind = -0.5f + _perlin.noise( _perlinOffset + ( (float) p.frameCount / 80f ) );
		else
			_perlinWind = 0;
		
		// increment movement values
		_x += _speedX + _perlinWind * 10f;
		_y += _speedY;
		if( _speedY < 0 ) {
			_speedX *= .98;
			_speedY += 1.8f;
		} else {
			if( _speedY < _maxSpeedY ) _speedY += .1f;
			_speedX *= .9;
		}
		_rot += _speedRot;
		
		// deactivate when off screen
		if( _y > p.height + 50 ) {
			_isActive = false;
		}
		return true;
	}
}