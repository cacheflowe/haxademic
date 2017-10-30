package com.haxademic.core.draw.toxi;

import java.util.ArrayList;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.geom.mesh.LaplacianSmooth;
import toxi.geom.mesh.TriangleMesh;
import toxi.geom.mesh.WETriangleMesh;

public class ThreeDeeUtil {

	public static void SmoothToxiMesh( PApplet p, WETriangleMesh mesh, int numSmoothings ) {
		for( int i = 0; i < numSmoothings; i++ ) {
			new LaplacianSmooth().filter( mesh, 1 );
		}
	}
	
	public static WETriangleMesh GetWETriangleMeshFromTriangleMesh( TriangleMesh mesh ) {
		WETriangleMesh weMesh = new WETriangleMesh();
		weMesh.addMesh(mesh);
		return weMesh;
	}
	
	/**
	 * Returns a GLModel, suitable to load into a fragment shader
	 * From: http://codeanticode.wordpress.com/2011/03/28/integrating-toxilibs-and-glgraphics/
	 * @param p
	 * @param mesh
	 * @return
	 */
//	public static GLModel GetGLModelFromToxiMesh( PApplet p, WETriangleMesh mesh ){
//		mesh.computeVertexNormals();
//		float[] verts = mesh.getMeshAsVertexArray();
//		int numV = verts.length / 4; // The vertices array from the mesh object has a spacing of 4.
//		float[] norms = mesh.getVertexNormalsAsArray();
//		
//		GLModel glmesh = new GLModel(p, numV, P.TRIANGLES, GLModel.STATIC);
//		glmesh.beginUpdateVertices();
//		for (int i = 0; i < numV; i++) glmesh.updateVertex(i, verts[4 * i], verts[4 * i + 1], verts[4 * i + 2]);
//		glmesh.endUpdateVertices(); 
//		
//		glmesh.initNormals();
//		glmesh.beginUpdateNormals();
//		for (int i = 0; i < numV; i++) glmesh.updateNormal(i, norms[4 * i], norms[4 * i + 1], norms[4 * i + 2]);
//		glmesh.endUpdateNormals();
//		  
//		return glmesh;
//	}
//	
//	public static void setGLProps( GLGraphics renderer ) {
//		renderer.gl.glEnable(GL.GL_LIGHTING);
//		renderer.gl.glDisable(GL.GL_COLOR_MATERIAL);
//		renderer.gl.glEnable(GL.GL_LIGHT0);
//		renderer.gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0.1f,0.1f,0.1f,1}, 0);
//		renderer.gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, new float[]{1,0,0,1}, 0);
//		renderer.gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] {-1000, 600, 2000, 0 }, 0);
//		renderer.gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, new float[] { 1, 1, 1, 1 }, 0);
//	}
	
	/**
	 * Move a mesh by a 3D vector
	 * @param meshes
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void addPositionToMeshArray( ArrayList<WETriangleMesh> meshes, float x, float y, float z ) {
		Vec3D center = null;
		for( int i=0; i < meshes.size(); i++ ) {
			// TODO: FIX THIS
			center = meshes.get( i ).computeCentroid();
			meshes.get( i ).center( new Vec3D( x - center.x, y - center.y, z - center.z ) );
		}
	}
	
	public static void addPositionToMesh( WETriangleMesh mesh, float x, float y, float z ) {
		Vec3D center = mesh.computeCentroid();
		mesh.center( new Vec3D( x - center.x, y - center.y, z - center.z ) );
	}

}
