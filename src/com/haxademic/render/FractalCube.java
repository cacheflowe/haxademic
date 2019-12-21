package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.camera.CameraOscillate;
import com.haxademic.core.draw.camera.common.ICamera;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.render.JoonsWrapper;

public class FractalCube
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// global vars
	protected float _frames = 50;
	protected FractCube _cube;
	protected ICamera camera;
	protected float BASE_CUBE_SIZE = 200;
	protected float MIN_CUBE_SIZE = 6;
	protected float CHILD_RATIO = 0.5f;
	float percentComplete;

	protected void config() {
		Config.setProperty( AppSettings.SUNFLOW, true );
		Config.setProperty( AppSettings.SUNFLOW_ACTIVE, false );
		Config.setProperty( AppSettings.SUNFLOW_QUALITY, AppSettings.SUNFLOW_QUALITY_LOW );
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void firstFrame() {

		
		BASE_CUBE_SIZE = p.width/4f;
		
		if(Config.getBoolean(AppSettings.SUNFLOW_ACTIVE, false) == false) {
			background(255);
			noStroke();
		}

		_cube = new FractCube( BASE_CUBE_SIZE );
		camera = new CameraOscillate( this, 200, 200, 0, 200 );
	}

	public void drawApp() {
//		CHILD_RATIO = 0.25f + 0.1f * P.sin(p.frameCount * 0.01f);
		percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		if(Config.getBoolean(AppSettings.SUNFLOW_ACTIVE, false) == true) {
			joons.jr.background(255, 255, 255); //background(gray), or (r, g, b), like Processing.
			joons.jr.background("gi_instant"); //Global illumination, normal mode.
			joons.jr.background("gi_ambient_occlusion"); //Global illumination, ambient occlusion mode.
			setUpRoom();
		} else {
			background( 0 );
			PG.setBetterLights(p);
		}
		
		// camera 
		translate(0, 0, -p.width);
		p.rotateY(P.map(p.mouseX, 0, p.width, -P.TWO_PI, P.TWO_PI));
		p.rotateX(P.map(p.mouseY, 0, p.height, P.TWO_PI, -P.TWO_PI));
		
		// draw cube
		_cube.update( 0, 0, 0 );
	}
	
	public class FractCube {
		float _baseSize;
		float _curSize;
		float _x, _y, _z;
		protected FractCube[] _childrens;
		
		public FractCube( float size ) {
			_baseSize = size;
			_curSize = _baseSize;
			
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
//			if( _curSize < _baseSize ) _curSize += ( _baseSize - _curSize ) / 75;
			
			pushMatrix();
			
			// draw self
			translate( _x, _y, _z );
			
			int color = P.round(255f - (_curSize / BASE_CUBE_SIZE) * 235f);  
			int colorDark = P.round(150f - (_curSize / BASE_CUBE_SIZE) * 135f); 
			
			if(Config.getBoolean(AppSettings.SUNFLOW_ACTIVE, false) == true) {
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
				strokeWeight(0.5f);
			}

			box(_curSize);
			
			if( _childrens != null )
				if( _curSize > _baseSize / 2 )
					updateChildrenBoxen();
			
			popMatrix();
		}
		
		void updateChildrenBoxen() {
			// half size of both boxes
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
