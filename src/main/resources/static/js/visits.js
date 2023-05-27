document.addEventListener("DOMContentLoaded", function () {
    // Attach click event handler to the "Zapłać" button
    let payButtons = document.querySelectorAll("button[data-id]");
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
