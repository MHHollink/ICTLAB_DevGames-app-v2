package nl.icode4living.devgames.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.squareup.otto.Subscribe;

import java.util.List;

import nl.icode4living.devgames.R;
import nl.icode4living.devgames.connection.task.poll.PollMyProjectTask;
import nl.icode4living.devgames.event.BusProvider;
import nl.icode4living.devgames.event.PollProjectsTaskFinishedEvent;
import nl.icode4living.devgames.models.Project;
import nl.icode4living.devgames.ui.activity.ProjectActivity;
import nl.icode4living.devgames.ui.widget.ProjectListViewAdapter;
import nl.icode4living.devgames.util.L;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class ProjectsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, DevGamesTab.OnRefreshRequestListener, DevGamesTab.OnSortActionListener {

    protected SwipeRefreshLayout swipeRefreshLayout;
    private boolean listShown;
    private ViewGroup listContainer;
    private ProgressBar refreshLayout;
    private ProjectListViewAdapter adapter;

    public ProjectsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_model_list, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listContainer = (ViewGroup) view.findViewById(R.id.list_container);
        final ListView listView = (ListView) view.findViewById(android.R.id.list);
        refreshLayout = (ProgressBar) view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_bar);
        LinearLayout emptyView = (LinearLayout) view.findViewById(android.R.id.empty);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.spring_light,
                R.color.highland_dark,
                R.color.whiskey_dark
        );

        if (listContainer.findViewById(android.R.id.list) == null
                || listContainer.findViewById(android.R.id.empty) == null) {
            L.e("List and empty view are not siblings! This will cause problems when you use the empty ");
            return;
        }

        adapter = new ProjectListViewAdapter(getContext(), Project.Sort.NAME);
        listView.setEmptyView(emptyView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Project project = (Project) listView.getItemAtPosition(position);

                Intent i = new Intent(getActivity(), ProjectActivity.class);
                i.putExtra(ProjectActivity.PROJECT_ID, project.getId());
                startActivity(i);
            }
        });

        new PollMyProjectTask(getContext()).executeThreaded();
        setListShown(true, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getBus().unregister(this);
    }

    @Override
    public void onRefresh() {
        new PollMyProjectTask(getContext()).executeThreaded();
    }

    /**
     * Hides the progress indicator and makes the ListView visible when true, otherwise the opposite.
     *
     * @param shown
     *         True when the list should be shown, false when the list should be hidden
     * @param animate
     *         Whether an animation should be used for the transition from hidden to visible / visible to hidden
     */
    protected void setListShown(boolean shown, boolean animate) {
        if (refreshLayout == null || swipeRefreshLayout == null) {
            L.e("Either one or both of the progress indicators are null! refresh_layout null = {0}, swipeRefreshLayout null = {1}",
                    refreshLayout == null, swipeRefreshLayout == null);
            return;
        }

        if (listShown == shown) {
            return;
        }

        listShown = shown;

        if (shown) {
            if (animate) {
                refreshLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                listContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            }
            else {
                refreshLayout.clearAnimation();
                listContainer.clearAnimation();
            }

            refreshLayout.setVisibility(View.GONE);
            listContainer.setVisibility(View.VISIBLE);
        }
        else {
            if (animate) {
                refreshLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                listContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            }
            else {
                refreshLayout.clearAnimation();
                listContainer.clearAnimation();
            }

            refreshLayout.setVisibility(View.VISIBLE);
            listContainer.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onPollProjectTaskFinishedEvent(PollProjectsTaskFinishedEvent event) {
        List<Project> projects = event.projects;

        if (projects != null && !projects.isEmpty()) {
            adapter.setData(projects, adapter.getCurrentSortOption());
            adapter.notifyDataSetChanged();
            setListShown(true, true);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefreshRequest() {
        onRefresh();
    }

    @Override
    public void onSortRequest() {
        // TODO: 22-6-2016  
    }
}
