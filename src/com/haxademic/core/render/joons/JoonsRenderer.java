package com.haxademic.core.render.joons;

import java.util.ArrayList;

import org.sunflow.SunflowAPI;
import org.sunflow.math.Matrix4;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

public class JoonsRenderer{

	private JRRecorder recorder;	
	private SunflowAPI api;
	private boolean renderIsAGo;
	private boolean rendering = false;
	private boolean rendered = false;
	
	public JoonsRenderer (PApplet parent){
		JRStatics.P=parent;
		JRStatics.FOV=((PGraphicsOpenGL) JRStatics.P.g).cameraFOV; //default value from Processing
		JRStatics.ASPECT=((PGraphicsOpenGL) JRStatics.P.g).cameraAspect; //default value from Processing
		recorder = new JRRecorder(); 
	}
	
	//rendering command interface
	public void beginRecord(){
		if(rendering) JRStatics.P.beginRecord(recorder);
	}

	public void endRecord(){
		if(rendering) {
			JRStatics.P.endRecord();
			rendering = false;
			rendered = renderSunflow();
		}
	}
	
	public void render(){
		rendering = true;
		rendered = false;
	}
	
	//image settings interface
	public void setSizeMultiplier(double multiplier) {
		JRStatics.SIZE_MULTIPLIER = multiplier;
	}

	public void setSampler(String sampler) {
		JRStatics.SAMPLER = sampler;
	}
	
	public void setAA(int aaMin, int aaMax) {
		JRStatics.AA_MIN = aaMin;
		JRStatics.AA_MAX = aaMax;
	}

	public void setAA(int aaMin, int aaMax, int aaSamples) {
		setAA(aaMin, aaMax);
		JRStatics.AA_SAMPLES = aaSamples;
	}

	public void setCaustics(int emitInMillions) {
		setCaustics(emitInMillions, 50 + 10 * emitInMillions - 5, 0.5f); //rule of thumb
	}
	
	public void setCaustics(int emitInMillions, int gather){
		setCaustics(emitInMillions, gather, 0.5f); //rule of thumb
	}

	public void setCaustics(int emitInMillions, int gather, float radius) {
		JRStatics.CAUSTICS_EMIT = emitInMillions * 1000000; // just to make life easier.
		JRStatics.CAUSTICS_GATHER = gather;
		JRStatics.CAUSTICS_RADIUS = radius;
	}

	public void setTraceDepths(int diff, int refl, int refr) {
		JRStatics.TRACE_DEPTH_DIFF = diff;
		JRStatics.TRACE_DEPTH_REFL = refl;
		JRStatics.TRACE_DEPTH_REFR = refr;
	}
	
	public void setDOF(float focalDistance, float lensRadius){
		JRStatics.FOCAL_DISTANCE = focalDistance;
		JRStatics.LENS_RADIUS = lensRadius; //larger the R, say 5, greater the DOF effect.
	}

	//background interface
	public void background(float gray){
		JRStatics.BG_R=gray/255f;
		JRStatics.BG_G=gray/255f;
		JRStatics.BG_B=gray/255f;
		JRStatics.P.background(gray);
	}
	
	public void background(float r, float g, float b){
		JRStatics.BG_R=r/255f;
		JRStatics.BG_G=g/255f;
		JRStatics.BG_B=b/255f;
		JRStatics.P.background(r,g,b);
	}
	
	public void background(String type){
		background(type, null);
	}
	
