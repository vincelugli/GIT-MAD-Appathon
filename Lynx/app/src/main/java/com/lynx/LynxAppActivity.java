package com.lynx;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LynxAppActivity extends Activity implements OnTaskCompleteGetUsers{

    private String mUsername;

    private Intent mIntent;

    private LinearLayout mLayout;
    private LinearLayout mFragmentContainer;

    private GetAllUsersTask mGetAllUsersTask = null;

    private View mProgressView;
    private View mLynxForm;

    private JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lynx_app);
//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new UserFragment())
//                    .commit();
//        }

        mFragmentContainer = (LinearLayout) findViewById(R.id.userLinearLayout);

        mLayout = new LinearLayout(this);
        mLayout.setOrientation(LinearLayout.VERTICAL);

        mLayout.setId(R.id.layout);

        mIntent = getIntent();

        mUsername = mIntent.getStringExtra("username");

        mProgressView = findViewById(R.id.lynx_progress);
        mLynxForm = findViewById(R.id.lynxScrollView);

        attemptGetAllUsers();

//        getFragmentManager().beginTransaction().add(layout.getId(), UserFragment.newInstance(mUsername), "user 1").commit();
//        getFragmentManager().beginTransaction().add(layout.getId(), UserFragment.newInstance("USER 2"), "user 2").commit();
//        getFragmentManager().beginTransaction().add(layout.getId(), UserFragment.newInstance("USER 3"), "user 3").commit();
//        getFragmentManager().beginTransaction().add(layout.getId(), UserFragment.newInstance("USER 4"), "user 4").commit();
//
//        mFragmentContainer.addView(mLayout);
    }

    public void attemptGetAllUsers() {
        if (mGetAllUsersTask != null) {
            return;
        }

        showProgress(true);
        mGetAllUsersTask = new GetAllUsersTask(this, mUsername);
        mGetAllUsersTask.execute((Void) null);
    }
    public class GetAllUsersTask extends AsyncTask<Void, Void, Boolean> {

        List<String> mUsernames, mDisplayNames;
        String mUsername;

        GetAllUsersTask (OnTaskCompleteGetUsers listener, String username) {
            mUsernames = new ArrayList<String>();
            mDisplayNames = new ArrayList<String>();
            mUsername = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = "http://143.215.109.184/t4j/get_friends.php";
            List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new BasicNameValuePair("username", mUsername));

            JSONObject json = jsonParser.makeHttpRequest(url, "GET", requestParams);

            try {
                int success = json.getInt("success");
                JSONArray reqData = json.getJSONArray("reqData");
                for (int i = 0; i < reqData.length(); i++) {
                    mUsernames.add(reqData.getJSONObject(i).getString("username"));
                    mDisplayNames.add(reqData.getJSONObject(i).getString("displayName"));
                }

                return success == 1;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetAllUsersTask = null;
            showProgress(false);
            OnTaskCompleted(mUsernames, mDisplayNames);
        }

        @Override
        protected void onCancelled() {
            mGetAllUsersTask = null;
            showProgress(false);
        }
    }

    public void OnTaskCompleted(List<String> usernames, List<String> displayNames) {
        for (int i = 0; i < usernames.size(); i++) {
            getFragmentManager().beginTransaction().add(mLayout.getId(),
                    UserFragment.newInstance(usernames.get(i), displayNames.get(i)),
                    usernames.get(i)).commit();
        }

        mFragmentContainer.addView(mLayout);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLynxForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mLynxForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLynxForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLynxForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lynx_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_my_requests) {
            Intent myRequestIntent = new Intent(this, RequestsActivity.class);
            myRequestIntent.putExtra("username", mUsername);
            startActivity(myRequestIntent);
            return true;
        }
        else if (id == R.id.action_my_profile) {
            Intent myProfileIntent = new Intent(this, MyProfileActivity.class);
            startActivity(myProfileIntent );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class UserFragment extends Fragment {

        public UserFragment() {
        }

        public static UserFragment newInstance(String username, String displayName) {

            UserFragment fragment = new UserFragment();

            Bundle b = new Bundle();
            b.putString("username", username);
            b.putString("displayName", displayName);
            fragment.setArguments(b);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View v =  inflater.inflate(R.layout.fragment_lynx_app, container, false);

            ((TextView) v.findViewById(R.id.userText)).setText(getArguments().getString("displayName"));
            ((Button) v.findViewById(R.id.viewProfileButton)).setTag(getArguments().getString("username"));
            return v;
        }
    }

    public void goToUserInfo(View view) {
        Intent userInfoIntent = new Intent(this, UserInfoActivity.class);
        userInfoIntent.putExtra("myUsername", mUsername);
        userInfoIntent.putExtra("username", ((Button) view).getTag().toString());

        startActivity(userInfoIntent);
    }
}
