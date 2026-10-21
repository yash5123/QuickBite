/**
 * QuickBite Enterprise Heavy Motion Engine
 * Wires the local Motion library for rich, heavy, tactile spring physics:
 * - 3D Card Perspective Tilt
 * - Sequential Pipeline Step Spring Pulses
 * - Dynamic Generative Particle Physics Canvas
 * - Rolling Number Counters
 * - Multi-Phase Victory Confetti
 */

(function (global) {
  'use strict';

  const BrutalMotion = {
    motionLib: null,
    reducedMotion: false,
    canvas: null,
    ctx: null,
    animFrameId: null,
    shapes: [],
    mouse: { x: -1000, y: -1000 },

    init() {
      this.reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

      // Connect to local Motion bundle
      if (global.Motion) {
        this.motionLib = global.Motion;
      }

      this.bindTactileKeypress();
      this.bindGenerativeCanvas();
      this.bindCardPerspectiveTilt();
      this.bindStickerTilts();
    },

    // ── 1. Tactile Arcade Keypress Physics ──────────────────────────────
    bindTactileKeypress() {
      document.addEventListener('mousedown', (e) => {
        const btn = e.target.closest('.brutal-btn, .preset-chip, .tab-btn');
        if (!btn || btn.disabled) return;
        btn.classList.add('is-pressed');
      });

      const release = () => {
        const pressedButtons = document.querySelectorAll('.is-pressed');
        pressedButtons.forEach((btn) => {
          btn.classList.remove('is-pressed');

          // Heavy spring release bounce
          if (!this.reducedMotion && this.motionLib && this.motionLib.animate) {
            this.motionLib.animate(btn, {
              y: [4, -1, 0],
              x: [4, -1, 0]
            }, {
              duration: 0.28,
              easing: this.motionLib.spring 
                ? this.motionLib.spring({ stiffness: 450, damping: 20, mass: 0.8 })
                : 'cubic-bezier(0.34, 1.56, 0.64, 1)'
            });
          }
        });
      };

      document.addEventListener('mouseup', release);
      document.addEventListener('mouseleave', release);
    },

    // ── 2. Interactive 3D Perspective Tilt ──────────────────────────────
    bindCardPerspectiveTilt() {
      if (this.reducedMotion) return;

      const cards = document.querySelectorAll('.stage-card, .stat-card, .form-section');
      cards.forEach((card) => {
        card.addEventListener('mousemove', (e) => {
          const rect = card.getBoundingClientRect();
          const x = (e.clientX - rect.left) / rect.width - 0.5;
          const y = (e.clientY - rect.top) / rect.height - 0.5;

          const tiltX = -y * 8;
          const tiltY = x * 8;

          card.style.transform = `perspective(700px) rotateX(${tiltX.toFixed(2)}deg) rotateY(${tiltY.toFixed(2)}deg) translate(-2px, -2px)`;
          card.style.boxShadow = `${(6 - x * 6).toFixed(1)}px ${(6 - y * 6).toFixed(1)}px 0 var(--ink)`;
        });

        card.addEventListener('mouseleave', () => {
          card.style.transform = 'perspective(700px) rotateX(0deg) rotateY(0deg) translate(0px, 0px)';
          card.style.boxShadow = 'var(--shadow-md)';
        });
      });
    },

    // ── 3. Heavy Choreographed Staggered Stage Reveals ─────────────────
    animateStagesReveal(containerSelector) {
      if (this.reducedMotion) return;
      const cards = document.querySelectorAll(`${containerSelector} .stage-card`);
      if (!cards || cards.length === 0) return;

      if (this.motionLib && this.motionLib.animate && this.motionLib.stagger) {
        this.motionLib.animate(cards, {
          opacity: [0, 1],
          y: [36, 0],
          scale: [0.93, 1.02, 1],
          rotate: [2, -1, 0]
        }, {
          delay: this.motionLib.stagger(0.07),
          duration: 0.42,
          easing: this.motionLib.spring
            ? this.motionLib.spring({ stiffness: 400, damping: 22, mass: 0.8 })
            : 'cubic-bezier(0.34, 1.56, 0.64, 1)'
        });
      }
    },

    // ── 4. Sequential Pipeline Step Spring Pulse ────────────────────────
    async animatePipelineExecution(stepIds, onStepActive) {
      for (let i = 0; i < stepIds.length; i++) {
        const id = stepIds[i];
        const card = document.getElementById(id);
        if (!card) continue;

        // Pulse active
        card.classList.add('is-active-step');
        if (onStepActive) onStepActive(id, i);

        if (!this.reducedMotion && this.motionLib && this.motionLib.animate) {
          this.motionLib.animate(card, {
            scale: [1, 1.05, 1],
            y: [0, -6, 0]
          }, {
            duration: 0.24,
            easing: 'ease-out'
          });
        }

        await new Promise(r => setTimeout(r, 160));

        // Mark passed
        card.classList.remove('is-active-step');
        card.classList.add('is-success-step');
        const badge = card.querySelector('.status-indicator');
        if (badge) badge.textContent = 'VERIFIED ✓';
      }
    },

    // ── 5. Generative Background Canvas (Heavy Geometric Particle Flow) ─
    bindGenerativeCanvas() {
      if (this.reducedMotion) return;
      this.canvas = document.getElementById('generativeBg');
      if (!this.canvas) return;

      this.ctx = this.canvas.getContext('2d');
      this.resizeCanvas();
      window.addEventListener('resize', () => this.resizeCanvas());

      const hero = document.getElementById('heroSection');
      if (hero) {
        hero.addEventListener('mousemove', (e) => {
          const rect = this.canvas.getBoundingClientRect();
          this.mouse.x = e.clientX - rect.left;
          this.mouse.y = e.clientY - rect.top;
        });
        hero.addEventListener('mouseleave', () => {
          this.mouse.x = -1000;
          this.mouse.y = -1000;
        });
        hero.addEventListener('touchmove', (e) => {
          if (e.touches && e.touches.length > 0) {
            const rect = this.canvas.getBoundingClientRect();
            this.mouse.x = e.touches[0].clientX - rect.left;
            this.mouse.y = e.touches[0].clientY - rect.top;
          }
        }, { passive: true });
        hero.addEventListener('touchend', () => {
          this.mouse.x = -1000;
          this.mouse.y = -1000;
        });
      }

      const colors = ['#FFD60A', '#FF6B9D', '#3B82F6', '#10B981', '#8B5CF6', '#FF8A3D', '#06B6D4', '#84CC16'];
      const types = ['square', 'circle', 'triangle', 'plus', 'diamond'];
      this.shapes = [];

      const count = Math.min(32, Math.floor(window.innerWidth / 45));
      for (let i = 0; i < count; i++) {
        this.shapes.push({
          x: Math.random() * this.canvas.width,
          y: Math.random() * this.canvas.height,
          vx: (Math.random() - 0.5) * 0.75,
          vy: (Math.random() - 0.5) * 0.75,
          size: 18 + Math.random() * 24,
          angle: Math.random() * Math.PI * 2,
          vAngle: (Math.random() - 0.5) * 0.025,
          color: colors[Math.floor(Math.random() * colors.length)],
          type: types[Math.floor(Math.random() * types.length)]
        });
      }

      const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) this.startLoop();
          else this.stopLoop();
        });
      }, { threshold: 0.1 });
      observer.observe(this.canvas);

      document.addEventListener('visibilitychange', () => {
        if (document.hidden) this.stopLoop();
        else this.startLoop();
      });

      this.startLoop();
    },

    resizeCanvas() {
      if (!this.canvas) return;
      const rect = this.canvas.parentElement.getBoundingClientRect();
      this.canvas.width = rect.width;
      this.canvas.height = rect.height;
    },

    startLoop() {
      if (this.animFrameId || this.reducedMotion) return;
      const loop = () => {
        this.renderCanvas();
        this.animFrameId = requestAnimationFrame(loop);
      };
      this.animFrameId = requestAnimationFrame(loop);
    },

    stopLoop() {
      if (this.animFrameId) {
        cancelAnimationFrame(this.animFrameId);
        this.animFrameId = null;
      }
    },

    renderCanvas() {
      if (!this.ctx || !this.canvas) return;
      const { ctx, canvas, shapes, mouse } = this;
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      for (let s of shapes) {
        s.x += s.vx;
        s.y += s.vy;
        s.angle += s.vAngle;

        // Wrap edges with bounce
        if (s.x < -40) s.x = canvas.width + 30;
        if (s.x > canvas.width + 40) s.x = -30;
        if (s.y < -40) s.y = canvas.height + 30;
        if (s.y > canvas.height + 40) s.y = -30;

        // Mouse proximity repulsion
        const dx = s.x - mouse.x;
        const dy = s.y - mouse.y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 150 && dist > 0) {
          const force = (150 - dist) / 150;
          s.x += (dx / dist) * force * 4.2;
          s.y += (dy / dist) * force * 4.2;
          s.angle += force * 0.08;
        }

        ctx.save();
        ctx.translate(s.x, s.y);
        ctx.rotate(s.angle);

        // Solid hard shadow (0 blur)
        ctx.fillStyle = '#0A0A0A';
        this.drawShapePath(ctx, 3.5, 3.5, s.size, s.type);
        ctx.fill();

        // Main colored surface
        ctx.fillStyle = s.color;
        this.drawShapePath(ctx, 0, 0, s.size, s.type);
        ctx.fill();

        // Solid 2.5px ink border
        ctx.lineWidth = 2.5;
        ctx.strokeStyle = '#0A0A0A';
        this.drawShapePath(ctx, 0, 0, s.size, s.type);
        ctx.stroke();

        ctx.restore();
      }
    },

    drawShapePath(ctx, ox, oy, sz, type) {
      ctx.beginPath();
      if (type === 'circle') {
        ctx.arc(ox, oy, sz / 2, 0, Math.PI * 2);
      } else if (type === 'square') {
        ctx.rect(ox - sz / 2, oy - sz / 2, sz, sz);
      } else if (type === 'diamond') {
        ctx.moveTo(ox, oy - sz / 2);
        ctx.lineTo(ox + sz / 2, oy);
        ctx.lineTo(ox, oy + sz / 2);
        ctx.lineTo(ox - sz / 2, oy);
        ctx.closePath();
      } else if (type === 'triangle') {
        const h = sz * 0.866;
        ctx.moveTo(ox, oy - h / 2);
        ctx.lineTo(ox - sz / 2, oy + h / 2);
        ctx.lineTo(ox + sz / 2, oy + h / 2);
        ctx.closePath();
      } else if (type === 'plus') {
        const w = sz * 0.35;
        const arm = sz / 2;
        ctx.rect(ox - w / 2, oy - arm, w, sz);
        ctx.rect(ox - arm, oy - w / 2, sz, w);
      }
    },

    // ── 6. Sticker Spring Wobble ────────────────────────────────────────
    bindStickerTilts() {
      if (this.reducedMotion) return;
      const stickers = document.querySelectorAll('.brutal-badge, .brand-box');
      stickers.forEach((sticker) => {
        sticker.addEventListener('mouseenter', () => {
          if (this.motionLib && this.motionLib.animate) {
            this.motionLib.animate(sticker, {
              rotate: [0, -6, 6, -2, 0],
              scale: [1, 1.1, 1]
            }, {
              duration: 0.36,
              easing: 'ease-out'
            });
          }
        });
      });
    },

    // ── 7. Rolling Number Counter ───────────────────────────────────────
    animateCounter(elementId, targetValue, prefix = '', suffix = '') {
      const el = document.getElementById(elementId);
      if (!el) return;
      const current = parseFloat(el.textContent.replace(/[^0-9.]/g, '')) || 0;
      const diff = targetValue - current;
      const frames = 18;
      let frame = 0;

      const timer = setInterval(() => {
        frame++;
        const progress = frame / frames;
        const ease = 1 - Math.pow(1 - progress, 3); // cubic ease-out
        const val = Math.round(current + diff * ease);
        el.textContent = `${prefix}${val}${suffix}`;
        if (frame >= frames) {
          clearInterval(timer);
          el.textContent = `${prefix}${targetValue}${suffix}`;
        }
      }, 20);
    },

    // ── 8. Multi-Burst Celebration Confetti ─────────────────────────────
    blastConfetti() {
      if (this.reducedMotion) return;
      const canvas = document.createElement('canvas');
      canvas.style.position = 'fixed';
      canvas.style.top = '0';
      canvas.style.left = '0';
      canvas.style.width = '100vw';
      canvas.style.height = '100vh';
      canvas.style.pointerEvents = 'none';
      canvas.style.zIndex = '9999';
      document.body.appendChild(canvas);

      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
      const ctx = canvas.getContext('2d');

      const colors = ['#FFD60A', '#FF6B9D', '#3B82F6', '#10B981', '#FF8A3D', '#8B5CF6', '#06B6D4', '#84CC16'];
      const particles = [];
      
      // Multi-burst from left, center, right
      const origins = [canvas.width * 0.2, canvas.width * 0.5, canvas.width * 0.8];
      origins.forEach(ox => {
        for (let i = 0; i < 35; i++) {
          particles.push({
            x: ox + (Math.random() - 0.5) * 60,
            y: canvas.height * 0.72,
            vx: (Math.random() - 0.5) * 22,
            vy: -12 - Math.random() * 16,
            gravity: 0.48,
            rot: Math.random() * 360,
            vRot: (Math.random() - 0.5) * 22,
            w: 12 + Math.random() * 8,
            h: 7 + Math.random() * 6,
            color: colors[Math.floor(Math.random() * colors.length)],
            life: 1
          });
        }
      });

      let animId;
      const render = () => {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        let alive = false;
        for (let p of particles) {
          p.x += p.vx;
          p.y += p.vy;
          p.vy += p.gravity;
          p.rot += p.vRot;
          p.life -= 0.014;

          if (p.life > 0) {
            alive = true;
            ctx.save();
            ctx.translate(p.x, p.y);
            ctx.rotate((p.rot * Math.PI) / 180);
            ctx.fillStyle = '#0A0A0A';
            ctx.fillRect(-p.w / 2 + 2, -p.h / 2 + 2, p.w, p.h);
            ctx.fillStyle = p.color;
            ctx.fillRect(-p.w / 2, -p.h / 2, p.w, p.h);
            ctx.strokeStyle = '#0A0A0A';
            ctx.lineWidth = 1.5;
            ctx.strokeRect(-p.w / 2, -p.h / 2, p.w, p.h);
            ctx.restore();
          }
        }

        if (alive) {
          animId = requestAnimationFrame(render);
        } else {
          cancelAnimationFrame(animId);
          canvas.remove();
        }
      };
      animId = requestAnimationFrame(render);
    }
  };

  global.BrutalMotion = BrutalMotion;
})(window);
