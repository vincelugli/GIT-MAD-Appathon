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


public class RequestsActivity extends Activity implements OnTaskCompleteRequest{

    private GetRequestsTask mGetRequestsTask = null;
    private AcceptRequestTask mAcceptRequestTask = null;

    private View mProgressView;
    private View mRequestsForm;

    private Intent mIntent;

    private JSONParser jsonParser = new JSONParser();

    private LinearLayout mLayout;
    private LinearLayout mFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new RequestFragment())
//                    .commit();
//        }

        mFragmentContainer = (LinearLayout) findViewById(R.id.requestsLinearLayout);

        mLayout = new LinearLayout(this);
        mLayout.setOrientation(LinearLayout.VERTICAL);

        mLayout.setId(R.id.layout);

        mIntent = getIntent();

        mProgressView = findViewById(R.id.requests_progress);
        mRequestsForm = findViewById(R.id.requestsScrollView);

        attemptGetRequests();
    }

    public void attemptGetRequests() {
        if (mGetRequestsTask != null) {
            return;
        }

        showProgress(true);
        mGetRequestsTask = new GetRequestsTask(this, mIntent.getStringExtra("username").toString());
        mGetRequestsTask.execute((Void) null);
    }

    public class GetRequestsTask extends AsyncTask<Void, Void, Boolean> {

        private OnTaskCompleteRequest mListener;
        private String mTo;
        private List<String> mUsernames, mReqIds;

        GetRequestsTask (OnTaskCompleteRequest listener, String to) {
            mListener = listener;
            mTo = to;
            mUsernames = new ArrayList<String>();
            mReqIds = new ArrayList<String>();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = "http://143.215.109.184/t4j/get_all_requests.php";
            List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new BasicNameValuePair("reqTo", mTo));

            JSONObject json = jsonParser.makeHttpRequest(url, "GET", requestParams);

            try {
                int success = json.getInt("success");
                JSONArray reqData = json.getJSONArray("reqData");
                for (int i = 0; i < reqData.length(); i++) {
                    mUsernames.add(reqData.getJSONObject(i).getString("reqFrom"));
                    mReqIds.add(reqData.getJSONObject(i).getString("reqID"));
                }

                return success == 1;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetRequestsTask = null;
            showProgress(false);
            OnTaskCompleted(mUsernames, mReqIds);
        }

        @Override
        protected void onCancelled() {
            mGetRequestsTask = null;
            showProgress(false);
        }
    }

    public void OnTaskCompleted(List<String> usernames, List<String> reqIds) {
        for (int i = 0; i < usernames.size(); i++) {
            getFragmentManager().beginTransaction().add(mLayout.getId(), RequestFragment.newInstance(usernames.get(i), reqIds.get(i)), "user 1").commit();
        }

        mFragmentContainer.addView(mLayout);
    }

    /**
     * Shows the progress UI and hides the registration form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRequestsForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mRequestsForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRequestsForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRequestsForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.requests, menu);
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
     * A placeholder fragment containing a simple view.
     */
    public static class RequestFragment extends Fragment {

        public RequestFragment() {
        }

        public static RequestFragment newInstance(String text, String reqId) {

            RequestFragment fragment = new RequestFragment();

            Bundle b = new Bundle();
            b.putString("text", text);
            b.putString("reqId", reqId);
            fragment.setArguments(b);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View v =  inflater.inflate(R.layout.fragment_requests, container, false);

            ((TextView) v.findViewById(R.id.requesterText)).setText(getArguments().getString("text"));
            ((Button) v.findViewById(R.id.acceptRequestButton)).setTag(getArguments().getString("reqId"));
            ((Button) v.findViewById(R.id.rejectRequestButton)).setTag(getArguments().getString("reqId"));
            return v;
        }
    }

    public void acceptRequest(View view) {
        attemptAcceptRequest(((Button) view).getTag().toString());
    }

    public void rejectRequest(View view) {
        attemptRejectRequest(((Button) view).getTag().toString());
    }

    public void attemptRejectRequest(String reqId) {
        if (mAcceptRequestTask != null) {
            return;
        }

        showProgress(true);
        mAcceptRequestTask = new AcceptRequestTask(reqId, "0");
        mAcceptRequestTask.execute((Void) null);
    }
    public void attemptAcceptRequest(String reqId) {
        if (mAcceptRequestTask != null) {
            return;
        }

        showProgress(true);
        mAcceptRequestTask = new AcceptRequestTask(reqId, "1");
        mAcceptRequestTask.execute((Void) null);
    }

    public class AcceptRequestTask extends AsyncTask<Void, Void, Boolean> {

        private String mReqId, mInvitation;

        AcceptRequestTask (String reqId, String invitation) {
            mReqId = reqId;
            mInvitation = invitation;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = "http://143.215.109.184/t4j/update_invatation_status.php";
            List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new BasicNameValuePair("reqID", mReqId));
            requestParams.add(new BasicNameValuePair("invatation", mInvitation));


            JSONObject json = jsonParser.makeHttpRequest(url, "POST", requestParams);

            try {
                int success = json.getInt("success");

                return success == 1;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetRequestsTask = null;
            showProgress(false);
            finish();
        }

        @Override
        protected void onCancelled() {
            mGetRequestsTask = null;
            showProgress(false);
        }
    }
}
