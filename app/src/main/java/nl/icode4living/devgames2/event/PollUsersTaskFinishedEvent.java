package nl.icode4living.devgames2.event;

import java.util.List;

import nl.icode4living.devgames2.models.User;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class PollUsersTaskFinishedEvent {

    public List<User> users;

    public PollUsersTaskFinishedEvent(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "PollUsersTaskFinishedEvent{" +
                "users=" + users +
                '}';
    }
}
