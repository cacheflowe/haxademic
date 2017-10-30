package com.haxademic.core.draw.shapes;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import com.haxademic.core.app.P;

public class MeshShapes {
	// M_3_3_02.pde
	// Mesh.pde
	// 
	// Generative Gestaltung, ISBN: 978-3-87439-759-9
	// First Edition, Hermann Schmidt, Mainz, 2009
	// Hartmut Bohnacker, Benedikt Gross, Julia Laub, Claudius Lazzeroni
	// Copyright 2009 Hartmut Bohnacker, Benedikt Gross, Julia Laub, Claudius Lazzeroni
	//
	// http://www.generative-gestaltung.de
	//
	// Licensed under the Apache License, Version 2.0 (the "License");
	// you may not use this file except in compliance with the License.
	// You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
	// Unless required by applicable law or agreed to in writing, software
	// distributed under the License is distributed on an "AS IS" BASIS,
	// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	// See the License for the specific language governing permissions and
	// limitations under the License.


	// ------ constants ------

	final public static int PLANE              = P.CUSTOM;
	final public static int TUBE               = 1;
	final public static int SPHERE             = 2;
	final public static int TORUS              = 3;
	final public static int PARABOLOID         = 4;
	final public static int STEINBACHSCREW     = 5;
	final public static int SINE               = 6;
	final public static int FIGURE8TORUS       = 7;
	final public static int ELLIPTICTORUS      = 8;
	final public static int CORKSCREW          = 9;
	final public static int BOHEMIANDOME       = 10;
	final public static int BOW                = 11;
	final public static int MAEDERSOWL         = 12;
	final public static int ASTROIDALELLIPSOID = 13;
	final public static int TRIAXIALTRITORUS   = 14;
	final public static int LIMPETTORUS        = 15;
	final public static int HORN               = 16;
	final public static int SHELL              = 17;
	final public static int KIDNEY             = 18;
	final public static int LEMNISCAPE         = 19;
	final public static int TRIANGULOID        = 20;
	final public static int SUPERFORMULA       = 21;


	// ------ mesh parameters ------

	int form = PARABOLOID;

	float uMin = -P.PI;
	float uMax = P.PI;
	int uCount = 50;

	float vMin = -P.PI;
	float vMax = P.PI;
	int vCount = 50;

	float[] params = {
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1        };

	int drawMode = P.TRIANGLE_STRIP;
	float minHue = 0;
	float maxHue = 0;
	float minSaturation = 0;
	float maxSaturation = 0;
	float minBrightness = 50;
	float maxBrightness = 50;
	float meshAlpha = 100;

	float meshDistortion = 0;

	PVector[][] points;
	
	PApplet p;


	// ------ construktors ------

	public MeshShapes() {
		form = P.CUSTOM;
		update();
	}

	public MeshShapes(int theForm) {
		if (theForm >=0) {
			form = theForm;
		}
		update();
	}

	public MeshShapes(int theForm, int theUNum, int theVNum) {
		if (theForm >=0) {
			form = theForm;
		}
		uCount = P.max(theUNum, 1);
		vCount = P.max(theVNum, 1);
		update();
	}

	public MeshShapes(int theForm, float theUMin, float theUMax, float theVMin, float theVMax) {
		if (theForm >=0) {
			form = theForm;
		}
		uMin = theUMin;    
		uMax = theUMax;    
		vMin = theVMin;    
		vMax = theVMax;    
		update();
	}

	public MeshShapes(int theForm, int theUNum, int theVNum, float theUMin, float theUMax, float theVMin, float theVMax) {
		if (theForm >=0) {
			form = theForm;
		}
		uCount = P.max(theUNum, 1);
		vCount = P.max(theVNum, 1);
		uMin = theUMin;    
		uMax = theUMax;    
		vMin = theVMin;    
		vMax = theVMax;    
		update();
	}



	// ------ calculate points ------

