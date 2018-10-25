// from: https://www.shadertoy.com/view/4lBBRz

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float thresholdLow = .1;
uniform float thresholdHigh = .9;


//"Random" function borrowed from The Book of Shaders: Random
float random ( vec2 xy ) {
    return fract( sin( dot( xy.xy, vec2(12, 78) ) ) );
}

float luminance(vec4 color)
{
    return ( (color.r * 0.3) + (color.g * 0.6) + (color.b * 0.1) ) * color.a;
}

// Returns the y coordinate of the first pixel that is brighter than the threshold
float getFirstThresholdPixel(vec2 xy, float threshold, vec2 resolution)
{
	float luma = luminance( texture2D( texture, xy / resolution.xy ) );

	//Looking at every sequential pixel is very resource intensive,
	//thus, we'll increment the inspected pixel by dividing the image height in sections,
	//and add a little randomness across the x axis to hide the division of said sections
    float increment = resolution.y / (30.0 + (random( xy.xx ) * 6.0));

    //Check if the luminance of the current pixel is brighter than the threshold,
    //if not, check the next pixel
	while( luma <= threshold )
	{
		xy.y -= increment;
        if( xy.y <= 0.0 ) return 0.0;
		luma = luminance( texture2D( texture, xy / resolution.xy ) );
	}

	return xy.y;
}

//Puts 10 pixels in an array
vec4[10] putItIn( vec2 startxy, float size, vec4 colorarray[10], vec2 resolution )
{
    vec2 xy;
    int j;

    for( j = 9; j >= 0; --j )
    {
        //Divide the line of pixels into 10 sections,
        //then store the pixel found at the junction of each section
        xy = vec2(startxy.x, startxy.y + (size / 9.0) * float(j));

        colorarray[j] = texture2D( texture, xy / resolution.xy );
    }

    return colorarray;
}

//An attempt at Bubble sort for 10 pixels, sorting them from darkest to brightest, top to bottom
vec4[10] sortArray(vec4 colorarray[10])
{
    vec4 tempcolor;
    int j;
    int swapped = 1;

    while( swapped > 0 )
    {
      swapped = 0;
		  for( j = 9; j > 0; --j )
    	{
        	if( luminance(colorarray[j]) > luminance(colorarray[j - 1]) )
        	{
            	tempcolor = colorarray[j];
            	colorarray[j] = colorarray[j - 1];
            	colorarray[j - 1] = tempcolor;

            	++swapped;
        	}
    	}
    }

    return colorarray;
}

void main() {
    vec2 resolution = vec2(1./texOffset.x, 1./texOffset.y);
    vec2 uv = vertTexCoord.xy * resolution;

	  float firsty = getFirstThresholdPixel( vec2(uv.x, resolution.y), thresholdLow, resolution );
    float secondy = getFirstThresholdPixel( vec2(uv.x, firsty) - 1.0, thresholdHigh, resolution );

    //Only work on the pixels that are between the two threshold pixels
    if( uv.y < firsty && uv.y > secondy ) {
  		float size = firsty - secondy;

  		vec4 colorarray[10];
  		colorarray = putItIn( vec2(uv.x, secondy), size, colorarray, resolution );
  		colorarray = sortArray( colorarray );

  		float sectionSize = size / 9.0;
  		float location = floor( (uv.y - secondy) / sectionSize );
  		float bottom = secondy + (sectionSize * location);
  		float locationBetween = (uv.y - bottom) / sectionSize;

      // A simple method for "fading" between the colors of our ten sampled pixels
  		vec4 topColor = colorarray[int(location) + 1] * locationBetween;
  		vec4 bottomColor = colorarray[int(location)] * (1.0 - locationBetween);

  		gl_FragColor = topColor + bottomColor;
    }
    else
    {
		  gl_FragColor = texture2D( texture, (uv.xy / resolution.xy) );
    }
}
