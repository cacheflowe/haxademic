package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.filters.pshader.compound.ColorAdjustmentFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.KinectV2SkeletonsAR;
import com.haxademic.core.hardware.depthcamera.ar.ArElementCustom;
import com.haxademic.core.hardware.depthcamera.ar.ArElementImage;
import com.haxademic.core.hardware.depthcamera.ar.ArElementObj;
import com.haxademic.core.hardware.depthcamera.ar.ArElementPool;
import com.haxademic.core.hardware.depthcamera.ar.IArElement.BodyTrackType;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;

public class Demo_KinectV2SkeletonsAR 
extends PAppletHax {

	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	
	protected KinectV2SkeletonsAR kinectSkeletonsAR;
	protected ArElementPool arPool;
	
	protected void config() {
		Config.setAppSize(1920, 1080);
		Config.setPgSize(1920, 1080);
	}
	
	protected void firstFrame() {
		kinectSkeletonsAR = new KinectV2SkeletonsAR(pg, buildArPool());
		ColorAdjustmentFilter.initUI();
	}
	
	protected ArElementPool buildArPool() {
		arPool = new ArElementPool();
			{
				ArElementImage arAsset = new ArElementImage(DemoAssets.smallTexture(), 0.15f, BodyTrackType.HEAD);
				arPool.addElement(arAsset);
			}
			{
				ArElementCustom arAsset = new ArElementCustom(0.1f);
				arAsset.setPositionOffset(0, -1, 0);
				arPool.addElement(arAsset);
			}
			{
				ArElementImage arAsset = new ArElementImage(DemoAssets.arrow(), 0.1f, BodyTrackType.HAND_POINT);
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
		kinectSkeletonsAR.update();
		ColorAdjustmentFilter.applyFromUI(kinectSkeletonsAR.bufferBG());
		p.image(kinectSkeletonsAR.bufferBG(), 0, 0);
		p.image(kinectSkeletonsAR.bufferAR(), 0, 0);
		
		// adjust ar element on the fly
//		arPool.elementAt(2).setPositionOffset(0.45f, 0f, 0f);
//		arPool.elementAt(1).setBaseScale(0.375f);
//		arPool.elementAt(1).setPivotOffset(0, 0.1f, 0);
//		arPool.elementAt(2).setRotationOffset(0, 0, -P.HALF_PI);
	}

}
