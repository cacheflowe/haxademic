package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.FloatBuffer;
import com.haxademic.core.math.MathUtil;

@SuppressWarnings("serial")
public class FloatBufferTest
extends PAppletHax  
{
	protected FloatBuffer buff;
	
	public void setup() {
		super.setup();		
		buff = new FloatBuffer(10);
	}

	public void drawApp() {
		buff.update((float)Math.sin(p.frameCount/20f) * 10f);
		
		P.println( 
				MathUtil.roundToPrecision( buff.sum(), 2 ), 
				" | ", 
				MathUtil.roundToPrecision( buff.sumPos(), 2 ), 
				" | ",
				MathUtil.roundToPrecision( buff.sumNeg(), 2 )
		);
	}
}