	public void background(String type, float...params){

		//gi, instant
		if(type==JRStatics.GI_INSTANT){
			if(params == null){
				JRStatics.GI_IS_CALLED=true;
			} else if (params.length==4){
				JRStatics.GI_IS_CALLED=true;
				JRStatics.GI_INSTANT_SAMPLES=(int) params[0];
				JRStatics.GI_INSTANT_SETS=(int) params[1];
				JRStatics.GI_INSTANT_C=params[2];
				JRStatics.GI_INSTANT_BIAS_SAMPLES=(int) params[3];
			} else {
				if(rendering) PApplet.println(JRStatics.GI_INSTANT_ERROR);
			}
		}
		
		//gi, ambient occlusion
		if(type==JRStatics.GI_AMB_OCC){
			if(params == null){
				JRStatics.GI_AMB_OCC_IS_CALLED=true;
			} else if (params.length == 8){
				JRStatics.GI_AMB_OCC_BRIGHT_R = params[0]/255f;
				JRStatics.GI_AMB_OCC_BRIGHT_G = params[1]/255f;
				JRStatics.GI_AMB_OCC_BRIGHT_B = params[2]/255f;
				JRStatics.GI_AMB_OCC_DARK_R = params[3]/255f;
				JRStatics.GI_AMB_OCC_DARK_G = params[4]/255f;
				JRStatics.GI_AMB_OCC_DARK_B = params[5]/255f;
				JRStatics.GI_AMB_OCC_MAX_DIST = params[6];
				JRStatics.GI_AMB_OCC_SAMPLES = (int) params[7];
			} else {
				if(rendering) PApplet.println(JRStatics.GI_AMB_OCC_ERROR);
			}
		}
		
		//cornell box
		if(type==JRStatics.CORNELL_BOX){
			if(params != null){
				if(params.length == 3) {
					cornellBox(params[0], params[1], params[2]);
				} else if(params.length == 7) {
					cornellBox(params[0], params[1], params[2], params[3], params[4], params[5], (int) params[6]);
				} else if(params.length == 22) {
					cornellBox(params[0], params[1], params[2], params[3], params[4], params[5], (int) params[6],
						params[7], params[8], params[9], params[10], params[11], params[12],
						params[13], params[14], params[15], params[16], params[17], params[18],
						params[19], params[20], params[21]);
				} else {
					if(rendering) PApplet.println(JRStatics.CORNELL_BOX_ERROR);
				}
			} else {
				if(rendering) PApplet.println(JRStatics.CORNELL_BOX_ERROR);
			}
		}
	}
	
	//shade interface
	public void fill(String type){
		if(type==JRStatics.LIGHT){
			fill(type, JRStatics.DEF_RADIANCE, JRStatics.DEF_RADIANCE, JRStatics.DEF_RADIANCE);
		} else {
			fill(type, JRStatics.DEF_RGB, JRStatics.DEF_RGB, JRStatics.DEF_RGB);
		}
	}
	
	public void fill(String type, float...params){
		if(type == JRStatics.CONSTANT || type == JRStatics.DIFFUSE || type == JRStatics.SHINY || type == JRStatics.MIRROR ||
				type == JRStatics.GLASS || type == JRStatics.PHONG || type == JRStatics.AMBIENT_OCCLUSION || type == JRStatics.LIGHT){
			JRStatics.fillers.add(new JRFiller(type, params));
		} else {
			if(rendering) PApplet.println(JRStatics.FILLER_UNKOWN_ERROR);
			JRStatics.FILLERS_ARE_VALID=false;
		}

		//First three parameters are always used as RGB. 
		//This can be used to give an idea about what the render may look like before render.
		//When showing light shaded objects in processing,
		//the r, g, b is normalized so that the highest value of them
		//is set to 255, and the rest is normalized in proportion.
		if(params.length >= 3){
			if(type == JRStatics.LIGHT){
				float r, g, b, max;
				r = params[0];
				g = params[1];
				b = params[2];
				max = PApplet.max(new float[] {r, g, b});
				JRStatics.P.fill(255*r/max, 255*g/max, 255*b/max);
			} else if (type == JRStatics.GLASS){
				JRStatics.P.fill(params[0],params[1],params[2],JRStatics.DEF_GLASS_ALPHA);
			} else {
				JRStatics.P.fill(params[0],params[1],params[2]);
			}
		}
	}
	
	//cornell box implementation
	private void cornellBox(float width, float height, float depth){
		cornellBox(width, height, depth, JRStatics.DEF_CORB_RADIANCE, JRStatics.DEF_CORB_RADIANCE, JRStatics.DEF_CORB_RADIANCE, JRStatics.DEF_SAMPLES);
	}
	
