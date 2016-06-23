package nl.icode4living.devgames2.event;

import java.util.List;

import nl.icode4living.devgames2.models.Project;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class PollProjectsTaskFinishedEvent {

    public List<Project> projects;

    public PollProjectsTaskFinishedEvent(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "PollProjectsTaskFinishedEvent{" +
                "projects=" + projects +
                '}';
    }
}
