package com.haxademic.core.draw.particle;

import java.util.ArrayList;
import java.util.Iterator;

import com.haxademic.core.app.P;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PVector;

public class ForceDirectedLayout {

	// original code from: https://www.openprocessing.org/sketch/177#

	protected int layoutX;
	protected int layoutY;
	protected int layoutW;
	protected int layoutH;
	protected ArrayList<ForceNode> nodes;
	protected ArrayList<ForceNode> nodesRecycled;
	protected int addFrameDelay = 2;
	protected float k;
	protected float kFactor = 0.8f;
	protected float frFactor = 0.8f;
	protected int curNodeCount = 0;
	protected int targetNodeCount;
	protected float nodeMass;
	protected PVector delta = new PVector();
	
	public ForceDirectedLayout(int layoutX, int layoutY, int layoutW, int layoutH, int targetNodeCount, float nodeMass) {
		this.layoutX = layoutX;
		this.layoutY = layoutY;
		this.layoutW = layoutW;
		this.layoutH = layoutH;
		this.targetNodeCount = targetNodeCount;
		this.nodeMass = nodeMass;
		nodes = new ArrayList<ForceNode>();
		nodesRecycled = new ArrayList<ForceNode>();
		reset();
	}
	
	public ArrayList<ForceNode> nodes() {
		return nodes;
	}

	public void reset() {
		while(curNodeCount > 0) removeNode();
	}
	
	public void setTargetNodes(int numNodes) {
		targetNodeCount = numNodes;
	}
	
	public void addTargetNodes() {
		targetNodeCount++;
	}
	
	public void removeTargetNodes() {
		if(targetNodeCount > 0) targetNodeCount--;
	}
	
	public void setFrameDelay(int frameDelay) {
		addFrameDelay = frameDelay;
	}
	
	public void setKFactor(float val) {
		kFactor = val;
	}
	
	public void setFfFactor(float val) {
		frFactor = val;
	}
	
	protected void updateForceFactor() {
		if(targetNodeCount < 1) return;
		k = P.sqrt(layoutW * layoutH / targetNodeCount) * kFactor;
	}
	
	protected float fr(float m1, float m2, float z){
		return frFactor * P.pow(m1+m2+k, 2f) / P.pow(z, 2f);  // .5f  
	}
	
	protected float norm(PVector v){
		return P.sqrt(P.pow(v.x,2)+P.pow(v.y,2));
	}
	
	protected PVector versor(PVector v){
		return new PVector(v.x/norm(v),v.y/norm(v));
	}
	
	protected void removeNode() {
		ForceNode removedNode = nodes.get(curNodeCount - 1);
		removedNode.setInactive();
		curNodeCount--;
	}
	
	protected void addNode() {
		float newX = layoutX + layoutW * 0.25f + P.p.random(layoutW * 0.5f);
		float newY = layoutY + layoutH * 0.25f + P.p.random(layoutH * 0.5f);
		ForceNode newNode = (nodesRecycled.size() > 0) ? 
				nodesRecycled.remove(0) :
				new ForceNode(newX, newY, nodeMass);
		newNode.reset(newX, newY);
		nodes.add(newNode);
		curNodeCount++;
	}
	
	public void update() {		
		// add/remove nodes if we haven't reached the max
		if ((P.p.frameCount % addFrameDelay) == 0 && curNodeCount < targetNodeCount) { 
			addNode();
		}
		if ((P.p.frameCount % addFrameDelay) == 0 && curNodeCount > targetNodeCount) { 
			removeNode();
		}
		
		// remove nodes that have animated away
		if(curNodeCount < nodes.size() && curNodeCount > 0) {
			for (int i = curNodeCount - 1; i < nodes.size(); i++) {
				if(nodes.get(i).active() == false) {
					ForceNode removedNode = nodes.remove(i);
					nodesRecycled.add(removedNode);
				}
			}
		}
		
		// run physics
		updateForceFactor();
		for(Iterator<ForceNode> it=nodes.iterator();it.hasNext();){
			ForceNode node1 = it.next();
			for(Iterator<ForceNode> it2=nodes.iterator();it2.hasNext();){
				ForceNode node2 = it2.next();      
				if (node1!=node2){
					delta.set(node2.pos).sub(node1.pos);
					if(norm(delta) != 0){
						node2.displacement.add(versor(delta).mult( fr(node2.mass(), node1.mass(), norm(delta) ) ) );
					}
				}
			}
		}
		
		// constrain to specified area
		for(Iterator<ForceNode> it=nodes.iterator();it.hasNext();){
			ForceNode node = it.next();
			node.update();   
			node.constrain(layoutX, layoutX + layoutW, layoutY, layoutY + layoutH);
		}
		
		// remove any nodes that overlap? some get stuck in the corner
	}
	
	public class ForceNode {
		
		protected PVector pos;
		protected PVector posLerp;
		protected PVector displacement;
		protected LinearFloat mass;
		protected float massTarget;
		protected boolean active = false;

		public ForceNode(float x, float y, float massTarget) {
			pos = new PVector();
			posLerp = new PVector();
			displacement = new PVector();
			this.massTarget = massTarget;
			mass = new LinearFloat(0, 0.025f);
			reset(x, y);
		}
		
		public boolean active() {
			return active;
		}
		
		public void setInactive() {
			mass.setTarget(0);
		}
		
		public PVector position() {
			return posLerp;
		}
		
		public float mass() {
			float easedMass = Penner.easeInOutQuart(mass.value(), 0, 1, 1);
			return easedMass * massTarget;
		}
		
		public void reset(float x, float y) {
			mass.setCurrent(0);
			mass.setTarget(1);
			pos.set(x, y);
			posLerp.set(x, y);
			displacement.set(0, 0);
			active = true;
		}

		void update() {
			if(active == false) return; 
			mass.update();
			pos.add(displacement);
			posLerp.lerp(pos, 0.2f);
			displacement.set(0,0);
			if(mass.value() == 0 && mass.target() == 0) active = false;
		}

		void constrain(float x0, float x1,float y0, float y1) {
			pos.x = P.min(x1,P.max(x0,pos.x));
			pos.y = P.min(y1,P.max(y0,pos.y));
		}
	}
}