	private void cornellBox(float width, float height, float depth,
						   float radianceR,float radianceG, float radianceB, int samples){
		cornellBox(	width, height, depth,
					radianceR, radianceG, radianceB, samples,
					JRStatics.DEF_CORB_COLOR_1, JRStatics.DEF_CORB_COLOR_2, JRStatics.DEF_CORB_COLOR_2, JRStatics.DEF_CORB_COLOR_2, JRStatics.DEF_CORB_COLOR_2, JRStatics.DEF_CORB_COLOR_1,
					JRStatics.DEF_CORB_COLOR_1, JRStatics.DEF_CORB_COLOR_1, JRStatics.DEF_CORB_COLOR_1, JRStatics.DEF_CORB_COLOR_1, JRStatics.DEF_CORB_COLOR_1, JRStatics.DEF_CORB_COLOR_1,
					JRStatics.DEF_CORB_COLOR_1, JRStatics.DEF_CORB_COLOR_1, JRStatics.DEF_CORB_COLOR_1); //default vaules
	}
	
	private void cornellBox(float width, float height, float depth, 
							float radianceR, float radianceG, float radianceB, int samples,
							float leftR, float leftG, float leftB, float rightR, float rightG, float rightB, 
							float backR, float backG, float backB, float topR, float topG, float topB,
							float bottomR, float bottomG, float bottomB){
		float w = width/2;
		float h = height/2;
		float d = depth/2;
		
		//back up current filler
		String tempFillerType = JRStatics.getCurrentFiller().getType();
		float[] tempParams = JRStatics.getCurrentFiller().p;
		
		//-x side
		this.fill("diffuse", leftR, leftG, leftB);
		JRStatics.P.beginShape(PApplet.QUADS);
		JRStatics.P.vertex(-w,h,-d);
		JRStatics.P.vertex(-w,h,d);
		JRStatics.P.vertex(-w,-h,d);
		JRStatics.P.vertex(-w,-h,-d);
		JRStatics.P.endShape();
		
		//+x side
		this.fill("diffuse", rightR, rightG, rightB);
		JRStatics.P.beginShape(PApplet.QUADS);
		JRStatics.P.vertex(w,h,-d);
		JRStatics.P.vertex(w,h,d);
		JRStatics.P.vertex(w,-h,d);
		JRStatics.P.vertex(w,-h,-d);
		JRStatics.P.endShape();
		
		//back
		this.fill("diffuse", backR, backG, backB);
		JRStatics.P.beginShape(PApplet.QUADS);
		JRStatics.P.vertex(w,h,-d);
		JRStatics.P.vertex(w,-h,-d);
		JRStatics.P.vertex(-w,-h,-d);
		JRStatics.P.vertex(-w,h,-d);
		JRStatics.P.endShape();
		
		//bottom
		this.fill("diffuse", bottomR, bottomG, bottomB);
		JRStatics.P.beginShape(PApplet.QUADS);
		JRStatics.P.vertex(w,h,-d);
		JRStatics.P.vertex(w,h,d);
		JRStatics.P.vertex(-w,h,d);
		JRStatics.P.vertex(-w,h,-d);
		JRStatics.P.endShape();
		
		//ceiling rim
		this.fill("diffuse", topR, topG, topB);
		JRStatics.P.beginShape(PApplet.QUADS);
		JRStatics.P.vertex(w,-h,d);
		JRStatics.P.vertex(w,-h,-d);
		JRStatics.P.vertex(w/3f,-h,-d/3f);
		JRStatics.P.vertex(w/3f,-h,d/3f);
		
		JRStatics.P.vertex(w,-h,-d);
		JRStatics.P.vertex(-w,-h,-d);
		JRStatics.P.vertex(-w/3f,-h,-d/3f);
		JRStatics.P.vertex(w/3f,-h,-d/3f);
		
		JRStatics.P.vertex(-w,-h,-d);
		JRStatics.P.vertex(-w,-h,d);
		JRStatics.P.vertex(-w/3f,-h,d/3f);
		JRStatics.P.vertex(-w/3f,-h,-d/3f);
		
		JRStatics.P.vertex(-w,-h,d);
		JRStatics.P.vertex(w,-h,d);
		JRStatics.P.vertex(w/3f,-h,d/3f);
		JRStatics.P.vertex(-w/3f,-h,d/3f);
		JRStatics.P.endShape();
		
		//ceiling light
		this.fill("light", radianceR, radianceG, radianceB, samples);
		JRStatics.P.beginShape(PApplet.QUADS);
		JRStatics.P.vertex(w/3f,-h,d/3f);
		JRStatics.P.vertex(w/3f,-h,-d/3f);
		JRStatics.P.vertex(-w/3f,-h,-d/3f);
		JRStatics.P.vertex(-w/3f,-h,d/3f);
		JRStatics.P.endShape();
		
		//restore previous fill
		this.fill(tempFillerType, tempParams);
	}
	
