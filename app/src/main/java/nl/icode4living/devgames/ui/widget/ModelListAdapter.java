package nl.icode4living.devgames.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.Collections;
import java.util.List;

import nl.icode4living.devgames.util.L;
import nl.icode4living.devgames.util.SortOption;

/**
 * An ArrayAdapter that takes care of binding the data from the {@link ModelClass} to a view. Use
 * {@link #setData(List, SortOption)} to set new data.
 * <p>
 * Use with:
 * <ul>
 * <li>{@link ModelListFragment}</li>
 * <li>{@link ModelListLoader}</li>
 * </ul>
 * </p>
 *
 *
 * @param <ModelClass>
 *         The type of the ORMLite model class that represents a table in the local database
 */
public abstract class ModelListAdapter<ModelClass> extends ArrayAdapter<ModelClass> {

    private final SortOption<ModelClass> defaultSortOption;
    private SortOption<ModelClass> currentSortOption;

    public ModelListAdapter(Context context) {
        this(context, null);
    }

    public ModelListAdapter(Context context, SortOption<ModelClass> defaultSortOption) {
        super(context, 0);
        this.defaultSortOption = defaultSortOption;
    }


    /**
     * Clears the current list of data and adds the supplied list of data. {@link #notifyDataSetChanged()} is called
     * after adding the data.
     *
     * @param data
     *         The new data
     */
    public void setData(List<ModelClass> data, SortOption<ModelClass> applySortOption) {

        L.v("setData");

        // clear() calls notifyDataSetChanged(), which means that the ListView is rendered again with 0 elements.
        // That means, the ListView scrolls to the top. Right after that, the new data is added. The ListView is smart and
        // sees somehow that the data is somehow the same (as Romain Guy claims at
        // https://www.youtube.com/watch?v=wDBM6wVEO70#t=52m25s ) and it will keep the scroll position
        setNotifyOnChange(false);

        // Clear all current elements
        clear();

        // If the applySortOption == null, apply the default one
        this.currentSortOption = applySortOption != null ? applySortOption : defaultSortOption;

        if (data != null && data.size() != 0 && currentSortOption != null && currentSortOption.getComparator() != null) {
            Collections.sort(data, currentSortOption.getComparator());
        }

        L.v("data size: {0}, using {1} -> {2}", data != null ? data.size() : 0,
                currentSortOption != null ? "currentSortOption" : "defaultSortOption",
                currentSortOption != null ? currentSortOption.getClass() : "none");

        // And fill the list with the new data, if that is available
        if (data != null) {

            if (android.os.Build.VERSION.SDK_INT >= 11) {
                // Available API >= 11
                addAll(data);
            }
            else {
                // Support for API < 11
                for (ModelClass item : data) {
                    add(item);
                }
            }
        }

        notifyDataSetChanged();
    }


    /**
     * Returns the default SortOption that was given with the constructor.
     *
     * @return The default SortOption that was given with the constructor.
     */
    public SortOption<ModelClass> getDefaultSortOption() {
        return defaultSortOption;
    }

    /**
     * Returns the current applied SortOption.
     *
     * @return The current applied SortOption.
     */
    public SortOption<ModelClass> getCurrentSortOption() {
        return currentSortOption;
    }

    /**
     * Sets the current SortOption. Invalidates the list by calling {@link #notifyDataSetInvalidated()}.
     *
     * @param currentSortOption
     *         The SortOption to be applied
     */
    public void setCurrentSortOption(SortOption<ModelClass> currentSortOption) {

        // Use defaultSortOption if the given currentSortOption is null
        if (currentSortOption != null) {
            this.currentSortOption = currentSortOption;
        }
        else {
            L.w("param currentSortOption is null! Falling back to defaultSortOption");
            this.currentSortOption = defaultSortOption;
        }

        if (currentSortOption != null && currentSortOption.getComparator() != null) {
            sort(currentSortOption.getComparator());
        }
        else {
            L.w("Current sort option or its comparator is null! List not sorted.");
        }


        notifyDataSetInvalidated();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = newView(position, LayoutInflater.from(getContext()), parent);
        }

        // Let the sub class bind the data to the view
        bindView(position, getItem(position), convertView);

        return convertView;
    }

    public abstract View newView(int position, LayoutInflater inflater, ViewGroup parent);

    public abstract void bindView(int position, ModelClass model, View view);
}