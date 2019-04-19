package com.haxademic.app.haxvisual.pools;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.textures.pgraphics.TextureAudioBlocksDeform;
import com.haxademic.core.draw.textures.pgraphics.TextureAudioSheetDeform;
import com.haxademic.core.draw.textures.pgraphics.TextureAudioTube;
import com.haxademic.core.draw.textures.pgraphics.TextureBlobSheet;
import com.haxademic.core.draw.textures.pgraphics.TextureBlocksSheet;
import com.haxademic.core.draw.textures.pgraphics.TextureConcentricDashedCubes;
import com.haxademic.core.draw.textures.pgraphics.TextureCyclingRadialGradient;
import com.haxademic.core.draw.textures.pgraphics.TextureDashedLineSine;
import com.haxademic.core.draw.textures.pgraphics.TextureEQBandDistribute;
import com.haxademic.core.draw.textures.pgraphics.TextureEQColumns;
import com.haxademic.core.draw.textures.pgraphics.TextureEQConcentricCircles;
import com.haxademic.core.draw.textures.pgraphics.TextureEQFloatParticles;
import com.haxademic.core.draw.textures.pgraphics.TextureEQGrid;
import com.haxademic.core.draw.textures.pgraphics.TextureEQLinesTerrain;
import com.haxademic.core.draw.textures.pgraphics.TextureFractalPolygons;
import com.haxademic.core.draw.textures.pgraphics.TextureLinesEQ;
import com.haxademic.core.draw.textures.pgraphics.TextureMeshAudioDeform;
import com.haxademic.core.draw.textures.pgraphics.TextureNoiseLines;
import com.haxademic.core.draw.textures.pgraphics.TextureOuterCube;
import com.haxademic.core.draw.textures.pgraphics.TextureOuterSphere;
import com.haxademic.core.draw.textures.pgraphics.TexturePixelatedAudio;
import com.haxademic.core.draw.textures.pgraphics.TexturePolygonLerpedVertices;
import com.haxademic.core.draw.textures.pgraphics.TextureRadialGridPulse;
import com.haxademic.core.draw.textures.pgraphics.TextureRotatingRings;
import com.haxademic.core.draw.textures.pgraphics.TextureRotatorShape;
import com.haxademic.core.draw.textures.pgraphics.TextureShaderTimeStepper;
import com.haxademic.core.draw.textures.pgraphics.TextureSphereOfCubes;
import com.haxademic.core.draw.textures.pgraphics.TextureSphereAudioTextures;
import com.haxademic.core.draw.textures.pgraphics.TextureSphereRotate;
import com.haxademic.core.draw.textures.pgraphics.TextureTwistingSquares;
import com.haxademic.core.draw.textures.pgraphics.TextureVectorFieldEQ;
import com.haxademic.core.draw.textures.pgraphics.TextureWaveformCircle;
import com.haxademic.core.draw.textures.pgraphics.TextureWaveformSimple;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;

import processing.core.PGraphics;

