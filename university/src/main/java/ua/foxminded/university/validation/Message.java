package ua.foxminded.university.validation;

public final class Message {

    /*
     * General Attribute Constants
     */
    public static final String SUCCESS = "successMessage";
    public static final String ERROR = "errorMessage";
    public static final String LOGIN_ERROR = "loginError";

    /*
     * Controller and Service classes Message Constants
     */
    public static final String FAILURE = "Operation Failed";
    public static final String CREATE_SUCCESS = "Created successfully";
    public static final String UPDATE_SUCCESS = "Data updated successfully";
    public static final String DELETE_SUCCESS = "Deleted successfully";
    public static final String ADMIN_NOT_FOUND = "Admin not found";
    public static final String ROOM_NOT_FOUND = "Class Room not found";
    public static final String COURSE_NOT_FOUND = "Course not found";
    public static final String DEPARTMENT_NOT_FOUND = "Department not found";
    public static final String FACULTY_NOT_FOUND = "Faculty not found";
    public static final String GROUP_NOT_FOUND = "Group not found";
    public static final String STAFF_NOT_FOUND = "Staff not found";
    public static final String STUDENT_NOT_FOUND = "Student not found";
    public static final String TEACHER_NOT_FOUND = "Teacher not found";
    public static final String TIMETABLE_NOT_FOUND = "Time Table not found";
    public static final String ASSIGNED = "User assigned to the Course";
    public static final String REASSIGNED = "User reassigned from Course";
    public static final String USER_ALREADY_ASSIGNED = "User already assigned with this Course!";
    public static final String USER_NOT_RELATED_WITH_COURSE = "User is not related with this Course!";
    public static final String EMAIL_EXISTS = "Email already registered";
    public static final String PASSWORD_WRONG = "Password incorrect";
    public static final String RECORD_EXISTS = "Record already exists";
    public static final String DEPARTMENT_EXISTS = "Faculty already contains this Department";
    public static final String FACULTY_EXISTS = "Faculty already exists";
    public static final String GROUP_EXISTS = "Faculty already contains this Group";
    public static final String TIMETABLE_SCHEDULED = "Timetable [date:{}, time from:{}, time to:{}] is scheduled successfully";

    /*
     * TimeTable validation Message Constants
     */
    public static final String VALIDATION_FAILED = "Validation failed while creating TimeTable";
    public static final String TIMING_WRONG = "'Time From' can't be ahaed of 'Time To'";
    public static final String IS_NOT_TEACHER_COURSE = "Teacher is not assigned with such Course";
    public static final String TEACHER_BUSY = "Teacher is busy during this time";

    private Message() {

    }
}
