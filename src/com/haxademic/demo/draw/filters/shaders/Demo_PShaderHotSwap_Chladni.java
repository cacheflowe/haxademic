package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

public class Demo_PShaderHotSwap_Chladni
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShaderHotSwap shader;

	protected String p1_0 = "p1_0";
	protected String p1_1 = "p1_1";
	protected String p1_2 = "p1_2";
	protected String p1_3 = "p1_3";

	protected String p2_0 = "p2_0";
	protected String p2_1 = "p2_1";
	protected String p2_2 = "p2_2";
	protected String p2_3 = "p2_3";

	protected String time = "time";
	protected String zoom = "zoom";
	protected String thickness = "thickness";

	protected String[] configs = new String[] {
		"""
			{ "p1_0": 80.235214, "p1_1": -72.60891, "p1_2": -85.92287, "p1_3": -86.484436, "p2_0": 39.54245, "p2_1": 76.31766, "p2_2": -55.103233, "p2_3": -2.1341782, "time": 17.71787, "zoom": 0.15, "thickness": 0.981 } 		
		""",
		"""
			{ "p1_0": 68.8107, "p1_1": 45.198734, "p1_2": -49.86623, "p1_3": -98.30886, "p2_0": -57.957222, "p2_1": 4.6936874, "p2_2": -30.646774, "p2_3": -69.47304, "time": 18.07322, "zoom": 0.105000004, "thickness": 1.0 }
		""",
		"""
			{ "p1_0": -2.9285583, "p1_1": -10.787239, "p1_2": 1.5929947, "p1_3": -97.16021, "p2_0": -23.220076, "p2_1": -68.35, "p2_2": 94.09999, "p2_3": 23.139315, "time": 23.35032, "zoom": 0.33185497, "thickness": 0.8388619 }
		""",
		"""
			{ "p1_0": 13.47144, "p1_1": -10.787239, "p1_2": 1.5929947, "p1_3": -97.16021, "p2_0": 26.650007, "p2_1": -55.8, "p2_2": 100.0, "p2_3": 23.139315, "time": 22.970327, "zoom": 0.26999992, "thickness": 0.8388619 }
		""",
		"""
			{ "p1_0": 9.549994, "p1_1": 8.300006, "p1_2": -0.3999741, "p1_3": -15.850005, "p2_0": -1.5500036, "p2_1": -1.0999991, "p2_2": -23.199997, "p2_3": -64.399994, "time": 8.950005, "zoom": 0.3500001, "thickness": 0.107999995 }		
		""",
		"""
			{ "p1_0": -28.562225, "p1_1": 10.358658, "p1_2": 79.03934, "p1_3": 20.89019, "p2_0": 53.07315, "p2_1": -58.83672, "p2_2": 77.942, "p2_3": 28.688915, "time": 5.5082154, "zoom": 0.22534758, "thickness": 0.67199993 }
		""",
		"""
			{ "p1_0": -100.0, "p1_1": -11.036792, "p1_2": 100.0, "p1_3": 10.113383, "p2_0": 51.586315, "p2_1": 75.76989, "p2_2": -3.8729947, "p2_3": 76.135826, "time": 64.19911, "zoom": 0.14070053, "thickness": 0.97099996 }
		""",
		"""
			{ "p1_0": 10.19709, "p1_1": 42.965286, "p1_2": 49.84018, "p1_3": -38.400005, "p2_0": -69.48636, "p2_1": 45.955154, "p2_2": 35.608368, "p2_3": 98.899994, "time": 71.86828, "zoom": 0.16400002, "thickness": 0.985925 }
		""",
		"""
			{ "p1_0": 53.09189, "p1_1": -34.700996, "p1_2": 49.2381, "p1_3": 26.724495, "p2_0": 0.1840744, "p2_1": 9.987022, "p2_2": -74.362236, "p2_3": -93.6294, "time": 38.77263, "zoom": 0.29970825, "thickness": 0.5327569 }
		""",
		"""
			{ "p1_0": 62.85254, "p1_1": 45.544464, "p1_2": 28.510147, "p1_3": 2.086029, "p2_0": -72.332275, "p2_1": -43.693523, "p2_2": -81.56378, "p2_3": 91.32439, "time": 28.435856, "zoom": 0.28918535, "thickness": 0.60118645 }
		""",
		"""
			{ "p1_0": -21.335976, "p1_1": 81.41576, "p1_2": 63.22078, "p1_3": -57.599403, "p2_0": 36.793842, "p2_1": -62.55, "p2_2": 79.93561, "p2_3": 75.850006, "time": 6.722543, "zoom": 0.25399998, "thickness": 1.3432287 }
		""",
		"""
			{ "p1_0": 8.013985, "p1_1": 69.37198, "p1_2": -82.29465, "p1_3": -87.32589, "p2_0": 34.13443, "p2_1": -7.421234, "p2_2": -83.69481, "p2_3": -46.706665, "time": 49.776913, "zoom": 0.27778217, "thickness": 2.3644316 }
		""",
		"""
			{ "p1_0": 29.79335, "p1_1": -11.514061, "p1_2": -75.40792, "p1_3": -93.99252, "p2_0": 58.739273, "p2_1": -82.170044, "p2_2": -81.07218, "p2_3": 4.8671145, "time": 57.0337, "zoom": 0.21300003, "thickness": 2.3587487 }
		""",
	};
	protected int configsIndex = 0;
	
	protected void firstFrame() {
		// load shader
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/textures/chladni.glsl"));

		// init UI
		UI.addSlider(p1_0, 1, -100, 100, 0.05f);
		UI.addSlider(p1_1, 1, -100, 100, 0.05f);
		UI.addSlider(p1_2, 7, -100, 100, 0.05f);
		UI.addSlider(p1_3, 2, -100, 100, 0.05f);
		UI.addSlider(p2_0, -2, -100, 100, 0.05f);
		UI.addSlider(p2_1, 1, -100, 100, 0.05f);
		UI.addSlider(p2_2, 4, -100, 100, 0.05f);
		UI.addSlider(p2_3, 4.6f, -100, 100, 0.05f);

		UI.addSlider(time, 0, 0, 100, 0.01f);
		UI.addSlider(zoom, 0.3f, 0.05f, 0.5f, 0.001f);
		UI.addSlider(thickness, 0.15f, 0, 4, 0.001f);
	}
	
	protected void drawApp() {
		if(p.frameCount == 1) PG.setTextureRepeat(p.g, true);
		p.background(0);

		// update shader
		shader.update();
		shader.shader().set("time", UI.valueEased(time));
		shader.shader().set("zoom", UI.valueEased(zoom));
		shader.shader().set("thickness", UI.valueEased(thickness));
		shader.shader().set("s1", 
			UI.valueEased(p1_0),
			UI.valueEased(p1_1),
			UI.valueEased(p1_2),
			UI.valueEased(p1_3)
		); 
		shader.shader().set("s2", 
			UI.valueEased(p2_0),
			UI.valueEased(p2_1),
			UI.valueEased(p2_2),
			UI.valueEased(p2_3)
		); 
		p.filter(shader.shader());
		shader.showShaderStatus(p.g);

		// keyboard commands
		if(KeyboardState.keyTriggered('o')) P.out(UI.valuesToJSON());
		if(KeyboardState.keyTriggered(' ')) {
			configsIndex++;
			configsIndex = configsIndex % configs.length;
			UI.loadValuesFromJSON(configs[configsIndex]);
			DebugView.setValue("configsIndex", configsIndex);
			// 5/6, 1/2, 
		}
		if(KeyboardState.keyTriggered('r')) {
			UI.setRandomValue(time);
			UI.setRandomValue(zoom);
			UI.setRandomValue(thickness);
			UI.setRandomValue(p1_0);
			UI.setRandomValue(p1_1);
			UI.setRandomValue(p1_2);
			UI.setRandomValue(p1_3);
			UI.setRandomValue(p2_0);
			UI.setRandomValue(p2_1);
			UI.setRandomValue(p2_2);
			UI.setRandomValue(p2_3);
		}
	}

}
