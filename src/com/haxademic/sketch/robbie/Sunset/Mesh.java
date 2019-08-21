package com.haxademic.sketch.robbie.Sunset;

import com.haxademic.core.app.P;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.sketch.robbie.CameraController;
import com.haxademic.sketch.robbie.Sunset.Sunset.App;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.MouseEvent;

public class Mesh 
implements IAppStoreListener {
	
	protected Sunset p;
	protected PGraphics pg;
	
	protected Gradial gradial;

	protected int cellSize = 5;
	protected int cols = 100;
	protected int rows = 100;
	
	protected int colorFG;
	protected int colorBG;
	
	protected int offsetW;
	protected int offsetH;
	
	protected InputTrigger key1 = (new InputTrigger()).addKeyCodes(new char[]{'1'});
	public static boolean drawGrid = false;
	
	protected CameraController cameraController;
	protected ImageGradient imageGradient;
	
	protected boolean mouseOver = false;
	protected LinearFloat mouseProgress = new LinearFloat(0, 1f/60);
	
	public Mesh() {
		p = (Sunset) P.p;
		pg = p.pg;
		P.p.registerMethod("mouseEvent", this); // add mouse listeners
		P.store.addListener(this);

		offsetW = pg.width;
		offsetH = pg.height;
		
		cols = pg.width / cellSize;
		rows = pg.height / cellSize;

		colorFG = p.color(255, 187, 156);
		colorBG = p.color(31, 101, 145);
		
		gradial = new Gradial(pg);
		cameraController = new CameraController(pg);
		
		imageGradient = new ImageGradient(P.p.loadImage(FileUtil.getFile("haxademic/images/palettes/sunset.png")));
		
		mouseProgress.setInc(1f/60);
		mouseProgress.setCurrent(0);
		mouseProgress.setTarget(1);
	}

	
	public void drawPre(int frameCount) {
		if(key1.triggered()) drawGrid = !drawGrid;
		
		gradial.draw(); 
//		gradial.GradialBuffer.loadPixels();
	}

	public void draw(int frameCount) {
		// Camera rotation
		cameraController.draw();
		
		drawMesh();
	}
	
	public void drawMesh() {
//		float colorProgress = 0.5f + 0.5f * P.sin(p.frameCount * 0.01f);
//		pg.background(imageGradient.getColorAtProgress(colorProgress));
//		if(p.frameCount % 100 == 1) imageGradient.randomGradientTexture();
		
//		pg.pushMatrix();
//		pg.translate(pg.width/2 - imageGradient.texture().width/2, pg.height/2);
//		imageGradient.drawDebug(p.g);
//		pg.popMatrix();
		
		pg.background(31, 101, 145);
		pg.noFill();
		pg.stroke(40, 40, 40);
		if (!drawGrid) pg.noStroke();

//		pg.translate(offsetW, offsetH, -100);
		pg.translate(offsetW, offsetH);
		pg.rotateX((float) (P.PI/3.0));
		
		mouseProgress.update();
    	float curMouse = Penner.easeOutQuad(mouseProgress.value(), 0, 1, 1);
    	if (mouseOver) {
    		mouseProgress.setTarget(0);
    	} else {
    		mouseProgress.setTarget(1);
    	}
		
		for (int y = 0; y < rows; y++) {
		    pg.beginShape(P.TRIANGLE_STRIP);
		    for (int x = 0; x < cols + 1; x++) {
		    	int posX = x*cellSize - offsetW;
		    	int posY = y*cellSize - offsetH;
		    	int posY2 = (y+1)*cellSize - offsetH;
//		    	float freq = 0.001f;
		    	float amp = 1f; //1200
		    	float ampOffset = -100f; //500
//		    	float ampSpeed = 200f;

//		    	float fillNoise = P.map(p.noise(posX * freq + p.frameCount/ampSpeed, posY * freq), 0.3f, 0.7f, 0, 1);
//		    	pg.fill(pg.lerpColor(colorBG, colorFG, fillNoise));
		    	
//		    	pg.vertex(posX, posY, p.noise(posX * freq + p.frameCount/ampSpeed, posY * freq) * amp - ampOffset);
//		    	pg.vertex(posX, posY2, p.noise(posX * freq  + p.frameCount/ampSpeed, posY2 * freq)  * amp - ampOffset);
		    	
//		    	if (x == (int)cols/2 && y == (int)rows/2) {
//		    		P.out(p.brightness(ImageUtil.getPixelColor(gradial.GradialBuffer, (int)P.map(posX, -offsetW, 0, 0, gradial.GradialBuffer.width), (int)P.map(posY, -offsetH, 0, 0, gradial.GradialBuffer.height))));
//		    	}
		    	
		    	int posXmap = (int)P.map(posX, -offsetW, 0, 0, gradial.GradialBuffer.width);
		    	int posYmap = (int)P.map(posY, -offsetH, 0, 0, gradial.GradialBuffer.height);
		    	int posY2map = (int)P.map(posY2, -offsetH, 0, 0, gradial.GradialBuffer.height);
		    	

		    	pg.fill(imageGradient.getColorAtProgress(P.map(p.brightness(ImageUtil.getPixelColor(gradial.GradialBuffer, posXmap, posYmap)), 0, 80 + (170 * curMouse), 0, 1)));		    		
		    	pg.vertex(posX, posY, p.brightness(ImageUtil.getPixelColor(gradial.GradialBuffer, posXmap, posYmap)) * amp - ampOffset);
		    	pg.fill(imageGradient.getColorAtProgress(P.map(p.brightness(ImageUtil.getPixelColor(gradial.GradialBuffer, posXmap, posY2map)), 0, 80 + (170 * curMouse), 0, 1)));		    		
		    	pg.vertex(posX, posY2, p.brightness(ImageUtil.getPixelColor(gradial.GradialBuffer, posXmap, posY2map))  * amp - ampOffset);
		    	

		    	
		    }
		    pg.endShape();
		}
		p.debugView.setTexture("meshTexture", pg);

	}
	
	/////////////////////////////////////////
	// Mouse listener
	/////////////////////////////////////////
	
	public void mouseEvent(MouseEvent event) {
//		mouseX = event.getX();
//		mouseY = event.getY();
		
		switch (event.getAction()) {
			case MouseEvent.ENTER:
				mouseOver = true;
				break;
			case MouseEvent.MOVE:
				break;
			case MouseEvent.EXIT:
			mouseOver = false;
				break;
			case MouseEvent.PRESS:
				break;
			case MouseEvent.RELEASE:
				break;
		}
	}

	
	/////////////////////////////////////
	// AppStore listeners
	/////////////////////////////////////
	
	@Override
	public void updatedNumber(String key, Number val) {
		if(key.equals(App.ANIMATION_FRAME_PRE)) drawPre(val.intValue());
		if(key.equals(App.ANIMATION_FRAME)) draw(val.intValue());
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
	

}
