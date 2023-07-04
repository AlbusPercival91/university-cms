document.addEventListener("DOMContentLoaded", function() {
    var sidebar = document.getElementById("sidebar");
    var content = document.querySelector(".content");

    sidebar.addEventListener("click", function(e) {
        var menuItem = e.target.closest(".menu-item");
        if (menuItem) {
            var submenu = menuItem.querySelector(".submenu");
            if (submenu) {
                submenu.style.display = submenu.style.display === "block" ? "none" : "block";
            }
        }
    });

    content.addEventListener("click", function() {
        var submenuItems = sidebar.querySelectorAll(".submenu");
        submenuItems.forEach(function(submenu) {
            submenu.style.display = "none";
        });
    });
});
