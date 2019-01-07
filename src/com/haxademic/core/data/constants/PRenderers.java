package com.haxademic.core.data.constants;

import com.haxademic.core.app.P;

public class PRenderers {
	public static String JAVA2D = P.JAVA2D;
	public static String FX2D = P.FX2D;
	public static String P2D = P.P2D;
	public static String P3D = P.P3D;
	public static String PDF = P.PDF;
	public static String currentRenderer() {
		return P.p.g.getClass().getName();
	}
}
