function toggleMenu(e) {
  e.stopPropagation();

  var menuButton = document.querySelector(".menu-button");
  var sidebar = document.getElementById("sidebar");

  menuButton.classList.toggle("active");
  sidebar.classList.toggle("show-menu");
}

document.addEventListener("DOMContentLoaded", function() {
  var content = document.querySelector(".content");
  var menuButton = document.querySelector(".menu-button");
  var submenuItems = document.querySelectorAll(".submenu");

  content.addEventListener("click", function() {
    var menuButton = document.querySelector(".menu-button");
    var sidebar = document.getElementById("sidebar");

    menuButton.classList.remove("active");
    sidebar.classList.remove("show-menu");
  });

  menuButton.addEventListener("click", toggleMenu);

  submenuItems.forEach(function(submenu) {
    submenu.addEventListener("click", function(e) {
      e.stopPropagation();
    });
  });
});



