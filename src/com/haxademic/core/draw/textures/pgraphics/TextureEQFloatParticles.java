package com.haxademic.core.draw.textures.pgraphics;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PVector;

public class TextureEQFloatParticles 
extends BaseTexture {

	protected int _numParticles;
	protected ArrayList<FloatParticle> _particles;

	public TextureEQFloatParticles( int width, int height ) {
		super(width, height);
		
		
		_numParticles = 256;
		_particles = new ArrayList<FloatParticle>();
		for( int i=0; i < _numParticles; i++ ) {
			_particles.add(new FloatParticle());
		}
	}

	public void newLineMode() {
		
	}

	public void draw() {
		_texture.clear();
		_texture.background(0);
		
		float spectrumInterval = ( 512f / _numParticles );
		
		_texture.noStroke();
		_texture.blendMode(PBlendModes.ADD);
		PG.setDrawCenter(_texture);

		for( int i=0; i < _particles.size(); i++ ) {
			int eqIndex = P.floor(spectrumInterval * i);
			_particles.get(i).update(eqIndex);
		}
	}
	
	public void postProcess() {
		super.postProcess();
		BlurHFilter.instance().setBlurByPercent(1.7f, width);
		BlurHFilter.instance().applyTo(_texture);
		BlurVFilter.instance().setBlurByPercent(1.7f, height);
		BlurVFilter.instance().applyTo(_texture);
	}
	
	public class FloatParticle {
		
		protected float _size;
		protected int _color;
		protected PVector _pos;
		protected PVector _speed;
		protected float maxSize = 10;
		protected float _amp = 0;
		protected float _ampDecay = 0.95f;
		
		public FloatParticle() {
			_size = P.p.random(1, 10);
			_pos = new PVector(P.p.random(0, width), P.p.random(0, height), 0);
			_speed = new PVector(0, _size * 0.1f * 0.2f, 0);
		}
		
		public void update(int eqIndex) {
//			_size = P.p.random(1, height * 0.3f);
//			_speed = new PVector(0, _size * 0.1f, 0);

			float amp = AudioIn.audioFreq(eqIndex) * 9f;
			if(amp > _amp) _amp = amp;
			float curSize = _amp;
			float curSpeed = (_speed.y / 3f) * (_amp);
			
			float x = _pos.x + P.sin(P.p.frameCount/(_size*10f)) * curSpeed/5f;
			_pos.set(x, _pos.y - curSpeed);
			
			if(_pos.y < -maxSize) _pos.set(_pos.x, height + maxSize);
			if(_pos.x < -maxSize) _pos.set(width + maxSize, height + maxSize);
			if(_pos.x > width + maxSize) _pos.set(-maxSize, height + maxSize);
			
//			_texture.fill(255, 255f * _amp);
			_texture.fill(255);
//			_texture.rect(_pos.x, _pos.y, curSize, curSize);
//			_texture.ellipse(_pos.x, _pos.y, curSize, curSize);
			_texture.image(DemoAssets.particle(), _pos.x, _pos.y, curSize, curSize);
			
			_amp *= _ampDecay;
		}
	}
}
