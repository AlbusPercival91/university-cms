// Function to toggle input fields' readonly attribute
function toggleInputsReadonly(readonly) {
    var inputs = document.querySelectorAll('.form-group input:not([name="password"])');
    inputs.forEach(function(input) {
        input.readOnly = readonly;
    });
}

// Get references to the Save button and its container
var saveButton = document.getElementById('saveButton');
var buttonContainer = document.querySelector('.button-container');

// Initially disable the Save button and its container
saveButton.disabled = true;
buttonContainer.classList.add('disabled');

// Handle the Edit button click event
var editButton = document.getElementById('editButton');
editButton.addEventListener('click', function() {
    // Enable the Save button and its container
    saveButton.disabled = false;
    buttonContainer.classList.remove('disabled');
    // Make input fields (except password) editable
    toggleInputsReadonly(false);
});
