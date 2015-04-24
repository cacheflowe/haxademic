package com.haxademic.sketch.three_d;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.cameras.CameraOscillate;
import com.haxademic.core.cameras.common.ICamera;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.render.Renderer;

@SuppressWarnings({ "serial" })
public class MegaFractalCube
extends PAppletHax
{
	// global vars
	protected float _frames = 50;
	protected FractCube _cube;
	protected int _cols = 10;
	protected int _rows = 10;
	protected ICamera camera;
	protected int NUM_BLOCKS = 20;  
	protected Renderer _render;
	protected float rotInc = 0;
	protected float BASE_CUBE_SIZE = 200;
	protected float MIN_CUBE_SIZE = 7;
	protected boolean RENDERING = false;
	float percentComplete;

	protected void overridePropsFile() {
		_appConfig.setProperty( "sunflow", "true" );
		_appConfig.setProperty( "sunflow_active", "false" );
		_appConfig.setProperty( "sunflow_quality", "low" );

		_appConfig.setProperty( "rendering_gif", "true" );
		_appConfig.setProperty( "rendering_gif_framerate", "45" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "3" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+2) );

		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "640" );
		
		_appConfig.setProperty( "rendering", "false" );
	}

	public void setup() {
		super.setup();
		
		BASE_CUBE_SIZE = p.width/4f;
		
		if(_appConfig.getBoolean("sunflow_active", false) == false) {
			p.smooth(OpenGLUtil.SMOOTH_HIGH);
			lights();
			shininess(500); 
			background(255);
			noStroke();
		}

		_cube = new FractCube( BASE_CUBE_SIZE );
		camera = new CameraOscillate( this, 200, 200, 0, 200 );
	}

	public void drawApp() {
		
		percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutCubic(percentComplete, 0, 1, 1);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);

		
		
		if(_appConfig.getBoolean("sunflow_active", false) == true) {
			_jw.jr.background(255, 255, 255); //background(gray), or (r, g, b), like Processing.
			_jw.jr.background("gi_instant"); //Global illumination, normal mode.
			_jw.jr.background("gi_ambient_occlusion"); //Global illumination, ambient occlusion mode.
			setUpRoom();
		} else {
			background( 255 );
			
//			p.ambientLight(102, 102, 102);
//			p.lightSpecular(100, 100, 100);
//			p.directionalLight(102, 102, 102, 0, 0, -1);
//			p.specular(100, 100, 100);
//			p.emissive(51, 51, 51);
//			p.ambient(50, 50, 50);
			
			p.shininess(20.0f); 
			
//			p.pointLight(0, 255, 255, 0, 0, -500);
//			p.pointLight(255, 255, 0, 0, 0, -500);
//			p.pointLight(0, 0, 0, 255, 500, 3000);
		}
		
		
		
//		camera( width/2, 700, 600, 400, 300, 0, 0, 1, 0);
//		translate(width/2, height/2, -800);
		translate(0, 0, -p.width);
		
		p.rotateY(P.PI/2f * percentComplete);
		
//		if(camera != null) camera.update();
		_cube.update( 0, 0, 0 );
		
		
		if( p.frameCount == _frames + 2 ) {
			if(_appConfig.getBoolean("rendering", false) ==  true) {				
				_renderer.stop();
				P.println("render done!");
			}
		}

	}
	
	public class FractCube {
		float _baseSize;
		float _curSize;
		float _x, _y, _z;
		protected FractCube[] _childrens;
		protected float CHILD_RATIO = 0.5f;
		
		public FractCube( float size ) {
			_baseSize = size;
			_curSize = _baseSize;	//0;// _baseSize * 1f 
			
			if( _baseSize > MIN_CUBE_SIZE ) {
				_childrens = new FractCube[ 6 ];
					
				// Initialize each object with base size
				for ( int i = 0; i < _childrens.length; i++ ) {
					_childrens[i] = new FractCube( _baseSize * CHILD_RATIO );
				}
			}
		} 
		
		float getCurrentSizeRatio()
		{
			if( _curSize == 0 )
				return .0000001f;
			else
				return _curSize / _baseSize;
		}

		/**
		 * Place and draw each cube
		 */
		void update( float x, float y, float z ) {
			// store 3d coordinates
			_x = x;
			_y = y;
			_z = z;
			
			// ease up the size
			if( _curSize < _baseSize ) _curSize += ( _baseSize - _curSize ) / 75;
			
			pushMatrix();
			
			// draw self
			translate( _x, _y, _z );
			
			int color = P.round(255f - (_curSize / BASE_CUBE_SIZE) * 235f);  
			int colorDark = P.round(150f - (_curSize / BASE_CUBE_SIZE) * 135f); 
			
			if(_appConfig.getBoolean("sunflow_active", false) == true) {
				if(_baseSize == BASE_CUBE_SIZE) {
					_jw.jr.fill( JoonsWrapper.MATERIAL_GLASS, 0, 0, 100);
				} else {
					_jw.jr.fill( JoonsWrapper.MATERIAL_SHINY, 
							color + color/4f * P.sin(percentComplete * P.TWO_PI) * _x/10f, 
							color + color/4f * P.sin(percentComplete * P.TWO_PI + P.PI) * _y/10f,
							color + color/4f * P.sin(percentComplete * P.TWO_PI + P.PI/2f) * _z/10f
							);
				}
			} else {
//				fill(p.color(color));
				fill(
					color + color/5f * P.sin(percentComplete * P.TWO_PI) * _x/40f, 
					color + color/5f * P.sin(percentComplete * P.TWO_PI + P.PI) * _y/40f,
					color + color/5f * P.sin(percentComplete * P.TWO_PI + P.PI/2f) * _z/40f
				);
				stroke(p.color(colorDark));
				strokeWeight(0.2f);
			}

			box(_curSize);
			
			if( _childrens != null )
				if( _curSize > _baseSize / 2 )
					updateChildrenBoxen();
			
			popMatrix();
		}
		
		void updateChildrenBoxen() {
			// half size of 
			float distance = ( _curSize / 2 ) + ( _curSize * CHILD_RATIO ) / 2;
			
			// update 6 sides
			_childrens[0].update( 0 + distance * _childrens[0].getCurrentSizeRatio(), 0, 0 );
			_childrens[1].update( 0 - distance * _childrens[1].getCurrentSizeRatio(), 0, 0 );
			_childrens[2].update( 0, 0 + distance * _childrens[2].getCurrentSizeRatio(), 0 );
			_childrens[3].update( 0, 0 - distance * _childrens[3].getCurrentSizeRatio(), 0 );
			_childrens[4].update( 0, 0, 0 + distance * _childrens[4].getCurrentSizeRatio() );
			_childrens[5].update( 0, 0, 0 - distance * _childrens[5].getCurrentSizeRatio() );
		
		}
	}
	
	
	
	protected void setUpRoom() {
		pushMatrix();
		translate(0, 0, -1000);
		float radiance = 20;
		int samples = 16;
		_jw.jr.background("cornell_box", 
				12000, 6000, 6000,	// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				255, 255, 255, // left rgb
				255, 255, 255, // right rgb
				255, 255, 255, // back rgb
				255, 255, 255, // top rgb
				255, 255, 255  // bottom rgb
		); 
		popMatrix();		
	}

}
