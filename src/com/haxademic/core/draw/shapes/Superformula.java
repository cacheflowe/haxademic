package com.haxademic.core.draw.shapes;

import java.util.Locale;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class Superformula {
	// From Thomas Diewald : http://www.openprocessing.org/sketch/84465
	// math constants
	private static final double PI      = Math.PI;
	private static final double TWO_PI  = PI*2.0;
	private static final double HALF_PI = PI*0.5;
	private static final int WHITE = 255;

	// Params
	private double a, b, m , n1, n2, n3;
	private int lon_res, lat_res;

	// Precomputed values
	private float[] sf_lon_cos, sf_lon_sin; // longitude
	private float[] sf_lat_cos, sf_lat_sin; // lattitude


	public Superformula(int res_x, int res_y, double a, double b, double m, double n1, double n2, double n3) {
		this
		.a(a)
		.b(b)
		.m(m)
		.n1(n1)
		.n2(n2)
		.n3(n3)
		.setResolution(res_x, res_y)
		.update();
	}


	//////////////////////////////////////////////////////////////////////////////
	// PARAMETERS
	//////////////////////////////////////////////////////////////////////////////

	// SET SHAPE PARAMS
	public Superformula a (double a ){ this.a  = a ; return this; }
	public Superformula b (double b ){ this.b  = b ; return this; }
	public Superformula m (double m ){ this.m  = m ; return this; }
	public Superformula n1(double n1){ this.n1 = n1; return this; }
	public Superformula n2(double n2){ this.n2 = n2; return this; }
	public Superformula n3(double n3){ this.n3 = n3; return this; }

	public Superformula setResolution(int lon_res, int lat_res){

		this.lon_res = lon_res;
		this.lat_res = lat_res;

		sf_lon_cos = new float[this.lon_res]; 
		sf_lon_sin = new float[this.lon_res]; 

		sf_lat_cos = new float[this.lat_res];
		sf_lat_sin = new float[this.lat_res];

		return this;
	}

	// GET SHAPE PARAMS
	public double a (){ return a ; }
	public double b (){ return b ; }
	public double m (){ return m ; }
	public double n1(){ return n1; }
	public double n2(){ return n2; }
	public double n3(){ return n3; }
	public int resx(){ return lon_res; }
	public int resy(){ return lat_res; }


	public void printParams(){
		System.out.printf(Locale.ENGLISH, "--------- Superformula: Shape Params ----------\n" );
		System.out.printf(Locale.ENGLISH, "res = %d/%d\n", lon_res, lat_res );
		System.out.printf(Locale.ENGLISH, "a   = %+6.3f\n", a  );
		System.out.printf(Locale.ENGLISH, "b   = %+6.3f\n", b  );
		System.out.printf(Locale.ENGLISH, "m   = %+6.3f\n", m  );
		System.out.printf(Locale.ENGLISH, "n1  = %+6.3f\n", n1 );
		System.out.printf(Locale.ENGLISH, "n2  = %+6.3f\n", n2 );
		System.out.printf(Locale.ENGLISH, "n3  = %+6.3f\n", n3 );
	}


	//////////////////////////////////////////////////////////////////////////////
	// SUPERFORMULA
	//////////////////////////////////////////////////////////////////////////////

	private double SUPERFORMULA(final double f){
		//http://en.wikipedia.org/wiki/Superformula
		final double s = m*f*0.25;
		return Math.pow((  Math.pow(Math.abs(Math.cos(s)*1/a), n2) +
				Math.pow(Math.abs(Math.sin(s)*1/b), n3) ), -1/n1 );
	}

	public void update(){
		// longitude
		{
			double lon, sf_lon, lon_step = TWO_PI / (lon_res);
			for(int i = 0; i < lon_res; i++){
				lon = -PI + i*lon_step;
				sf_lon = SUPERFORMULA(lon);
				sf_lon_cos[i] = (float)(sf_lon * Math.cos(lon));
				sf_lon_sin[i] = (float)(sf_lon * Math.sin(lon));
			}
		}

		// lattitude
		{
			double lat, sf_lat, lat_step = PI / (lat_res-1);
			for(int i = 0; i < lat_res; i++){
				lat = -HALF_PI + i*lat_step;
				sf_lat = SUPERFORMULA(lat);
				sf_lat_cos[i] = (float)(sf_lat * Math.cos(lat)) * 100;
				sf_lat_sin[i] = (float)(sf_lat * Math.sin(lat)) * 100;
			}
		}
	}








	//////////////////////////////////////////////////////////////////////////////
	// DRAW
	//////////////////////////////////////////////////////////////////////////////

	// V represents a small path of the surface, that gets shifted during
	// drawing, so each vertex gets only updated once !!
	private Vertex[][] V = new Vertex[0][4];

	// create/init/fill starting mesh-patch
	private void initPatch(){
		if( V.length != lat_res){
			V = new Vertex[lat_res][4];
			for(int i = 0; i < 4; i++)
				for(int j = 0; j < lat_res; j++)
					V[j][i] = new Vertex(j);
		}

		for(int j = 0; j < lat_res; j++){
			V[j][0].updatePos(0);
			V[j][1].updatePos(1);
			V[j][2].updatePos(2);
			V[j][3].updatePos(3);
		}

		for(int j = 0; j < lat_res; j++){
			V[j][1].updateNormalAndAO(0, 1, 2);
			V[j][2].updateNormalAndAO(1, 2, 3);
		}
	}


	// temporary buffers
	private final PVector P1 = new PVector(),    //_      P2    
			P2 = new PVector(),    //_      |     
			P3 = new PVector(),    //_ P1---P---P3
			P4 = new PVector(),    //_      |     
			C  = new PVector();    //_      P4  Face

	private class Vertex{
		final int j; // j is fixed
		final PVector p = new PVector();
		final PVector n = new PVector();
		final PVector n_face = new PVector();;
		float ao = 1;

		Vertex(int j){
			this.j = j;
		}

		private final void updatePos(int i){
			i = (i+lon_res)%lon_res;
			p.x = sf_lat_cos[j] * sf_lon_cos[i];
			p.y = sf_lat_cos[j] * sf_lon_sin[i];
			p.z = sf_lat_sin[j];
		}

		private final void updateNormalAndAO(int l, int c, int r){
			PVector.sub(V[j][l].p, p, P1);
			PVector.sub(V[j][r].p, p, P3);

			// normal
			n.set(0, 0, 0); // reset
			if( j > 0         ) {  PVector.sub(V[j-1][c].p, p, P2); n.add(PVector.cross(P1, P2, C     ));  n.add(PVector.cross(P2, P3, C)); }
			if( j < lat_res-1 ) {  PVector.sub(V[j+1][c].p, p, P4); n.add(PVector.cross(P3, P4, n_face));  n.add(PVector.cross(P4, P1, C)); }
			n.normalize();

			// face normal ... already done during normal computation (3 lines above)
			//		      if( j < lat_res-1 ) PVector.cross(P3, P4, n_face);


			// ambient occlusion factor
			ao = 1;
			P1.normalize();  ao -= Math.max(0, P1.dot(n));
			P3.normalize();  ao -= Math.max(0, P3.dot(n));
			if( j > 0         ){ P2.normalize();  ao -= Math.max(0, P2.dot(n)); }
			if( j < lat_res-1 ){ P4.normalize();  ao -= Math.max(0, P4.dot(n)); }

			ao = Math.max(ao,0);
		}

		// returns true, if a face is facing the camera
		boolean frontface(){
			return PVector.sub(CAM, p, VIEW).dot(n_face) > 0;
		}
	}


	private final PVector CAM  = new PVector();
	private final PVector VIEW = new PVector();
	private PGraphics papplet;

	public void drawMesh(PGraphics papplet, boolean smooth, boolean ao, boolean faces, boolean edges, float[] cam_pos){
		this.papplet = papplet;

		this.CAM.x = cam_pos[0];
		this.CAM.y = cam_pos[1];
		this.CAM.z = cam_pos[2];

		initPatch();

		int id_, id0 = 0, id1 = 1, id2 = 2, id3 = 3;
		
		for(int i = 0; i < lon_res; i++){

			if( faces )  FACES_quadStrip( id1, id2, smooth, ao);
			if( edges )  EDGES_quads    ( id1, id2);

			// rotate id's, to shift mesh-patch!
			id_ = id0;  id0 = id1; id1 = id2; id2 = id3; id3 = id_;//(++id3)%4;

			// update next row ...
			for(int j = 0;j < lat_res; j++){ 
				V[j][id3].updatePos(i+4);                   // get new vertex position
				V[j][id2].updateNormalAndAO(id1, id2, id3); // get old new vertex normal and ao (and face normal for backface culling)
			}
		}

	}


	// draw faces using a quad-strip, ...is using backface-culling.
	private void FACES_quadStrip(int id1, int id2, boolean smooth, boolean ao){

		papplet.fill(WHITE); // in case ao=false
		papplet.noStroke();
		papplet.beginShape(PConstants.QUAD_STRIP);
		{
			boolean cut = false;
			Vertex A, B;
			for( int j = 0; j < lat_res; j++){ 
				// using degenerate quads: necessary when doing backface-culling!!!
				A = V[j][id1];
				B = V[j][id2];
				if( A.frontface() ){
					if( cut ){
						vertex(A, A);
						vertex(A, B, smooth, ao);
						cut = false;
					} else {
						vertex(A, B, smooth, ao);
					}
				} else if( !cut ){
					vertex(A, B, smooth, ao);
					vertex(B, B, smooth, ao);
					cut = true;
				}

				// vertex(A, B, smooth, ao); // simple version: no backface-culling

			}
		}
		papplet.endShape();
	}

	// draw edges using quads (quad-strip didnt work) ...is using backface-culling.
	private void EDGES_quads(int id1, int id2){
		papplet.noFill();
		papplet.stroke(75, 25, 25);
		papplet.strokeWeight(1.5f);

		papplet.beginShape(PConstants.QUADS);
		{
			for( int j = 0; j < lat_res-1; j++){ 
				//  V1----V4
				//   |    | 
				//  V2----V3
				if( V[j][id1].frontface() ){
					quad(V[j  ][id1], V[j+1][id1], V[j+1][id2], V[j  ][id2]);
				}
			}
		}
		papplet.endShape();

	}

	final private void vertex(Vertex v1, Vertex v2){
		papplet.vertex(v1.p.x, v1.p.y, v1.p.z);
		papplet.vertex(v2.p.x, v2.p.y, v2.p.z);
	}

	final private void vertex(Vertex v1, Vertex v2, boolean smooth, boolean ao){
		if(ao    )papplet.fill  (v1.ao*WHITE);
		if(smooth)papplet.normal(v1.n.x, v1.n.y, v1.n.z);
		papplet.vertex(v1.p.x, v1.p.y, v1.p.z);

		if(ao    )papplet.fill  (v2.ao*WHITE);
		if(smooth)papplet.normal(v2.n.x, v2.n.y, v2.n.z);
		papplet.vertex(v2.p.x, v2.p.y, v2.p.z);
	}

	final private void quad(Vertex v1, Vertex v2, Vertex v3, Vertex v4){
		papplet.vertex(v1.p.x, v1.p.y, v1.p.z);
		papplet.vertex(v2.p.x, v2.p.y, v2.p.z);
		papplet.vertex(v3.p.x, v3.p.y, v3.p.z);
		papplet.vertex(v4.p.x, v4.p.y, v4.p.z);
	}


}
