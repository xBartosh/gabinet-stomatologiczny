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
                console.log('Visit scheduled successfully.');
            } else {
                console.error('Failed to schedule visit.');
            }
        })
        .catch(error => {
            console.error('An error occurred while scheduling the visit:', error);
        });
}

// Add event listener to the span element
let submitSpan = document.getElementById('submit');
submitSpan.addEventListener('click', sendPostRequest);
