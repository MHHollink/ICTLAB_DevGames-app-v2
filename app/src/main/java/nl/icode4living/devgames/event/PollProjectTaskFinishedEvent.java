package nl.icode4living.devgames.event;

import nl.icode4living.devgames.models.Project;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class PollProjectTaskFinishedEvent {

    public Project project;

    public PollProjectTaskFinishedEvent(Project project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "PollProjectTaskFinishedEvent{" +
                "project=" + project +
                '}';
    }
}
