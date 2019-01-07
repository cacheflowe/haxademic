package com.haxademic.core.data.constants;

import com.haxademic.core.app.P;

public class PBlendModes {
	public static int BLEND = P.BLEND; 				// - linear interpolation of colours: C = A*factor + B. This is the default blending mode.
	public static int ADD = P.ADD; 					// - additive blending with white clip: C = min(A*factor + B, 255)
	public static int SUBTRACT = P.SUBTRACT; 		// - subtractive blending with black clip: C = max(B - A*factor, 0)
	public static int DARKEST = P.DARKEST; 			// - only the darkest colour succeeds: C = min(A*factor, B)
	public static int LIGHTEST = P.LIGHTEST; 		// - only the lightest colour succeeds: C = max(A*factor, B)
	public static int DIFFERENCE = P.DIFFERENCE; 	// - subtract colors from underlying image.
	public static int EXCLUSION = P.EXCLUSION; 		// - similar to DIFFERENCE, but less extreme.
	public static int MULTIPLY = P.MULTIPLY; 		// - multiply the colors, result will always be darker.
	public static int SCREEN = P.SCREEN; 			// - opposite multiply, uses inverse values of the colors.
	public static int REPLACE = P.REPLACE; 			// - the pixels entirely replace the others and don't utilize alpha (transparency) values
}
