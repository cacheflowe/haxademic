package com.haxademic.core.draw.mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import processing.core.PApplet;
import toxi.geom.mesh.WETriangleMesh;

/**
 * ObjPool is a convenient way to load a bunch of .obj files and have a toxiclibs WETriangleMesh of each, always ready to be copied or used for different purposes.
 * @author cacheflowe
 *
 */
public class MeshPool {
	
	protected PApplet p;
	protected HashMap<String, ObjItem> _models;
	
	public MeshPool( PApplet p ) {
		this.p = p;
		_models = new HashMap<String, ObjItem>();
	}
	
	public void addMesh( String id, WETriangleMesh mesh, float scale ) {
		_models.put( id, new ObjItem( mesh, scale ) );
	}

	public WETriangleMesh getMesh( String id ) {
		return _models.get( id )._mesh;
	}

	public int size() {
		return _models.size();
	}

	public ArrayList<String> getIds() {
		ArrayList<String> keyList = new ArrayList<String>();
		Iterator<String> iter = _models.keySet().iterator();
	    while (iter.hasNext()) {
	    	keyList.add( iter.next().toString() );
	    }
		return keyList;
	}

	/**
	 * ObjItem is used to initialize a model with a base scale, since we might not always be able to normalize the model in Blender, etc.
	 * @author cacheflowe
	 */
	public class ObjItem {
		public float _scale;
		public WETriangleMesh _mesh;
		
		/**
		 * Initializes
		 * @param mesh
		 * @param scale
		 */
		public ObjItem( WETriangleMesh mesh, float scale ) {
			_mesh = mesh;
			_scale = scale;
			_mesh.scale( _scale );
		}
	}
}
