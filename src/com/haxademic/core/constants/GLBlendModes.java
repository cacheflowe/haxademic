package com.haxademic.core.constants;

import com.jogamp.opengl.GL;

import processing.core.PGraphics;
import processing.opengl.PJOGL;

public class GLBlendModes {

	// use:
	// glEnable(GL_BLEND);
	// glBlendFunc(GLBlendModes.FN_SRC_COLOR, GLBlendModes.FN_SRC_COLOR);
	// glBlendEquation(GL_MIN);

	// functions
	public static int FN_GL_ZERO = GL.GL_ZERO;
	public static int FN_GL_ONE = GL.GL_ONE;
	public static int FN_SRC_COLOR = GL.GL_SRC_COLOR;
	public static int FN_ONE_MINUS_SRC_COLOR = GL.GL_ONE_MINUS_SRC_COLOR;
	public static int FN_DST_COLOR = GL.GL_DST_COLOR;
	public static int FN_ONE_MINUS_DST_COLOR = GL.GL_ONE_MINUS_DST_COLOR;
	public static int FN_SRC_ALPHA = GL.GL_SRC_ALPHA;
	public static int FN_ONE_MINUS_SRC_ALPHA = GL.GL_ONE_MINUS_SRC_ALPHA;
	public static int FN_DST_ALPHA = GL.GL_DST_ALPHA;
	public static int FN_ONE_MINUS_DST_ALPHA = GL.GL_ONE_MINUS_DST_ALPHA;
	public static int FN_SRC_ALPHA_SATURATE = GL.GL_SRC_ALPHA_SATURATE;

	// equations
	public static int EQ_FUNC_ADD = GL.GL_FUNC_ADD;
	public static int EQ_FUNC_SUBTRACT = GL.GL_FUNC_SUBTRACT;
	public static int EQ_FUNC_REVERSE_SUBTRACT = GL.GL_FUNC_REVERSE_SUBTRACT;

	// collections
	public static int[] blendFunctions = new int[] {
			GLBlendModes.FN_GL_ZERO,
			GLBlendModes.FN_GL_ONE,
			GLBlendModes.FN_SRC_COLOR,
			GLBlendModes.FN_ONE_MINUS_SRC_COLOR,
			GLBlendModes.FN_DST_COLOR,
			GLBlendModes.FN_ONE_MINUS_DST_COLOR,
			GLBlendModes.FN_SRC_ALPHA,
			GLBlendModes.FN_ONE_MINUS_SRC_ALPHA,
			GLBlendModes.FN_DST_ALPHA,
			GLBlendModes.FN_ONE_MINUS_DST_ALPHA,
			GLBlendModes.FN_SRC_ALPHA_SATURATE,
	};

	public static int[] blendEquations = new int[] {
			GLBlendModes.EQ_FUNC_ADD,
			GLBlendModes.EQ_FUNC_REVERSE_SUBTRACT,
			GLBlendModes.EQ_FUNC_SUBTRACT,
	};

	public static void setBlendModeFromPreset(PGraphics pg, int presetIndex) {
		int[] preset = presets[presetIndex];
		GL gl = ((PJOGL)pg.beginPGL()).gl.getGL();
		gl.glBlendFunc(GLBlendModes.blendFunctions[preset[0]], GLBlendModes.blendFunctions[preset[1]]);
		gl.glBlendEquation(GLBlendModes.blendEquations[preset[2]]);
	}

	public static void setBlendModeFromPresetNoAlpha(PGraphics pg, int presetIndex) {
		int[] preset = presetsNoAlpha[presetIndex];
		GL gl = ((PJOGL)pg.beginPGL()).gl.getGL();
		gl.glBlendFunc(GLBlendModes.blendFunctions[preset[0]], GLBlendModes.blendFunctions[preset[1]]);
		gl.glBlendEquation(GLBlendModes.blendEquations[preset[2]]);
	}
	