	public void update() {
		if(p == null) p = P.p;
		points = new PVector[vCount+1][uCount+1];

		float u, v;
		for (int iv = 0; iv <= vCount; iv++) {
			for (int iu = 0; iu <= uCount; iu++) {
				u = P.map(iu, 0, uCount, uMin, uMax);
				v = P.map(iv, 0, vCount, vMin, vMax);

				switch(form) {
				case P.CUSTOM: 
					points[iv][iu] = calculatePoints(u, v);
					break;
				case TUBE: 
					points[iv][iu] = Tube(u, v);
					break;
				case SPHERE: 
					points[iv][iu] = Sphere(u, v);
					break;
				case TORUS: 
					points[iv][iu] = Torus(u, v);
					break;
				case PARABOLOID: 
					points[iv][iu] = Paraboloid(u, v);
					break;
				case STEINBACHSCREW: 
					points[iv][iu] = SteinbachScrew(u, v);
					break;
				case SINE: 
					points[iv][iu] = Sine(u, v);
					break;
				case FIGURE8TORUS: 
					points[iv][iu] = Figure8Torus(u, v);
					break;
				case ELLIPTICTORUS: 
					points[iv][iu] = EllipticTorus(u, v);
					break;
				case CORKSCREW: 
					points[iv][iu] = Corkscrew(u, v);
					break;
				case BOHEMIANDOME: 
					points[iv][iu] = BohemianDome(u, v);
					break;
				case BOW: 
					points[iv][iu] = Bow(u, v);
					break;
				case MAEDERSOWL: 
					points[iv][iu] = MaedersOwl(u, v);
					break;
				case ASTROIDALELLIPSOID: 
					points[iv][iu] = AstroidalEllipsoid(u, v);
					break;
				case TRIAXIALTRITORUS: 
					points[iv][iu] = TriaxialTritorus(u, v);
					break;
				case LIMPETTORUS: 
					points[iv][iu] = LimpetTorus(u, v);
					break;
				case HORN: 
					points[iv][iu] = Horn(u, v);
					break;
				case SHELL: 
					points[iv][iu] = Shell(u, v);
					break;
				case KIDNEY: 
					points[iv][iu] = Kidney(u, v);
					break;
				case LEMNISCAPE: 
					points[iv][iu] = Lemniscape(u, v);
					break;
				case TRIANGULOID: 
					points[iv][iu] = Trianguloid(u, v);
					break;
				case SUPERFORMULA: 
					points[iv][iu] = Superformula(u, v);
					break;

				default:
					points[iv][iu] = calculatePoints(u, v);
					break;          
				}
			}
		}
	}


	// ------ getters and setters ------

	public int getForm() {
		return form;
	}
	public void setForm(int theValue) {
		form = theValue;
	}

	public String getFormName() {
		switch(form) {
		case P.CUSTOM: 
			return "Custom";
		case TUBE: 
			return "Tube";
		case SPHERE: 
			return "Sphere";
		case TORUS: 
			return "Torus";
		case PARABOLOID: 
			return "Paraboloid";
		case STEINBACHSCREW: 
			return "Steinbach Screw";
		case SINE: 
			return "Sine";
		case FIGURE8TORUS: 
			return "Figure 8 Torus";
		case ELLIPTICTORUS: 
			return "Elliptic Torus";
		case CORKSCREW: 
			return "Corkscrew";
		case BOHEMIANDOME: 
			return "Bohemian Dome";
		case BOW: 
			return "Bow";
		case MAEDERSOWL: 
			return "Maeders Owl";
		case ASTROIDALELLIPSOID: 
			return "Astoidal Ellipsoid";
		case TRIAXIALTRITORUS: 
			return "Triaxial Tritorus";
		case LIMPETTORUS: 
			return "Limpet Torus";
		case HORN: 
			return "Horn";
		case SHELL: 
			return "Shell";
		case KIDNEY: 
			return "Kidney";
		case LEMNISCAPE: 
			return "Lemniscape";
		case TRIANGULOID: 
			return "Trianguloid";
		case SUPERFORMULA: 
			return "Superformula";
		}
		return "";
	}

	public float getUMin() {
		return uMin;
	}
	public void setUMin(float theValue) {
		uMin = theValue;
	}

	public float getUMax() {
		return uMax;
	}
	public void setUMax(float theValue) {
		uMax = theValue;
	}

	public int getUCount() {
		return uCount;
	}
	public void setUCount(int theValue) {
		uCount = theValue;
	}