	//rendering inteface
	private boolean renderSunflow() {
		PApplet.println(JRStatics.JR_VERSION_PRINT);
		checkSettings();		
		if (renderIsAGo) {
			//saves processing image to sketch folder
			JRStatics.P.saveFrame(JRStatics.UNRENDERED_FILE_NAME);
			
			//create & build sunflow renderer api
			createSunflowRenderer();
			if(buildSunflowRenderer()){
				
				//render using the created & built api
				JRImagePanel imagePanel = new JRImagePanel();
				api.render(SunflowAPI.DEFAULT_OPTIONS, imagePanel);
				JRStatics.IMG_RENDERED = imagePanel.getInversedImage();
				JRStatics.IMG_RENDERED.save(JRStatics.P.sketchPath(JRStatics.RENDERED_INV_FILE_NAME));				
				return true;
			}
		}
		return false;
	}
	
	private void checkSettings(){
		renderIsAGo = true;
		
		if(!JRStatics.FILLERS_ARE_VALID) renderIsAGo = false;
		
		if (!JRStatics.SAMPLER.equals(JRStatics.IPR) && !JRStatics.SAMPLER.equals(JRStatics.BUCKET)) {
			PApplet.println(JRStatics.IMAGE_SAMPLER_ERROR);
			renderIsAGo = false;
		}
		if (JRStatics.AA_MIN > 2 || JRStatics.AA_MIN < -2 || JRStatics.AA_MAX > 2 || JRStatics.AA_MAX < -2 || JRStatics.AA_MIN > JRStatics.AA_MAX) {
			PApplet.println(JRStatics.IMAGE_AA_ERROR);
			renderIsAGo = false;
		}
	}
	
	private void createSunflowRenderer() {
		//compiling sunflow api
		StringBuilder template = new StringBuilder();
		template.append("import org.sunflow.core.*;\n");
        template.append("import org.sunflow.core.accel.*;\n");
        template.append("import org.sunflow.core.camera.*;\n");
        template.append("import org.sunflow.core.primitive.*;\n");
        template.append("import org.sunflow.core.shader.*;\n");
        template.append("import org.sunflow.image.Color;\n");
        template.append("import org.sunflow.math.*;\n\n");
        template.append("public void build() {\n");
        template.append("}\n");
        String buildTemplate = template.toString();
        api = SunflowAPI.compile(buildTemplate);
	}

