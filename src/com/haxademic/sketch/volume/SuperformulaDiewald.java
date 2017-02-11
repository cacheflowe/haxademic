package com.haxademic.sketch.volume;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Superformula;
import com.haxademic.core.draw.util.OpenGLUtil;

import controlP5.ControlP5;

public class SuperformulaDiewald 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	ControlP5 _cp5;
	public float sliderA = 0;
	public float sliderB = 0;
	public float sliderM = 0;
	public float sliderN1 = 0;
	public float sliderN2 = 0;
	public float sliderN3 = 0;
	
	protected Superformula _superForm;
	protected float[] _camPos = { 0f, 0f, 50000f};
	
	protected boolean _audioEnabled = false;

	public void setup() {
		super.setup();
		_superForm = new Superformula( 200, 200, 10, 1, 6, 20, 7, 18);
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		// set averages
		p._audioInput.getFFT().averages(2);
		p._audioInput.setGain(10);
		
		// setup controls
	  _cp5 = new ControlP5(this);
	  
	  // add a horizontal sliders, the value of this slider will be linked
	  // to variable 'sliderValue' 
	  _cp5.addSlider("sliderA").setPosition(20,20).setWidth(p.width - 140).setRange(0,1000);
	  _cp5.addSlider("sliderB").setPosition(20,60).setWidth(p.width - 140).setRange(0,1000);
	  _cp5.addSlider("sliderM").setPosition(20,100).setWidth(p.width - 140).setRange(0,1000);
	  _cp5.addSlider("sliderN1").setPosition(20,140).setWidth(p.width - 140).setRange(0,1000);
	  _cp5.addSlider("sliderN2").setPosition(20,180).setWidth(p.width - 140).setRange(0,1000);
	  _cp5.addSlider("sliderN3").setPosition(20,220).setWidth(p.width - 140).setRange(0,1000);

	}

	public void drawApp() {
		p.background(0);
		
		p.pushMatrix();
		p.translate( p.width/2, p.height/2, -3000 );

		// debug mouse position
		float x = (float) p.mouseX * 0.1f; // 10 + 6f * ( (float) p.mouseX / (float) p.width );
		float y = (float) p.mouseY * 0.1f; // 6f * ( (float) p.mouseY / (float) p.height );

		
		//		p.rotateX(p.frameCount/20f);
//		p.rotateY(P.PI/2f);
//		p.rotateZ(p.frameCount/200f);
		p.rotateZ(P.HALF_PI);
		
		if( _audioEnabled == false ) {
			setSuperFormulaProps( 10, 1, 6, 20, 7, 18 );
			_superForm.a( sliderA );
			_superForm.b( sliderB );
			_superForm.m( sliderM );
			_superForm.n1( sliderN1 );
			_superForm.n2( sliderN2 );
			_superForm.n3( sliderN3 );

		} else {
			
			
			setSuperFormulaProps( 10, 1, 6, 20, 7, 18 );
			
			// form 1
//			_superForm.a( 76 + ( 50f * p._audioInput.getFFT().averages[0]));
//			_superForm.n1( 5 + ( 10f * p._audioInput.getFFT().averages[1]));

			// form 2
			setSuperFormulaProps( 1, 1, 6, x, y, 18 );
			
			
			
			float audioRange = 0.1f;
//			_superForm.a( x + (audioRange * 300f * p._audioInput.getFFT().averages[0]));
			_superForm.b( 8 + (audioRange * 10f * p._audioInput.getFFT().averages[0]));
//			_superForm.m( 15 + (audioRange * 10f * p._audioInput.getFFT().averages[2]));
//			_superForm.n1( y + (audioRange * 20f * p._audioInput.getFFT().averages[1]));
			_superForm.n2( 15 + (audioRange * 50f * p._audioInput.getFFT().averages[1]));
//			_superForm.n3( 6 + (audioRange * 40f * p._audioInput.getFFT().averages[5]));
		}
	
		
		_superForm.update();
		_superForm.drawMesh(p.g, true, true, true, false, _camPos );
		p.popMatrix();
	}

	protected void setSuperFormulaProps( double a, double b, double m, double n1, double n2, double n3 ) {
		_superForm.a( a );
		_superForm.b( b );
		_superForm.m( m );
		_superForm.n1( n1 );
		_superForm.n2( n2 );
		_superForm.n3( n3 );
	}
}
