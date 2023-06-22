package com.haxademic.demo.draw.shapes.polygons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.polygons.CollisionUtil;
import com.haxademic.core.draw.shapes.polygons.Edge;
import com.haxademic.core.draw.shapes.polygons.Polygon;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.UI;

import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;

public class Demo_Polygon 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/*
		* Polygon map texture
				* Map to constrain areas for new polygon generation?? 
				* Map to destroy polygons? For user interaction??? 
					* Generative patterns a la Zoom lights for this
				* Add Kinect map
					* Does KInect map power a separate mesh system with a shadow on top of the main one??
		* Animated destroy/collapse method
			* Make lerp speed adjustable/randomized
			* What about Penner curves??
		* Overall draw styles & colors
			* Wiggly lines should use all of Debbie's textures, and be more or less wiggly at times
				* Adjustable thickness
			* Lines, background and triangle backgrounds should use a global palette
				* No flashing! All colors should lerp
		* Inner-polygon draw styles
			* Turn debug mode into a draw style interface
			* Mesh traversal for draw styles and deletion and??
				* Add random neighbor getter 
				* Traverse mesh and redistribute color palettes
				* Iterative distribution of styles
		* Add UI sliders to find nice configuration presets
		* Ability to remove polygons & neighbors
			* If a polygon is too small, pick a vertex and destroy all connected neighbors??
				* Or collapse it somehow? 
			* Polygon pool needed for proper recycling
		* Mesh enhancements
			* Sometimes subdivide a poly
			* Sometimes combine two polys - how to make sure it's not concave?
			* Sometimes start with multiple seed locations
			* Sometimes triangles aren't closing that really should
			* Layout system
			* Rect polygons subdivision layout instead of polygon mesh
				* Can we transition to this from an odd triangle?
				* Sometimes only let the newest triangle make new neighbors?? 
		* Post-processing:
			* Fake light shader??
			* Vertex shader if everything's moved to PShape? 
		* Animate mesh:
			* Mesh displacement based on vertices/ position, so it works across shared vertices
			* Can entire mesh rotate around a point?
		* Intentional imperfections
		* Music
			* Try playing sounds when triangles are built/destroyed
			* ... or when modes change. Cuoud modes/parameters changing be the musical driver?? 
	*/
	
	// mesh / growth
	protected int RESET_FRAME_INTERVAL = 600;
	protected ArrayList<Polygon> polygons;
	protected float MAX_POLY_AREA = 20000;
	protected float MAX_TOTAL_AREA = 1000000;
	protected float baseShapeSize = 100;
	protected float SNAP_RADIUS = baseShapeSize / 2f;
	protected float tooFarThresh = baseShapeSize * 3.5f;
	protected int totalArea = 0;
	protected int NEW_NEIGHBOR_ATTEMPTS = 4;
	protected Polygon tempTriangle;
	
	// search for available connections
	protected ArrayList<PVector> availableVertices = new ArrayList<PVector>();
	protected PVector vClose1 = new PVector();
	protected PVector vClose2 = new PVector();
	protected PVector vCompare;
	protected int[] closeIndexes = new int[3];
	
	// styles
	// color
	protected int[] palette = new int[] {
		0xff9fef9c,
		0xffec008c,
		0xff46dcb3,
		0xffc5a1eb,
		0xff45c5dd,
		0xff808285,
		0xffffe24f,
		0xff07aa99,
		0xff87d3dc,
		0xfffa71b3,
		0xff64428d,
		0xfff8b923,
		0xff050003,
	};
	protected int[] curPaletteIndexes = new int[4];
	
	// animation
	protected int curEdgeCopyStyle = Polygon.EDGE_COPY_1;
	protected boolean clearsBg = false;
	
	// movement
	protected PVector speed = new PVector();
	

	
	// debug
	protected boolean POLYGON_DEBUG = false;
	protected PVector mouseVec = new PVector();
	protected StringBufferLog log = new StringBufferLog(30);
	
	// post processing
	protected String AMBIENT = "AMBIENT";
	protected String GRAD_AMP = "GRAD_AMP";
	protected String GRAD_BLUR = "GRAD_BLUR";
	protected String SPEC_AMP = "SPEC_AMP";
	protected String DIFF_DARK = "DIFF_DARK";
	protected String FILTER_ACTIVE = "FILTER_ACTIVE";

	PShaderHotSwap polygonShader;
	protected SimplexNoiseTexture displaceTexture;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.WIDTH, 1920 );
		Config.setProperty( AppSettings.HEIGHT, 1080 );
	}

	protected void firstFrame() {
		tempTriangle = Polygon.buildShape(0, 0, 3, 100);
		newSeedPolygon();
		setupPostProcessing();
		
		polygonShader = new PShaderHotSwap(
				FileUtil.getPath("haxademic/shaders/vertex/mesh-2d-deform-vert.glsl"),
				FileUtil.getPath("haxademic/shaders/vertex/mesh-2d-deform-frag.glsl") 
			);
		displaceTexture = new SimplexNoiseTexture(256, 256);

	}
	
	///////////////////////////////////
	// POST-PROCESSING
	///////////////////////////////////
	
	protected void setupPostProcessing() {
		UI.addSlider(AMBIENT, 2f, 0.3f, 6f, 0.01f, false);
		UI.addSlider(GRAD_AMP, 0.66f, 0.1f, 6f, 0.01f, false);
		UI.addSlider(GRAD_BLUR, 1f, 0.1f, 6f, 0.01f, false);
		UI.addSlider(SPEC_AMP, 2.25f, 0.1f, 6f, 0.01f, false);
		UI.addSlider(DIFF_DARK, 0.85f, 0.1f, 2f, 0.01f, false);
		UI.addSlider(FILTER_ACTIVE, 1f, 0f, 1f, 1f, false);
	}
	
	protected void postProcess() {
		FakeLightingFilter.instance().setAmbient(UI.value(AMBIENT));
		FakeLightingFilter.instance().setGradAmp(UI.value(GRAD_AMP));
		FakeLightingFilter.instance().setGradBlur(UI.value(GRAD_BLUR));
		FakeLightingFilter.instance().setSpecAmp(UI.value(SPEC_AMP));
		FakeLightingFilter.instance().setDiffDark(UI.value(DIFF_DARK));
		FakeLightingFilter.instance().setMap(pg);
		
		if(UI.value(FILTER_ACTIVE) > 0.5f) {
			FakeLightingFilter.instance().applyTo(pg);
		}
	}
	
	///////////////////////////////////
	// DRAW
	///////////////////////////////////
	
	protected void newSeedPolygon() {
		// new colors & speed
		newPalette();
		speed.set(MathUtil.randRangeDecimal(-2f, 2f), MathUtil.randRangeDecimal(-2f, 2f));

		// new size params
		MAX_TOTAL_AREA = 4600000;
		baseShapeSize = 150 + 90 * P.sin(p.frameCount);
		MAX_POLY_AREA = P.pow(baseShapeSize, 2.2f);
		SNAP_RADIUS = baseShapeSize / 2f;
		tooFarThresh = baseShapeSize * 3.5f;
		DebugView.setValue("baseShapeSize", baseShapeSize);
		
		curEdgeCopyStyle = Polygon.randomEdgeCopyStyle();
		RESET_FRAME_INTERVAL = 800;
		clearsBg = MathUtil.randBoolean();
		
		// new seed param
		polygons = new ArrayList<Polygon>();
		Polygon firstPoly = Polygon.buildShape(p.width * p.random(0.1f, 0.9f), p.height * p.random(0.1f, 0.9f), 3, baseShapeSize);
		addNewPolygon(firstPoly);
//		Polygon poly2 = Polygon.buildShape(p.width * p.random(0.1f, 0.9f), p.height * p.random(0.1f, 0.9f), 3, baseShapeSize);
//		polygons.add(poly2);
	}
	
	protected void addNewPolygon(Polygon poly) {
		polygons.add(poly);
		poly.bgColor(nextColor());
		poly.initAnim(curEdgeCopyStyle);
	}
	
	protected void newPalette() {
		for (int i = 0; i < curPaletteIndexes.length; i++) {
			curPaletteIndexes[i] = MathUtil.randRange(0, palette.length - 1);
		}
	}
	
	protected void drawApp() {
		background(0);
		
//		camera test
//		p.translate(0, 0, -1500 * Mouse.xNorm);
//		p.rotateX(Mouse.yNorm * 3f);
		displaceTexture.offsetX(p.frameCount/100f);
		displaceTexture.update();
		// draw & generate shapes
		pg.beginDraw();
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		polygonShader.shader().set("time", p.frameCount);
		polygonShader.shader().set("displacementMap", displaceTexture.texture());
		polygonShader.shader().set("displaceAmp", baseShapeSize);
		polygonShader.shader().set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);
		polygonShader.update();
		// apply polygons shader
		pg.shader(polygonShader.shader());  

		BrightnessStepFilter.instance().setBrightnessStep(-1f/255f);
		BrightnessStepFilter.instance().applyTo(pg);
		if(clearsBg) pg.background(255);
		if(p.frameCount % RESET_FRAME_INTERVAL == 0) newSeedPolygon();
		movePolygons();
		drawPolygons();
		createNeighbors();
		closeNeighbors();
		removePolygons();
		pg.resetShader();

		pg.endDraw();
		// postProcess();
	
		// draw main buffer to screen
		ImageUtil.cropFillCopyImage(pg, p.g, false);
		
		// draw debug log
		log.printToScreen(p.g, 20, 20);
		DebugView.setValue("Polygons", polygons.size());
	}
	
	protected void drawPolygons() {
		mouseVec.set(p.mouseX, mouseY);
		totalArea = 0;
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).collided(CollisionUtil.polygonContainsPoint(polygons.get(i), mouseVec));
			polygons.get(i).draw(pg, POLYGON_DEBUG);
			totalArea += polygons.get(i).area();
		}
		DebugView.setValue("Total Area", totalArea);
	}
	
	protected void createNeighbors() {
//		if(p.frameCount % 10 > 1) return;
		if(totalArea > MAX_TOTAL_AREA) return;
		int startTime = p.millis();
		for(int i=0; i < NEW_NEIGHBOR_ATTEMPTS; i++) addNewNeighbor();
		DebugView.setValue("addNewNeighbor()", (p.millis() - startTime)+"ms");
	}
	
	protected void movePolygons() {
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).translate(speed);
		}
	}
	
	///////////////////////////////////
	// Generate new polygons from available edges
	///////////////////////////////////
	
	protected Polygon randomPolygon() {
		return polygons.get(MathUtil.randRange(0, polygons.size() - 1));
	}
	
	protected void addNewNeighbor() {
		// find a polygon that needs neighbors
		Polygon randPoly = randomPolygon();
		int attempts = 0;
		while(randPoly.needsNeighbors() == false && attempts < 100) {
			randPoly = randomPolygon();
			attempts++;
		}
//		log.update("attempts: " + attempts);
		
		// try to add a neighbor
		if(randPoly.needsNeighbors()) {
			Polygon newNeighbor = createNeighborTriangle(randPoly);
			if(newNeighbor != null) {
				addNewPolygon(newNeighbor);
				ensureNeighborsConnect();
				log.update("createdNeighbor!");
			}
		}
	}
	
	public Polygon createNeighborTriangle(Polygon parentPoly) {
		// get available edge
		// and find a reasonable new vertex for a neighbor 
		Edge edge = parentPoly.availableNeighborEdge();
		PVector newNeighborVertex = parentPoly.newNeighbor3rdVertex(edge, MathUtil.randRangeDecimal(0.5f, 1.8f), 0.25f, 0.75f);
		
		// new triangle off the Edge, but lerp the shared edge away a tiny bit to prevent overlap check
		tempTriangle.setVertex(0, edge.v1());
		tempTriangle.setVertex(1, edge.v2());
		tempTriangle.setVertex(2, newNeighborVertex);
		tempTriangle.shrink(0.001f);
		
		// if offscreen, bail
		if(polygonOffscreen(tempTriangle)) return null;
		
		// check to see if we're overlapping with another polygon
		Polygon overlappedPoly = null;
		for (int i = 0; i < polygons.size(); i++) {
			if(overlappedPoly == null) {
				if(CollisionUtil.polygonsIntersect(polygons.get(i), tempTriangle)) {
					overlappedPoly = polygons.get(i);
//					log.update("overlappedPoly");
				}
			}
		}
		
		// if we're overlapping another poly, try to move the new vertex to the closest vertex of the overlapped triangle, then see if the two triangles share an edge
		if(overlappedPoly != null) {
			PVector closestOverlappedVert = overlappedPoly.closestVertexToVertex(newNeighborVertex);
			newNeighborVertex.set(closestOverlappedVert);
//			log.update("OVERLAP SNAP!");
		} else {
			// if we're not overlapped, but close to another vertex, let's try to snap
			boolean snapped = false;
			for (int i = 0; i < polygons.size(); i++) {
				for (int j = 0; j < polygons.get(i).vertices().size(); j++) {
					if(snapped == false && polygons.get(i) != parentPoly) {		// don't snap to parent, or we get overlaps that don't get cleaned up below
						PVector vertex = polygons.get(i).vertices().get(j);
						if(newNeighborVertex.dist(vertex) < SNAP_RADIUS) {
							newNeighborVertex.set(vertex);
							overlappedPoly = polygons.get(i);	// ensures that the neighbors are connected below
							snapped = true;
							log.update("SNAP!");
						}
					}
				}
			}
		}
		
//		// TODO: Do we need to check for overlap again, based on "SNAP" above??
//		if(overlappedPoly != null) {
//			
//		}
		
		// new triangle to attach
		Polygon newNeighbor = new Polygon(new float[] {
				edge.v1().x, edge.v1().y, edge.v1().z,
				edge.v2().x, edge.v2().y, edge.v2().z,
				newNeighborVertex.x, newNeighborVertex.y, newNeighborVertex.z
		});
		
		// if not overlapping another, add to collection
		if(overlappedPoly == null && newNeighbor.area() < MAX_POLY_AREA) { // && newNeighborArea > 800) {
			// tell polys about their shared edges
			parentPoly.findNeighbor(newNeighbor);
			newNeighbor.findNeighbor(parentPoly);
			return newNeighbor;
		} else {
			// TODO: put this in an object pool for recycling
			return null;
		}
	}
	
	protected void ensureNeighborsConnect() {
		for (int i = 0; i < polygons.size(); i++) {
			for (int j = 0; j < polygons.size(); j++) {
				Polygon poly1 = polygons.get(i);
				Polygon poly2 = polygons.get(j);
				if(poly1 != poly2) {
					if(poly1.findNeighbor(poly2)) {	// if we find a neighbor, make it mutual
						poly2.findNeighbor(poly1);
					}
				}
			}
		}
	}
	
	protected int nextColor() {
		return palette[curPaletteIndexes[polygons.size() % curPaletteIndexes.length]];
	}
	
	///////////////////////////////////
	// Attempt to close neighbors w/triangles
	///////////////////////////////////
	
	public Comparator<PVector> distanceComparator = new Comparator<PVector>() {         
		public int compare(PVector v1, PVector v2) {
			float dist1 = vCompare.dist(v1);
			float dist2 = vCompare.dist(v2);
			return (dist1 < dist2 ? -1 :                     
				(dist1 == dist2 ? 0 : 1));           
		}     
	}; 
	
	protected void fillArrayWithUniqueIndexes(int[] arr, int maxIndex) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = MathUtil.randRange(0, maxIndex);
			while(valueExistsInArr(arr, arr[i], i)) arr[i] = MathUtil.randRange(0, maxIndex);
		}
	}
	
	protected boolean valueExistsInArr(int[] arr, int val, int curIndex) {
		for (int i = 0; i < curIndex; i++) {
			if(arr[i] == val) return true;
		}
		return false;
	}
	
	protected void closeNeighbors() {
		int startTime = p.millis();

		// create array of vertices that can be connected with another
		availableVertices.clear();
		for (int i = 0; i < polygons.size(); i++) {
			Polygon poly = polygons.get(i);
			for (int j = 0; j < poly.edges().size(); j++) {
				Edge edge = poly.edges().get(j);
				if(poly.edgeHasNeighbor(edge) == false) {
					if(availableVertices.contains(edge.v1()) == false) availableVertices.add(edge.v1());
					if(availableVertices.contains(edge.v2()) == false) availableVertices.add(edge.v2());
				}
			}
		}
		DebugView.setValue("availableVertices", availableVertices.size());
		
		// draw available vertices
		p.stroke(255, 255, 0);
		p.noFill();
		for (int i = 0; i < availableVertices.size(); i++) {
			p.circle(availableVertices.get(i).x, availableVertices.get(i).y, 20);
		}
		p.noStroke();
		
		// loop through all available vertices,
		// sort other available vertices based on distance from current in loop,
		if(availableVertices.size() >= 3) {
			for (int i = 0; i < availableVertices.size(); i++) {
				// store current vertex and sort other availables by distance using comparator function
				vCompare = availableVertices.get(i);
				Collections.sort(availableVertices, distanceComparator);
				
				// try random vertices instead of sorting every frame and trying the same set - this might succeed more often
				// only use the 20 closest to the current in the loop through all available
				for (int attempt = 0; attempt < 10; attempt++) {					
					fillArrayWithUniqueIndexes(closeIndexes, P.min(20, availableVertices.size()-1));
					attemptCloseTriangle(
						availableVertices.get(closeIndexes[0]), 
						availableVertices.get(closeIndexes[1]),
						availableVertices.get(closeIndexes[2])
					);
				}
			}
		}
		
		// debug time taken
		DebugView.setValue("closeNeighbors()", (p.millis() - startTime)+"ms");
	}

	protected boolean attemptCloseTriangle(PVector v1, PVector v2, PVector v3) {
		// are vertices too far from each other to close a triangle?
		if(v1.dist(v2) > tooFarThresh || v1.dist(v3) > tooFarThresh || v2.dist(v3) > tooFarThresh) {
			return false;
		}
		
		// build triangle to check for collisions
		tempTriangle.setVertex(0, v1);
		tempTriangle.setVertex(1, v2);
		tempTriangle.setVertex(2, v3);
		tempTriangle.shrink(0.001f);
		
		// bail if the triangle is too big
//		float newTriArea = CollisionUtil.polygonArea(tempTriangle);
		if(tempTriangle.area() > MAX_POLY_AREA) {
			log.update("TRIANGLE TOO BIG");
			return false;
		}

		// debug draw attempted/potential connections
		p.strokeWeight(2);
		p.stroke(255,0,0);
		p.line(v1.x + 2, v1.y + 2, v2.x + 2, v2.y + 2);
		p.line(v1.x + 2, v1.y + 2, v3.x + 2, v3.y + 2);
		p.line(v3.x + 2, v3.y + 2, v2.x + 2, v2.y + 2);
		p.strokeWeight(1);

		// check to see if we're overlapping with another polygon
		Polygon overlappedPoly = null;
		for (int i = 0; i < polygons.size(); i++) {
			if(overlappedPoly == null) {
				if(CollisionUtil.polygonsIntersect(polygons.get(i), tempTriangle)) {
					overlappedPoly = polygons.get(i);
				}
			}
		}
		
		// if we've passed all of the tests, add a new triangle and return the success result
		if(overlappedPoly == null) {
			Polygon newNeighbor = new Polygon(new float[] {
					v1.x, v1.y, v1.z,
					v2.x, v2.y, v2.z,
					v3.x, v3.y, v3.z
			});
			addNewPolygon(newNeighbor);
			log.update("CLOSED TRIANGLE!!!!!");
			
			// find any matching edges and add neighbor Polygons.
			// This is super important to stop edges without neighbors from being in `availableVertices`
			ensureNeighborsConnect();
			return true;
		} else {
			return false;
		}

	}
	
	///////////////////////////////////
	// Polygon removal
	///////////////////////////////////
	
	protected boolean polygonOffscreen(Polygon poly) {
		return (poly.xMax() < 0 ||
				poly.xMin() > p.width ||
				poly.yMax() < 0 ||
				poly.yMin() > p.height);
	}
	
	protected void removePolygons() {
		Polygon foundPolyOffscreen = null;
		for (int i = 0; i < polygons.size(); i++) {
			boolean offScreen = polygonOffscreen(polygons.get(i));
			if(foundPolyOffscreen == null && offScreen) {
				foundPolyOffscreen = polygons.get(i);
			}
			if(offScreen) polygons.get(i).bgColor(0xffff0000);
		}
		// remove up to one offscreen poly per frame
		if(foundPolyOffscreen != null) removePolygon(foundPolyOffscreen);
	}
	
	protected void removePolygon(Polygon poly) {
		// detach from neighbors
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).removeNeighbor(poly);
		}
		// remove from array. TODO: recycle!
		polygons.remove(poly);
		log.update("Removed neighbor!");
	}
	
	///////////////////////////////////
	// user input
	///////////////////////////////////
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
//			for (int i = 0; i < 10; i++) 
			addNewNeighbor();
		}
		if(p.key == 'r') {
			newSeedPolygon();
		}
	}

	public void mousePressed() {
		super.mousePressed();
		addNewNeighbor();
	}
}
