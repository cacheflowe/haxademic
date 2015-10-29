package com.haxademic.sketch.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.MeshPool;
import com.haxademic.core.draw.mesh.MeshUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.vendor.Toxiclibs;

@SuppressWarnings("serial")
public class GifRenderEllo000ThreeDeeRotate
extends PAppletHax  
{	
	protected MeshPool _objPool;
	
	protected int animCount = 0;
	
	public void setup() {
		super.setup();
		
		// create ello mesh
		_objPool = new MeshPool( p );
		_objPool.addMesh( "ELLO_SVG", MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, "../data/svg/ello.svg", -1, 20, 2f ), 5 ), 1 );

		 p.smooth(OpenGLUtil.SMOOTH_HIGH);
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
		_appConfig.setProperty( "rendering_gif", "true" );
		_appConfig.setProperty( "rendering_gif_framerate", "45" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", "22" ); // 26 for imgs=5
	}
		
	public void drawApp() {
		p.background(255);
		p.noStroke();
		
		DrawUtil.setColorForPImage(p);
		DrawUtil.setDrawCenter(p);

		DrawUtil.setCenterScreen( p );
		DrawUtil.setBasicLights( p );
		
		p.translate(0, 360, 300);
		
		p.rotateX(-p.frameCount * (float)Math.PI/60f);
		
		float imgs = 6;
		float rotInc = P.TWO_PI / imgs;
		
		for(int i=0; i < imgs; i++) {
			p.rotateX(rotInc);
			p.pushMatrix();
			p.translate(0, -626, 0);
//			p.image(_image, 0, 0);
			p.fill(0);
			p.noStroke();
			Toxiclibs.instance(p).toxi.mesh( _objPool.getMesh("ELLO_SVG"), true );
			p.popMatrix();
		}
		
		p.translate(0, -260, 0);
		
		p.fill(0, 255);	// ello black
		p.noStroke();
		
		int spacing = 1000;
		animCount += 50;
		animCount = animCount % spacing;
	}
}
