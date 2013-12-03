
package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;

public class JoonsTesterHax extends PAppletHax {

	//    JoonsRenderer jr;
	//
	//    //camera declarations
	//    float eyeX = 0;
	//    float eyeY = 0;
	//    float eyeZ = 0;
	//    float centerX = 0;
	//    float centerY = 0;
	//    float centerZ = -1;
	//    float upX = 0;
	//    float upY = 1;
	//    float upZ = 0;
	//    float fov = PI / 4; 
	//    float aspect = 4/3f;  
	//    float zNear = 5;
	//    float zFar = 10000;

	protected void overridePropsFile() {
		_appConfig.setProperty( "sunflow", "true" );
		_appConfig.setProperty( "sunflow_active", "true" );
		_appConfig.setProperty( "sunflow_quality", "high" );


		_appConfig.setProperty( "width", "800" );
		_appConfig.setProperty( "height", "600" );
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "fps", "30" );
	}

	//    public void setup() {
	//            size(800, 600, P3D);
	//            jr = new JoonsRenderer(this);
	//            _jw.jr.setSampler("bucket"); //Rendering mode, either "ipr" or "bucket".
	//            _jw.jr.setSizeMultiplier(1); //Set size of the .PNG file as a multiple of the Processing sketch size.
	//            _jw.jr.setAA(-2, 0, 1); //Set anti-aliasing, (min, max, samples). -2 < min, max < 2, samples = 1,2,3,4..
	//            _jw.jr.setCaustics(1); //Set caustics. 1 ~ 100. affects quality of light scattered through glass.
	//            //_jw.jr.setTraceDepth(1,4,4); //Set trace depth, (diffraction, reflection, refraction). Affects glass. (1,4,4) is good.
	//            //_jw.jr.setDOF(170, 5); //Set depth of field of camera, (focus distance, lens radius). Larger radius => more blurry.
	//    }

	public void drawApp() {
		//    	if( p.frameCount >= 4 ) _jw.jr.render();
		//            _jw.jr.beginRecord(); //Make sure to include methods you want rendered.
		//            camera(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		//            perspective(fov, aspect, zNear, zFar);

		_jw.jr.background(0, 255, 255); //background(gray), or (r, g, b), like Processing.
		_jw.jr.background("gi_instant"); //Global illumination, normal mode.
		//_jw.jr.background("gi_ambient_occlusion"); //Global illumination, ambient occlusion mode.

//		pushMatrix();
//		translate(0, 0, -120);
//		_jw.jr.background("cornell_box", 100, 100, 100); //cornellBox(width, height, depth);
//		popMatrix();

		pushMatrix();
		translate(-40, 20, -140);
		pushMatrix();
		rotateY(-PI/8);

		//_jw.jr.fill("light"); or
		//_jw.jr.fill("light", r, g, b); or
		//_jw.jr.fill("light", r, g, b, int samples);
		_jw.jr.fill("light", 5, 5, 5);
		sphere(13);
		translate(27, 0, 0);

		//_jw.jr.fill("mirror"); or
		//_jw.jr.fill("mirror", r, g, b);    
		_jw.jr.fill("mirror", 255, 255, 255);
		sphere(13);
		translate(27, 0, 0);

		//_jw.jr.fill("diffuse"); or
		//_jw.jr.fill("diffuse", r, g, b);
		_jw.jr.fill("diffuse", 150, 255, 255);
		sphere(13);
//		beginShape(TRIANGLES);
//		vertex(0, 10, 0);
//		vertex(10, -10, 0);
//		vertex(-10, -15, 0);
//		endShape();
		translate(27, 0, 0);

		//_jw.jr.fill("shiny"); or
		//_jw.jr.fill("shiny", r, g, b);  or
		//_jw.jr.fill("shiny", r, g, b, shininess);  or
		_jw.jr.fill("shiny", 150, 255, 255, 0.1f);
		sphere(13);
		translate(27, 0, 0);
		popMatrix();
		rotateY(PI/8);
		translate(-10, -27, 30);

		//_jw.jr.fill("ambient_occlusion"); or
		//_jw.jr.fill("ambient_occlusion", bright r, bright g, bright b); or
		//_jw.jr.fill("ambient occlusion", bright r, bright g, bright b, dark r, dark g, dark b, maximum distance, int samples);
		_jw.jr.fill("ambient_occlusion", 150, 255, 255, 0, 0, 255, 50, 16);
		sphere(13);
		translate(27, 0, 0);

		//_jw.jr.fill("phong", r, g, b);
		_jw.jr.fill("phong", 150, 255, 255);
		sphere(13);
		translate(27, 0, 0);

		//_jw.jr.fill("glass", r, g, b);
		_jw.jr.fill("glass", 255, 255, 255);
		sphere(13);
		translate(27, 0, 0);

		//_jw.jr.fill("constant", r, g, b);
		_jw.jr.fill("constant", 150, 255, 255);
		sphere(13);
		popMatrix();

		//            _jw.jr.endRecord(); //Make sure to end record.
		//            _jw.jr.displayRendered(true); //Display rendered image if rendering completed, and the argument is true.
	}

	public void keyPressed() {
		if (key == 'r' || key == 'R') _jw.jr.render(); //Press 'r' key to start rendering.
	}

}
