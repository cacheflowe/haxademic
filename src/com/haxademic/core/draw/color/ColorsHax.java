package com.haxademic.core.draw.color;

public class ColorsHax {

	public static int WHITE = 0xffffffff;
	public static int WHITE_CLEAR = 0x00ffffff;
	public static int BLACK = 0xff000000;
	public static int BLACK_CLEAR = 0x00000000;
	
	public static int TITLE_BG = 0xff666666;
	public static int BUTTON_BG = 0xff000000;
	public static int BUTTON_BG_HOVER = 0xff3B1854;
	public static int BUTTON_BG_PRESS = 0xff666666;
	public static int BUTTON_BG_SELECTED = 0xff007700;
	public static int BUTTON_TEXT = 0xffffffff;
	public static int BUTTON_OUTLINE = 0xffffffff;
	public static int BUTTON_OUTLINE_HOVER = 0xffeeeeee;

	public static int[][] COLOR_GROUPS = new int[][] {
		new int[] {0xff9c2c63, 0xffd073a2, 0xffe5a8ff, 0xfffffde2, 0xffcebf6d},
		new int[] {0xffc67dff, 0xff807bbf, 0xff4e6c7f, 0xff40a293, 0xffab322a},
		new int[] {0xfff2f2f2, 0xfff4d88b, 0xffdcb88e, 0xffc3844c, 0xffab322a},
		new int[] {0xff5b1f37, 0xfff469f3, 0xfff4a4f3, 0xfff3d9f3, 0xff0d0d0d},
		new int[] {0xffcfd9d6, 0xffbeb5b5, 0xfff6d68b, 0xffedc3b8, 0xfffaf3e3},
		new int[] {0xff2d2e47, 0xffcdf200, 0xffd9eabe, 0xffe1efff, 0xffd74400},
		new int[] {0xff624b33, 0xfffff8e2, 0xffffe86c, 0xffa4fef9, 0xffffcebb},
		new int[] {0xff232522, 0xffd27f83, 0xffdd9b8a, 0xffd8e1bc, 0xffe8fffb},
		new int[] {0xff02ffff, 0xff00fa00, 0xfffdfd04, 0xfffd5002, 0xfffe007c},
		new int[] {0xffd865fe, 0xff00fffe, 0xff28fb00, 0xfffeff02, 0xffff00ff},
		new int[] {0xff02b8ff, 0xfffffa02, 0xffff0091, 0xff858585, 0xffc182f4},
		new int[] {0xffe9b000, 0xffd1ff36, 0xffff6409, 0xffeb00aa, 0xff4c00ff},
		new int[] {0xffe2ea63, 0xffffdd51, 0xff00ff00, 0xff72e9b7, 0xff83e4ff},
		new int[] {0xff8eff0a, 0xff00ff00, 0xff3531ff, 0xff00bfff, 0xff350076},
		new int[] {0xffff006a, 0xfffffa00, 0xff0085ff, 0xffc2b6ae, 0xffffffff},
		new int[] {0xffff61cd, 0xff67009c, 0xffcbff00, 0xff656565, 0xffcccccc},
		new int[] {0xfffbfbf9, 0xff00e800, 0xff008d00, 0xff005a00, 0xff002800},
		new int[] {0xff31cbff, 0xfff6e098, 0xffff5734, 0xff9a39eb, 0xff58f74a},
		new int[] {0xffa91ae5, 0xfffff600, 0xff81ff00, 0xffea27c1, 0xff000000},
		new int[] {0xffdc1a27, 0xfff5328f, 0xff008c7f, 0xffdef427, 0xffed8100},
		new int[] {0xff264653, 0xff2A9D8F, 0xffE9C46A, 0xffF4A261, 0xffE76F51},
		new int[] {0xffEF476F, 0xffFFD166, 0xff06D6A0, 0xff118AB2, 0xff073B4C},
		new int[] {0xff011627, 0xffFDFFFC, 0xff2EC4B6, 0xffE71D36, 0xffFF9F1C},
		new int[] {0xff0081A7, 0xff00AFB9, 0xffFDFCDC, 0xffFED9B7, 0xffF07167},
		new int[] {0xff1A535C, 0xff4ECDC4, 0xffF7FFF7, 0xffFF6B6B, 0xffFFE66D},
		new int[] {0xff3D348B, 0xff7678ED, 0xffF7B801, 0xffF18701, 0xffF35B04},
		new int[] {0xff0D3B66, 0xffFAF0CA, 0xffF4D35E, 0xffEE964B, 0xffF95738},
		new int[] {0xffCFDBD5, 0xffE8EDDF, 0xffF5CB5C, 0xff242423, 0xff333533},
		new int[] {0xff001524, 0xff15616D, 0xffFFECD1, 0xffFF7D00, 0xff78290F},
		new int[] {0xffDDFFF7, 0xff93E1D8, 0xffFFA69E, 0xffAA4465, 0xff861657},
		new int[] {0xff156064, 0xff00C49A, 0xffF8E16C, 0xffFFC2B4, 0xffFB8F67},
		new int[] {0xff227C9D, 0xff17C3B2, 0xffFFCB77, 0xffFEF9EF, 0xffFE6D73},
		new int[] {0xff432371, 0xff714674, 0xff9F6976, 0xffCC8B79, 0xffFAAE7B},
		new int[] {0xffD6D6D6, 0xffFFEE32, 0xffFFD100, 0xff202020, 0xff333533},
	};
	
	// get more from Coolors.co by running this, then clicking on the info box. check the console for output
	/*
	document.body.addEventListener('click', (e) => {
	  let colorContainer = e.target.closest('.explore-palette');
	  if(colorContainer) {
	    let colorHexSpans = colorContainer.querySelectorAll('.explore-palette_colors span');
	    let colorCodeStr = "new int[] {"; 
	    colorHexSpans.forEach((el, i) => {
	      colorCodeStr += '0xff' + el.innerText;
	      if(i < colorHexSpans.length - 1) colorCodeStr += ', ';
	    });
	    colorCodeStr += '}';
	    console.log(colorCodeStr);
	  }
	})
	*/
	
	public static int[] colorGroupAt(int group) {
		int groupLooped = group % COLOR_GROUPS.length;
		return COLOR_GROUPS[groupLooped];
	}
	
	public static int colorFromGroupAt(int group, int color) {
		int groupLooped = group % COLOR_GROUPS.length;
		int[] colorGroup = COLOR_GROUPS[groupLooped];
		return colorGroup[color % colorGroup.length];
	}
	
	public static int PRIDE[] = new int[]{
		0xFFFF0000,
		0xFFFF4A00,
		0xFFFFFF08,
		0xFF006F08,
		0xFF0000FB,
		0xFF350074,
		0xFFB217FE
	};

}
