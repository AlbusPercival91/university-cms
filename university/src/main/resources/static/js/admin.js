document.addEventListener("DOMContentLoaded", function() {
    var sidebar = document.getElementById("sidebar");
    var menuItems = sidebar.querySelectorAll(".menu-item-box");

    menuItems.forEach(function(menuItem) {
        menuItem.addEventListener("click", function(event) {
            var submenu = this.querySelector(".submenu");
            if (submenu) {
                event.stopPropagation(); // Stop event propagation to prevent closing the submenu immediately
                submenu.style.display = submenu.style.display === "block" ? "none" : "block";
            }
        });
    });

    document.addEventListener("click", function() {
        var submenuItems = sidebar.querySelectorAll(".submenu");
        submenuItems.forEach(function(submenu) {
            submenu.style.display = "none";
        });
    });
});