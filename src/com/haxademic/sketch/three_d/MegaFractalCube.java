package com.haxademic.sketch.three_d;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.camera.CameraOscillate;
import com.haxademic.core.camera.common.ICamera;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.render.JoonsWrapper;

public class MegaFractalCube
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// global vars
	protected float _frames = 50;
	protected FractCube _cube;
	protected ICamera camera;
	protected float BASE_CUBE_SIZE = 200;
	protected float MIN_CUBE_SIZE = 3;
	float percentComplete;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SUNFLOW, "true" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, "false" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, "low" );

		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "45" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "3" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+2) );

		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "640" );
		
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void setup() {
		super.setup();
		
		BASE_CUBE_SIZE = p.width/4f;
		
		if(p.appConfig.getBoolean("sunflow_active", false) == false) {
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
		
		
		if(p.appConfig.getBoolean("sunflow_active", false) == true) {
			joons.jr.background(255, 255, 255); //background(gray), or (r, g, b), like Processing.
			joons.jr.background("gi_instant"); //Global illumination, normal mode.
			joons.jr.background("gi_ambient_occlusion"); //Global illumination, ambient occlusion mode.
			setUpRoom();
		} else {
			background( 255 );
			
			p.ambientLight(102, 102, 122);
			p.lightSpecular(100, 100, 150);
			p.directionalLight(102, 132, 102, 0, 0, -1);
			p.specular(100, 150, 100);
			p.emissive(81, 51, 51);
			p.ambient(200, 200, 200);
			
			p.shininess(20.0f); 
			
			p.pointLight(0, 255, 255, 0, 0, -500);
			p.pointLight(255, 255, 0, 0, 0, -500);
			p.pointLight(0, 0, 0, 255, 500, 3000);
		}
		
		
		
//		camera( width/2, 700, 600, 400, 300, 0, 0, 1, 0);
//		translate(width/2, height/2, -800);
		translate(0, 0, -p.width);

		// angled view!
		translate(0, 0, p.width/1.7f);
		p.rotateX(-P.PI/4f * 10f);
		p.rotateZ(-P.PI/4f * 6f);

		
		p.rotateY(P.PI/2f * percentComplete);
		
//		if(camera != null) camera.update();
		_cube.update( 0, 0, 0 );
		
		
		if( p.frameCount == _frames + 2 ) {
			if(p.appConfig.getBoolean("rendering", false) ==  true) {				
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
			
			if(p.appConfig.getBoolean("sunflow_active", false) == true) {
				if(_baseSize == BASE_CUBE_SIZE) {
					joons.jr.fill( JoonsWrapper.MATERIAL_GLASS, 60, 60, 100);
				} else {
					joons.jr.fill( JoonsWrapper.MATERIAL_SHINY, 
							color + color/5f * P.sin(percentComplete * P.TWO_PI) * _x/40f, 
							color + color/5f * P.sin(percentComplete * P.TWO_PI + P.PI) * _y/40f,
							color + color/5f * P.sin(percentComplete * P.TWO_PI + P.PI/2f) * _z/40f
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
		joons.jr.background("cornell_box", 
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
