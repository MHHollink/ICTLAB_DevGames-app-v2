package nl.icode4living.devgames.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import nl.icode4living.devgames.DevGamesApplication;
import nl.icode4living.devgames.R;
import nl.icode4living.devgames.connection.task.poll.PollUserTask;
import nl.icode4living.devgames.event.BusProvider;
import nl.icode4living.devgames.event.PollUserFinishedEvent;
import nl.icode4living.devgames.models.User;
import nl.icode4living.devgames.util.L;
import nl.icode4living.devgames.util.Utils;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class ProfileFragment extends Fragment implements DevGamesTab.OnRefreshRequestListener {

    public static final String USER_LOCAL_ID = "user_local_id";

    private Long initialUserId = null;
    private User user = null;

    private TextView nameView;
    private TextView scoreView;
    private TextView projectsView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        if (b != null) {
            initialUserId = b.getLong(USER_LOCAL_ID);

            if(initialUserId.longValue() != DevGamesApplication.get(this).getLoggedInUser().getId().longValue()) {
                new PollUserTask(getContext(), initialUserId).executeThreaded();
            } else {
                user = DevGamesApplication.get(this).getLoggedInUser();
            }
        }
        else {
            L.e("No LocalUserId given!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameView = (TextView) view.findViewById(R.id.txt_name);
        scoreView = (TextView) view.findViewById(R.id.txt_score);
        projectsView = (TextView) view.findViewById(R.id.txt_projects);

        updateUI();
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

    private void updateUI(){
        L.d("Called, User : {0}", user);

        if (user == null) {
            new PollUserTask(getContext(), initialUserId).executeThreaded();
            return;
        }

        nameView.setText(user.getUsername());
        scoreView.setText(String.valueOf(user.getTotalScore()));
        projectsView.setText(String.valueOf(
                user.getProjects() != null ? user.getProjects().size() : 0
        ));
    }


    @Subscribe
    public void onPollUserFinished(PollUserFinishedEvent event) {
        L.v("{0}", event);

        if (!event.success)
            Utils.createToast(this, "Een update kon niet met de server worden gesynchonizeerd", Toast.LENGTH_SHORT);
        else {
            user = event.user;
            updateUI();
        }
    }

    @Override
    public void onRefreshRequest() {
        // TODO: 22-6-2016  
    }
}
