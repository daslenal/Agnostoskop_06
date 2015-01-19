#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

//processing image filter vars
uniform sampler2D texture;
varying vec4 vertTexCoord;

//custom var to set alphaStrengh
uniform float alphaThreshold = 0.5;
uniform float strength = 0.0;
varying float aValue = 0.0;
uniform float alphaStrength = 1.0;

vec4 value = texture2D(texture, vertTexCoord);

void main () 
{
	aValue = value.a * alphaStrength;
	if( value.r >= alphaThreshold && value.g >= alphaThreshold && value.b >= alphaThreshold )
	{
		aValue = strength;
	}
	
    gl_FragColor = vec4(value.r, value.g, value.b, aValue);
	//Debug:
	//gl_FragColor = vec4(aValue, aValue, aValue, 1.0);
}