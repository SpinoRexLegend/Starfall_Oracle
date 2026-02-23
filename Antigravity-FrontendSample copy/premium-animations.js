/**
 * Premium Command Input Enhancements
 * Adds sophisticated animations and interactions to the command center
 */

// Add typing indicator when user types
function initPremiumCommandInput() {
    const commandInput = document.getElementById('command-prompt');
    const analyzeBtn = document.getElementById('analyze-btn');

    if (!commandInput) return;

    let typingTimer;
    const typingDelay = 500; // ms

    // Add typing animation class while user is typing
    commandInput.addEventListener('input', function () {
        clearTimeout(typingTimer);
        this.classList.add('typing');

        typingTimer = setTimeout(() => {
            this.classList.remove('typing');
        }, typingDelay);
    });

    // Enhanced focus animations
    commandInput.addEventListener('focus', function () {
        this.parentElement.parentElement.classList.add('panel-focused');
    });

    commandInput.addEventListener('blur', function () {
        this.parentElement.parentElement.classList.remove('panel-focused');
    });

    // Button click particle effect
    if (analyzeBtn) {
        analyzeBtn.addEventListener('click', function (e) {
            createRipple(e, this);
        });
    }
}

// Create ripple effect on button click
function createRipple(event, element) {
    const ripple = document.createElement('span');
    const rect = element.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = event.clientX - rect.left - size / 2;
    const y = event.clientY - rect.top - size / 2;

    ripple.style.width = ripple.style.height = size + 'px';
    ripple.style.left = x + 'px';
    ripple.style.top = y + 'px';
    ripple.classList.add('ripple-effect');

    element.appendChild(ripple);

    setTimeout(() => {
        ripple.remove();
    }, 600);
}

// Add particle background to command panel
function addParticleBackground() {
    const panel = document.querySelector('.panel-input');
    if (!panel || panel.querySelector('.particle-container')) return;

    const particleContainer = document.createElement('div');
    particleContainer.className = 'particle-container';

    // Create floating particles
    for (let i = 0; i < 20; i++) {
        const particle = document.createElement('div');
        particle.className = 'particle';
        particle.style.left = Math.random() * 100 + '%';
        particle.style.animationDelay = Math.random() * 5 + 's';
        particle.style.animationDuration = (5 + Math.random() * 10) + 's';
        particleContainer.appendChild(particle);
    }

    panel.insertBefore(particleContainer, panel.firstChild);
}

// Animated placeholder text
function animatePlaceholder() {
    const commandInput = document.getElementById('command-prompt');
    if (!commandInput) return;

    const placeholders = [
        "Enter mission command (e.g., 'Target asteroid Apophis-99942')...",
        "Initiate threat assessment protocol...",
        "Deploy planetary defense systems...",
        "Analyze near-Earth object trajectory...",
        "Execute deflection strategy simulation..."
    ];

    let currentIndex = 0;

    setInterval(() => {
        if (document.activeElement !== commandInput && commandInput.value === '') {
            currentIndex = (currentIndex + 1) % placeholders.length;
            commandInput.setAttribute('placeholder', placeholders[currentIndex]);
        }
    }, 4000);
}

// Initialize all premium features
document.addEventListener('DOMContentLoaded', () => {
    initPremiumCommandInput();
    addParticleBackground();
    animatePlaceholder();
});
