const mess = document.getElementById('message');
mess.style.display = "none";

// Retrieve the list of checked surgeries
function getCheckedSurgeries() {
    return Array.from(document.querySelectorAll('input[name="surgeries"]:checked'))
        .map(checkbox => checkbox.value);
}

// Retrieve the ID of the selected doctor
function getSelectedDoctorId() {
    return document.querySelector('select').value;
}

// Retrieve the date and time in the required format (yyyy-MM-ddTHH:mm:ssZ)
function getSelectedDateTime() {
    const dateTimeInput = document.getElementById('meeting-time');
    return dateTimeInput.value;
}

// Send the POST request
function sendPostRequest() {
    const surgeriesList = getCheckedSurgeries();
    const doctorId = getSelectedDoctorId();
    const startTime = getSelectedDateTime();
    const messageDiv = document.getElementById('message'); // Get the message div

    fetch('/api/visit/schedule/patient?' + new URLSearchParams({
        doctorId: doctorId,
        start: startTime,
        surgeries: surgeriesList
    }), {
        method: 'POST',
        credentials: "include"
    })
        .then(response => {
            if (response.ok) {
                messageDiv.textContent = 'Pomyślnie zarezerwowano wizytę!'; // Display success message
                messageDiv.classList.remove('error'); // Remove error class if present
                messageDiv.classList.add('success'); // Add success class
                mess.style.display = "block";
            } else {
                messageDiv.textContent = 'Nie udało się zarezerwować wizyty!'; // Display error message
                messageDiv.classList.remove('success'); // Remove success class if present
                messageDiv.classList.add('error'); // Add error class
                mess.style.display = "block";
            }
        })
        .catch(error => {
            messageDiv.textContent = 'Nie udało się zarezerwować wizyty!'; // Display error message
            messageDiv.classList.remove('success'); // Remove success class if present
            messageDiv.classList.add('error'); // Add error class
            mess.style.display = "block";
            console.error('An error occurred while scheduling the visit:', error);
        });
}

// Add event listener to the span element
let submitSpan = document.getElementById('submit');
submitSpan.addEventListener('click', sendPostRequest);

// Get the datetime-local input element
let meetingTimeInput = document.getElementById("meeting-time");

// Set the minimum and maximum values for time
meetingTimeInput.min = "2023-01-01T06:00";
meetingTimeInput.max = "2023-12-31T21:00";

// Add a change event listener to validate the selected time
meetingTimeInput.addEventListener("change", function () {
    let selectedTime = new Date(meetingTimeInput.value);
    let selectedDay = selectedTime.getDay();

    if (selectedTime.getHours() < 6 || selectedTime.getHours() >= 21 || selectedDay === 0 || selectedDay === 6) {
        meetingTimeInput.setCustomValidity("Please select a time between 6 AM and 9 PM on Monday to Saturday");
    } else {
        meetingTimeInput.setCustomValidity(""); // Reset the custom validity
    }
});
