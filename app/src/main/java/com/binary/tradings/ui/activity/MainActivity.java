package com.binary.tradings.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.binary.tradings.adapters.DealsAdapter;
import com.binary.tradings.loaders.DealsLoader;
import com.binary.tradings.model.Deal;
import com.binary.tradings.model.Rate;
import com.binary.tradings.ui.fragment.DealsFragment;
import com.binary.tradings.ui.fragment.DetailsFragment;
import com.binary.tradings.ui.fragment.NavigationDrawerFragment;
import com.binary.tradings.R;
import com.binary.tradings.ui.fragment.SettingsFragment;
import com.binary.tradings.util.NetworkRequestHelper;

import java.util.List;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, DealsFragment.OnFragmentInteractionListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private LoaderManager.LoaderCallbacks<List<Deal>> listLoaderCallbacks;
    private LoaderManager.LoaderCallbacks<List<Rate>> historyLoaderCallbacks;
    public static final String SHARED_PREFERENCES_KEY = "binary.shared.prefs";
    public static final String UPDATE_PERIOD_KEY = "binary.shared.prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        switch(position){
            case 0:
                fragment = new SettingsFragment();
                break;
            case 1:
                fragment = DealsFragment.newInstance(true);
                listLoaderCallbacks = (LoaderManager.LoaderCallbacks<List<Deal>>)fragment;
                break;
            case 2:
                shareApp();
                return;
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void setDetailsFragment(Deal deal){
        FragmentManager fragmentManager = getSupportFragmentManager();
        DetailsFragment fragment = DetailsFragment.newInstance();
        historyLoaderCallbacks = fragment;
        fragment.setDeal(deal);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void updateDealsFragmentContent(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = DealsFragment.newInstance(true);
        listLoaderCallbacks = (LoaderManager.LoaderCallbacks<List<Deal>>)fragment;
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setLogo(R.drawable.appicon);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onFragmentInteraction(final Deal deal) {
        setDetailsFragment(deal);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    private void shareApp(){
        Intent in = new Intent(Intent.ACTION_SEND);
        in.setType("text/plain");
        in.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + " " + getString(R.string.app_name));
        startActivity(Intent.createChooser(in, "Share via"));
    }

    public LoaderManager.LoaderCallbacks<List<Deal>> getListLoaderCallbacks() {
        return listLoaderCallbacks;
    }

    public LoaderManager.LoaderCallbacks<List<Rate>> getHistoryLoaderCallbacks() {
        return historyLoaderCallbacks;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
