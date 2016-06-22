package nl.icode4living.devgames.event;

import nl.icode4living.devgames.models.User;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class PollUserFinishedEvent {

    public boolean success;
    public User user;

    public PollUserFinishedEvent(boolean success, User user) {
        this.success = success;
        this.user = user;
    }

    @Override
    public String toString() {
        return "PollUserFinishedEvent{" +
                "success=" + success +
                ", user=" + user +
                '}';
    }
}
