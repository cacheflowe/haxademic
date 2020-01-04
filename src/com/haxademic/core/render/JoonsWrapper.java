package com.haxademic.core.render;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.toxi.DrawToxiMesh;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import joons.JoonsRenderer;
import processing.core.PApplet;
import toxi.geom.Sphere;
import toxi.geom.mesh.WETriangleMesh;

public class JoonsWrapper {

	public static final String QUALITY_HIGH = "bucket";
	public static final String QUALITY_LOW = "ipr";

	
	// Backgrounds ========================================================
	public static final String BACKGROUND_GI = "gi_instant";
	public static final String BACKGROUND_AO = "gi_ambient_occlusion";
	public static final String CORNELL_BOX = "cornell_box";
    //jr.background(0, 255, 255); //background(gray), or (r, g, b), like Processing.
    //jr.background("gi_instant"); //Global illumination, normal mode.
    //jr.background("gi_ambient_occlusion"); //Global illumination, ambient occlusion mode.

	
	// Materials ==========================================================
	public static final String MATERIAL_DIFFUSE = "diffuse";
    //jr.fill("diffuse"); or
    //jr.fill("diffuse", r, g, b);
	public static final String MATERIAL_MIRROR = "mirror";
    //jr.fill("mirror"); or
    //jr.fill("mirror", r, g, b);    
	public static final String MATERIAL_GLASS = "glass";
    //jr.fill("glass", r, g, b);
	public static final String MATERIAL_SHINY = "shiny";
    //jr.fill("shiny"); or
    //jr.fill("shiny", r, g, b);  or
    //jr.fill("shiny", r, g, b, shininess);  or
	public static final String MATERIAL_CONSTANT = "constant";
    //jr.fill("constant", r, g, b);
	public static final String MATERIAL_LIGHT = "light";
    //jr.fill("light"); or
    //jr.fill("light", r, g, b); or
    //jr.fill("light", r, g, b, int samples);
	public static final String MATERIAL_PHONG = "phong";
    //jr.fill("phong", r, g, b);
	public static final String MATERIAL_AMBIENT_OCCLUSION = "ambient_occlusion";
    //jr.fill("ambient_occlusion"); or
    //jr.fill("ambient_occlusion", bright r, bright g, bright b); or
    //jr.fill("ambient occlusion", bright r, bright g, bright b, dark r, dark g, dark b, maximum distance, int samples);

	protected PApplet p;
	public JoonsRenderer jr;
	protected String _quality;
	
	protected float _eyeX = 0;
	protected float _eyeY = 0;
	protected float _eyeZ = 0;
	protected float _centerX = 0;
	protected float _centerY = 0;
	protected float _centerZ = -1;
	protected float _upX = 0;
	protected float _upY = 1;
	protected float _upZ = 0;
	protected float _fov = P.PI / 4f; 
	protected float _aspect = 4/3f;  
	protected float _zNear = 5;
	protected float _zFar = 10000;
	
	protected Boolean _isActive;

	public JoonsWrapper( PApplet p, int width, int height, String quality, Boolean isActive ) {
		this.p = p;
		_quality = quality;
		_isActive = isActive;

		_aspect = (float) width / (float) height;

		jr = new JoonsRenderer( p );
		jr.setSampler(_quality); //Rendering mode, either "ipr" or "bucket".
		jr.setSizeMultiplier(1); //Set size of the .PNG file as a multiple of the Processing sketch size.
		if( quality == QUALITY_HIGH ) {
			jr.setAA(-0, 2, 4); //Set anti-aliasing, (min, max, samples). -2 < min, max < 2, samples = 1,2,3,4..
		} else {
			jr.setAA(-2, 0, 1);
		}
        jr.setCaustics(1); //Set caustics. 1 ~ 100. affects quality of light scattered through glass.
        //jr.setDOF(170, 5); //Set depth of field of camera, (focus distance, lens radius). Larger radius => more blurry.
	}

	public void startFrame() {
		if( _isActive == true ) {
			if( p.frameCount >= 1 ) jr.render();
			jr.beginRecord(); 
		}
		p.camera( _eyeX, _eyeY, _eyeZ, _centerX, _centerY, _centerZ, _upX, _upY, _upZ );
		p.perspective( _fov, _aspect, _zNear, _zFar);
	}

