package com.haxademic.core.data;

import com.haxademic.core.math.MathUtil;

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
	

}
