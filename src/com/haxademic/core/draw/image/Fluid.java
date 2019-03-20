package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;

import processing.core.PGraphics;

public class Fluid {
	
	// from: Mike Ash & Daniel Shiffman: 
	// https://github.com/shiffman/CFD/tree/master/CFDash

	protected int scale = 1;
	protected int width;
	protected int height;
	protected int iter = 2;
	protected float fadeAmp = 0.01f;

	protected int size;
	protected float dt;
	protected float diff;
	protected float visc;
	protected float[] s;
	protected float[] density;
	protected float[] vx;
	protected float[] vy;
	protected float[] vx0;
	protected float[] vy0;

	public Fluid(int width, int height, float diffusion, float viscosity, float dt) {
		this.width = width;
		this.height = height;
		this.dt = dt;
		this.diff = diffusion;
		this.visc = viscosity;
		this.s = new float[width*height];
		this.density = new float[width*height];
		this.vx = new float[width*height];
		this.vy = new float[width*height];
		this.vx0 = new float[width*height];
		this.vy0 = new float[width*height];
	}
	
	public void scale(int scale) { this.scale = scale; }
	public int scale() { return scale; }
	public void fadeAmp(float fadeAmp) { this.fadeAmp = fadeAmp; }
	public float fadeAmp() { return fadeAmp; }

	public int index(int x, int y) {
		x = P.constrain(x, 0, width-1);
		y = P.constrain(y, 0, height-1);
		return x + y * width;
	}


	public void step() {
		iter = 5;
		diffuse(1, vx0, vx, visc, dt, iter);
		diffuse(2, vy0, vy, visc, dt, iter);

		project(vx0, vy0, vx, vy, iter);

		advect(1, vx, vx0, vx0, vy0, dt);
		advect(2, vy, vy0, vx0, vy0, dt);

		project(vx, vy, vx0, vy0, iter);

		diffuse(0, s, density, diff, dt, iter);
		advect(0, density, s, vx, vy, dt);
	}

	public void addDensity(int x, int y, float amount) {
		if(x >= width-1) return;
		if(y >= height-1) return;
		
		int indx = index(x, y);
		this.density[indx] += amount;
		if(this.density[indx] > 1000) this.density[indx] = 1000;
	}

	public void addVelocity(int x, int y, float amountX, float amountY) {
		if(x >= width-1) return;
		if(y >= height-1) return;
		
		int index = index(x, y);
		this.vx[index] += amountX;
		this.vy[index] += amountY;
	}

