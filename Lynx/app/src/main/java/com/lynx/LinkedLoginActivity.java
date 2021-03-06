package com.lynx;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class LinkedLoginActivity extends Activity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_login);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getActionBar().setSelectedNavigationItem(position%5);
                    }
                }
        );

        actionBar.addTab(
                actionBar.newTab()
                        .setText("Facebook")
                        .setTabListener(this)
        );
        actionBar.addTab(
                actionBar.newTab()
                        .setText("LinkedIn")
                        .setTabListener(this)
        );
        actionBar.addTab(
                actionBar.newTab()
                        .setText("Google+")
                        .setTabListener(this)
        );
        actionBar.addTab(
                actionBar.newTab()
                        .setText("Twitter")
                        .setTabListener(this)
        );
        actionBar.addTab(
                actionBar.newTab()
                        .setText("Go")
                        .setTabListener(this)
        );

        Intent thisIntent = getIntent();
        mUsername = thisIntent.getStringExtra("username");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.linked_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position%5) {
                case 0:
                    return FacebookFragment.newInstance(position);
                case 1:
                    return LinkedInFragment.newInstance(position);
                case 2:
                    return GoogleFragment.newInstance(position);
                case 3:
                    return TwitterFragment.newInstance(position);
                case 4:
                    return GoFragment.newInstance(position);
            }
            return GoFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 6 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position%5) {
                case 0:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section4).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section5).toUpperCase(l);
                case 4:
                    return getString(R.string.title_section6).toUpperCase(l);
            }
            return null;
        }
    }

    public void goToLynx(View view) {
        Intent lynxIntent = new Intent(this, LynxAppActivity.class);
        lynxIntent.putExtra("username", mUsername);

        startActivity(lynxIntent);
    }
}
