package nl.icode4living.devgames.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import nl.icode4living.devgames.R;
import nl.icode4living.devgames.connection.task.poll.PollProjectTask;
import nl.icode4living.devgames.event.BusProvider;
import nl.icode4living.devgames.event.PollProjectTaskFinishedEvent;
import nl.icode4living.devgames.models.Project;
import nl.icode4living.devgames.models.User;
import nl.icode4living.devgames.ui.activity.ProjectActivity;
import nl.icode4living.devgames.util.L;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class ProjectFragment extends Fragment implements DevGamesTab.OnRefreshRequestListener {

    private Long initialProjectId = null;
    private Project project = null;

    private TextView txtName;
    private TextView txtScore;
    private TextView txtDevelopers;
    private TextView txtCommits;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        if (b != null) {
            initialProjectId = b.getLong(ProjectActivity.PROJECT_ID);
            new PollProjectTask(getContext(), initialProjectId);
        }
        else {
            L.e("No initialProjectId given!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.project_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtName = (TextView) view.findViewById(R.id.txt_name);
        txtScore = (TextView) view.findViewById(R.id.txt_score);
        txtDevelopers = (TextView) view.findViewById(R.id.txt_developers);
        txtCommits = (TextView) view.findViewById(R.id.txt_commits);
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


    @Subscribe
    public void onPollProjectTaskFinishedEvent(PollProjectTaskFinishedEvent event) {
        project = event.project;

        updateUI();
    }

    private void updateUI() {
        txtName.setText(project.getName());
        txtDevelopers.setText(String.valueOf(project.getDevelopers().size()));

        double score = 0;
        for (User user : project.getDevelopers()) score += user.getTotalScore();
        txtScore.setText(String.valueOf(score));

    }

    @Override
    public void onRefreshRequest() {
        // TODO: 22-6-2016  
    }
}
