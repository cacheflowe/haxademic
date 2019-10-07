package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

public class FluidSimulateFlexi extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// from: http://www.openprocessing.org/sketch/103738
	// built by Felix Woitzel
	
	static final int numParticles=1024;
	Particle[] particles = new Particle[numParticles ];
	float v = 1/30;

	static final double GRAVITY = 128;
	static final float BOUNCE_DAMPENING = 1f/64f;
	private float radius = 5;

	private boolean grav = false;

	int decay = color(0, 0, 0, 16); // black with 16/256 % opacity

	NavierStokesSolver fluidSolver;
	float visc, diff, vScale, velocityScale;
	float  limitVelocity;
	int oldMouseX = 1, oldMouseY = 1;

	boolean vectors = true;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "512" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "512" );
	}

	public void setup() {
		super.setup();
		//size(512, 512);
		//frameRate(60);

		fluidSolver = new NavierStokesSolver();

		visc = 0.00025f;
		diff = 0.03f;
		vScale = 0;
		velocityScale = 8;

		limitVelocity = 200;
		textSize(20);

		stroke(color(0));
		fill(color(0));

		initParticles();
	}


	private void initParticles() {
		for (int i = 0; i < numParticles ; i++) {
			float x = random(0, width);
			float y = random(0, height);
			particles[i] = new Particle(x, y, radius);
			particles[i].charge = (float) 0.5;
		}
	}

	public void drawApp() {

		handleMouseMotion();

		//background(0, 0,0,256);

		fill(0, 0,0,11);

		rect(0,0,width,height);

		float dt = 1 / frameRate;
		fluidSolver.tick(dt, visc, diff);

		if (vectors)
			drawMotionVectorsImmediate( vScale*1.4f);

		vScale = velocityScale * 60.f / frameRate;
		drawParticles();

		stroke(256);

		text("fps: " + nf(frameRate, 2, 1), 10, 20);
	}

	int c = color(64,128,256); // particle pixel color


	private void drawParticles() {
		noStroke();
		particles[0].charge = 0;
		particles[1].charge = 1;
		updateParticles();

		for (int i = 0; i < numParticles; i++) {
			float ladung = particles[i].charge;
			fill((int) (128 + (ladung - 0.5) * 256),128,
					(int) (128 - (ladung - 0.5) * 256));
			int d = parseInt(particles[i].radius * 2);
			ellipse((float) particles[i].posX, (float) particles[i].posY, d, d);
		}
		int n = NavierStokesSolver.N;
		float cellHeight = height / n;
		float cellWidth = width / n;


		for (int i = 0; i < numParticles - 1; i++) {
			Particle p =  particles[i];
			if (p != null) {

				int cellX = floor(p.posX / cellWidth);
				int cellY = floor(p.posY / cellHeight);
				float dx =  fluidSolver.getDx(cellX, cellY);
				float dy =  fluidSolver.getDy(cellX, cellY);

				float lX = p.posX - cellX * cellWidth - cellWidth / 2;
				float lY = p.posY - cellY * cellHeight - cellHeight / 2;

				int v, h, vf, hf;

				if (lX > 0) {
					v = Math.min(n, cellX + 1);
					vf = 1;
				}
				else {
					v = Math.max(0, cellX - 1);
					vf = -1;
				}

				if (lY > 0) {
					h = Math.min(n, cellY + 1);
					hf = 1;
				}
				else {
					h = Math.max(0, cellY - 1);
					hf = -1;
				}

				float dxv =  fluidSolver.getDx(v, cellY);
				float dxh =  fluidSolver.getDx(cellX, h);
				float dxvh =  fluidSolver.getDx(v, h);

				float dyv =  fluidSolver.getDy(v, cellY);
				float dyh =  fluidSolver.getDy(cellX, h);
				float dyvh =  fluidSolver.getDy(v, h);

				dx = lerp(lerp(dx, dxv, vf * lX / cellWidth),
						lerp(dxh, dxvh, vf * lX / cellWidth),
						hf * lY / cellHeight);

				dy = lerp(lerp(dy, dyv, vf * lX / cellWidth),
						lerp(dyh, dyvh, vf * lX / cellWidth),
						hf * lY / cellHeight);

				p.posX += dx * vScale;
				p.posY += dy * vScale;
				/*
  if (p.posX < 0 || p.posX >= width) {
    p.posX = random(width);
  }
  if (p.posY < 0 || p.posY >= height) {
    p.posY = random(height);
  }
				 */
			}
		}
	}

	private void drawMotionVectorsImmediate(float l) {
		int n = NavierStokesSolver.N;
		float cellHeight = height / n;
		float cellWidth = width / n;
		float dx, dy, x, y, x1, y1, x2, y2, x3, y3;
		int i, j;

		float thick = 0.1f;

		beginShape(TRIANGLES);

		//noStroke();
		//stroke(256,128,0, 96);
		fill(256, 256, 256, 128);
		//noFill();
		for (i = 0; i < n; i++) {
			for (j = 0; j < n; j++) {

				dx =  fluidSolver.getDx(i, j);
				dy =  fluidSolver.getDy(i, j);

				x = cellWidth / 2 + cellWidth * i;
				y = cellHeight / 2 + cellHeight * j;

				x1 = x + dx * l;
				y1 = y + dy * l;

				x2 = x + dy * l * thick;
				y2 = y - dx * l * thick;

				x3 = x - dy * l * thick;
				y3 = y + dx * l * thick;

				// normal(0, 0, 1f);
				vertex(x1, y1);
				vertex(x2, y2);
				vertex(x3, y3);
			}
		}
		endShape();
	}

	private void handleMouseMotion() {
		mouseX = max(1, mouseX);
		mouseY = max(1, mouseY);

		int n = NavierStokesSolver.N;
		float cellHeight = height / n;
		float cellWidth = width / n;

		float mouseDx = mouseX - oldMouseX;
		float mouseDy = mouseY - oldMouseY;
		int cellX = floor(mouseX / cellWidth);
		int cellY = floor(mouseY / cellHeight);

		//mouseDx = (abs(mouseDx) > limitVelocity) ? Math.signum(mouseDx) * limitVelocity : mouseDx;
		//mouseDy = (abs(mouseDy) > limitVelocity) ? Math.signum(mouseDy) * limitVelocity : mouseDy;

		fluidSolver.applyForce(cellX, cellY, mouseDx, mouseDy);

		oldMouseX = mouseX;
		oldMouseY = mouseY;
	}

	/**
	 * Java implementation of the Navier-Stokes-Solver from
	 * http://www.dgp.toronto.edu/people/stam/reality/Research/pdf/GDC03.pdf
	 */
	public class NavierStokesSolver {
		final static int N = 16;
		final static int SIZE = (N + 2) * (N + 2);
		float[] u = new float[SIZE];
		float[] v = new float[SIZE];
		float[] u_prev = new float[SIZE];
		float[] v_prev = new float[SIZE];
		//    float[] dense = new float[SIZE];
		//    float[] dense_prev = new float[SIZE];

		public NavierStokesSolver() {
		}

		public float getDx(int x, int y) {
			return u[INDEX(x + 1, y + 1)];
		}

		public float getDy(int x, int y) {
			return v[INDEX(x + 1, y + 1)];
		}

		public void applyForce(int cellX, int cellY, float vx, float vy) {
			cellX += 1;
			cellY += 1;
			float dx = u[INDEX(cellX, cellY)];
			float dy = v[INDEX(cellX, cellY)];

			u[INDEX(cellX, cellY)] = (vx != 0) ? lerp( vx,
					dx, 0.85f) : dx;
			v[INDEX(cellX, cellY)] = (vy != 0) ? lerp( vy,
					dy, 0.85f) : dy;
		}

		void tick(float dt, float visc, float diff) {
			vel_step(u, v, u_prev, v_prev, visc, dt);
			//      dens_step(dense, dense_prev, u, v, diff, dt);
		}

		final int INDEX(int i, int j) {
			return i + (N + 2) * j;
		}

		float[] tmp = new float[SIZE];

		final void SWAP(float[] x0, float[] x) { // not longer used anyway
			System.arraycopy(x0, 0, tmp, 0, SIZE);
			System.arraycopy(x, 0, x0, 0, SIZE);
			System.arraycopy(tmp, 0, x, 0, SIZE);
		}

		void add_source(float[] x, float[] s, float dt) {
			int i, size = (N + 2) * (N + 2);
			for (i = 0; i < size; i++)
				x[i] += dt * s[i];
		}

		void diffuse(int b, float[] x, float[] x0, float diff, float dt) {
			int i, j, k;
			float a = dt * diff * N * N;
			for (k = 0; k < 20; k++) {
				for (i = 1; i <= N; i++) {
					for (j = 1; j <= N; j++) {
						x[INDEX(i, j)] = (x0[INDEX(i, j)] + a
								* (x[INDEX(i - 1, j)] + x[INDEX(i + 1, j)]
										+ x[INDEX(i, j - 1)] + x[INDEX(i, j + 1)]))
										/ (1 + 4 * a);
					}
				}
				set_bnd(b, x);
			}
		}

		void advect(int b, float[] d, float[] d0, float[] u, float[] v,
				float dt) {
			int i, j, i0, j0, i1, j1;
			float x, y, s0, t0, s1, t1, dt0;
			dt0 = dt * N;
			for (i = 1; i <= N; i++) {
				for (j = 1; j <= N; j++) {
					x = i - dt0 * u[INDEX(i, j)];
					y = j - dt0 * v[INDEX(i, j)];
					if (x < 0.5f)
						x = 0.5f;
					if (x > N + 0.5f)
						x = N + 0.5f;
					i0 = (int) x;
					i1 = i0 + 1;
					if (y < 0.5f)
						y = 0.5f;
					if (y > N + 0.5f)
						y = N + 0.5f;
					j0 = (int) y;
					j1 = j0 + 1;
					s1 = x - i0;
					s0 = 1 - s1;
					t1 = y - j0;
					t0 = 1 - t1;
					d[INDEX(i, j)] = s0
							* (t0 * d0[INDEX(i0, j0)] + t1 * d0[INDEX(i0, j1)])
							+ s1
							* (t0 * d0[INDEX(i1, j0)] + t1 * d0[INDEX(i1, j1)]);
				}
			}
			set_bnd(b, d);
		}

		void set_bnd(int b, float[] x) {
			int i;
			for (i = 1; i <= N; i++) {
				x[INDEX(0, i)] = (b == 1) ? -x[INDEX(1, i)] : x[INDEX(1, i)];
				x[INDEX(N + 1, i)] = b == 1 ? -x[INDEX(N, i)] : x[INDEX(N, i)];
				x[INDEX(i, 0)] = b == 2 ? -x[INDEX(i, 1)] : x[INDEX(i, 1)];
				x[INDEX(i, N + 1)] = b == 2 ? -x[INDEX(i, N)] : x[INDEX(i, N)];
			}
			x[INDEX(0, 0)] = 0.5f * (x[INDEX(1, 0)] + x[INDEX(0, 1)]);
			x[INDEX(0, N + 1)] = 0.5f * (x[INDEX(1, N + 1)] + x[INDEX(0, N)]);
			x[INDEX(N + 1, 0)] = 0.5f * (x[INDEX(N, 0)] + x[INDEX(N + 1, 1)]);
			x[INDEX(N + 1, N + 1)] = 0.5f * (x[INDEX(N, N + 1)] + x[INDEX(N + 1, N)]);
		}

		void dens_step(float[] x, float[] x0, float[] u, float[] v,
				float diff, float dt) {
			add_source(x, x0, dt);
			SWAP(x0, x);
			diffuse(0, x, x0, diff, dt);
			SWAP(x0, x);
			advect(0, x, x0, u, v, dt);
		}

		void vel_step(float[] u, float[] v, float[] u0, float[] v0,
				float visc, float dt) {
			//      add_source(u, u0, dt);
			//      add_source(v, v0, dt);
			//      SWAP(u0, u);
			//      diffuse(1, u, u0, visc, dt);
			//      SWAP(v0, v);
			//      diffuse(2, v, v0, visc, dt);
			//      project(u, v, u0, v0);
			//      SWAP(u0, u);
			//      SWAP(v0, v);
			//      advect(1, u, u0, u0, v0, dt);
			//      advect(2, v, v0, u0, v0, dt);
			//      project(u, v, u0, v0);

			diffuse(1, u, u, visc, dt);
			diffuse(2, v, v, visc, dt);
			project(u, v, u0, v0);
		}

		void project(float[] u, float[] v, float[] p, float[] div) {
			int i, j, k;
			float h;
			h = 1.0f / N;
			for (i = 1; i <= N; i++) {
				for (j = 1; j <= N; j++) {
					div[INDEX(i, j)] = -0.5f
							* h
							* (u[INDEX(i + 1, j)] - u[INDEX(i - 1, j)]
									+ v[INDEX(i, j + 1)] - v[INDEX(i, j - 1)]);
					p[INDEX(i, j)] = 0;
				}
			}
			set_bnd(0, div);
			set_bnd(0, p);
			for (k = 0; k < 20; k++) {
				for (i = 1; i <= N; i++) {
					for (j = 1; j <= N; j++) {
						p[INDEX(i, j)] = (div[INDEX(i, j)] + p[INDEX(i - 1, j)]
								+ p[INDEX(i + 1, j)] + p[INDEX(i, j - 1)] + p[INDEX(
										i, j + 1)]) / 4;
					}
				}
				set_bnd(0, p);
			}
			for (i = 1; i <= N; i++) {
				for (j = 1; j <= N; j++) {
					u[INDEX(i, j)] -= 0.5
							* (p[INDEX(i + 1, j)] - p[INDEX(i - 1, j)]) / h;
					v[INDEX(i, j)] -= 0.5
							* (p[INDEX(i, j + 1)] - p[INDEX(i, j - 1)]) / h;
				}
			}
			set_bnd(1, u);
			set_bnd(2, v);
		}
	}
	private void updateParticles() {
		for (int i = 0; i < numParticles; i++) {
			Particle particle = particles[i];

			// bounce off bottom
			if (particle.posY > height - particle.radius) {
				particle.vY = -abs(particle.vY) * (1 - BOUNCE_DAMPENING);
				particle.posY = height - particle.radius;
			}

			// bounce off ceiling
			if (particle.posY < particle.radius) {
				particle.vY = abs(particle.vY) * (1 - BOUNCE_DAMPENING);
				particle.posY = particle.radius;
			}

			// bounce off left border
			if (particle.posX < particle.radius) {
				particle.vX = abs(particle.vX) * (1 - BOUNCE_DAMPENING);
				particle.posX = particle.radius;
			}

			// bounce off right border
			if (particle.posX > width - particle.radius) {
				particle.vX = -abs(particle.vX) * (1 - BOUNCE_DAMPENING);
				particle.posX = width - particle.radius;
			}

			// apply interactive gravity
			applyMouseGravity(particle);

			// inter particle
			for (int j = i+1; j < numParticles; j++) {

				// bounce
				particles[i].applyElectroStaticForce(particles[j]);
				particles[i].bounce(particles[j]);
			}

			// apply Gravity
			if (grav) {
				particle.vY += GRAVITY * 0.1 * v;
			}


			// move it
			particle.tick();
		}

	}

	private void applyMouseGravity(Particle particle) {
		if (mousePressed) {
			float d = sqrt(pow(particle.posX - mouseX, 2)
					+ pow(particle.posY - mouseY, 2))
					* (float) 2.0;
			float ang = atan2(particle.posX - mouseX, particle.posY - mouseY);
			float F = (float) 24 * v;
			if (mouseButton == RIGHT) {
				F = -F;
			}

			particle.vX += sin(ang) * F;
			particle.vY += cos(ang) * F;
		}
	}

	class Particle {

		float posX;
		float posY;

		float vX = 0;
		float vY = 0;

		float radius;

		float charge = 0;

		public Particle(float x, float y, float r) {
			posX = x;
			posY = y;
			radius = r;
		}

		public float getVelocity() {
			return sqrt(vX * vX + vY * vY);
		}

		public float getMotionDirection() {
			return atan2(vX, vY);
		}

		public void tick() {
			posX += vX * v;
			posY += vY * v;
		}

		public void applyElectroStaticForce(Particle theOtherParticle){
			float d = sqrt(pow((float) (theOtherParticle.posX - posX), 2)
					+ pow((float) (theOtherParticle.posY - posY), 2))*((float)0.8);

			float v = (float) 127/(d*d);
			v*= (charge-0.5)*(theOtherParticle.charge-0.5);

			float dx = (theOtherParticle.posX - posX);
			float dy = (theOtherParticle.posY - posY);

			dx *= v;
			dy *= v;

			vX -= dx;
			vY -= dy;
			theOtherParticle.vX += dx;
			theOtherParticle.vY += dy;
		}

		public void bounce(Particle theOtherParticle) {
			if (hit(theOtherParticle)) {
				charge = (float) ((charge + theOtherParticle.charge)*0.5);
				theOtherParticle.charge = charge;
				float commonTangentAngle = atan2(
						(float) (posX - theOtherParticle.posX),
						(float) (posY - theOtherParticle.posY))
						+ asin(1);

				float v1 = theOtherParticle.getVelocity();
				float v2 = getVelocity();
				float w1 = theOtherParticle.getMotionDirection();
				float w2 = getMotionDirection();

				theOtherParticle.vX = sin(commonTangentAngle) * v1
						* cos(w1 - commonTangentAngle)
						+ cos(commonTangentAngle) * v2
						* sin(w2 - commonTangentAngle);
				theOtherParticle.vY = cos(commonTangentAngle) * v1
						* cos(w1 - commonTangentAngle)
						- sin(commonTangentAngle) * v2
						* sin(w2 - commonTangentAngle);
				vX = sin(commonTangentAngle) * v2
						* cos(w2 - commonTangentAngle)
						+ cos(commonTangentAngle) * v1
						* sin(w1 - commonTangentAngle);
				vY = cos(commonTangentAngle) * v2
						* cos(w2 - commonTangentAngle)
						- sin(commonTangentAngle) * v1
						* sin(w1 - commonTangentAngle);

				theOtherParticle.vX *= (1 - BOUNCE_DAMPENING);
				theOtherParticle.vY *= (1 - BOUNCE_DAMPENING);
				vX *= (1 - BOUNCE_DAMPENING);
				vY *= (1 - BOUNCE_DAMPENING);

			}
		}

		private boolean hit(Particle theOtherParticle) {
			return (sqrt(pow((float) (theOtherParticle.posX - posX), 2)
					+ pow((float) (theOtherParticle.posY - posY), 2)) < (theOtherParticle.radius + radius))
					&& (sqrt(pow((float) (theOtherParticle.posX - posX), 2)
							+ pow((float) (theOtherParticle.posY - posY), 2)) > sqrt(pow(
									(float) (theOtherParticle.posX
											+ theOtherParticle.vX*v - posX - vX*v), 2)
											+ pow((float) (theOtherParticle.posY
													+ theOtherParticle.vY*v - posY - vY*v), 2)));
		}

	}

}
