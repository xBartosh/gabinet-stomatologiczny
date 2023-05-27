let message = document.getElementById('message');
message.style.display = 'none';
// Wait for the DOM content to load
document.addEventListener('DOMContentLoaded', function() {
    // Hide the message initially

    // Add submit event listener to the form
    let form = document.getElementById('balance-form');
    form.addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent the default form submission

        // Get the form data
        let formData = new FormData(form);

        // Make the fetch request
        fetch(form.action, {
            method: 'POST',
            body: formData,
            credentials: 'include'
        })
            .then(function(response) {
                if (response.ok) {
                    // If the response is successful, display a success message
                    showMessage('Pomyślnie doładowano!', true);
                    // You can also redirect the user to another page here
                } else {
                    // If the response is not successful, display an error message
                    showMessage('Wystąpił błąd. Spróbuj ponownie.', false);
                }
            })
            .catch(function(error) {
                // If an error occurs during the fetch request, display an error message
                showMessage('Wystąpił błąd. Spróbuj ponownie.', false);
            });
    });

    function showMessage(mess, isSuccess) {
        mess = mess || ''; // Default to empty mess if not provided

        message.textContent = mess; // Set the mess text

        if (isSuccess) {
            message.classList.remove('error');
            message.classList.add('success');
        } else {
            message.classList.remove('success');
            message.classList.add('error');
        }

        // Display the mess
        message.style.display = 'block';
    }
});
