package com.haxademic.core.math;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.haxademic.core.app.P;

import processing.core.PVector;

/**
 * A series of common, static math helper methods
 */
public class MathUtil {
	
	public static float scaleToTarget(float val, float target) {
		return target / val;
	}
	
	/**
	 * Return the eased value of a value towards a destination, with a given easing factor
	 * @param current		current value being eased towards target
	 * @param target		target value that current eases towards
	 * @param easingFactor	larger numbers mean slower easing and must be above 1 for easing over time
	 * @return				the eased value
	 */
	public static float easeTo(float current, float target, float easingFactor) {
		return current -= ( ( current - target ) / easingFactor );
	}
	
	/**
	 * Return easing from 0-1 based on a pow-based ease-in-out factor
	 * @param progress		progress from 0-1 
	 * @param easeFactor	pow-based factor
	 * @return				the remapped 0-1 value
	 */
	public static float easePowPercent(float progress, float easeFactor) {
		if (progress < 0.5) 
			return 0.5f * (float)Math.pow(2*progress, easeFactor);
		else
			return 1 - 0.5f * (float)Math.pow(2*(1 - progress), easeFactor);
	}

	public static boolean randBoolean() {
		return ( P.p.random( 0f, 1f ) > 0.5f ) ? true : false;
	}

	public static boolean randBooleanWeighted(float likeliness) {
		return (P.p.random( 0f, 1f ) < likeliness) ? true : false;
	}

	public static int randIndex(int size) {
		return randRange(0, size - 1);
	}
	
	/**
	 *	Calculates a random number within a minimum and maximum range.
	 *	@param	min		the value for the bottom range.
	 *	@param	max		the value for the upper range.
	 *	@return			the random number within the range.
	 * 	@use			{@code var vRandRange = MathUtil.randRange( 0, 999999 );}
	 */
	public static int randRange(float min, float max) {
		return (int) (Math.round(Math.random() * (max - min)) + min);
	}

	/**
	 *  Calculates a random number within a minimum and maximum range.
	 *  @param  min   the value for the bottom range.
	 *  @param  max   the value for the upper range.
	 *  @return the random number within the range.
	 *  @use    {@code var vRandRange = MathUtil.randRangeDecimal( 0, 999999 );}
	 */
	public static float randRangeDecimal(float min, float max) {	
		return (float) Math.random() * (max - min) + min;
	}

	/**
	 *  Returns a percentage of a value in between 2 other numbers.
	 *  @param  bottomRange   low end of the range.
	 *  @param  topRange      top end of the range.
	 *  @param  valueInRange  value to find a range percentage of.
	 *  @return The percentage [0-1] of valueInRange in the range.
	 *  @use    {@code var vPercent = MathUtil.getPercentWithinRange( 50, 150, 100 );  // displays 50 }
	 */
	public static float getPercentWithinRange(float bottomRange, float topRange, float valueInRange) {
		// normalize values to work positively from zero
		topRange += -bottomRange;
		valueInRange += -bottomRange;
		bottomRange += -bottomRange;  // last to not break other offsets
		// return percentage or normalized values 
		return (valueInRange / (topRange - bottomRange));
	}

	public static float pythagDistance( float a, float b ) {
		return Math.abs((float)Math.sqrt(a*a + b*b));
	}
	
	public static float polygonClosestPoint(float vertices, float radius) {
		float segmentRads = P.TWO_PI / vertices;
		float point1x = radius * P.cos(segmentRads);
		float point1y = radius * P.sin(segmentRads);
		float point2x = radius * P.cos(0);
		float point2y = radius * P.sin(0);
		float midpointX = (point1x + point2x) / 2f;
		float midpointY = (point1y + point2y) / 2f;
		return MathUtil.getDistance(0, 0, midpointX, midpointY);
	}

	/**
	 *  Get distance between 2 points with the pythagorean theorem.
	 *  @param  x1  first point's x position
	 *  @param  y1  first point's y position
	 *  @param  x2  second point's x position
	 *  @param  y2  second point's y position
	 *  @return The distance between point 1 and 2
	 *  @use    {@code var distance = MathUtil.getDistance( 7, 5, 3, 2 );}
	 */
	public static float getDistance(float x1, float y1, float x2, float y2) {
		float a = x1 - x2;
		float b = y1 - y2;
		return Math.abs((float) Math.sqrt(a*a + b*b));
	};
	
