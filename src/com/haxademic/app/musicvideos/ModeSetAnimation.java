package com.haxademic.app.musicvideos;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.toxi.MeshUtilToxi;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.ElasticFloat;
import com.haxademic.core.vendor.Toxiclibs;

import toxi.color.TColor;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.WETriangleMesh;

public class ModeSetAnimation
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	
	/**
	 * Sequence:
	 * Fade black to blue
	 * Blue text pokes out and recedes
	 * Vertex points bounce in, revealing logo
	 * Spin back starts slow and accelerates as points bounce in to singular point
	 */
	protected int _mode;
	protected final int FADE_IN = 0;
	protected final int TEXT_SHOW = 1;
	protected final int LOGO_VERTEXES = 2;
	protected final int SPIN_BACK = 3;
	protected final int REBUILD_LOGO = 4;
	protected final int GROW_LOGO = 5;
	protected final int FILL_SCREEN = 6;
	protected final int FADE_OUT = 7;
	protected int _curModeFrames = 0;

	protected WETriangleMesh _mesh;
	protected WETriangleMesh _meshDeform;
	protected WETriangleMesh _meshText;
	protected final EasingColor MODE_SET_BLUE = new EasingColor( 0, 200, 234, 255 );
	protected final EasingColor MODE_SET_GREY = new EasingColor( 96, 96, 96, 255 );
	protected final EasingColor BLACK = 		new EasingColor( 0, 0, 0, 255 );
	protected EasingColor _logoColor;
	protected EasingColor _textColor;
	protected EasingColor _particleColor;
	protected float _textZ = 0;
	protected float _textXRot = 0;
	protected float _textZIncrementer = 0;
	protected float _spinX = 1;
	protected boolean _wireframe = false;
	protected EasingFloat _baseRotX;
	protected EasingFloat _audioLightener;
	protected MeshParticles _particles;
	
	protected ArrayList<ElasticFloat> _elasticVertices;
		
	public void settings() {
		customPropsFile = FileUtil.getHaxademicDataPath() + "properties/modesetlogo.properties";
		super.settings();
	}

	public void setup() {
		super.setup();
		initRender();
	}

	public void initRender() {
		// initialize the mode
		_mode = FADE_IN;
		
		// get meshes
//		_mesh = MeshUtilToxi.meshFromOBJ( p, "../data/models/mode-set.obj", 300f );
		_meshDeform = _mesh.copy();
		_meshText = MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, "../data/svg/modeset-logotype.svg", 10, -1, 0.7f ), 250 );
		_particles = new MeshParticles( _meshText.copy() );
		
		// set up color
		_logoColor = new EasingColor( MODE_SET_BLUE.r(), MODE_SET_BLUE.g(), MODE_SET_BLUE.b() );
		_textColor = new EasingColor( MODE_SET_BLUE.r(), MODE_SET_BLUE.g(), MODE_SET_BLUE.b() );
		_particleColor = new EasingColor( MODE_SET_GREY.r(), MODE_SET_GREY.g(), MODE_SET_GREY.b() );
		
		// store elastic points per vertex
		int numVertices = _mesh.getNumVertices();
		_elasticVertices = new ArrayList<ElasticFloat>();
		for( int i = 0; i < numVertices; i++ ) {
			_elasticVertices.add( new ElasticFloat( 10f, 0.5f, 0.5f ) );
		}
		
		// other draw props
		_baseRotX = new EasingFloat( P.TWO_PI/15f, 20f ); 
		_textXRot = P.PI/12f;
		_audioLightener = new EasingFloat( 0f, 20f );
		
		// for sunflow, we need to set these before the first draw()
		p.background( 0 );
		p.smooth();
		p.noStroke();
	}
		
	public void drawApp() {
		DrawUtil.setBasicLights( p );
		// draw background and set to center
		if( PRenderers.currentRenderer() == P.P3D ) p.background(0,0,0,255);
		p.translate(p.width/2, p.height/2, -400);
		
		// update easing values
		_baseRotX.update();
		_audioLightener.update();
		
		// keyframing for different sections
		if( _mode == FADE_IN ) {
			p.rotateX( _baseRotX.value() );
			if( _curModeFrames == 1 ) {
				_logoColor.setTargetInt( MODE_SET_BLUE.colorInt() );
			}
			if( _curModeFrames == 30 ) switchMode( TEXT_SHOW );
		} else if( _mode == TEXT_SHOW ) {
			p.rotateX( _baseRotX.value() );
			if( _curModeFrames == 1 ) _baseRotX.setTarget( 0f );
			_textZ = P.sin( _textZIncrementer ) * 60;
			_textXRot -= P.PI/500f;
			_textZIncrementer += P.TWO_PI/170f;
			if( _curModeFrames == 80 ) switchMode( LOGO_VERTEXES );
		} else if( _mode == LOGO_VERTEXES ) {
			if( _curModeFrames == 1 ) _audioLightener.setTarget( 500f );
			int curVert = _curModeFrames % _mesh.getNumVertices();
			_elasticVertices.get( curVert ).setTarget( 1f );
//			_wireframe = ( _curModeFrames > 40 && _curModeFrames < 50 && _curModeFrames % 3 == 0 ) ? true : false;
			if( _curModeFrames == 80 ) switchMode( SPIN_BACK );
		} else if( _mode == SPIN_BACK ) {
			if( _curModeFrames == 1 ) setExtraBounce();
			int curVert = _curModeFrames % _mesh.getNumVertices();
			_elasticVertices.get( curVert ).setTarget( 0f );
			p.rotateX(_curModeFrames * (_spinX/200f));
			_spinX++;
			if( _curModeFrames == 180 ) switchMode( GROW_LOGO );
		} else if( _mode == GROW_LOGO ) {
			if( _curModeFrames == 1 ) setNormalBounce();
			int curVert = _curModeFrames % _mesh.getNumVertices();
			_elasticVertices.get( curVert ).setTarget( 1f );
			if( _curModeFrames == 75 ) switchMode( FILL_SCREEN );
		} else if( _mode == FILL_SCREEN ) {
			if( _curModeFrames == 1 ) setNormalBounce();
			int curVert = _curModeFrames % _mesh.getNumVertices();
			_elasticVertices.get( curVert ).setTarget( 10f );
			if( _curModeFrames == 50 ) switchMode( FADE_OUT );
		} else if( _mode == FADE_OUT ) {
			if( _curModeFrames == 1 ) _logoColor.setTargetInt( BLACK.colorInt() );
			if( _curModeFrames == 70 ) p.exit();
		}
		_curModeFrames++;
		
		// draw objects
		drawLogo();
		if( _mode == TEXT_SHOW ) drawText();
		drawTextExplode();
	}
	
	protected void switchMode( int mode ) {
		_mode = mode;
		_curModeFrames = 0;
	}
	
	protected void setExtraBounce() {
		for( int i = 0; i < _elasticVertices.size(); i++ ) {
			_elasticVertices.get( i ).setAccel( 0.75f );
			_elasticVertices.get( i ).setFriction( 0.75f );
		}
	}
	
	protected void setNormalBounce() {
		for( int i = 0; i < _elasticVertices.size(); i++ ) {
			_elasticVertices.get( i ).setAccel( 0.5f );
			_elasticVertices.get( i ).setFriction( 0.5f );
		}
	}
	
	protected void drawLogo() {
		deformWithElasticAndAudio();
		_logoColor.update();

		p.pushMatrix();
		p.translate( 0, 0, 0 );
		// draw to screen
//		p.rotateY( p.frameCount / 100f );
		if( _wireframe == false ) {
			p.fill( _logoColor.colorInt() );
			p.noStroke();
		} else {
			p.stroke( _logoColor.colorInt() );
			p.fill( BLACK.colorInt() );
		}
		Toxiclibs.instance(p).toxi.mesh( _meshDeform );
//		drawToxiFaces( _meshDeform, _logoColor.color() );
		p.popMatrix();
	}
	
	protected void drawText() {
		if( _curModeFrames == 40 ) _textColor.setTargetInt( MODE_SET_GREY.colorInt() );
		_textColor.update();
		p.pushMatrix();
		p.translate( 0, 0, _textZ );
//		p.rotateX( _textXRot );
		p.fill( _textColor.colorInt() );	// mode set blue
		Toxiclibs.instance(p).toxi.mesh( _meshText );
//		drawToxiFaces( _meshText, _textColor.color() );
		p.popMatrix();
	}
	
	protected void drawTextExplode() {
		_particleColor.update();
		p.pushMatrix();
		p.translate( 0, 0, _textZ );
		if( _mode == TEXT_SHOW && _curModeFrames == 75 ) _particleColor.setTargetInt( BLACK.colorInt() );
		if( ( _mode == TEXT_SHOW && _curModeFrames > 75 ) || _mode == LOGO_VERTEXES ) _particles.update();
		p.popMatrix();
	}
	
	protected void drawToxiFaces( WETriangleMesh mesh, TColor baseColor ) {
		// loop through and set vertices
		Triangle3D tri;
		Face face;
		
		// break up EQ by number of vertices
		int numVertices = mesh.faces.size();
		int eqStep = ( numVertices > 512f ) ? Math.round( (float) numVertices / 512f ) : Math.round( (float) 512f / numVertices );
		
		// loop through model's vertices
		for( int i = 0; i < mesh.faces.size(); i++ ) {
			float eq = 1;
			if( eqStep != 0 ) {
				eq = p.audioFreq(Math.round(i*eqStep));
				eq *= 15f;
			}
//			if(i == 10) P.println(eq);
			// adjust face color per audio EQ
			TColor faceColor = baseColor.copy(); 
			p.fill( faceColor.lighten( eq ).toARGB() );

			face = mesh.faces.get( i );
			tri = new Triangle3D( 
					new Vec3D( face.a.x, face.a.y, face.a.z ), 
					new Vec3D( face.b.x, face.b.y, face.b.z ), 
					new Vec3D( face.c.x, face.c.y, face.c.z )
				);
			Toxiclibs.instance(p).toxi.triangle( tri );
		}		
	}

	
	protected void deformWithElastic() {
		int numVertices = _mesh.getNumVertices();
		
		for( int i = 0; i < numVertices; i++ ) {
			_elasticVertices.get( i ).update();
			if( _mesh.getVertexForID( i ) != null ) {
				_meshDeform.getVertexForID( i ).x = _mesh.getVertexForID( i ).x * _elasticVertices.get( i ).value();
				_meshDeform.getVertexForID( i ).y = _mesh.getVertexForID( i ).y * _elasticVertices.get( i ).value();
				_meshDeform.getVertexForID( i ).z = _mesh.getVertexForID( i ).z * _elasticVertices.get( i ).value() * 0.9f;
			}
		}
	}

	protected void deformWithElasticAndAudio() {
		int numVertices = _mesh.getNumVertices();
		int eqStep = ( numVertices > 512f ) ? Math.round( (float) numVertices / 512f ) : Math.round( (float) 512f / numVertices );
		
		for( int i = 0; i < numVertices; i++ ) {
			float eq = 1;
			if( eqStep != 0 ) {
				eq = p.audioFreq(Math.round(i*eqStep));
				eq *= _audioLightener.value();
			}
			_elasticVertices.get( i ).update();
			if( _mesh.getVertexForID( i ) != null ) {
				_meshDeform.getVertexForID( i ).x = _mesh.getVertexForID( i ).x * _elasticVertices.get( i ).value();
				_meshDeform.getVertexForID( i ).y = _mesh.getVertexForID( i ).y * _elasticVertices.get( i ).value();
//				_meshDeform.getVertexForID( i ).z = _mesh.getVertexForID( i ).z * _elasticVertices.get( i ).val() * 0.9f + eq;
				_meshDeform.getVertexForID( i ).z = MathUtil.easeTo( _meshDeform.getVertexForID( i ).z, _mesh.getVertexForID( i ).z * _elasticVertices.get( i ).value() * 0.9f + eq, 2.2f );
			}
		}
	}

	protected void deformWithTrig2() {
		int numVertices = _mesh.getNumVertices();
		for( int i = 0; i < numVertices; i++ ) {
			if( _mesh.getVertexForID( i ) != null ) {
				_meshDeform.getVertexForID( i ).x = _mesh.getVertexForID( i ).x + 20*((float)Math.sin(p.frameCount*i/100f));
				_meshDeform.getVertexForID( i ).y = _mesh.getVertexForID( i ).y + 20*((float)Math.cos(p.frameCount*i/100f));
				_meshDeform.getVertexForID( i ).z = _mesh.getVertexForID( i ).z + 20*((float)Math.sin(p.frameCount*i/100f));
			}
		}
	}
	
	protected void deformWithAudio() {
		int numVertices = _mesh.getNumVertices();
		int eqStep = Math.round( (float) numVertices / 512f );
		for( int i = 0; i < numVertices; i++ ) {
			float eq = p.audioFreq(Math.round(i/eqStep) % 64);	// only use bottom 64 eq bands
			eq *= 2f;
			
			if( _mesh.getVertexForID( i ) != null ) {
				_meshDeform.getVertexForID( i ).x = _mesh.getVertexForID( i ).x;
				_meshDeform.getVertexForID( i ).y = _mesh.getVertexForID( i ).y;
				_meshDeform.getVertexForID( i ).z = 100 * eq;
			}
		}

	}

	public class MeshParticles {
		protected WETriangleMesh _meshData;
		protected int _numVertices = 0;
		protected ArrayList<Particle> _particles;
		
		public MeshParticles( WETriangleMesh mesh ) {
			_meshData = mesh;
			_numVertices = _meshData.getNumVertices();
			_particles = new ArrayList<Particle>(); 
			
			
			for( int i = 0; i < _numVertices; i++ ) {
				Vec3D vertexPoint = new Vec3D( _meshData.getVertexForID( i ).x, _meshData.getVertexForID( i ).y, _meshData.getVertexForID( i ).z );
				_particles.add( new Particle( vertexPoint ) );
			}
		}
		
		public void update() {
			p.fill( _particleColor.colorInt() );
			for( int i = 0; i < _numVertices; i++ ) {
				_particles.get( i ).update();
			}
			
		}
	}
	
	public class Particle {
		protected float _speedX;
		protected float _speedY;
		protected float _speedYGravity = 2f;
		protected float _speedZ;
		protected float _decel = 0.99f;
		protected float _size = 4f;
		protected float _shrink = 0.1f;
		protected Vec3D _vertexPoint;
		
		public Particle( Vec3D vertex ) {
			_vertexPoint = vertex;
			float dist = MathUtil.pythagDistance( Math.abs( _vertexPoint.x() ), Math.abs( _vertexPoint.y() ) ) / 5f;
			_speedX = _vertexPoint.x() * 1f/dist*0.5f*MathUtil.randRangeDecimal( 1.1f, 1.3f );
			_speedY = _vertexPoint.y() * 1f/dist*0.5f*MathUtil.randRangeDecimal( 1.1f, 1.3f );
			_speedZ = _vertexPoint.z() * 1f/dist*0.5f*MathUtil.randRangeDecimal( 1.1f, 1.3f );
//			_speedX = (1/_vertexPoint.x() )*60f;//*MathUtil.randRangeDecimel( 1.1f, 1.3f );
//			_speedY = (1/_vertexPoint.y() )*30f;//*MathUtil.randRangeDecimel( 1.1f, 1.3f );
//			_speedZ = 1;//(1/_vertexPoint.z() )*30f;//*MathUtil.randRangeDecimel( 1.1f, 1.3f );

//			if( _vertexPoint.x() < 0 ) _speedX *= -1;
//			if( _vertexPoint.y() < 0 ) _speedY *= -1;
//			if( _vertexPoint.z() < 0 ) _speedZ *= -1;
		}
		
		public void update() {
			p.pushMatrix();
			p.translate( _vertexPoint.x(), _vertexPoint.y(), _vertexPoint.z() );
			p.box( _size );
			p.popMatrix();
			
			// push particles out
			_vertexPoint.x = _vertexPoint.x() + _speedX;
			_vertexPoint.y = _vertexPoint.y() + _speedY;
			_vertexPoint.z = _vertexPoint.z() + _speedZ;
			
			_speedX *= _decel;
			_speedY *= _decel;
//			_speedY += _speedYGravity;
			_speedZ *= 0.99f;
			
			_size -= _shrink;
			if( _size < 0 ) _size = 0;
		}

	}

}
