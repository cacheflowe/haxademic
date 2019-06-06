package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Superformula;

public class Demo_Superformula 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// ui
	public String a = "a";
	public String b = "b";
	public String m = "m";
	public String n1 = "n1";
	public String n2 = "n2";
	public String n3 = "n3";
	
	protected Superformula _superForm;
	protected float[] _camPos = { 0f, 0f, 50000f};
	
	protected boolean _audioEnabled = false;

	public void setupFirstFrame() {
		_superForm = new Superformula( 200, 200, 10, 1, 6, 20, 7, 18);
		
		p.ui.addSlider(a, 6, 0, 30, 0.1f, false);
		p.ui.addSlider(b, 8, 0, 30, 0.1f, false);
		p.ui.addSlider(m, 15, 0, 30, 0.1f, false);
		p.ui.addSlider(n1, 15, 0, 30, 0.1f, false);
		p.ui.addSlider(n2, 15, 0, 30, 0.1f, false);
		p.ui.addSlider(n3, 6, 0, 30, 0.1f, false);
	}

	public void drawApp() {
		p.background(0);
		
		p.pushMatrix();
		p.translate( p.width/2, p.height/2, -300 );

		// debug mouse position
		float x = (float) p.mouseX * 0.1f; // 10 + 6f * ( (float) p.mouseX / (float) p.width );
		float y = (float) p.mouseY * 0.1f; // 6f * ( (float) p.mouseY / (float) p.height );

		
		//		p.rotateX(p.frameCount/20f);
//		p.rotateY(P.PI/2f);
//		p.rotateZ(p.frameCount/200f);
		p.rotateZ(P.HALF_PI);
		
		if( _audioEnabled == false ) {
			setSuperFormulaProps( 10, 1, 6, 20, 7, 18 );
			_superForm.a( p.ui.value(a) );
			_superForm.b( p.ui.value(b) );
			_superForm.m( p.ui.value(m) );
			_superForm.n1( p.ui.value(n1) );
			_superForm.n2( p.ui.value(n2) );
			_superForm.n3( p.ui.value(n3) );

		} else {
			
			
			setSuperFormulaProps( 10, 1, 6, 20, 7, 18 );
			
			// form 1
//			_superForm.a( 76 + ( 50f * p._audioInput.getFFT().averages[0]));
//			_superForm.n1( 5 + ( 10f * p._audioInput.getFFT().averages[1]));

			// form 2
			setSuperFormulaProps( 1, 1, 6, x, y, 18 );
			
			
			
			float audioRange = 0.1f;
//			_superForm.a( x + (audioRange * 300f * p._audioInput.getFFT().averages[0]));
			_superForm.b( 8 + (audioRange * 10f * p.audioFreq(0)));
//			_superForm.m( 15 + (audioRange * 10f * p._audioInput.getFFT().averages[2]));
//			_superForm.n1( y + (audioRange * 20f * p._audioInput.getFFT().averages[1]));
			_superForm.n2( 15 + (audioRange * 50f * p.audioFreq(1)));
//			_superForm.n3( 6 + (audioRange * 40f * p._audioInput.getFFT().averages[5]));
		}
	
		PG.basicCameraFromMouse(p.g);
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