	private boolean buildSunflowRenderer() {		
		//image settings
		api.parameter("resolutionX", (int) (JRStatics.P.width*JRStatics.SIZE_MULTIPLIER));
		api.parameter("resolutionY", (int) (JRStatics.P.height*JRStatics.SIZE_MULTIPLIER));
		api.parameter("sampler", JRStatics.SAMPLER);	
		api.parameter("aa.min", JRStatics.AA_MIN);
		api.parameter("aa.max", JRStatics.AA_MAX);
		api.parameter("aa.samples", JRStatics.AA_SAMPLES);
		api.parameter("filter", "gaussian");
		api.options(SunflowAPI.DEFAULT_OPTIONS);
		
		//camera block
		//common settings
		//default setting for camera after viewModel transformation.
		api.parameter("transform", Matrix4.lookAt(	new Point3 (0, 0,  0), 
													new Point3 (0, 0, -1),
													new Vector3(0, 1,  0)));
		
		//compensating for the different ways Processing and Sunflow implement FOV.
		//Processing has mid-plane vertical FOV, whereas Sunflow has mid-plane horizontal FOV.
		float fovSunflow = 2*PApplet.atan(PApplet.tan(JRStatics.FOV/2f)*JRStatics.ASPECT)*360/(2*PApplet.PI);
		api.parameter("fov", fovSunflow);
		api.parameter("aspect", JRStatics.ASPECT);
		
		//individual camera block
		if(JRStatics.FOCAL_DISTANCE == -1){
			//pinhole camera
			api.camera("Camera_0", "pinhole");
			api.parameter("camera", "Camera_0");
			api.options(SunflowAPI.DEFAULT_OPTIONS);
			
		} else {			
			//thin lens camera
			api.parameter("focus.distance", JRStatics.FOCAL_DISTANCE);
			api.parameter("lens.radius", JRStatics.LENS_RADIUS);
			api.camera("Camera_0", "thinlens");
			api.parameter("camera", "Camera_0");
			api.options(SunflowAPI.DEFAULT_OPTIONS);
		}

        //caustics block
        api.parameter("caustics.emit", JRStatics.CAUSTICS_EMIT);
        api.parameter("caustics", "kd");
        api.parameter("caustics.gather", JRStatics.CAUSTICS_GATHER);
        api.parameter("caustics.radius", JRStatics.CAUSTICS_RADIUS);
        api.options(SunflowAPI.DEFAULT_OPTIONS);
        
		//trace depth block
        api.parameter("depths.diffuse", JRStatics.TRACE_DEPTH_DIFF);
        api.parameter("depths.reflection", JRStatics.TRACE_DEPTH_REFL);
        api.parameter("depths.refraction", JRStatics.TRACE_DEPTH_REFR);
        api.options(SunflowAPI.DEFAULT_OPTIONS);
        
        //global illumination block
        //gi, ambient occlusion
        if(JRStatics.GI_AMB_OCC_IS_CALLED){
	        api.parameter("gi.engine", "ambocc");
	        api.parameter("gi.ambocc.bright", null, new float[] {JRStatics.GI_AMB_OCC_BRIGHT_R, JRStatics.GI_AMB_OCC_BRIGHT_G, JRStatics.GI_AMB_OCC_BRIGHT_B});
	        api.parameter("gi.ambocc.dark", null, new float[] {JRStatics.GI_AMB_OCC_DARK_R, JRStatics.GI_AMB_OCC_DARK_G, JRStatics.GI_AMB_OCC_DARK_B});
	        api.parameter("gi.ambocc.samples", JRStatics.GI_AMB_OCC_SAMPLES);
	        api.parameter("gi.ambocc.maxdist", JRStatics.GI_AMB_OCC_MAX_DIST);
			api.options(SunflowAPI.DEFAULT_OPTIONS);
			
        } else if(!JRStatics.GI_AMB_OCC_IS_CALLED && JRStatics.GI_IS_CALLED){ //gi, instant
			api.parameter("gi.engine", "igi");
			api.parameter("gi.igi.samples", JRStatics.GI_INSTANT_SAMPLES);
			api.parameter("gi.igi.sets", JRStatics.GI_INSTANT_SETS);
			api.parameter("gi.igi.c", JRStatics.GI_INSTANT_C);
			api.parameter("gi.igi.bias_samples", JRStatics.GI_INSTANT_BIAS_SAMPLES);
			api.options(SunflowAPI.DEFAULT_OPTIONS);
			
		} else { //no gi
			api.parameter("gi.engine", "none");
			api.options(SunflowAPI.DEFAULT_OPTIONS);
		}
        
		//light block
		for(int i = 0; i < JRStatics.fillers.size(); i++){
			JRFiller temp = JRStatics.fillers.get(i);
			if(temp.getType() == JRStatics.LIGHT) {
				if (!buildLight(temp, i)) return false;
			}
		}
        
        //shader block
		for(int i = 0; i < JRStatics.fillers.size(); i++){
			JRFiller temp = JRStatics.fillers.get(i);
			if(temp.getType() != JRStatics.LIGHT) {
				if(!buildFiller(temp, i)) return false;
			}
		}		
        
        //instance block
		for(int i = 0; i < JRStatics.fillers.size(); i++){
			JRFiller temp = JRStatics.fillers.get(i);
			if(temp.getType() != JRStatics.LIGHT) buildInstance(temp, i); //should have the same condition as shader block
		}
		
		//background primitive
        api.parameter("color", null, new float[] {JRStatics.BG_R, JRStatics.BG_G, JRStatics.BG_B});
        api.shader("bg.shader", "constant");
        api.geometry("bg", "background");
        api.parameter("shaders", "bg.shader");
        api.instance("bg.instance", "bg");
        
        return true;
	}
	
	
	private boolean buildLight(JRFiller temp, int i) {
		//light mesh
		if(temp.np == 3 || temp.np == 4){
			api.parameter("radiance", null, new float[] {temp.p[0], temp.p[1], temp.p[2]});
			if(temp.np == 4) api.parameter("samples", (int) temp.p[3]);
			api.parameter("points", "point", "vertex", temp.verticesToArray());
			api.parameter("triangles", temp.triangleIndicesToArray());
			api.light("Shader_"+i, "triangle_mesh");
		} else {
			PApplet.println(JRStatics.FILLER_LIGHT_ERROR);
			return false;
		}
		
		//light spheres
		ArrayList<Float> spheres = temp.getSpheres();
		int noOfSpheres = (int) spheres.size()/4;
		
		for (int j = 0; j < noOfSpheres; j++) {
			float x = spheres.get(j * 4);
			float y = spheres.get(j * 4 + 1);
			float z = spheres.get(j * 4 + 2);
			float r = spheres.get(j * 4 + 3);
			
			if(temp.np == 3 || temp.np == 4){
				api.parameter("radiance", null, new float[] {temp.p[0], temp.p[1], temp.p[2]});
				api.parameter("center", new Point3 (x, y, z));
				api.parameter("radius", r);
				if(temp.np == 4) api.parameter("samples", (int) temp.p[3]);
				api.light("SphereLight_" + i + "_" + j, "sphere");
			} else {
				return false;
			}
		}
		return true;
	}
	
