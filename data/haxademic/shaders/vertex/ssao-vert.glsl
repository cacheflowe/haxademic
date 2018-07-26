/**
 * @author alteredq / http://alteredqualia.com/
 *
 * Screen-space ambient occlusion shader
 * - ported from
 *   SSAO GLSL shader v1.2
 *   assembled by Martins Upitis (martinsh) (http://devlog-martinsh.blogspot.com)
 *   original technique is made by ArKano22 (http://www.gamedev.net/topic/550699-ssao-no-halo-artifacts/)
 * - modifications
 * - modified to use RGBA packed depth texture (use clear color 1,1,1,1 for depth pass)
 * - refactoring and optimizations
 */

uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;

uniform mat4 projection;
uniform mat4 modelview;

attribute vec4 vertex;
attribute vec4 color;
attribute vec2 texCoord;
attribute vec3 normal;

varying vec4 vertColor;
varying vec4 vertTexCoord;
varying vec3 vertNormal;
varying vec3 vertLightDir;


varying vec2 vUv;

void main() {
	vUv = texCoord;
	gl_Position = projection * modelview * vec4( vertex.x, vertex.y * -1.0, vertex.z, 1.0 );
}