public class HaxVisualTexturePools {

//	public static void addTexturesToPoolSG(PGraphics _pg, ArrayList<BaseTexture> _bgTexturePool, ArrayList<BaseTexture> _fgTexturePool, ArrayList<BaseTexture> _overlayTexturePool, ArrayList<BaseTexture> _topLayerPool) {
//		int textureW = P.round(_pg.width);
//		int textureH = P.round(_pg.height);
//		
//		// complex textures in the back
//		_bgTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
//		_bgTexturePool.add( new TextureOuterCube( textureW, textureH ) );
//		_bgTexturePool.add( new TextureBlobSheet( textureW, textureH ) );
//		_bgTexturePool.add( new TextureRotatorShape( textureW, textureH ) );
//		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cubert.glsl" ));
//		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "triangle-perlin.glsl" ));
//
////		_bgTexturePool.add( new TextureAudioTube( textureW, textureH ) );
////		_bgTexturePool.add( new TextureRotatingRings( textureW, textureH ) );
//		_fgTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
//		_fgTexturePool.add( new TextureVectorFieldEQ( textureW, textureH ) );
//		_fgTexturePool.add( new TextureEQFloatParticles( textureW, textureH ));
//		_fgTexturePool.add( new TextureLinesEQ( textureW, textureH ));
//		_fgTexturePool.add( new TextureEQBandDistribute( textureW, textureH ));
//		_fgTexturePool.add( new TextureWaveformCircle( textureW, textureH ));
//		_fgTexturePool.add( new TextureEQConcentricCircles( textureW, textureH ) );
//		
////		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire.glsl" ));
//		_overlayTexturePool.add( new TextureAudioSheetDeform( textureW, textureH ));
//		_overlayTexturePool.add( new TextureMeshAudioDeform( textureW, textureH ));
////		_overlayTexturePool.add( new TextureBlocksSheet( textureW, textureH ) );
//		_overlayTexturePool.add( new TexturePixelatedAudio( textureW, textureH ));
//		_overlayTexturePool.add( new TextureEQLinesTerrain( textureW, textureH ));
//		_overlayTexturePool.add( new TextureAudioBlocksDeform( textureW, textureH ));
//		_overlayTexturePool.add( new TexturePixelatedAudio( textureW, textureH ));
////		_overlayTexturePool.add( );
//		
//		_topLayerPool.add( new TextureSvg3dExtruded( textureW, textureH ) );
//		_topLayerPool.add( new TextureSphereAudioTextures( _pg.width, _pg.height ) );
//	}
	
	public static void addTexturesToPoolMinimal(PGraphics _pg, ArrayList<BaseTexture> _bgTexturePool, ArrayList<BaseTexture> _fgTexturePool, ArrayList<BaseTexture> _overlayTexturePool, ArrayList<BaseTexture> _topLayerPool) {
		int textureW = P.round(_pg.width);
		int textureH = P.round(_pg.height);
		
		// complex textures in the back
//		_bgTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
		_bgTexturePool.add( new TextureOuterCube( textureW, textureH ) );
		_bgTexturePool.add( new TextureConcentricDashedCubes( textureW, textureH ));
		_bgTexturePool.add( new TextureDashedLineSine( textureW, textureH ));
		_bgTexturePool.add( new TextureNoiseLines( textureW, textureH ));
		_bgTexturePool.add( new TextureRadialGridPulse( textureW, textureH ));
		_bgTexturePool.add( new TexturePolygonLerpedVertices( textureW, textureH ));
		_bgTexturePool.add( new TextureSphereOfCubes( textureW, textureH ));


//		_bgTexturePool.add( new TextureAudioTube( textureW, textureH ) );
//		_bgTexturePool.add( new TextureBlobSheet( textureW, textureH ) );
//		_bgTexturePool.add( new TextureRotatorShape( textureW, textureH ) );
//		_bgTexturePool.add( new TextureRotatingRings( textureW, textureH ) );
////		_bgTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
//		_bgTexturePool.add( new TextureVectorFieldEQ( textureW, textureH ) );

//		_fgTexturePool.add( new TextureMeshAudioDeform( textureW, textureH ));
//		_fgTexturePool.add( new TextureEQBandDistribute( textureW, textureH ));
		_fgTexturePool.add( new TextureEQConcentricCircles( textureW, textureH ) );
		_fgTexturePool.add( new TextureWaveformCircle( textureW, textureH ));

		_fgTexturePool.add( new TextureConcentricDashedCubes( textureW, textureH ));
		_fgTexturePool.add( new TextureDashedLineSine( textureW, textureH ));
		_fgTexturePool.add( new TextureNoiseLines( textureW, textureH ));
		_fgTexturePool.add( new TextureRadialGridPulse( textureW, textureH ));
		_fgTexturePool.add( new TexturePolygonLerpedVertices( textureW, textureH ));
		_fgTexturePool.add( new TextureSphereOfCubes( textureW, textureH ));

//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-metaballs.glsl" ));
		_overlayTexturePool.add( new TextureConcentricDashedCubes( textureW, textureH ));
		_overlayTexturePool.add( new TextureDashedLineSine( textureW, textureH ));
		_overlayTexturePool.add( new TextureNoiseLines( textureW, textureH ));
		_overlayTexturePool.add( new TextureRadialGridPulse( textureW, textureH ));
		_overlayTexturePool.add( new TexturePolygonLerpedVertices( textureW, textureH ));
		_overlayTexturePool.add( new TextureSphereOfCubes( textureW, textureH ));

//		_overlayTexturePool.add( );
		
//		_topLayerPool.add( new TextureSphereAudioTextures( _pg.width, _pg.height ) );
//		_topLayerPool.add( new TextureWords2d( _pg.width, _pg.height ) );
		_topLayerPool.add( new TexturePolygonLerpedVertices( textureW, textureH ));
		_topLayerPool.add( new TextureSphereOfCubes( textureW, textureH ));

	}
	
