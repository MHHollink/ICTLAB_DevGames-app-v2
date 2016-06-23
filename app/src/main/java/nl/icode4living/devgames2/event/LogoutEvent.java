package nl.icode4living.devgames2.event;

public class LogoutEvent {
    public final boolean hasUnSynchronizedWork;

    public LogoutEvent(boolean hasUnSynchronizedWork) {
        this.hasUnSynchronizedWork = hasUnSynchronizedWork;
    }

    @Override
    public String toString() {
        return "LogoutEvent{" +
                "hasUnSynchronizedWork=" + hasUnSynchronizedWork +
                '}';
    }
}