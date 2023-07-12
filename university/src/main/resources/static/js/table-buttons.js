document.addEventListener('DOMContentLoaded', function() {
    const teacherRows = document.querySelectorAll('tbody tr');

    teacherRows.forEach(function(row, index) {
        const editButton = document.getElementById(`editButton-${index}`);
        const removeButton = document.getElementById(`removeButton-${index}`);

        row.addEventListener('mouseover', function() {
            editButton.style.display = 'inline-block';
            removeButton.style.display = 'inline-block';
        });

        row.addEventListener('mouseout', function() {
            editButton.style.display = 'none';
            removeButton.style.display = 'none';
        });
    });
});
