#version 150

in vec2 vUv;
out vec4 fragColor;

uniform sampler2D Sampler0; // your rift texture
uniform float Time;

// hash noise
float noise(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float smoothNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);

    float a = noise(i);
    float b = noise(i + vec2(1,0));
    float c = noise(i + vec2(0,1));
    float d = noise(i + vec2(1,1));

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(a,b,u.x)
    + (c - a)*u.y*(1.0-u.x)
    + (d - b)*u.x*u.y;
}

// jagged tear shape
float tearMask(vec2 uv) {
    vec2 centered = uv - 0.5;
    float r = length(centered);

    float jag = smoothNoise(uv * 10.0 + Time * 0.3) * 0.15;

    return smoothstep(0.45 + jag, 0.38 + jag, r);
}

void main() {
    vec2 uv = vUv;

    vec2 centered = uv - 0.5;
    float r = length(centered);

    // --- SWIRL DISTORTION ---
    float angle = atan(centered.y, centered.x);
    float swirl = sin(r * 10.0 - Time * 2.5) * 0.25;
    angle += swirl;

    vec2 distorted = vec2(cos(angle), sin(angle)) * r + 0.5;

    // --- SAMPLE RIFT INTERIOR ---
    vec4 inner = texture(Sampler0, distorted * 1.2);

    // --- TEAR MASK ---
    float mask = tearMask(uv);

    // --- EDGE GLOW ---
    float edge = smoothstep(0.35, 0.5, r);
    vec3 glow = vec3(0.8, 0.3, 1.2) * (1.0 - edge);

    // --- FRAYED THREADS ---
    float threads = abs(sin(uv.y * 80.0 + Time * 8.0));
    threads *= smoothstep(0.5, 0.35, r); // only near edge
    threads *= 0.25;

    // --- DEPTH DARKENING ---
    float depth = 1.3 - r * 1.8;

    vec3 color = inner.rgb;

    // combine
    color *= depth;
    color += glow;
    color += threads;

    fragColor = vec4(color, mask * inner.a);
}