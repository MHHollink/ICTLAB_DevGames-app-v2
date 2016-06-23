package nl.icode4living.devgames2.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import nl.icode4living.devgames2.R;
import nl.icode4living.devgames2.connection.task.poll.PollProjectTask;
import nl.icode4living.devgames2.event.BusProvider;
import nl.icode4living.devgames2.ui.fragment.ProjectFragment;
import nl.icode4living.devgames2.ui.fragment.UsersListFragment;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class ProjectActivity extends DevGamesActivity {

    public static final String PROJECT_ID = "project_id";

    public static final boolean[] showSortInActionBar    = new boolean[]{false,  true};
    public static final boolean[] showRefreshInActionBar = new boolean[]{ true,  true};

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private Handler handler;

    private Long projectId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setSupportActionBar(getToolbar());

        projectId = getIntent().getExtras().getLong(PROJECT_ID);
        new PollProjectTask(this, projectId).executeThreaded();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle(1);
        bundle.putLong(PROJECT_ID, projectId);

        ProjectFragment projectFragment = new ProjectFragment();
        projectFragment.setArguments(bundle);
        adapter.addFragment(projectFragment,"Project");

        UsersListFragment usersListFragment = new UsersListFragment();
        usersListFragment.setArguments(bundle);
        adapter.addFragment(usersListFragment, "Developers");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        BusProvider.getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        BusProvider.getBus().unregister(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