	public static float distance3d(float x1, float y1, float z1, float x2, float y2, float z2) {
	    return P.sqrt(P.pow(x1 - x2, 2f) + P.pow(y1 - y2, 2f) + P.pow(z1 - z2, 2f));
	}
	
	public static float getDistance( float x1, float y1, float z1, float x2, float y2, float z2 ) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dz = z1 - z2;
	    return Math.abs((float) Math.sqrt( dx * dx + dy * dy + dz * dz ));
	}


	/**
	 *  Convert a number from Degrees to Radians.
	 *  @param  d degrees (45�, 90�)
	 *  @return radians (3.14..., 1.57...)
	 *  @use    {@code var vRadians = MathUtil.degreesToRadians( 180 );}
	 */

	public static float degreesToRadians(float d) {
		return d * (P.PI / 180f);
	}

	/**
	 *  Convert a number from Radians to Degrees.
	 *  @param  r radians (3.14..., 1.57...)
	 *  @return degrees (45�, 90�)
	 *  @use    {@code var vDegrees = MathUtil.radiansToDegrees( 3.14 );}
	 */

	public static float radiansToDegrees(float r) {
		return r * (180f / P.PI);
	}

	/**
	 *  Convert a number from a Percentage to Degrees (based on 360�).
	 *  @param  n percentage (1, .5)
	 *  @return degrees (360�, 180�)
	 *  @use    {@code var vDegreesPercent = MathUtil.percentToDegrees( 50 );}
	 */

	public static float percentToDegrees(float n) {
		return ((Math.abs(n / 100f)) * 360f) * 100f;	
	}

	/**
	 *  Convert a number from Degrees to a Percentage (based on 360�).
	 *  @param  n degrees (360�, 180�)
	 *  @return percentage (1, .5)
	 *  @use    {@code var vPercentDegrees = MathUtil.degreesToPercent( 180 );}
	 */

	public static float degreesToPercent(float n) {
		return (Math.abs(n / 360f));
	}

	/**
	 *	Linear interpolate between two values.  
	 *	@param		lower	first value (-1.0, 43.6)
	 *	@param		upper	second value (-100.0, 3.1415)
	 *	@param		n	point between values (0.0, 1.0)
	 * 	@return 		number (12.3, 44.555)
	 * 	@use			{@code var value = MathUtil.interp( 10, 20, .5 );  //returns 15}
	 */
	public static float interp(float lower, float upper, float n) {
		return ((upper - lower) * n) + lower;
	}

	/**   
	 *	Re-maps a number from one range to another. 
	 *	@param		value  The incoming value to be converted
	 *	@param		lower1 Lower bound of the value's current range
	 *	@param		upper1 Upper bound of the value's current range
	 *	@param		lower2 Lower bound of the value's target range
	 *	@param		upper2 Upper bound of the value's target range
	 * 	@return 	number (12.3, 44.555)
	 * 	@use		{@code var value = MathUtil.remap( 10, 0, 20, 1, 2 );  //returns 1.5}
	 */
	public static float remap(float value, float lower1, float upper1, float lower2, float upper2) {
		return interp(lower2, upper2, getPercentWithinRange(lower1, upper1, value) / 100f);
	}

	/**
	 * Rounds a number to a (generally) smaller number of decmiel places. Useful for making smaller floating point number to transmit.
	 * @param 	value				The number to round
	 * @param 	numDecimalPlaces	The number of decmiel places to round to
	 * @return
	 * @use    	{@code var floatNum = MathUtil.roundToPrecision( 0.5555555555, 3 );}
	 */
	public static float roundToPrecision(float value, int numDecimalPlaces) {
		float multiplyFactor = (float) Math.pow(10f, numDecimalPlaces);
	    float valueMultiplied = value * multiplyFactor;
	    return (float) Math.round(valueMultiplied) / multiplyFactor;
	}
	
	public static String roundToPrecision2(float value, int numDecimalPlaces) {
		String decimalPlaces = "";
		for (int i = 0; i < numDecimalPlaces; i++) decimalPlaces += "#";
		DecimalFormat df = new DecimalFormat("#."+decimalPlaces);
		String output = df.format(value);
		if(output.length() < numDecimalPlaces + 2) output += ".0";
		while(output.length() < numDecimalPlaces + 2) output += "0";
		return output;
	}

	/**
	 *  Keep an angle between 0-360
	 *  @param  angle 	the angle to constrain
	 *  @return The normalized angle
	 *  @use    {@code var angle = MathUtil.constrainAngle( 540 );}
	 */
	public static float constrainAngle(float angle) {
		if(angle < 0f) return angle + 360f;
		if(angle > 360f) return angle - 360f;
		return angle;
	};
	
	public static void rotateAroundPoint(PVector point, PVector anchor, float rads) {
		float radius = getDistance(point.x, point.y, anchor.x, anchor.y);
		float destRads = getRadiansToTarget(0, 0, point.x - anchor.x, point.y - anchor.y) + rads;
		point.set(anchor.x + radius * P.cos(destRads), anchor.y + radius * P.sin(destRads), point.z);
	}

	public static float circumferenceFromRadius(float radius) {
		return radius * P.TWO_PI;
	};

	/**
	 *  Get the angle fron current coordinate to target coordinate
	 *  @param  x1  first point's x position
	 *  @param  y1  first point's y position
	 *  @param  x2  second point's x position
	 *  @param  y2  second point's y position
	 *  @return The angle from point 1 and 2
	 *  @use    {@code var angle = MathUtil.getAngleToTarget( 0, 0, 5, 5 );}
	 */
	public static float getAngleToTarget(float x1, float y1, float x2, float y2) {
		return constrainAngle((float) -Math.atan2(x1 - x2, y1 - y2) * 180f / P.PI);
	};

	/**
	 *  Figures out which way to rotate, for the shortest path from current to target angle
	 *  @param  curAngle    starting angle
	 *  @param  targetAngle destination angle
	 *  @return +1 for clockwise, -1 for counter-clockwise
	 *  @use    {@code var direction = MathUtil.rotationDirectionToTarget( 90, 180 );}
	 */
	public static float getRotationDirectionToTarget(float curAngle, float targetAngle) {
		// calculate the difference between the current angle and destination angle
		float angleDifference = Math.abs(curAngle - targetAngle);
		// turn left or right to get to the target
		if(curAngle > targetAngle) {
			return (angleDifference < 180f) ? -1 : 1;
		} else {
			return (angleDifference < 180f) ? 1 : -1;
		}
	};

	/**
	 *  Get the radians fron current coordinate to target coordinate
	 *  @param  x1  first point's x position
	 *  @param  y1  first point's y position
	 *  @param  x2  second point's x position
	 *  @param  y2  second point's y position
	 *  @return The radians from point 1 and 2
	 *  @use    {@code var angle = MathUtil.getRadiansToTarget( 0, 0, 5, 5 );}
	 */
	public static float getRadiansToTargetWrong(float x1, float y1, float x2, float y2) {
		return (float) (Math.PI + Math.atan2( x1 - x2, y1 - y2 ));	// add PI to normalize between 0 - 2*pi. atan2 returnes -pi/2 - pi/2
	};
	
	public static float getRadiansToTarget(float x1, float y1, float x2, float y2) {
		return P.atan2(y2 - y1, x2 - x1) % P.TWO_PI;
	};


	/**
	 *  Figures out which way to rotate, for the shortest path from current to target angle
	 *  @param  curAngle    starting angle
	 *  @param  targetAngle destination angle
	 *  @return +1 for clockwise, -1 for counter-clockwise
	 *  @use    {@code var direction = MathUtil.getRadiansDirectionToTarget( 90, 180 );}
	 */
	public static float getRadiansDirectionToTarget(float curRadians, float targetRadians) {
		while(curRadians < P.TWO_PI) curRadians += P.TWO_PI;
		while(targetRadians < P.TWO_PI) targetRadians += P.TWO_PI;
		float rotDiff = curRadians - targetRadians;
		while(rotDiff < 0) rotDiff += P.TWO_PI;
		if(rotDiff < P.PI) return -1;
		else return 1;
	};
	
	// from: https://stackoverflow.com/a/2007279
	public static float getRadiansDiff(float radians1, float radians2) {
		return P.atan2(P.sin(radians2-radians1), P.cos(radians2-radians1));
	};

	public static float averageOfThree(float one, float two, float three) {
		return (one + two + three) / 3f;
	}
	
	public static float averageOfFour(float one, float two, float three, float four) {
		return (one + two + three + four) / 4f;
	}
	
	public static Point triangleCenter = new Point(0,0);
	public static Point computeTriangleCenter(float x1, float y1, float x2, float y2, float x3, float y3) {
		triangleCenter.setLocation(averageOfThree(x1, x2, x3), averageOfThree(y1, y2, y3));
		return triangleCenter;
	};

	public static Point quadCenter = new Point(0,0);
	public static Point computeQuadCenter(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		quadCenter.setLocation( averageOfFour(x1, x2, x3, x4), averageOfFour(y1, y2, y3, y4));
		return quadCenter;
	};

	public static PVector triangle3dCenter = new PVector(0,0,0);
	public static PVector computeTriangleCenter(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
		triangle3dCenter.set(averageOfThree(x1, x2, x3), averageOfThree(y1, y2, y3), averageOfThree(z1, z2, z3));
		return triangle3dCenter;
	};
	
	public static PVector computeTriangleCenter( PVector v1, PVector v2, PVector v3 ) {
		triangle3dCenter.set(averageOfThree(v1.x, v2.x, v3.x), averageOfThree(v1.y, v2.y, v3.y), averageOfThree(v1.z, v2.z, v3.z));
		return triangle3dCenter;
	};
	
	public static PVector quad3dCenter = new PVector(0,0,0);
	public static PVector computeQuadCenter(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
		quad3dCenter.set(averageOfFour(x1, x2, x3, x4), averageOfFour(y1, y2, y3, y4), averageOfFour(z1, z2, z3, z4));
		return quad3dCenter;
	};
	
	public static PVector computeQuadCenter(PVector v1, PVector v2, PVector v3, PVector v4) {
		quad3dCenter.set(averageOfFour(v1.x, v2.x, v3.x, v4.x), averageOfFour(v1.y, v2.y, v3.y, v4.y), averageOfFour(v1.z, v2.z, v3.z, v4.z));
		return quad3dCenter;
	};
	
	public static float circleSizeAroundSquare(float squareSize) {
		return squareSize * P.sqrt(2f);
	}
	
	public static float circleSizeAroundRect(float rectWidth, float rectHeight) {
		return P.sqrt(rectWidth * rectWidth + rectHeight * rectHeight);
	}
	
	public static float fract(float f) {
	    return f % 1f;
	}
	
	public static int gridXFromIndex(int index, int detail) {
		return index % detail;
	}

	public static int gridYFromIndex(int index, int detail) {
		return P.floor((index / detail) % detail);
	}
	
	public static int gridZFromIndex(int index, float detail) {
		return P.floor(index / (detail * detail));
	}
	
	public static int gridIndexFromXY(int x, int y, int width) {
		return y * width + x;
	}
	
	public static float mapCoordToCircular(float x, float y, float boundsW, float boundsH) {
		float unitY = P.map(y, 0, boundsH, -1, 1);
		float unitX = P.map(x, 0, boundsW, -1, 1);
		unitY = P.constrain(unitY, -1, 1);	// we can get NaN if we don't constrain here
		float xAmp = P.cos(P.asin(unitY));	// pinch amp at poles
		float halfW = boundsW / 2f;
		float xRemapped = halfW + (halfW * unitX * xAmp);
		return xRemapped; 
	}
	
	public static float saw(float rads) {
	    rads = rads / P.TWO_PI + P.PI / 4f; // add quarter PI to sync with sin()
	    rads = fract(rads); 
	    return -1f + 2f * P.abs(rads * 2f - 1f);
	}
	
	public static float sawTan(float rads) {
		rads += P.PI;
		float percent = (rads % P.TWO_PI) / P.TWO_PI;
		percent *= 2;
		percent -= 1;
		return percent;
	}
	
	public static float cuberoot(float val) {
		return P.pow(val, 1/3f);
	}	
	
	public static int[] buildIndexArray(int size) {
		int[] indexArray = new int[size];
		for (int i = 0; i < size; i++) {
			indexArray[i] = i;
		}
		return indexArray;
	}
	
	public static void shuffleIntArray(int[] ar) {
		Random rnd = ThreadLocalRandom.current();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}
}
