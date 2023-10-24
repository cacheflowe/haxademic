package com.haxademic.core.draw.context;

import java.nio.ByteBuffer;

import com.drew.tools.FileUtil;
import com.haxademic.core.app.P;
import com.jogamp.opengl.GL4;

import processing.core.PGraphics;
import processing.opengl.FrameBuffer;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

// Custom FBO for blitting depth buffer into a readable texture
	// adapted by Nacho Cossio (@nacho_cossio) from this article by Sergiu Craitoiu http://in2gpu.com/2014/09/24/render-to-texture-in-opengl/ 
	// from: https://github.com/kosowski/Processing_DepthBuffer/blob/master/DepthBufferRead/CustomFrameBuffer.pde

public class DepthBuffer {
	protected int[] FBO = new int[1]; // framebuffer object
	protected int[] texture_color = new int[1];
	protected int[] texture_depth = new int[1];
	protected int[] drawbuffer = new int[1]; // add texture attachements
	protected GL4 gl;
	protected int width, height;
	protected PGraphics depthPG;
	protected PShader shader;

	public DepthBuffer(int w, int h) {
		gl = P.gl4;
		width = w;
		height = h;

		// bild internals
		generateFBO(width, height);

		// build buffer & shader to display
		// shader = P.p.loadShader(P.path("haxademic/shaders/vertex/depth-viewer.glsl"));
		String shaderFrag = """
				uniform sampler2D depthTexture;
				varying vec4 vertTexCoord;
				void main() {
					vec4 depth = texture2D(depthTexture, vec2(vertTexCoord.x, 1. - vertTexCoord.y)); // flip y
					gl_FragColor = vec4(depth.r);
				}
				""";
		shader = new PShaderCompiler(P.p, shaderFrag);
		depthPG = PG.newPG32(width, height, true, false);
	}

	// generate an empty color texture with 4 channels (RGBA8) using bilinear
	// filtering
	protected void generateColorTexture(int width, int height) {
		gl.glGenTextures(1, texture_color, 0);
		gl.glBindTexture(PGL.TEXTURE_2D, texture_color[0]);
		gl.glTexParameteri(PGL.TEXTURE_2D, PGL.TEXTURE_MAG_FILTER, PGL.LINEAR);
		gl.glTexParameteri(PGL.TEXTURE_2D, PGL.TEXTURE_MIN_FILTER, PGL.LINEAR);
		gl.glTexParameteri(PGL.TEXTURE_2D, PGL.TEXTURE_WRAP_S, PGL.CLAMP_TO_EDGE);
		gl.glTexParameteri(PGL.TEXTURE_2D, PGL.TEXTURE_WRAP_T, PGL.CLAMP_TO_EDGE);
		gl.glTexImage2D(PGL.TEXTURE_2D, 0, PGL.RGBA8, width, height, 0, PGL.RGBA, PGL.UNSIGNED_BYTE,
				ByteBuffer.allocate(width * height * 4));
	}

	// generate an empty depth texture with 1 depth channel using bilinear
	// filtering
	protected void generateDepthTexture(int width, int height) {
		gl.glGenTextures(1, texture_depth, 0);
		gl.glActiveTexture(PGL.TEXTURE1);
		gl.glBindTexture(PGL.TEXTURE_2D, texture_depth[0]);
		gl.glTexParameteri(PGL.TEXTURE_2D, PGL.TEXTURE_MAG_FILTER, PGL.LINEAR);
		gl.glTexParameteri(PGL.TEXTURE_2D, PGL.TEXTURE_MIN_FILTER, PGL.LINEAR);
		gl.glTexParameteri(PGL.TEXTURE_2D, PGL.TEXTURE_WRAP_S, PGL.CLAMP_TO_EDGE);
		gl.glTexParameteri(PGL.TEXTURE_2D, PGL.TEXTURE_WRAP_T, PGL.CLAMP_TO_EDGE);
		gl.glTexImage2D(PGL.TEXTURE_2D, 0, PGL.DEPTH24_STENCIL8, width, height, 0, PGL.DEPTH_COMPONENT, PGL.FLOAT,
				ByteBuffer.allocate(width * height * 4));
	}