	// to be used without alpha
	public static int[][] presetsNoAlpha = new int[][] {
		new int[] {7, 2, 1},
		new int[] {6, 3, 2},
		new int[] {3, 8, 1},
		new int[] {2, 4, 0},
		new int[] {8, 3, 2},
		new int[] {1, 8, 0},
		new int[] {1, 1, 0},
		new int[] {3, 1, 0},
		new int[] {6, 3, 2},
		new int[] {4, 7, 2},
		new int[] {2, 3, 0},
		new int[] {1, 2, 0},
		new int[] {0, 2, 0},
		new int[] {5, 8, 0},
		new int[] {3, 1, 1},
		new int[] {6, 4, 0},
		new int[] {6, 3, 2},
		new int[] {2, 6, 0},
		new int[] {3, 6, 1},
		new int[] {8, 3, 0},
		new int[] {5, 2, 1},
		new int[] {3, 6, 0},
		new int[] {6, 5, 2},
		new int[] {4, 7, 0},
		new int[] {10, 2, 0},
		new int[] {2, 6, 0},
		new int[] {0, 2, 1},
		new int[] {1, 4, 0},
		new int[] {8, 4, 0},
		new int[] {2, 4, 0},
		new int[] {6, 4, 0},
		new int[] {5, 2, 1},
		new int[] {9, 2, 1},
		new int[] {6, 3, 2},
		new int[] {4, 5, 0},
		new int[] {5, 1, 0},
		new int[] {8, 1, 0},
		new int[] {4, 3, 2},
		new int[] {6, 3, 2},
		new int[] {5, 1, 0},
		new int[] {3, 6, 0},
		new int[] {1, 5, 0},
		new int[] {4, 2, 0},
		new int[] {5, 10, 0},
		new int[] {4, 0, 2},
		new int[] {5, 4, 0},
		new int[] {8, 5, 0},
		new int[] {1, 8, 0},
		new int[] {6, 2, 0},
		new int[] {6, 3, 0},
		new int[] {3, 6, 1},
		new int[] {5, 4, 0},
		new int[] {8, 6, 0},
		new int[] {3, 4, 1},
		new int[] {3, 4, 0},
		new int[] {6, 4, 0},
		new int[] {5, 8, 0},
		new int[] {2, 1, 0},
		new int[] {6, 8, 0},
		new int[] {3, 2, 1},
		new int[] {0, 2, 1},
		new int[] {6, 3, 2},
		new int[] {1, 6, 0},
		new int[] {3, 8, 1},
		new int[] {6, 4, 0},
		new int[] {3, 1, 0},
		new int[] {6, 6, 0},
		new int[] {7, 2, 0},
		new int[] {6, 6, 0},
		new int[] {1, 6, 0},
		new int[] {9, 2, 1},
		new int[] {4, 0, 2},
		new int[] {6, 3, 2},
		new int[] {4, 7, 0},
		new int[] {4, 9, 0},
		new int[] {7, 2, 1},
		new int[] {4, 3, 2},
		new int[] {4, 7, 2},
		new int[] {5, 2, 1},
		new int[] {0, 2, 1},
		new int[] {0, 2, 1},
		new int[] {3, 2, 1},
		new int[] {2, 5, 0},
		new int[] {4, 7, 2},
		new int[] {5, 8, 0},
		new int[] {7, 2, 0},
	};

	// work best with lower alpha transparencyon fill or image
	public static int[][] presets = new int[][] {
		new int[] {2, 3, 0},
		new int[] {2, 6, 2},
		new int[] {9, 7, 0},
		new int[] {5, 0, 0},
		new int[] {6, 9, 0},
		new int[] {1, 0, 2},
		new int[] {3, 8, 0},
		new int[] {2, 7, 0},
		new int[] {8, 2, 2},
		new int[] {6, 2, 0},
		new int[] {7, 9, 0},
		new int[] {2, 4, 0},
		new int[] {8, 0, 2},
		new int[] {9, 5, 0},
		new int[] {3, 1, 0},
		new int[] {2, 1, 0},
		new int[] {2, 1, 2},
		new int[] {6, 8, 0},
		new int[] {8, 4, 0},
		new int[] {1, 1, 2},
		new int[] {8, 5, 0},
		new int[] {5, 9, 0},
		new int[] {9, 2, 0},
		new int[] {3, 0, 2},
		new int[] {8, 8, 2},
		new int[] {7, 7, 2},
		new int[] {2, 9, 0},
		new int[] {5, 4, 0},
		new int[] {8, 2, 0},
		new int[] {5, 7, 2},
		new int[] {5, 8, 0},
		new int[] {9, 2, 0},
		new int[] {1, 7, 0},
		new int[] {8, 6, 0},
		new int[] {7, 2, 2},
		new int[] {1, 2, 0},
		new int[] {9, 9, 0},
		new int[] {1, 9, 0},
		new int[] {5, 3, 0},
		new int[] {1, 7, 0},
		new int[] {3, 0, 2},
		new int[] {6, 8, 0},
		new int[] {3, 5, 0},
		new int[] {3, 1, 2},
		new int[] {7, 5, 2},
		new int[] {6, 9, 0},
		new int[] {7, 4, 0},
		new int[] {1, 8, 2},
		new int[] {9, 6, 2},
		new int[] {2, 6, 0},
		new int[] {8, 2, 2},
		new int[] {7, 1, 0},
		new int[] {3, 9, 2},
		new int[] {8, 1, 0},
		new int[] {9, 5, 0},
		new int[] {3, 4, 0},
		new int[] {1, 9, 0},
		new int[] {8, 0, 0},
		new int[] {1, 2, 0},
		new int[] {9, 10, 2},
		new int[] {8, 8, 0},
		new int[] {3, 3, 2},
		new int[] {2, 6, 2},
		new int[] {5, 1, 0},
		new int[] {1, 6, 0},
		new int[] {3, 2, 2},
		new int[] {8, 6, 0},
		new int[] {5, 6, 2},
		new int[] {3, 1, 2},
	};
}