	public static void addTexturesToPool(PGraphics _pg, ArrayList<BaseTexture> _bgTexturePool, ArrayList<BaseTexture> _fgTexturePool, ArrayList<BaseTexture> _overlayTexturePool, ArrayList<BaseTexture> _topLayerPool) {

		int videoW = 640;
		int videoH = 360;
		int textureW = P.round(_pg.width/2);
		int textureH = P.round(_pg.height/2);
		
		
		// complex textures in the back
		_bgTexturePool.add( new TextureAudioTube( textureW, textureH ) );
		_bgTexturePool.add( new TextureBlobSheet( textureW, textureH ) );
		_bgTexturePool.add( new TextureRotatorShape( textureW, textureH ) );
		_bgTexturePool.add( new TextureRotatingRings( textureW, textureH ) );
		_bgTexturePool.add( new TextureOuterCube( textureW, textureH ) );
		_bgTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
		_bgTexturePool.add( new TextureVectorFieldEQ( textureW, textureH ) );
		_bgTexturePool.add( new TextureMeshAudioDeform( textureW, textureH ));
		
		_bgTexturePool.add( new TextureAudioSheetDeform( textureW, textureH ));
		_bgTexturePool.add( new TextureAudioBlocksDeform( textureW, textureH ));
//		_bgTexturePool.add( new TextureBlocksSheet( textureW, textureH ) );
		_bgTexturePool.add( new TextureEQBandDistribute( textureW, textureH ));
		_bgTexturePool.add( new TextureEQConcentricCircles( textureW, textureH ) );
		_bgTexturePool.add( new TextureEQColumns( textureW, textureH ));
		_bgTexturePool.add( new TextureEQFloatParticles( textureW, textureH ));
		_bgTexturePool.add( new TextureEQGrid( textureW, textureH ));
		_bgTexturePool.add( new TextureEQLinesTerrain( textureW, textureH ));
		_bgTexturePool.add( new TextureLinesEQ( textureW, textureH ));
		_bgTexturePool.add( new TextureMeshAudioDeform( textureW, textureH ));
		_bgTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
		_bgTexturePool.add( new TexturePixelatedAudio( textureW, textureH ));
		_bgTexturePool.add( new TextureSphereRotate( textureW, textureH ));
		_bgTexturePool.add( new TextureVectorFieldEQ( textureW, textureH ) );
		_bgTexturePool.add( new TextureWaveformSimple( textureW, textureH ));
		_bgTexturePool.add( new TextureWaveformCircle( textureW, textureH ));

		_bgTexturePool.add( new TextureConcentricDashedCubes( textureW, textureH ));
		_bgTexturePool.add( new TextureDashedLineSine( textureW, textureH ));
		_bgTexturePool.add( new TextureNoiseLines( textureW, textureH ));
		_bgTexturePool.add( new TextureRadialGridPulse( textureW, textureH ));
		_bgTexturePool.add( new TexturePolygonLerpedVertices( textureW, textureH ));
		_bgTexturePool.add( new TextureSphereOfCubes( textureW, textureH ));

		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sdf-01-auto.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sdf-02-auto.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sdf-03.glsl" ));
//		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bubbles-iq.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-clouds.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-kaleido.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-radial-wave.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-tiled-moire.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cog-tunnel.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cubert.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "firey-spiral.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "flame-wisps.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "flexi-spiral.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "glowwave.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "hex-alphanumerics.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "inversion-iq.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "iq-iterations-shiny.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "light-leak.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "radial-burst.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sin-waves.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "spinning-iq.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "star-field.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "stars-fractal-field.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "stars-nice.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "stars-screensaver.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "stars-scroll.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "supershape-2d.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "warped-tunnel.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "water-smoke.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "wavy-3d-tubes.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "space-swirl.glsl" ));
//		_bgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "docking-tunnel.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "hughsk-metaballs.glsl" ));
//		_bgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "hughsk-tunnel.glsl" ));

//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/NudesInLimbo-1983.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/Microworld 1980 with William Shatner.mp4" ));
//
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/smoke-loop.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/tree-loop.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-in-water.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-grow-shrink.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/fire.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/bubbles.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/clouds-timelapse.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/water.mp4" ));

		
		
		_fgTexturePool.add( new TextureCyclingRadialGradient( textureW, textureH ));
		_fgTexturePool.add( new TextureEQBandDistribute( textureW, textureH ));
		_fgTexturePool.add( new TextureEQConcentricCircles( textureW, textureH ) );
		_fgTexturePool.add( new TextureEQColumns( textureW, textureH ));
		_fgTexturePool.add( new TextureEQFloatParticles( textureW, textureH ));
		_fgTexturePool.add( new TextureEQGrid( textureW, textureH ));
		_fgTexturePool.add( new TextureFractalPolygons( textureW, textureH ));
		_fgTexturePool.add( new TextureLinesEQ( textureW, textureH ));
		_fgTexturePool.add( new TexturePixelatedAudio( textureW, textureH ));
		_fgTexturePool.add( new TextureOuterCube( textureW, textureH ) );
		_fgTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
//		_fgTexturePool.add( new TextureScrollingColumns( textureW, textureH ));
		_fgTexturePool.add( new TextureSphereRotate( textureW, textureH ));
//		_fgTexturePool.add( new TextureStarTrails( textureW, textureH ));
//		_fgTexturePool.add( new TextureSvgPattern( textureW, textureH ));
		_fgTexturePool.add( new TextureTwistingSquares( textureW, textureH ));
		_fgTexturePool.add( new TextureVectorFieldEQ( textureW, textureH ) );
		_fgTexturePool.add( new TextureWaveformSimple( textureW, textureH ));
		_fgTexturePool.add( new TextureWaveformCircle( textureW, textureH ));
		_fgTexturePool.add( new TextureConcentricDashedCubes( textureW, textureH ));
		_fgTexturePool.add( new TextureDashedLineSine( textureW, textureH ));
		_fgTexturePool.add( new TextureNoiseLines( textureW, textureH ));
		_fgTexturePool.add( new TextureRadialGridPulse( textureW, textureH ));
		_fgTexturePool.add( new TexturePolygonLerpedVertices( textureW, textureH ));
		_fgTexturePool.add( new TextureSphereOfCubes( textureW, textureH ));

		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "basic-checker.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "basic-diagonal-stripes.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-dazzle-voronoi.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-expand-loop.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-eye-jacker-01.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-eye-jacker-02.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-motion-illusion.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-scroll-rows.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-waves.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "circle-parts-rotate.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "dots-orbit.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "fade-dots.glsl" ));