	public float getVMin() {
		return vMin;
	}
	public void setVMin(float theValue) {
		vMin = theValue;
	}

	public float getVMax() {
		return vMax;
	}
	public void setVMax(float theValue) {
		vMax = theValue;
	}

	public int getVCount() {
		return vCount;
	}
	public void setVCount(int theValue) {
		vCount = theValue;
	}

	public float[] getParams() {
		return params;
	}
	public void setParams(float[] theValues) {
		params = theValues;
	}

	public float getParam(int theIndex) {
		return params[theIndex];
	}
	public void setParam(int theIndex, float theValue) {
		params[theIndex] = theValue;
	}

	public int getDrawMode() {
		return drawMode;
	}
	public void setDrawMode(int theMode) {
		drawMode = theMode;
	}

	public float getMeshDistortion() {
		return meshDistortion;
	}
	public void setMeshDistortion(float theValue) {
		meshDistortion = theValue;
	}

	public void setColorRange(float theMinHue, float theMaxHue, float theMinSaturation, float theMaxSaturation, float theMinBrightness, float theMaxBrightness, float theMeshAlpha) {
		minHue = theMinHue;
		maxHue = theMaxHue;
		minSaturation = theMinSaturation;
		maxSaturation = theMaxSaturation;
		minBrightness = theMinBrightness;
		maxBrightness = theMaxBrightness;
		meshAlpha = theMeshAlpha;
	}

	public float getMinHue() {
		return minHue;
	}
	public void setMinHue(float minHue) {
		this.minHue = minHue;
	}

	public float getMaxHue() {
		return maxHue;
	}
	public void setMaxHue(float maxHue) {
		this.maxHue = maxHue;
	}

	public float getMinSaturation() {
		return minSaturation;
	}
	public void setMinSaturation(float minSaturation) {
		this.minSaturation = minSaturation;
	}

	public float getMaxSaturation() {
		return maxSaturation;
	}
	public void setMaxSaturation(float maxSaturation) {
		this.maxSaturation = maxSaturation;
	}

	public float getMinBrightness() {
		return minBrightness;
	}
	public void setMinBrightness(float minBrightness) {
		this.minBrightness = minBrightness;
	}

	public float getMaxBrightness() {
		return maxBrightness;
	}
	public void setMaxBrightness(float maxBrightness) {
		this.maxBrightness = maxBrightness;
	}

	public float getMeshAlpha() {
		return meshAlpha;
	}
	public void setMeshAlpha(float meshAlpha) {
		this.meshAlpha = meshAlpha;
	}


	// ------ functions for calculating the mesh points ------

	public PVector calculatePoints(float u, float v) {
		float x = u;
		float y = v;
		float z = 0;

		return new PVector(x, y, z);
	}

	public PVector defaultForm(float u, float v) {
		float x = u;
		float y = v;
		float z = 0;

		return new PVector(x, y, z);
	}

	public PVector Tube(float u, float v) {
		float x = (P.sin(u));
		float y = params[0] * v;
		float z = (P.cos(u));

		return new PVector(x, y, z);
	}

	public PVector Sphere(float u, float v) {
		v /= 2;
		v += P.HALF_PI;
		float x = 2 * (P.sin(v) * P.sin(u));
		float y = 2 * (params[0] * P.cos(v));
		float z = 2 * (P.sin(v) * P.cos(u));

		return new PVector(x, y, z);
	}

	public PVector Torus(float u, float v) {
		float x = 1 * ((params[1] + 1 + params[0] * P.cos(v)) * P.sin(u));
		float y = 1 * (params[0] * P.sin(v));
		float z = 1 * ((params[1] + 1 + params[0] * P.cos(v)) * P.cos(u));

		return new PVector(x, y, z);
	}

	public PVector Paraboloid(float u, float v) {
		float pd = params[0]; 
		if (pd == 0) {
			pd = 0.0001f; 
		}
		float x = power((v/pd),0.5f) * P.sin(u);
		float y = v;
		float z = power((v/pd),0.5f) * P.cos(u);

		return new PVector(x, y, z);
	}