	private boolean buildFiller(JRFiller temp, int i){
		//constant shader		
		if (temp.getType() == JRStatics.CONSTANT) {
			if (temp.np == 3) {
				api.parameter("color", JRStatics.SRGB_NONLINEAR, temp.p[0] / 255f, temp.p[1] / 255f, temp.p[2] / 255f);
				api.shader("Shader_" + i, "constant");
			} else {
				PApplet.println(JRStatics.FILLER_CONSTANT_ERROR);
				return false;
			}
		}
		
		//diffuse shader
		if (temp.getType() == JRStatics.DIFFUSE) {
			if (temp.np == 3) {
				api.parameter("diffuse", JRStatics.SRGB_NONLINEAR, temp.p[0] / 255f, temp.p[1] / 255f, temp.p[2] / 255f);
				api.shader("Shader_" + i, "diffuse");
			} else {
				PApplet.println(JRStatics.FILLER_DIFFUSE_ERROR);
				return false;
			}
		}
		
		//shiny shader
		if (temp.getType() == JRStatics.SHINY) {
			if (temp.np == 3 || temp.np == 4) {
				api.parameter("diffuse", JRStatics.SRGB_NONLINEAR, temp.p[0] / 255f, temp.p[1] / 255f, temp.p[2] / 255f);
				if(temp.np==4) api.parameter("shiny", temp.p[3]);
				api.shader("Shader_" + i, "shiny_diffuse");
			} else {
				PApplet.println(JRStatics.FILLER_SHINY_ERROR);
				return false;
			}
		}
		
		//mirror shader
		if (temp.getType() == JRStatics.MIRROR) {
			if (temp.np == 3) {
				api.parameter("color", JRStatics.SRGB_NONLINEAR, temp.p[0] / 255f, temp.p[1] / 255f, temp.p[2] / 255f);
				api.shader("Shader_" + i, "mirror");
			} else {
				PApplet.println(JRStatics.FILLER_MIRROR_ERROR);
				return false;
			}
		}
		
		//glass shader
		if (temp.getType() == JRStatics.GLASS) {
			if(temp.np == 3 || temp.np == 4 || temp.np == 8){
				api.parameter("color", JRStatics.SRGB_NONLINEAR, temp.p[0] / 255f, temp.p[1] / 255f, temp.p[2] / 255f);
				if(temp.np >= 4) api.parameter("eta", temp.p[3]); // index of refraction, IOR, def 1.6f
				if(temp.np == 8) {
					api.parameter("absorption.distance", temp.p[4]); // def 5
					api.parameter("absorption.color", JRStatics.SRGB_NONLINEAR, temp.p[5] / 255f, temp.p[6] / 255f, temp.p[7] / 255f);
				}
				api.shader("Shader_" + i, "glass");
			} else {
				PApplet.println(JRStatics.FILLER_GLASS_ERROR);
				return false;
			}
		}
		
		//phong shader
		if (temp.getType() == JRStatics.PHONG) {
			if (temp.np == 3 || temp.np == 6 || temp.np == 8 ) {
				api.parameter("diffuse", JRStatics.SRGB_NONLINEAR, new float[] {temp.p[0]/255f, temp.p[1]/255f, temp.p[2]/255f});
	            if (temp.np >= 6) api.parameter("specular", JRStatics.SRGB_NONLINEAR, new float[] {temp.p[3]/255f, temp.p[4]/255f, temp.p[5]/255f});
	            if (temp.np == 8) {
	            	api.parameter("power", temp.p[6]);
	                api.parameter("samples", (int) temp.p[7]);
	            }
                api.shader("Shader_"+i, "phong");
			} else {
				PApplet.println(JRStatics.FILLER_PHONG_ERROR);
				return false;
			}
		}
		
		//ambient occlusion shader
		if (temp.getType() == JRStatics.AMBIENT_OCCLUSION) {
			if (temp.np == 3 || temp.np == 8){
				api.parameter("bright", JRStatics.SRGB_NONLINEAR, new float[] {temp.p[0]/255f, temp.p[1]/255f, temp.p[2]/255f});
				if (temp.np != 8 ) api.parameter("maxdist", JRStatics.DEF_AMB_OCC_MAX_DIST);
				if (temp.np == 8) {
					api.parameter("dark", JRStatics.SRGB_NONLINEAR, new float[] {temp.p[3]/255f, temp.p[4]/255f, temp.p[5]/255f});
					api.parameter("maxdist", temp.p[6]);
					api.parameter("samples", (int) temp.p[7]);
				}
				api.shader("Shader_"+i, "ambient_occlusion");
			} else {
				PApplet.println(JRStatics.FILLER_AMB_OCC_ERROR);
				return false;
			}
		}		
		return true;
	}
	
