package nl.icode4living.devgames.ui.fragment;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public interface DevGamesTab {

    interface OnSortActionListener {
        void onSortRequest();
    }

    interface OnRefreshRequestListener {
        void onRefreshRequest();
    }
}
