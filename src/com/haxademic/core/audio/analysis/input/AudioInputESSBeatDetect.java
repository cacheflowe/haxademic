package com.haxademic.core.audio.analysis.input;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;

import krister.Ess.AudioInput;
import krister.Ess.FFT;
import processing.core.PApplet;

public class AudioInputESSBeatDetect
{
	PApplet p;

	private boolean isOnset;
	private FFT spect;
	private int bins;
	private float sampleRate;
	private int currFrame;
	private int valCnt;
	private float[] valGraph;
	
	public static final int SOUND_ENERGY = 0;
	public static final int FREQ_ENERGY = 1;
	private int algorithm;
	
	// vars for sEnergy
	private float[] eBuffer;
	private float[] dBuffer;

	// vars for fEnergy
	private float[][] feBuffer;
	private float[][] fdBuffer;
	private boolean[] fIsOnset;
	private float[] varGraph;
	private int varCnt;
	private int feNum;

	AudioInputESSBeatDetect()
	{
		init(1024, 44100);
	}

	public AudioInputESSBeatDetect(int s, float r)
	{
		p = P.p;
		init(s, r);
	}

	private void init(int s, float r) {
		DebugUtil.printErr("Convert BeatDetect to use AudioStreamData, not just ESS");
		bins = s;
		sampleRate = r;
		algorithm = FREQ_ENERGY;
		isOnset = false;
		currFrame = p.frameCount;
		spect = new FFT(bins*2);
		spect.damp(1.0f);
		valCnt = varCnt = 0;
		valGraph = new float[p.width];
		varGraph = new float[p.width];
		// sEnergy vars: energy buffer, difference buffer
		eBuffer = new float[(int)(r/s)];
		dBuffer = new float[15];
		// fEnergy vars: number of averages, energy buffers, difference buffers, onset array
		detectDetail(64);
	}

	public void detect(AudioInput in)
	{
		switch ( algorithm )
		{
		case 1: sEnergy(in); break;
		case 2: fEnergy(in); break;
		}
	}

	public boolean isOnset()
	{
		return isOnset || isRange(0, feNum-1, feNum/4);
	}

	public boolean isOnset(int i)
	{
		return fIsOnset[i];
	}

	public boolean isKick()
	{
		return fIsOnset[0];
	}

	public boolean isSnare()
	{
		int low = 1;
		int hi = (int)(feNum/3);
		int thresh = (int) ( (hi-low)/3 );
		return isRange(low, hi, thresh);
	}

	public boolean isHat()
	{
		int low = (int)(feNum/2);
		int hi = feNum-1;
		int thresh = (int)( (hi-low)/3 );
		return isRange(low, hi, thresh);
	}

	public boolean isRange(int low, int high, int threshold)
	{
		int num = 0;
		for (int i = low; i < high+1; i++) if ( isOnset(i) ) num++;
		return num > threshold;
	}

	public void detectMode( int algo )
	{
		if ( algo == SOUND_ENERGY ) 
		{
			algorithm = 1;
			fIsOnset = new boolean[feNum];
		}
		else if ( algo == FREQ_ENERGY ) 
		{
			algorithm = 2;
			valCnt = varCnt = 0;
			valGraph = new float[p.width];
			varGraph = new float[p.width];
		}      
	}

	public void detectDetail( int num )
	{
		feNum = num;
		spect.averages(num);
		feBuffer = new float[num][(int)(sampleRate/bins)];
		fdBuffer = new float[num][15];
		fIsOnset = new boolean[num];
	}

	public void drawGraph()
	{
		// draw valGraph
		for (int i = 0; i < valCnt; i++)
		{
			p.stroke(255);
			p.line(i, (p.height/2) - valGraph[i], i, (p.height/2) + valGraph[i]);
		}
		// draw varGraph
		for (int i = 0; i < varCnt-1; i++)
		{
			p.stroke(255);
			p.line(i, p.height - varGraph[i], i+1, p.height - varGraph[i+1]);
		}
		p.strokeWeight(5);
		for (int i = 0; i < fIsOnset.length; i++)
		{
			int c = (i%8 == 0) ? p.color(255, 0, 0) : p.color(255);
			p.stroke(c);
			if ( fIsOnset[i] ) p.line((i*10), p.height - 100, (i*10), p.height);
		}
	} 

	private void sEnergy(AudioInput in)
	{
		float instant = spect.getLevel(in)*100;
		// compute the average local energy
		float E = average(eBuffer);
		// compute the variance of the energies in eBuffer
		float V = variance(eBuffer, E);
		// compute C using a linear digression of C with V
		float C = (-0.0025714f*V) + 1.5142857f;
		float diff = PApplet.max(instant - C*E, 0);
		pushVal(diff);
		// find the average of only the positive values in dBuffer
		float dAvg = specAverage(dBuffer);
		// filter negative values
		float diff2 = PApplet.max(diff - dAvg, 0);
		pushVar(diff2);
		if ( isOnset && currFrame == p.frameCount ) isOnset = true;
		else if ( currFrame < p.frameCount && isOnset ) isOnset = false;
		else isOnset = diff2 > 0 && instant > 2;
		shift(eBuffer, instant);
		shift(dBuffer, diff);
		currFrame = p.frameCount;
	}

	private void fEnergy(AudioInput in)
	{
		float instant, E, V, C, diff, dAvg, diff2;
		spect.getSpectrum(in);
		for (int i = 0; i < feNum; i++)
		{
			instant = (i+1)*spect.averages[i]*2000;
			E = average(feBuffer[i]);
			V = variance(feBuffer[i], E);
			C = (-0.0025714f*V) + 1.5142857f;
			diff = P.max(instant - C*E, 0);
			dAvg = specAverage(fdBuffer[i]);
			diff2 = P.max(diff - dAvg, 0);
			if ( fIsOnset[i] && currFrame == p.frameCount ) fIsOnset[i] = true;
			else if ( currFrame < p.frameCount && fIsOnset[i] ) fIsOnset[i] = false;
			else fIsOnset[i] = diff2 > 0 && instant > 2;
			shift(feBuffer[i], instant);
			shift(fdBuffer[i], diff);
		}
		currFrame = p.frameCount;    
	}

	private void pushVal(float v)
	{
		// println(valCnt);
		if ( valCnt == p.width)
		{
			valCnt = 0;
			valGraph = new float[p.width];
		}
		valGraph[valCnt] = v;
		valCnt++;
	}

	private void pushVar(float v)
	{
		// println(valCnt);
		if ( varCnt == p.width)
		{
			varCnt = 0;
			varGraph = new float[p.width];
		}
		varGraph[varCnt] = v;
		varCnt++;
	} 

	private void shift(float[] arr, float val)
	{
		System.arraycopy(arr, 0, arr, 1, arr.length-1);
		arr[0] = val;
	}

	private float average(float[] arr)
	{
		float avg = 0;
		for (int i = 0; i < arr.length; i++) avg += arr[i];
		avg /= arr.length;
		return avg;
	}

	private float specAverage(float[] arr)
	{
		float avg = 0;
		float num = 0;
		for (int i = 0; i < arr.length; i++) 
		{
			if ( arr[i] > 0 ) 
			{
				avg += arr[i];
				num++;
			}
		}
		if ( num > 0 ) avg /= num;
		return avg;
	}

	private float variance(float[] arr, float val)
	{
		float V = 0;
		for (int i = 0; i < arr.length; i++) V += P.pow(arr[i]-val, 2);
		V /= arr.length;
		return V;
	}   
}