	public void renderV(PGraphics pg) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				float x = i * scale;
				float y = j * scale;
				pg.stroke(255);
				pg.strokeWeight(1);
				float vx = this.vx[index(i, j)];
				float vy = this.vy[index(i, j)];
				if (vx + vy > 0.05) {
					pg.line(x, y, x + scale*vx, y + scale*vy);
				}
			}
		}
	}

	public void renderD(PGraphics pg) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				float x = i * scale;
				float y = j * scale;
				pg.noStroke();
				pg.fill(255, this.density[index(i, j)]);
				pg.rect(x, y, scale, scale);
			}
		}
	}

	public void fadeD() {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				this.density[index(i, j)] -= fadeAmp;
			}
		}
	}


	public void set_bnd ( int b, float[] x ) {
		for (int i=1; i<height-1; i++ ) {
			x[index(0, i)] = b==1 ? -x[index(1, i)] : x[index(1, i)];
			x[index(width-1, i)] = b==1 ? -x[index(width-1, i)] : x[index(width-1, i)];
		}
		for (int i=1; i<width-1; i++ ) {
			x[index(i, 0  )] = b==2 ? -x[index(i, 1)] : x[index(i, 1)];
			x[index(i, height-1)] = b==2 ? -x[index(i, height-1)] : x[index(i, height-1)];
		}
		x[index(0, 0  )] = 0.5f*(x[index(1, 0  )]+x[index(0, 1)]);
		x[index(0, height-1)] = 0.5f*(x[index(1, height-1)]+x[index(0, height-1)]);
		x[index(width-1, 0  )] = 0.5f*(x[index(width-2, 0  )]+x[index(width-1, 1)]);
		x[index(width-1, height-1)] = 0.5f*(x[index(width-2, height-1)]+x[index(width-1, height-2)]);
	}

	public void lin_solve(int b, float[]x, float[] x0, float a, float c, int iter) {
		float cRecip = 1f / c;
		for (int k = 0; k < iter; k++) {
			for (int j = 1; j < height - 1; j++) {
				for (int i = 1; i < width - 1; i++) {
					x[index(i, j)] =
							(x0[index(i, j)]
									+ a*
									(x[index(i+1, j)]
											+x[index(i-1, j)]
													+x[index(i, j+1)]
															+x[index(i, j-1)]
											)) * cRecip;
				}
			}
		}
		set_bnd(b, x);
	}


	public void diffuse (int b, float[] x, float[] x0, float diff, float dt, int iter) {
		float a = dt * diff * (width - 2) * (height - 2);
		lin_solve(b, x, x0, a, 1 + 6 * a, iter);
	}

	public void project(float[] velocX, float[] velocY, float[] p, float[] div, int iter) {
		for (int j = 1; j < height - 1; j++) {
			for (int i = 1; i < width - 1; i++) {
				div[index(i, j)] = -0.5f*(
						velocX[index(i+1, j)]
								-velocX[index(i-1, j)]
										+velocY[index(i, j+1)]
												-velocY[index(i, j-1)]
						)/((width+height)*0.5f);
				p[index(i, j)] = 0;
			}
		}
		set_bnd(0, div); 
		set_bnd(0, p);
		lin_solve(0, p, div, 1, 6, iter);

		for (int j = 1; j < height - 1; j++) {
			for (int i = 1; i < width - 1; i++) {
				velocX[index(i, j)] -= 0.5f * (  p[index(i+1, j)]
						-p[index(i-1, j)]) * width;
				velocY[index(i, j)] -= 0.5f * (  p[index(i, j+1)]
						-p[index(i, j-1)]) * height;
			}
		}

		set_bnd(1, velocX);
		set_bnd(2, velocY);
	}

	public void advect(int b, float[] d, float[] d0, float[] velocX, float[] velocY, float dt) {
		float i0, i1, j0, j1;

		float dtx = dt * (width - 2);
		float dty = dt * (height - 2);

		float s0, s1, t0, t1;
		float tmp1, tmp2, x, y;

		float NXfloat = width;
		float NYfloat = height;
		float ifloat, jfloat;
		int i, j;

		for (j = 1, jfloat = 1; j < height - 1; j++, jfloat++) { 
			for (i = 1, ifloat = 1; i < width - 1; i++, ifloat++) {
				tmp1 = dtx * velocX[index(i, j)];
				tmp2 = dty * velocY[index(i, j)];
				x    = ifloat - tmp1; 
				y    = jfloat - tmp2;

				if (x < 0.5f) x = 0.5f; 
				if (x > NXfloat + 0.5f) x = NXfloat + 0.5f; 
				i0 = P.floor(x); 
				i1 = i0 + 1.0f;
				if (y < 0.5f) y = 0.5f; 
				if (y > NYfloat + 0.5f) y = NYfloat + 0.5f; 
				j0 = P.floor(y);
				j1 = j0 + 1.0f; 

				s1 = x - i0; 
				s0 = 1.0f - s1; 
				t1 = y - j0; 
				t0 = 1.0f - t1;

				int i0i = (int)i0;
				int i1i = (int)i1;
				int j0i = (int)j0;
				int j1i = (int)j1;

				d[index(i, j)] = 
						s0 * ( t0 * d0[index(i0i, j0i)] + t1 * d0[index(i0i, j1i)]) + 
						s1 * ( t0 * d0[index(i1i, j0i)] + t1 * d0[index(i1i, j1i)]);
			}
		}

		set_bnd(b, d);
	}
}