	public PVector SteinbachScrew(float u, float v) {
		float x = u * P.cos(v);
		float y = u * P.sin(params[0] * v);
		float z = v * P.cos(u);

		return new PVector(x, y, z);
	}

	public PVector Sine(float u, float v) {
		float x = 2 * P.sin(u);
		float y = 2 * P.sin(params[0] * v);
		float z = 2 * P.sin(u+v);

		return new PVector(x, y, z);
	}


	public PVector Figure8Torus(float u, float v) {
		float x = 1.5f * P.cos(u) * (params[0] + P.sin(v) * P.cos(u) - P.sin(2*v) * P.sin(u) / 2f);
		float y = 1.5f * P.sin(u) * (params[0] + P.sin(v) * P.cos(u) - P.sin(2*v) * P.sin(u) / 2f) ;
		float z = 1.5f * P.sin(u) * P.sin(v) + P.cos(u) * P.sin(2*v) / 2;

		return new PVector(x, y, z);
	}

	public PVector EllipticTorus(float u, float v) {
		float x = 1.5f * (params[0] + P.cos(v)) * P.cos(u);
		float y = 1.5f * (params[0] + P.cos(v)) * P.sin(u) ;
		float z = 1.5f * P.sin(v) + P.cos(v);

		return new PVector(x, y, z);
	}

	public PVector Corkscrew(float u, float v) {
		float x = P.cos(u) * P.cos(v);
		float y = P.sin(u) * P.cos(v);
		float z = P.sin(v) + params[0] * u;

		return new PVector(x, y, z);
	}

	public PVector BohemianDome(float u, float v) {
		float x = 2 * P.cos(u);
		float y = 2 * P.sin(u) + params[0] * P.cos(v);
		float z = 2 * P.sin(v);

		return new PVector(x, y, z);
	}

	public PVector Bow(float u, float v) {
		u /= P.TWO_PI;
		v /= P.TWO_PI;
		float x = (2 + params[0] * P.sin(P.TWO_PI * u)) * P.sin(2 * P.TWO_PI * v);
		float y = (2 + params[0] * P.sin(P.TWO_PI * u)) * P.cos(2 * P.TWO_PI * v);
		float z = params[0] * P.cos(P.TWO_PI * u) + 3 * P.cos(P.TWO_PI * v);

		return new PVector(x, y, z);
	}

	public PVector MaedersOwl(float u, float v) {
		float x = 0.4f * (v * P.cos(u) - 0.5f*params[0] * power(v,2) * P.cos(2 * u));
		float y = 0.4f * (-v * P.sin(u) - 0.5f*params[0] * power(v,2) * P.sin(2 * u));
		float z = 0.4f * (4 * power(v,1.5f) * P.cos(3 * u / 2) / 3);

		return new PVector(x, y, z);
	}

	public PVector AstroidalEllipsoid(float u, float v) {
		u /= 2;
		float x = 3 * power(P.cos(u)*P.cos(v),3*params[0]);
		float y = 3 * power(P.sin(u)*P.cos(v),3*params[0]);
		float z = 3 * power(P.sin(v),3*params[0]);

		return new PVector(x, y, z);
	}

	public PVector TriaxialTritorus(float u, float v) {
		float x = 1.5f * P.sin(u) * (1 + P.cos(v));
		float y = 1.5f * P.sin(u + P.TWO_PI / 3 * params[0]) * (1 + P.cos(v + P.TWO_PI / 3 * params[0]));
		float z = 1.5f * P.sin(u + 2*P.TWO_PI / 3 * params[0]) * (1 + P.cos(v + 2*P.TWO_PI / 3 * params[0]));

		return new PVector(x, y, z);
	}

	public PVector LimpetTorus(float u, float v) {
		float x = 1.5f * params[0] * P.cos(u) / (P.sqrt(2) + P.sin(v));
		float y = 1.5f * params[0] * P.sin(u) / (P.sqrt(2) + P.sin(v));
		float z = 1.5f * 1 / (P.sqrt(2) + P.cos(v));

		return new PVector(x, y, z);
	}

	public PVector Horn(float u, float v) {
		u /= P.PI;
		//v /= PI;
		float x = (2*params[0] + u * P.cos(v)) * P.sin(P.TWO_PI * u);
		float y = (2*params[0] + u * P.cos(v)) * P.cos(P.TWO_PI * u) + 2 * u;
		float z = u * P.sin(v);

		return new PVector(x, y, z);
	}