	// Generate FBO and two empty textures
	protected void generateFBO(int width, int height) {
		// Generate a framebuffer object(FBO) and bind it to the pipeline
		gl.glGenFramebuffers(1, FBO, 0);
		gl.glBindFramebuffer(PGL.FRAMEBUFFER, FBO[0]);

		generateColorTexture(width, height);// generate empty texture
		generateDepthTexture(width, height);// generate empty texture

		// to keep track of our textures
		int attachment_index_color_texture = 0;

		// bind textures to pipeline. texture_depth is optional
		// 0 is the mipmap level. 0 is the heightest
		gl.glFramebufferTexture2D(PGL.FRAMEBUFFER, PGL.COLOR_ATTACHMENT0 + attachment_index_color_texture, GL4.GL_TEXTURE_2D, texture_color[0], 0);
		gl.glFramebufferTexture2D(PGL.FRAMEBUFFER, PGL.DEPTH_ATTACHMENT, GL4.GL_TEXTURE_2D, texture_depth[0], 0);// optional

		// add attachements
		// drawbuffer.push_back(GL_COLOR_ATTACHMENT0 +
		// attachment_index_color_texture);
		gl.glDrawBuffers(1, drawbuffer, 0);
		// Check for FBO completeness
		if (gl.glCheckFramebufferStatus(PGL.FRAMEBUFFER) != PGL.FRAMEBUFFER_COMPLETE) {
			System.out.println("Error! FrameBuffer is not complete");
		}
		// unbind framebuffer
		gl.glBindFramebuffer(PGL.FRAMEBUFFER, 0);
	}

	public PGraphics image() {
		return depthPG;
	}

	public void copyDepthToPG(PGraphics pg) {
		// get underlying fbo from passed-in pg
		PGL pgl = pg.beginPGL();
		FrameBuffer fb = ((PGraphicsOpenGL) pg).getFrameBuffer(true);
		copyDepthFrom(pgl, fb.glFbo);
		pg.endPGL();

		// Pass depth buffer as texture to shader
		depthPG.beginDraw();
		pgl = depthPG.beginPGL();
		int textureID = getDepthTexture()[0];
		int textureUnit = PGL.TEXTURE2;
		// int loc = pgl.getUniformLocation(shader.glProgram, "depthTexture");
		// pgl.uniform1i(loc, textureID);
		pgl.activeTexture(textureUnit);
		pgl.bindTexture(PGL.TEXTURE_2D, textureID);
		shader.set("depthTexture", textureID);
		depthPG.shader(shader);
		
		// Draw full screen quad
		depthPG.rect(0, 0, width, height);
		depthPG.resetShader();
		depthPG.endPGL();
		depthPG.endDraw();
	}

	public void copyFrom(PGL pgl, int sourceFboID, int destFboID, int mask) {
		copyFrom(pgl, sourceFboID, this.width, this.height, destFboID, mask);
	}

	public void copyFrom(PGL pgl, int sourceFboID, int sourceWidth, int sourceHeight, int destFboID, int mask) {
		// int mask = PGL.DEPTH_BUFFER_BIT;
		pgl.bindFramebuffer(PGL.READ_FRAMEBUFFER, sourceFboID);
		pgl.bindFramebuffer(PGL.DRAW_FRAMEBUFFER, destFboID);
		pgl.blitFramebuffer(0, 0, sourceWidth, sourceHeight,
				0, 0, this.width, this.height, mask, PGL.NEAREST);
		pgl.bindFramebuffer(PGL.READ_FRAMEBUFFER, 0);
		pgl.bindFramebuffer(PGL.DRAW_FRAMEBUFFER, 0);
	}

	public void copyDepthFrom(PGL pgl, int sourceFboID) {
		copyFrom(pgl, sourceFboID, FBO[0], PGL.DEPTH_BUFFER_BIT);
	}

	// return color texture from the framebuffer
	public int[] getColorTexture() {
		return texture_color;
	}

	// return depth texture from the framebuffer
	public int[] getDepthTexture() {
		return texture_depth;
	}

	// bind framebuffer to pipeline. We will call this method in the render loop
	public void bind() {
		gl.glBindFramebuffer(PGL.FRAMEBUFFER, FBO[0]);
		// this can be moved in GenerateFBO function but
		// we have to put here in case of a MRT functionality
	}

	// unbind framebuffer from pipeline. We will call this method in the render
	// loop
	public void unbind() {
		// 0 is the default framebuffer
		gl.glBindFramebuffer(PGL.FRAMEBUFFER, 0);
	}

}
