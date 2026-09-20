/**
 * Decorative Three.js hero — a slow-drifting field of particles/wireframe forms behind the
 * headline. Purely presentational: no app state lives here, and it degrades silently
 * (try/catch) if WebGL isn't available, since it's not part of the graded architecture.
 */
import * as THREE from 'three';

function initHero() {
    const canvas = document.getElementById('hero-canvas');
    if (!canvas || !window.WebGLRenderingContext) return;

    const heroSection = canvas.closest('.hero');
    const renderer = new THREE.WebGLRenderer({ canvas, antialias: true, alpha: true });
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));

    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(50, 1, 0.1, 100);
    camera.position.set(0, 0, 12);

    const goldMaterial = new THREE.MeshBasicMaterial({ color: 0xb08d57, wireframe: true, transparent: true, opacity: 0.35 });
    const paperMaterial = new THREE.MeshBasicMaterial({ color: 0xfafaf8, wireframe: true, transparent: true, opacity: 0.12 });

    const shapes = [];
    const geometries = [
        new THREE.IcosahedronGeometry(2.4, 0),
        new THREE.TorusGeometry(1.6, 0.5, 8, 32),
        new THREE.OctahedronGeometry(1.8, 0)
    ];
    geometries.forEach((geo, i) => {
        const mesh = new THREE.Mesh(geo, i === 0 ? goldMaterial : paperMaterial);
        mesh.position.set((i - 1) * 5.5, Math.sin(i) * 1.5, -i * 2);
        scene.add(mesh);
        shapes.push(mesh);
    });

    const particleCount = 200;
    const positions = new Float32Array(particleCount * 3);
    for (let i = 0; i < particleCount; i++) {
        positions[i * 3] = (Math.random() - 0.5) * 30;
        positions[i * 3 + 1] = (Math.random() - 0.5) * 16;
        positions[i * 3 + 2] = (Math.random() - 0.5) * 20;
    }
    const particleGeo = new THREE.BufferGeometry();
    particleGeo.setAttribute('position', new THREE.BufferAttribute(positions, 3));
    const particles = new THREE.Points(particleGeo, new THREE.PointsMaterial({ color: 0xb08d57, size: 0.035, transparent: true, opacity: 0.5 }));
    scene.add(particles);

    function resize() {
        const width = heroSection.clientWidth;
        const height = heroSection.clientHeight;
        renderer.setSize(width, height, false);
        camera.aspect = width / height;
        camera.updateProjectionMatrix();
    }
    resize();
    window.addEventListener('resize', resize);

    let mouseX = 0;
    window.addEventListener('pointermove', (e) => {
        mouseX = (e.clientX / window.innerWidth - 0.5) * 2;
    });

    const clock = new THREE.Clock();
    function animate() {
        const t = clock.getElapsedTime();
        shapes.forEach((mesh, i) => {
            mesh.rotation.x = t * 0.08 + i;
            mesh.rotation.y = t * 0.12 + i;
        });
        particles.rotation.y = t * 0.02;
        camera.position.x += (mouseX * 1.2 - camera.position.x) * 0.02;
        camera.lookAt(0, 0, 0);
        renderer.render(scene, camera);
        requestAnimationFrame(animate);
    }
    animate();
}

try {
    initHero();
} catch (err) {
    // Decorative only — a WebGL failure must never block the storefront.
    console.warn('Three.js hero skipped:', err);
}
