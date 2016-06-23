package nl.icode4living.devgames2.ui.widget;

import java.util.Comparator;

import nl.icode4living.devgames2.models.Project;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class ProjectByNameComparator implements Comparator<Project> {
    @Override
    public int compare(Project lhs, Project rhs) {
        return lhs.getName().compareToIgnoreCase(rhs.getName());
    }
}