	private void buildInstance(JRFiller temp, int i){
		//render the respective objects with the above defined shaders
		
		//generic mesh method
		api.parameter("points", "point", "vertex", temp.verticesToArray()); //np is the number of points, or vertices
		api.parameter("triangles", temp.triangleIndicesToArray()); //nt is the number of triangle faces.
		api.geometry("Object_"+i, "triangle_mesh");	
		api.parameter("shaders", "Shader_" + i);
		api.instance("Object_"+i + ".instance", "Object_"+i);
		
		//render the respective spheres
		ArrayList<Float> spheres = temp.getSpheres();
		int noOfSpheres = spheres.size()/4;
		
		for (int j = 0; j < noOfSpheres; j++) {
			float x = spheres.get(j * 4);
			float y = spheres.get(j * 4 + 1);
			float z = spheres.get(j * 4 + 2);
			float r = spheres.get(j * 4 + 3);

			Matrix4 translate = Matrix4.IDENTITY.multiply(Matrix4.translation(x, y, z));
			Matrix4 scale = Matrix4.IDENTITY.multiply(Matrix4.scale(r, r, r));

			Matrix4 m = Matrix4.IDENTITY;
			m = scale.multiply(m);
			m = translate.multiply(m);

			api.geometry("Sphere_" + i + "_" + j, "sphere");
			api.parameter("shaders", "Shader_" + i);
			api.parameter("transform", m);
			api.instance("Sphere_" + i + "_" + j + ".instance", "Sphere_" + i + "_" + j);
		}
	}
	
	public void displayRendered(boolean displaySwitch) {
		//the below are to reset the display before displaying the rendered image
		if(rendered && displaySwitch){
			JRStatics.P.background(255);
			JRStatics.P.noLights();
			JRStatics.P.camera();
			JRStatics.P.perspective();
			JRStatics.P.image(JRStatics.IMG_RENDERED, 0, 0, JRStatics.P.width, JRStatics.P.height);
		}
	}
}
