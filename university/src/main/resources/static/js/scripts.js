document.addEventListener('DOMContentLoaded', function() {
  // Add fade-in animation to hero section
  var heroSection = document.querySelector('.hero');
  heroSection.classList.add('fade-in');

  // Smooth scrolling for anchor links
  var navLinks = document.querySelectorAll('nav a');
  navLinks.forEach(function(link) {
    link.addEventListener('click', function(e) {
      e.preventDefault();
      var target = document.querySelector(this.getAttribute('href'));
      target.scrollIntoView({ behavior: 'smooth' });
    });
  });
});
