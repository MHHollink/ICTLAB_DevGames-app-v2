package nl.icode4living.devgames.ui.widget;

import java.util.Comparator;

import nl.icode4living.devgames.models.Project;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class ProjectByScoreComparator implements Comparator<Project> {
    @Override
    public int compare(Project lhs, Project rhs) {
        return lhs.getDevelopers().size() - rhs.getDevelopers().size();
    }
}
