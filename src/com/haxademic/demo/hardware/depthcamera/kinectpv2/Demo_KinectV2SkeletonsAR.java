package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.filters.pshader.compound.ColorAdjustmentFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.KinectV2SkeletonsAR;
import com.haxademic.core.hardware.depthcamera.KinectV2SkeletonsAR.IKinectV2SkeletonsARDelegate;
import com.haxademic.core.hardware.depthcamera.ar.ArElementCompound;
import com.haxademic.core.hardware.depthcamera.ar.ArElementCustom;
import com.haxademic.core.hardware.depthcamera.ar.ArElementImage;
import com.haxademic.core.hardware.depthcamera.ar.ArElementObj;
import com.haxademic.core.hardware.depthcamera.ar.ArElementPool;
import com.haxademic.core.hardware.depthcamera.ar.IArElement;
import com.haxademic.core.hardware.depthcamera.ar.IArElement.BodyTrackType;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import KinectPV2.KSkeleton;
import processing.core.PShape;

public class Demo_KinectV2SkeletonsAR 
extends PAppletHax
implements IKinectV2SkeletonsARDelegate {

	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	
	protected KinectV2SkeletonsAR kinectSkeletonsAR;
	protected ArElementPool arPool;
	
	protected void config() {
		Config.setAppSize(1920, 1080);
		Config.setPgSize(1920, 1080);
	}
	
	protected void firstFrame() {
		kinectSkeletonsAR = new KinectV2SkeletonsAR(pg, buildArPool(), true);
		kinectSkeletonsAR.setDelegate(this);
		ColorAdjustmentFilter.initUI();
	}
	
	protected ArElementPool buildArPool() {
		arPool = new ArElementPool();
			{
				ArElementImage arAsset = new ArElementImage(DemoAssets.smallTexture(), 0.15f, BodyTrackType.HEAD);
				arPool.addElement(arAsset);
			}
			{
				ArElementCompound arAsset = new ArElementCompound();
				arPool.addElement(arAsset);
			}
			{
				ArElementCustom arAsset = new ArElementCustom(0.1f);
				arAsset.setPositionOffset(0, -1, 0);
				arPool.addElement(arAsset);
			}
			{
				ArElementImage arAsset = new ArElementImage(DemoAssets.arrow(), 0.1f, BodyTrackType.HAND_POINT_RIGHT);
				arAsset.setRotationOffset(0, 0, -P.HALF_PI);
				arAsset.setPositionOffset(0.5f, 0, 0);
				arPool.addElement(arAsset);
			}
			{
				ArElementImage arAsset = new ArElementImage("haxademic/images/floaty-blob.anim/", 0.1f, BodyTrackType.HEAD, 24);
				arAsset.setPositionOffset(0, -1f, 0);
				arPool.addElement(arAsset);
			}
			{
				// add skull helmet
				PShape shape = P.p.loadShape(FileUtil.getPath(DemoAssets.objSkullRealisticPath));
				PShapeUtil.centerShape(shape);
				PShapeUtil.meshRotateOnAxis(shape, P.PI, P.Z);
				// PShapeUtil.meshRotateOnAxis(shape, P.PI, P.Y);
				// PShapeUtil.meshFlipOnAxis(shape, P.Y);
				// float modelW = PShapeUtil.getWidth(shape);
				// float modelH = PShapeUtil.getHeight(shape);
				// PShapeUtil.offsetShapeVertices(shape, modelW * 0.75f, modelH * -0.75f, 0);
				ArElementObj arAsset = new ArElementObj(shape, 0.17f, BodyTrackType.HEAD);
				arAsset.setPositionOffset(0, 0, 0);
				arPool.addElement(arAsset);
			}
			return arPool;
	}

	protected void drawApp() {
		p.background(0);
		UI.setValueToggle(KinectV2SkeletonsAR.DRAW_AR_ELEMENTS, true);	// externally toggle whether AR elements are active
		kinectSkeletonsAR.update();
		ColorAdjustmentFilter.applyFromUI(kinectSkeletonsAR.bufferBG());
		p.image(kinectSkeletonsAR.bufferBG(), 0, 0);
		p.image(kinectSkeletonsAR.bufferAR(), 0, 0);
		
		
		// adjust ar element on the fly
//		arPool.elementAt(2).setPositionOffset(0, 0.075f, 0f);
//		arPool.elementAt(3).setPositionOffset(0, 0f, 0f);
//		arPool.elementAt(3).setBaseScale(0.65f);
//		arPool.elementAt(1).setPivotOffset(0, 0.1f, 0);
//		arPool.elementAt(2).setRotationOffset(0, 0, -P.HALF_PI);
	}

	//////////////////////////////////////////
	// IKinectV2SkeletonsARDelegate methods
	//////////////////////////////////////////

	public void arElementShowing(IArElement arElement, KSkeleton skeleton2d, KSkeleton skeleton3d) {
		P.out("arElement SHOW");
	}

	public void arElementHidden(IArElement arElement, KSkeleton skeleton2d, KSkeleton skeleton3d) {
		P.out("arElement HIDE");
	}

}