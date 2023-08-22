document.addEventListener('DOMContentLoaded', function() {
    const teacherRows = document.querySelectorAll('tbody tr');

    teacherRows.forEach(function(row) {
        const editIcon = row.querySelector('.edit-icon');
        const removeIcon = row.querySelector('.remove-icon');

        row.addEventListener('mouseover', function() {
            editIcon.style.display = 'inline-block';
            removeIcon.style.display = 'inline-block';
        });

        row.addEventListener('mouseout', function() {
            editIcon.style.display = 'none';
            removeIcon.style.display = 'none';
        });
    });
});