	public void endFrame( boolean saveFrameImg ) {
		if( _isActive == true ) { 
            jr.endRecord(); //Make sure to end record.
            PG.setDrawCorner(p);
            jr.displayRendered(true); //Display rendered image if rendering completed, and the argument is true.
			if( saveFrameImg == true ) {
				String sunflowOutputPath = FileUtil.haxademicOutputPath()+"/_sunflow/";
				if(FileUtil.fileOrPathExists(sunflowOutputPath) == false) FileUtil.createDir(sunflowOutputPath);
				p.save(sunflowOutputPath + SystemUtil.getTimestamp() + "-render.png");
			}
		}
	}

	public void addColorForObject( String type, int color, int reflective, boolean sphere ) {
//		if( _isActive == false ) return;
//		_shaderTypes.add( type );
//		_colors.add( color );
//		_reflectives.add( reflective );
//		_isSpheres.add( sphere );
//		if( sphere == false ) p.noSmooth();
	}

	// TODO: fix this to make more sense with the size of the room... 400??
	public void drawRoomWithSizeAndColor( float width, float height ) {

	}
	
	public void drawRoomSphereWithColor( String shaderType, int color, int refl ) {
		WETriangleMesh mesh = new WETriangleMesh();
		mesh.addMesh( (new Sphere(400)).toMesh( 10 ) );
		DrawToxiMesh.drawToxiMeshFacesNative(p, mesh);
//		addColorForObject( shaderType, color, refl, false );
	}
	
	
	// internal methods during rendering -------------------------------------
//	/**
//	 * Auto-increment shader colors for every object that's drawn.
//	 * This isn't necessarily efficient, but it is flexible for lots of dynamic colors
//	 * @param shaderAutoId
//	 * @param color
//	 */
//	protected void makeJoonsColor( String shaderAutoId, String type, int color, int refl, boolean sphere ) {
//		_jr.addAfterShader("shader {");
//		_jr.addAfterShader("   name " + shaderAutoId);
//		_jr.addAfterShader("   type " + type);
//		
//		if( type == MATERIAL_DIFFUSE ) {
//			_jr.addAfterShader("   diff "+ roundForShader( ColorHax.redFromColorInt(color) ) +" "+ roundForShader( ColorHax.greenFromColorInt(color) ) +" "+ roundForShader( ColorHax.blueFromColorInt(color) ) +"");
//		} else if( type == MATERIAL_CONSTANT ) {
//			_jr.addAfterShader("   color "+ roundForShader( ColorHax.redFromColorInt(color) ) +" "+ roundForShader( ColorHax.greenFromColorInt(color) ) +" "+ roundForShader( ColorHax.blueFromColorInt(color) ) +"");
//		} else if( type == MATERIAL_MIRROR ) {
//			_jr.addAfterShader("   refl "+ roundForShader( ColorHax.redFromColorInt(refl) ) +" "+ roundForShader( ColorHax.greenFromColorInt(refl) ) +" "+ roundForShader( ColorHax.blueFromColorInt(refl) ) +"");
//		} else if( type == MATERIAL_SHINY ) {
//			_jr.addAfterShader("   diff "+ roundForShader( ColorHax.redFromColorInt(color) ) +" "+ roundForShader( ColorHax.greenFromColorInt(color) ) +" "+ roundForShader( ColorHax.blueFromColorInt(color) ) +"");
//			_jr.addAfterShader("   refl "+ refl +"");
//		} else if( type == MATERIAL_GLASS ) {
//			_jr.addAfterShader("   eta 1.6");
//			_jr.addAfterShader("   color "+ roundForShader( ColorHax.redFromColorInt(refl) ) +" "+ roundForShader( ColorHax.greenFromColorInt(refl) ) +" "+ roundForShader( ColorHax.blueFromColorInt(refl) ) +"");
//		}
//		
//		_jr.addAfterShader("}");
//		_jr.addAfterShader("");
//
//		// increment for next objects
//		if( sphere == false ) {
//			_jr.setShader("object"+_objectMaterialIndex, shaderAutoId);
//			_objectMaterialIndex++;
//		} else {
//			_jr.setShader("sphere"+_sphereMaterialIndex, shaderAutoId);
//			_sphereMaterialIndex++;
//		}
//	}

	protected float roundForShader( float value ) {
		value /= 255f;
		return Math.round(1000f*value)/1000f;
	}
	
	public void setUpRoom(int r, int g, int b) {
		P.p.pushMatrix();
		P.p.translate(0, 0, 0);
		float radiance = 10;
		int samples = 16;
		jr.background(JoonsWrapper.CORNELL_BOX, 
				4000, 3000, 5000,						// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				r, g, b, 								// left rgb
				r, g, b, 								// right rgb
				r, g, b, 								// back rgb
				r, g, b, 								// top rgb
				r, g, b 								// bottom rgb
		); 
		P.p.popMatrix();		
	}


}
