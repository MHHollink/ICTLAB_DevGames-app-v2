package nl.icode4living.devgames.models;

import java.util.Comparator;
import java.util.List;

import nl.icode4living.devgames.R;
import nl.icode4living.devgames.ui.widget.ProjectByNameComparator;
import nl.icode4living.devgames.ui.widget.ProjectByScoreComparator;
import nl.icode4living.devgames.util.SortOption;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class Project {

    private Long id;
    private User owner;
    private List<User> developers;
    private String name;
    private String description;

    public Project(){
    }

    public Project(Long id, String description, String name, User owner) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<User> developers) {
        this.developers = developers;
    }


    public enum Sort implements SortOption<Project> {
        NAME(R.string.sort_name, new ProjectByNameComparator()),
        SCORE(R.string.sort_score, new ProjectByScoreComparator());

        public final int i18nFilterName;
        public final Comparator<Project> comparator;


        Sort(int i18nFilterName, Comparator<Project> comparator) {
            this.i18nFilterName = i18nFilterName;
            this.comparator = comparator;
        }

        /**
         * Returns the {@link Sort} that this name represents. Returns null if not found.
         *
         * @param name
         *         The {@link #name()} of one of the elements in this enum
         *
         * @return The {@link Sort} that this name represents, or null if non found
         */
        public static Sort tryValueOf(String name) {
            try {
                return valueOf(name);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }

        @Override
        public int getI18nTitleResId() {
            return i18nFilterName;
        }

        @Override
        public Comparator<Project> getComparator() {
            return comparator;
        }
    }



    @Override
    public String toString() {
        return "Project{" +
                "description='" + description + '\'' +
                ", developers=" + developers +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                '}';
    }
}
