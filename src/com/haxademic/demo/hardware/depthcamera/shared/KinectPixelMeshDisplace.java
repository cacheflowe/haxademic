package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pgraphics.archive.PixelFilter;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;

import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.WETriangleMesh;

public class KinectPixelMeshDisplace 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public static final float PIXEL_SIZE = 10;
	public static final int KINECT_TOP = 0;
	public static final int KINECT_BOTTOM = 480;
	public static final int KINECT_CLOSE = 500;
	public static final int KINECT_FAR = 2000;

	protected PixelFilter _pixelFilter;
	protected WETriangleMesh _mesh;

	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.Realsense);
		_pixelFilter = new PixelFilter(DepthCameraSize.WIDTH, DepthCameraSize.WIDTH, (int)PIXEL_SIZE);
		setupMeshForTexture( (int)(DepthCameraSize.WIDTH / PIXEL_SIZE), (int)(DepthCameraSize.WIDTH / PIXEL_SIZE), 640, 480 );
	}
	
	public void setupMeshForTexture( int cols, int rows, int width, int height ) {
		_mesh = new WETriangleMesh();
		int xInc = P.floor(width/cols);
		int yInc = P.floor(height/rows);
		for ( int i = 0; i < width - 1; i+=xInc) {
			for ( int j = 0; j < height - 1; j+=yInc) {
				// position mesh out from center
				float x = i - cols/2;
				float y = j - rows/2;
				// create 2 faces and their UV texture coordinates
				_mesh.addFace( new Vec3D( x, y, 0 ), new Vec3D( x+xInc, y, 0 ), new Vec3D( x+xInc, y+yInc, 0 ), new Vec2D( i, j ), new Vec2D( i+1, j ), new Vec2D( i+1, j+1 ) );
				_mesh.addFace( new Vec3D( x, y, 0 ), new Vec3D( x+xInc, y+yInc, 0 ), new Vec3D( x, y+yInc, 0 ), new Vec2D( i, j ), new Vec2D( i+1, j+1 ), new Vec2D( i, j+1 )  );
			}
		}
	}

	protected void drawApp() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		PG.resetGlobalProps( p );
		p.shininess(1000f); 
		p.lights();
		p.background(0);

		// draw filtered web cam
		PG.setDrawCenter(p);
		PG.setColorForPImage(p);
		
		// iterate over all mesh triangles
		// and deform/draw their vertices
		p.beginShape(P.TRIANGLES);
		PG.setColorForPImage(p);
		p.noStroke();
		p.texture(_pixelFilter.updateWithPImage(depthCamera.getRgbImage()));
		float pixelDepth;
		for( Face f : _mesh.getFaces() ) {
			// deform z-position
			float normalizedDepth = 0;
			pixelDepth = depthCamera.getDepthAt( (int)f.uvA.x, (int)f.uvA.y );
			if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
				normalizedDepth = 1 - (pixelDepth - KINECT_CLOSE) / (KINECT_FAR - KINECT_CLOSE);
			}
			f.a.z = normalizedDepth * 400f;

			if(normalizedDepth > 0) {
				// draw vertices
				p.vertex(f.a.x,f.a.y,f.a.z,f.uvA.x,f.uvA.y);
				p.vertex(f.b.x,f.b.y,f.b.z,f.uvB.x,f.uvB.y);
				p.vertex(f.c.x,f.c.y,f.c.z,f.uvC.x,f.uvC.y);
			}
	   	}
		p.endShape();
	}
}
