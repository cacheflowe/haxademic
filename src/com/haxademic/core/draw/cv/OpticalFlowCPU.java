package com.haxademic.core.draw.cv;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class OpticalFlowCPU {

	// From: http://www.openprocessing.org/sketch/84287
	/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/10435*@* */
	/* !do not delete the line above, required for linking your tweak if you re-upload */
	// Optical Flow 2010/05/28
	// Hidetoshi Shimodaira shimo@is.titech.ac.jp 2010 GPL
	// Componentized & further optimized by @cacheflowe

	protected PGraphics source;
	protected float scale;
	protected float scaleUp;

	// capture parameters
	protected int fps = 60;
	protected int analyzeW;
	protected int analyzeH;

	// grid parameters
	protected int gridStep=2; // grid step (pixels). should be no less than 2, because of `gs2 = gridStep/2`
	protected float predsec=1.0f; // prediction time (sec): larger for longer vector

	protected int as = gridStep*1;  // window size for averaging (-as,...,+as)
	protected int gw;
	protected int gh;
	protected int gs2;
	protected float df = predsec*fps;

	// regression vectors
	protected float[] fx, fy, ft;
	protected int fm = 3*9; // length of the vectors

	// regularization term for regression
	protected float fc = P.pow(10,8); // larger values for noisy video

	// smoothing parameters
	protected float smoothing = 0.1f; // smaller value for longer smoothing

	// switch
	protected boolean flagmovie=false; // saving movie?

	// internally used variables
	protected float ar,ag,ab; // used as return value of pixave
	protected float[] dtr, dtg, dtb; // differentiation by t (red,gree,blue)
	protected float[] dxr, dxg, dxb; // differentiation by x (red,gree,blue)
	protected float[] dyr, dyg, dyb; // differentiation by y (red,gree,blue)
	protected float[] par, pag, pab; // averaged grid values (red,gree,blue)
	protected float[] flowx, flowy; // computed optical flow
	protected float[] sflowx, sflowy; // slowly changing version of the flow
	protected int clockNow,clockPrev, clockDiff; // for timing check

	// data buffer for shaders
	protected PGraphics dataBuffer;

	public OpticalFlowCPU(PImage sourceImg, float scale) {
		this.scale = scale;
		scaleUp = 1f / scale;
		analyzeW = P.round(scale * sourceImg.width);
		analyzeH = P.round(scale * sourceImg.height);
		source = PG.newPG(analyzeW, analyzeH, false, false);
		source.noSmooth();

		// screen size
		// make sure internal grid is even-number width x height
		gw = analyzeW / gridStep;
		if(gw % 2 == 1) gw--;
		gh = analyzeH / gridStep;
		if(gh % 2 == 1) gh--;
		gs2 = gridStep / 2;

		// arrays
		par = new float[gw*gh];
		pag = new float[gw*gh];
		pab = new float[gw*gh];
		dtr = new float[gw*gh];
		dtg = new float[gw*gh];
		dtb = new float[gw*gh];
		dxr = new float[gw*gh];
		dxg = new float[gw*gh];
		dxb = new float[gw*gh];
		dyr = new float[gw*gh];
		dyg = new float[gw*gh];
		dyb = new float[gw*gh];
		flowx = new float[gw*gh];
		flowy = new float[gw*gh];
		sflowx = new float[gw*gh];
		sflowy = new float[gw*gh];
		fx = new float[fm];
		fy = new float[fm];
		ft = new float[fm];
	}
	
	// getters
	
	public int analyzeW() {
		return analyzeW;
	}
	
	public int analyzeH() {
		return analyzeH;
	}
	
	public float scaleUp() {
		return scaleUp;
	}
	
	protected float[] outputVec = new float[2];
	public float[] getVectorAt(float x, float y) {
		// get normalized position to local smoothed data position
		// edges are ignored in calculations, so ignore outer col/row, but -2 on the indexes because that's how far the grid data goes
		int ix = P.floor(1 + x * (gw - 2.001f));
		int iy = P.floor(1 + y * (gh - 2.001f));
		// get array index
		int ig = iy * gw + ix;
		// set output vector
		outputVec[0] = df * sflowx[ig];
		outputVec[1] = df * sflowy[ig];
		return outputVec;
	}
	
	// setters

	public void smoothing(float smooth) {
		smoothing = smooth;
	}
	
	// debug
	
	public void debugDraw(PGraphics pg) {
		debugDraw(pg, true);
	}
	
	public void debugDraw(PGraphics pg, boolean colors) {
		// NOTICE! Make sure to beginDraw/endDraw if PGraphics
		for(int ix=0;ix<gw;ix++) {
			int x0=ix*gridStep+gs2;
			for(int iy=0;iy<gh;iy++) {
				int y0=iy*gridStep+gs2;
				int ig=iy*gw+ix;

				float u=df*sflowx[ig];
				float v=df*sflowy[ig];

				// draw the line segments for optical flow
				float a=P.sqrt(u*u+v*v);
				if(a>=0.1f) { // draw only if the length >=2.0
					float r=0.5f*(1.0f+u/(a+0.1f));
					float g=0.5f*(1.0f+v/(a+0.1f));
					float b=0.5f*(2.0f-(r+g));
					if(colors == false) pg.stroke(255);
					else pg.stroke(255*r,255*g,255*b);
					pg.line(x0*scaleUp,y0*scaleUp,x0*scaleUp+u*scaleUp,y0*scaleUp+v*scaleUp);
				}
			}
		}
	}
	
	public void drawDataBuffer() {
		// lazy-init data buffer when called
		if(dataBuffer == null) dataBuffer = PG.newPG(analyzeW, analyzeH, true, true);
		
		// draw data pixels
		dataBuffer.beginDraw();
		dataBuffer.noStroke();
		dataBuffer.background(127);
		for(int ix=0;ix<gw;ix++) {
			int x0=ix*gridStep+gs2;
			for(int iy=0;iy<gh;iy++) {
				int y0=iy*gridStep+gs2;
				int ig=iy*gw+ix;

				float u=df*sflowx[ig];
				float v=df*sflowy[ig];

				// draw the line segments for optical flow
//				float a=P.sqrt(u*u+v*v);
//				if(a>=0.1f) { // draw only if the length >=2.0
					dataBuffer.fill(127 + u, 127 + v);
					dataBuffer.rect(x0,y0,1,1);
//				}
			}
		}

		dataBuffer.endDraw();
	}
	
	// OPTICAL FLOW 

	public void update(PImage newFrame) {
		// int analyzeStart = P.p.millis();

		// copy image to current buffer
		ImageUtil.copyImage(newFrame, source);

		// clock in msec
		clockNow = P.p.millis();
		clockDiff = clockNow - clockPrev;
		clockPrev = clockNow;

		// load pixels for image reading
		source.loadPixels();

		// 1st sweep : differentiation by time
		for(int ix=0;ix<gw;ix++) {
			int x0=ix*gridStep+gs2;
			for(int iy=0;iy<gh;iy++) {
				int y0=iy*gridStep+gs2;
				int ig=iy*gw+ix;
				// compute average pixel at (x0,y0)
				pixave(x0-as,y0-as,x0+as,y0+as);
				// compute time difference
				dtr[ig] = ar-par[ig]; // red
				dtg[ig] = ag-pag[ig]; // green
				dtb[ig] = ab-pab[ig]; // blue
				// save the pixel
				par[ig]=ar;
				pag[ig]=ag;
				pab[ig]=ab;
			}
		}

		// 2nd sweep : differentiations by x and y
		for(int ix=1;ix<gw-1;ix++) {
			for(int iy=1;iy<gh-1;iy++) {
				int ig=iy*gw+ix;
				// compute x difference
				dxr[ig] = par[ig+1]-par[ig-1]; // red
				dxg[ig] = pag[ig+1]-pag[ig-1]; // green
				dxb[ig] = pab[ig+1]-pab[ig-1]; // blue
				// compute y difference
				dyr[ig] = par[ig+gw]-par[ig-gw]; // red
				dyg[ig] = pag[ig+gw]-pag[ig-gw]; // green
				dyb[ig] = pab[ig+gw]-pab[ig-gw]; // blue
			}
		}

		// 3rd sweep : solving optical flow
		for(int ix=1;ix<gw-1;ix++) {
			// int x0=ix*gridStep+gs2;
			for(int iy=1;iy<gh-1;iy++) {
				// int y0=iy*gridStep+gs2;
				int ig=iy*gw+ix;

				// prepare vectors fx, fy, ft
				getnext9(dxr,fx,ig,0); // dx red
				getnext9(dxg,fx,ig,9); // dx green
				getnext9(dxb,fx,ig,18);// dx blue
				getnext9(dyr,fy,ig,0); // dy red
				getnext9(dyg,fy,ig,9); // dy green
				getnext9(dyb,fy,ig,18);// dy blue
				getnext9(dtr,ft,ig,0); // dt red
				getnext9(dtg,ft,ig,9); // dt green
				getnext9(dtb,ft,ig,18);// dt blue

				// solve for (flowx, flowy) such that
				// fx flowx + fy flowy + ft = 0
				solveflow(ig);

				// smoothing
				sflowx[ig]+=(flowx[ig]-sflowx[ig])*smoothing;
				sflowy[ig]+=(flowy[ig]-sflowy[ig])*smoothing;
			}
		}

		// debug props
//		DebugView.setValue("OpticalFlow time", (P.p.millis() - analyzeStart)+"ms");
//		DebugView.setValue("OpticalFlow size", analyzeW+"x"+analyzeH);
	}
	
	// calculate average pixel value (r,g,b) for rectangle region
	protected void pixave(int x1, int y1, int x2, int y2) {
		float sumr,sumg,sumb;
		int pix;
		int r,g,b;
		int n;

		if(x1<0) x1=0;
		if(x2>=analyzeW) x2=analyzeW-1;
		if(y1<0) y1=0;
		if(y2>=analyzeH) y2=analyzeH-1;

		sumr=sumg=sumb=0.0f;
		for(int y=y1; y<=y2; y++) {
			for(int i=analyzeW*y+x1; i<=analyzeW*y+x2; i++) {
				pix=source.pixels[i];
				b=pix & 0xFF; // blue
				pix = pix >> 8;
				g=pix & 0xFF; // green
				pix = pix >> 8;
				r=pix & 0xFF; // red
				// averaging the values
				sumr += r;
				sumg += g;
				sumb += b;
			}
		}
		n = (x2-x1+1)*(y2-y1+1); // number of pixels
		// the results are stored in static variables
		ar = sumr/n;
		ag=sumg/n;
		ab=sumb/n;
	}

	// extract values from 9 neighbour grids
	protected void getnext9(float x[], float y[], int i, int j) {
		y[j+0] = x[i+0];
		y[j+1] = x[i-1];
		y[j+2] = x[i+1];
		y[j+3] = x[i-gw];
		y[j+4] = x[i+gw];
		y[j+5] = x[i-gw-1];
		y[j+6] = x[i-gw+1];
		y[j+7] = x[i+gw-1];
		y[j+8] = x[i+gw+1];
	}

	// solve optical flow by least squares (regression analysis)
	protected void solveflow(int ig) {
		float xx, xy, yy, xt, yt;
		float a,u,v;

		// prepare covariances
		xx=xy=yy=xt=yt=0.0f;
		for(int i=0;i<fm;i++) {
			xx += fx[i]*fx[i];
			xy += fx[i]*fy[i];
			yy += fy[i]*fy[i];
			xt += fx[i]*ft[i];
			yt += fy[i]*ft[i];
		}

		// least squares computation
		a = xx*yy - xy*xy + fc; // fc is for stable computation
		u = yy*xt - xy*yt; // x direction
		v = xx*yt - xy*xt; // y direction

		// write back
		flowx[ig] = -2*gridStep*u/a; // optical flow x (pixel per frame)
		flowy[ig] = -2*gridStep*v/a; // optical flow y (pixel per frame)
	}

}

