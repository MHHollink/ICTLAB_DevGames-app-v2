package nl.icode4living.devgames2.models;

import java.util.Comparator;
import java.util.List;

import nl.icode4living.devgames2.R;
import nl.icode4living.devgames2.ui.widget.UserByNameComparator;
import nl.icode4living.devgames2.ui.widget.UserByScoreComparator;
import nl.icode4living.devgames2.util.SortOption;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class User {


    private Long id;
    private String username;
    private String gitUsername;
    private String gcmId;
    private String firstName;
    private String tween;
    private String lastName;
    private int age;
    private String mainJob;
    private List<Project> projects;
    private double totalScore;

    public User(Long id, String username, String gitUsername, String gcmId, String firstName, String tween, String lastName, int age, String mainJob) {
        this.id = id;
        this.username = username;
        this.gitUsername = gitUsername;
        this.gcmId = gcmId;
        this.firstName = firstName;
        this.tween = tween;
        this.lastName = lastName;
        this.age = age;
        this.mainJob = mainJob;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGitUsername() {
        return gitUsername;
    }

    public void setGitUsername(String gitUsername) {
        this.gitUsername = gitUsername;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getTween() {
        return tween;
    }

    public void setTween(String tween) {
        this.tween = tween;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMainJob() {
        return mainJob;
    }

    public void setMainJob(String mainJob) {
        this.mainJob = mainJob;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public enum Sort implements SortOption<User> {
        NAME(R.string.sort_name, new UserByNameComparator()),
        SCORE(R.string.sort_score, new UserByScoreComparator());

        public final int i18nFilterName;
        public final Comparator<User> comparator;


        Sort(int i18nFilterName, Comparator<User> comparator) {
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
        public Comparator<User> getComparator() {
            return comparator;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", gitUsername='" + gitUsername + '\'' +
                ", gcmId='" + gcmId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", tween='" + tween + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mainJob='" + mainJob + '\'' +
                ", projects=" + projects +
                ", totalScore=" + totalScore +
                '}';
    }

    public void logout() {
        setGcmId(null);
    }

    public double getTotalScore() {
        return totalScore;
    }
}
