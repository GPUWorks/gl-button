uniform vec4 u_Color;
uniform vec2 u_Size;
uniform float u_Radius;
uniform vec2 u_Center;
attribute vec4 a_Position;
varying vec4 v_Color;
varying vec2 v_Position;

void main()
{
    v_Color = u_Color;
    gl_Position =   a_Position;
    v_Position =   a_Position.xy;
}