package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.filters.pshader.compound.ColorAdjustmentFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.KinectV2SkeletonsAR;
import com.haxademic.core.hardware.depthcamera.KinectV2SkeletonsAR.IKinectV2SkeletonsARDelegate;
import com.haxademic.core.hardware.depthcamera.ar.ArElementCompound;
import com.haxademic.core.hardware.depthcamera.ar.ArElementGloves;
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
		ColorAdjustmentFilter.buildUI();
	}
	
	protected ArElementPool buildArPool() {
		arPool = new ArElementPool();
		
		{
			ArElementObj arAsset = loadObjFingie("../../afi-stadium-apps/data/ar-assets/falcons/foam-finger/FOAM_FINGIE.obj");
			arPool.addElement(arAsset);
		}
		{
			ArElementGloves arAsset = new ArElementGloves("../../bounty-fan-cam-sb-2024/data/ar-assets/paper-towel-roll/", "../../bounty-fan-cam-sb-2024/data/ar-assets/paper-towel-sheet/", 0.25f);
			arPool.addElement(arAsset);
		}
		{
			ArElementObj arAsset = loadObjFingie("../../bounty-fan-cam-sb-2024/data/ar-assets/foam-fingie/FOAM_FINGER.obj");
			arPool.addElement(arAsset);
		}
		{
			ArElementImage arAsset = new ArElementImage("../../bounty-fan-cam-sb-2024/data/ar-assets/text-bubbles/", 0.13f, BodyTrackType.HEAD, 30);
			arAsset.setPositionOffset(0.75f, -0.55f, 0f);
			arPool.addElement(arAsset);
		}
		{
			ArElementImage arAsset = new ArElementImage("../../bounty-fan-cam-sb-2024/data/ar-assets/wing-spin/", 0.15f, BodyTrackType.HEAD);
			arPool.addElement(arAsset.setPositionOffset(0, -0.55f, 0));
		}
		{
			ArElementImage arAsset = new ArElementImage("../../bounty-fan-cam-sb-2024/data/ar-assets/bounty-face/", 0.22f, BodyTrackType.HEAD);
			arPool.addElement(arAsset.setPositionOffset(0, -0.05f, 0));
		}
		{
			ArElementObj arAsset = loadObjHelmet(
					"../../bounty-fan-cam-sb-2024/data/ar-assets/helmet/HELMET_CAM.obj",
					"../../bounty-fan-cam-sb-2024/data/ar-assets/helmet/Helmet_03_01_Helmet.Justin.001_Baked.002_BaseColor.png");
			arPool.addElement(arAsset);
		}
		/*
		{
			ArElementObj arAsset = loadObjHelmet(
					"../../afi-stadium-apps/data/ar-assets/falcons/helmet-with-visor/helmet-fancam-v05.obj",
					"../../afi-stadium-apps/data/ar-assets/falcons/helmet-with-visor/helmet-modern-flattened.png");
			arPool.addElement(arAsset);
		}
		{
			ArElementImage arAsset = new ArElementImage(DemoAssets.arrow(), 0.1f, BodyTrackType.HAND_POINT_RIGHT);
			arAsset.setRotationOffset(0, 0, 0);
			arAsset.setPositionOffset(0.5f, 0, 0);
			arPool.addElement(arAsset);
		}
		{
			ArElementImage arAsset = new ArElementImage("../../afi-stadium-apps/data/ar-assets/falcons/footballs-spin/", 0.15f, BodyTrackType.HEAD);
			arPool.addElement(arAsset.setPositionOffset(0, -0.55f, 0));
		}
		{
			ArElementImage arAsset = new ArElementImage("../../afi-stadium-apps/data/ar-assets/united/thought-bubble-2/", 0.1f, BodyTrackType.HEAD, 1);
			arAsset.setPositionOffset(0.7f, -0.45f, 0f);
			arPool.addElement(arAsset);
		}
		{
			ArElementImage arAsset = new ArElementImage("../../afi-stadium-apps/data/ar-assets/united/thought-bubble-1/", 0.1f, BodyTrackType.HEAD, 1);
			arAsset.setPositionOffset(0.7f, -0.45f, 0f);
			arPool.addElement(arAsset);
		}
		{
			ArElementCompound arAsset = new ArElementCompound();
			arPool.addElement(arAsset);
		}
		{
			ArElementImage arAsset = new ArElementImage("../../afi-stadium-apps/data/ar-assets/united/crown/", 0.15f, BodyTrackType.HEAD);
			arAsset.setPositionOffset(-0.015f, -0.275f, 0);
			arPool.addElement(arAsset);
		}
		 */

//			{
//				ArElementCustom arAsset = new ArElementCustom(0.1f);
//				arAsset.setPositionOffset(0, -1, 0);
//				arPool.addElement(arAsset);
//			}
//			{
//				ArElementImage arAsset = new ArElementImage("haxademic/images/floaty-blob.anim/", 0.17f, BodyTrackType.HEAD, 24);
//				arAsset.setPositionOffset(0, -1f, 0);
//				arPool.addElement(arAsset);
//			}
/*
			{
				ArElementImage arAsset = new ArElementImage("images/_sketch/FreddieFalcon/", 0.21f, BodyTrackType.HEAD, 24);
//				arAsset.setPositionOffset(0, -1f, 0);
				arPool.addElement(arAsset);
			}
			{
				ArElementImage arAsset = new ArElementImage("images/_sketch/falcons-headset/", 0.14f, BodyTrackType.HEAD, 24);
//				arAsset.setPositionOffset(0, -1f, 0);
				arPool.addElement(arAsset);
			}
			{
				// add skull helmet
//				PShape shape = P.p.loadShape(FileUtil.getPath(DemoAssets.objSkullRealisticPath));
				PShape shape = PShapeUtil.loadModelAndTexture("models/Helmet_Hole/ATL_Helmet_hole_03.obj", "models/Helmet_Hole/helmet_v2_default_BaseColor.1001-rework.png");
				PShapeUtil.centerShape(shape);
				PShapeUtil.meshRotateOnAxis(shape, P.PI, P.Z);
				// PShapeUtil.meshRotateOnAxis(shape, P.PI, P.Y);
				// PShapeUtil.meshFlipOnAxis(shape, P.Y);
				// float modelW = PShapeUtil.getWidth(shape);
				// float modelH = PShapeUtil.getHeight(shape);
				// PShapeUtil.offsetShapeVertices(shape, modelW * 0.75f, modelH * -0.75f, 0);
				ArElementObj arAsset = new ArElementObj(shape, 0.11f, BodyTrackType.HEAD);
				arAsset.setPositionOffset(0, -0.1f, 0);
				arAsset.setRotationOffset(-0.2f, 0, 0);
				arPool.addElement(arAsset);
			}
			{
			}
		{
			ArElementImage arAsset = new ArElementImage(DemoAssets.smallTexture(), 0.15f, BodyTrackType.HEAD);
			arPool.addElement(arAsset);
		}
		{
			// add skull
			PShape shape = P.p.loadShape(FileUtil.getPath(DemoAssets.objSkullRealisticPath));
			PShapeUtil.centerShape(shape);
			PShapeUtil.meshRotateOnAxis(shape, P.PI, P.Z);
			// PShapeUtil.meshRotateOnAxis(shape, P.PI, P.Y);
			// PShapeUtil.meshFlipOnAxis(shape, P.Y);
			// float modelW = PShapeUtil.getWidth(shape);
			// float modelH = PShapeUtil.getHeight(shape);
			// PShapeUtil.offsetShapeVertices(shape, modelW * 0.75f, modelH * -0.75f, 0);
			ArElementObj arAsset = new ArElementObj(shape, 0.11f, BodyTrackType.HEAD);
			arAsset.setPositionOffset(0, 0.01f, 0);
			arPool.addElement(arAsset);
		}
			*/

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
		// arPool.elementAt(4).setPositionOffset(0, -0.3f, 0);
		// arPool.elementAt(4).setBaseScale(0.28f);
		// arPool.elementAt(1).setPivotOffset(0, 0.1f, 0);
		
		// DemoAssets.setDemoFont(pg);

		DebugView.logUptime();
	}

	//////////////////////////////////////////
	// Special asset loader methods
	//////////////////////////////////////////

	protected ArElementObj loadObjFingie(String filePath) {
		PShape shape = P.p.loadShape(FileUtil.getPath(filePath));
		PShapeUtil.centerShape(shape);
		PShapeUtil.meshFlipOnAxis(shape, P.Y);
		ArElementObj newObj = new ArElementObj(shape, 0.15f, BodyTrackType.HAND_POINT_LEFT);
		newObj.setRotationOffset(0, 0, P.HALF_PI);
		newObj.setPositionOffset(0, -0.23f, 0);
		return newObj;
	}

	protected ArElementObj loadObjHelmet(String modelPath, String texturePath) {
		PShape shape = PShapeUtil.loadModelAndTexture(modelPath, texturePath);
		PShapeUtil.centerShape(shape);
		PShapeUtil.meshRotateOnAxis(shape, P.PI, P.Z);
		ArElementObj arAsset = new ArElementObj(shape, 0.11f, BodyTrackType.HEAD);
		arAsset.setPositionOffset(0, -0.15f, 0);
		arAsset.setRotationOffset(-0.2f, 0, 0);
		return arAsset;
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
