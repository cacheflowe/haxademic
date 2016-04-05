//package com.haxademic.sketch.three_d;
//import joons.JoonsRenderer;
//import processing.core.PConstants;
//import toxi.geom.mesh.Face;
//import toxi.geom.mesh.WETriangleMesh;
//
//import com.haxademic.core.app.PAppletHax;
//import com.haxademic.core.draw.mesh.MeshUtil;
//import com.haxademic.core.draw.shapes.Shapes;
//import com.haxademic.core.system.FileUtil;
//import com.haxademic.core.system.SystemUtil;
//
////public class SunflowJoonsTest
//extends PAppletHax {
//	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
//
//	JoonsRenderer jr;
//	boolean rendered = false;
//	boolean autoRender = true;
//
//	float eyeX = 0;
//	float eyeY = 120;
//	float eyeZ = 40;
//	float centerX = 0;
//	float centerY = 0;
//	float centerZ = 0; // was 40
//	float upX = 0;
//	float upY = 0;
//	float upZ = -1;
//	float fov = PI / 4; 
//	float aspect = (float) 1.7777;  // was 1.3333
//	float zNear = 5;
//	float zFar = 10000;
//	
//	WETriangleMesh _mesh;
//
//	protected void overridePropsFile() {
//		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
//		p.appConfig.setProperty( AppSettings.WIDTH, "1280" );
//		p.appConfig.setProperty( AppSettings.HEIGHT, "720" );
//	}
//
//	public void setup() {
//		super.setup();
//		jr = new JoonsRenderer(this,width,height);//just declare like this.
//		jr.setRenderSpeed(1);//render speed is 1 by default. Set it to 2 for x2 speed. Set it to any number. Lowers quality.
//		
//		jr.addBeforeShader("light {");
//		jr.addBeforeShader("   type spherical");
//		jr.addBeforeShader("   color { \"sRGB nonlinear\" 1.000 1.000 0.500 }");
//		jr.addBeforeShader("   radiance 100.0");
//		jr.addBeforeShader("   center 0 0 0");
//		jr.addBeforeShader("   radius 0");
//		jr.addBeforeShader("   samples 16");
//		jr.addBeforeShader("}");
//		jr.addBeforeShader("");
//		
//		
//		_mesh = MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/mode-set.obj", 40f );
//	}
//
//	public void drawApp() {
//		lights();
//		fill(255);
//		noStroke();
//
//		beginRecord("joons.OBJWriter","");//just call like this. Leave the second parameter as "".
//
//		jr.addAfterShader("shader {");
//		jr.addAfterShader("   name Green"+frameCount);
//		jr.addAfterShader("   type diffuse");
//		jr.addAfterShader("   diff 0.2 "+Math.round(1000f*(Math.sin(frameCount/100f)/2f + 0.5f))/1000f+" 0.2");
//		jr.addAfterShader("}");
//		jr.addAfterShader("");
//
//		
//		
//		perspective(fov, aspect, zNear, zFar);//call perspective() before camera()!!
//		camera(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
//
//		// spin room
//		rotateZ(frameCount/20f);
//		
//		// build room -------------------
//		rect(-width*2f,-height*2f,width*4,height*4); // the floor plane
//		
//		pushMatrix();
//		translate(0,-400,0);
//		rotateX(PI/2);
//		rect(-width*2f,-height*2f,width*4,height*4); // the back plane
//		popMatrix();
//		
//		pushMatrix();
//		translate(0,400,0);
//		rotateX(PI/2);
//		rect(-width*2f,-height*2f,width*4,height*4); // the behind-camera plane
//		popMatrix();
//		
//		pushMatrix();
//		translate(-400,0,0);
//		rotateY(PI/2);
//		rect(-width*2f,-height*2f,width*4,height*4); // left plane
//		popMatrix();
//		
//		pushMatrix();
//		translate(400,0,0);
//		rotateY(PI/2);
//		rect(-width*2f,-height*2f,width*4,height*4); // right plane
//		popMatrix();
//		
//		pushMatrix();
//		translate(0,0,400);
//		rect(-width*2f,-height*2f,width*4,height*4); // the roof plane
//		popMatrix();
//				
//		// set next material and draw boxes ------------------
//		noSmooth();
//		pushMatrix();
//		translate(40,-80,30);
//		rotateZ(PI/8);
//		rotateX(-PI/8 * frameCount/20f);
//		box(20);
//		popMatrix();
//
//		fill(255);
//		pushMatrix();
//		translate(0,0,0); 
//		drawToxiFaces( _mesh );
////		Shapes.drawDisc(p, 20, 15, 50);
////		translate(0,0,20); 
////		Shapes.drawDisc(p, 25, 20, 50);
//		popMatrix();
//
//
//		noSmooth();
//		pushMatrix();
//		translate(60,-80,30);
//		rotateZ(PI/8);
//		rotateX(PI/8 * frameCount/20f);
//		box(14);
//		popMatrix();
//
//		pushMatrix();
//		translate(60,30,0);
//		Shapes.drawPyramid(p, 20, 40, true);
//		popMatrix();
//
//////		Shapes.drawDisc3D(p, 10, 8, 2, 20, p.color(0), p.color(127));
////		pushMatrix();
////		translate(0,-20,30);
//////		Shapes.drawDisc(p, 10, 8, 20);
////		popMatrix();
//
//		pushMatrix();
//		translate(-80,-60,30);
//		fill(255);
//		sphere(30);
//		popMatrix();
//
//		pushMatrix();
//		translate(-60,30, 30);
//		fill(255);
//		sphere(15);
//		popMatrix();
//
//		
//		
//		endRecord();
//
//		if( autoRender ) {
////			jr.setShader("object0", "Green");	// +frameCount
//			jr.setShader("object0", "ModeBlue");
//			jr.setShader("object1", "Glossy");
//			jr.setShader("object2", "Glass");
//			jr.setShader("sphere0", "Glass");
//			jr.setShader("sphere1", "Mirror");
//			jr.setSC(FileUtil.getHaxademicDataPath()+"joons/ambient.sc");	// http://sfwiki.geneome.net/index.php5?title=Main_Page
//			rendered=jr.render("bucket");
//			// render using render("ipr") to render quick and rough,
//			// and render("bucket") to render slow and smooth
//			// if successfully rendered, render() returns true
//			
//			// lots more info here: https://code.google.com/p/joons-renderer/wiki/3_Advanced_Use
//			jr.display();
//			saveFrame(FileUtil.getHaxademicOutputPath()+SystemUtil.getTimestamp(p)+"render.png");
//		}
//	}
//	
//	
//	
//	protected void drawToxiFaces( WETriangleMesh mesh ) {
//		Face f;
//		for( int i = 0; i < mesh.faces.size(); i++ ) {
//			p.beginShape(PConstants.TRIANGLES);
//			f = mesh.faces.get( i );
//			p.vertex(f.a.x, f.a.y, f.a.z);
//			p.vertex(f.b.x, f.b.y, f.b.z);
//			p.vertex(f.c.x, f.c.y, f.c.z);
//			p.endShape();
//		}		
//	}
//		
//}
//
