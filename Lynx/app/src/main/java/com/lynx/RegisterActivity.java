package com.lynx;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.app.ActionBar.Tab;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


public class RegisterActivity extends Activity {

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

    private EditText mPasswordView;
    private EditText mUsernameView;
    private EditText mDisplayNameView;
    private EditText mPasswordConfirmView;
    private View mProgressView;
    private View mRegisterFormView;

    private UserRegisterTask mRegisterTask = null;

    private JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // Select tab when swiping.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                }
        );

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                // TODO Auto-generated method stub
                // show the given tab
            }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                // TODO Auto-generated method stub
                // hide the given tab
                if (tab.getPosition() == 0) {
                    attemptRegister();
                }
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
                // TODO Auto-generated method stub
                // probably ignore this event
            }
        };

        actionBar.addTab(
                actionBar.newTab()
                        .setText("Register")
                        .setTabListener(tabListener)
        );
        actionBar.addTab(
                actionBar.newTab()
                        .setText("Facebook")
                        .setTabListener(tabListener)
        );
        actionBar.addTab(
                actionBar.newTab()
                        .setText("LinkedIn")
                        .setTabListener(tabListener)
        );
        actionBar.addTab(
                actionBar.newTab()
                        .setText("Google+")
                        .setTabListener(tabListener)
        );
        actionBar.addTab(
                actionBar.newTab()
                        .setText("Twitter")
                        .setTabListener(tabListener)
        );
        actionBar.addTab(
                actionBar.newTab()
                        .setText("Go")
                        .setTabListener(tabListener)
        );


        mPasswordView = (EditText) findViewById(R.id.password_edit_text);
        mUsernameView = (EditText) findViewById(R.id.username_edit_text);
        mDisplayNameView = (EditText) findViewById(R.id.display_name_edit_text);
        mPasswordConfirmView = (EditText) findViewById(R.id.confirm_password_edit_text);

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    public void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        mPasswordView.setError(null);
        mUsernameView.setError(null);
        mDisplayNameView.setError(null);
        mPasswordConfirmView.setError(null);

        String username = mUsernameView.getText().toString();
        String displayName = mDisplayNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConfirm = mPasswordConfirmView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for valid username
        if (TextUtils.isEmpty(username)){
            mUsernameView.setError(getString(R.string.error_field_required));
        }
        else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for valid displayName
        if (TextUtils.isEmpty(displayName)){
            mDisplayNameView.setError(getString(R.string.error_field_required));
        }
        else if (!isUsernameValid(displayName)) {
            mDisplayNameView.setError(getString(R.string.error_invalid_username));
            focusView = mDisplayNameView;
            cancel = true;
        }

        // Check if password and passwordConfirm Match
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
        }

        if (TextUtils.isEmpty(passwordConfirm)) {
            mPasswordConfirmView.setError(getString(R.string.error_field_required));
        }
        else if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordConfirm)) {
            if (TextUtils.equals(password, passwordConfirm)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() < 16;
    }

    private boolean isPasswordValid(String password) {
        return password.length() < 24;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            switch (position) {
                case 0:
                    return RegisterFragment.newInstance(position);
                case 1:
                    return FacebookFragment.newInstance(position);
                case 2:
                    return LinkedInFragment.newInstance(position);
                case 3:
                    return GoogleFragment.newInstance(position);
                case 4:
                    return TwitterFragment.newInstance(position);
                case 5:
                    return GoFragment.newInstance(position);
            }
            return RegisterFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 6 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 4:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 5:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }


    public static class RegisterFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static RegisterFragment newInstance(int sectionNumber) {
            RegisterFragment fragment = new RegisterFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public RegisterFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_register,
                    container, false);
            return rootView;
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mDisplayName;
        private final String mPassword;
        private final String mPasswordConfirm;

        UserRegisterTask (String username, String displayName, String password, String passwordConfirm) {
            mUsername = username;
            mDisplayName = displayName;
            mPassword = password;
            mPasswordConfirm = passwordConfirm;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = "http://143.215.109.184/t4j/.php";
            System.out.println(url);
            List<NameValuePair> requestParams = new ArrayList<NameValuePair>();

            JSONObject json = jsonParser.makeHttpRequest(url, "POST", requestParams);

            try {
                int success = json.getInt("success");
                if (success == 1) {
                    return true;
                }
                else {
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;
            showProgress(false);

            if (success) {
                Intent lynxIntent = new Intent (getApplicationContext(), LynxActivity.class);
                startActivity(lynxIntent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            showProgress(false);
        }
    }

    public void goToLynx(View view) {
        Intent lynxIntent = new Intent(this, LynxActivity.class);

        startActivity(lynxIntent);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
