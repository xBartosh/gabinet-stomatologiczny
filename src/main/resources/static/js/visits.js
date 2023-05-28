document.addEventListener("DOMContentLoaded", function () {
    // Attach click event handler to the "Zapłać" button
    let payButtons = document.querySelectorAll(".pay");
    payButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            let visitId = this.getAttribute("data-id");

            // Send fetch request to pay for the visit
            fetch("/api/visit/pay?visitId=" + visitId, {
                method: "POST",
                credentials: "include"
            })
                .then(function (response) {
                    if (response.ok) {
                        // Update "stan konta" value after successful payment
                        let balanceElement = document.getElementById("balance");
                        let currentBalance = parseFloat(balanceElement.textContent.replace("Stan konta: ", "").replace(" zł", ""));
                        let visitContainer = button.parentNode;
                        let surgeries = visitContainer.querySelectorAll(".surgery");
                        let totalSurgeriesPrice = Array.from(surgeries).reduce(function (sum, surgery) {
                            let price = parseFloat(surgery.getAttribute("data-price"));
                            return sum + price;
                        }, 0);
                        let newBalance = currentBalance - totalSurgeriesPrice;
                        balanceElement.textContent = "Stan konta: " + newBalance.toFixed(2) + " zł";

                        // Display success message
                        showSuccessMessage("Pomyślnie zapłacono!");

                        // Replace the "Zapłać" button with a "Zaplacone" label
                        let paidLabel = document.createElement("span");
                        paidLabel.className = "paid-label";
                        paidLabel.textContent = "Zapłacone";
                        visitContainer.replaceChild(paidLabel, button);
                    } else {
                        // Display error message and provide a link to redirect
                        response.text().then(function (errorMessage) {
                            showErrorWithRedirect("Nie udało się zapłacić!");
                        });
                    }
                })
                .catch(function (error) {
                    // Display error message if the request fails
                    showErrorWithRedirect("Coś poszło nie tak!");
                });
        });
    });

    function showSuccessMessage(message) {
        // Create a new element to display success message
        let successMessage = document.createElement("div");
        successMessage.className = "alert alert-success";
        successMessage.textContent = message;

        // Append the success message to the container
        let cardBody = document.querySelector(".card-body");
        cardBody.appendChild(successMessage);
    }

    function showErrorWithRedirect(message) {
        // Create a new element to display error message and redirect link
        let errorMessage = document.createElement("div");
        errorMessage.className = "alert alert-danger";
        errorMessage.textContent = message;

        let redirectLink = document.createElement("a");
        redirectLink.href = "/user/add";
        redirectLink.textContent = "Kliknij tutaj aby dodać środki";

        // Append the error message and redirect link to the container
        let cardBody = document.querySelector(".card-body");
        cardBody.appendChild(errorMessage);
        cardBody.appendChild(redirectLink);
    }
});

// Your existing JavaScript code

// Add the following event listener to handle "edytuj wizyte" button click
const editButtons = document.querySelectorAll('.edit');
editButtons.forEach((button) => {
    button.addEventListener('click', handleEditButtonClick);
});

const options = {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: 'numeric',
    minute: 'numeric',
    hour12: false
};

function handleEditButtonClick(event) {
    const visitId = event.target.getAttribute('data-id');
    const dateModal = document.getElementById('dateModal');
    dateModal.style.display = 'block';

    // Function to handle "Confirm" button click inside the modal
    document.getElementById('confirmDateBtn').onclick = function () {
        const newDateInput = document.getElementById('newDateInput');
        const newStart = new Date(newDateInput.value).toLocaleString('pl-PL', options);

        // Example AJAX request using fetch API
        fetch('/api/visit/reschedule?' + new URLSearchParams({
            visitId: visitId,
            newStart: newDateInput.value.concat(':00')
        }), {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: "include",
        })
            .then(response => {
                if (response.ok) {
                    console.log('Reschedule success:', response);
                    // Handle success response if needed

                    // Update the displayed date
                    const visitContainer = event.target.closest('.visit');
                    const dateElement = visitContainer.querySelector('.visit-date');
                    dateElement.textContent = newStart;

                    // Display success popup
                    showPopup('Pomyślnie zmieniono termin wizyty', true);
                }
                else {
                    showPopup('Nie udało się zmienić terminu wizyty', false);
                }
            })
            .catch(error => {
                console.error('Reschedule error:', error);
                // Handle error response if needed
                showPopup('Nie udało się zmienić terminu wizyty', false);
            });
        dateModal.style.display = 'none';
    };
}

// Function to show a popup message
function showPopup(message, isSuccess) {
    const popup = document.getElementById('popup');
    const popupMessage = popup.querySelector('.popup-message');
    popupMessage.textContent = message;
    popup.style.display = 'block';
    if(isSuccess) {
        popup.style.color = "green";
    }else {
        popup.style.color = "red";
    }

    // Hide the popup after 3 seconds
    setTimeout(() => {
        popup.style.display = 'none';
    }, 3000);
}


// Add the following event listener to handle closing the modal
const closeBtn = document.querySelector('.close');
closeBtn.addEventListener('click', handleCloseModal);

// Function to handle closing the modal
function handleCloseModal() {
    const dateModal = document.getElementById('dateModal');
    dateModal.style.display = 'none';
}

// Your existing JavaScript code

// Your existing JavaScript code

// Add the following event listener to handle "anuluj wizyte" button click
const cancelButtons = document.querySelectorAll('.cancel');
cancelButtons.forEach((button) => {
    button.addEventListener('click', handleCancelButtonClick);
});

// Function to handle "anuluj wizyte" button click
function handleCancelButtonClick(event) {
    const visitId = event.target.getAttribute('data-id');

    // Display confirmation popup
    if (confirm("Czy na pewno chcesz anulować wizytę?")) {
        // Send request to /api/visit/cancel with visitId
        const requestData = {
            visitId: visitId
        };

        // Example AJAX request using fetch API
        fetch(`/api/visit/cancel?visitId=${visitId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    // Handle successful cancellation
                    console.log('Cancellation success');
                    // Display success popup
                    alert('Wizyta została anulowana.');
                    // Remove the corresponding div
                    const divToRemove = event.target.closest('.visit');
                    divToRemove.remove();
                } else {
                    // Handle cancellation error
                    console.error('Cancellation error:', response.statusText);
                    // Display error popup or handle the error in an appropriate way
                }
            })
            .catch(error => {
                console.error('Cancellation error:', error);
                // Handle error response if needed
            });
    }
}