//		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "gradient-line.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "lines-scroll-diag.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "matrix-rain.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "radial-waves.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "shiny-circle-wave.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sin-grey.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "square-fade.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "square-twist.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "swirl.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "triangle-perlin.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "wobble-sin.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "dot-grid-dof.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "morphing-bokeh-shape.glsl" ));

		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-asterisk-wave.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-checkerboard-stairs.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-hex-lines.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-hypno-lines.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-plasma.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-distance-blobs.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-dots-on-planes.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-drunken-holodeck.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-folded-wrapping-paper.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-grid-noise-warp.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire-camo-alt.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-metaballs.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-op-wavy-rotate.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-repeating-circles.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-scrolling-dashed-lines.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-scrolling-radial-twist.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-squound-tunnel.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-stripe-waves.glsl" ));
//		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-triangle-wobble-stairs.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-warp-vortex.glsl" ));

//		_overlayTexturePool.add( new TextureImageTileScroll( textureW, textureH ) );
//		_overlayTexturePool.add( new TextureImageTileScroll( textureW, textureH ));
//		_overlayTexturePool.add( new TextureImageTileScroll( textureW, textureH ) );
//		_overlayTexturePool.add( new TextureImageTileScroll( textureW, textureH ));
//		_overlayTexturePool.add( new TextureImageTileScroll( textureW, textureH ) );
//		_overlayTexturePool.add( new TextureImageTileScroll( textureW, textureH ));
		
		
		_overlayTexturePool.add( new TextureAudioSheetDeform( textureW, textureH ));
		_overlayTexturePool.add( new TextureAudioBlocksDeform( textureW, textureH ));
		_overlayTexturePool.add( new TextureBlobSheet( textureW, textureH ) );
		_overlayTexturePool.add( new TextureBlocksSheet( textureW, textureH ) );
		_overlayTexturePool.add( new TextureEQBandDistribute( textureW, textureH ));
		_overlayTexturePool.add( new TextureEQConcentricCircles( textureW, textureH ) );
		_overlayTexturePool.add( new TextureEQColumns( textureW, textureH ));
		_overlayTexturePool.add( new TextureEQFloatParticles( textureW, textureH ));
		_overlayTexturePool.add( new TextureEQGrid( textureW, textureH ));
		_overlayTexturePool.add( new TextureEQLinesTerrain( textureW, textureH ));
		_overlayTexturePool.add( new TextureLinesEQ( textureW, textureH ));
		_overlayTexturePool.add( new TextureMeshAudioDeform( textureW, textureH ));
		_overlayTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
		_overlayTexturePool.add( new TextureOuterCube( textureW, textureH ) );
		_overlayTexturePool.add( new TexturePixelatedAudio( textureW, textureH ));
		_overlayTexturePool.add( new TextureRotatorShape( textureW, textureH ) );
		_overlayTexturePool.add( new TextureSphereRotate( textureW, textureH ));
		_overlayTexturePool.add( new TextureVectorFieldEQ( textureW, textureH ) );
		_overlayTexturePool.add( new TextureWaveformSimple( textureW, textureH ));
		_overlayTexturePool.add( new TextureWaveformCircle( textureW, textureH ));
		