	public PVector Shell(float u, float v) {
		float x = params[1] * (1 - (u / P.TWO_PI)) * P.cos(params[0]*u) * (1 + P.cos(v)) + params[3] * P.cos(params[0]*u);
		float y = params[1] * (1 - (u / P.TWO_PI)) * P.sin(params[0]*u) * (1 + P.cos(v)) + params[3] * P.sin(params[0]*u);
		float z = params[2] * (u / P.TWO_PI) + params[0] * (1 - (u / P.TWO_PI)) * P.sin(v);

		return new PVector(x, y, z);
	}

	public PVector Kidney(float u, float v) {
		u /= 2;
		float x = P.cos(u) * (params[0]*3*P.cos(v) - P.cos(3*v));
		float y = P.sin(u) * (params[0]*3*P.cos(v) - P.cos(3*v));
		float z = 3 * P.sin(v) - P.sin(3*v);

		return new PVector(x, y, z);
	}

	public PVector Lemniscape(float u, float v) {
		u /= 2;
		float cosvSqrtAbsSin2u = P.cos(v)*P.sqrt(P.abs(P.sin(2*params[0]*u)));
		float x = cosvSqrtAbsSin2u*P.cos(u);
		float y = cosvSqrtAbsSin2u*P.sin(u);
		float z = 3 * (power(x,2) - power(y,2) + 2 * x * y * power(P.tan(v),2));
		x *= 3;
		y *= 3;
		return new PVector(x, y, z);
	}

	public PVector Trianguloid(float u, float v) {
		float x = 0.75f * (P.sin(3*u) * 2 / (2 + P.cos(v)));
		float y = 0.75f * ((P.sin(u) + 2 * params[0] * P.sin(2*u)) * 2 / (2 + P.cos(v + P.TWO_PI)));
		float z = 0.75f * ((P.cos(u) - 2 * params[0] * P.cos(2*u)) * (2 + P.cos(v)) * ((2 + P.cos(v + P.TWO_PI/3))*0.25f));

		return new PVector(x, y, z);
	}

	public PVector Superformula(float u, float v) {
		v /= 2;

		// Superformel 1
		float a = params[0];
		float b = params[1];
		float m = (params[2]);
		float n1 = (params[3]);
		float n2 = (params[4]);
		float n3 = (params[5]);
		float r1 = P.pow(P.pow(P.abs(P.cos(m*u/4)/a), n2) + P.pow(P.abs(P.sin(m*u/4)/b), n3), -1/n1);

		// Superformel 2
		a = params[6];
		b = params[7];
		m = (params[8]);
		n1 = (params[9]);
		n2 = (params[10]);
		n3 = (params[11]);
		float r2 = P.pow(P.pow(P.abs(P.cos(m*v/4)/a), n2) + P.pow(P.abs(P.sin(m*v/4)/b), n3), -1/n1);

		float x = 2 * (r1*P.sin(u) * r2*P.cos(v));
		float y = 2 * (r2*P.sin(v));
		float z = 2 * (r1*P.cos(u) * r2*P.cos(v));

		return new PVector(x, y, z);
	}




	// ------ definition of some mathematical functions ------

	// the processing-function pow works a bit differently for negative bases
	float power(float b, float e) {
		if (b >= 0 || (int)e == e) {
			return P.pow(b, e);
		} 
		else {
			return -P.pow(-b, e);
		}
	}

	float logE(float v) {
		if (v >= 0) {
			return P.log(v);
		} 
		else{
			return -P.log(-v);
		}
	}

	float sinh(float a) {
		return (P.sin(P.HALF_PI/2f-a));
	}

	float cosh(float a) {
		return (P.cos(P.HALF_PI/2-a));
	}

	float tanh(float a) {
		return (P.tan(P.HALF_PI/2-a));
	}



	// ------ draw mesh ------

