#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

//processing image filter vars
uniform sampler2D texture;
varying vec4 vertTexCoord;

//custom var to weight the intensity of chromatic aberrations
uniform float caIntensity = 1.0;

//uv offset variables (uvspace = 0-1)
vec2 rOffset = vec2(-0.001,0.002);
vec2 gOffset = vec2(0.00,-0.002);
vec2 bOffset = vec2(0.002,-0.001);

void main () {
	
	//basecolor
	vec4 baseColor = texture2D(texture, vertTexCoord);
	
	//apply offset to single channels
    vec4 rValue = texture2D(texture, vertTexCoord - rOffset);  
    vec4 gValue = texture2D(texture, vertTexCoord - gOffset);
    vec4 bValue = texture2D(texture, vertTexCoord - bOffset);
	

    // Combine the offset colors and add them to the base color using caIntensity to weight the balance
	//0  = full base color
	//1  = full chromatic aberration
	//+1 = overdrive chromatic aberration 
	
	gl_FragColor = (baseColor*(1-caIntensity)) + (vec4(rValue.r, gValue.g, bValue.b, baseColor.a)*caIntensity);
}