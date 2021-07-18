package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.camera.CameraUtil;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.DitherFilter;
import com.haxademic.core.draw.filters.pshader.RadialFlareFilter;
import com.haxademic.core.draw.filters.pshader.ToneMappingFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class Demo_3dInfiniteRunScene
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 300;
	public final float FAR_Z = -20000;
	public final float GAME_TIME_LENGTH = 60 * 1000;
	protected float gameStartTime = 0;
	protected float scrollZ = 0;
	protected float scrollDelta = 0;
	protected float runSpeed = 1;
	
	protected Ground ground;
	protected Box[] box;
	protected PShape shape;
	
	protected String CAMERA_TILT = "CAMERA_TILT";
	protected String CAMERA_Y = "CAMERA_Y";
	protected String CAMERA_X = "CAMERA_X";
	
	protected void config() {
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 1024);
		Config.setProperty(AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
		gameStartTime = p.millis();
		
		// build camera
		UI.addTitle("Camera");
		UI.addSlider(CAMERA_TILT, -0.2f, -P.TWO_PI, P.TWO_PI, 0.001f, false);
		UI.addSlider(CAMERA_Y, 1.25f, -5, 5, 0.01f, false);
		UI.addSlider(CAMERA_X, 0, -2f, 2f, 0.01f, false);
		
		// build elements
		ground = new Ground();
		box = new Box[20];
		for (int i = 0; i < box.length; i++) {
			box[i] = new Box();
		}
		
		// load model
		shape = PShapeUtil.shapeFromImage(P.getImage("images/_sketch/pixel-objects/football.png"));
		PShapeUtil.scaleVertices(shape, 40, 40, 340);
		PShapeUtil.scaleShapeToWidth(shape, p.height * 0.7f);
		PShapeUtil.setRegistrationOffset(shape, 0, -0.5f, 0);

	}
	
	protected void applySpeed(float inputSpeed) {
		float lastScroll = scrollZ;
		scrollZ += inputSpeed;
		scrollDelta = scrollZ - lastScroll;
	}


	protected void updateGameProgress() {
		// update progress (0-1) based on time
		float gameDuration = p.millis() - gameStartTime;
		float gameProgress = gameDuration / GAME_TIME_LENGTH;
		gameProgress = P.constrain(gameProgress, 0, 1);

		// modulo for 4 levels
		float levelProgress = (gameProgress % 0.25f) * 4f;
	}

	protected void updateInput() {
		runSpeed = 25;
		applySpeed(runSpeed);
	}
	
	protected void setContext() {
		CameraUtil.setCameraDistance(p.g, 1, 25000);
		p.background(0);
		PG.setDrawCorner(p.g);
		p.translate(p.width * -UI.valueEased(CAMERA_X), p.height * UI.valueEased(CAMERA_Y), 0);
		p.rotateX(UI.valueEased(CAMERA_TILT));
	}
	
	protected void drawApp() {
		updateInput();
		setContext();
		ground.preDraw();
		ground.draw(p.g);
		PG.setBetterLights(p.g);
		drawBoxes();
		postProcessStage();
	}
	
	protected void drawBoxes() {
		for (int i = 0; i < box.length; i++) {
			box[i].draw(p.g);
		}
	}

	
	protected void postProcessStage() {
		ToneMappingFilter.instance(P.p).setMode(1);
		ToneMappingFilter.instance(P.p).setGamma(2.2f);
		ToneMappingFilter.instance(P.p).setCrossfade(1f);
		ToneMappingFilter.instance(P.p).applyTo(p.g);

		RadialFlareFilter.instance(p).setImageBrightness(12f);
		RadialFlareFilter.instance(p).setFlareBrightness(2f);
		RadialFlareFilter.instance(p).applyTo(p.g);
		
		VignetteFilter.instance(p).setDarkness(0.8f);
		VignetteFilter.instance(p).applyTo(p.g);
		
		DitherFilter.instance(P.p).setDitherMode8x8();
		DitherFilter.instance(P.p).applyTo(p.g);
	}
	
	public class Ground {

		protected TiledTexture tiledImg;
		protected PGraphics gridCellPG;
		protected float tileScale = 1f;
		
		protected PShape sheetMesh;
		protected float meshSize;
		protected PGraphics materialBuffer;
		
		protected EasingColor colorBg = new EasingColor("#ff000000", 20);
		protected EasingColor colorStroke = new EasingColor("#ffffffff", 20);

		
		public Ground() {
			generateTexture();
			buildMesh();
			tiledImg = new TiledTexture(gridCellPG);
		}
		
		protected void generateTexture() {
			gridCellPG = PG.newPG2DFast(64, 64);
			redrawTile();
		}
		
		protected void buildMesh() {
			// built mesh texture
			materialBuffer = p.createGraphics(2048, 2048, PRenderers.P3D);
			materialBuffer.smooth(8);
			PG.setTextureRepeat(materialBuffer, true);
			OpenGLUtil.setTextureQualityHigh(materialBuffer);
			tileScale = (float) gridCellPG.width * (1f / ((float) materialBuffer.width / (float) gridCellPG.width));
			
			// build sheet mesh
			meshSize = P.abs(FAR_Z * -1f);
			sheetMesh = Shapes.createSheet(10, meshSize, meshSize);
			sheetMesh.setTexture(materialBuffer);
		}

		
		protected void redrawTile() {
			colorBg.update();
			colorStroke.update();
			
			colorStroke.setTargetInt(0xff00ff00);
			gridCellPG.beginDraw();
			gridCellPG.background(colorBg.colorInt()); 
			gridCellPG.stroke(colorStroke.colorInt());
			gridCellPG.strokeWeight(3f);
			gridCellPG.noFill();
			gridCellPG.rect(0, 0, gridCellPG.width, gridCellPG.height);
			gridCellPG.endDraw();
		}

		public void setColors(String hex1, String hex2) {
			colorBg.setTargetHex(hex1);
			colorStroke.setTargetHex(hex2);
		}
		
		
		public void preDraw() {
			// update colors
			redrawTile();
			
			// update scrolling texture 
			tileScale = (float) gridCellPG.width * (1f / ((float) materialBuffer.width / (float) gridCellPG.width));
			float repeatZoom = 1f;
			tiledImg.setZoom(1f, repeatZoom);
			tiledImg.setOffset(0, -scrollZ / tileScale * repeatZoom / meshSize * (float) gridCellPG.height);
			tiledImg.update();
			
			// draw repeating scroll texture to texture map 
			materialBuffer.beginDraw();
			materialBuffer.fill(255);
			PG.setCenterScreen(materialBuffer);
			tiledImg.draw(materialBuffer, materialBuffer.width, materialBuffer.height, true);
			
			// add gradient on top
			materialBuffer.rotate(P.HALF_PI);
			Gradients.linear(materialBuffer, materialBuffer.width, materialBuffer.height, p.color(0,0), 0x00ffffff);
			materialBuffer.endDraw();
			
			// debug textures
			DebugView.setTexture("materialBuffer", materialBuffer);
			DebugView.setTexture("gridCellPG", gridCellPG);
			DebugView.setValue("tileScale", tileScale);
		}
		
		public void draw(PGraphics pg) {
			// set center
			pg.push();
			pg.translate(0, 0, FAR_Z * 0.5f);
			pg.rotateX(P.HALF_PI);
			
			// draw textured mesh 
			pg.shape(sheetMesh);
			pg.pop();
		}
		
	}
	
	public class Box {
		
		protected PVector position = new PVector();
		protected PVector size = new PVector();
		protected float rot = 0;
		protected float scale = 0;
		
		public Box() {
			reset();
			position.z = MathUtil.randRange(-0, -25000); // -512 * 16;
		}
		
		public void reset() {
			size.x = 512;
			size.y = MathUtil.randRange(64, 2048);
			size.z = 512;
			position.x = MathUtil.randRange(-1024 * 4, 1024 * 4);
			position.y = -size.y/2f;
			position.z = MathUtil.randRange(-10000, -20000); // -512 * 16;
			rot = MathUtil.randRangeDecimal(0, P.TWO_PI);
			scale = 0;
		}
		
		public void draw(PGraphics pg) {
			position.z += scrollDelta;
			if(position.z > 200) reset();
			scale += 0.05f;
			if(scale > 1) scale = 1;
			
			pg.push();
			pg.stroke(0xff00ff00);
			pg.strokeWeight(3);
			pg.fill(80, 80, 0);
			pg.translate(position.x, position.y * scale, position.z);
//			pg.translate(position.x, 0, position.z);
			pg.rotateY(rot);
			pg.box(size.x * scale, size.y * scale, size.z * scale);
//			pg.shape(shape);
			pg.pop();
		}
		
	}
	
	
	/////////////////////////////////
	// Assets helpers
	/////////////////////////////////
	
	public static PShape buildTexturedPlane(String imagePath, float planeHeight) {
		return buildTexturedPlane(imagePath, planeHeight, 0);
	}
	
	public static PShape buildTexturedPlane(String imagePath, float planeHeight, float xOffset) {
		PImage sprite = P.getImage(imagePath);
		PShape texturedMesh = Shapes.createSheet(1, sprite.width, sprite.height);
		texturedMesh.setTexture(sprite);
		PShapeUtil.centerShape(texturedMesh);
		PShapeUtil.scaleShapeToHeight(texturedMesh, planeHeight);
		PShapeUtil.setOnGround(texturedMesh);
		PShapeUtil.setRegistrationOffset(texturedMesh, xOffset, 0, 0);
		return texturedMesh;
	}
	


}
