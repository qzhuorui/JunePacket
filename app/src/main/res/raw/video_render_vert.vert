attribute vec4 vPosition;
attribute vec4 vCoord;
uniform mat4 vMatrix;
varying vec2 fCoord;


void main(){
    gl_Position = vPosition;
    fCoord = (vMatrix * vCoord).xy;
}