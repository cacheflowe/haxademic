package com.haxademic.core.data;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;

public class ArrayUtil {

	public static void fillArrayWithUniqueIndexes(int[] arr, int maxIndex) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = MathUtil.randRange(0, maxIndex);
			while(valueExistsInArr(arr, arr[i], i)) arr[i] = MathUtil.randRange(0, maxIndex);
		}
	}
	
	public static boolean valueExistsInArr(int[] arr, int val, int curIndex) {
		for (int i = 0; i < curIndex; i++) {
			if(arr[i] == val) return true;
		}
		return false;
	}
	
	public static void crossfadeEnds(float[] array, float fadeSize /* 0.1 */) {
	    // number of elements to fade on either end
	    int numToFade = Math.round(array.length * fadeSize);
	    // average of start/end values
	    float endAvg = (array[array.length - 1] + array[0]) / 2f;
	    for(int i = 0; i <= numToFade; i++) {
	    	// lerp strength increases
	    	float lerpStrength = (float) i / numToFade;
	    	// indices go from inland towards the edges, increasingly fading towards the ends average
	    	int endIndex = array.length - numToFade - 1 + i;
	    	float linearValEnd = P.lerp(endAvg, array[array.length - numToFade - 1], Penner.easeInOutSine(1-lerpStrength));
	    	array[endIndex] = P.lerp(array[endIndex], linearValEnd, Penner.easeInOutSine(lerpStrength));
	    	int startIndex = numToFade - i;
	    	float linearVal = P.lerp(endAvg, array[numToFade], Penner.easeInOutSine(lerpStrength));
	    	array[i] = P.lerp(array[i], linearVal, Penner.easeInOutSine(1-lerpStrength));
//	    	P.out(lerpStrength);
	    }
	}

}
