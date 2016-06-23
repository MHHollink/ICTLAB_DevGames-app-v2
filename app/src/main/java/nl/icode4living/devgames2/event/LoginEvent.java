package nl.icode4living.devgames2.event;

/**
 * An event denoting the result of a login attempt.
 */
public class LoginEvent {

    /**
     * Whether the login attempt was successful or not.
     */
    public final boolean success;

    /**
     * The message for the user.
     */
    public final String message;

    public LoginEvent(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public String toString() {
        return "LoginEvent{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
