package com.haxademic.core.math.easing;

/**
 * Stores an array of values, intended to be the 
 * last [x] frames of data over time, allowing the 
 * developer to keep track of the most recent 
 * sampling of data from input, movement, or other
 * constantly-changing values that need to be smoothed.  
 */
public class FloatBuffer {
	
	protected int size;
	protected int sampleIndex;
	protected float[] buffer;
	
	/**
	 * Build a FloatBuffer object at a fixed size.
	 * @param size	Number of indexes in the buffer array
	 */
	public FloatBuffer( int size ) {
		initBuffer( size );
	}
	
	protected void initBuffer( int size ) {
		this.size = size;
		sampleIndex = 0;
		buffer = new float[size];
		for( int i=0; i < size; i++) buffer[i] = 0;
	}
	
	/**
	 * Overwrite the oldest value in the buffer with a new value.
	 * @param update		The new float value
	 * @return				The FloatBuffer instance, for chaining
	 */
	public FloatBuffer update( float value ) {
		sampleIndex++;
		if(sampleIndex == size) sampleIndex = 0;
		buffer[sampleIndex] = value;
		return this;
	};
	
	/**
	 * Returns a sum of the buffer values
	 * @return		A sum of the values in the buffer
	 */
	public float sum() {
		float sum = 0;
		for( int i=0; i < size; i++ ) {
			sum += buffer[i];
		}
		return sum;
	};
	
	/**
	 * Returns a sum of the positive buffer values
	 * @return		A sum of the positive values in the buffer
	 */
	public float sumPos() {
		float sum = 0;
		for( int i=0; i < size; i++ ) {
			if(buffer[i] > 0) sum += buffer[i];
		}
		return sum;
	};
	
	/**
	 * Returns a sum of the negative buffer values
	 * @return		A sum of the positive values in the buffer
	 */
	public float sumNeg() {
		float sum = 0;
		for( int i=0; i < size; i++ ) {
			if(buffer[i] < 0) sum += buffer[i];
		}
		return sum;
	};
	
	/**
	 * Returns a sum of the numbers in the buffer, run through Math.abs()
	 * @return		A sum of the absolute positive values in the buffer
	 */
	public float absSum() {
		int absSum = 0;
		for( int i=0; i < size; i++) {
			absSum += Math.abs(buffer[i]);
		}
		return absSum;
	};
	
	/**
	 * Returns the most positive value in the buffer
	 * @return		The most positive value in the buffer
	 */
	public float max() {
		float max = buffer[0];
		for( int i=0; i < size; i++) {
			if( buffer[i] > max ) max = buffer[i];
		}
		return max;
	};

	/**
	 * Returns the most negative value in the buffer
	 * @return		The most negative value in the buffer
	 */
	public float min() {
		float min = buffer[0];
		for( int i=0; i < size; i++) {
			if( buffer[i] < min ) min = buffer[i];
		}
		return min;
	};

	/**
	 * Returns the most positive absolute value in the buffer
	 * @return		The most positive absolute value in the buffer
	 */
	public float absMax() {
		float max = Math.abs(buffer[0]);
		for( int i=0; i < size; i++) {
			if( Math.abs(buffer[i]) > max ) max = Math.abs(buffer[i]);
		}
		return max;
	};
	
	/**
	 * Returns the average value of the buffer
	 * @return		The average value of the buffer
	 */
	public float average() {
		return sum() / size;
	};
	
	/**
	 * Returns the overall difference between values in the buffer
	 * @return		The total variance value of the buffer
	 */
	public float variance() {
		float variance = 0;
		for(int i=0; i < size - 1; i++) {
			variance += Math.abs(buffer[i] - buffer[i+1]);
		}
		return variance;
	};
	
	/**
	 * Returns first index of a needle value
	 * @return		The buffer index of needle
	 */
	public int indexOf(float needle) {
		for( int i=0; i < size; i++) {
			if( buffer[i] == needle) return i;
		}
		return -1;
	};
	
	/**
	 * Returns The a percentage of buffer values that aren't zero. Confidences decreases as zeros increase.
	 * @return		The percentage of buffer values that aren't zero
	 */
	public float confidence() {
		float numZeros = 0;
		for( int i=0; i < size; i++) {
			if( buffer[i] == 0 ) numZeros++;
		}
		return 1f - numZeros/size;
	};
}
