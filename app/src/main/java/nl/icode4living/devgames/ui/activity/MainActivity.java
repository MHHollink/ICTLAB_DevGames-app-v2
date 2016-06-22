package nl.icode4living.devgames.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import nl.icode4living.devgames.DevGamesApplication;
import nl.icode4living.devgames.R;
import nl.icode4living.devgames.connection.task.GcmRegistrationTask;
import nl.icode4living.devgames.event.LogoutEvent;
import nl.icode4living.devgames.ui.fragment.DevGamesTab;
import nl.icode4living.devgames.ui.fragment.ProfileFragment;
import nl.icode4living.devgames.ui.fragment.ProjectsListFragment;
import nl.icode4living.devgames.util.L;
import nl.icode4living.devgames.util.Utils;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class MainActivity extends DevGamesActivity {

    public static final boolean[] showSortInActionBar    = new boolean[]{false,  true};
    public static final boolean[] showRefreshInActionBar = new boolean[]{ true,  true};

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setSupportActionBar(getToolbar());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        ProfileFragment profileFragment = new ProfileFragment();
        Bundle profileBundle = new Bundle(1);
        profileBundle.putLong(ProfileFragment.USER_LOCAL_ID, DevGamesApplication.get(this).getLoggedInUser().getId());
        profileFragment.setArguments(profileBundle);
        adapter.addFragment(profileFragment,"Profile");

        ProjectsListFragment projectsListFragment = new ProjectsListFragment();
        adapter.addFragment(projectsListFragment, "Projects");

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

        if (Utils.playServicesAvailable(this)) {

            // Check registration id
            new GcmRegistrationTask(this).executeThreaded();

        } else {

            L.w("Google Play Services is not available on this device! Showing dialog = {0}",
                    getPreferenceManager().getShowPlayServicesDialog());

            // Don't display the dialog again if the user already dismissed it earlier
            if (!getPreferenceManager().getShowPlayServicesDialog()) {
                return;
            }

            final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainActivity.this);

            if (GooglePlayServicesUtil.isUserRecoverableError(result)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.common_google_play_services_unsupported_title)
                        .setMessage(R.string.play_services_user_recoverable)
                        .setPositiveButton(R.string.play_services_install, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                GooglePlayServicesUtil.getErrorDialog(result, MainActivity.this, 0);

                                // Hide this dialog next time
                                getPreferenceManager().setShowPlayServicesDialog(false);

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.play_services_continue_without_installing, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Hide this dialog next time
                                getPreferenceManager().setShowPlayServicesDialog(false);

                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.common_google_play_services_unsupported_title)
                        .setMessage(R.string.play_services_non_user_recoverable)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Hide this dialog next time
                                getPreferenceManager().setShowPlayServicesDialog(false);

                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (!isFinishing()) {
            L.d("selectedPage: " + viewPager.getCurrentItem());

            menu.findItem(R.id.menu_btn_sort).setVisible(showSortInActionBar[viewPager.getCurrentItem()]);
            menu.findItem(R.id.menu_btn_refresh).setVisible(showRefreshInActionBar[viewPager.getCurrentItem()]);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        L.d("onOptionsItemSelected, selectedPage: {0}, item: {1}", viewPager.getCurrentItem(), item.getTitle());

        if (adapter == null) {
            L.e("Menu Options item selected could not be executed, because the adapter is null!");
            return false;
        }

        Fragment lastFragment = (Fragment) adapter.instantiateItem(viewPager, viewPager.getCurrentItem());

        int i = item.getItemId();
        if (i == R.id.menu_btn_sort) {
            L.v("item=menu_btn_sort");
            if (lastFragment != null) {
                if (lastFragment instanceof DevGamesTab.OnSortActionListener) {
                    // If it is the StatusFragment, pass it on
                    ((DevGamesTab.OnSortActionListener) lastFragment).onSortRequest();
                }
                else {
                    L.w("Sort action was clicked, but lastFragment does not implement TeamUpTabPage.OnSortActionListener!");
                    return false;
                }
            }
            return true;
        }
        else if (i == R.id.menu_btn_refresh) {
            L.v("item=menu_btn_refresh");
            if (lastFragment != null) {
                if (lastFragment instanceof DevGamesTab.OnRefreshRequestListener) {
                    ((DevGamesTab.OnRefreshRequestListener) lastFragment).onRefreshRequest();
                }
                else {
                    L.w("Refresh action was clicked, but lastFragment does not implement TeamUpTabPage.OnRefreshRequestListener!");
                    return false;
                }
            }
            return true;
        }
        else if (i == R.id.menu_btn_settings) {
            //startActivity(new Intent(this, SettingsActivity.class));

            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe
    @Override
    public void onLogoutEvent(LogoutEvent event) {
        L.d("event caught!");
        super.onLogoutEvent(event);
    }

}
