#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;

out vec2 vUv;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

void main() {
    vUv = UV0;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}