package ua.foxminded.university.validation;

public final class Message {

    /*
     * General Attribute Constants
     */
    public static final String SUCCESS = "successMessage";
    public static final String ERROR = "errorMessage";
    public static final String LOGIN_ERROR = "loginError";

    /*
     * Controller Message Constants
     */
    public static final String FAILURE = "Operation Failed";
    public static final String CREATE_SUCCESS = "Created successfully";
    public static final String UPDATE_SUCCESS = "Data updated successfully";
    public static final String DELETE_SUCCESS = "Deleted successfully";
    public static final String NOT_FOUND = "Result not found";
    public static final String ASSIGNED = "User assigned to the Course";
    public static final String REASSIGNED = "User reassigned from Course";

    private Message() {

    }
}
