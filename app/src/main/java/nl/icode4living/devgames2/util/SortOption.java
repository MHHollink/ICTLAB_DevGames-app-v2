package nl.icode4living.devgames2.util;

import java.util.Comparator;

/**
 * TODO: provide class level documentation
 */
public interface SortOption<Model> {

    /**
     * The i18n title string resource id for this sort option. If an non valid id is supplied, an empty string is used
     *
     * @return The i18n title string resource id for this sort option
     */
    int getI18nTitleResId();

    /**
     * The comparator that takes care of sorting the list.
     *
     * @return The comparator that takes care of sorting the list.
     */
    Comparator<Model> getComparator();

    /**
     * Returns the string representation of this sort option. This will be used to store the sort option preference of
     * the user. Make sure this is a unique name.
     *
     * @return The string representation of this sort option.
     */
    String name();
}
