package com.haxademic.core.render;

import java.util.ArrayList;

import joons.JoonsRenderer;
import processing.core.PApplet;
import toxi.geom.Sphere;
import toxi.geom.mesh.WETriangleMesh;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorHax;
import com.haxademic.core.draw.util.DrawMesh;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class JoonsWrapper {

	public static final String QUALITY_HIGH = "bucket";
	public static final String QUALITY_LOW = "ipr";

	// http://sfwiki.geneome.net/index.php5?title=Shaders
	public static final String MATERIAL_DIFFUSE = "diffuse";
	public static final String MATERIAL_MIRROR = "mirror";
	public static final String MATERIAL_GLASS = "glass";
	public static final String MATERIAL_SHINY = "shiny";
	public static final String MATERIAL_CONSTANT = "constant";

	protected PApplet p;
	protected JoonsRenderer _jr;
	protected String _quality;

	protected int _objectMaterialIndex = 0;
	protected int _sphereMaterialIndex = 0;

	protected float _eyeX = 0;
	protected float _eyeY = 120;
	protected float _eyeZ = 40;
	protected float _centerX = 0;
	protected float _centerY = 0;
	protected float _centerZ = 0; // was 40
	protected float _upX = 0;
	protected float _upY = 0;
	protected float _upZ = -1;
	protected float _fov = P.PI / 4; 
	protected float _aspect = (float) 1;	//1.7777;  // was 1.3333
	protected float _zNear = 5;
	protected float _zFar = 10000;

	protected ArrayList<Integer> _colors;
	protected ArrayList<Integer> _reflectives;
	protected ArrayList<String> _shaderTypes;
	protected ArrayList<Boolean> _isSpheres;
	
	protected Boolean _isActive;

	public JoonsWrapper( PApplet p, int width, int height, String quality, Boolean isActive ) {
		this.p = p;
		_quality = quality;
		_isActive = isActive;

		_aspect = (float) width / (float) height;

		_jr = new JoonsRenderer( p, width, height );
		_jr.setRenderSpeed(1);							//render speed is 1 by default. Set it to 2 for x2 speed. Set it to any number. Lowers quality.

		_shaderTypes = new ArrayList<String>();
		_colors = new ArrayList<Integer>();
		_reflectives = new ArrayList<Integer>();
		_isSpheres = new ArrayList<Boolean>();
	}

	public void startFrame() {
		if( _isActive == true ) { 
			p.beginRecord("joons.OBJWriter","");	//	just call like this. Leave the second parameter as "".
	
			_objectMaterialIndex = 0;
			_sphereMaterialIndex = 0;
			
			_shaderTypes.clear();
			_colors.clear();
			_reflectives.clear();
			_isSpheres.clear();
		}

		p.perspective( _fov, _aspect, _zNear, _zFar);	// call perspective() before camera()!!
		p.camera( _eyeX, _eyeY, _eyeZ, _centerX, _centerY, _centerZ, _upX, _upY, _upZ );
	}

	public void endFrame( boolean saveFrameImg ) {
		if( _isActive == true ) { 
			p.endRecord();
	
			// add shader colors from array
			for(int i=0; i < _colors.size(); i++ ) {
				makeJoonsColor( "Color-"+i, _shaderTypes.get(i), _colors.get(i), _reflectives.get(i), _isSpheres.get(i) );
			}
	
			// set scene rendering config and do the deed
			// lots more info here: https://code.google.com/p/joons-renderer/wiki/3_Advanced_Use
			// http://sfwiki.geneome.net/index.php5?title=Main_Page
			_jr.setSC( FileUtil.getHaxademicDataPath() + "joons/ambient.sc" );	
			if( _jr.render( _quality ) == false ) {
				P.println("Error: Joons is having issues...");
			}
	
			// draw to screen and save an image, since drawing to screen doesn't necessarily work...
			_jr.display();	
			if( saveFrameImg == true ) p.saveFrame( FileUtil.getHaxademicOutputPath() + SystemUtil.getTimestamp(p) + "-render.png" );
		}
	}

	public void addColorForObject( String type, int color, int reflective, boolean sphere ) {
		if( _isActive == false ) return;
		_shaderTypes.add( type );
		_colors.add( color );
		_reflectives.add( reflective );
		_isSpheres.add( sphere );
		if( sphere == false ) p.noSmooth();
	}

	// TODO: fix this to make more sense with the size of the room... 400??
	public void drawRoomWithSizeAndColor( float width, float height, String shaderType, int color, int refl ) {

		// build room -------------------
		p.rect(-width*2f,-height*2f,width*4,height*4); // the floor plane
//		p.pushMatrix();
//		p.translate(0,0,-400);
//		p.rect(-width*2f,-height*2f,width*4,height*4); // the roof plane
//		p.popMatrix();
		
		p.pushMatrix();
		p.translate(0,-400,0);
		p.rotateX(P.PI/2);
		p.rect(-width*2f,-height*2f,width*4,height*4); // the back plane
		p.popMatrix();
		
		p.pushMatrix();
		p.translate(0,400,0);
		p.rotateX(P.PI/2);
		p.rect(-width*2f,-height*2f,width*4,height*4); // the behind-camera plane
		p.popMatrix();
		
		p.pushMatrix();
		p.translate(-400,0,0);
		p.rotateY(P.PI/2);
		p.rect(-width*2f,-height*2f,width*4,height*4); // left plane
		p.popMatrix();
		
		p.pushMatrix();
		p.translate(400,0,0);
		p.rotateY(P.PI/2);
		p.rect(-width*2f,-height*2f,width*4,height*4); // right plane
		p.popMatrix();
		
		p.pushMatrix();
		p.translate(0,0,400);
		p.rect(-width*2f,-height*2f,width*4,height*4); // the roof plane
		p.popMatrix();

		// add shader color after drawing
		addColorForObject( shaderType, color, refl, false );
	}
	
	public void drawRoomSphereWithColor( String shaderType, int color, int refl ) {
		WETriangleMesh mesh = new WETriangleMesh();
		mesh.addMesh( (new Sphere(400)).toMesh( 10 ) );
		DrawMesh.drawToxiMeshFacesNative(p, mesh);
		addColorForObject( shaderType, color, refl, false );
	}
	
	// internal methods during rendering -------------------------------------
	/**
	 * Auto-increment shader colors for every object that's drawn.
	 * This isn't necessarily efficient, but it is flexible for lots of dynamic colors
	 * @param shaderAutoId
	 * @param color
	 */
	protected void makeJoonsColor( String shaderAutoId, String type, int color, int refl, boolean sphere ) {
		_jr.addAfterShader("shader {");
		_jr.addAfterShader("   name " + shaderAutoId);
		_jr.addAfterShader("   type " + type);
		
		if( type == MATERIAL_DIFFUSE ) {
			_jr.addAfterShader("   diff "+ roundForShader( ColorHax.redFromColorInt(color) ) +" "+ roundForShader( ColorHax.greenFromColorInt(color) ) +" "+ roundForShader( ColorHax.blueFromColorInt(color) ) +"");
		} else if( type == MATERIAL_CONSTANT ) {
			_jr.addAfterShader("   color "+ roundForShader( ColorHax.redFromColorInt(color) ) +" "+ roundForShader( ColorHax.greenFromColorInt(color) ) +" "+ roundForShader( ColorHax.blueFromColorInt(color) ) +"");
		} else if( type == MATERIAL_MIRROR ) {
			_jr.addAfterShader("   refl "+ roundForShader( ColorHax.redFromColorInt(refl) ) +" "+ roundForShader( ColorHax.greenFromColorInt(refl) ) +" "+ roundForShader( ColorHax.blueFromColorInt(refl) ) +"");
		} else if( type == MATERIAL_SHINY ) {
			_jr.addAfterShader("   diff "+ roundForShader( ColorHax.redFromColorInt(color) ) +" "+ roundForShader( ColorHax.greenFromColorInt(color) ) +" "+ roundForShader( ColorHax.blueFromColorInt(color) ) +"");
			_jr.addAfterShader("   refl "+ refl +"");
		} else if( type == MATERIAL_GLASS ) {
			_jr.addAfterShader("   eta 1.6");
			_jr.addAfterShader("   color "+ roundForShader( ColorHax.redFromColorInt(refl) ) +" "+ roundForShader( ColorHax.greenFromColorInt(refl) ) +" "+ roundForShader( ColorHax.blueFromColorInt(refl) ) +"");
		}
		
		_jr.addAfterShader("}");
		_jr.addAfterShader("");

		// increment for next objects
		if( sphere == false ) {
			_jr.setShader("object"+_objectMaterialIndex, shaderAutoId);
			_objectMaterialIndex++;
		} else {
			_jr.setShader("sphere"+_sphereMaterialIndex, shaderAutoId);
			_sphereMaterialIndex++;
		}
	}

	protected float roundForShader( float value ) {
		value /= 255f;
		return Math.round(1000f*value)/1000f;
	}

}
