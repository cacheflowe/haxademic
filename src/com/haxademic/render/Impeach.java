package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;

public class Impeach
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected TextToPShape textToPShape;
	protected PShape[] words;
	
	protected PShaderHotSwap wobbleShader;
	protected SimplexNoiseTexture displaceTexture;
	
	protected int FRAMES = 290;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1024 );
		Config.setProperty( AppSettings.HEIGHT, 1024 );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}

	public void firstFrame()	{
		// build text
		textToPShape = new TextToPShape(TextToPShape.QUALITY_MEDIUM);
		String fontFile = DemoAssets.fontOpenSansPath;
		
		// build extruded words
		words = new PShape[] {
			textToPShape.stringToShape3d("IMPEACH", 100, fontFile),
			textToPShape.stringToShape3d("THE", 100, fontFile),
			textToPShape.stringToShape3d("MOTHER", 100, fontFile),
			textToPShape.stringToShape3d("FUCKER", 100, fontFile),
		};
		// size the words
		PShapeUtil.scaleShapeToWidth(words[0], 760);
		PShapeUtil.scaleShapeToWidth(words[1], 250);
		PShapeUtil.scaleShapeToWidth(words[2], 690);
		PShapeUtil.scaleShapeToWidth(words[3], 650);
		
		// move the words
		PShapeUtil.offsetShapeVertices(words[0], 0, -300, 0);
		PShapeUtil.offsetShapeVertices(words[1], 0, -80, 0);
		PShapeUtil.offsetShapeVertices(words[2], 0, 70, 0);
		PShapeUtil.offsetShapeVertices(words[3], 0, 250, 0);

		// apply color & UV
		for (int i = 0; i < words.length; i++) {
			PShapeUtil.addTextureUVToShape(words[i], null);
			addFillToShape(words[i], 0.02f);
		}
		
		// load displacement shader & texture
		wobbleShader = new PShaderHotSwap(
			FileUtil.getFile("haxademic/shaders/vertex/mesh-2d-deform-vert.glsl"),
			FileUtil.getFile("haxademic/shaders/vertex/mesh-2d-deform-frag.glsl") 
		);
		displaceTexture = new SimplexNoiseTexture(256, 256);
		DebugView.setTexture("displacement map", displaceTexture.texture());
	}
	
	public void addFillToShape(PShape shape, float oscMult) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector vertex = shape.getVertex(i);
			float zFade = P.map(vertex.z, 50, -50, 1, 0);
			int fillReplace = P.p.color(
				(127 + 127f * P.sin(vertex.x * oscMult)) * zFade,
				(127 + 127f * P.sin(vertex.y * oscMult)) * zFade,
				(127 + 127f * P.sin(vertex.z * oscMult)) * zFade
			);
			shape.setFill(i, fillReplace);
			shape.noStroke();
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			addFillToShape(shape.getChild(j), oscMult);
		}
	}


	public void drawApp() {
		// set context
		background(0);
		PG.setCenterScreen(p);
		p.rotateX(0.2f + 0.2f * P.sin(FrameLoop.progressRads()) );
		p.rotateY(0.1f * P.sin(FrameLoop.progressRads()) );
//		PG.basicCameraFromMouse(p.g);

		// update displacement map
		displaceTexture.offsetX(P.sin(FrameLoop.progressRads()) * 0.4f);
		displaceTexture.rotation(FrameLoop.progressRads());
//		displaceTexture.zoom(1f);
		displaceTexture.zoom(1f + 0.1f * P.sin(FrameLoop.progressRads()));
		displaceTexture.update();
		
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		wobbleShader.shader().set("time", p.frameCount);
		wobbleShader.shader().set("displacementMap", displaceTexture.texture());
		wobbleShader.shader().set("displaceAmp", 10f);
		wobbleShader.shader().set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);
		wobbleShader.update();
		p.shader(wobbleShader.shader());  
		
		// draw word
		p.fill(255);
		for (int i = 0; i < words.length; i++) {
			p.shape(words[i]);
		}
		
		// pop shader
		p.resetShader();
	}

}