//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "basic-checker.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "basic-diagonal-stripes.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-dazzle-voronoi.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-expand-loop.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-eye-jacker-01.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-eye-jacker-02.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-motion-illusion.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-scroll-rows.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-waves.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "circle-parts-rotate.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "dots-orbit.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "fade-dots.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "gradient-line.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "lines-scroll-diag.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "matrix-rain.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "radial-waves.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "shiny-circle-wave.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sin-grey.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "square-fade.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "square-twist.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "swirl.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "triangle-perlin.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "wobble-sin.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "dot-grid-dof.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "morphing-bokeh-shape.glsl" ));
//
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-asterisk-wave.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-checkerboard-stairs.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-hex-lines.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-hypno-lines.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-plasma.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-distance-blobs.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-dots-on-planes.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-drunken-holodeck.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-folded-wrapping-paper.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-grid-noise-warp.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire-camo-alt.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-metaballs.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-op-wavy-rotate.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-repeating-circles.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-scrolling-dashed-lines.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-scrolling-radial-twist.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-squound-tunnel.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-stripe-waves.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-triangle-wobble-stairs.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-warp-vortex.glsl" ));
		
//		_overlayTexturePool.add( new TextureAppFrameEq2d( textureW, textureH ));

//		_bgTexturePool.add( new TextureSphereAudioTextures( videoW, videoH ));
//		_bgTexturePool.add( new TextureWebCam( videoW, videoH ));
//		_bgTexturePool.add( new TextureImageTimeStepper( textureW, textureH ));
//		_fgTexturePool.add( new TextureMeshDeform( textureW, textureH ));
		
		
		_topLayerPool.add( new TextureSphereAudioTextures( _pg.width, _pg.height ) );

	}
	
}
