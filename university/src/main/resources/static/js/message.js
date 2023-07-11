const successMessageContainer = document.getElementById("success-message-container");
const errorMessageContainer = document.getElementById("error-message-container");

if (successMessageContainer && successMessageContainer.textContent !== "") {
    showMessage(successMessageContainer.textContent, "success");
} else if (errorMessageContainer && errorMessageContainer.textContent !== "") {
    showMessage(errorMessageContainer.textContent, "error");
}




