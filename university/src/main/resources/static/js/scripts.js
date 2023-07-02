document.addEventListener('DOMContentLoaded', function() {
    // Add fade-in animation to hero section
    var heroSection = document.querySelector('.hero');
    heroSection.classList.add('fade-in');

    // Smooth scrolling for anchor links except for links with target="_blank"
    var navLinks = document.querySelectorAll('nav a:not([target="_blank"])');
    navLinks.forEach(function(link) {
        link.addEventListener('click', function(e) {
            // Check if the link is pointing to a section within the same page
            var target = document.querySelector(this.getAttribute('href'));
            if (target) {
                e.preventDefault();
                target.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });
});

