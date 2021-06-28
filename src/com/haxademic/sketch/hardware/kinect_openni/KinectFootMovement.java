
package com.haxademic.sketch.hardware.kinect_openni;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.SkeletonsTrackerKinectV1;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;

import SimpleOpenNI.SimpleOpenNI;
import ddf.minim.AudioPlayer;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Vec3D;

public class KinectFootMovement
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SkeletonsTrackerKinectV1 _skeletonTracker;
	protected PGraphics _texture;
	protected AudioPlayer _sound;
	protected PImage _ballImage;
	protected Ball _ball;
	protected PImage _goal;
	protected PImage _goal2;
	protected boolean _noKick;
	protected float _goalScale = 6f;
	protected String _backgroundColor;
	protected int _backgroundTime;
	protected PImage _blue;
	protected float _blueScale = 5f;
	protected PImage _ground;
	protected float _groundScale = 5f;
		
	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.KinectV1);
		
		// do something
		if(Config.getBoolean("kinect_active", true) == true) _skeletonTracker = new SkeletonsTrackerKinectV1();
		_texture = P.p.createGraphics( p.width, p.height, P.P3D );
//		_sound = _minim.loadFile( "audio/bodymovements/goal.wav", 512 );
		_ballImage = p.loadImage("images/foot-movement/ball.png");
		_ball = new Ball();
		_goal = p.loadImage("images/foot-movement/goal.png", "png");
		_goal2 = p.createImage(_goal.width, _goal.height, ARGB);
		_goal2.set(0, 0, _goal);
		_blue = p.loadImage("images/foot-movement/blue.png");
		_ground = p.loadImage("images/foot-movement/ground.png");
		
	}
	
	protected void config() {
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.WIDTH, "640" );
		Config.setProperty( AppSettings.HEIGHT, "480" );
	}
	
	protected void drawApp() {
		PG.resetGlobalProps( p );
		
		p.background(0);
		
		p.pushMatrix();
		
		p.noStroke();
		
		
		PG.setColorForPImage(p);
		PG.setDrawCenter(p);
		PG.setBasicLights( p );


		drawGoal();
		drawGround();
		drawSun();
		
		p.popMatrix();
	}
	
	protected void drawGoal() {
		PG.setColorForPImage(p);
		p.pushMatrix();
		p.translate(p.width/2, p.height/2, 0);
		p.image(_goal2, 0, 0, _goal.width, _goal.height);
		p.popMatrix();
	}
	
	protected void drawGround() {
		p.pushMatrix();
		p.fill(11, 149, 14);
		p.fill(100);
		p.translate(p.width/2f, (p.height/2f)+100f, 0);
		p.rotateX(P.PI/2f);
		p.rect(0, 0, 4800, 6000);
		p.popMatrix();
	}
	
	protected void drawSun() {
		p.pushMatrix();
		p.fill(255, 255, 0, 255);
		PG.setDrawCenter(p);
		p.ellipse(0, 0, 200, 200);
		p.popMatrix();
	}
	
	protected void drawWebCam( float rotations ) {
		IDepthCamera depthCamera = DepthCamera.instance().camera;

		// draw cam
		PG.setColorForPImage(p);
		// control brightness with 2nd variable
		PG.setPImageAlpha(p, 1f);
		// normal camera video output
		PImage drawCamImg = depthCamera.getRgbImage();

		
		for( int i=0; i < rotations; i++ ) {
			p.rotate((float)P.TWO_PI/rotations * (float)i);
			p.image( drawCamImg, 0, 0 );
		}
	}
	
	protected void findSkeletonJoints() {
		int[] users = _skeletonTracker.getUserIDs();
		for(int i=0; i < users.length; i++) {
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_HEAD),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_NECK)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_SHOULDER),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_SHOULDER)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_HAND),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_ELBOW)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_ELBOW),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_SHOULDER)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_SHOULDER),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_TORSO)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_HAND),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_ELBOW)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_ELBOW),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_SHOULDER)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_SHOULDER),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_TORSO)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_TORSO),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_HIP)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_HIP),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_KNEE)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_KNEE),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_FOOT)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_TORSO),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_HIP)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_KNEE)
			);
			drawSkeletonBoxes(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_KNEE),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_FOOT)
			);
		}
	}
	
	public void drawSkeletonBoxes( Vec3D joint1, Vec3D joint2 ) {
		
		if( joint1 == null || joint2 == null ) return;
				
		p.pushMatrix();
		p.fill(255, 255, 0, 255);
		p.scale(0.5f);
		p.translate(p.width/2, (p.height/2)+700f, 20f);
		beginShape(TRIANGLES);		
		vertex( joint1.x, joint1.y, joint1.z );
		vertex( joint1.x + 20, joint1.y, joint1.z );
		vertex( joint2.x + 20, joint2.y, joint2.z );
		vertex( joint2.x, joint2.y, joint2.z );
		
		p.endShape();
		p.popMatrix();
		
	}
	
	protected void footMovement() {
		// loop through attractors and store the closest & our distance for coloring
		int[] users = _skeletonTracker.getUserIDs();
		for(int i=0; i < users.length; i++) {
			feetHips(
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_FOOT),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_FOOT),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
				_skeletonTracker.getBodyPartVec3d(users[i], SimpleOpenNI.SKEL_LEFT_HIP),
				_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_FOOT),
				_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_FOOT)
			);
		}
		
		//updates ball movement 
		_ball.update();
		
	}
	
	public void feetHips( Vec3D rFoot, Vec3D lFoot, Vec3D rHip, Vec3D lHip, Vec3D rFootPos, Vec3D lFootPos ) {
				
		if( rFoot == null || lFoot == null || rHip == null || lHip == null || rFootPos == null || lFootPos == null ) return;
		//P.println("Distance from right hip to right foot" + (rHip.y - rFoot.y));
		//P.println("Distance from left hip to left foot" + (lHip.y - lFoot.y));
		
		if( rFoot.z > (rHip.z + 200) || lFoot.z > (lHip.z + 200) ) {
			if( (rFoot.z > lFoot.z) && (_noKick != true) ) {
				_ball.kick(rFootPos.x, rFootPos.y, (rHip.x)-(rFoot.x));
				_noKick = true;
			} else if ( (rFoot.z < lFoot.z) && (_noKick != true) ){
				_ball.kick(lFootPos.x, lFootPos.y, (lHip.x)-(lFoot.x));
				_noKick = true;
			} 
		} else {
			_noKick = false;
		}
	}
	
	
	public class Ball {
		
		protected final float STARTYSPEED = 30f;
		protected final float STARTZSPEED = 50f;
		
		protected PImage _ballImage;
		protected float z;
		protected float y;
		protected float x;
		protected float xSpeed;
		protected float ySpeed = STARTYSPEED;
		protected float zSpeed = STARTZSPEED;
		protected float rotationSpeed;
		protected float rotation = 0;
		protected float gravity = 2.0f;
		protected boolean active;
		
		public Ball() {
			//constructor stuff here
			_ballImage = p.loadImage("images/foot-movement/ball.png");
		}
		
		public void kick(float xFoot, float yFoot, float xHipFoot) {
			if(active == false) {
				active = true;
				x = xFoot;
				y = yFoot;
				xSpeed = xHipFoot;
			}
		}
		
		public void update() {
			if(active == true) {
				if(z > -1500) {
					p.pushMatrix();
					p.translate(x, y, z);
					PG.setDrawCenter(p);
					rotation += 0.2f;
					p.rotate(rotation);
					PG.setColorForPImage(p);
					p.image(_ballImage, 0, 0);
					z -= zSpeed;
					ySpeed -= gravity;
					y -= ySpeed;
					x -= xSpeed/4;
					p.popMatrix();
				} else {
					if( x < ((p.width/2) + (_goal.width/2*_goalScale)) && y < ((p.height/2) + (_goal.height/2*_goalScale)) && x > ((p.width/2) - (_goal.width/2*_goalScale)) && y > ((p.height/2) - (_goal.height/2*_goalScale)) ) {
						//goal scored
//						_sound.play(0);
						P.println("GOOOOOOOAL");
						_backgroundColor = "green";
					} else {
						//goal missed
						P.println("MISS");
						_backgroundColor = "red";
					}
					_backgroundTime = millis();
					reset();
				}
			}
		}
		
		public void reset() {
			active = false;
			z = 0;
			y = 0;
			x = 0;
			xSpeed = 0;
			ySpeed = STARTYSPEED;
			zSpeed = STARTZSPEED;
			rotationSpeed = 0;
			rotation = 0 ;
			
		}
		
	}
	
}