	public void draw(PGraphics pg) {
		int iuMax, ivMax;

		if (drawMode == P.QUADS || drawMode == P.TRIANGLES) {
			iuMax = uCount-1;
			ivMax = vCount-1;
		}
		else{
			iuMax = uCount;
			ivMax = vCount-1;
		}

		// store previously set colorMode
		pg.pushStyle();
		pg.colorMode(P.HSB, 360, 100, 100, 100);

		float minH = minHue;
		float maxH = maxHue;
		if (P.abs(maxH-minH) < 20) maxH = minH;
		float minS = minSaturation;
		float maxS = maxSaturation;
		if (P.abs(maxS-minS) < 10) maxS = minS;
		float minB = minBrightness;
		float maxB = maxBrightness;
		if (P.abs(maxB-minB) < 10) maxB = minB;


		for (int iv = 0; iv <= ivMax; iv++) {
			if (drawMode == P.TRIANGLES) {

				for (int iu = 0; iu <= iuMax; iu++) {
					pg.fill(p.random(minH, maxH), p.random(minS, maxS), p.random(minB, maxB), meshAlpha);
					pg.beginShape(drawMode);
					float r1 = meshDistortion * p.random(-1, 1);
					float r2 = meshDistortion * p.random(-1, 1);
					float r3 = meshDistortion * p.random(-1, 1);
					pg.vertex(points[iv][iu].x+r1, points[iv][iu].y+r2, points[iv][iu].z+r3);
					pg.vertex(points[iv+1][iu+1].x+r1, points[iv+1][iu+1].y+r2, points[iv+1][iu+1].z+r3);
					pg.vertex(points[iv+1][iu].x+r1, points[iv+1][iu].y+r2, points[iv+1][iu].z+r3);
					pg.endShape();

					pg.fill(p.random(minH, maxH), p.random(minS, maxS), p.random(minB, maxB), meshAlpha);
					pg.beginShape(drawMode);
					r1 = meshDistortion * p.random(-1, 1);
					r2 = meshDistortion * p.random(-1, 1);
					r3 = meshDistortion * p.random(-1, 1);
					pg.vertex(points[iv+1][iu+1].x+r1, points[iv+1][iu+1].y+r2, points[iv+1][iu+1].z+r3);
					pg.vertex(points[iv][iu].x+r1, points[iv][iu].y+r2, points[iv][iu].z+r3);
					pg.vertex(points[iv][iu+1].x+r1, points[iv][iu+1].y+r2, points[iv][iu+1].z+r3);
					pg.endShape();
				}       

			}
			else if (drawMode == P.QUADS) {
				for (int iu = 0; iu <= iuMax; iu++) {
					pg.fill(p.random(minH, maxH), p.random(minS, maxS), p.random(minB, maxB), meshAlpha);
					pg.beginShape(drawMode);

					float r1 = meshDistortion * p.random(-1, 1);
					float r2 = meshDistortion * p.random(-1, 1);
					float r3 = meshDistortion * p.random(-1, 1);
					pg.vertex(points[iv][iu].x+r1, points[iv][iu].y+r2, points[iv][iu].z+r3);
					pg.vertex(points[iv+1][iu].x+r1, points[iv+1][iu].y+r2, points[iv+1][iu].z+r3);
					pg.vertex(points[iv+1][iu+1].x+r1, points[iv+1][iu+1].y+r2, points[iv+1][iu+1].z+r3);
					pg.vertex(points[iv][iu+1].x+r1, points[iv][iu+1].y+r2, points[iv][iu+1].z+r3);

					pg.endShape();
				}        
			}
			else{
				// Draw Strips
				pg.fill(p.random(minH, maxH), p.random(minS, maxS), p.random(minB, maxB), meshAlpha);
				pg.beginShape(drawMode);

				for (int iu = 0; iu <= iuMax; iu++) {
					float r1 = meshDistortion * p.random(-1, 1);
					float r2 = meshDistortion * p.random(-1, 1);
					float r3 = meshDistortion * p.random(-1, 1);
					pg.vertex(points[iv][iu].x+r1, points[iv][iu].y+r2, points[iv][iu].z+r3);
					pg.vertex(points[iv+1][iu].x+r1, points[iv+1][iu].y+r2, points[iv+1][iu].z+r3);
				}  

				pg.endShape();
			}
		}

		pg.popStyle();
	}

}
