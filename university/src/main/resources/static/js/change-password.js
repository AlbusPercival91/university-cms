var passwordModal = document.getElementById('passwordModal');
var passwordEditIcon = document.getElementById('passwordEditIcon');
var modalCloseButton = document.getElementById('modalClose');
var cancelPasswordButton = document.getElementById('cancelPasswordButton');

// Open the modal on edit icon click
passwordEditIcon.addEventListener('click', function() {
    passwordModal.style.display = 'block';
});

// Close the modal on modal close button click
modalCloseButton.addEventListener('click', function() {
    passwordModal.style.display = 'none';
});

// Close the modal on cancel button click
cancelPasswordButton.addEventListener('click', function() {
    passwordModal.style.display = 'none';
});
