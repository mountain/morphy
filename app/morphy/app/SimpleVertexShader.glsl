#version 120

uniform mat4 projection = mat4(1.0);
uniform mat4 view = mat4(1.0);
uniform mat4 model = mat4(1.0);

attribute vec3 position;

void main() {
    gl_Position = projection * view * model * vec4(position, 1.0);
}