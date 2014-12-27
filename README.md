Ripple (gl-button)
=========

A quick Android custom-view test for a visual feedback button using OpenGL ES2

All rendering is done through a fragment shader which gives much flexibility and possibilities.

Demo:
http://www.youtube.com/watch?v=_sPiK1Y41LE

https://www.dropbox.com/s/6mtu8j8beuadawm/GLRipple.apk?dl=0

```glsl
precision highp float;

uniform vec2 u_Size;    // View size
uniform float u_Radius; // Ripple radius
varying vec4 v_Color;   // View color
uniform vec2 u_Center;  // Ripple center
varying vec2 v_Position;    // Fragment/pixel position

void main()
{
    // Remap position to actual pixels
    float x = (u_Size.x*(v_Position.x+1.0))/2.0;
    float y = (u_Size.y*(v_Position.y+1.0))/2.0;

    // Calculating pixels distance to touch center
    float dx = x-u_Center.x;
    float dy = y-u_Center.y;
    float dist = sqrt(dx*dx + dy*dy);

    // Calculating light, here you can do any calculation you like
    float light = 0.0;
    if (dist<=u_Radius)
        light = 1.0 - clamp(dist/u_Radius, 0.3, 1.0);

    gl_FragColor = (1.0 + light/2.0) * v_Color;
    gl_FragColor.a = 1.0;
}
```


The **Ripple** view accepts a color for the view and percentage for the radius.
