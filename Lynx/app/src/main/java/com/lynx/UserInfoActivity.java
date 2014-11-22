package com.lynx;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UserInfoActivity extends Activity implements OnTaskComplete {

    private Button mFacebookView;
    private Button mGoogleView;
    private Button mLinkedInView;
    private Button mTwitterView;

    private TextView mUsernameView;

    private View mUserData;
    private View mProgressView;

    private Intent mIntent;

    private GetUserDataTask mGetUserDataTask = null;
    private MakeFacebookRequestTask mMakeFacebookRequestTask = null;
    private MakeLinkedInRequestTask mMakeLinkedInRequestTask = null;
    private MakeGoogleRequestTask mMakeGoogleRequestTask = null;
    private MakeTwitterRequestTask mMakeTwitterRequestTask = null;

    private JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mIntent = getIntent();

        mUsernameView = (TextView) findViewById(R.id.userInfoUsername);
        mFacebookView = (Button) findViewById(R.id.make_facebook_request);
        mLinkedInView = (Button) findViewById(R.id.make_linkedin_request);
        mGoogleView = (Button) findViewById(R.id.make_google_request);
        mTwitterView = (Button) findViewById(R.id.make_twitter_request);

        mUserData = findViewById(R.id.userData);
        mProgressView = findViewById(R.id.user_data_progress);

        attemptGetUserData();
    }

    public void attemptGetUserData(){
        if (mGetUserDataTask != null) {
            return;
        }

        showProgress(true);
        mGetUserDataTask = new GetUserDataTask(mIntent.getStringExtra("username"), this);
        mGetUserDataTask.execute((Void) null);
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

            mUserData.setVisibility(show ? View.GONE : View.VISIBLE);
            mUserData.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUserData.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mUserData.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_info, menu);
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

    public class GetUserDataTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final OnTaskComplete mListener;
        private JSONObject mData;

        GetUserDataTask (String username, OnTaskComplete listener) {
            mUsername = username;
            mListener = listener;
            mData = null;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = "http://143.215.109.184/t4j/get_user_public_info.php";

            List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new BasicNameValuePair("username", mUsername));

            JSONObject json = jsonParser.makeHttpRequest(url, "GET", requestParams);

            try {
                int success = json.getInt("success");
                if (success == 1) {
                    JSONArray userData = json.getJSONArray("user");
                    mData = userData.getJSONObject(0);
                }
                return success == 1;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetUserDataTask = null;
            showProgress(false);

            if (success) {
                // Find a way to display data
                String displayName = "Lost Connection";
                boolean fb = false;
                boolean google = false;
                boolean twitter = false;
                boolean linkedin = false;

                try {
                    displayName = mData.getString("displayName");
                    fb = Integer.parseInt(mData.getString("fb")) == 1;
                    google = Integer.parseInt(mData.getString("google")) == 1;
                    twitter = Integer.parseInt(mData.getString("twitter")) == 1;
                    linkedin = Integer.parseInt(mData.getString("linkedin")) == 1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mListener.OnTaskCompleted(displayName, fb, google, twitter, linkedin);
            }
        }

        @Override
        protected void onCancelled() {
            mGetUserDataTask = null;
            showProgress(false);
        }
    }

    @Override
    public void OnTaskCompleted(String displayName, boolean fb, boolean linkedin, boolean google, boolean twitter) {
        mUsernameView.setText(displayName);
        mFacebookView.setEnabled(fb);
        mLinkedInView.setEnabled(linkedin);
        mGoogleView.setEnabled(google);
        mTwitterView.setEnabled(twitter);
    }

    public void makeFacebookRequest (View view) {
        if (mMakeFacebookRequestTask != null) {
            return;
        }

        showProgress(true);
        mMakeFacebookRequestTask = new MakeFacebookRequestTask(mIntent.getStringExtra("myUsername"), mIntent.getStringExtra("username"));
        mMakeFacebookRequestTask.execute((Void) null);
    }

    public class MakeFacebookRequestTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mMyUsername;
        private JSONObject mData;

        MakeFacebookRequestTask (String myUsername, String username) {
            mMyUsername = myUsername;
            mUsername = username;
            mData = null;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = "http://143.215.109.184/t4j/submit_request.php";

            List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new BasicNameValuePair("reqFrom", mMyUsername));
            requestParams.add(new BasicNameValuePair("reqTo", mUsername));
            requestParams.add(new BasicNameValuePair("fb", "1"));

            JSONObject json = jsonParser.makeHttpRequest(url, "POST", requestParams);

            try {
                int success = json.getInt("success");
                if (success == 1) {
                    JSONArray userData = json.getJSONArray("user");
                    mData = userData.getJSONObject(0);
                }
                return success == 1;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetUserDataTask = null;
            showProgress(false);

            finish();
        }

        @Override
        protected void onCancelled() {
            mGetUserDataTask = null;
            showProgress(false);
        }
    }

    public void makeLinkedInRequest (View view) {
        if (mMakeLinkedInRequestTask != null) {
            return;
        }

        showProgress(true);
        mMakeLinkedInRequestTask = new MakeLinkedInRequestTask(mIntent.getStringExtra("myUsername"), mIntent.getStringExtra("username"));
        mMakeLinkedInRequestTask.execute((Void) null);
    }

    public class MakeLinkedInRequestTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mMyUsername;
        private JSONObject mData;

        MakeLinkedInRequestTask (String myUsername, String username) {
            mMyUsername = myUsername;
            mUsername = username;
            mData = null;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = "http://143.215.109.184/t4j/submit_request.php";

            List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new BasicNameValuePair("reqFrom", mMyUsername));
            requestParams.add(new BasicNameValuePair("reqTo", mUsername));
            requestParams.add(new BasicNameValuePair("linkedin", "1"));

            JSONObject json = jsonParser.makeHttpRequest(url, "POST", requestParams);

            try {
                int success = json.getInt("success");
                if (success == 1) {
                    JSONArray userData = json.getJSONArray("user");
                    mData = userData.getJSONObject(0);
                }
                return success == 1;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetUserDataTask = null;
            showProgress(false);

            finish();
        }

        @Override
        protected void onCancelled() {
            mGetUserDataTask = null;
            showProgress(false);
        }
    }

    public void makeGoogleRequest (View view) {
        if (mMakeGoogleRequestTask != null) {
            return;
        }

        showProgress(true);
        mMakeGoogleRequestTask = new MakeGoogleRequestTask(mIntent.getStringExtra("myUsername"), mIntent.getStringExtra("username"));
        mMakeGoogleRequestTask.execute((Void) null);
    }

    public class MakeGoogleRequestTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mMyUsername;
        private JSONObject mData;

        MakeGoogleRequestTask (String myUsername, String username) {
            mMyUsername = myUsername;
            mUsername = username;
            mData = null;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = "http://143.215.109.184/t4j/submit_request.php";

            List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new BasicNameValuePair("reqFrom", mMyUsername));
            requestParams.add(new BasicNameValuePair("reqTo", mUsername));
            requestParams.add(new BasicNameValuePair("google", "1"));

            JSONObject json = jsonParser.makeHttpRequest(url, "POST", requestParams);

            try {
                int success = json.getInt("success");
                if (success == 1) {
                    JSONArray userData = json.getJSONArray("user");
                    mData = userData.getJSONObject(0);
                }
                return success == 1;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetUserDataTask = null;
            showProgress(false);

            finish();
        }

        @Override
        protected void onCancelled() {
            mGetUserDataTask = null;
            showProgress(false);
        }
    }

    public void makeTwitterRequest (View view) {
        if (mMakeTwitterRequestTask != null) {
            return;
        }

        showProgress(true);
        mMakeTwitterRequestTask = new MakeTwitterRequestTask(mIntent.getStringExtra("myUsername"), mIntent.getStringExtra("username"));
        mMakeTwitterRequestTask.execute((Void) null);
    }

    public class MakeTwitterRequestTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mMyUsername;
        private JSONObject mData;

        MakeTwitterRequestTask (String myUsername, String username) {
            mMyUsername = myUsername;
            mUsername = username;
            mData = null;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = "http://143.215.109.184/t4j/submit_request.php";

            List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new BasicNameValuePair("reqFrom", mMyUsername));
            requestParams.add(new BasicNameValuePair("reqTo", mUsername));
            requestParams.add(new BasicNameValuePair("twitter", "1"));

            JSONObject json = jsonParser.makeHttpRequest(url, "POST", requestParams);

            try {
                int success = json.getInt("success");
                if (success == 1) {
                    JSONArray userData = json.getJSONArray("user");
                    mData = userData.getJSONObject(0);
                }
                return success == 1;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetUserDataTask = null;
            showProgress(false);

            finish();
        }

        @Override
        protected void onCancelled() {
            mGetUserDataTask = null;
            showProgress(false);
        }
    }
}
